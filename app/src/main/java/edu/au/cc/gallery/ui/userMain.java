package edu.au.cc.gallery.ui;

import edu.au.cc.gallery.data.*;
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
import spark.template.handlebars.HandlebarsTemplateEngine;
import javax.servlet.http.Part;
import spark.utils.IOUtils;

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
    return new HandlebarsTemplateEngine()
    .render(new ModelAndView(model, "uploadImg.hbs"));
  }

 public String viewImages(Request req, Response resp) throws Exception {    
    Map<String,Object> model = new HashMap<String, Object>();
   ArrayList<String> imageList = userImg.getUserImages(req.session().attribute("user"));
   ArrayList<imageObject> ig = new ArrayList<imageObject>();
     
    for(String image : imageList) {
       String picture = imageUtil.getThumbnail(image);
       ig.add(new imageObject(image, picture));
   
     }        
       model.put("images", ig);
       return new HandlebarsTemplateEngine()
       .render(new ModelAndView(model, "viewImg.hbs"));
 } 

 public String openImg(Request req, Response resp) throws Exception {
     Map<String,Object> model = new HashMap<String, Object>();
     String picture = imageUtil.getFullImage(req.queryParams("imagename"));
     imgFile = req.queryParams("imagename");
     model.put("B64image", picture);
     return new HandlebarsTemplateEngine()
     .render(new ModelAndView(model, "openImg.hbs"));
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

 public String deleteImage(Request req, Response resp) throws Exception {
  userImg.deleteObjectByUser(req.session().attribute("user"), imgFile);
  resp.redirect("/main/viewImg");
  return "";
 } 

 public void addRoutes() {
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
