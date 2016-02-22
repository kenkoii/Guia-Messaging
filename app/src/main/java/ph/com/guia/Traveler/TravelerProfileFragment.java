package ph.com.guia.Traveler;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.R;

public class TravelerProfileFragment extends Fragment {

    public static LinearLayout traveler_profile_cover;
    public static ImageView profImage;
    public static ProgressDialog pd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        LoggedInTraveler.mToolbar.setTitle("Profile");

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.guide_calendar, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.traveler_profile, container, false);
        //JSONParser.getInstance(getActivity().getApplicationContext()).getReviewsByGuideId(Constants.getReviewsByGuideId + LoggedInGuide.guide_id);

        traveler_profile_cover = (LinearLayout) view.findViewById(R.id.traveler_profile_cover);
        profImage = (ImageView) view.findViewById(R.id.traveler_profile_image);
        TextView profName = (TextView) view.findViewById(R.id.traveler_profile_name);
        TextView profAge = (TextView) view.findViewById(R.id.traveler_profile_age);
        TextView profPoints = (TextView) view.findViewById(R.id.traveler_points);

        JSONParser parser = new JSONParser(getActivity().getApplicationContext());
        parser.getImageUrl(MainActivity.cover, "TravelerProfileCover", 0);
        parser.getImageUrl(LoggedInTraveler.image, "TravelerProfile", 0);

        profName.setText(LoggedInTraveler.name);
        profAge.setText(LoggedInTraveler.age + " years old");
        profPoints.setText("Points: "+MainActivity.points);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LoggedInTraveler.mToolbar.setTitle("Profile");
        LoggedInTraveler.doubleBackToExitPressedOnce = false;
    }
}
