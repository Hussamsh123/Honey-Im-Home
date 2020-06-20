package com.hussam.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;

class AppStorage {
    private SharedPreferences sp;
    private String homeLongitudeValue, homeLatitudeValue, phoneNumber;

    AppStorage(Context context){
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        retrieveData();
    }

    private void retrieveData(){
        ThreadingHelper.runAsyncInBackground(new Runnable() {
            @Override
            public void run() {
                phoneNumber = sp.getString("phone", "");
                homeLatitudeValue = sp.getString("lat", "");
                homeLongitudeValue = sp.getString("lon", "");
            }
        });
    }

    void saveHomeLocation(final String longitude, final String latitude){
        ThreadingHelper.runAsyncInBackground(new Runnable() {
            @Override
            public void run() {
                homeLatitudeValue = latitude;
                homeLongitudeValue = longitude;
                Gson gson =  new Gson();
                String lon = gson.toJson(homeLongitudeValue);
                String lat = gson.toJson(homeLatitudeValue);
                sp.edit().putString("lon", lon).putString("lat", lat).apply();
            }
        });
    }

    void saveNumber(final String number){
        ThreadingHelper.runAsyncInBackground(new Runnable() {
            @Override
            public void run() {

                phoneNumber = number;
                Gson gson =  new Gson();
                String phone = gson.toJson(phoneNumber);
                sp.edit().putString("phone", phone).apply();
            }
        });
    }

    String getHomeLongitude(){return homeLongitudeValue;}
    String getHomeLatitude() {return homeLatitudeValue;}
    String getPhoneNumber(){ return phoneNumber;}
    void clearLocation(){
        sp.edit().putString("lon", null).putString("lat", null).apply();
    }
}
