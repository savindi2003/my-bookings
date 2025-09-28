package lk.javainstitute.mybookings.model;

public class Attraction {
    private String name;
    private String distance;

    public Attraction(String name, String distance) {
        this.name = name;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getDistance() {
        return distance;
    }
}

