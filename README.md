# Geotiff-s3-tiler

Sbt version: 1.2.8  
Scala version: 2.11.12

## Setup 

### Setting up S3 for testing locally

* Set up an aws account with your chosen email _(assuming an account with credentials doesn't already exist)_
* Under services chose s3
* Here at the s3 console you can create a bucket you can use for testing
* Once that is complete you can an access key and secret key by going to your security credentials page
* Click on Access keys which will genereate the keys for you.
* Make a copy `.env-sample` and call it `.env`
* Next add your `AWS_ACCESS_KEY` & `AWS_SECRET_KEY` to the `.env` file

### Sbt and compile
* first simply run the `sbt` command in the root of the project
* Next run `compile` so that all the necessary dependencies are installed and ready


### Running the application
* This project is running out of the `OrthoGeoTrellisLocalDemo` main, so after compiling, simply use the `run` command to execute
* _Important to note that this application was removed from a larger project with some of it's architecture still intact, hence why it may seem to have a convuluded file structure..._ 
* `OrthoGeoTrellisLocalDemo` -> `OrthoManager`'s load method -> `IngestOrthoWithEnv` sets up a sparkConf and calls -> `LocalTileSlicer`



## Transitive Dependency Strategy ====>

* Installed a plugin called Mima, which is a essentially a migration manager for any new packages
you add to the project; `addSbtPlugin("com.typesafe" %% "sbt-mima-plugin" % "0.3.0")`
If you encounter a binary incompatibility issue while compiling, you can use `evicted` to check which library are to blame, and then check the scope of the incompatibility- _i.e. whether it will break your application or not_
```scala
mimaPreviousArtifacts := Set(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7.2"
)
mimaPreviousArtifacts := Set(
  "com.fasterxml.jackson.core" % "jackson-core" % "2.6.7.2"
)
mimaPreviousArtifacts := Set(
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.6.7.2"
)
```
with this added to the `build.sbt` file we can now run `mimaReportBinaryIssues` in sbt to check the scope of the issue. For more info on mima you can check their docs here => [Mima](https://github.com/lightbend/migration-manager/blob/master/README.md)


* In order to fix these issues, the only method I found to work is to utilize the assembly plugin for sbt `addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.9")`
With this we can essentially create a strategy to rename the incompatible version of the library in order for it to be used by the necessary dependancies.

* To do this we first create a general assembly strategy to handle any _deduplicated_ methods/functions within our dependencies which seems to happen out of the box with `sbt-assembly` =>
```scala
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x                             => MergeStrategy.first
}
```

* Next we can use a _shading rule_ to rename the trouble libraries identified by Mima;
```scala
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
```
As you can see, we go one by one with the incompatible libraries, and _shade_ or rename their class path.

* Once these rules are set in place, we're able to use the `assembly` command to create a fat jar, and later reference that fat jar when we run our `spark-submit` command => 
```bash
spark-submit /home/mathew/Documents/projects/geotiff-s3-tiler/target/scala-2.11/geotiff-s3-tiler-assembly-0.1.0-SNAPSHOT.jar
```
Et voila, you're application should run as expected.