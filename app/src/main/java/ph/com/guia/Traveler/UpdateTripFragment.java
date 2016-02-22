package ph.com.guia.Traveler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.Utils;
import com.cloudinary.utils.ObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import ph.com.guia.Helper.JSONParser;
import ph.com.guia.Helper.LVadapter;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.Trip;
import ph.com.guia.Navigation.TripListFragment;
import ph.com.guia.R;

public class UpdateTripFragment extends Fragment {

    public static ProgressDialog pd;
    EditText title, description;
    LinearLayout image_holder;
    ImageView iv;
    Button btnOk;
    Uri image;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final int position = getArguments().getInt("position");
        View view = inflater.inflate(R.layout.create_tour, container, false);

        title = (EditText) view.findViewById(R.id.itinerary_title);
        description = (EditText) view.findViewById(R.id.itinerary_desc);
        image_holder = (LinearLayout) view.findViewById(R.id.image_holder);
        iv = (ImageView) view.findViewById(R.id.itinerary_image);
        btnOk = (Button) view.findViewById(R.id.itinerary_btnNext);

        title.setVisibility(View.GONE);
        image_holder.setVisibility(View.GONE);
        description.setText(TripListFragment.mList.get(position).description);
        btnOk.setText("Update");

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = description.getText().toString().trim();

                if (desc.isEmpty()) {
                    description.setError("Required!");
                    description.requestFocus();
                } else {
                    pd = ProgressDialog.show(getContext(), "Loading", "Please wait...", true, true);
                    try {
                        JSONObject request = new JSONObject();
                        request.accumulate("_id", TripListFragment.mList.get(position).id);
                        request.accumulate("description", desc);
                        Trip trip = TripListFragment.mList.get(position);
                        if(image != null){
                            Cloudinary cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(getActivity().getApplicationContext()));
                            File file = new File(getRealPathFromURI(image));
                            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                            String imageUrl = uploadResult.get("url").toString();

                            request.accumulate("image", imageUrl);

                            TripListFragment.mList.set(position, new Trip(trip.id, trip.location,
                                    trip.start_date, trip.end_date, imageUrl, desc));
                        }else{
                            TripListFragment.mList.set(position, new Trip(trip.id, trip.location,
                                    trip.start_date, trip.end_date, trip.image, desc));
                        }

                        TripListFragment.adapter = new LVadapter(getContext(), TripListFragment.mList);
                        TripListFragment.lv.setAdapter(TripListFragment.adapter);

                        JSONParser.getInstance(getContext()).updateTrip(request, Constants.updateTrip);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            image = data.getData();
            iv.setImageURI(image);
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
}
