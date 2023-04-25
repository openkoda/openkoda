######################################

IN CASE OF UPDATING ALREADY DEPLOYED APP

                                to version with implemented AWS S3 file storage,
please execute SQL commands from file: codedose-template\template\etc\prepare-aws-s3-storage.sql
It's adding new column storage_type, which value can't be null, to table "file"
and updating existing data to have required value in that column, of course adequate to where the file is stored.

######################################

https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html

On AWS S3 please create IAM user with permission to AmazonS3FullAccess (tutorial in above link).
Please set AWS S3 credentials (obtained when creating IAM user - access key ID and secret access key)
and region in your system as described in above link in section:
"Set default credentials and Region" - you can set them in 2 files stored on local disc OR set as system's environment variables.

In application-template.properties (or any other, loaded after this) set:
- "file.storage.type" to "amazon"
- "file.storage.amazon.bucket" to name of AWS S3 Bucket in which files have to be stored.
Consider that the bucket name is visible in presigned URL which is used to download files from AWS S3.
- "file.storage.amazon.presigned-url.expiry.time.seconds" to after how many seconds should the presigned URL be expired by AWS S3 server

For tests - in template/src/test/resources/application-test.properties (or any other, loaded after this) set:
- "file.storage.filesystem.path" to directory where tests will save temp files used in tests - they are cleared after testing
- "file.storage.amazon.bucket" - you don't need to create new Bucket in AWS S3. Tests aren't saving anything to S3.
    Just type down random bucket name for ex. - app-test