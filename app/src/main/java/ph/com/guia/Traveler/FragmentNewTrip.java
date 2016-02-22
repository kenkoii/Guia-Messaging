package ph.com.guia.Traveler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ph.com.guia.Helper.JSONParser;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.R;

public class FragmentNewTrip extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public static Spinner spnrLocation;
    public static String[] location_list;
    public static ArrayAdapter<String> adapter;
    Button btnNext;
    TextView startDate, endDate;
    boolean startClicked = false;
    Calendar start_calendar = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_trip, container, false);

        spnrLocation = (Spinner) view.findViewById(R.id.new_travel_spnnr);
        btnNext = (Button) view.findViewById(R.id.new_travel_next);
        startDate = (TextView) view.findViewById(R.id.new_travel_startDate);
        endDate = (TextView) view.findViewById(R.id.new_travel_endDate);

        JSONParser parser = new JSONParser(getActivity().getApplicationContext());
        parser.getAllLocations(Constants.getAllLocations, "FragmentNewTrip");

        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Calendar now = Calendar.getInstance();

        switch (id){
            case R.id.new_travel_startDate:
                startClicked = true;
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMinDate(now);
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.new_travel_endDate:
                startClicked = false;
                if(start_calendar != null) {
                    dpd = DatePickerDialog.newInstance(
                            this,
                            start_calendar.get(Calendar.YEAR),
                            start_calendar.get(Calendar.MONTH),
                            start_calendar.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.setMinDate(start_calendar);
                    dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                }else Toast.makeText(getContext(), "Please pick starting date first", Toast.LENGTH_SHORT).show();
                break;
            case R.id.new_travel_next:
                String location = spnrLocation.getSelectedItem().toString();
                String start = startDate.getText().toString();
                String end = endDate.getText().toString();

                if (start.equalsIgnoreCase("Pick date")) Toast.makeText(getActivity().getApplicationContext(),
                        "Please pick starting date", Toast.LENGTH_SHORT).show();
                else if(end.equalsIgnoreCase("Pick date")) Toast.makeText(getActivity().getApplicationContext(),
                        "Please pick ending date", Toast.LENGTH_SHORT).show();
                else{
                    Bundle bundle = new Bundle();
                    bundle.putString("location", location);
                    bundle.putString("start", start);
                    bundle.putString("end", end);

                    FragmentTripBooking ftb = new FragmentTripBooking();
                    ftb.setArguments(bundle);

                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.drawer_fragment_container, ftb).addToBackStack(null).commit();
                }
                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        if(startClicked){
            startDate.setText((month+1)+"/"+day+"/"+year);
            start_calendar = new GregorianCalendar(year, month, day);
        } else endDate.setText((month+1)+"/"+day+"/"+year);
    }
}
