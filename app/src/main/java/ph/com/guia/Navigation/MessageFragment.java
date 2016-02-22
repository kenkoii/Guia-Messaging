package ph.com.guia.Navigation;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.Helper.RVadapter;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.MessageItem;
import ph.com.guia.R;
import ph.com.guia.Traveler.LoggedInTraveler;

public class MessageFragment extends Fragment {
    //TODO: Kentoy

    ArrayList<MessageItem> mList = new ArrayList<MessageItem>();
    RVadapter adapter;
    String id = MainActivity.user_id;

    @Override
    public void onResume() {
        super.onResume();
        try {
            LoggedInGuide.mToolbar.setTitle("Messages");
        }catch (Exception e){
            LoggedInTraveler.mToolbar.setTitle("Messages");
        }
        //Fetch Conversations
        /*mList.clear();
        this.fetchConversations();*/
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            LoggedInGuide.mToolbar.setTitle("Messages");
        }catch (Exception e){
            LoggedInTraveler.mToolbar.setTitle("Messages");
        }
        //Fetch Conversations
        mList.clear();
        this.fetchConversations();
/*
        String message = "Hooyyy asa naman tawn ka ui, pagdali kay ikaw nalay gihuwat";
        String message_part;
        if(message.length()>25) message_part = message.substring(0, 24) + "...";
        else message_part = message;
        mList.clear();
        mList.add(new MessageItem(R.drawable.default_profile, "Claire Magz", message_part, message));
*/
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        adapter = new RVadapter(getActivity().getApplicationContext(),null, mList, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.cardList);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        //RVadapter adapter = new RVadapter(getActivity().getApplicationContext(),null, mList, null, null);
        rv.setAdapter(adapter);
        return view;
    }

    public void fetchConversations(){
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Constants.getConversations + id, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, final JSONArray response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Id", id+"");
                        Log.e("Response:", response.toString());
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject conversation = (JSONObject) response.get(i);
                                String id = conversation.getString("_id");
                                JSONArray messages = conversation.getJSONArray("messages");
                                JSONObject guide = conversation.getJSONObject("guide");
                                JSONObject traveler = conversation.getJSONObject("traveler");
                                String profImage;
                                String username;
                                String message;
                                String message_part;
                                if (!guide.getString("id").equals(MainActivity.user_id)) {
                                    username = guide.getString("name");
                                    profImage = guide.getString("profImage");
                                } else {
                                    username = traveler.getString("name");
                                    profImage = traveler.getString("profImage");
                                }
                                if(messages.length()>0) {
                                    JSONObject msg = (JSONObject) messages.get(messages.length() - 1);
                                    message = msg.getString("message");
                                    if (message.length() > 25)
                                        message_part = message.substring(0, 24) + "...";
                                    else message_part = message;
                                }
                                else{
                                    message = "";
                                    message_part = "";
                                }
                                mList.add(new MessageItem(id,profImage, username, message_part, message));
                                adapter.notifyItemInserted(mList.size() - 1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getActivity(), responseString, Toast.LENGTH_LONG).show();
                Log.e("Error:",responseString);
            }
        });
    }
}
