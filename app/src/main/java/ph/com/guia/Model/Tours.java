package ph.com.guia.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Tours implements Parcelable {
    public String tour_id, tour_name, tour_location, tour_description,
            duration_format, tour_preference, tour_guideId, main_image,
            activity, guide_name;
    public String[] additional_images;
    public int tour_duration, tour_rate, points;

    public Tours(String tour_id, String tour_name, String tour_location,String tour_description,
                 String duration_format, String tour_preference, String tour_guideId, int tour_rate,
                 String main_image, int tour_duration, String[] additional_images, int points,
                 String activity, String guide_name) {
        this.tour_id = tour_id;
        this.tour_name = tour_name;
        this.tour_location = tour_location;
        this.tour_description = tour_description;
        this.duration_format = duration_format;
        this.tour_preference = tour_preference;
        this.tour_guideId = tour_guideId;
        this.tour_rate = tour_rate;
        this.main_image = main_image;
        this.tour_duration = tour_duration;
        this.additional_images = additional_images;
        this.points = points;
        this.activity = activity;
        this.guide_name = guide_name;
    }

    protected Tours(Parcel in) {
        tour_id = in.readString();
        tour_name = in.readString();
        tour_location = in.readString();
        tour_description = in.readString();
        duration_format = in.readString();
        tour_preference = in.readString();
        tour_guideId = in.readString();
        main_image = in.readString();
        activity = in.readString();
        additional_images = in.createStringArray();
        tour_duration = in.readInt();
        tour_rate = in.readInt();
        points = in.readInt();
    }

    public static final Creator<Tours> CREATOR = new Creator<Tours>() {
        @Override
        public Tours createFromParcel(Parcel in) {
            return new Tours(in);
        }

        @Override
        public Tours[] newArray(int size) {
            return new Tours[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tour_id);
        dest.writeString(tour_name);
        dest.writeString(tour_location);
        dest.writeString(tour_description);
        dest.writeString(duration_format);
        dest.writeString(tour_preference);
        dest.writeString(tour_guideId);
        dest.writeString(main_image);
        dest.writeString(activity);
        dest.writeStringArray(additional_images);
        dest.writeInt(tour_duration);
        dest.writeInt(tour_rate);
        dest.writeInt(points);
    }
}
