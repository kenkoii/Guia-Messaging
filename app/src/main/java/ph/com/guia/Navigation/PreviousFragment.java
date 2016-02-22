package ph.com.guia.Navigation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import ph.com.guia.Model.Tours;
import ph.com.guia.R;
import ph.com.guia.Traveler.LoggedInTraveler;

public class PreviousFragment extends Fragment {
    public static ArrayList<Tours> mList = new ArrayList<Tours>();
    public static RecyclerView rv;
    public static RVadapter adapter;
    public static ProgressDialog pd;
    public static LinearLayoutManager llm;
    public static String location;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        location = getArguments().getString("location");
        pd = ProgressDialog.show(this.getContext(), "Loading", "Please wait...", true, true);
        mList.clear();

        try {
            JSONObject request = new JSONObject();
            request.accumulate("booking_user_id", LoggedInTraveler.user_id);
            JSONParser.getInstance(getContext()).getBookingsById(request, Constants.getBookingsByUserId, "PreviousTraveler");
        } catch (JSONException je) {
            je.printStackTrace();
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
