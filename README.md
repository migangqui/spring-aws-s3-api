# Spring AWS S3 API

Here we are a Java and a Kotlin API to manage files of AmazonS3 in Spring framework. In order to use it, are necesaries the following steps:

## Compile the project

Clone the repo and execute ```mvn clean -U install``` command (the jar's aren't in maven central at this moment).

### Add dependency to pom.xml:

If you use Java:

```xml
<dependency>
	<groupId>com.migangqui</groupId>
	<artifactId>spring-aws-s3-java</artifactId>
	<version>${currentVersion}</version>
</dependency>
```

If you use Kotlin:

```xml
<dependency>
	<groupId>com.migangqui</groupId>
	<artifactId>spring-aws-s3-kotlin</artifactId>
	<version>${currentVersion}</version>
</dependency>
```

```${currentVersion}``` right now is ```1.0.0.RC```

### Register the following properties in your application.yml:

```xml
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

You must add in your component scan configuration the package ```com.migangqui```.

## File size

To controle max size of files you can upload, set the following properties:
```yml
spring:
    http:
        multipart:
            max-file-size=128KB
            max-request-size=128KB
```

## Localstack support

This library can be tested with Localstack (https://github.com/localstack/localstack).
You only have to set the following properties in your application.yml:

```yml
localstack:
  enabled: false (by default)
  endpoint: http://localhost:4572
  region: us-east-1
```

In order to run easily Localstack, I have added ```docker-compose.yml``` file to the folder ```localstack```. 
You have run the command ```docker-compose up``` to make it work.

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
