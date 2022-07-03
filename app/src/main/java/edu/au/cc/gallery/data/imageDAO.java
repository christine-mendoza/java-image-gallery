package edu.au.cc.gallery.data;

import java.util.ArrayList;

public interface imageDAO {


public ArrayList<String> getImageList(String u) throws Exception;

public void addImage(String u, String img) throws Exception;

public void deleteImage(String img, String u) throws Exception;

}
