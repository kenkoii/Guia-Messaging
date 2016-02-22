package ph.com.guia.Guide;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Helper.RVadapter;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.Tours;
import ph.com.guia.R;

/**
 * Created by Kentoy on 12/30/2015.
 */
public class CompleteTourFragment extends Fragment{

    Tours tour;
    boolean ok;
    NetworkImageView image;
    TextView title, rate, date, traveler_name, description;
    Button btnComplete;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complete_tour, container, false);

        tour = getArguments().getParcelable("tour");
        ok = getArguments().getBoolean("ok");

        LoggedInGuide.addedFrag = true;
        image = (NetworkImageView) view.findViewById(R.id.upcoming_main_image);
        title = (TextView) view.findViewById(R.id.upcoming_title);
        rate = (TextView) view.findViewById(R.id.upcoming_rate);
        date = (TextView) view.findViewById(R.id.upcoming_date);
        traveler_name = (TextView) view.findViewById(R.id.upcoming_traveler);
        description = (TextView) view.findViewById(R.id.upcoming_description);
        btnComplete = (Button) view.findViewById(R.id.upcoming_complete);

        ImageLoader imageLoader = JSONParser.getInstance(getActivity().getApplicationContext()).getImageLoader();
        imageLoader.get(tour.main_image, ImageLoader.getImageListener(image,
                R.drawable.default_home_image, android.R.drawable.ic_dialog_alert));

        image.setImageUrl(tour.main_image, imageLoader);

        title.setText(tour.tour_name);
        rate.setText("Rate: " + tour.tour_rate);
        date.setText("Date: " + tour.duration_format);
        traveler_name.setText(tour.tour_preference);
        description.setText(tour.tour_description);

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject request = new JSONObject();
                try {
                    request.accumulate("_id", tour.tour_location);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RVadapter.CardViewHolder.due.clear();
                JSONParser.getInstance(getActivity().getApplicationContext())
                        .acceptBooking(request, Constants.completeBooking);
            }
        });

        if(!ok) btnComplete.setVisibility(View.GONE);


        return view;
    }


}
