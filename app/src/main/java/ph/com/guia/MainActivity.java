package ph.com.guia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import ph.com.guia.Helper.ConnectionChecker;
import ph.com.guia.Helper.DBHelper;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Model.Constants;
import ph.com.guia.Navigation.NoConnectionFragment;


public class MainActivity extends AppCompatActivity {

    public static LoginButton loginButton;
    public static boolean end = false;
    public static LoginManager manager;
    public static String fb_id, image, name, bday, gender, age, cover, user_id;
    public static double points;
    public static ProgressDialog pd;
    public static FragmentManager fm;
    CallbackManager callbackManager;
    DBHelper db = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        manager = LoginManager.getInstance();

        Cursor c = db.getFilter();
        if(!c.moveToFirst()) db.defaultFilter();

        loginButton = (LoginButton) findViewById(R.id.authButton);
        loginButton.setReadPermissions(Arrays.asList("user_birthday", "email", "user_location"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login attempt cancelled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(MainActivity.this, "Login attempt failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestData(AccessToken token){
        pd = ProgressDialog.show(this, "Loading", "Please wait...", true, false);
        GraphRequest request = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        //Toast.makeText(MainActivity.this, "JSON: "+object, Toast.LENGTH_LONG).show();
                        try {

                            //if(user==null) {
                            fb_id = object.getString("id");
                            cover = object.getJSONObject("cover").getString("source");
                            JSONObject pic = object.getJSONObject("picture");
                            JSONObject data = pic.getJSONObject("data");
                            //Toast.makeText(MainActivity.this, "awa ari nisud", Toast.LENGTH_LONG).show();
                            image = data.getString("url");
                            name = object.getString("name");
                            bday = object.getString("birthday");
                            gender = object.getString("gender");

                            JSONObject age_range = object.getJSONObject("age_range");

                            try {
                                age = age_range.getString("max");
                            }
                            catch (Exception e){
                                age = age_range.getString("min");
                            }

                            JSONObject request = new JSONObject();
                            request.accumulate("facebook_id", fb_id);
                            request.accumulate("name", name);
                            request.accumulate("birthday", bday);
                            request.accumulate("age", age);
                            request.accumulate("gender", gender);
                            request.accumulate("profImage", image);
                            request.accumulate("coverPhoto", cover);

                            JSONParser parser = new JSONParser(MainActivity.this);
                            parser.postLogin(request, Constants.login);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pd.dismiss();
                        //MainActivity.this.finish();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,picture,name,birthday,gender,age_range,cover");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (end) {
            end = false;
            this.finish();
        }
        else if(AccessToken.getCurrentAccessToken()!=null){
            if(new ConnectionChecker(this).isConnectedToInternet()) requestData(AccessToken.getCurrentAccessToken());
            else Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }
}
