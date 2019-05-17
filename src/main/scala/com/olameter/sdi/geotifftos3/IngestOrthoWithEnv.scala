package com.olameter.sdi.geotifftos3

import scala.io.StdIn

import org.apache.spark._

class IngestOrtho {
  def sparkInitLocalSlicer(path: String, bucket: String): Unit = {
    // Setup Spark to use Kryo serializer.
    val conf =
      new SparkConf()
        .setMaster("local[*]")
        .setAppName("Spark Tiler")
        .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        .set("spark.kryo.registrator", "geotrellis.spark.io.kryo.KryoRegistrator")

    val sc = new SparkContext(conf)
    try {
      
      var slicer = new LocalTileSlicer(
        orthoPath = path, 
        bucketName = bucket, 
        tileWidth = sys.env("TILE_WIDTH_IN_PIXELS").toInt,
        layerName = sys.env("GEOTRELLIS_LAYER_NAME"))
      slicer.run(sc)
      // Pause to wait to close the spark context,
      // so that you can check out the UI at http://localhost:4040
      println("Hit enter to exit.")
      StdIn.readLine()
    } finally {
      sc.stop()
    }
  }

}