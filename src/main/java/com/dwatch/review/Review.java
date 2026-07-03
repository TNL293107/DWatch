package com.dwatch.review;

import java.util.Date;

public class Review {
    private int    reviewID;
    private int    productID;
    private Integer userID;
    private String authorName;
    private int    rating;
    private String comment;
    private Date   createdDate;

    public Review() {}

    public int     getReviewID()          { return reviewID; }
    public void    setReviewID(int v)     { reviewID = v; }

    public int     getProductID()         { return productID; }
    public void    setProductID(int v)    { productID = v; }

    public Integer getUserID()            { return userID; }
    public void    setUserID(Integer v)   { userID = v; }

    public String  getAuthorName()        { return authorName; }
    public void    setAuthorName(String v){ authorName = v; }

    public int     getRating()            { return rating; }
    public void    setRating(int v)       { rating = v; }

    public String  getComment()           { return comment; }
    public void    setComment(String v)   { comment = v; }

    public Date    getCreatedDate()       { return createdDate; }
    public void    setCreatedDate(Date v) { createdDate = v; }
}
