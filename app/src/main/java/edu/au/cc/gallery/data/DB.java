package edu.au.cc.gallery.data;
import edu.au.cc.gallery.aws.secrets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONObject;

public class DB {

 // private static final String dbUrl = "jdbc:postgresql://image-gallery.cpni1ycia55k.us-east-1.rds.amazonaws.com/image_gallery";
  private Connection connection; 
    
  private JSONObject getSecret() {
    String s = secrets.getSecretImageGallery();
    return new JSONObject(s);
  }
  
 private String getPassword(JSONObject secret) {
   return secret.getString("password");
 }

private String getUser(JSONObject secret) {
  return secret.getString("username");
}

private String getHost(JSONObject secret) {
  return secret.getString("host");
}

private String getDBID(JSONObject secret) {
  return secret.getString("dbInstanceIdentifier");
}

private String getDBURL(JSONObject secret) {
 return "jdbc:postgresql:/" + "/" + getHost(secret) + "/" + getUser(secret); 
}


 public void connect() throws SQLException {
   try {
     Class.forName("org.postgresql.Driver");
     JSONObject secret = getSecret();
     connection = DriverManager.getConnection(getDBURL(secret), getUser(secret), getPassword(secret));
   } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
      System.exit(1);
   }
 }

 public void getUsers() throws SQLException {
   PreparedStatement stmt = connection.prepareStatement("select * from users");
   ResultSet rs = stmt.executeQuery();
   String heading = "username";
   String heading1 =  "password";
   String heading2 = "full name";
   System.out.printf("%-20s %-20s %-20s %n", heading, heading1, heading2);
   System.out.println("---------------------------------------------------");
   while(rs.next()) {
    String un =  rs.getString("username");
    String pw = rs.getString("password");
    String fn = rs.getString("full_name");
    System.out.printf("%-20s %-20s %-20s %n", un, pw, fn);
	}
 }

 public ResultSet executeQuery(String query) throws SQLException {
  PreparedStatement stmt = connection.prepareStatement(query);
  ResultSet rs = stmt.executeQuery();
  return rs;
 }

 public ResultSet executeQuery(String query, String[] values) throws SQLException {
  PreparedStatement stmt = connection.prepareStatement(query);
  for(int i = 0; i < values.length; i++){  
   stmt.setString(i+1, values[i]);
 }
 ResultSet rs = stmt.executeQuery(); 
  return rs;
 
}
 public void execute(String query, String[] values) throws SQLException {
  PreparedStatement stmt = connection.prepareStatement(query);
  for(int i = 0; i < values.length; i++) {
   stmt.setString(i+1, values[i]);
   stmt.execute();
 }
}
public ArrayList<String> getUsersNoPW() throws SQLException {
  PreparedStatement stmt = connection.prepareStatement("select username from users");
   ResultSet rs = stmt.executeQuery();
   ArrayList<String> list = new ArrayList<String>();
   while(rs.next()) {
   list.add(rs.getString("username"));
}
  return list;
} 

 public void addUser(String username, String password, String full_name) throws SQLException {
  String sql = "INSERT INTO users(username, password, full_name) VALUES(?,?,?);";
  PreparedStatement ps = connection.prepareStatement(sql);
  ps.setString(1, username);
  ps.setString(2, password);
  ps.setString(3, full_name);
  ps.executeUpdate();
}

 public void deleteUser(String username) throws SQLException {
  String sql =  "DELETE FROM users WHERE username = ?";
  PreparedStatement ps = connection.prepareStatement(sql);
  ps.setString(1, username);
  ps.executeUpdate();
}

 public String getPassword(String username) throws SQLException {
  String sql = "SELECT password FROM users WHERE username = ?";
  PreparedStatement ps = connection.prepareStatement(sql);
  ps.setString(1, username);
  String result = "";
  ResultSet rs = ps.executeQuery();
 while(rs.next()) {
  result = rs.getString("password");
 }
 return result;
}
 public String getFullName(String username) throws SQLException {
  String sql = "SELECT full_name FROM users WHERE username = ?";
  PreparedStatement ps = connection.prepareStatement(sql);
  String result = "";
  ps.setString(1, username);
  ResultSet rs = ps.executeQuery();
  while(rs.next()) {
  result =  rs.getString("full_name");
 }
return result;
}

 public void updateUser(String username, String password, String full_name) throws SQLException {
  String sql = "UPDATE users SET password = ?, full_name = ? WHERE username = ?";
   PreparedStatement ps = connection.prepareStatement(sql);
   ps.setString(3, username);
   ps.setString(1, password);
   ps.setString(2, full_name);
   ps.executeUpdate();
}
public boolean userExists(String username) throws SQLException {
  String sql = "SELECT username FROM users WHERE username = ?";
  PreparedStatement ps = connection.prepareStatement(sql);
  ps.setString(1, username);
  ResultSet rs = ps.executeQuery();
  if(rs.next()) {
    return true;
  }
  return false;
}
public ArrayList<String> getImageList(String user) throws SQLException {
 String sql = "select image from images where username = ?";
 PreparedStatement ps = connection.prepareStatement(sql);
 ps.setString(1, user);
 ResultSet rs = ps.executeQuery();
 ArrayList<String> result = new ArrayList<String>();
 while(rs.next()) {
  result.add(rs.getString("image"));
 }
 return result;
}

public void addImage(String username, String image) throws SQLException {
 String sql = "insert into images(username, image) values(?,?)";
 PreparedStatement ps = connection.prepareStatement(sql);
 ps.setString(1, username);
 ps.setString(2, image);
 ps.executeUpdate();
}

public void deleteImage(String img, String user) throws SQLException {
 String sql = "delete from images where image = ? and username = ?";
 PreparedStatement ps = connection.prepareStatement(sql);
 ps.setString(1, img);
 ps.setString(2, user);
 ps.executeUpdate();
}
 public void close() throws SQLException {
   connection.close();
 }

 public static void demo() throws Exception {
  DB db = new DB();
  db.connect();
  System.out.println("FROM DB:");
  db.getUsers();
  db.getPassword("fred");
  db.getFullName("fred");
  db.close();	
 }
}    
