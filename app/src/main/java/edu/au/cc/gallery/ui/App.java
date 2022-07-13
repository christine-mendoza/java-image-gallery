/*
 * This Java source file was generated by the Gradle 'init' task.
 */

package edu.au.cc.gallery.ui;
import edu.au.cc.gallery.data.Postgres;
import edu.au.cc.gallery.data.UserDAO;
import edu.au.cc.gallery.data.User;
import edu.au.cc.gallery.aws.S3;
import edu.au.cc.gallery.data.DB;
import static spark.Spark.*;
import spark.Request;
import spark.Response;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.SQLException;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import java.util.Scanner;
import java.io.File;

public class App {
    public String getGreeting() {
        return "Hello Christine!";
    }
   private static UserDAO getUserDAO() throws Exception {
   return Postgres.getUserDAO();
  }

    public static void main(String[] args) throws Exception {
    String portString = System.getenv("JETTY_PORT");
    String pg_host = System.getenv("PG_HOST");
    String pg_port = System.getenv("PG_PORT");
    String ig_database = System.getenv("IG_DATABASE");
    String ig_user = System.getenv("IG_USER");
    String ig_passwd_file = System.getenv("IG_PASSWD_FILE");
    String s3_image_bucket = System.getenv("S3_IMAGE_BUCKET");
    
    String ig_password = "";
    
	if (portString == null || portString.equals(""))
	    port(5000);
	else
	    port(Integer.parseInt(portString));

    if(ig_passwd_file != null && !ig_passwd_file.equals("")) {
	try {
	  File file = new File(ig_passwd_file);
	  Scanner scan = new Scanner(file);
	  while (scan.hasNextLine()) {
	   ig_password = (scan.nextLine());
	  } 
	 } catch (Exception ex) {
	   System.out.println("Error: " + ex.getMessage());
	 } 
    }
    DB.setIg_passwd(ig_password);
    DB.setPg_host(pg_host);
    DB.setIg_user(ig_user);
    DB.setPg_port(pg_port);
    DB.setIg_db(ig_database);
    S3.setS3_ImageBucket(s3_image_bucket);

      new userManager().addRoutes();
      new userMain().addRoutes();
    }
}
