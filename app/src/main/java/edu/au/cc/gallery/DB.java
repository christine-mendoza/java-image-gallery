package edu.au.cc.gallery;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DB {

  private static final String dbUrl = "jdbc:postgresql://demo-database-1.cpni1ycia55k.us-east-1.rds.amazonaws.com/image_gallery";
  private Connection connection; 
  
 private String getPassword() {
  try(BufferedReader br = new BufferedReader(new FileReader("/home/ec2-user/.sql-passwd"))) {
  String result = br.readLine();
  return result;
 } catch (IOException ex) {
	System.err.println("Error opening password file. Make sure .sql-passwd exists and contains your sql password.");
        System.exit(1);
 }
  return null;
} 

 public void connect() throws SQLException {
   try {
     Class.forName("org.postgresql.Driver");
     connection = DriverManager.getConnection(dbUrl, "image_gallery", getPassword());
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
