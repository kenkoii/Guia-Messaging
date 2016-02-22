package ph.com.guia.Model;

public class Trip {
    public String id, location, start_date, end_date, image, description;

    public Trip(String id, String location, String start_date, String end_date, String image, String description) {
        this.id = id;
        this.location = location;
        this.start_date = start_date;
        this.end_date = end_date;
        this.image = image;
        this.description = description;
    }
}
