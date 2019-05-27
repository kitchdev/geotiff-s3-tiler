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

mimaPreviousArtifacts := Set(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7.2"
)
mimaPreviousArtifacts := Set(
  "com.fasterxml.jackson.core" % "jackson-core" % "2.6.7.2"
)
mimaPreviousArtifacts := Set(
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.6.7.2"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x                             => MergeStrategy.first
}

assemblyShadeRules in assembly ++= Seq(
  ShadeRule
    .rename("com.fasterxml.jackson.core.**" -> "my_jackson.@1")
    .inLibrary("com.fasterxml.jackson.core" % "jackson-databind" % "2.9.0-pr3")
    .inProject,
  ShadeRule
    .rename("com.fasterxml.jackson.core.**" -> "my_jackson.@1")
    .inLibrary("com.fasterxml.jackson.core" % "jackson-core" % "2.9.0-pr3")
    .inProject,
  ShadeRule
    .rename("com.fasterxml.jackson.core.**" -> "my_jackson.@1")
    .inLibrary(
      "com.fasterxml.jackson.core" % "jackson-annotations" % "2.9.0-pr3"
    )
    .inProject
)
