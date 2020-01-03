package com.example.week1_contact;

public class WifiData implements Comparable<WifiData> {
    private String SSID;
    private String BSSID;
    private String RSSI;
    private int ID;
//    private boolean isOn; // 허용 /차단

    public WifiData(String SSID, String BSSID, String RSSI, int ID) {
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.RSSI = RSSI;
        this.ID = ID;
//        this.isOn = isOn;
    }

    public String getSSID() {
        return this.SSID;
    }

    public String getBSSID(){
        return this.BSSID;
    }

    public String getRSSI() {
        return this.RSSI;
    }

    public int getID() {
        return this.ID;
    }

//    public boolean getisOn() {
//        return this.isOn;
//    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public void setRSSI(String RSSI) {
        this.RSSI = RSSI;
    }

    @Override
    public int compareTo(WifiData wifiData) {
        return this.RSSI.compareTo(wifiData.getRSSI());
    }

//    public void setisOn(boolean isOn) {
//        this.isOn = isOn;
//    }

}
