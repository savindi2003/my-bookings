package lk.javainstitute.mybookings.model;

public class PlaceModel {
    private String name;
    private String distance;

    public PlaceModel(String name, String distance) {
        this.name = name;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
    }
}

