package model;

public class Product {
    private int productID;
    private String productName;
    private String description;
    private double price;
    private int stock;
    private String imageURL;
    private String brand;
    private String origin;
    private String waterResist;
    private String caseSize;
    private String movement;
    private int categoryID;
    private String categoryName;

    public Product() {}

    public Product(int productID, String productName, String description,
                   double price, int stock, String imageURL, String brand,
                   String origin, String waterResist, String caseSize,
                   String movement, int categoryID) {
        this.productID   = productID;
        this.productName = productName;
        this.description = description;
        this.price       = price;
        this.stock       = stock;
        this.imageURL    = imageURL;
        this.brand       = brand;
        this.origin      = origin;
        this.waterResist = waterResist;
        this.caseSize    = caseSize;
        this.movement    = movement;
        this.categoryID  = categoryID;
    }

    // ---- Getters & Setters ----
    public int    getProductID()   { return productID; }
    public void   setProductID(int v)   { productID = v; }

    public String getProductName() { return productName; }
    public void   setProductName(String v) { productName = v; }

    public String getDescription() { return description; }
    public void   setDescription(String v) { description = v; }

    public double getPrice()       { return price; }
    public void   setPrice(double v) { price = v; }

    public int    getStock()       { return stock; }
    public void   setStock(int v)  { stock = v; }

    public String getImageURL()    { return imageURL; }
    public void   setImageURL(String v) { imageURL = v; }

    public String getBrand()       { return brand; }
    public void   setBrand(String v) { brand = v; }

    public String getOrigin()      { return origin; }
    public void   setOrigin(String v) { origin = v; }

    public String getWaterResist() { return waterResist; }
    public void   setWaterResist(String v) { waterResist = v; }

    public String getCaseSize()    { return caseSize; }
    public void   setCaseSize(String v) { caseSize = v; }

    public String getMovement()    { return movement; }
    public void   setMovement(String v) { movement = v; }

    public int    getCategoryID()  { return categoryID; }
    public void   setCategoryID(int v) { categoryID = v; }

    public String getCategoryName() { return categoryName; }
    public void   setCategoryName(String v) { categoryName = v; }

    /** Format price as Vietnamese dong */
    public String getFormattedPrice() {
        return String.format("%,.0f₫", price);
    }
}
