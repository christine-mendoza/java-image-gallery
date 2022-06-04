package edu.au.cc.gallery;

import java.util.Scanner;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserAdmin {

   public void getCommandList() {
      System.out.println();
      System.out.println("1) List Users");
      System.out.println("2) Add User");
      System.out.println("3) Edit User");
      System.out.println("4) Delete User");
      System.out.println("5) Quit");
    }
  public static void interactiveProgram() throws Exception {
   UserAdmin ad = new UserAdmin();
   String name;
   String pw;
   String fullName;
    int input = 0;
    DB db = new DB();
    db.connect();
  while(input != 5) {
    ad.getCommandList();
   Scanner scan = new Scanner(System.in);
    input = scan.nextInt();
   
 if(input == 1) {
      db.getUsers();
    }

 if(input == 2) {
  System.out.println("Username ");
  name = scan.next();
  System.out.println("Password ");
  pw = scan.next();
  System.out.println("Full name ");
  scan.skip("[\r\n]+");
  fullName = scan.nextLine();
   if(db.userExists(name)) {
  System.out.println("Error: user with username " + name + " already exists");
  continue;
}
  db.addUser(name, pw, fullName);
}
  if(input == 3) {
   System.out.println("User name to edit ");
   String  str = scan.next();
   name = str;
   if(!db.userExists(name)) {
    System.out.println("No such user");
    continue;
  }
   System.out.println("New password (press enter to keep current) ");
   str = scan.nextLine();
   str += scan.nextLine();
   if(!str.contentEquals("")) {
    pw = str;
 }
  else {
   pw = db.getPassword(name);
  }

  System.out.println("New full name ");
  str = scan.nextLine();
//  str += scan.nextLine();
  if(!str.contentEquals("")) {
    fullName = str;
 }
  else{
   fullName =db.getFullName(name);
  }
   db.updateUser(name, pw, fullName);
  }

 if(input == 4) {
  System.out.println("Enter username to delete  ");
  name = scan.next();
  System.out.println("Are you sure you want to delete " + name +"?");
  String check = scan.next();
  if(check.equalsIgnoreCase("Yes")) {
   db.deleteUser(name);
   System.out.println("Deleted.");
 }
 else {
 continue;
}
 }
 if(input == 5) {
  System.out.println("Bye.");
 }
}
 db.close();
 }
}
