package ph.com.guia.Navigation;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.Utils;
import com.cloudinary.utils.ObjectUtils;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import ph.com.guia.Helper.ConnectionChecker;
import ph.com.guia.Helper.JSONParser;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.R;

public class ShareFragment extends Fragment{

    EditText album_name, album_desc;
    ImageView add_photo;
    LinearLayout linearLayout;
    ArrayList<String> photos = new ArrayList<String>();
    ShareButton shareButton;
    int id = 0;

    public static ShareDialog shareDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share_album, container, false);

        shareDialog = new ShareDialog(this);

        album_name = (EditText) view.findViewById(R.id.share_album_name);
        album_desc = (EditText) view.findViewById(R.id.share_album_desc);
        add_photo = (ImageView) view.findViewById(R.id.add_photo);
        linearLayout = (LinearLayout) view.findViewById(R.id.share_photos);
        shareButton = (ShareButton) view.findViewById(R.id.share_button);

        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = album_name.getText().toString();
                String desc = album_desc.getText().toString();

                if(name.equals("") || name.length() > 15) album_name.setError("Required! Must be less than 15 letters");
                else if(desc.equals("") || desc.length() > 300) album_desc.setError("Required! Must be less than 300 letters");
                else{
                    JSONObject request = new JSONObject();
                    try {
                        JSONObject user = new JSONObject();
                        user.accumulate("id", MainActivity.user_id);
                        user.accumulate("facebook_id", MainActivity.fb_id);
                        user.accumulate("name", MainActivity.name);
                        user.accumulate("profImage", MainActivity.image);

                        JSONArray images = new JSONArray();
                        for(int i = 0; i < photos.size(); i++){
                            images.put(photos.get(i));
                        }

                        request.accumulate("album_name", name);
                        request.accumulate("description", desc);
                        request.accumulate("images", images);
                        request.accumulate("album_tour_id", "5680059797ee9f1100e6ff25");
                        request.accumulate("user", user);

                        JSONParser.getInstance(getActivity().getApplicationContext()).shareAlbum(request, Constants.shareAlbum);
                    } catch (JSONException e) {
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

        try{
            Uri imgUri = data.getData();

            switch(requestCode) {
                case 1:
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
                    params.setMargins(5, 10, 5, 10);
                    ImageView iv = new ImageView(getActivity().getApplicationContext());
                    iv.setId(id++);
                    iv.setImageURI(imgUri);
                    iv.setLayoutParams(params);
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setFlags(id-1);
                            startActivityForResult(intent, 2);
                        }
                    });
                    linearLayout.addView(iv);

                    if (new ConnectionChecker(getActivity().getApplicationContext()).isConnectedToInternet()) {
                        Cloudinary cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(getActivity().getApplicationContext()));
                        File file = new File(getRealPathFromURI(imgUri));
                        Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                        photos.add(uploadResult.get("url").toString());
                    }
                    break;
                case 2:
                    int position = data.getFlags();
                    ((ImageView) linearLayout.findViewById(position)).setImageURI(imgUri);
                    break;
            }
        }catch (Exception e){}
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
