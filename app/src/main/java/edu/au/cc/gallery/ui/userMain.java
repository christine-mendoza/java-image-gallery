package edu.au.cc.gallery.ui;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

import edu.au.cc.gallery.data.Postgres;
import edu.au.cc.gallery.data.User;
import edu.au.cc.gallery.data.UserDAO;
import edu.au.cc.gallery.aws.S3;
import edu.au.cc.gallery.data.userImage;
import edu.au.cc.gallery.data.DB;

import static spark.Spark.*;
import spark.Request;
import spark.Response;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import spark.ModelAndView;
import java.sql.SQLException;
import spark.*;
import java.io.*;
import java.nio.file.*;
import javax.servlet.*;
import java.util.Base64;
import spark.template.handlebars.HandlebarsTemplateEngine;
import java.awt.image.BufferedImage;
import java.util.Base64.Decoder;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.awt.Graphics;
import javax.servlet.http.Part;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import spark.utils.IOUtils;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.awt.Image;

public class userMain {
 private  userImage userImg = new userImage();
 private String imgFile;

private static UserDAO getUserDAO() throws Exception {
  return Postgres.getUserDAO();
 }

public String login(Request req, Response resp) {
    Map<String,Object> model = new HashMap<String, Object>();
   return new HandlebarsTemplateEngine()
    .render(new ModelAndView(model, "login.hbs"));
}

private String loginPost(Request req, Response resp) {
 try {
  String username = req.queryParams("username");
  User u = getUserDAO().getUserByUsername(username);
  if (u == null || !u.getPassword().equals(req.queryParams("password"))) {   
  resp.redirect("/login");
   return "";
}
 req.session().attribute("user", username);
 resp.redirect("/main");
 } catch (Exception ex) {
    return "Error: " +ex.getMessage();
  }
 return "";
}

private String mainSession(Request req, Response resp) {
     Map<String,Object> model = new HashMap<String, Object>();
     model.put("username", req.session().attribute("user"));
    return new HandlebarsTemplateEngine()
     .render(new ModelAndView(model, "main.hbs"));
}

public String uploadImages(Request req, Response resp) {
     Map<String,Object> model = new HashMap<String, Object>();
    // model.put("username", req.queryParams("username"));
    return new HandlebarsTemplateEngine()
    .render(new ModelAndView(model, "uploadImg.hbs"));
  }

public String viewImages(Request req, Response resp) throws Exception {
     
    Map<String,Object> model = new HashMap<String, Object>();
     DB db = new DB();
     db.connect();
     ArrayList<String> imageList = db.getImageList(req.session().attribute("user"));
    ArrayList<imageObject> ig = new ArrayList<imageObject>();
     model.put("files", imageList);
     ArrayList<String> thumbList = new ArrayList<String>();
     for(String image : imageList) {
      model.put("fileName", image);
     S3 s3 = new S3();
     s3.connect();
     String fileName = image; 
     String fileType = "";
     if(fileName.contains(".jpg")) {
	fileType = "jpg";
     }
     if(fileName.contains(".png")) {
	fileType = "png";
      }
     String imgType;
     s3.getObject("trash", fileName);
     String localFile = "/home/ec2-user/userImages/" + fileName;	     
      File inputImgFile = new File(localFile);
      int thumbnail_width = 150;
      int thumbnail_height = 150;
      File outputFile=null;
      try {
       BufferedImage img = new BufferedImage(thumbnail_width, thumbnail_height, BufferedImage.TYPE_INT_RGB);
       img.createGraphics().drawImage(ImageIO.read(inputImgFile).getScaledInstance(thumbnail_width, thumbnail_height, Image.SCALE_SMOOTH),0,0,null);
       outputFile=new File(inputImgFile.getParentFile()+File.separator+"thumbnail_"+inputImgFile.getName());
        ImageIO.write(img, fileType, outputFile);
	 byte[] bytes = Files.readAllBytes(Paths.get("/home/ec2-user/userImages/"+"thumbnail_"+inputImgFile.getName()));
	   String encodedString = Base64.getEncoder().encodeToString(bytes);
           thumbList.add(encodedString);
	   ig.add(new imageObject(fileName, encodedString));
      } catch (IOException e) {
	      System.out.println("Exception while generating thumbnail "+e.getMessage());
      }
    
     } 
             
              model.put("images", ig);
	      return new HandlebarsTemplateEngine()
	     .render(new ModelAndView(model, "viewImg.hbs"));
 } 

 public String openImg(Request req, Response resp) throws Exception {
     Map<String,Object> model = new HashMap<String, Object>();
  
   S3 s3 = new S3();
   s3.connect();
   String fileName = req.queryParams("imagename");
   String fileType = "";
   if(fileName.contains(".jpg")) {
    fileType = "jpg";
   }
   if(fileName.contains(".png")) {
    fileType = "png";
   }
   System.out.println(fileType);
   imgFile = req.queryParams("imagename");
   String imgType;
   s3.getObject("trash", fileName);

   
   String localFile = "/home/ec2-user/userImages/" + fileName;
   File file = new File(localFile);
   InputStream input = new FileInputStream(file);
   BufferedImage bi = ImageIO.read(input);
   byte[] rawImage = null;
   try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
    ImageIO.write( bi, fileType, baos );
    baos.flush();
    rawImage = baos.toByteArray();
   // resp.type("image/jpeg");
   } 
   byte[] bytes = Files.readAllBytes(Paths.get(localFile));
  String encodedString = Base64.getEncoder().encodeToString(bytes);
     model.put("B64image", encodedString);
     return new HandlebarsTemplateEngine()
     .render(new ModelAndView(model, "openImg.hbs"));

   // return rawImage;
 }

private boolean isUser(String username, String user) {
 return username != null && user != null && username.equals(user);
}

private String checkUser(Request req, Response resp) throws Exception {
try {   
    String username = req.queryParams("username");
    User u = getUserDAO().getUserByUsername(username);
    if (!isUser(req.session().attribute("user"), u.getUsername()))  {
      resp.redirect("/login");
      halt();
     }
    } catch (Exception ex) {
       return "Error: " +ex.getMessage();
    }
    return "";
}
private boolean isAdmin(String username) {
  return username != null &&  username.equals("Administrator");
}

private String checkAdmin(Request req, Response resp) {
try {
 if(!isAdmin(req.session().attribute("user"))) {
   resp.redirect("/login");
   halt();
}
} catch (Exception ex) {
   return "Error: " + ex.getMessage();
}
  return "";
}

public String adminLink(Request req, Response resp) {
  new userManager().addRoutes();
 return "";
}

public String addImg(Request req, Response resp) throws Exception {
 
   try {
   req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/home/ec2-user/temp/"));
   Part filePart = req.raw().getPart("uploaded_file");
   InputStream inputStream = filePart.getInputStream(); 
   String keyName = filePart.getSubmittedFileName();
   OutputStream outputStream = new FileOutputStream("/home/ec2-user/temp/tmp");

   IOUtils.copy(inputStream, outputStream);

   outputStream.close();

   File fileHandle = new File("/home/ec2-user/temp/tmp");
    userImg.putObjectByUser(req.session().attribute("user"), keyName, fileHandle);
    fileHandle.delete();
    } catch (Exception ex) {

        return "Error :" + ex.toString();
    }
    Map<String,Object> model = new HashMap<String, Object>();
    return new HandlebarsTemplateEngine()
   .render(new ModelAndView(model, "imgUploadMenu.hbs"));
   }
public String imageTest(Request res, Response resp) throws Exception {
   Map<String,Object> model = new HashMap<String, Object>();
    return new HandlebarsTemplateEngine()
    .render(new ModelAndView(model, "imgTest.hbs"));
}

  private String getImage(Request res, Response resp) throws Exception {
    S3 s3 = new S3();
    s3.connect();
    String f = s3.getObject("edu.au.cc.image-gallery-con","hotdog_.jpg");
    System.out.println(f);
       Map<String,Object> model = new HashMap<String, Object>();
  //     model.put("image", "templates/public/images/temp");
       return new HandlebarsTemplateEngine()
       .render(new ModelAndView(model, "imgTest.hbs"));
  }

 public String deleteImage(Request req, Response resp) throws Exception {
  userImg.deleteObjectByUser(req.session().attribute("user"), imgFile);
  resp.redirect("/main/viewImg");
  return "";
}

 public void addRoutes() {
// staticFileLocation("/resources");
  get("/login",(req, res) -> login(req, res));
  before("/main/:username/*",(req, res) -> checkUser(req, res));
  post("/login", (req,res) -> loginPost(req, res));
  get("/main",(req, res) -> mainSession(req, res));
  get("/main/viewImg", (req,res) -> viewImages(req, res));
  get("/main/uploadImg", (req,res) -> uploadImages(req, res));
  before("/admin/*",(req,res) -> checkAdmin(req,res));
  get("/admin/users",(req,res) -> adminLink(req, res));
  post("/main/uploadImg", (req,res) ->  addImg(req,res));
  get("/main/imgTest",(req,res) -> openImg(req,res));
  get("/main/openImg/:imagename",(req,res) -> openImg(req,res));
  get("/main/delete",(req,res) -> deleteImage(req,res));
 }
}
