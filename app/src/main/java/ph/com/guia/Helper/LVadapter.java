package ph.com.guia.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import ph.com.guia.Model.Trip;
import ph.com.guia.R;

public class LVadapter extends ArrayAdapter<Trip> {
    ArrayList<Trip> trips;

    public LVadapter(Context context, ArrayList<Trip> trips) {
        super(context, 0, trips);
        this.trips = trips;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trip_list_detail, parent, false);
        }

        NetworkImageView iv = (NetworkImageView) convertView.findViewById(R.id.trip_image);

        ImageLoader imageLoader = JSONParser.getInstance(getContext()).getImageLoader();
        imageLoader.get(trips.get(position).image, ImageLoader.getImageListener(iv,
                R.drawable.default_home_image, android.R.drawable.ic_dialog_alert));

        iv.setImageUrl(trips.get(position).image, imageLoader);

        TextView location = (TextView) convertView.findViewById(R.id.trip_location);
        TextView detail = (TextView) convertView.findViewById(R.id.trip_detail);

        location.setText("To: "+trips.get(position).location);
        detail.setText(trips.get(position).description);

        return convertView;
    }
}
