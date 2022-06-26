package edu.au.cc.gallery.ui;

import edu.au.cc.gallery.data.Postgres;
import edu.au.cc.gallery.data.User;
import edu.au.cc.gallery.data.UserDAO;
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

import spark.template.handlebars.HandlebarsTemplateEngine;

public class userManager {
  
private static UserDAO getUserDAO() throws Exception {
  return Postgres.getUserDAO();
 }

/**
  public ArrayList<String> users() throws SQLException {
   DB db = new DB();
   db.connect();
   return db.getUsersNoPW();
  }
**/

 private String admin(Request req, Response resp) throws SQLException  {
  try {
   Map<String,Object> model = new HashMap<String, Object>();
   model.put("users",getUserDAO().getUsers());
   return new HandlebarsTemplateEngine()
    .render(new ModelAndView(model, "admin.hbs"));
  } catch (Exception ex) {
      return "Error: "+ ex.getMessage();
   }
}

public String createUser(Request req, Response resp) {
    Map<String,Object> model = new HashMap<String, Object>();
   return new HandlebarsTemplateEngine()
    .render(new ModelAndView(model, "createUser.hbs"));
}

public String addUserButton(Request req, Response resp) throws Exception {
  DB db = new DB();
  db.connect();
  db.addUser(req.queryParams("username"), req.queryParams("password"), req.queryParams("full_name"));
  return  req.queryParams("username") + " has been added";
}

public String deleteUser(Request req, Response resp) {
  Map<String,Object> model = new HashMap<String, Object>();
  model.put("user", req.queryParams("user"));
   return new HandlebarsTemplateEngine()
    .render(new ModelAndView(model, "deleteUser.hbs"));  
}

public String delButton(Request req, Response resp) throws Exception {
DB db = new DB();
db.connect();
db.deleteUser(req.params("user"));
return req.params("user") + " has been deleted";
}

public String editUser(Request req, Response resp) {
    Map<String,Object> model = new HashMap<String, Object>();
   model.put("user", req.queryParams("user"));
   return new HandlebarsTemplateEngine()
    .render(new ModelAndView(model, "editUser.hbs"));
}

public String editUserButton(Request req, Response resp) throws Exception {
DB db = new DB();
db.connect();
String pw = "";
if(req.queryParams("password").equals("*")) {
 pw = db.getPassword(req.params("user"));
}
else {
  pw = req.queryParams("password");
}
String fn = "";
if(req.queryParams("full_name").equals("*")) {
 fn = db.getFullName(req.params("user"));
}
else {
fn = req.queryParams("full_name");
}
db.updateUser(req.params("user"), pw, fn);
return req.params("user") + " updated";
}

public void addRoutes() {
  get("/admin/users", (req,res) -> admin(req,res));
  get("/admin/deleteUser",(req,res) -> deleteUser(req, res));
  post("/admin/deleteUser/:user", (req,res) -> delButton(req,res));
  post("/admin/addUser/added",(req,res) -> addUserButton(req,res));
  post("/admin/addUser",(req, res) ->  createUser(req, res));
  get("/admin/editUser",(req,res) -> editUser(req,res));
  post("/admin/editUser/:user",(req,res) -> editUserButton(req,res));
}
}

