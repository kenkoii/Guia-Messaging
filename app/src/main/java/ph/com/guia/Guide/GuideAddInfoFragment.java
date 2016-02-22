package ph.com.guia.Guide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

import ph.com.guia.Helper.DBHelper;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.R;
import ph.com.guia.RegisterActivity;

public class GuideAddInfoFragment extends Fragment {
    public static Spinner spnrLocation;
    public static String location, contact, email;
    public static String[] location_list;
    public static ArrayAdapter<String> adapter;
    public static LinearLayout linearLayout;
    public static ArrayList<CheckBox> cbs = new ArrayList<CheckBox>();
    EditText txtContact, txtEmail;
    Button btnNext, btnBack;
    FragmentTransaction ft;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_registration, container, false);

        spnrLocation = (Spinner) view.findViewById(R.id.spnrLocation);
        txtContact = (EditText) view.findViewById(R.id.txtContact);
        txtEmail = (EditText) view.findViewById(R.id.txtEmail);
        btnNext = (Button) view.findViewById(R.id.guide1_next);
        btnBack = (Button) view.findViewById(R.id.guide1_back);
        linearLayout = (LinearLayout) view.findViewById(R.id.preferences_holder);

        cbs.clear();
        JSONParser parser = new JSONParser(getActivity().getApplicationContext());
        parser.getAllLocations(Constants.getAllLocations, "GuideAddInfoFragment");
        parser.getPreferences(Constants.getPreferences, "GuideAddInfo");

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = spnrLocation.getSelectedItem().toString();
                contact = txtContact.getText().toString();
                email = txtEmail.getText().toString();
                String type = "";
                Boolean ok = false;

                StringTokenizer st = new StringTokenizer(location, ", ");
                String city = st.nextToken();
                String country = st.nextToken();

                for (int i = 0; i < cbs.size(); i++) {
                    if (cbs.get(i).isChecked()) {
                        if (!type.equals("")) type += "/";
                        type += cbs.get(i).getText().toString();
                        ok = true;
                    }
                }

                if (contact.equals("")) txtContact.setError("Required!");
                else if (email.equals("")) txtEmail.setError("Required!");
                else if(!ok) Toast.makeText(getContext(), "Must select atleast 1 specialty", Toast.LENGTH_SHORT).show();
                else {
                    DBHelper db = new DBHelper(getActivity().getApplicationContext());
                    db.updSetting(RegisterActivity.fb_id, 0, "isTraveler");

                    JSONObject request = new JSONObject();
                    try {
                        request.accumulate("city", "Cebu");
                        request.accumulate("country", "Philippines");
                        request.accumulate("contact_number", contact);
                        request.accumulate("email_address", email);
                        request.accumulate("type", type);
                        request.accumulate("profImage", RegisterActivity.image);
                        request.accumulate("guide_user_id", RegisterActivity.user_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONParser parser = new JSONParser(getActivity().getApplicationContext());
                    parser.postGuide(request, Constants.postGuide);

                    getActivity().finish();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RegisterActivity.def == 0) {
                    MainActivity.manager.logOut();
                    getActivity().finish();
                } else getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        return view;
    }
}
