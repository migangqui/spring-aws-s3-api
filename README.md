# Spring AWS S3 API (Java/Kotlin)

Here we are a Java and a Kotlin API to manage files of AmazonS3 in Spring framework. In order to use it, are necesaries the following steps:

## Compile the project (only for Kotlin project)

Clone the repo and execute ```mvn clean -U install``` command (the Kotlin project jar isn't at maven central at this moment).

### Add dependency to pom.xml:

If you use Java:

```xml
<dependency>
	<groupId>com.github.migangqui</groupId>
	<artifactId>spring-aws-s3-java</artifactId>
	<version>${currentVersion}</version>
</dependency>
```

If you use Kotlin (not in Maven central):

```xml
<dependency>
	<groupId>com.github.migangqui</groupId>
	<artifactId>spring-aws-s3-kotlin</artifactId>
	<version>${currentVersion}</version>
</dependency>
```

```${currentVersion}``` right now is ```1.0.0```

### Register the following properties in your application.yml:

```yaml
amazon:
    s3:
        accessKey: [AMAZON_ACCESS_KEY]
        secretKey: [AMAZON_SECRET_KEY]
        bucket.name: example-bucket-s3
    region: [GovCloud("us-gov-west-1"),
               US_EAST_1("us-east-1"),
               US_WEST_1("us-west-1"),
               US_WEST_2("us-west-2"),
               EU_WEST_1("eu-west-1"),
               EU_CENTRAL_1("eu-central-1"),
               AP_SOUTH_1("ap-south-1"),
               AP_SOUTHEAST_1("ap-southeast-1"),
               AP_SOUTHEAST_2("ap-southeast-2"),
               AP_NORTHEAST_1("ap-northeast-1"),
               AP_NORTHEAST_2("ap-northeast-2"),
               SA_EAST_1("sa-east-1"),
               CN_NORTH_1("cn-north-1")]**
```
** Only one and only the string of the region.

## Enable async

Add ```@EnableAsync``` annotation in your Spring Application class to enable async upload method.

## Component scan

You must add in your component scan configuration the package ```com.github.migangqui```.

## File size

To controle max size of files you can upload, set the following properties:
```yaml
spring:
    servlet:
        multipart:
            max-file-size: 128KB
            max-request-size: 128KB
```

## Localstack support

This library can be tested with Localstack (https://github.com/localstack/localstack).
You only have to set the following properties in your application.yml:

```yaml
localstack:
  enabled: false (by default)
  endpoint: http://localhost:4572
  region: us-east-1
```

In order to run easily Localstack, I have added ```docker-compose.yml``` file to the folder ```localstack```. 
You have run the command ```docker-compose up``` to make it work.

I hardly recommend install AWS CLI in your local. It helps you to manage the buckets to run the tests with Localstack.
Here you are the documentation to install: https://docs.aws.amazon.com/cli/latest/userguide/awscli-install-bundle.html

To create a local bucket you must run this command `aws --endpoint-url=http://localhost:4572 s3 mb s3://mytestbucket`

To check out if the bucket has been created run this command `aws --endpoint-url=http://localhost:4572 s3 ls`

When you create a bucket, you have to add `yourbucketname.localhost` to your hosts local file mapped to `127.0.0.1`.

## How to use

You have to inject ```AmazonS3Service``` as dependency in your Spring component.
The service provide these methods:

##### Java
```java
public interface AmazonS3Service {
	
	UploadFileResult uploadFile(InputStream stream, String folder, String name, String contentType);

	UploadFileResult uploadFile(byte[] bytes, String folder, String name, String contentType);

	UploadFileResult uploadFileAsync(InputStream stream, String folder, String name, String contentType);

	UploadFileResult uploadFileAsync(byte[] bytes, String folder, String name, String contentType);
	
	InputStream getFile(String path);
	
	boolean deleteFile(String path);
}
```
##### Kotlin
```kotlin
interface AmazonS3Service {

    fun uploadFile(stream: InputStream, folder: String, name: String, contentType: String): UploadFileResult

    fun uploadFile(bytes: ByteArray, folder: String, name: String, contentType: String): UploadFileResult

    fun uploadFileAsync(stream: InputStream, folder: String, name: String, contentType: String): UploadFileResult

    fun uploadFileAsync(bytes: ByteArray, folder: String, name: String, contentType: String): UploadFileResult

    fun getFile(path: String): InputStream

    fun deleteFile(path: String): Boolean
}
```

## License

This project is licensed under the MIT License - see the LICENSE.md file for details

## Next improvements

In future versions is planned include:
* AWS S3 File access configuration (nowadays is Public Read by default)
* Unit tests
* ... 