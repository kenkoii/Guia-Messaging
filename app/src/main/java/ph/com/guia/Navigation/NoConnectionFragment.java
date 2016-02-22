package ph.com.guia.Navigation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ph.com.guia.Guide.CreateTourFragment;
import ph.com.guia.Guide.GuideCalendarFragment;
import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.Helper.ConnectionChecker;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.R;
import ph.com.guia.Traveler.FragmentNewTrip;
import ph.com.guia.Traveler.LoggedInTraveler;

public class NoConnectionFragment extends Fragment {

    ImageView iv;
    FragmentTransaction ft;
    HomeFragment hf = new HomeFragment();
    SettingFragment sf = new SettingFragment();
    MessageFragment mf = new MessageFragment();
    FilterFragment ff = new FilterFragment();
    GuideCalendarFragment gcf = new GuideCalendarFragment();
    FragmentNewTrip fnt = new FragmentNewTrip();
    CreateTourFragment aif = new CreateTourFragment();
    ShareFragment shf = new ShareFragment();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.no_connection, container, false);
        iv = (ImageView) view.findViewById(R.id.no_connection);
        final int id = getArguments().getInt("id");

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new ConnectionChecker(getContext()).isConnectedToInternet()){
                    if(getContext() instanceof MainActivity){

                    }else if(getContext() instanceof LoggedInTraveler){
                        switch (id){
                            case R.id.filter:
                                LoggedInTraveler.mToolbar.setTitle("Filter");
                                LoggedInTraveler.addedFrag = true;
                                ft = LoggedInTraveler.fm.beginTransaction();
                                ft.replace(R.id.drawer_fragment_container, ff).addToBackStack(null).commit();
                                break;
                            case R.id.calendar:
                                LoggedInTraveler.mToolbar.setTitle("Schedules");
                                LoggedInTraveler.addedFrag = true;
                                ft = LoggedInTraveler.fm.beginTransaction();
                                ft.replace(R.id.drawer_fragment_container, gcf).addToBackStack(null).commit();
                                break;
                            case R.id.add_trip:
                                LoggedInTraveler.mToolbar.setTitle("Schedules");
                                LoggedInTraveler.addedFrag = true;
                                ft = LoggedInTraveler.fm.beginTransaction();
                                ft.replace(R.id.drawer_fragment_container, fnt).addToBackStack(null).commit();
                                break;
                            case R.id.done:
                                LoggedInTraveler.addedFrag = false;
                                LoggedInTraveler.fm.popBackStackImmediate();
                                break;
                            case R.id.nav_home:
                                ft = LoggedInTraveler.fm.beginTransaction();
                                ft.replace(R.id.drawer_fragment_container, hf).commit();
                                break;
                            case R.id.nav_tours:
                                ft = LoggedInTraveler.fm.beginTransaction();
                                TripFragment tf = new TripFragment();
                                ft.replace(R.id.drawer_fragment_container, tf).commit();
                                break;
                            case R.id.nav_messages:
                                ft = LoggedInTraveler.fm.beginTransaction();
                                ft.replace(R.id.drawer_fragment_container, mf).commit();
                                break;
                            case R.id.nav_settings:
                                ft = LoggedInTraveler.fm.beginTransaction();
                                ft.replace(R.id.drawer_fragment_container, sf).commit();
                                break;
                            case R.id.nav_logout:
                                MainActivity.manager.logOut();
                                getActivity().finish();
                                break;
                            case 0:
                                LoggedInTraveler.fm.popBackStackImmediate();
                                break;
                        }
                    }else if(getContext() instanceof LoggedInGuide){
                        switch (id){
                            case R.id.filter:
                                LoggedInGuide.mToolbar.setTitle("Filter");
                                LoggedInGuide.addedFrag = true;
                                ft = LoggedInGuide.fm.beginTransaction();
                                ft.replace(R.id.drawer_fragment_container, ff).addToBackStack(null).commit();
                                break;
                            case R.id.calendar:
                                LoggedInGuide.mToolbar.setTitle("Schedules");
                                LoggedInGuide.addedFrag = true;
                                ft = LoggedInGuide.fm.beginTransaction();
                                ft.replace(R.id.drawer_fragment_container, gcf).addToBackStack(null).commit();
                                break;
                            case R.id.add_trip:
                                LoggedInGuide.mToolbar.setTitle("Create Tour");
                                LoggedInGuide.addedFrag = true;
                                ft = LoggedInGuide.fm.beginTransaction();
                                ft.replace(R.id.drawer_fragment_container, aif).addToBackStack(null).commit();
                                break;
                            case R.id.done:
                                LoggedInGuide.addedFrag = false;
                                LoggedInGuide.fm.popBackStackImmediate();
                                break;
                            case R.id.nav_home:
                                ft = LoggedInGuide.fm.beginTransaction();
                                ft.replace(R.id.drawer_fragment_container, hf).commit();
                                break;
                            case R.id.nav_tours:
                                ft = LoggedInGuide.fm.beginTransaction();
                                TripFragment tf = new TripFragment();
                                ft.replace(R.id.drawer_fragment_container, tf).commit();
                                break;
                            case R.id.nav_messages:
                                ft = LoggedInGuide.fm.beginTransaction();
                                ft.replace(R.id.drawer_fragment_container, mf).commit();
                                break;
                            case R.id.nav_settings:
                                ft = LoggedInGuide.fm.beginTransaction();
                                ft.replace(R.id.drawer_fragment_container, sf).commit();
                                break;
                            case R.id.nav_share:
                                ft = LoggedInGuide.fm.beginTransaction();
                                ft.replace(R.id.drawer_fragment_container, shf).commit();
                                break;
                            case R.id.nav_logout:
                                MainActivity.manager.logOut();
                                LoggedInGuide.mToolbar = null;
                                getActivity().finish();
                                break;
                            case 0:
                                LoggedInGuide.fm.popBackStackImmediate();
                                break;
                            case 1:
                                JSONParser.getInstance(getContext()).getGuideById(Constants.getGuideById + LoggedInGuide.guide_id,
                                        LoggedInGuide.guide_id, "GuideProfile");
                                LoggedInGuide.fm.popBackStackImmediate();
                                break;
                        }
                    }
                }
            }
        });
        return view;
    }
}
