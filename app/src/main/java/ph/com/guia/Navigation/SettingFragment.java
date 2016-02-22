package ph.com.guia.Navigation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.Helper.DBHelper;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.R;
import ph.com.guia.Traveler.LoggedInTraveler;

public class SettingFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    Switch alert, reminder, isTraveler;
    DBHelper db = null;
    String fb_id;
    boolean ok = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            LoggedInGuide.mToolbar.setTitle("Settings");
        }catch (Exception e){
            LoggedInTraveler.mToolbar.setTitle("Settings");
        }
        db = new DBHelper(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        alert = (Switch) view.findViewById(R.id.alert_switch);
        reminder = (Switch) view.findViewById(R.id.reminder_switch);
        isTraveler = (Switch) view.findViewById(R.id.type_switch);

        try {
            if (!LoggedInGuide.fb_id.equals("")) fb_id = LoggedInGuide.fb_id;
        }catch(Exception e){
            if (!LoggedInTraveler.fb_id.equals("")) fb_id = LoggedInTraveler.fb_id;
        }

        //Toast.makeText(getActivity().getApplicationContext(), fb_id, Toast.LENGTH_LONG).show();
        Cursor c = db.getSettingById(fb_id);
        if(c.moveToFirst()){
            //Toast.makeText(getActivity().getApplication(), "Nisud man ngarii", Toast.LENGTH_LONG).show();
            if(c.getInt(c.getColumnIndex("alert")) == 0) alert.setChecked(false);
            else alert.setChecked(true);

            if(c.getInt(c.getColumnIndex("reminder")) == 0) reminder.setChecked(false);
            else reminder.setChecked(true);

            if(c.getInt(c.getColumnIndex("isTraveler")) == 0) isTraveler.setChecked(false);
            else isTraveler.setChecked(true);
        }

        alert.setOnCheckedChangeListener(this);
        reminder.setOnCheckedChangeListener(this);
        isTraveler.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();

        String column = null;

        switch (id){
            case R.id.alert_switch: column = "alert"; break;
            case R.id.reminder_switch: column = "reminder"; break;
            case R.id.type_switch: column = "isTraveler";
        }

        if(column.equals("isTraveler")){
            if(!ok) {
                if (isChecked) showDialog("Traveler");
                else showDialog("Guide");
            }else ok = false;
        }else{
            if (isChecked) db.updSetting(fb_id, 1, column);
            else db.updSetting(fb_id, 0, column);
        }
    }

    public void showDialog(final String type){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Login As");
        builder.setMessage("Are you sure you want to login as: " + type);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ok = true;
                if (type.equals("Traveler")) isTraveler.setChecked(false);
                else isTraveler.setChecked(true);

            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.manager.logOut();
                if (type.equals("Traveler")) {
                    db.updSetting(fb_id, 1, "isTraveler");
                    LoggedInGuide.mToolbar = null;
                } else db.updSetting(fb_id, 0, "isTraveler");

                try {
                    JSONObject request = new JSONObject();
                    request.accumulate("facebook_id", fb_id);
                    request.accumulate("name", MainActivity.name);
                    request.accumulate("birthday", MainActivity.bday);
                    request.accumulate("age", MainActivity.age);
                    request.accumulate("gender", MainActivity.gender);
                    request.accumulate("profImage", MainActivity.image);
                    request.accumulate("coverPhoto", MainActivity.cover);

                    JSONParser.getInstance(getContext()).postLogin(request, Constants.login);
                    getActivity().finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.show();
    }
}
