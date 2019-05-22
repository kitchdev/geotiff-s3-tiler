moduleName := "osdi-geotifftos3"
scalaVersion := "2.11.12"
organization := "com.olameter"
organizationName := "Olameter inc."
startYear := Some(2019)
version := "0.1.0-SNAPSHOT"
publishTo := Some(Resolver.file("file", new File("./build")))

libraryDependencies ++= Seq(
  "org.locationtech.geotrellis" %% "geotrellis-raster" % "2.1.0",
  "org.locationtech.geotrellis" %% "geotrellis-proj4" % "2.1.0",
  "org.locationtech.geotrellis" %% "geotrellis-spark" % "2.1.0",
  "org.locationtech.geotrellis" %% "geotrellis-vector" % "2.1.0",
  "org.locationtech.geotrellis" %% "geotrellis-s3" % "2.1.0",
  "org.apache.spark" %% "spark-core" % "2.3.1",
  "org.apache.spark" %% "spark-sql" % "2.3.1",
  "com.amazonaws" % "aws-java-sdk" % "1.11.548",
  "io.minio" % "minio" % "4.0.2"
)

mimaPreviousArtifacts := Set("io.netty" % "netty" % "3.6.2.Final")
