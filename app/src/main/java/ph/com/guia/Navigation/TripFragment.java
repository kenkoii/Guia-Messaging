package ph.com.guia.Navigation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.Helper.RVadapter;
import ph.com.guia.R;
import ph.com.guia.Traveler.LoggedInTraveler;

public class TripFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_trip, container, false);

        tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout);

        try {
            LoggedInGuide.mToolbar.setTitle("Tours");
            tabLayout.addTab(tabLayout.newTab().setText("Pending"));
        }
        catch(Exception e){ LoggedInTraveler.mToolbar.setTitle("Tours");}

        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));
        //tabLayout.addTab(tabLayout.newTab().setText("Previous"));

        viewPager = (ViewPager) inflatedView.findViewById(R.id.viewpager);

        viewPager.setAdapter(new PagerAdapter
                (getFragmentManager(), tabLayout.getTabCount()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return inflatedView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_tour, menu);
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            try {
                LoggedInGuide.mToolbar.setTitle("Tours");

                switch (position) {
                    case 0:
                        PendingFragment pf = new PendingFragment();
                        return pf;
                    case 1:
                        UpcomingFragment uf = new UpcomingFragment();
                        return uf;
                    case 2:
                        PreviousFragment pvf = new PreviousFragment();
                        return pvf;
                    default:
                        return null;
                }
            }
            catch(Exception e){
                switch (position) {
                    case 0:
                        UpcomingFragment uf = new UpcomingFragment();
                        return uf;
                    case 1:
                        PreviousFragment pvf = new PreviousFragment();
                        return pvf;
                    default:
                        return null;
                }
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            viewPager.setAdapter(new PagerAdapter
                    (getFragmentManager(), tabLayout.getTabCount()));
        }catch(Exception e){}
    }
}
