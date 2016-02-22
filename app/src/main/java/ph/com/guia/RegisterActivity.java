package ph.com.guia;


import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ph.com.guia.Guide.GuideAddInfoFragment;

public class RegisterActivity extends AppCompatActivity {
    public static String fb_id, guide_id, user_id, name, bday, gender, age, image;
    public static int def;
    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try{
            Bundle b = this.getIntent().getExtras();
            fb_id = b.getString("fb_id");
            guide_id = b.getString("guide_id");
            user_id = b.getString("user_id");
            name = b.getString("name");
            bday = b.getString("bday");
            gender = b.getString("gender");
            age = b.getString("age");
            image = b.getString("image");
            def = b.getInt("default");
        }
        catch(Exception e){}

        if(def == 1){
            RegisterFragment rf = new RegisterFragment();
            ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.new_frag_container, rf).commit();
        }
        else{
            GuideAddInfoFragment g1f = new GuideAddInfoFragment();
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.new_frag_container, g1f).addToBackStack(null).commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.manager.logOut();
        if(def == 0) this.finish();
    }
}
