package com.example.mycontactlist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,
GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    final int PERMISSION_REQUEST_LOCATION = 101;
    GoogleMap gMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    ArrayList<Contact> contacts = new ArrayList<>();
    Contact currentContact = null;
    SensorManager sensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    TextView textDirection;
    private SensorEventListener mySensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        ////////////////
        }
        float[] accelerometerValues;
        float[] magnetometerValues;
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                accelerometerValues = event.values;
            if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                magnetometerValues = event.values;
            if(accelerometerValues != null && magnetometerValues != null){
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, accelerometerValues, magnetometerValues);
            if(success){
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                float azimut = (float) Math.toDegrees(orientation[0]);
                if(azimut < 0.0f){azimut += 360.0f;}
                String direction;
                if(azimut > 315 || azimut < 45){direction = "N";}
                else if(azimut >= 225 && azimut < 315){direction = "W";}
                else if(azimut >= 135 && azimut < 225){direction = "S";}
                else {direction = "E";}
                textDirection.setText(direction);
            }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Bundle extras = getIntent().getExtras();
        try{
            ContactDataSource ds = new ContactDataSource(ContactMapActivity.this);
            ds.open();
            if(extras != null){
                currentContact = ds.getSpecificContact(extras.getInt("contactid"));
            }
            else{
                contacts = ds.getContacts("contactname", "ASC");
            }
            ds.close();
        }
        catch(Exception e){
            Toast.makeText(this, "Contact(s) could not be retrieved.", Toast.LENGTH_LONG).show();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //This get the map async.
        mapFragment.getMapAsync(this);
        //This creates the location listener
        createLocationRequest();

        //Establishes a connection with GOOGLE API
        if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //SENSOR INSTANTIATION
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //Not all devices have sensors, so we first test if it does so our app doesnt crash.
        //The 1st registerListener Parameter is for what listener you want to associate it with and the second is
        //how fast you want to process the sensor events. In our case we want updates as fast as possible
        if(accelerometer != null && magnetometer != null){
            sensorManager.registerListener(mySensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(mySensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else{
            Toast.makeText(this, "Sensors not found", Toast.LENGTH_SHORT).show();
        }
        textDirection = (TextView) findViewById(R.id.textHeading);

        initListButton();
        initMapButton();
        initSettingsButton();
        initMapTypeButton();
    }

    //Does the actual connecting to the API
    protected void onStart(){
        mGoogleApiClient.connect();
        super.onStart();
    }

    //Disconnects from API services when onStop method is called
    //This is not used in onPause, so the app can still monitor location in background
    protected void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location){
//        Toast.makeText(getBaseContext(), "Lat: " + location.getLatitude() +
//                " Long: " + location.getLongitude() +
//                "Accuracy: " + location.getAccuracy(), Toast.LENGTH_LONG).show();
    }

    protected void createLocationRequest(){
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle){
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i){
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.dark_map);
        googleMap.setMapStyle(mapStyleOptions);
        //The next 5 lines are used to get the dimensions of device in order to display markers properly
        Point size = new Point();
        WindowManager w = getWindowManager();
        w.getDefaultDisplay().getSize(size);
        int measureWidth = size.x;
        int measureHeight = size.y;
        googleMap.getUiSettings().setCompassEnabled(true);
        if(contacts.size() > 0) {
            //constructs the geographic boundaries of a set of GPS Coordinates
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            //A loop that loops through all the contacts in the phone and adds a marker for each contact.
            for(int i=0; i <contacts.size(); i++){
                currentContact = contacts.get(i);
                Geocoder geo = new Geocoder(this);
                List<Address> addresses = null;
                //Getting the address of the current contact
                String address = currentContact.getStreetAddress() + ", " + currentContact.getCity()
                        + ", " + currentContact.getState() + " " + currentContact.getZipCode();

                try{
                    //getFromLocation returns longitude and latitude from name
                    addresses = geo.getFromLocationName(address, 1);
                    LatLng point = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                    //This point is now added to the builder where it is considered in creating the map boundaries
                    builder.include(point);
                    //MarkerOptions().postion sets the point on the map. The title is what you see when you click on that particular point... .setIcon
                    gMap.addMarker(new MarkerOptions().position(point).title(currentContact.getContactName() + " " + currentContact.getPhoneNumber()).snippet(address)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.googlepin2));
                    gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), measureWidth, measureHeight, 450));

                }
                catch(IOException e){

                    e.printStackTrace();
                }
            }

    }
        else{
            if(currentContact != null){
                Geocoder geo = new Geocoder(this);
                List<Address> addresses = null;

                String address = currentContact.getStreetAddress() + ", " + currentContact.getCity()
                        + ", " + currentContact.getState() + ", " + currentContact.getZipCode();

                try{
                    addresses = geo.getFromLocationName(address, 1);
                }
                catch(IOException e){

                    e.printStackTrace();
                }
                if(addresses != null){
                LatLng point = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                gMap.addMarker(new MarkerOptions().position(point).title(currentContact.getContactName()).snippet(address));
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point,16));}
                else{
                    Toast.makeText(getBaseContext(), "Could not find Contacts ", Toast.LENGTH_LONG).show();

                }
            }

            else{
                AlertDialog alertDialog = new AlertDialog.Builder(ContactMapActivity.this).create();
                alertDialog.setTitle("No Data");
                alertDialog.setMessage("No data is available for the mapping function");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        finish();
                    } });
                alertDialog.show();
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(ContactMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ContactMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Snackbar.make(findViewById(R.id.activity_contact_map), "MyContactList requires this permission to locate your contacts"
                                        , Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ActivityCompat.requestPermissions(
                                        ContactMapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_REQUEST_LOCATION);
                            }
                        }).show();
                    } else {
                        ActivityCompat.requestPermissions(ContactMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_REQUEST_LOCATION);
                    }
                } else {
                    startLocationUpdates();
                }
            } else {
                startLocationUpdates();
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error requesting Permission", Toast.LENGTH_LONG).show();
        }

    }


//    private void initGetLocationButton(){
//        Button locationButton = (Button) findViewById(R.id.buttonGetLocation);
//        locationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//    }


        @Override
    public void onPause(){
            if(Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                return;
            }
        super.onPause();
        try{
//            locationManager.removeUpdates(gpsListener);
//            locationManager.removeUpdates(networkListener);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    private void startLocationUpdates(){

        if(Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            return;
        }

        gMap.setMyLocationEnabled(true);

        }


//        private boolean isBetterLocation(Location location){
//        boolean isBetter = false;
//        if(currentBestLocation == null){
//            isBetter = true;
//        }
//        else if(location.getAccuracy() <= currentBestLocation.getAccuracy()){
//            isBetter = true;
//        }
//        else if(location.getTime() - currentBestLocation.getTime() > 5*60*1000){
//            isBetter = true;
//        }
//        return isBetter;
//        }


        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch(requestCode){
            case PERMISSION_REQUEST_LOCATION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startLocationUpdates();
                }
                else{
                    Toast.makeText(ContactMapActivity.this, "MyContactList will not locate your contacts.", Toast.LENGTH_LONG).show();
                }
            }
        }
        }



    private void initListButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactMapActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    private void initMapButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonMap);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactMapActivity.this, ContactMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingsButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonSettings);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactMapActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initMapTypeButton(){
        final Button satelitebtn = (Button) findViewById(R.id.buttonMapType);
        satelitebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentSetting = satelitebtn.getText().toString();
                if(currentSetting.equalsIgnoreCase("Satellite View")){
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                else{
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    satelitebtn.setText("Satellite View");
                }
            }
        });
    }


}


