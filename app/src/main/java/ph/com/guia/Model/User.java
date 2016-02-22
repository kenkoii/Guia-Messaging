package ph.com.guia.Model;

public class User {
    public String id, name, birthday, gender, age, location, type;
    public int tourCount;
    public float rating;

    public User(String id, String name, String birthday, String gender, String age, String location, String type, int tourCount, float rating) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.age = age;
        this.location = location;
        this.type = type;
        this.tourCount = tourCount;
        this.rating = rating;
    }
}
