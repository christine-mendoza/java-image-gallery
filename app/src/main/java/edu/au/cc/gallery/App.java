/*
 * This Java source file was generated by the Gradle 'init' task.
 */

package edu.au.cc.gallery;

import static spark.Spark.*;
import spark.Request;
import spark.Response;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.SQLException;

import spark.ModelAndView;

import spark.template.handlebars.HandlebarsTemplateEngine;

public class App {
    public String getGreeting() {
        return "Hello Christine!";
    }

    public static void main(String[] args) throws Exception {
       // System.out.println(new App().getGreeting());
	//DB.demo();
	//UserAdmin.interactiveProgram();
    String portString = System.getenv("JETTY_PORT");
	if (portString == null || portString.equals(""))
	    port(5000);
	else
	    port(Integer.parseInt(portString));
       new userManager().addRoutes();
//     S3.demo();
    }
}
