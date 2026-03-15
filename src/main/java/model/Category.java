package model;

public class Category {
    private int    categoryID;
    private String categoryName;
    private String description;

    public Category() {}
    public Category(int id, String name, String desc) {
        this.categoryID   = id;
        this.categoryName = name;
        this.description  = desc;
    }

    public int    getCategoryID()   { return categoryID; }
    public void   setCategoryID(int v) { categoryID = v; }

    public String getCategoryName() { return categoryName; }
    public void   setCategoryName(String v) { categoryName = v; }

    public String getDescription()  { return description; }
    public void   setDescription(String v) { description = v; }
}
