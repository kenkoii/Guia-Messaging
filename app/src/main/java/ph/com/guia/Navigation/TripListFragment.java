package ph.com.guia.Navigation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Helper.LVadapter;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.Trip;
import ph.com.guia.R;
import ph.com.guia.Traveler.LoggedInTraveler;
import ph.com.guia.Traveler.UpdateTripFragment;

public class TripListFragment extends Fragment {
    public static ListView lv;
    public static ArrayList<Trip> mList = new ArrayList<Trip>();
    public static LVadapter adapter;
    public static ProgressDialog pd;

    AdapterView.AdapterContextMenuInfo info;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if(item.getItemId() == R.id.edit){
            Bundle bundle = new Bundle();
            bundle.putInt("position", info.position);

            UpdateTripFragment utf = new UpdateTripFragment();
            utf.setArguments(bundle);

            LoggedInTraveler.addedFrag = true;
            LoggedInTraveler.ft = LoggedInTraveler.fm.beginTransaction();
            LoggedInTraveler.ft.add(R.id.drawer_fragment_container, utf).addToBackStack(null).commit();
        }

        return super.onContextItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LoggedInTraveler.mToolbar.setTitle("My Trips");
        mList.clear();
        pd = ProgressDialog.show(this.getContext(), "Loading", "Please wait...", true, true);
        View view = inflater.inflate(R.layout.trip_list, container, false);
        lv = (ListView) view.findViewById(R.id.trip_list);
        JSONParser.getInstance(getContext()).getTripsById(Constants.getTripsById+ LoggedInTraveler.user_id);

        registerForContextMenu(lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("location", mList.get(position).location);

                PreviousFragment pf = new PreviousFragment();
                pf.setArguments(bundle);

                LoggedInTraveler.addedFrag = true;
                LoggedInTraveler.ft = LoggedInTraveler.fm.beginTransaction();
                LoggedInTraveler.ft.replace(R.id.drawer_fragment_container, pf).addToBackStack(null).commit();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LoggedInTraveler.mToolbar.setTitle("My Trips");
        lv.setAdapter(adapter);
    }
}
