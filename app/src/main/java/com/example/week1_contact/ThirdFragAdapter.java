package com.example.week1_contact;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.week1_contact.fragment.Room;

import java.util.ArrayList;

public class ThirdFragAdapter extends BaseAdapter {

    private ArrayList<Room> roomList = new ArrayList<Room>();
    private String userName;

    public ThirdFragAdapter(ArrayList<Room> roomList, String userName){
        this.userName = userName;
        this.roomList = roomList;
    }

    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.connected_person, parent, false);
        }

        TextView nameTextView = (TextView) convertView.findViewById(R.id.userName);
        TextView scoreTextView = (TextView) convertView.findViewById(R.id.userScore);
        Button inviteButton = (Button) convertView.findViewById(R.id.inviteButton);

        final Room room = roomList.get(position);
        String userNum = Integer.toString(room.getUserNum());
        nameTextView.setText(userNum);
        scoreTextView.setText(room.getUserScore());
        inviteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), gameActivity.class);
                intent.putExtra("name", userName);
                intent.putExtra("roomName", room.getRoomName());

                v.getContext().startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return roomList.get(position);
    }

}
