## Setting up S3 for testing locallysensible chuckle

* Set up an aws account with your chosen email
* Under services chose s3
* Here at the s3 console you can create a bucket you can use for testing
* Once that is complete you can an access key and secret key by going to your security credentials page
* Click on Access keys which will genereate the keys for you, Ensure you copy the keys to put in your local env file
* Next add your `AWS_ACCESS_KEY` & `AWS_SECRET_KEY` to the `Docker/dependencies/orthotiling.env` file
* Now you can instantiate your s3 connection in the code with your own aws connection and observe any activity with your buckets in your s3 dashboard
