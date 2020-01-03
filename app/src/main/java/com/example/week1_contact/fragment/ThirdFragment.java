package com.example.week1_contact.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.week1_contact.R;
import com.example.week1_contact.WifiAdapter;
import com.example.week1_contact.WifiData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;


public class ThirdFragment extends Fragment {

    boolean doneWifiScan = true;
    private WifiManager wifiManager;
    private WifiAdapter adapter;
    private List<WifiData> wifiList = new ArrayList<WifiData>();
    private ListView listView;

    IntentFilter filter;
    private Context mContext;
    private List<ScanResult> scanResults = new ArrayList<>();
    ArrayList<String> BSSIDList = new ArrayList<String>();

    public static ThirdFragment newInstance() {
        ThirdFragment fragment = new ThirdFragment();
        return fragment;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                getWifiInfo();
                wifiManager.startScan();

            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                mContext.sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
            }
        }
    };

    private void getWifiInfo() {
        wifiList.clear();
        BSSIDList.clear();

        if(!doneWifiScan) {
            scanResults = wifiManager.getScanResults();

            for(int i=0; i<scanResults.size();i++) {
                ScanResult select = scanResults.get(i);
                String SSID = select.SSID;
                String BSSID = select.BSSID;
                String RSSI = String.valueOf(select.level);

                if(!BSSIDList.contains(SSID)) {
                    BSSIDList.add(SSID);
                    WifiData wifiData = new WifiData(SSID, BSSID, RSSI, i);
                    wifiList.add(wifiData);

                }
                Log.d("wifi", SSID);
            }
            Collections.sort(wifiList);
            adapter.notifyDataSetChanged();
            doneWifiScan = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[] {
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.CHANGE_WIFI_STATE,
                                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                                Manifest.permission.SYSTEM_ALERT_WINDOW
                        },
                        1);
            }
        }

        View view = inflater.inflate(R.layout.fragment_third, container, false);
        mContext = view.getContext();

        wifiManager = (WifiManager) mContext.getSystemService(WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        initWifiScan();

        listView = (ListView) view.findViewById(R.id.listView2);

        adapter = new WifiAdapter(mContext, R.layout.item_accesspoint, wifiList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, wifiList.get(position).getSSID(), Toast.LENGTH_SHORT).show();
//                Log.d("ssid1", wifiList.get(position).getSSID());
                int idx = wifiList.get(position).getID();
                final ScanResult result = scanResults.get(idx);
//                Log.d("ssid", result.SSID);
                openDialog(result);
            }
        });

        FloatingActionButton btn = (FloatingActionButton)view.findViewById(R.id.start);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(doneWifiScan == true) {
                    Toast.makeText(getActivity(), "WIFI scan start!!", Toast.LENGTH_LONG).show();
                    wifiManager.startScan();
                    doneWifiScan = false;
                }
            }
        });

        FloatingActionButton btn2 = (FloatingActionButton)view.findViewById(R.id.stop);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Toast.makeText(getActivity(), "WIFI scan stopped!!", Toast.LENGTH_LONG).show();
                    stopWifi();
                    doneWifiScan = true;
            }
        });
        return view;
    }

    public void initWifiScan() {
        filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mContext.registerReceiver(receiver, filter);
        wifiManager.startScan();
    }

    public void stopWifi() {
        mContext.unregisterReceiver(receiver);
    }

    private void openDialog(final ScanResult scanResults) {
        PasswordDialogFragment dialogFragment = PasswordDialogFragment.newInstance(new PasswordDialogFragment.PasswordInputListener() {
            @Override
            public void onPasswordInputComplete(String password) {
                if(password != null) {
                    Toast.makeText(mContext, "Connecting..", Toast.LENGTH_SHORT).show();
                    final ScanResult param = scanResults;
                    connectWiFi(param, password);
                } else {
                    Toast.makeText(mContext, "Null Password!", Toast.LENGTH_SHORT).show();
                    Log.d("password", "nononono");
                }
            }
        });
        dialogFragment.show(getFragmentManager(), "addDialog");
    }

    public void connectWiFi(ScanResult scanResult, String password) {
        try {
            Log.v("rht", "Item clicked, SSID " + scanResult.SSID + " Security : " + scanResult.capabilities);

            String networkSSID = scanResult.SSID;
            String networkPass = password; //다이얼로그에서 받은 패스워드 받아오기

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
            conf.status = WifiConfiguration.Status.ENABLED;
            conf.priority = 40;

            if (scanResult.capabilities.toUpperCase().contains("WEP")) {
                Log.v("rht", "Configuring WEP");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

                if (networkPass.matches("^[0-9a-fA-F]+$")) {
                    conf.wepKeys[0] = networkPass;
                } else {
                    conf.wepKeys[0] = "\"".concat(networkPass).concat("\"");
                }

                conf.wepTxKeyIndex = 0;

            } else if (scanResult.capabilities.toUpperCase().contains("WPA")) {
                Log.v("rht", "Configuring WPA");

                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

                conf.preSharedKey = "\"" + networkPass + "\"";

            } else {
                Log.v("rht", "Configuring OPEN network");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.clear();
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            }

            WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
            int networkId = wifiManager.addNetwork(conf);

            Log.v("rht", "Add result " + networkId);

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    Log.v("rht", "WifiConfiguration SSID " + i.SSID);
                    boolean isDisconnected = wifiManager.disconnect();
                    Log.v("rht", "isDisconnected : " + isDisconnected);
                    boolean isEnabled = wifiManager.enableNetwork(i.networkId, true);
                    Log.v("rht", "isEnabled : " + isEnabled);
                    boolean isReconnected = wifiManager.reconnect();
                    Log.v("rht", "isReconnected : " + isReconnected);
                    break;
                }
            }

//            ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//            if (mWifi.isConnected()) {
//                Toast.makeText(getActivity(), "WIFI Connect Success!", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(getActivity(), "WIFI Connect Fail!", Toast.LENGTH_LONG).show();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
