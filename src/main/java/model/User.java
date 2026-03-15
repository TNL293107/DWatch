package model;

import java.util.Date;

public class User {
    private int    userID;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private Date   createdDate;

    public User() {}

    public int    getUserID()            { return userID; }
    public void   setUserID(int v)       { userID = v; }
    public String getFullName()          { return fullName; }
    public void   setFullName(String v)  { fullName = v; }
    public String getEmail()             { return email; }
    public void   setEmail(String v)     { email = v; }
    public String getPassword()          { return password; }
    public void   setPassword(String v)  { password = v; }
    public String getPhone()             { return phone; }
    public void   setPhone(String v)     { phone = v; }
    public String getAddress()           { return address; }
    public void   setAddress(String v)   { address = v; }
    public Date   getCreatedDate()       { return createdDate; }
    public void   setCreatedDate(Date v) { createdDate = v; }
}
