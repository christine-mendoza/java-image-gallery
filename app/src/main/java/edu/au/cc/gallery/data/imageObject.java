package edu.au.cc.gallery.data;

public class imageObject {
 String fileName;
 String image;

 public imageObject(String fileName, String image) {
   this.fileName = fileName;
   this.image = image;	
  }

 public String getFileName() {
  return fileName;
 }
 
 public void setFileName(String fn) {
   fileName = fn;
 }

 public String getImage() {
  return image;
 } 

 public void setImage(String img) {
  image = img;
 }

 public String toString() {
  return fileName + "/n" + image;
 }

}
