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

 public String getImage() {
  return image;
 }

public String toString() {
 return fileName + "/n" + image;
}
}
