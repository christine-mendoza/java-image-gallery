package edu.au.cc.gallery.data;

import java.util.List;

public interface UserDAO {
/**
 *@return return the list of users (possibly empty)
 */
  List<User> getUsers() throws Exception;
  
  /**
  *@return user with specified username or null if no such user
  **/
  User getUserByUsername(String username) throws Exception;
  
  /**
  *Add user to the database
  **/
  void addUser(User u) throws Exception;

}
