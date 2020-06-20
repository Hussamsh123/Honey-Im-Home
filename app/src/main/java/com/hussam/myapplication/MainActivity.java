package com.hussam.myapplication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
    private static final String NEW_LOCATION = "new_location";
    private static final String STOPPED = "stopped";
    private static final String STARTED = "started";
    public static final String START_TRACKING_LOCATION = "Start tracking location";
    public static final String STOP_TRACKING_LOCATION = "Stop tracking location";
    private static final int locationRequestCode = 1111;
    private static final int sendSmsRequestCode = 2222;
    public static final int METERS = 50;
    public static final String HONEY_I_M_HOME = "Honey I'm Home";
    public static final String PHONE_PROVIDING_MSG = "if this is not your " +
            "first time providing a number, you can delete the old one by " +
            "inserting an empty phone number";
    public static final String SMS_PERMISSION_NEEDED = "In order to use this feature, you need to allow" +
            " the application to send messages";
    public static final String LOCATION_PERMISSION_NEEDED = "You can't run this application without location" +
            " services allowed";
    public static final String PROVIDE_A_PHONE_NUMBER = "Provide a Phone Number";
    public static final String CONFIRM = "Confirm";
    Button trackButton, setHomeButton, clearHomeButton, smsButton, testButton;
    TextView longitudeValue, longitudeText,
            latitudeText, accuracyText, latitudeValue,
            accuracyValue, homeLocationText, homeLatitude,
            homeLongitude;
    LocationInfo locationInfo;
    BroadcastReceiver broadcastReceiver;
    IntentFilter updateIntent, stopIntent, startIntent;
    ApplicationManager applicationPointer;
    boolean showHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showHome = false;
        applicationPointer = (ApplicationManager)getApplicationContext();
        longitudeValue = findViewById(R.id.longitudeVal);
        latitudeValue = findViewById(R.id.latitudeVal);
        accuracyValue = findViewById(R.id.accuracyVal);
        accuracyText = findViewById(R.id.accuracyText);
        longitudeText = findViewById(R.id.longitudeText);
        latitudeText = findViewById(R.id.latitudeText);
        trackButton = findViewById(R.id.trackButton);
        setHomeButton = findViewById(R.id.homeButton);
        homeLocationText = findViewById(R.id.homeLocation);
        homeLatitude = findViewById(R.id.homeLatitude);
        homeLongitude = findViewById(R.id.homeLongitude);
        clearHomeButton = findViewById(R.id.clearHome);
        smsButton = findViewById(R.id.smsButton);
        testButton = findViewById(R.id.testButton);
        locationInfo = new LocationInfo(this);
        if (!applicationPointer.storage.getPhoneNumber().equals("")){
            testButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewHomeLocation();
        trackButtonListener();
        receiveBroadCasts();
        registerContextReceiver();
        homeButtonsListener();
        if (showHome){
            clearHomeButton.setVisibility(View.VISIBLE);
            homeLocationText.setVisibility(View.VISIBLE);
            homeLongitude.setVisibility(View.VISIBLE);
            homeLatitude.setVisibility(View.VISIBLE);
        }
        smsButtonListener();
    }

    void trackButtonListener(){
        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trackButton.getText().toString().equals(START_TRACKING_LOCATION)){
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        onLocationPermissionGranted();

                    }else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                locationRequestCode);
                    } }else{
                    locationInfo.stopTracking();
                    makeTextInvisible();
                    trackButton.setText(START_TRACKING_LOCATION);
                    locationInfo.getLocationManager().removeUpdates(locationInfo.getLocationListener());
                    setHomeButton.setVisibility(View.GONE);
                    longitudeValue.setText("0");
                    latitudeValue.setText("0");
                    accuracyValue.setText("0");
                }
            }
        });
    }

    void homeButtonsListener(){
        setHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHomeLocation();
                clearHomeButton.setVisibility(View.VISIBLE);
            }
        });
        clearHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHomeLocation();
            }
        });
    }
    private void smsButtonListener() {
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission
                        (MainActivity.this, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED){
                    onSmsPermissionGranted();
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.SEND_SMS}, sendSmsRequestCode);
                }
            }
        });
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (applicationPointer.storage.getPhoneNumber() != null) {
                    sendBroadcast(new Intent(ApplicationManager.broadCastFilter).
                            putExtra("phone", applicationPointer.storage.getPhoneNumber()).
                            putExtra("content", HONEY_I_M_HOME));
                }
            }
        });
    }

    void registerContextReceiver(){
        updateIntent = new IntentFilter(NEW_LOCATION);
        stopIntent = new IntentFilter(STOPPED);
        startIntent = new IntentFilter(STARTED);
        registerReceiver(broadcastReceiver, updateIntent);
        registerReceiver(broadcastReceiver, startIntent);
        registerReceiver(broadcastReceiver, stopIntent);
    }

    void makeTextVisible(){
        latitudeValue.setVisibility(View.VISIBLE);
        longitudeValue.setVisibility(View.VISIBLE);
        accuracyValue.setVisibility(View.VISIBLE);
        latitudeText.setVisibility(View.VISIBLE);
        longitudeText.setVisibility(View.VISIBLE);
        accuracyText.setVisibility(View.VISIBLE);
    }

    void makeTextInvisible(){
        latitudeValue.setVisibility(View.GONE);
        longitudeValue.setVisibility(View.GONE);
        accuracyValue.setVisibility(View.GONE);
    }
    void onLocationPermissionGranted(){
        locationInfo.startTracking();
        updateUIValues(locationInfo);
        makeTextVisible();
        trackButton.setText(STOP_TRACKING_LOCATION);
    }
    void onSmsPermissionGranted(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final TextView textView = new EditText(this);
        builder.setTitle(PROVIDE_A_PHONE_NUMBER).setMessage(PHONE_PROVIDING_MSG).setView(textView);
        builder.setPositiveButton(CONFIRM, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String hbd = textView.getText().toString();
                applicationPointer.storage.saveNumber(hbd);
                if(hbd.equals("")){
                    testButton.setVisibility(View.GONE);
                }else{
                    testButton.setVisibility(View.VISIBLE);
                }
            }
        });
        builder.create().show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == locationRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onLocationPermissionGranted();
            } else {
                Toast toast = Toast.makeText(this,
                        LOCATION_PERMISSION_NEEDED, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }else if (requestCode == sendSmsRequestCode){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                onSmsPermissionGranted();
            }else{
                Toast toast = Toast.makeText(this,
                        SMS_PERMISSION_NEEDED, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        locationInfo.getLocationManager().removeUpdates(locationInfo.getLocationListener());
    }

    void updateUIValues(LocationInfo location){
        latitudeValue.setText(String.valueOf(location.getLatitude()));
        longitudeValue.setText(String.valueOf(location.getLongitude()));
        accuracyValue.setText(String.valueOf(location.getAccuracy()));
    }
    void receiveBroadCasts(){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null){
                    return;
                }
                assert intent.getAction() != null;
                if (intent.getAction().equals(NEW_LOCATION)){
                    updateUIValues(locationInfo);
                    makeTextVisible();
                    if(locationInfo.getAccuracy() <= METERS){
                        setHomeButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
    }
    void viewHomeLocation(){
        if (!applicationPointer.storage.getHomeLatitude().equals("")){
            showHome = true;
            String homeLong = "Longitude: " + applicationPointer.storage.getHomeLongitude();
            homeLongitude.setText(homeLong);
            String homeLat = "Latitude: " + applicationPointer.storage.getHomeLatitude();
            homeLatitude.setText(homeLat);
        }
    }

    void saveHomeLocation(){
        String homeLong = "Longitude: " + longitudeValue.getText().toString();
        String homeLat = "Latitude: " + latitudeValue.getText().toString();
        homeLongitude.setText(homeLong);
        homeLatitude.setText(homeLat);
        applicationPointer.storage.saveHomeLocation(longitudeValue.getText().toString(),
                latitudeValue.getText().toString());
        homeLocationText.setVisibility(View.VISIBLE);
        homeLongitude.setVisibility(View.VISIBLE);
        homeLatitude.setVisibility(View.VISIBLE);
    }
    void clearHomeLocation(){
        applicationPointer.storage.clearLocation();
        clearHomeButton.setVisibility(View.GONE);
        homeLongitude.setVisibility(View.GONE);
        homeLatitude.setVisibility(View.GONE);
        homeLocationText.setVisibility(View.GONE);
    }
}
