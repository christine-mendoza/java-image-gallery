package edu.au.cc.gallery.aws;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import java.io.File;
import java.nio.file.Paths;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
//import software.amazon.awssdk.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.FileOutputStream;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.util.IOUtils;

public class S3 {
    private static final Region region = Region.US_EAST_1;
    private S3Client client;
    private AmazonS3 awsClient;

    private static String s3_image_bucket;

    public static void setS3_ImageBucket(String bucket) {
     s3_image_bucket = bucket;
    }

    public static String getS3_ImageBucket() {
     return s3_image_bucket;
    }
    public void connect() {
        client = S3Client.builder().region(region).build();
	awsClient = AmazonS3ClientBuilder.standard().withRegion("us-east-1").build();
    }

    public void createBucket(String bucketName) {
	CreateBucketRequest createBucketRequest = CreateBucketRequest
	    .builder()
	    .bucket(bucketName)
	    .createBucketConfiguration(CreateBucketConfiguration.builder()
				      // .locationConstraint(region.id())
				       .build())
	    .build();
	client.createBucket(createBucketRequest);
    }

   public void putObject(String bucketName, String key, File file) throws Exception {
	PutObjectRequest por = PutObjectRequest.builder()
	    .bucket(bucketName)
	    .key(key)
           //  .contentEncoding("base64")
           //  .contentType(contentType)
	    .build();
	client.putObject(por, RequestBody.fromFile(file));
      //   client.putObject(por, RequestBody.fromFile(value));
    }

  public void deleteObject(String bucketName, String key) {
     DeleteObjectRequest dor = DeleteObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
          .build();
      client.deleteObject(dor);
}
     public String getObject(String bucketName, String key) throws Exception {
       GetObjectRequest gor = GetObjectRequest.builder()
                 .bucket("edu.au.cc.image-gallery-con")
                 .key(key)
                 .build();
               // client.getObject(gor);
  bucketName = "edu.au.cc.image-gallery-con";     
  S3Object object = awsClient.getObject(bucketName, key);
  S3ObjectInputStream objectData = object.getObjectContent();
  String filePath = "/home/ec2-user/userImages/" + key;
  FileOutputStream fos = new FileOutputStream(new File(filePath));
  IOUtils.copy(objectData, fos);
  fos.close();
  return "";
 }



    public static void demo() {
	String bucketName = "edu.au.cc.img-gallery-test";
	S3 s3 = new S3();
	s3.connect();
	//s3.createBucket(bucketName);
	//s3.putObject(bucketName, "banana", "yellow");
        s3.deleteObject(bucketName, "banana");
    }

}
