package com.satish.facebook.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.satish.facebook.R;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.app.AppController;
import com.satish.facebook.models.Friend;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by satish on 30/8/15.
 */
public class FindFriendAdapter extends BaseAdapter {
    private ArrayList<Friend> friendArrayList;
    private LayoutInflater inflater;
    private Activity activity;
    String id;
    private TextView lblRequestSent;
    private Button btnAddFriend;
    private static String tag = "json_tag";
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public FindFriendAdapter(ArrayList<Friend> friendArrayList, Activity activity, String id) {

        this.friendArrayList = friendArrayList;
        this.activity = activity;
        this.id = id;
    }

    @Override
    public int getCount() {
        return friendArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            view = inflater.inflate(R.layout.find_friend_list_view, null);
        TextView lblName = (TextView) view.findViewById(R.id.friend_name);
        lblRequestSent = (TextView) view.findViewById(R.id.text_friend_request_sent);
        NetworkImageView profileImage = (NetworkImageView) view.findViewById(R.id.image);
        btnAddFriend = (Button) view.findViewById(R.id.add_friend);
        final Friend friend = friendArrayList.get(position);
        Log.d("id,name", friend.getName());
        lblName.setText(friend.getName());
        profileImage.setImageUrl(friend.getProfileImageUrl(), imageLoader);
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnAddFriend.getText().equals("Add Friend")) {
                    Toast.makeText(activity.getApplicationContext(),id+""+friend.getId(),Toast.LENGTH_LONG).show();
                    requestSent(id, friend.getId());
                } else if (btnAddFriend.getText().equals("Cancel")) {
                    lblRequestSent.setText("Request canceled");
                    btnAddFriend.setText("Add Friend");
                    btnAddFriend.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                }
            }
        });
        return view;
    }

    private void requestSent(final String user_id, final String friend_id) {
        String url = AppConfig.URL_FRIEND_SUGGESTIONS;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                lblRequestSent.setText("Request is sent");
                                btnAddFriend.setText("Cancel");
                                btnAddFriend.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                            } else lblRequestSent.setText("");
                            btnAddFriend.setText("Add Friend");
                        } catch (Exception e) {
                            Log.d("error in", "catch");
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("FindFriendAdapter", "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("user_id", user_id);
                params.put("friend_id", friend_id);
                return params;
            }
        };

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

    }
}