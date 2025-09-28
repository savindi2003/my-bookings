package lk.javainstitute.mybookings;

import java.util.ArrayList;
import java.util.List;

public class Hotel {

    private List<String> imageUrls;
    private String name;
    private String description;
    private String location;
    private double price;
    private float rating; // Changed to float for consistency
    private List<String> amenities;
    private String contactNo1;
    private String contactNo2;
    private String email;
    private String id;



    public Hotel() {
        this.imageUrls = new ArrayList<>(); // Ensures it's never null
    }

    public Hotel(int image, String name, String location, double price, float rating) {
        //this.image = image;
        this.name = name;
        this.location = location;
        this.price = price;
        this.rating = rating;
    }

    public Hotel(List<String> imageUrls, String name, String description, String location, double price, float rating,
                 List<String> amenities, String contactNo1, String contactNo2, String email,String id) {
        this.imageUrls = imageUrls;
        this.name = name;
        this.description = description;
        this.location = location;
        this.price = price;
        this.rating = rating;
        this.amenities = amenities;
        this.contactNo1 = contactNo1;
        this.contactNo2 = contactNo2;
        this.email = email;
        this.id = id;
    }

    public List<String> getImageUrls() { return imageUrls; }

    public String getName() { return name; }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public double getPrice() { return price; }
    public float getRating() { return rating; } // Return float rating
    public List<String> getAmenities() { return amenities; }
    public String getContactNo1() { return contactNo1; }
    public String getContactNo2() { return contactNo2; }
    public String getEmail() { return email; }
}
