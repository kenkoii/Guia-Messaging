package ph.com.guia.Traveler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.Tours;
import ph.com.guia.R;

public class FragmentBookingRequest extends Fragment implements DatePickerDialog.OnDateSetListener {

    public static ImageView iv;
    private static final String CONFIG_CLIENT_ID = "AdSWgpp_bt-NLQMoZBDquci7RxnqGAHAyww92qH2NBgrzYR6uVZj3AOdEEeg50B4IybD0y0wICbirfj6";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static PayPalConfiguration config;
    static boolean startClicked = false;
    TextView start_date, end_date;
    Calendar now = Calendar.getInstance();
    Calendar start_calendar;
    String start, end;

    Tours tour;
    TextView title, description, rate, points, duration, guide;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        config = new PayPalConfiguration()
                .environment(CONFIG_ENVIRONMENT)
                .clientId(CONFIG_CLIENT_ID);
        tour = getArguments().getParcelable("tour");

        Intent intent = new Intent(getContext(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getContext().startService(intent);

        View view = inflater.inflate(R.layout.fragment_trip_detials, container, false);
        iv = (ImageView) view.findViewById(R.id.detail_main_image);
        title = (TextView) view.findViewById(R.id.detail_title);
        description = (TextView) view.findViewById(R.id.detail_description);
        rate = (TextView) view.findViewById(R.id.detail_rate);
        points = (TextView) view.findViewById(R.id.detail_points);
        duration = (TextView) view.findViewById(R.id.detail_duration);
        guide = (TextView) view.findViewById(R.id.detail_guide);

        JSONParser parser = new JSONParser(getActivity().getApplicationContext());
        parser.getImageUrl(tour.main_image, "FragmentBookingRequest", 0);

        title.setText(tour.tour_name);
        description.setText(tour.tour_description);
        rate.setText("Rate: "+tour.tour_rate);
        points.setText("Points Reward: "+tour.points);
        duration.setText("Duration: "+tour.tour_duration+" "+tour.duration_format);
        guide.setText(tour.guide_name);

        Button btnBook = (Button) view.findViewById(R.id.detail_book);

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tour.activity.equalsIgnoreCase("HomeFragment")){
                    View view2 = inflater.inflate(R.layout.date_dialog, null, false);
                    start_date = (TextView) view2.findViewById(R.id.dd_startDate);
                    end_date = (TextView) view2.findViewById(R.id.dd_endDate);


                    start_date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startClicked = true;
                            DatePickerDialog dpd = DatePickerDialog.newInstance(
                                    FragmentBookingRequest.this,
                                    now.get(Calendar.YEAR),
                                    now.get(Calendar.MONTH),
                                    now.get(Calendar.DAY_OF_MONTH)
                            );
                            dpd.setMinDate(now);
                            dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                        }
                    });

                    end_date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startClicked = false;
                            if(start_calendar != null) {
                                DatePickerDialog dpd = DatePickerDialog.newInstance(
                                        FragmentBookingRequest.this,
                                        start_calendar.get(Calendar.YEAR),
                                        start_calendar.get(Calendar.MONTH),
                                        start_calendar.get(Calendar.DAY_OF_MONTH)
                                );
                                dpd.setMinDate(start_calendar);
                                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                            }else Toast.makeText(getContext(), "Please pick starting date first", Toast.LENGTH_SHORT).show();
                        }
                    });

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setIcon(R.drawable.ic_launcher);
                    builder.setView(view2);
                    builder.setTitle("Book Schedule");
                    builder.setNegativeButton("Back", null);
                    builder.setPositiveButton("Done", null);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            start = start_date.getText().toString();
                            end = end_date.getText().toString();

                            if (start.equalsIgnoreCase("Pick date"))
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Please pick starting date", Toast.LENGTH_SHORT).show();
                            else if (end.equalsIgnoreCase("Pick date"))
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Please pick ending date", Toast.LENGTH_SHORT).show();
                            else {
                                alertDialog.dismiss();
                                showDialog();
                            }
                        }
                    });
                }else {
                    showDialog();
                }
            }
        });

        guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONParser.getInstance(getContext()).getGuideById(Constants.getGuideById + tour.tour_guideId,
                        tour.tour_guideId, "GuideProfile");
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        System.out.println(confirm.toJSONObject().toString(4));
                        System.out.println(confirm.getPayment().toJSONObject()
                                .toString(4));

                        JSONObject request = new JSONObject();
                        try {
                            request.accumulate("booking_guide_id", tour.tour_guideId);
                            request.accumulate("booking_tour_id", tour.tour_id);
                            request.accumulate("booking_user_id", LoggedInTraveler.user_id);
                            if(tour.activity.equalsIgnoreCase("HomeFragment")) {
                                request.accumulate("start_date", start);
                                request.accumulate("end_date", end);
                            }else{
                                request.accumulate("start_date", FragmentTripBooking.start);
                                request.accumulate("end_date", FragmentTripBooking.end);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONParser parser = new JSONParser(getActivity().getApplicationContext());
                        parser.requestBooking(request, Constants.requestBooking);
                        //JSONObject obj = parser.makeHttpRequest("http://guia.herokuapp.com/api/v1/book", "POST", params);
                        //Toast.makeText(getActivity().getApplicationContext(), "CLicked!"+LoggedInTraveler.user_id, Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity().getApplicationContext(), "Successfully Booked!",
                                Toast.LENGTH_LONG).show();
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                System.out.println("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    public void showDialog(){
        final double rate;
        String charge = "50PHP";
        if(tour.tour_rate <= 500) rate = 50;
        else{
            rate = tour.tour_rate * 0.1;
            charge = "10%";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Notice");
        builder.setMessage("\nTour price: " + tour.tour_rate + "\nService Charge: " + charge +
                "\nEstimated Tour Expense: " + (tour.tour_rate + rate) +
                "\n\nCharge upon booking: " + rate + "\n");
        builder.setNegativeButton("Back", null);
        builder.setPositiveButton("Book", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PayPalPayment ppp = new PayPalPayment(new BigDecimal(String.valueOf(rate)), "PHP",
                        tour.tour_name, PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(getContext(),
                        PaymentActivity.class);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, ppp);
                startActivityForResult(intent, 1);
            }
        });
        builder.show();
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        if(startClicked){
            start_date.setText((month+1)+"/"+day+"/"+year);
            start_calendar = new GregorianCalendar(year, month, day);
        } else end_date.setText((month+1)+"/"+day+"/"+year);
    }
}
