package ph.com.guia.Model;

public class review {
    public String review_image, review_name, review_text;
    public int review_rate;

    public review(String review_image, String review_name, String review_text, int review_rate) {
        this.review_image = review_image;
        this.review_name = review_name;
        this.review_text = review_text;
        this.review_rate = review_rate;
    }
}
