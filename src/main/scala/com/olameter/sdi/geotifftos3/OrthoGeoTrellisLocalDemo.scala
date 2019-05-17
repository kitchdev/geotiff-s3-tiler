package com.olameter.sdi.geotifftos3

object OrthoGeoTrellisLocalDemo {

  val orthoManager = new OrthoManager
    // Instanciate the OrthoManager
  def main(args: Array[String]): Unit = {
    //Use the OrthoManager to save locally in GeoTrellis the Geotiff
    val hostName = "s3"
    val bucketName = "testy-geotrellis"
    val pathName = sys.env("ORTHO_FULL_PATH")
    val slicedTile = orthoManager.load(hostName, bucketName, pathName)

    println("Done!")
  }
}