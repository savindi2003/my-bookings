package lk.javainstitute.mybookings.model;

public class Traveller {
    private String name;
    private char firstLetter;

    public Traveller(String name, char firstLetter) {
        this.name = name;
        this.firstLetter=firstLetter;
    }

    public String getName() {
        return name;
    }

    public char getFirstLetter() {
        return name.charAt(0);
    }
}

