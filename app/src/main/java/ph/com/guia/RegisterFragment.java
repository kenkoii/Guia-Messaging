package ph.com.guia;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ph.com.guia.Guide.GuideAddInfoFragment;
import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.Helper.DBHelper;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Model.Constants;
import ph.com.guia.Traveler.LoggedInTraveler;

public class RegisterFragment extends Fragment {
    ImageView mTraveler, mGuide;
    FragmentTransaction ft;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        mTraveler = (ImageView) view.findViewById(R.id.traveler_logo);
        mGuide = (ImageView) view.findViewById(R.id.guide_logo);

        mTraveler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper db = new DBHelper(getActivity().getApplicationContext());
                db.updSetting(RegisterActivity.fb_id, 1, "isTraveler");

                Intent intent = new Intent(getActivity().getApplicationContext(), LoggedInTraveler.class);
                intent.putExtra("fb_id", RegisterActivity.fb_id);
                intent.putExtra("name", RegisterActivity.name);
                intent.putExtra("bday", RegisterActivity.bday);
                intent.putExtra("gender", RegisterActivity.gender);
                intent.putExtra("age", RegisterActivity.age);
                intent.putExtra("image", RegisterActivity.image);
                intent.putExtra("user_id", RegisterActivity.user_id);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
        mGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RegisterActivity.guide_id.equals("")) {
                    GuideAddInfoFragment g1f = new GuideAddInfoFragment();
                    ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.new_frag_container, g1f).addToBackStack(null).commit();
                }
                else{
                    JSONParser parser = new JSONParser(getActivity().getApplicationContext());
                    parser.getGuideById(Constants.getGuideById+RegisterActivity.guide_id, RegisterActivity.guide_id, "RegisterActivity");
                }
            }
        });

        return view;
    }
}