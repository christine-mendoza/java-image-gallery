package edu.au.cc.gallery.data;

import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import edu.au.cc.gallery.aws.S3;

public class userImage {
 private S3 connection = new S3();
 private final String bucketName = "edu.au.cc.image-gallery-con";
 private Image imgObj = new Image();


 public void putObjectByUser(String user, String keyName, File image) throws Exception {
  connection.connect();
  connection.putObject(bucketName, keyName, image); 
  imgObj.addImage(user, keyName);
 }

 public ArrayList<String> getUserImages(String user) throws Exception {	
  return imgObj.getImageList(user);
 }

// public List<String> getUserFileList(String user) throws Exception {
// return imgObj.getImageList(user);
 //}

 public String getCurrentObject(String key) throws Exception {
  connection.connect();
  return connection.getObject(bucketName, key);
 }

 public void deleteObjectByUser(String user, String keyName) throws Exception {
 connection.connect();
 connection.deleteObject(bucketName, keyName);
 imgObj.deleteImage(keyName, user);
 }
}
