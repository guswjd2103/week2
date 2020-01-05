package com.example.week1_contact;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.week1_contact.fragment.Room;

import java.util.ArrayList;

public class ThirdFragAdapter extends BaseAdapter {

    private ArrayList<Room> players = new ArrayList<Room>();

    public ThirdFragAdapter(ArrayList<Room> players){
        this.players = players;
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.connected_person, parent, false);
        }
        TextView nameTextView = (TextView) convertView.findViewById(R.id.userName);
        TextView scoreTextView = (TextView) convertView.findViewById(R.id.userScore);
        Button inviteButton = (Button) convertView.findViewById(R.id.inviteButton);

        final Room player = players.get(position);

        nameTextView.setText(player.getUserName());
        scoreTextView.setText(player.getUserScore());
        inviteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), gameActivity.class);
                intent.putExtra("name", player.getUserName());
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
        return players.get(position);
    }

}
