package edu.au.cc.gallery.data;

import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Image implements imageDAO {
 private DB connection = new DB();

public ArrayList<String> getImageList(String u) throws SQLException {
 connection.connect();
 return connection.getImageList(u);
}

 public void addImage(String u, String img) throws SQLException {
  connection.connect();
  connection.addImage(u,img);
 }

 public void deleteImage(String img, String u) throws SQLException {
  connection.connect();
  connection.deleteImage(img, u);
 }
}
