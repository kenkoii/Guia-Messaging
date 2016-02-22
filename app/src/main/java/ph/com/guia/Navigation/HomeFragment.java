package ph.com.guia.Navigation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Helper.RVadapter;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.Tours;
import ph.com.guia.R;
import ph.com.guia.Traveler.FragmentBookingRequest;
import ph.com.guia.Traveler.LoggedInTraveler;

public class HomeFragment extends Fragment {
    static AppCompatActivity activity;
    public static RecyclerView rv;
    public static LinearLayoutManager llm;
    public static RVadapter adapter;
    public static ProgressDialog pd;
    public static ArrayList<Tours> mList = new ArrayList<Tours>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        pd = ProgressDialog.show(this.getContext(), "Loading", "Please wait...", true, true);
        mList.clear();
        JSONParser.getInstance(getContext()).getAllTours(Constants.getAllTours);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        try {
            LoggedInGuide.mToolbar.setTitle("Explore");
        }catch (Exception e) {
            LoggedInTraveler.mToolbar.setTitle("Explore");
            inflater.inflate(R.menu.filter, menu);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        activity = (AppCompatActivity) view.getContext();
        rv = (RecyclerView) view.findViewById(R.id.cardList);
        rv.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        llm.scrollToPosition(0);

        if(mList.size() > 0)
            pd = ProgressDialog.show(this.getContext(), "Loading", "Please wait...", true, true);

        adapter = new RVadapter(getActivity().getApplicationContext(), HomeFragment.mList, null, null, null);
        rv.setAdapter(adapter);
        try {
            LoggedInGuide.mToolbar.setTitle("Explore");
            LoggedInGuide.doubleBackToExitPressedOnce = false;
        }catch (Exception e){
            LoggedInTraveler.mToolbar.setTitle("Explore");
            LoggedInTraveler.doubleBackToExitPressedOnce = false;
        }

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
}
