package ph.com.guia.Guide;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ph.com.guia.Helper.ImageLoadTask;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Helper.RVadapter;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.Note;
import ph.com.guia.Model.Tours;
import ph.com.guia.Model.review;
import ph.com.guia.R;
import ph.com.guia.Traveler.LoggedInTraveler;

public class GuideProfileFragment extends Fragment {

    public static LinearLayout guide_profile_cover;
    public static ImageView profImage;
    public static ArrayList<review> mList = new ArrayList<review>();
    public static RecyclerView rv;
    public static RVadapter adapter;
    public static ProgressDialog pd;
    public static ArrayList<Note> notes = new ArrayList<Note>();
    double rating;
    String type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            LoggedInGuide.mToolbar.setTitle("Profile");
            setHasOptionsMenu(true);
        }catch(Exception e){}

        mList.clear();
        notes.clear();
        rating = getArguments().getDouble("rating");
        type = getArguments().getString("type");

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.guide_calendar, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guide_profile, container, false);

        rv = (RecyclerView) view.findViewById(R.id.guide_prof_review_rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        JSONParser.getInstance(getActivity().getApplicationContext()).getReviewsByGuideId(Constants.getReviewsByGuideId + LoggedInGuide.guide_id);
        JSONParser.getInstance(getContext()).getNotesByGuideId(Constants.getNotesByGuideId + LoggedInGuide.guide_id);

        guide_profile_cover = (LinearLayout) view.findViewById(R.id.guide_profile_cover);
        profImage = (ImageView) view.findViewById(R.id.main_profile_image);
        TextView profName = (TextView) view.findViewById(R.id.main_profile_name);
        TextView profEmail = (TextView) view.findViewById(R.id.main_profile_email);
        TextView profAge = (TextView) view.findViewById(R.id.main_profile_age);
        TextView profLocation = (TextView) view.findViewById(R.id.location_info);
        TextView profSpecialty = (TextView) view.findViewById(R.id.specialty_info);
        TextView profNumber = (TextView) view.findViewById(R.id.number_info);
        RatingBar rb = (RatingBar) view.findViewById(R.id.rating);


        JSONParser parser = new JSONParser(getActivity().getApplicationContext());
        parser.getImageUrl(MainActivity.cover, "GuideProfileCover", 0);
        parser.getImageUrl(LoggedInGuide.image, "GuideProfile", 0);

        profName.setText(LoggedInGuide.name);
        profEmail.setText(LoggedInGuide.email);
        profAge.setText(LoggedInGuide.age+" years old");
        profLocation.setText(LoggedInGuide.location);
        profSpecialty.setText(type);
        profNumber.setText(LoggedInGuide.contact);
        rb.setMax(5);
        rb.setNumStars(5);
        rb.setRating((float)rating);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            LoggedInGuide.mToolbar.setTitle("Profile");
            LoggedInGuide.doubleBackToExitPressedOnce = false;
        }catch(Exception e){
            LoggedInTraveler.mToolbar.setTitle("Profile");
            LoggedInTraveler.doubleBackToExitPressedOnce = false;
        }
    }
}
