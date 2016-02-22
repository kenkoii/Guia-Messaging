package ph.com.guia.Navigation;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import ph.com.guia.Guide.LoggedInGuide;
import ph.com.guia.Helper.MessageAdapter;
import ph.com.guia.MainActivity;
import ph.com.guia.Model.Constants;
import ph.com.guia.Model.Message;
import ph.com.guia.R;

import cz.msebera.android.httpclient.Header;
import ph.com.guia.Traveler.LoggedInTraveler;

public class MessageDetailFragment extends Fragment{
    private LinearLayout mBackground;
    private RecyclerView mMessagesView;
    private EditText mInputMessageView;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView.Adapter mAdapter;
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler() {
        @Override
        public void close() {

        }

        @Override
        public void flush() {

        }

        @Override
        public void publish(LogRecord record) {

        }
    };
    //public String mUsername = "Kentoy Johannessen";
    public String mUsername;
    public String id = MainActivity.user_id;
    public String conversation;

    //TODO: Kentoy
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAdapter = new MessageAdapter(activity, mMessages);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_chat, container, false);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);
        mInputMessageView = (EditText) view.findViewById(R.id.inputMsg);
        //Fetch Conversation Data
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Constants.postMessage+conversation, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Loading", Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray messages = response.getJSONArray("messages");
                            String username;
                            String message;
                            for(int i=0;i<messages.length();i++){
                                JSONObject msg = messages.getJSONObject(i);
                                username = msg.getString("name");
                                message = msg.getString("message");
                                addMessage(username, message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getActivity(), responseString, Toast.LENGTH_LONG).show();
            }
        });
        //Pusher Code
        Pusher pusher = new Pusher("b21e529d3fc61f261eb1");
        pusher.connect();
        Channel channel = pusher.subscribe(conversation);
        channel.bind("new_conversation", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Data", data);
                        try {
                            JSONObject obj = new JSONObject(data);
                            JSONArray messages = obj.getJSONArray("messages");
                            JSONObject msg = messages.getJSONObject(messages.length()-1);
                            String username;
                            String message;
                            username = msg.getString("name");
                            message = msg.getString("message");
                            if(!username.equals(mUsername)){
                                addMessage(username, message);
                            }
                        } catch (JSONException e) {
                            return;
                        }
                    }

                });
            }

        });

        mInputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.btnSend || id == EditorInfo.IME_NULL) {
                    attemptSend();
                    return true;
                }
                return false;
            }
        });
        mInputMessageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == mUsername) return;
                /*if (!mSocket.connected()) return;

                if (!mTyping) {
                    mTyping = true;
                    mSocket.emit("typing");
                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);*/
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Button sendButton = (Button) view.findViewById(R.id.btnSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });
    }

    private void addMessage(String username, String message) {
        if(this.mUsername.equals(username)){
            mMessages.add(new Message.Builder(Message.TYPE_MESSAGE_SELF)
                    .username(username).message(message).build());
        }else{

            mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                    .username(username).message(message).build());
        }
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }


    private void attemptSend() {
        if (null == mUsername) return;
        mTyping = false;

        String message = mInputMessageView.getText().toString().trim();
        mInputMessageView.setText("");
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return;
        }

        JSONObject messages = new JSONObject();

        try {
            messages.put("message", message);
            messages.put("name", mUsername);
            messages.put("id", id);
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("messages",messages);
            StringEntity entity = new StringEntity(jsonParams.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(getActivity(),Constants.postMessage + conversation,entity,"application/json",new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Lusot", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(getActivity(), responseString, Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        addMessage(mUsername, message);
    }

    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }
}
