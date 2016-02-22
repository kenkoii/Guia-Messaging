package ph.com.guia.Navigation;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;

import ph.com.guia.Helper.DBHelper;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Model.Constants;
import ph.com.guia.R;

public class FilterFragment extends Fragment{

    public static LinearLayout linearLayout;
    public static ArrayList<CheckBox> cbs = new ArrayList<CheckBox>();
    public static ProgressDialog pd;
    RangeSeekBar rsb;
    RadioGroup rg;
    RadioButton rb_both, rb_male, rb_female;
    DBHelper db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        cbs.clear();
        pd = ProgressDialog.show(this.getContext(), "Loading", "Please wait...", true, true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.done, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        db = new DBHelper(getActivity().getApplicationContext());

        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        rg = (RadioGroup) view.findViewById(R.id.rg_gender);
        rb_both = (RadioButton) view.findViewById(R.id.rb_both);
        rb_male = (RadioButton) view.findViewById(R.id.rb_male);
        rb_female = (RadioButton) view.findViewById(R.id.rb_female);
        linearLayout = (LinearLayout) view.findViewById(R.id.filter_cbs);
        rsb = (RangeSeekBar) view.findViewById(R.id.range_seekbar);

        rsb.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                rsb.setSelectedMaxValue((Number) maxValue);
                rsb.setSelectedMinValue((Number) minValue);
            }
        });

        JSONParser parser = new JSONParser(getActivity().getApplicationContext());
        parser.getPreferences(Constants.getPreferences, "TravelerFilter");

        Cursor c = db.getFilter();
        if (c.moveToFirst()) {
            String gender = c.getString(c.getColumnIndex("gender"));

            if (gender.equals("BOTH")) rb_both.setChecked(true);
            else if (gender.equals("MALE")) rb_male.setChecked(true);
            else rb_female.setChecked(true);


        }


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_both:
                        db.updFilterGender("BOTH");
                        break;
                    case R.id.rb_male:
                        db.updFilterGender("MALE");
                        break;
                    case R.id.rb_female:
                        db.updFilterGender("FEMALE");
                        break;
                }
            }
        });

        return view;
    }
}
