package ph.com.guia.Traveler;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ph.com.guia.Guide.GuideCalendarFragment;
import ph.com.guia.Helper.ConnectionChecker;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.Navigation.FilterFragment;
import ph.com.guia.Navigation.HomeFragment;
import ph.com.guia.Navigation.MessageFragment;
import ph.com.guia.Navigation.NoConnectionFragment;
import ph.com.guia.Navigation.PreviousFragment;
import ph.com.guia.Navigation.SettingFragment;
import ph.com.guia.Navigation.TripFragment;
import ph.com.guia.Navigation.TripListFragment;
import ph.com.guia.Navigation.UpcomingFragment;
import ph.com.guia.R;

public class LoggedInTraveler extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean doubleBackToExitPressedOnce = false;
    public static boolean addedFrag = false;
    public static Toolbar mToolbar;
    public static ImageView nav_image;
    public static LinearLayout nav_cover;
    public static FragmentTransaction ft;
    DrawerLayout drawer;
    TextView nav_name, nav_info;
    ActionBarDrawerToggle mToggle;

    public static String name, bday, gender, age, image, fb_id, user_id;
    HomeFragment hf = new HomeFragment();
    SettingFragment sf = new SettingFragment();
    MessageFragment mf = new MessageFragment();
    FilterFragment ff = new FilterFragment();
    GuideCalendarFragment gcf = new GuideCalendarFragment();
    FragmentNewTrip fnt = new FragmentNewTrip();
    TripListFragment tlf = new TripListFragment();
    public static FragmentManager fm;
    public static LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        try{
            Bundle b = this.getIntent().getExtras();
            fb_id = b.getString("fb_id");
            name = b.getString("name");
            bday = b.getString("bday");
            gender = b.getString("gender");
            age = b.getString("age");
            image = b.getString("image");
            user_id = b.getString("user_id");
        }
        catch(Exception e){}

        fm = getSupportFragmentManager();
        inflater = getLayoutInflater();

        setUpHeader();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        mToggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mToggle);
        mToggle.syncState();


        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.drawer_fragment_container, hf).commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void setUpHeader(){
        nav_image = (ImageView) findViewById(R.id.nav_image);
        nav_cover = (LinearLayout) findViewById(R.id.nav_cover);
        nav_name = (TextView) findViewById(R.id.nav_name);
        nav_info = (TextView) findViewById(R.id.nav_info);

        JSONParser.getInstance(this).getImageUrl(image, "LoggedInTraveler", 0);
        nav_name.setText(name);
        nav_info.setText(gender.substring(0,1).toUpperCase()+gender.substring(1)+", "+age);

        nav_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravelerProfileFragment tpf = new TravelerProfileFragment();
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, tpf).commit();
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(addedFrag) fm.popBackStackImmediate();

        if(!new ConnectionChecker(this).isConnectedToInternet()){
            Bundle bundle = new Bundle();
            bundle.putInt("id", id);

            NoConnectionFragment ncf = new NoConnectionFragment();
            ncf.setArguments(bundle);

            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.drawer_fragment_container, ncf).commit();
            return true;
        }

        switch (id){
            case R.id.filter:
                mToolbar.setTitle("Filter");
                addedFrag = true;
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, ff).addToBackStack(null).commit();
                break;
            case R.id.calendar:
                mToolbar.setTitle("Schedules");
                addedFrag = true;
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, gcf).addToBackStack(null).commit();
                break;
            case R.id.add_trip:
                mToolbar.setTitle("Schedules");
                addedFrag = true;
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, fnt).addToBackStack(null).commit();
                break;
            case R.id.done:
                addedFrag = false;
                ft = getSupportFragmentManager().beginTransaction();
                HomeFragment home = new HomeFragment();
                ft.replace(R.id.drawer_fragment_container, home).commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //try{fm.popBackStackImmediate();}
        //catch(Exception e){}

        addedFrag = false;
        doubleBackToExitPressedOnce = false;

        int id = item.getItemId();

        if(!new ConnectionChecker(this).isConnectedToInternet()){
            Bundle bundle = new Bundle();
            bundle.putInt("id", id);

            NoConnectionFragment ncf = new NoConnectionFragment();
            ncf.setArguments(bundle);

            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.drawer_fragment_container, ncf).commit();
            return true;
        }

        switch(id){
            case R.id.nav_home:
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, hf).commit();
                break;
            case R.id.nav_tours:
                ft = getSupportFragmentManager().beginTransaction();
                TripFragment tf = new TripFragment();
                ft.replace(R.id.drawer_fragment_container, tf).commit();
                break;
            case R.id.nav_trips:
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, tlf).commit();
                break;
            case R.id.nav_messages:
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, mf).commit();
                break;
            case R.id.nav_settings:
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, sf).commit();
                break;
            case R.id.nav_logout:
                MainActivity.manager.logOut();
                LoggedInTraveler.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            MainActivity.end = true;
            return;
        }
        if (!addedFrag) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        }
        else {
            addedFrag = false;
            super.onBackPressed();
            return;
        }
    }


}
