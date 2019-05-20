# Geotiff-s3-tiler

Sbt version: 1.2.8  
Scala version: 2.11.12

## Setup 

### Setting up S3 for testing locally

* Set up an aws account with your chosen email
* Under services chose s3
* Here at the s3 console you can create a bucket you can use for testing
* Once that is complete you can an access key and secret key by going to your security credentials page
* Click on Access keys which will genereate the keys for you, Ensure you copy the keys to put in your local env file
* Next add your `AWS_ACCESS_KEY` & `AWS_SECRET_KEY` to the `.env` file
* Now you can instantiate your s3 connection in the code with your own aws connection and observe any activity with your buckets in your s3 dashboard

### Sbt and compile
* first simply run the `sbt` command in the root of the project
* Next run `compile` so that all the necessary dependencies are installed and ready


### Running the application
* This project is running out of the `OrthoGeoTrellisLocalDemo` main, so after compiling, simply use the `run` command to execute
* _Important to note that this application was removed from a larger project with some of it's architecture still intact, hence why it may seem to have a convuluded file structure..._ 
* `OrthoGeoTrellisLocalDemo` -> `OrthoManager`'s load method -> `IngestOrthoWithEnv` sets up a sparkConf and calls -> `LocalTileSlicer`
