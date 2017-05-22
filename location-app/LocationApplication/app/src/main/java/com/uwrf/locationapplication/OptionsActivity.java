package com.uwrf.locationapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.google.gson.Gson;
import com.uwrf.config.Configs;
import com.uwrf.service.data.Message;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStreamReader;


public class OptionsActivity extends AppCompatActivity {
    private TextView textView;
    private Button getLocButton;
    private Button addLocButton;
    private Button updateLocButton;
    private Button deleteLocButton;
    private Button signOutButton;
    private String mEmail;
    private String mPassword;

    private static final int REGISTER_LOCATION_UPDATES = 0;
    private static final int ADD_LOCATION = 1;
    private static final int UPDATE_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        SharedPreferences prefs = getSharedPreferences("credentials", MODE_PRIVATE);
        mEmail = prefs.getString("email", null);
        mPassword = prefs.getString("password", null);

        textView = (TextView) findViewById(R.id.textView);
        getLocButton = (Button) findViewById(R.id.get_location_button);
        addLocButton = (Button) findViewById(R.id.add_location_button);
        updateLocButton = (Button) findViewById(R.id.update_location_button);
        deleteLocButton = (Button) findViewById(R.id.delete_location_button);
        signOutButton = (Button) findViewById(R.id.sign_out_button);

        textView.setText(mEmail);
        //textView.setHeight(5);

        getPermission(REGISTER_LOCATION_UPDATES);

        getLocButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        addLocButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermission(ADD_LOCATION);
            }
        });

        updateLocButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermission(UPDATE_LOCATION);
            }
        });

        deleteLocButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLocation();
            }
        });

        signOutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void getLocation() {
        GetLocationTask getLocationTask = new GetLocationTask(mEmail, mPassword, this);
        getLocationTask.execute((Void) null);
    }

    private void addLocation() throws SecurityException {
        Location lastKnownLocation = getLastKnownLocation();
        if (lastKnownLocation != null) {
            double latitude = lastKnownLocation.getLatitude();
            double longitude = lastKnownLocation.getLongitude();
            Message msg = new Message(mEmail, Double.toString(latitude), Double.toString(longitude), Long.toString(System.currentTimeMillis()) );
            AddLocationTask addLocationTask = new AddLocationTask(mPassword, this, msg);
            addLocationTask.execute((Void) null);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_no_last_known_location), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void updateLocation() {
        Location lastKnownLocation = getLastKnownLocation();
        if (lastKnownLocation != null) {
            double latitude = lastKnownLocation.getLatitude();
            double longitude = lastKnownLocation.getLongitude();
            Message msg = new Message(mEmail, Double.toString(latitude), Double.toString(longitude), Long.toString(System.currentTimeMillis()) );
            UpdateLocationTask updateLocationTask = new UpdateLocationTask(mPassword, this, msg);
            updateLocationTask.execute((Void) null);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_no_last_known_location), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void deleteLocation() {
        DeleteLocationTask deleteLocationTask = new DeleteLocationTask(mEmail, mPassword, this);
        deleteLocationTask.execute((Void) null);
    }

    private void signOut() {
        SharedPreferences prefs = getSharedPreferences("credentials", MODE_PRIVATE);
        prefs.edit().remove("email");
        prefs.edit().remove("password");
        this.finish();
    }

    private void getPermission(int action) {
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, action);
        } else {
            switch (action) {
                case ADD_LOCATION: {
                    addLocation();
                    break;
                }
                case UPDATE_LOCATION: {
                    updateLocation();
                    break;
                }
                default: {
                    registerLocationUpdate();
                    break;
                }
            }
        }
    }

    private void registerLocationUpdate() throws SecurityException{
        LocationManager locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                System.out.println();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        });
    }

    private Location getLastKnownLocation() throws SecurityException{
        LocationManager locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        return lastKnownLocation;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ADD_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    addLocation();
                return;
            }

            case UPDATE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    updateLocation();
                return;
            }

            default: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    registerLocationUpdate();
                return;
            }
        }
    }

    public class AddLocationTask extends AsyncTask<Void, Void, HttpResponse> {

        private final String mEmail;
        private final String mPassword;
        private final Activity currentActivity;
        private Message message;

        AddLocationTask(String password, Activity currentActivity, Message message) {
            this.mEmail = message.getEmail();
            this.mPassword = password;
            this.currentActivity = currentActivity;
            this.message = message;
        }

        @Override
        protected HttpResponse doInBackground(Void... params) {
            HttpClient httpclient = new DefaultHttpClient();
            String url = Configs.protocol + "://" + Configs.host + ":" + Configs.port + "/" + Configs.service + "/add";
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader( "Authorization", "Basic " + Base64.encodeToString( (mEmail+":"+mPassword).getBytes(), Base64.NO_WRAP ) );
            try {
                // Add your data
                Gson gson = new Gson();
                String json = gson.toJson(message);
                StringEntity se = new StringEntity(gson.toJson(message));
                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                return httpclient.execute(httpPost);
            } catch (Exception exp) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final HttpResponse response) {
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.add_loc_successful), Toast.LENGTH_LONG);
                    toast.show();
                } else if (response.getStatusLine().getStatusCode() == 400) {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_email_exists_already), Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_unknown), Toast.LENGTH_LONG);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_server_not_reachable), Toast.LENGTH_LONG);
                toast.show();
            }
        }

        @Override
        protected void onCancelled() {}
    }

    public class GetLocationTask extends AsyncTask<Void, Void, HttpResponse> {

        private final String mEmail;
        private final String mPassword;
        private final Activity currentActivity;

        GetLocationTask(String email, String password, Activity currentActivity) {
            this.mEmail = email;
            this.mPassword = password;
            this.currentActivity = currentActivity;
        }

        @Override
        protected HttpResponse doInBackground(Void... params) {
            HttpClient httpclient = new DefaultHttpClient();
            String url = Configs.protocol + "://" + Configs.host + ":" + Configs.port + "/" + Configs.service + "/get";
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader( "Authorization", "Basic " + Base64.encodeToString( (mEmail+":"+mPassword).getBytes(), Base64.NO_WRAP ) );
            try {
                return httpclient.execute(httpGet);
            } catch (Exception exp) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final HttpResponse response) {
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    Gson gson = new Gson();
                    try {
                        Message message = gson.fromJson(new InputStreamReader(response.getEntity().getContent()), Message.class);
                        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("email", message.getEmail());
                        editor.putString("latitude", message.getLatitude());
                        editor.putString("longitude", message.getLongitude());
                        editor.putString("timestamp", message.getDateTime());
                        editor.commit();

                        Intent intent = new Intent(currentActivity, DisplayActivity.class);
                        currentActivity.startActivity(intent);
                    } catch (IOException e) {
                        Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_unknown), Toast.LENGTH_LONG);
                        toast.show();
                    }

                } else if (response.getStatusLine().getStatusCode() == 404) {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_no_last_known_location), Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_unknown), Toast.LENGTH_LONG);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_server_not_reachable), Toast.LENGTH_LONG);
                toast.show();
            }
        }

        @Override
        protected void onCancelled() {}
    }

    public class UpdateLocationTask extends AsyncTask<Void, Void, HttpResponse> {

        private final String mEmail;
        private final String mPassword;
        private final Activity currentActivity;
        private Message message;

        UpdateLocationTask(String password, Activity currentActivity, Message message) {
            this.mEmail = message.getEmail();
            this.mPassword = password;
            this.currentActivity = currentActivity;
            this.message = message;
        }

        @Override
        protected HttpResponse doInBackground(Void... params) {
            HttpClient httpclient = new DefaultHttpClient();
            String url = Configs.protocol + "://" + Configs.host + ":" + Configs.port + "/" + Configs.service + "/update";
            HttpPut httpPut = new HttpPut(url);
            httpPut.setHeader( "Authorization", "Basic " + Base64.encodeToString( (mEmail+":"+mPassword).getBytes(), Base64.NO_WRAP ) );
            try {
                // Add your data
                Gson gson = new Gson();
                String json = gson.toJson(message);
                StringEntity se = new StringEntity(gson.toJson(message));
                httpPut.setEntity(se);
                httpPut.setHeader("Accept", "application/json");
                httpPut.setHeader("Content-type", "application/json");

                return httpclient.execute(httpPut);
            } catch (Exception exp) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final HttpResponse response) {
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.update_loc_successful), Toast.LENGTH_LONG);
                    toast.show();
                } else if (response.getStatusLine().getStatusCode() == 400) {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_nothing_to_update), Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_unknown), Toast.LENGTH_LONG);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_server_not_reachable), Toast.LENGTH_LONG);
                toast.show();
            }
        }

        @Override
        protected void onCancelled() {}
    }

    public class DeleteLocationTask extends AsyncTask<Void, Void, HttpResponse> {

        private final String mEmail;
        private final String mPassword;
        private final Activity currentActivity;

        DeleteLocationTask(String email, String password, Activity currentActivity) {
            this.mEmail = email;
            this.mPassword = password;
            this.currentActivity = currentActivity;
        }

        @Override
        protected HttpResponse doInBackground(Void... params) {
            HttpClient httpclient = new DefaultHttpClient();
            String url = Configs.protocol + "://" + Configs.host + ":" + Configs.port + "/" + Configs.service + "/delete";
            HttpDelete httpDelete = new HttpDelete(url);
            httpDelete.setHeader( "Authorization", "Basic " + Base64.encodeToString( (mEmail+":"+mPassword).getBytes(), Base64.NO_WRAP ) );
            try {
                return httpclient.execute(httpDelete);
            } catch (Exception exp) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final HttpResponse response) {
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.delete_loc_successful), Toast.LENGTH_LONG);
                    toast.show();
                } else if (response.getStatusLine().getStatusCode() == 400) {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_nothing_to_delete), Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_unknown), Toast.LENGTH_LONG);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_server_not_reachable), Toast.LENGTH_LONG);
                toast.show();
            }
        }

        @Override
        protected void onCancelled() {}
    }
}
