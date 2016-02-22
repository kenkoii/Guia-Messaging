package ph.com.guia.Navigation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Helper.RVadapter;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.PendingRequest;
import ph.com.guia.Model.Tours;
import ph.com.guia.R;
import ph.com.guia.Traveler.LoggedInTraveler;

public class UpcomingFragment extends Fragment {

    public static ArrayList<Tours> mList = new ArrayList<Tours>();
    public static RecyclerView rv;
    public static RVadapter adapter;
    public static ProgressDialog pd;
    public static LinearLayoutManager llm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pd = ProgressDialog.show(this.getContext(), "Loading", "Please wait...", true, true);
        mList.clear();

        try {
            LoggedInGuide.mToolbar.setTitle("Tours");
            if (!LoggedInGuide.guide_id.equals("")) {
                JSONObject request = new JSONObject();
                try {
                    request.accumulate("booking_guide_id", LoggedInGuide.guide_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONParser parser = new JSONParser(getActivity());
                parser.getBookingsById(request, Constants.getBookingsByGuideId, "UpcomingFragment");
            }
        }catch(Exception e){
            JSONObject request = new JSONObject();
            try {
                request.accumulate("booking_user_id", LoggedInTraveler.user_id);
            } catch (JSONException je) {
                je.printStackTrace();
            }

            JSONParser parser = new JSONParser(getActivity());
            parser.getBookingsById(request, Constants.getBookingsByUserId, "UpcomingTraveler");
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
}
