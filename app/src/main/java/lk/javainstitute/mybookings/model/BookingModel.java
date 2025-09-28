package lk.javainstitute.mybookings.model;

import com.google.firebase.Timestamp;

public class BookingModel {
    private String hotelName;
    private String location;
    private String imageResource;
    private String hotel_id;
    private Double room_count;
    private String room_id;
    private String reservation_id;
    private String status;
    private String check_in_date;
    private String check_out_date;
    private String date;
    private String client_address;
    private String client_country;
    private String client_email;
    private String client_mobile;
    private String client_name;
    private String roomName;
    private Double roomCount;

    private double lat;
    private double lang;


    // Constructor
    public BookingModel(String hotelName, String location, String imageResource, String hotel_id, Double room_count, String room_id,
                        String reservation_id, String status, String check_in_date, String check_out_date, String date,
                        String client_address, String client_country, String client_email, String client_mobile, String client_name,double lat,double lang) {
        this.hotelName = hotelName;
        this.location = location;
        this.imageResource = imageResource;
        this.hotel_id = hotel_id;
        this.room_count = room_count;
        this.room_id = room_id;
        this.reservation_id = reservation_id;
        this.status = status;
        this.check_in_date = check_in_date;
        this.check_out_date = check_out_date;
        this.date = date;
        this.client_address = client_address;
        this.client_country = client_country;
        this.client_email = client_email;
        this.client_mobile = client_mobile;
        this.client_name = client_name;
        this.lat = lat;
        this.lang = lang;

    }

    public BookingModel(Double room_count, String room_id,
                        String reservation_id, String status, String check_in_date, String check_out_date, String date,
                        String client_mobile, String client_name,String roomName) {

        this.room_count = room_count;
        this.room_id = room_id;
        this.reservation_id = reservation_id;
        this.status = status;
        this.check_in_date = check_in_date;
        this.check_out_date = check_out_date;
        this.date = date;
        this.client_mobile = client_mobile;
        this.client_name = client_name;
        this.roomName = roomName;

    }

    public double isLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double isLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }

    public String getRoomName() {
        return roomName;
    }

    // Getters
    public String getHotelName() {
        return hotelName;
    }

    public String getLocation() {
        return location;
    }

    public String getImageResource() {
        return imageResource;
    }

    public String getHotel_id() {
        return hotel_id;
    }

    public Double getRoom_count() {
        return room_count;
    }

    public String getRoom_id() {
        return room_id;
    }

    public String getReservation_id() {
        return reservation_id;
    }

    public String getStatus() {
        return status;
    }

    public String getCheck_in_date() {
        return check_in_date;
    }

    public String getCheck_out_date() {
        return check_out_date;
    }

    public String getDate() {
        return date;
    }

    public String getClient_address() {
        return client_address;
    }

    public String getClient_country() {
        return client_country;
    }

    public String getClient_email() {
        return client_email;
    }

    public String getClient_mobile() {
        return client_mobile;
    }

    public String getClient_name() {
        return client_name;
    }

    public Double getRoomCount() {
        return roomCount;
    }
}
