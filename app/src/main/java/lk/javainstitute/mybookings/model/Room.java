package lk.javainstitute.mybookings.model;

public class Room {


    private String name;
    private String id;
    private String imageUrl;
    private String hotel_id;
    private String room_type;
    private double pricePerNight;
    private String maxPerson;
    private double roomCount;

    private String bed;
    private String size;
    private String bathroom;
    private String sound;
    private String wifi;
    private String balcony;

    private String view;

    private int count;
    private int availabe_count;

    private double count_d;
    private double availabe_count_d;

    public Room(String roomId, String roomName, int availabe_count_d,int count_d, double pricePerNight, String maxGuests, String imageUrl) {
        this.id = roomId;
        this.name = roomName;
        this.availabe_count = availabe_count_d;
        this.count = count_d;
        this.pricePerNight = pricePerNight;
        this.maxPerson = maxGuests;
        this.imageUrl = imageUrl;
    }

    public Room(String name, String id, String imageUrl, String hotel_id, String room_type, double pricePerNight, String maxPerson, double roomCount, String bed, String size, String bathroom, String sound, String wifi, String view, String balcony) {
        this.name = name;
        this.id = id;
        this.imageUrl = imageUrl;
        this.hotel_id = hotel_id;
        this.room_type = room_type;
        this.pricePerNight = pricePerNight;
        this.maxPerson = maxPerson;
        this.roomCount = roomCount;
        this.bed = bed;
        this.size = size;
        this.bathroom = bathroom;
        this.sound = sound;
        this.wifi = wifi;
        this.view = view;
        this.balcony = balcony;
    }

    public Room(String roomId, String roomName, int count, double price) {
        this.id = roomId;
        this.name = roomName;
        this.count = count;
        this.pricePerNight = price;
    }

    public Room() {}

    public double getCount_d() {
        return count_d;
    }

    public double getAvailabe_count_d() {
        return availabe_count_d;
    }

    public int getAvailabe_count() {
        return availabe_count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setAvailabe_count(int availabe_count) {
        this.availabe_count = availabe_count;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getMaxPerson() {
        return maxPerson;
    }

    public void setMaxPerson(String maxPerson) {
        this.maxPerson = maxPerson;
    }

    public double getRoomCount() {
        return roomCount;
    }

    public String getBed() {
        return bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBathroom() {
        return bathroom;
    }

    public void setBathroom(String bathroom) {
        this.bathroom = bathroom;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getWifi() {
        return wifi;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public String getBalcony() {
        return balcony;
    }

    public void setBalcony(String balcony) {
        this.balcony = balcony;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public void setRoomCount(double roomCount) {
        this.roomCount = roomCount;
    }
}

