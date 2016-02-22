package ph.com.guia.Helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.logging.Filter;

import ph.com.guia.Guide.CreateTourFragment;
import ph.com.guia.Guide.GuideAddInfoFragment;
import ph.com.guia.Guide.GuideCalendarFragment;
import ph.com.guia.Guide.GuideProfileFragment;
import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.Model.Note;
import ph.com.guia.Model.Trip;
import ph.com.guia.Navigation.FilterFragment;
import ph.com.guia.Navigation.NoConnectionFragment;
import ph.com.guia.Navigation.PreviousFragment;
import ph.com.guia.Navigation.TripListFragment;
import ph.com.guia.Traveler.TravelerProfileFragment;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.PendingRequest;
import ph.com.guia.Model.Tours;
import ph.com.guia.Model.review;
import ph.com.guia.Navigation.HomeFragment;
import ph.com.guia.Navigation.PendingFragment;
import ph.com.guia.Navigation.ShareFragment;
import ph.com.guia.Navigation.TripFragment;
import ph.com.guia.Navigation.UpcomingFragment;
import ph.com.guia.R;
import ph.com.guia.RegisterActivity;
import ph.com.guia.Traveler.FragmentBookingRequest;
import ph.com.guia.Traveler.FragmentNewTrip;
import ph.com.guia.Traveler.FragmentTripBooking;
import ph.com.guia.Traveler.LoggedInTraveler;
import ph.com.guia.Traveler.UpdateTripFragment;

public class JSONParser {

    private static JSONParser parser;
    static Context context;
    RequestQueue mRequestQueue;
    private ImageLoader imageLoader;
    public static int size=0;

    public JSONParser(Context context) {
        this.context = context;
        this.mRequestQueue = getRequestQueue();

        imageLoader = new ImageLoader(mRequestQueue,
            new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap>
                        cache = new LruCache<String, Bitmap>(20);

                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            });
    }

    public static synchronized JSONParser getInstance(Context context) {
        JSONParser.context = context;
        if (parser == null) {
            parser = new JSONParser(context);
        }
        return parser;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(context.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public void acceptBooking(final JSONObject request, final String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.e("Accept Booking", response.toString());
                        TripFragment tf = new TripFragment();
                        LoggedInGuide.ft = LoggedInGuide.fm.beginTransaction();
                        LoggedInGuide.ft.replace(R.id.drawer_fragment_container, tf).commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("ACCEPTBOOKING", error.getMessage());
                if(new ConnectionChecker(context).isConnectedToInternet()){
                    acceptBooking(request, url);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", R.id.nav_tours);

                    NoConnectionFragment ncf = new NoConnectionFragment();
                    ncf.setArguments(bundle);

                    LoggedInGuide.ft = LoggedInGuide.fm.beginTransaction();
                    LoggedInGuide.ft.replace(R.id.drawer_fragment_container, ncf).commit();
                }
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void requestBooking(final JSONObject request, final String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("REQUESTBOOKING", error.getMessage());
                if(new ConnectionChecker(context).isConnectedToInternet()){
                    requestBooking(request, url);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", 0);

                    NoConnectionFragment ncf = new NoConnectionFragment();
                    ncf.setArguments(bundle);

                    LoggedInTraveler.ft = LoggedInTraveler.fm.beginTransaction();
                    LoggedInTraveler.ft.replace(R.id.drawer_fragment_container, ncf).addToBackStack(null).commit();
                }
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void getBookingsById(final JSONObject request, final String url, final String activity){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        size = 0;
                        for(int i = 0; i < response.length(); i++){
                            try {
                                JSONObject req = response.getJSONObject(i);

                                String tour_id, tour_name, tour_location, tour_description,
                                        duration_format, tour_preference, tour_guideId,main_image;
                                String[] additional_images;
                                int tour_duration, tour_rate, points;

                                String booking_id = req.getString("_id");
                                String date = req.getString("start_date");
                                String image = req.getJSONObject("user").getString("profImage");
                                String user_name = req.getJSONObject("user").getString("name");
                                String user_gender = req.getJSONObject("user").getString("gender");
                                String user_age = req.getJSONObject("user").getString("age");
                                tour_id = req.getJSONObject("tour").getString("id");
                                tour_name = req.getJSONObject("tour").getString("name");
                                tour_duration = req.getJSONObject("tour").getInt("duration");
                                tour_description = req.getJSONObject("tour").getString("details");
                                //tour_preference = req.getJSONObject("tour").getString("tour_preference");
                                tour_guideId = req.getJSONObject("tour").getString("tour_guide_id");
                                tour_rate = req.getJSONObject("tour").getInt("rate");
                                main_image = req.getJSONObject("tour").getString("main_image");
                                points = req.getJSONObject("tour").getInt("points");

                                if(activity.equalsIgnoreCase("PendingFragment")) {
                                    if (req.getString("status").equalsIgnoreCase("pending")) {
                                        size++;

                                        PendingFragment.mList.add(new PendingRequest(user_name, user_age, user_gender,
                                                tour_name, date, booking_id, image));
                                        //getTourById(Constants.getTourById + tour_id, user_id, booking_id, date, activity);
                                    }

                                    if(i == response.length()-1) {
                                        PendingFragment.adapter = new RVadapter(context, null, null, null, PendingFragment.mList);
                                        PendingFragment.rv.setAdapter(PendingFragment.adapter);
                                        PendingFragment.pd.dismiss();
                                    }
                                }else if(activity.equalsIgnoreCase("UpcomingFragment")){
                                    if (req.getString("status").equalsIgnoreCase("accepted")) {
                                        size++;

                                        UpcomingFragment.mList.add(new Tours(tour_id, tour_name, booking_id,
                                                tour_description, date, user_name, tour_guideId, tour_rate,
                                                main_image, tour_duration, null, points, "UpcomingFragment", ""));
                                    }

                                    if(i == response.length()-1){
                                        UpcomingFragment.adapter = new RVadapter(context, UpcomingFragment.mList, null, null, null);
                                        UpcomingFragment.rv.setAdapter(UpcomingFragment.adapter);
                                        UpcomingFragment.pd.dismiss();
                                    }
                                }else if(activity.equalsIgnoreCase("UpcomingTraveler")){
                                    if (req.getString("status").equalsIgnoreCase("accepted") ||
                                            req.getString("status").equalsIgnoreCase("completed")) {
                                        size++;

                                        UpcomingFragment.mList.add(new Tours(req.getString("status"), tour_name, booking_id,
                                                tour_description, date, user_name, tour_guideId, tour_rate,
                                                main_image, tour_duration, null, points, "UpcomingTraveler",
                                                req.getJSONObject("guide").getString("name")));
                                    }

                                    if(i == response.length()-1){
                                        UpcomingFragment.adapter = new RVadapter(context, UpcomingFragment.mList, null, null, null);
                                        UpcomingFragment.rv.setAdapter(UpcomingFragment.adapter);
                                        UpcomingFragment.pd.dismiss();
                                    }
                                }else if(activity.equalsIgnoreCase("PreviousTraveler")){
                                    tour_location = req.getJSONObject("tour").getString("tour_location");
                                    if (req.getString("status").equalsIgnoreCase("done") &&
                                            tour_location.equalsIgnoreCase(PreviousFragment.location)) {
                                        size++;

                                        PreviousFragment.mList.add(new Tours(req.getString("status"), tour_name, booking_id,
                                                tour_description, date, user_name, tour_guideId, tour_rate,
                                                main_image, tour_duration, null, points, "PreviousTraveler", ""));
                                    }

                                    if(i == response.length()-1){
                                        PreviousFragment.adapter = new RVadapter(context, PreviousFragment.mList, null, null, null);
                                        PreviousFragment.rv.setAdapter(PreviousFragment.adapter);
                                        PreviousFragment.pd.dismiss();
                                        LoggedInTraveler.mToolbar.setTitle(PreviousFragment.location+" Tours");
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if(response.length() == 0 && activity.equalsIgnoreCase("PendingFragment")) PendingFragment.pd.dismiss();
                        else if(response.length() == 0 && (activity.equalsIgnoreCase("UpcomingFragment") ||
                                activity.equalsIgnoreCase("UpcomingTraveler"))) UpcomingFragment.pd.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETBOOKINGSBYID", error.getMessage());
                if(new ConnectionChecker(context).isConnectedToInternet()){
                    getBookingsById(request, url, activity);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", R.id.nav_tours);

                    NoConnectionFragment ncf = new NoConnectionFragment();
                    ncf.setArguments(bundle);

                    if(context instanceof LoggedInGuide) {
                        LoggedInGuide.ft = LoggedInGuide.fm.beginTransaction();
                        LoggedInGuide.ft.replace(R.id.drawer_fragment_container, ncf).commit();
                    }else if(context instanceof LoggedInTraveler){
                        LoggedInTraveler.ft = LoggedInTraveler.fm.beginTransaction();
                        LoggedInTraveler.ft.replace(R.id.drawer_fragment_container, ncf).commit();
                    }
                }
            }
        });
        mRequestQueue.add(jsonArrayRequest);
    }

    public void addTour(final JSONObject request, final String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CreateTourFragment.pd.dismiss();
                        LoggedInGuide.fm.popBackStackImmediate();
                        Toast.makeText(context, "New Tour Created!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("ADDTOUR", error.getMessage());
                if(new ConnectionChecker(context).isConnectedToInternet()){
                    addTour(request, url);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", 0);

                    NoConnectionFragment ncf = new NoConnectionFragment();
                    ncf.setArguments(bundle);

                    if(context instanceof LoggedInGuide) {
                        LoggedInGuide.ft = LoggedInGuide.fm.beginTransaction();
                        LoggedInGuide.ft.replace(R.id.drawer_fragment_container, ncf).addToBackStack(null).commit();
                    }else if(context instanceof LoggedInTraveler){
                        LoggedInTraveler.ft = LoggedInTraveler.fm.beginTransaction();
                        LoggedInTraveler.ft.replace(R.id.drawer_fragment_container, ncf).addToBackStack(null).commit();
                    }
                }
            }
        });
       mRequestQueue.add(jsonObjectRequest);
    }

    public void getAllTours(final String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String tour_id, tour_name, tour_location, tour_description,
                                duration_format, tour_preference, tour_guideId,main_image,
                                tour_gender, guide_name;
                        String[] additional_images;
                        int tour_duration, tour_rate, points;

                        size = response.length();

                        for(int i = 0; i < response.length(); i++){
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                tour_guideId = obj.getString("tour_guide_id");
                                guide_name = obj.getJSONObject("user").getString("name");

                                if(!tour_guideId.equalsIgnoreCase("deactivated")) {
                                    tour_id = obj.getString("_id");
                                    tour_name = obj.getString("name");
                                    tour_location = obj.getString("tour_location");
                                    tour_duration = obj.getInt("duration");
                                    duration_format = obj.getString("duration_format");
                                    tour_description = obj.getString("details");
                                    tour_preference = obj.getString("tour_preference");
                                    tour_rate = obj.getInt("rate");
                                    main_image = obj.getString("main_image");
                                    points = obj.getInt("points");
                                    tour_gender = obj.getJSONObject("user").getString("gender");

                                    if(context instanceof LoggedInGuide) {
                                        HomeFragment.mList.add(new Tours(tour_id, tour_name, tour_location,
                                                tour_description, duration_format, tour_preference, tour_guideId,
                                                tour_rate, main_image, tour_duration, null, points, "HomeFragment",
                                                guide_name));
                                    }else{
                                        DBHelper db = new DBHelper(context);
                                        Cursor c = db.getFilter();
                                        if(c.moveToFirst()) {
                                            boolean ok = false;
                                            String gender = c.getString(c.getColumnIndex("gender"));
                                            String interest = c.getString(c.getColumnIndex("interest"));

                                            StringTokenizer st = new StringTokenizer(tour_preference, "/");
                                            while(st.hasMoreTokens()){
                                                if(interest.contains(st.nextToken())){
                                                    ok = true;
                                                    break;
                                                }
                                            }

                                            if((ok || interest.equals("13")) && (gender.equalsIgnoreCase(tour_gender) || gender.equalsIgnoreCase("Both")) &&
                                                (!obj.getJSONObject("user").getString("id").equals(LoggedInTraveler.user_id))){
                                                HomeFragment.mList.add(new Tours(tour_id, tour_name, tour_location,
                                                        tour_description, duration_format, tour_preference, tour_guideId,
                                                        tour_rate, main_image, tour_duration, null, points, "HomeFragment",
                                                        guide_name));
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(i == response.length()-1) {
                                MainActivity.pd.dismiss();
                                HomeFragment.pd.dismiss();
                                HomeFragment.adapter = new RVadapter(context, HomeFragment.mList, null, null, null);
                                HomeFragment.rv.setAdapter(HomeFragment.adapter);
                            }
                        }

                        if(response.length() == 0){
                            MainActivity.pd.dismiss();
                            HomeFragment.pd.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETALLTOURS", error.getMessage());
                if(new ConnectionChecker(context).isConnectedToInternet()) {
                    getAllTours(url);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", R.id.nav_home);

                    NoConnectionFragment ncf = new NoConnectionFragment();
                    ncf.setArguments(bundle);

                    if(context instanceof LoggedInGuide) {
                        LoggedInGuide.ft = LoggedInGuide.fm.beginTransaction();
                        LoggedInGuide.ft.replace(R.id.drawer_fragment_container, ncf).commit();
                    }else if(context instanceof LoggedInTraveler){
                        LoggedInTraveler.ft = LoggedInTraveler.fm.beginTransaction();
                        LoggedInTraveler.ft.replace(R.id.drawer_fragment_container, ncf).commit();
                    }
                }
            }
        });
        mRequestQueue.add(jsonArrayRequest);
    }

    public void getAllToursByPreference(final JSONObject jsonObject, final String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String tour_id, tour_name, tour_location, tour_description,
                                duration_format, tour_preference, tour_guideId,main_image,
                                tour_gender, guide_name;
                        String[] additional_images;
                        int tour_duration, tour_rate, points;

                        DBHelper db = new DBHelper(context);
                        Cursor c = db.getFilter();
                        if(c.moveToFirst()){
                            String gender = c.getString(c.getColumnIndex("gender"));
                            String interest = c.getString(c.getColumnIndex("interest"));

                            size = response.length();

                            Log.e("response", response.length()+"");
                            for(int i = 0; i < response.length(); i++){
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    boolean ok = false;

                                    tour_guideId = obj.getString("tour_guide_id");
                                    guide_name = obj.getJSONObject("user").getString("name");
                                    tour_preference = obj.getString("tour_preference");
                                    tour_gender = obj.getJSONObject("user").getString("gender");

                                    StringTokenizer st = new StringTokenizer(tour_preference, "/");
                                    while(st.hasMoreTokens()){
                                        if(interest.contains(st.nextToken())){
                                            ok = true;
                                            break;
                                        }
                                    }

                                    Log.e("Error", String.valueOf(ok && !tour_guideId.equalsIgnoreCase("deactivated") &&
                                            (gender.equalsIgnoreCase(tour_gender) || gender.equalsIgnoreCase("Both"))));
                                    if(ok && !tour_guideId.equalsIgnoreCase("deactivated") &&
                                            (gender.equalsIgnoreCase(tour_gender) || gender.equalsIgnoreCase("Both")) ) {
                                        tour_id = obj.getString("_id");
                                        tour_name = obj.getString("name");
                                        tour_location = obj.getString("tour_location");
                                        tour_duration = obj.getInt("duration");
                                        duration_format = obj.getString("duration_format");
                                        tour_description = obj.getString("details");
                                        tour_rate = obj.getInt("rate");
                                        main_image = obj.getString("main_image");
                                        points = obj.getInt("points");

                                        FragmentTripBooking.mList.add(new Tours(tour_id, tour_name, tour_location,
                                                tour_description, duration_format, tour_preference, tour_guideId,
                                                tour_rate, main_image, tour_duration, null, points, "FragmentTripBooking",
                                                guide_name));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show();

                                if(i == response.length()-1) {
                                    FragmentTripBooking.adapter = new RVadapter(context, FragmentTripBooking.mList, null, null, null);
                                    FragmentTripBooking.rv.setAdapter(FragmentTripBooking.adapter);
                                    FragmentTripBooking.pd.dismiss();
                                }
                            }
                        }else{
                            FilterFragment ff = new FilterFragment();
                            LoggedInTraveler.mToolbar.setTitle("Filter");
                            LoggedInTraveler.fm.popBackStackImmediate();
                            LoggedInTraveler.ft = LoggedInTraveler.fm.beginTransaction();
                            LoggedInTraveler.ft.replace(R.id.drawer_fragment_container, ff).addToBackStack(null).commit();
                        }

                        if(response.length() == 0){
                            FragmentTripBooking.pd.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETALLTOURSBYPREFERENCE", error.getMessage());
                if(new ConnectionChecker(context).isConnectedToInternet()){
                    getAllToursByPreference(jsonObject, url);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", R.id.add_trip);

                    NoConnectionFragment ncf = new NoConnectionFragment();
                    ncf.setArguments(bundle);

                    if(context instanceof LoggedInGuide) {
                        LoggedInGuide.ft = LoggedInGuide.fm.beginTransaction();
                        LoggedInGuide.ft.replace(R.id.drawer_fragment_container, ncf).commit();
                    }else if(context instanceof LoggedInTraveler){
                        LoggedInTraveler.ft = LoggedInTraveler.fm.beginTransaction();
                        LoggedInTraveler.ft.replace(R.id.drawer_fragment_container, ncf).commit();
                    }
                }
            }
        });
        mRequestQueue.add(jsonArrayRequest);
    }

    public void getTourById(String url, final String user_id, final String booking_id, final String date, final String activity){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(activity.equalsIgnoreCase("PendingFragment")) {
                                String tour_name = response.getString("name");
                                getUserById(Constants.getUserById + user_id, tour_name, booking_id, date);
                            }else if(activity.equalsIgnoreCase("UpcomingFragment")){
                                String tour_id, tour_name, tour_location, tour_description,
                                        duration_format, tour_preference, tour_guideId,main_image;
                                String[] additional_images;
                                int tour_duration, tour_rate, points;

                                tour_id = response.getString("_id");
                                tour_name = response.getString("name");
                                tour_location = response.getString("tour_location");
                                tour_duration = response.getInt("duration");
                                duration_format = response.getString("duration_format");
                                tour_description = response.getString("details");
                                tour_preference = response.getString("tour_preference");
                                tour_guideId = response.getString("tour_guide_id");
                                tour_rate = response.getInt("rate");
                                main_image = response.getString("main_image");
                                points = response.getInt("points");

                                UpcomingFragment.mList.add(new Tours(tour_id, tour_name, tour_location,
                                        tour_description, date, tour_preference, tour_guideId, tour_rate,
                                        main_image, tour_duration, null, points, "Upcoming", ""));

                                UpcomingFragment.adapter = null;
                                UpcomingFragment.adapter = new RVadapter(context, UpcomingFragment.mList, null, null, null);
                                UpcomingFragment.rv.setAdapter(UpcomingFragment.adapter);
                                UpcomingFragment.pd.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETTOURBYID", error.getMessage());
            }
        });
       mRequestQueue.add(jsonObjectRequest);
    }

    public void getUserById(final String url, final String tour_name, final String booking_id, final String date){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String image = response.getString("profImage");
                            String user_name = response.getString("name");
                            String user_gender = response.getString("gender");
                            String user_age = response.getString("age");

                            PendingFragment.mList.add(new PendingRequest(user_name, user_age, user_gender,
                                    tour_name, date, booking_id, image));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Log.e("SizesP", PendingFragment.mList.size()+" "+size);
                        //if(PendingFragment.mList.size() == size) {
                            PendingFragment.adapter = new RVadapter(context, null, null, null, PendingFragment.mList);
                            PendingFragment.rv.setAdapter(PendingFragment.adapter);
                        //}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETUSERBYID", error.getMessage());
                if(new ConnectionChecker(context).isConnectedToInternet()){
                    getUserById(url, tour_name, booking_id, date);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", R.id.nav_home);

                    NoConnectionFragment ncf = new NoConnectionFragment();
                    ncf.setArguments(bundle);

                    if(context instanceof LoggedInGuide) {
                        LoggedInGuide.ft = LoggedInGuide.fm.beginTransaction();
                        LoggedInGuide.ft.replace(R.id.drawer_fragment_container, ncf).commit();
                    }else if(context instanceof LoggedInTraveler){
                        LoggedInTraveler.ft = LoggedInTraveler.fm.beginTransaction();
                        LoggedInTraveler.ft.replace(R.id.drawer_fragment_container, ncf).commit();
                    }
                }
            }
        });
       mRequestQueue.add(jsonObjectRequest);
    }

    public void postLogin(final JSONObject request, final String url){
        JsonObjectRequest jsonObjectRequest =    new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String user_id = response.getString("_id");
                            String guide_id = response.getString("guide_id");

                            MainActivity.user_id = user_id;
                            MainActivity.points = response.getDouble("points");

                            DBHelper db = new DBHelper(context);
                            Cursor c = db.getSettingById(MainActivity.fb_id);
                            if(!c.moveToFirst()) {
                                db.addSetting(MainActivity.fb_id);
                                Intent intent = new Intent(context, RegisterActivity.class);
                                intent.putExtra("fb_id", MainActivity.fb_id);
                                intent.putExtra("name", MainActivity.name);
                                intent.putExtra("bday", MainActivity.bday);
                                intent.putExtra("gender", MainActivity.gender);
                                intent.putExtra("age", MainActivity.age);
                                intent.putExtra("image", MainActivity.image);
                                intent.putExtra("default", 1);
                                intent.putExtra("guide_id", guide_id);
                                intent.putExtra("user_id", user_id);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);

                            }
                            else{
                                //Toast.makeText(getApplicationContext(), String.valueOf(c.getInt(c.getColumnIndex("isTraveler"))), Toast.LENGTH_LONG).show();
                                if(c.getInt(c.getColumnIndex("isTraveler")) == 1){
                                    Intent intent = new Intent(context, LoggedInTraveler.class);
                                    intent.putExtra("fb_id", MainActivity.fb_id);
                                    intent.putExtra("name", MainActivity.name);
                                    intent.putExtra("bday", MainActivity.bday);
                                    intent.putExtra("gender", MainActivity.gender);
                                    intent.putExtra("age", MainActivity.age);
                                    intent.putExtra("image", MainActivity.image);
                                    intent.putExtra("user_id", user_id);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                                else {
                                    //Toast.makeText(getApplicationContext(), obj.toString(), Toast.LENGTH_LONG).show();

                                    if(guide_id.equals("")){
                                        Intent intent = new Intent(context, RegisterActivity.class);
                                        intent.putExtra("fb_id", MainActivity.fb_id);
                                        intent.putExtra("guide_id", guide_id);
                                        intent.putExtra("name", MainActivity.name);
                                        intent.putExtra("bday", MainActivity.bday);
                                        intent.putExtra("gender", MainActivity.gender);
                                        intent.putExtra("age", MainActivity.age);
                                        intent.putExtra("image", MainActivity.image);
                                        intent.putExtra("default", 0);
                                        intent.putExtra("user_id", user_id);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    }
                                    else{
                                        if(!guide_id.equalsIgnoreCase("deactivated")){
                                            if(!guide_id.equalsIgnoreCase("pending")){
                                                getGuideById(Constants.getGuideById + guide_id, guide_id, "MainActivity");
                                            }else {
                                                Intent intent = new Intent(context, LoggedInGuide.class);
                                                intent.putExtra("fb_id", MainActivity.fb_id);
                                                intent.putExtra("name", MainActivity.name);
                                                intent.putExtra("bday", MainActivity.bday);
                                                intent.putExtra("gender", MainActivity.gender);
                                                intent.putExtra("age", MainActivity.age);
                                                intent.putExtra("image", MainActivity.image);
                                                intent.putExtra("location", "Pending Data");
                                                intent.putExtra("contact", "Pending Data");
                                                intent.putExtra("email", "Pending Data");
                                                intent.putExtra("type", "Pending Data");
                                                intent.putExtra("guide_id", guide_id);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(intent);
                                            }
                                        }else{
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setIcon(R.drawable.ic_launcher);
                                            builder.setTitle("Notice");
                                            builder.setMessage("\nYou have been deactivated as guide.\nProceed as Traveler?\n");
                                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    MainActivity.manager.logOut();
                                                }
                                            });
                                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    DBHelper db = new DBHelper(context);
                                                    db.updSetting(MainActivity.fb_id, 1, "isTraveler");
                                                    postLogin(request, url);
                                                }
                                            });
                                            builder.show();
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("POSTLOGIN", error.getMessage());
                MainActivity.manager.logOut();
                Toast.makeText(context, "Login Failed, Please try again.", Toast.LENGTH_LONG).show();
            }
        });
       mRequestQueue.add(jsonObjectRequest);
    }

    public void postGuide(JSONObject request, String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(context, LoggedInGuide.class);
                        intent.putExtra("fb_id", RegisterActivity.fb_id);
                        intent.putExtra("guide_id", RegisterActivity.guide_id);
                        intent.putExtra("name", RegisterActivity.name);
                        intent.putExtra("bday", RegisterActivity.bday);
                        intent.putExtra("gender", RegisterActivity.gender);
                        intent.putExtra("age", RegisterActivity.age);
                        intent.putExtra("image", RegisterActivity.image);
                        intent.putExtra("location", GuideAddInfoFragment.location);
                        intent.putExtra("contact", GuideAddInfoFragment.contact);
                        intent.putExtra("email", GuideAddInfoFragment.email);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("POSTGUIDE", error.getMessage());
            }
        });
       mRequestQueue.add(jsonObjectRequest);
    }

    public void getGuideById(final String url, final String guide_id, final String activity){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String contact = null, email = null, location = null, type = null;
                        try {
                            contact = response.getString("contact_number");
                            email = response.getString("email_address");
                            location = response.getString("city")+", "+
                                    response.getString("country");
                            type = response.getString("type");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(activity.equalsIgnoreCase("MainActivity")) {
                            Intent intent = new Intent(context, LoggedInGuide.class);
                            intent.putExtra("fb_id", MainActivity.fb_id);
                            intent.putExtra("name", MainActivity.name);
                            intent.putExtra("bday", MainActivity.bday);
                            intent.putExtra("gender", MainActivity.gender);
                            intent.putExtra("age", MainActivity.age);
                            intent.putExtra("image", MainActivity.image);
                            intent.putExtra("location", location);
                            intent.putExtra("contact", contact);
                            intent.putExtra("email", email);
                            intent.putExtra("type", type);
                            intent.putExtra("guide_id", guide_id);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            //((Activity)context).finish();
                        }
                        else if(activity.equalsIgnoreCase("RegisterActivity")){
                            Intent intent = new Intent(context, LoggedInGuide.class);
                            intent.putExtra("fb_id", RegisterActivity.fb_id);
                            intent.putExtra("name", RegisterActivity.name);
                            intent.putExtra("bday", RegisterActivity.bday);
                            intent.putExtra("gender", RegisterActivity.gender);
                            intent.putExtra("age", RegisterActivity.age);
                            intent.putExtra("image", RegisterActivity.image);
                            intent.putExtra("location", location);
                            intent.putExtra("contact", contact);
                            intent.putExtra("email", email);
                            intent.putExtra("type", type);
                            intent.putExtra("guide_id", RegisterActivity.guide_id);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        else if(activity.equalsIgnoreCase("GuideProfile")){
                            try {
                                LoggedInGuide.guide_id = guide_id;
                                LoggedInGuide.name = response.getJSONObject("user").getString("name");
                                LoggedInGuide.age = response.getJSONObject("user").getString("age");
                                LoggedInGuide.location = response.getString("city")+", "+response.getString("country");
                                LoggedInGuide.email = email;
                                LoggedInGuide.contact = contact;
                                LoggedInGuide.image = response.getJSONObject("user").getString("profImage");


                                Bundle bundle = new Bundle();
                                bundle.putDouble("rating", response.getDouble("rating"));
                                bundle.putString("type", response.getString("type"));
                                GuideProfileFragment gpf = new GuideProfileFragment();
                                gpf.setArguments(bundle);

                                try {
                                    try{ LoggedInGuide.fm.popBackStackImmediate();}
                                    catch(Exception e){}

                                    FragmentTransaction ft = LoggedInGuide.fm.beginTransaction();
                                    ft.replace(R.id.drawer_fragment_container, gpf).commit();
                                }catch(Exception e){
                                    FragmentTransaction ft = LoggedInTraveler.fm.beginTransaction();
                                    ft.replace(R.id.drawer_fragment_container, gpf).addToBackStack(null).commit();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETGUIDEBYID", error.getMessage());
                if(new ConnectionChecker(context).isConnectedToInternet()){
                    getGuideById(url, guide_id, activity);
                }else{
                    Bundle bundle = new Bundle();

                    if(activity.equalsIgnoreCase("GuideProfile")) bundle.putInt("id", 1);
                    else bundle.putInt("id", 0);

                    NoConnectionFragment ncf = new NoConnectionFragment();
                    ncf.setArguments(bundle);

                    if(context instanceof LoggedInGuide) {
                        LoggedInGuide.ft = LoggedInGuide.fm.beginTransaction();
                        LoggedInGuide.ft.replace(R.id.drawer_fragment_container, ncf).addToBackStack(null).commit();
                    }else if(context instanceof LoggedInTraveler){
                        LoggedInTraveler.ft = LoggedInTraveler.fm.beginTransaction();
                        LoggedInTraveler.ft.replace(R.id.drawer_fragment_container, ncf).addToBackStack(null).commit();
                    }
                }
            }
        });
       mRequestQueue.add(jsonObjectRequest);
    }

    public void getAllLocations(final String url, final String activity){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if(activity.equalsIgnoreCase("GuideAddInfoFragment")) {
                                GuideAddInfoFragment.location_list = new String[response.length()];
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject obj = response.getJSONObject(i);
                                    GuideAddInfoFragment.location_list[i] = obj.getString("city") + ", " + obj.getString("country");
                                }
                            }else if(activity.equalsIgnoreCase("FragmentNewTrip")){
                                FragmentNewTrip.location_list = new String[response.length()];
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject obj = response.getJSONObject(i);
                                    FragmentNewTrip.location_list[i] = obj.getString("city") + ", " + obj.getString("country");
                                }
                            }
                        }catch (JSONException e) {
                            Toast.makeText(context, "Error Getting Locations", Toast.LENGTH_SHORT).show();
                        }

                        if(activity.equalsIgnoreCase("GuideAddInfoFragment")) {
                            GuideAddInfoFragment.adapter = new ArrayAdapter<String>(context,
                                    R.layout.spinner_item, GuideAddInfoFragment.location_list);

                            GuideAddInfoFragment.spnrLocation.setAdapter(GuideAddInfoFragment.adapter);
                        }else if(activity.equalsIgnoreCase("FragmentNewTrip")){
                            FragmentNewTrip.adapter = new ArrayAdapter<String>(context,
                                    R.layout.spinner_item, FragmentNewTrip.location_list);

                            FragmentNewTrip.spnrLocation.setAdapter(FragmentNewTrip.adapter);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETALLLOCATION", error.getMessage());
                getAllLocations(url, activity);
            }
        });
       mRequestQueue.add(jsonArrayRequest);
    }

    public void getImageUrl(String url, final String activity, final int position){
        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        if(activity.equalsIgnoreCase("LoggedInTraveler")){
                            LoggedInTraveler.nav_image.setImageBitmap(bitmap);
                            getImageUrl(MainActivity.cover, "LoggedInTravelerCover", 0);
                        }
                        else if(activity.equalsIgnoreCase("LoggedInGuide")){
                            LoggedInGuide.nav_image.setImageBitmap(bitmap);
                            getImageUrl(MainActivity.cover, "LoggedInGuideCover", 0);
                        }
                        else if(activity.equalsIgnoreCase("HomeImages")){
                            //RVadapter.iv.get(position).setImageBitmap(bitmap);
                            if(position == HomeFragment.llm.findLastCompletelyVisibleItemPosition() ||
                                    position == size) HomeFragment.pd.dismiss();
                        }
                        else if(activity.equalsIgnoreCase("FragmentBookingRequest")) FragmentBookingRequest.iv.setImageBitmap(bitmap);
                        else if(activity.equalsIgnoreCase("AcceptBooking")){
                            //RVadapter.pending_image.get(position).setImageBitmap(bitmap);
                            if(position == HomeFragment.llm.findLastCompletelyVisibleItemPosition() ||
                                    position == size) PendingFragment.pd.dismiss();
                        }
                        else if(activity.equalsIgnoreCase("GuideProfile")) GuideProfileFragment.profImage.setImageBitmap(bitmap);
                        else if(activity.equalsIgnoreCase("TravelerProfile")) TravelerProfileFragment.profImage.setImageBitmap(bitmap);
                        else if(activity.equalsIgnoreCase("LoggedInGuideCover")){
                            BitmapDrawable background = new BitmapDrawable(bitmap);
                            LoggedInGuide.nav_cover.setBackgroundDrawable(background);
                        }
                        else if(activity.equalsIgnoreCase("LoggedInTravelerCover")){
                            BitmapDrawable background = new BitmapDrawable(bitmap);
                            LoggedInTraveler.nav_cover.setBackgroundDrawable(background);
                        }
                        else if(activity.equalsIgnoreCase("GuideProfileCover")){
                            BitmapDrawable background = new BitmapDrawable(bitmap);
                            GuideProfileFragment.guide_profile_cover.setBackgroundDrawable(background);
                        }
                        else if(activity.equalsIgnoreCase("TravelerProfileCover")){
                            BitmapDrawable background = new BitmapDrawable(bitmap);
                            TravelerProfileFragment.traveler_profile_cover.setBackgroundDrawable(background);
                        }else if(activity.equalsIgnoreCase("MessagesCircle")){
                            RVadapter.cvh.message_image.setImageBitmap(bitmap);
                        }

                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("GETIMAGEURL", error.getMessage());
                    }
                });
       mRequestQueue.add(imageRequest);
    }

    public void getReviewsByGuideId(final String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = 0; i < response.length(); i++){
                            try {
                                JSONObject req = response.getJSONObject(i);

                                String image = req.getJSONObject("user").getString("profImage");
                                String name = req.getJSONObject("user").getString("name");
                                int rating = req.getInt("rating");
                                String review = req.getString("review");

                                GuideProfileFragment.mList.add(new review(image, name, review, rating));

                                if(i == response.length()-1){
                                    GuideProfileFragment.adapter = new RVadapter(context, null, null, null, null,
                                            GuideProfileFragment.mList);
                                    GuideProfileFragment.rv.setAdapter(GuideProfileFragment.adapter);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETREVIEWBYGUIDEID", error.getMessage());
                if(new ConnectionChecker(context).isConnectedToInternet()){
                    getReviewsByGuideId(url);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", 1);

                    NoConnectionFragment ncf = new NoConnectionFragment();
                    ncf.setArguments(bundle);

                    if(context instanceof LoggedInGuide) {
                        LoggedInGuide.ft = LoggedInGuide.fm.beginTransaction();
                        LoggedInGuide.ft.replace(R.id.drawer_fragment_container, ncf).addToBackStack(null).commit();
                    }else if(context instanceof LoggedInTraveler){
                        LoggedInTraveler.ft = LoggedInTraveler.fm.beginTransaction();
                        LoggedInTraveler.ft.replace(R.id.drawer_fragment_container, ncf).addToBackStack(null).commit();
                    }
                }
            }
        });
        mRequestQueue.add(jsonArrayRequest);
    }

    public void shareAlbum(final JSONObject request, String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (ShareDialog.canShow(ShareLinkContent.class)) {
                                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                        .setContentTitle(response.getString("album_name"))
                                        .setContentDescription(response.getString("description"))
                                        .setContentUrl(Uri.parse("http://guia.herokuapp.com/album?id="+response.getString("_id")))
                                        .setImageUrl(Uri.parse(response.getJSONArray("images").get(0).toString()))
                                        .build();

                                ShareFragment.shareDialog.show(linkContent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("SHAREALBUM", error.getMessage());
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void postRateReview(final JSONObject request, String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(context, "Successfully created a review!", Toast.LENGTH_SHORT).show();
                            MainActivity.points += request.getDouble("points");
                            TripFragment tf = new TripFragment();
                            LoggedInTraveler.ft = LoggedInTraveler.fm.beginTransaction();
                            LoggedInTraveler.ft.replace(R.id.drawer_fragment_container, tf).commit();

                            JSONObject req = new JSONObject();
                            req.accumulate("trip_user_id", MainActivity.user_id);
                            req.accumulate("location", response.getJSONObject("tour").getString("tour_location"));
                            req.accumulate("date_from", response.getString("start_date"));
                            req.accumulate("date_to", response.getString("end_date"));

                            JSONParser.getInstance(context).postTrip(req, Constants.postTrip);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("POSTRATEREVIEW", error.getMessage());

            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void getPreferences(final String url, final String activity){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        if(activity.equalsIgnoreCase("GuideAddInfo")) {
                            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(0,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            params2.weight = 1;

                            LinearLayout ll = new LinearLayout(context);
                            int count = 0;
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    if (response.getJSONObject(i).getBoolean("isActivated")) {
                                        CheckBox cb = new CheckBox(context);
                                        cb.setId(i + 1);
                                        cb.setText(response.getJSONObject(i).getString("preference"));
                                        cb.setTextColor(Color.BLACK);
                                        cb.setLayoutParams(params2);

                                        GuideAddInfoFragment.cbs.add(cb);

                                        ll.addView(cb);
                                        count++;
                                        if (count == 2) {
                                            GuideAddInfoFragment.linearLayout.addView(ll);
                                            ll = new LinearLayout(context);
                                            ll.setOrientation(LinearLayout.HORIZONTAL);
                                            ll.setLayoutParams(params);
                                            count = 0;
                                        } else {
                                            if (i == response.length() - 1) {
                                                GuideAddInfoFragment.linearLayout.addView(ll);
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }else if(activity.equalsIgnoreCase("TravelerFilter")){
                            String updInt="";
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    if (response.getJSONObject(i).getBoolean("isActivated")) {
                                        CheckBox cb = new CheckBox(context);
                                        cb.setId(i + 1);
                                        cb.setText(response.getJSONObject(i).getString("preference"));
                                        cb.setTextColor(Color.BLACK);
                                        cb.setLayoutParams(params);

                                        updInt += cb.getText().toString()+"/";
                                        FilterFragment.linearLayout.addView(cb);
                                        FilterFragment.cbs.add(cb);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if(i == response.length()-1){
                                    final DBHelper db = new DBHelper(context);
                                    Cursor c = db.getFilter();
                                    if(c.moveToFirst()){
                                        String interest = c.getString(c.getColumnIndex("interest"));
                                        if(interest.equals("13")){
                                            db.updFilterInterest(updInt);
                                        }

                                        FilterFragment.pd.dismiss();
                                        for(int j = 0; j<FilterFragment.cbs.size(); j++){
                                            Cursor cur = db.getFilter();
                                            if(cur.moveToFirst()) interest = cur.getString(cur.getColumnIndex("interest"));
                                            cur.close();

                                            if(interest.contains(FilterFragment.cbs.get(j).getText().toString()))
                                                FilterFragment.cbs.get(j).setChecked(true);

                                            FilterFragment.cbs.get(j).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    String updInt = "";
                                                    for(int k = 0; k < FilterFragment.cbs.size(); k++){
                                                        if(FilterFragment.cbs.get(k).isChecked()){
                                                            updInt += FilterFragment.cbs.get(k).getText().toString()+"/";
                                                        }

                                                        if(k == FilterFragment.cbs.size()-1) db.updFilterInterest(updInt);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETPREFERENCES", error.getMessage());
                getPreferences(url, activity);
            }
        });
        mRequestQueue.add(jsonArrayRequest);
    }

    public void addNote(final JSONObject request, final String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.e("Accept Booking", response.toString());
                        try {
                            String id, title, details, date;

                            id = response.getString("_id");
                            title = response.getString("title");
                            details = response.getString("note_content");
                            date = response.getString("note_date");

                            GuideProfileFragment.notes.add(new Note(id, title, details, date));
                            Toast.makeText(context, "Note Added!", Toast.LENGTH_SHORT).show();

                            GuideCalendarFragment.refreshNote(new CalendarDay(GuideCalendarFragment
                                    .formatter.parse(GuideCalendarFragment.date)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("ADDNOTE", error.getMessage());
                if(new ConnectionChecker(context).isConnectedToInternet()){
                    addNote(request, url);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", 0);

                    NoConnectionFragment ncf = new NoConnectionFragment();
                    ncf.setArguments(bundle);

                    LoggedInGuide.ft = LoggedInGuide.fm.beginTransaction();
                    LoggedInGuide.ft.replace(R.id.drawer_fragment_container, ncf).addToBackStack(null).commit();
                }
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void updateNote(final JSONObject request, final String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.e("Accept Booking", response.toString());
                        try {
                            String id, title, details, date;

                            id = response.getString("_id");
                            title = request.getString("title");
                            details = request.getString("note_content");
                            date = request.getString("note_date");

                            GuideCalendarFragment.deleteNote(id);
                            GuideProfileFragment.notes.add(new Note(id, title, details, date));
                            Toast.makeText(context, "Note Updated!", Toast.LENGTH_SHORT).show();

                            GuideCalendarFragment.refreshNote(new CalendarDay(GuideCalendarFragment
                                    .formatter.parse(GuideCalendarFragment.date)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("UPDATENOTE", error.getMessage());
                if(new ConnectionChecker(context).isConnectedToInternet()){
                    updateNote(request, url);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", 0);

                    NoConnectionFragment ncf = new NoConnectionFragment();
                    ncf.setArguments(bundle);

                    LoggedInGuide.ft = LoggedInGuide.fm.beginTransaction();
                    LoggedInGuide.ft.replace(R.id.drawer_fragment_container, ncf).addToBackStack(null).commit();
                }
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void deleteNote(final String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String id;
                            id = response.getString("_id");
                            GuideCalendarFragment.deleteNote(id);

                            Toast.makeText(context, "Note deleted!", Toast.LENGTH_SHORT).show();

                            GuideCalendarFragment.refreshNote(new CalendarDay(GuideCalendarFragment
                                    .formatter.parse(GuideCalendarFragment.date)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("UPDATENOTE", error.getMessage());
                if(new ConnectionChecker(context).isConnectedToInternet()){
                    deleteNote(url);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", 0);

                    NoConnectionFragment ncf = new NoConnectionFragment();
                    ncf.setArguments(bundle);

                    LoggedInGuide.ft = LoggedInGuide.fm.beginTransaction();
                    LoggedInGuide.ft.replace(R.id.drawer_fragment_container, ncf).addToBackStack(null).commit();
                }
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void getNotesByGuideId(final String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = 0; i < response.length(); i++){
                            try {
                                JSONObject req = response.getJSONObject(i);

                                String id = req.getString("_id");
                                String title = req.getString("title");
                                String detail = req.getString("note_content");
                                String date = req.getString("note_date");

                                GuideProfileFragment.notes.add(new Note(id, title, detail, date));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETNOTE", error.getMessage());
                if(new ConnectionChecker(context).isConnectedToInternet()){
                    getNotesByGuideId(url);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", 0);

                    NoConnectionFragment ncf = new NoConnectionFragment();
                    ncf.setArguments(bundle);

                    LoggedInGuide.ft = LoggedInGuide.fm.beginTransaction();
                    LoggedInGuide.ft.replace(R.id.drawer_fragment_container, ncf).addToBackStack(null).commit();
                }
            }
        });
        mRequestQueue.add(jsonArrayRequest);
    }

    public void getTripsById(final String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, "",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = 0; i < response.length(); i++){
                            try {
                                JSONObject req = response.getJSONObject(i);

                                String id = req.getString("_id");
                                String location = req.getString("location");
                                String start_date = req.getString("date_from");
                                String end_date = req.getString("date_to");
                                String image = req.getString("image");
                                String description = req.getString("description");

                                TripListFragment.mList.add(new Trip(id, location, start_date, end_date, image, description));

                                if(i == response.length()-1){
                                    TripListFragment.adapter = new LVadapter(context, TripListFragment.mList);
                                    TripListFragment.lv.setAdapter(TripListFragment.adapter);
                                    TripListFragment.pd.dismiss();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if(response.length() == 0) TripListFragment.pd.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("GETTRIPS", error.getMessage());
                if(new ConnectionChecker(context).isConnectedToInternet()){
                    getTripsById(url);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", 0);

                    NoConnectionFragment ncf = new NoConnectionFragment();
                    ncf.setArguments(bundle);

                    LoggedInTraveler.ft = LoggedInGuide.fm.beginTransaction();
                    LoggedInTraveler.ft.replace(R.id.drawer_fragment_container, ncf).addToBackStack(null).commit();
                }
            }
        });
        mRequestQueue.add(jsonArrayRequest);
    }

    public void postTrip(final JSONObject request, final String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("asdas", "New Trip Added!");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("POSTTRIP", error.getMessage());
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void updateTrip(final JSONObject request, final String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Trip Updated!", Toast.LENGTH_SHORT).show();
                        LoggedInTraveler.fm.popBackStackImmediate();
                        UpdateTripFragment.pd.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("POSTTRIP", error.getMessage());
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }
}