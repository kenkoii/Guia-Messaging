package ph.com.guia.Guide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;

import java.util.ArrayList;
import java.util.Arrays;

import ph.com.guia.Helper.ConnectionChecker;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.Navigation.FilterFragment;
import ph.com.guia.Navigation.HomeFragment;
import ph.com.guia.Navigation.MessageFragment;
import ph.com.guia.Navigation.NoConnectionFragment;
import ph.com.guia.Navigation.SettingFragment;
import ph.com.guia.Navigation.ShareFragment;
import ph.com.guia.Navigation.TripFragment;
import ph.com.guia.R;

public class LoggedInGuide extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean doubleBackToExitPressedOnce = false;
    public static boolean addedFrag = false;
    public static Toolbar mToolbar;
    public static ImageView nav_image;
    public static LinearLayout nav_cover;
    public static FragmentManager fm;
    ArrayList<SharePhoto> photos = new ArrayList<SharePhoto>();

    TextView nav_name, nav_info;
    DrawerLayout drawer;
    ActionBarDrawerToggle mToggle;


    public static String name, bday, gender, age, image, location, contact,
            email, guide_id, fb_id, type;
    public static FragmentTransaction ft;
    HomeFragment hf = new HomeFragment();
    SettingFragment sf = new SettingFragment();
    MessageFragment mf = new MessageFragment();
    FilterFragment ff = new FilterFragment();
    //GuideProfileFragment gpf = new GuideProfileFragment();
    GuideCalendarFragment gcf = new GuideCalendarFragment();
    CreateTourFragment aif = new CreateTourFragment();
    ShareFragment shf = new ShareFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.drawer_layout2);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        MainActivity.manager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));

        fm = getSupportFragmentManager();
        try{
            Bundle b = this.getIntent().getExtras();
            fb_id = b.getString("fb_id");
            name = b.getString("name");
            bday = b.getString("bday");
            gender = b.getString("gender");
            age = b.getString("age");
            image = b.getString("image");
            location = b.getString("location");
            contact = b.getString("contact");
            email = b.getString("email");
            guide_id = b.getString("guide_id");
            type = b.getString("type");
        }
        catch(Exception e){}

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setUpHeader();

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

        JSONParser parser = new JSONParser(this);
        parser.getImageUrl(image, "LoggedInGuide", 0);

        nav_name.setText(name);
        nav_info.setText(email);

        nav_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.drawer_fragment_container, gpf).commit();
                if(!guide_id.equalsIgnoreCase("pending")) {
                    JSONParser.getInstance(LoggedInGuide.this).getGuideById(Constants.getGuideById + guide_id, guide_id, "GuideProfile");
                    drawer.closeDrawer(GravityCompat.START);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoggedInGuide.this);
                    builder.setIcon(R.drawable.ic_launcher);
                    builder.setTitle("Notice");
                    builder.setMessage("\nGuide Request Still Pending!\n");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
                mToolbar.setTitle("Create Tour");
                addedFrag = true;
                ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.drawer_fragment_container, aif).addToBackStack(null).commit();
                break;
            case R.id.done:
                addedFrag = false;
                this.getSupportFragmentManager().popBackStackImmediate();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
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
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        switch(id){
            case R.id.nav_home:
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, hf).commit();
                break;
            case R.id.nav_tours:
                if(!guide_id.equalsIgnoreCase("pending")) {
                    ft = getSupportFragmentManager().beginTransaction();
                    TripFragment tf = new TripFragment();
                    ft.replace(R.id.drawer_fragment_container, tf).commit();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoggedInGuide.this);
                    builder.setIcon(R.drawable.ic_launcher);
                    builder.setTitle("Notice");
                    builder.setMessage("\nGuide Request Still Pending!\n");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                }
                break;
            case R.id.nav_messages:
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, mf).commit();
                break;
            case R.id.nav_settings:
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, sf).commit();
                break;
            case R.id.nav_share:
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.drawer_fragment_container, shf).commit();
//                Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.guia_logo);
//                Bitmap image1 = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
//                SharePhoto photo1 = new SharePhoto.Builder()
//                        .setBitmap(image)
//                        .setCaption("Photo 1")
//                        .build();
//                SharePhoto photo2 = new SharePhoto.Builder()
//                        .setBitmap(image1)
//                        .setCaption("Photo 2")
//                        .build();
//
//                photos.add(photo1);
//                photos.add(photo2);
//
//                SharePhotoContent content = new SharePhotoContent.Builder()
//                        .addPhotos(photos)
//                        .build();
//
//                ShareApi.share(content, null);
                break;
//            case R.id.nav_pending:
//                ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.drawer_fragment_container, pdf).commit();
//                break;
//            case R.id.nav_upcoming:
//                ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.drawer_fragment_container, upf).commit();
//                break;
//            case R.id.nav_previous:
//                ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.drawer_fragment_container, pvf).commit();
//                break;
            case R.id.nav_logout:
                MainActivity.manager.logOut();
                LoggedInGuide.mToolbar = null;
                LoggedInGuide.this.finish();
        }

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
