package ph.com.guia.Guide;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.Utils;
import com.cloudinary.utils.ObjectUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import ph.com.guia.Helper.ConnectionChecker;
import ph.com.guia.Helper.EventDecorator;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.Note;
import ph.com.guia.R;
import ph.com.guia.Traveler.LoggedInTraveler;

public class GuideCalendarFragment extends Fragment {
    public static DateFormat formatter = SimpleDateFormat.getDateInstance();
    public static MaterialCalendarView calendar;
    public static TextView sched_details, sched_title;
    public static ImageView add_note, edit_note, delete_note;
    public static EditText title, detail;
    public static LinearLayout note_edit_delete;
    public static String date;
    public static Note note;
    static Context context;
    static View view2;
    static LayoutInflater inflater;

   @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar, container, false);

        this.inflater = inflater;
        calendar = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        sched_title = (TextView) view.findViewById(R.id.sched_title);
        sched_details = (TextView) view.findViewById(R.id.sched_details);
        add_note = (ImageView) view.findViewById(R.id.add_note);
        edit_note = (ImageView) view.findViewById(R.id.note_edit);
        delete_note = (ImageView) view.findViewById(R.id.note_delete);
        note_edit_delete = (LinearLayout) view.findViewById(R.id.note_edit_delete);

        context = getContext();
        markDates();

        calendar.setOnDateChangedListener(new OnDateChangedListener() {
            @Override
            public void onDateChanged(MaterialCalendarView materialCalendarView, @Nullable CalendarDay calendarDay) {
                //sched_details.setText(formatter.format(calendarDay.getDate()));
                refreshNote(calendarDay);
            }
        });

        add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog("Create Note", "add");
            }
        });
        return view;
    }

    public static void showAlertDialog(String dialogTitle, final String process){

        view2 = inflater.inflate(R.layout.note_dialog, null, false);
        title = (EditText) view2.findViewById(R.id.note_dialog_title);
        detail = (EditText) view2.findViewById(R.id.note_dialog_details);

        if(note != null){
            title.setText(note.note_title);
            detail.setText(note.note_detail);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setView(view2);
        builder.setTitle(dialogTitle);
        builder.setNegativeButton("Back", null);
        builder.setPositiveButton("Done", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if EditText is empty disable closing on possitive button
                if (title.getText().toString().trim().isEmpty() || title.getText().toString().length() > 15) {
                    title.requestFocus();
                    title.setError("Required! Must be less than 15 letters");
                } else if (detail.getText().toString().trim().isEmpty() || detail.getText().toString().length() > 300) {
                    detail.requestFocus();
                    detail.setError("Required! Must be less than 300 letters");
                } else {
                    if (process.equalsIgnoreCase("add")) {
                        try {
                            JSONObject request = new JSONObject();
                            request.accumulate("title", title.getText().toString());
                            request.accumulate("note_content", detail.getText().toString());
                            request.accumulate("note_date", date);
                            request.accumulate("note_guide_id", LoggedInGuide.guide_id);

                            JSONParser.getInstance(context).addNote(request, Constants.addNote);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (process.equalsIgnoreCase("edit")) {
                        try {
                            JSONObject request = new JSONObject();
                            request.accumulate("title", title.getText().toString());
                            request.accumulate("note_content", detail.getText().toString());
                            request.accumulate("note_date", date);
                            request.accumulate("note_guide_id", LoggedInGuide.guide_id);

                            JSONParser.getInstance(context).updateNote(request, Constants.updateNote + note.note_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    alertDialog.dismiss();
                }
            }
        });
    }

    public static Note getNoteByDate(String date){
        Note note = null;
        for(int i = 0; i < GuideProfileFragment.notes.size(); i++){
            if(GuideProfileFragment.notes.get(i).note_date.equals(date)){
                note = GuideProfileFragment.notes.get(i);
                break;
            }
        }
        return note;
    }

    public static void deleteNote(String id){
        for(int i = 0; i < GuideProfileFragment.notes.size(); i++){
            if(GuideProfileFragment.notes.get(i).note_id.equals(id)){
                GuideProfileFragment.notes.remove(i);
                break;
            }
        }
    }

    public static void refreshNote(CalendarDay calendarDay){
        date = String.valueOf(formatter.format(calendarDay.getDate()));

        add_note.setVisibility(View.VISIBLE);
        sched_title.setVisibility(View.VISIBLE);
        sched_details.setVisibility(View.GONE);
        note_edit_delete.setVisibility(View.GONE);

        if (GuideProfileFragment.notes.size() > 0) {
            note = getNoteByDate(date);
            if (note != null) {
                add_note.setVisibility(View.GONE);
                note_edit_delete.setVisibility(View.VISIBLE);
                sched_details.setVisibility(View.VISIBLE);

                sched_title.setText(note.note_title);
                sched_details.setText(note.note_detail);

                edit_note.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAlertDialog("Edit Note", "edit");
                    }
                });

                delete_note.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setIcon(R.drawable.ic_launcher);
                        builder.setTitle("Warning!");
                        builder.setMessage("\nAre you sure you want to delete note?\n");
                        builder.setNegativeButton("No", null);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JSONParser.getInstance(context).deleteNote(Constants.deleteNote+note.note_id);
                            }
                        });
                        builder.show();
                    }
                });
            } else {
                sched_title.setText("Nothing to do " + date);
            }
        } else {
            sched_title.setText("Nothing to do " + date);
        }

        calendar.removeDecorators();
        markDates();
    }

    public static void markDates(){
        HashSet<CalendarDay> dates = new HashSet<CalendarDay>();
        for(int i = 0; i < GuideProfileFragment.notes.size(); i++){
            try {
                dates.add(new CalendarDay(formatter.parse(GuideProfileFragment.notes.get(i).note_date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(i == GuideProfileFragment.notes.size()-1){
                calendar.addDecorator(new EventDecorator(Color.RED, dates));
            }
        }
    }
}
