package edu.au.cc.gallery.data;

import java.util.ArrayList;
import java.sql.SQLException;
import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.Base64.Decoder;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.awt.Graphics;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.awt.Image;
import java.io.IOException;
import javax.servlet.http.Part;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import spark.utils.IOUtils;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

public final class imageUtil {

 public static String getFileType(String fileName) {
  String fileType = "";
  if(fileName.contains(".jpg")) {
    fileType = "jpg";
   }
   if(fileName.contains(".png")) {
     fileType = "png";
   }
   return fileType;
 }

 public static String getThumbnail(String fileName) throws Exception {
  userImage ui = new userImage();
  ui.getObject(fileName);
  String fileType = getFileType(fileName);
  String localFile = "/home/ec2-user/userImages/" + fileName;
  File inputImgFile = new File(localFile);
  int thumbnail_width = 150;
  int thumbnail_height = 150;
  File outputFile = null;
  try {
       BufferedImage img = new BufferedImage(thumbnail_width, thumbnail_height, BufferedImage.TYPE_INT_RGB);
       img.createGraphics().drawImage(ImageIO.read(inputImgFile).getScaledInstance(thumbnail_width, thumbnail_height, Image.SCALE_SMOOTH),0,0,null);
       outputFile=new File(inputImgFile.getParentFile()+File.separator+"thumbnail_"+inputImgFile.getName());
       ImageIO.write(img, fileType, outputFile);
       byte[] bytes = Files.readAllBytes(Paths.get("/home/ec2-user/userImages/"+"thumbnail_"+inputImgFile.getName()));
       String encodedString = Base64.getEncoder().encodeToString(bytes);
       return encodedString;
      } catch (IOException e) {
              System.out.println("Exception while generating thumbnail "+e.getMessage());
      }
        return "";
     }

 public static String getFullImage(String fileName) throws Exception {
   userImage ui = new userImage();
   ui.getObject(fileName);
   String fileType = getFileType(fileName);
   String localFile = "/home/ec2-user/userImages/" + fileName;
   File file = new File(localFile);
   InputStream input = new FileInputStream(file);
   BufferedImage bi = ImageIO.read(input);
   byte[] rawImage = null;
   try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
    ImageIO.write( bi, fileType, baos );
    baos.flush();
    rawImage = baos.toByteArray();
   }
    byte[] bytes = Files.readAllBytes(Paths.get(localFile));
    String encodedString = Base64.getEncoder().encodeToString(bytes);
 return encodedString;
 }

}
