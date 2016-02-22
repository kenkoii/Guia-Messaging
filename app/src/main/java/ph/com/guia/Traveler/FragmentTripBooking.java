package ph.com.guia.Traveler;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ph.com.guia.Helper.DBHelper;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Helper.RVadapter;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.Tours;
import ph.com.guia.R;

public class FragmentTripBooking extends Fragment {

    public static String location, start, end;
    public static RecyclerView rv;
    public static LinearLayoutManager llm;
    public static RVadapter adapter;
    public static ProgressDialog pd;
    public static ArrayList<Tours> mList = new ArrayList<Tours>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        location = getArguments().getString("location");
        start = getArguments().getString("start");
        end = getArguments().getString("end");

        mList.clear();
        pd = ProgressDialog.show(this.getContext(), "Loading", "Please wait...", true, false);

        //Toast.makeText(getActivity().getApplicationContext(), location, Toast.LENGTH_LONG).show();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("tour_location", location);

            JSONParser parser = new JSONParser(getActivity().getApplicationContext());
            parser.getAllToursByPreference(jsonObject, Constants.getAllToursByPreference);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        rv = (RecyclerView) view.findViewById(R.id.cardList);
        rv.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        return view;
    }

    public static void onCardClick(Tours tour){
        LoggedInTraveler.addedFrag = true;
        Bundle bundle = new Bundle();
        bundle.putParcelable("tour", tour);
        FragmentBookingRequest fbr = new FragmentBookingRequest();
        fbr.setArguments(bundle);
        FragmentTransaction ft = LoggedInTraveler.fm.beginTransaction();
        ft.replace(R.id.drawer_fragment_container, fbr).addToBackStack(null).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        llm.scrollToPosition(0);
        adapter = new RVadapter(getActivity().getApplicationContext(), mList, null, null, null);
        rv.setAdapter(adapter);
    }
}
