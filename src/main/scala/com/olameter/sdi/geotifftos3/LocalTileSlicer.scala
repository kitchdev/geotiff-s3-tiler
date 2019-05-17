package com.olameter.sdi.geotifftos3

import geotrellis.raster._
import geotrellis.raster.io.geotiff._
import geotrellis.raster.io.geotiff.reader.GeoTiffReader
import geotrellis.raster.render._
import geotrellis.raster.resample._
import geotrellis.raster.reproject._
import geotrellis.proj4._

import geotrellis.spark._
import geotrellis.spark.io.hadoop._
import geotrellis.spark.io._
import geotrellis.spark.io.file._
import geotrellis.spark.io.index._
import geotrellis.spark.pyramid._
import geotrellis.spark.reproject._
import geotrellis.spark.tiling._
import geotrellis.spark.render._

import geotrellis.vector._

import geotrellis.spark.io.s3._

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.services.s3.{AmazonS3Client=>AWSAmazonS3Client}
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ListObjectsV2Result
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.regions._
import com.amazonaws.client.builder._

import org.apache.spark._
import org.apache.spark.rdd._

import scala.io.StdIn
import java.io.File
import java.util.List

import scala.collection.JavaConversions._
import java.io.ByteArrayInputStream

class LocalTileSlicer(
    var orthoPath: String, 
    var bucketName: String, 
    var tileWidth: Int, 
    var layerName: String) {
    
    val AWS_ACCESS_KEY = sys.env("AWS_ACCESS_KEY")
    val AWS_SECRET_KEY = sys.env("AWS_SECRET_KEY")

    val region = Regions.US_EAST_2
    val awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY)
    val credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials)
    println("========s3Region", region)
    var amazonS3 = AmazonS3ClientBuilder
      .standard()
      .withCredentials(credentialsProvider)
      .withRegion(region)
      .build()

    var specialS3Client = new AmazonS3Client(amazonS3)

    def run(implicit sc: SparkContext): Unit  = {

      val orthoFullPath = "file://" + new File(orthoPath).getAbsolutePath
      val orthoDemoFile = new File(orthoPath)
      // upload ortho image with standard s3 client for testing purposes
      // ==============================================================
      if (orthoDemoFile.exists()) {
        try {
          println(s"Uploading ${orthoPath} to S3 bucket ${bucketName}...\n")
          amazonS3.putObject(bucketName, "geotiffDemo.tif", orthoDemoFile)
          println(s"Uploading ${orthoPath} to S3 bucket ${bucketName} ======> SUCCESSFUL\n")
        } catch {
          case err: AmazonClientException => println("AmazonClientException", err)
          case err: AmazonServiceException => println("AmazonServiceException", err)
        } finally {
          val results: ListObjectsV2Result = amazonS3.listObjectsV2(bucketName)
          val objects: List[S3ObjectSummary] = results.getObjectSummaries()
          for (s3Objs <- objects) {
            println(s"Uploaded Objects in ${bucketName}", s3Objs)
          }
        }
      }
      // ==============================================================

      // Read the geotiff in as a single image RDD,
      // using a method implicitly added to SparkContext by
      // an implicit class available via the
      // "import geotrellis.spark.io.hadoop._ " statement.
      val inputRdd: RDD[(ProjectedExtent, MultibandTile)] =
        sc.hadoopMultibandGeoTiffRDD(orthoFullPath)

      //We create a layout scheme based on our required tile size of 1006x1006 pixels
      val (_, rasterMetaData) =
        TileLayerMetadata.fromRDD(inputRdd, FloatingLayoutScheme(tileWidth))

      // Use the Tiler to cut our tiles into tiles that are index to a floating layout scheme.
      // We'll repartition it so that there are more partitions to work with, since spark
      // likes to work with more, smaller partitions (to a point) over few and large partitions.
      val tiled: RDD[(SpatialKey, MultibandTile)] =
        inputRdd
          .tileToLayout(rasterMetaData.cellType, rasterMetaData.layout, Bilinear)
          .repartition(100)

      //  Create the MultibandTileLayerRDD required to write data into the catalog
      val tileLayerRdd: RDD[(SpatialKey, MultibandTile)] with Metadata[TileLayerMetadata[SpatialKey]] = MultibandTileLayerRDD(tiled, rasterMetaData)

      // Create the attributes store that will tell us information about our catalog.
      val attributeStore = new S3AttributeStore(bucketName, "geotiff_tiles") {
        override def s3Client = specialS3Client
      }
      // Create the writer that we will use to store the tiles in the local catalog.
      val writer = S3LayerWriter(attributeStore)

      // Checking if the client is configured properly
      val s3BucketCustomConfig = attributeStore.s3Client.doesBucketExist(bucketName)
      println(s3BucketCustomConfig, "====================s3Client")

      val layerId = LayerId(layerName, 0)
      if (attributeStore.layerExists(layerId)) {
        // println(new LayerManager)
        attributeStore.delete(layerId)
        // println(layerId, "LAYER ID ALREADY EXISTS PREPARE TO FAIL")
      }
      writer.write(layerId, tileLayerRdd, ZCurveKeyIndexMethod)
    }
}
