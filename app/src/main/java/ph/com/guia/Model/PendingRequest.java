package ph.com.guia.Model;

public class PendingRequest {
    public String user_name, user_age, user_gender, tour_booked, date_booked, booking_id, image;

    public PendingRequest(String user_name, String user_age, String user_gender, String tour_booked,
                          String date_booked, String booking_id, String image) {
        this.user_name = user_name;
        this.user_age = user_age;
        this.user_gender = user_gender;
        this.tour_booked = tour_booked;
        this.date_booked = date_booked;
        this.booking_id = booking_id;
        this.image = image;
    }
}
