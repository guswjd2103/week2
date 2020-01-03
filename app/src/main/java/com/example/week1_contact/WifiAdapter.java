package com.example.week1_contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

public class WifiAdapter extends ArrayAdapter<WifiData> {
    Context context;
    int resource;
    List<WifiData> wifiData;
    LayoutInflater inflater;

    public WifiAdapter(Context context, int resource, List<WifiData> wifiData) {
        super(context, resource, wifiData);
        this.resource = resource;
        this.wifiData = wifiData;
        this.context = context;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
            convertView = inflater.inflate(resource, parent, false);

            TextView ssid = (TextView) convertView.findViewById(R.id.ssidTextView);
            TextView bssid = (TextView) convertView.findViewById(R.id.bssidTextView);
            TextView rssi = (TextView) convertView.findViewById(R.id.rssiLevelTextView);

            ssid.setText(wifiData.get(position).getSSID());
            bssid.setText(wifiData.get(position).getBSSID());
            rssi.setText(wifiData.get(position).getRSSI());
        return convertView;
    }
}
