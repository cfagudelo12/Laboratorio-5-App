package com.syvelasquez10.cfagudelo12.laboratorio5;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationSettingsRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;

public class TCPActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private Location mCurrentLocation;

    private Location mLastLocation;

    private String latitude;

    private String longitude;

    private String altitude;

    private String speed;

    private boolean mRequestingLocationUpdates;

    private boolean backEnabled;

    private TCPCommunicationTask tcpTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tcp_activity);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        backEnabled=true;

        Button startButton = (Button) findViewById(R.id.tcpStart);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });

        Button stopButton = (Button) findViewById(R.id.tcpStop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
            }
        });
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        latitude = "Fetching data";
        longitude = "Fetching data";
        speed = "Fetching data";
        altitude = "Fetching data";

        createLocationRequest();

        startLocationUpdates();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(950);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void start() {
        mRequestingLocationUpdates = true;
        backEnabled = false;
        tcpTask = new TCPCommunicationTask();
        tcpTask.execute();
    }

    public void stop() {
        mRequestingLocationUpdates = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if(!backEnabled){
            AlertDialog alertDialog = new AlertDialog.Builder(TCPActivity.this).create();
            alertDialog.setTitle("Alerta");
            alertDialog.setMessage("Por favor intente de nuevo en un momento");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        else{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = mCurrentLocation;
        mCurrentLocation = location;
        latitude = String.valueOf(mCurrentLocation.getLatitude());
        longitude = String.valueOf(mCurrentLocation.getLongitude());
        altitude = String.valueOf(mCurrentLocation.getAltitude());
        if(mLastLocation!=null){
            speed = String.valueOf(calculateDistance(mLastLocation.getLatitude(),mLastLocation.getLongitude(),mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()));
        }
        else {
            speed = String.valueOf(mCurrentLocation.getSpeed());
        }
    }

    private static long calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        long distanceInMeters = Math.round(6371000 * c);
        return distanceInMeters;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        AlertDialog alertDialog = new AlertDialog.Builder(TCPActivity.this).create();
        alertDialog.setTitle("Alerta");
        alertDialog.setMessage("Fuck");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public class TCPCommunicationTask extends AsyncTask<Void, Void, Void> {

        private final String IP = "192.168.0.5";

        private final int PORT = 8080;

        private PrintWriter escritor;

        private BufferedReader lector;

        private Socket socket;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                socket = new Socket(IP, PORT);
                escritor = new PrintWriter(socket.getOutputStream(), true);
                lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                escritor.println("HELLO");
                String in = lector.readLine();
                if (!in.equals("GO")) {
                    mRequestingLocationUpdates = false;
                    AlertDialog alertDialog = new AlertDialog.Builder(TCPActivity.this).create();
                    alertDialog.setTitle("Alerta");
                    alertDialog.setMessage("No se logró conexión con el servidor");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return null;
                }
                while (mRequestingLocationUpdates) {
                    Thread.sleep(1000);
                    escritor.println( latitude + "," + longitude + "," + altitude + "," + speed);
                }
                escritor.println("STOP");
                escritor.close();
                lector.close();
                backEnabled=true;
            } catch (Exception e) {
                backEnabled=true;
                System.err.println("Exception: " + e.getMessage());
                System.exit(1);
            }
            return null;
        }
    }
}
