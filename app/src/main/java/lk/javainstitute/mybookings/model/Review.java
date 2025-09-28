package lk.javainstitute.mybookings.model;

public class Review {
    private String reviewerName;
    private String reviewText;
    private String reviewDate;
    private String image;
    private double reviewCount;

    public Review(String reviewerName, String reviewText,String reviewDate,String image) {
        this.reviewerName = reviewerName;
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
        this.image = image;

    }

    public String getImage() {
        return image;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public double getReviewCount() {
        return reviewCount;
    }

    public String getReviewText() {
        return reviewText;
    }
}
