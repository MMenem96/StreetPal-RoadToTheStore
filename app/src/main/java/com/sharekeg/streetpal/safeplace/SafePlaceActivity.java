package com.sharekeg.streetpal.safeplace;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Location;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sharekeg.streetpal.Androidversionapi.ApiInterface;
import com.sharekeg.streetpal.Home.HomeActivity;
import com.sharekeg.streetpal.R;
import com.sharekeg.streetpal.Registration.TrustedContact;
import com.sharekeg.streetpal.googleanalytics.GoogleAnalyticsHelper;
import com.sharekeg.streetpal.locationservice.LocationProvider;
import com.sharekeg.streetpal.safeplace.nearbyplaceutil.Example;

import java.io.File;
import java.io.IOException;
import java.text.Bidi;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.security.AccessController.getContext;

public class SafePlaceActivity extends AppCompatActivity implements LocationProvider.LocationCallback, OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int RequestPermissionCode = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int REQUEST_PHONE_CALL = 189;
    private static final int REQUEST_CHECK_SETTINGS = 10;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    private static MediaPlayer mediaPlayer;
    private static String audioFilePath;
    private static ImageView ivStop, ivPause, ivPlay;
    double closest_distance = 0;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    Marker userMarker;
    Marker placeMarker;
    private Uri uriContact;
    private String contactID;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Button btnCallTrustedContact, btnMarkSafe;
    private Chronometer mChronometer;
    private boolean isRecording = false;
    private long timeDifference;
    private TextView tvReturnToTheGuide;
    private int PROXIMITY_RADIUS = 5000;
    private String Type;
    private LatLng nearest_place, latLng;
    private MarkerOptions markerOptions;
    private String placeName;
    private String vicinity;
    private String contactName, token, contactNumber;
    private String email;
    private ProgressDialog pDialog;
    private Retrofit retrofitforauthentication;
    private Intent telIntent;
    private double lati, lngi;
    private String userName;
    private String fullName;
    private String dateInString;
    private File audiofile;
    private File SDCardpath;
    private File myDataPath;
    SharedPreferences languagepref;
    String language;
    private Button btnNavigate;
    private Location currentUserLocation;
    private LocationProvider mLocationProvider;

    private GoogleAnalyticsHelper mGoogleHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");
        checkLanguage(language);
        setContentView(R.layout.activity_safe_palce);

        InitGoogleAnalytics();
        SendScreenNameGoogleAnalytics();

        mChronometer = (Chronometer) findViewById(R.id.chrono);

        mLocationProvider = new LocationProvider(this, this);

        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(SafePlaceActivity.this);
        if (mypreference.getBoolean("loggedIn", false)) {
            token = mypreference.getString("token", null);
            contactName = mypreference.getString("contactName", null);
            contactNumber = mypreference.getString("contactNumber", null);
            userName = mypreference.getString("myUserName", "User Name");
            fullName = mypreference.getString("myFullName", null);

        }
        if (checkPermission()) {
            Date currentTime = Calendar.getInstance().getTime();
            AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Voice Evidence" + currentTime + ".3gp";
            Log.d("savef", AudioSavePathInDevice);

            MediaRecorderReady();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                mChronometer.start();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            Toast.makeText(SafePlaceActivity.this, R.string.recording_started, Toast.LENGTH_LONG).show();
        } else {

            requestPermission();

        }


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        SharedPreferences navTagPref = getSharedPreferences("Tag", MODE_PRIVATE);
        Type = navTagPref.getString("NavigationTag", "");


        //recordButton = (Button) findViewById(R.id.recordButton);


        tvReturnToTheGuide = (TextView) findViewById(R.id.tvReturnToTheGuide);
        tvReturnToTheGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Handle ur code here khaled
                //Done ya m3lm
                //hahaha
                //ahy el comments de el rw4a t7n heya el sbb en el activity b t crash :P
//                getFragmentManager().popBackStack();
                if (getFragmentManager().getBackStackEntryCount() == 0) {
                    SafePlaceActivity.this.finish();

                }


            }
        });
        ivStop = (ImageView) findViewById(R.id.imStop);
        ivPlay = (ImageView) findViewById(R.id.imPlay);
        ivPlay.setVisibility(View.GONE);
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ivPause.setVisibility(View.VISIBLE);
//                ivPlay.setVisibility(View.INVISIBLE);
//                mChronometer.start();
//                mChronometer.setBase(timeDifference + SystemClock.elapsedRealtime());


            }
        });


        ivPause = (ImageView) findViewById(R.id.imPause);
        ivPause.setVisibility(View.GONE);

        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ivPause.setVisibility(View.INVISIBLE);
//                ivPlay.setVisibility(View.VISIBLE);
//                timeDifference = 0;
//                mChronometer.stop();
//                timeDifference = mChronometer.getBase() - SystemClock.elapsedRealtime();


            }
        });

        ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
                Toast.makeText(SafePlaceActivity.this, R.string.txtRecordingstop, Toast.LENGTH_LONG).show();

            }
        });


        btnMarkSafe = (Button) findViewById(R.id.btnMarkSafe);
        btnMarkSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String message = fullName + " " + getApplicationContext().getResources().getString(R.string.user_informed_us_that_she_is_safe_now);
                    sendLocationViaSMS(message, contactNumber);
                    sendUserSituationToTheServer(lati, lngi, "safe");
                    SendEventGoogleAnalytics("Guide chat", "MarkSafe", "User is safe");

                } catch (RuntimeException stopException) {
                }


            }
        });
        btnCallTrustedContact = (Button) findViewById(R.id.btnCallVolunteer);
        btnCallTrustedContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfUserHasTrustedContact();
            }
        });

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        retrofitforauthentication = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://streetpal.org/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        btnNavigate = (Button) findViewById(R.id.btn_navigate);
        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nearest_place != null) {
                    try {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr=" + currentUserLocation.getLatitude() + "," + currentUserLocation.getLongitude() + "&daddr=" + nearest_place.latitude + "," + nearest_place.longitude));
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(SafePlaceActivity.this, getResources().getString(R.string.error_while_navigation), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Type == "hospital") {
                        SendEventGoogleAnalytics("Nearest Hospital", "SelectedFromSafePlaceActivity", "Couldn't find nearest Hospital ");

                        Toast.makeText(SafePlaceActivity.this, getResources().getString(R.string.we_cdnt_find_hospital_near_you), Toast.LENGTH_SHORT).show();
                    } else {
                        SendEventGoogleAnalytics("Nearest PoliceStation", "SelectedFromSafePlaceActivity", "Couldn't find nearest PoliceStation ");

                        Toast.makeText(SafePlaceActivity.this, getResources().getString(R.string.we_cdnt_find_police_station_near_you), Toast.LENGTH_SHORT).show();
                        btnNavigate.setEnabled(false);
                    }
                }
            }
        });


    }

    private void sendUserDestinationToGoogleAnalytics() {
        if (Type == "hospital") {
            SendEventGoogleAnalytics("Nearest Hospital", "SelectedFromSafePlaceActivity", "Nearest Hospital is shown to  the user");

        } else {
            SendEventGoogleAnalytics("Nearest PoliceStation", "SelectedFromSafePlaceActivity", "Nearest PoliceStation is shown to  the user");

        }

    }

    private void stopRecording() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mChronometer.stop();
            ivStop.setEnabled(false);
            Toast.makeText(SafePlaceActivity.this, R.string.txtRecordingstop, Toast.LENGTH_LONG).show();

        } catch (Exception e) {

        }
    }

    private void checkIfUserHasTrustedContact() {

        if (contactNumber == null) {
            askForContactPermission();

        } else {
            telIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactNumber));
            if (ContextCompat.checkSelfPermission(SafePlaceActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SafePlaceActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            } else {
                startActivity(telIntent);
            }
        }

    }

    public void askForContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                    builder.setTitle(R.string.txtcontactAccessNeeded);
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage(R.string.txtcontactAccessNeededmessage);//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    , MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    private void retrieveContactNumber() {


        // getting contacts ID

        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }


        cursorID.close();


        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();


        sendTrustedContact(contactName, contactNumber, email);


    }

    private void retrieveContactName() {


        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();


    }

    private void sendTrustedContact(final String contactName, final String contactNumber, String email) {
        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<ResponseBody> mService = mApi.addTrustedContact(new TrustedContact(contactName, contactNumber, email));
        mService.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {


                    SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(SafePlaceActivity.this);
                    mypreference.edit().putString("contactName", contactName).apply();
                    mypreference.edit().putString("contactNumber", contactNumber).apply();

                    telIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactNumber));
                    if (ContextCompat.checkSelfPermission(SafePlaceActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SafePlaceActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                    } else {
                        startActivity(telIntent);
                    }


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SafePlaceActivity.this, getApplicationContext().getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();

            }

        });
    }

    private void sendUserSituationToTheServer(double lati, double lngi, String severity) {
        ApiInterface mApi = retrofitforauthentication.create(ApiInterface.class);
        Call<ResponseBody> mService = mApi.sendUserSituation(new UserSituation(lati, lngi, severity));
        mService.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SafePlaceActivity.this, getApplicationContext().getResources().getString(R.string.marked_safe), Toast.LENGTH_LONG).show();
                    //    sendMessageToTrustedContact();
                    SendEventGoogleAnalytics("sendLocation", "E-mail", "E-mail sent");
                    Intent i = new Intent(SafePlaceActivity.this, HomeActivity.class);
                    startActivity(i);


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SafePlaceActivity.this, getApplicationContext().getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();

            }

        });

    }

    private void sendMessageToTrustedContact() {

        if (contactNumber != null) {

        } else {
            Toast.makeText(SafePlaceActivity.this, getApplicationContext().getResources().getString(R.string.marked_safe), Toast.LENGTH_LONG).show();
            Intent i = new Intent(SafePlaceActivity.this, HomeActivity.class);
            startActivity(i);
        }
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.
                PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            checkLocationPermission();

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            updateUserLocation();
        } else {
            createLocationRequest();

        }

    }

    private void updateUserLocation() {
        LatLng userLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        if (userMarker == null) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_sp_pin);

            userMarker = mMap.addMarker(new MarkerOptions()
                    .position(userLatLng)
                    .title(getApplicationContext().getResources().getString(R.string.mapyourLocation))
                    .icon(icon));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
        } else {
            userMarker.setPosition(userLatLng);
        }
        lati = mLastLocation.getLatitude();
        lngi = mLastLocation.getLongitude();
        if (Type == "hospital") {
            build_retrofit_for_hospital_and_get_response(Type, lati, lngi);
        } else {
            build_retrofit_for_police_and_get_response(Type, lati, lngi);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void createLocationRequest() {
        final LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        final PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.

                        checkLocationPermission();
                        if (ActivityCompat.checkSelfPermission(SafePlaceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(SafePlaceActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, SafePlaceActivity.this);

//                        Toast.makeText(SafePlaceActivity.this, R.string.txtSuccess, Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
//                            Toast.makeText(SafePlaceActivity.this, R.string.resolutionRequired, Toast.LENGTH_SHORT).show();
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    SafePlaceActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(SafePlaceActivity.this, R.string.txtSettingsChange, Toast.LENGTH_LONG).show();
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateUserLocation();
    }

    public void onClick(View view) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (Exception e) {
            // TODO: Handle the error.
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
//                showSearchedPlaceOnMap(place.getLatLng());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    //    private void showSearchedPlaceOnMap(LatLng latLng) {
//        if (placeMarker == null) {
//            if (Type == "hospital") {
//                placeMarker = mMap.addMarker(new MarkerOptions().position(latLng).
//                        icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital_pin)));
//            } else {
//                placeMarker = mMap.addMarker(new MarkerOptions().position(latLng).
//                        icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_police_pin)));
//            }
//        } else {
//            placeMarker.setPosition(latLng);
//        }
//
//        if (mLastLocation != null) {
//            LatLng userLatLng = new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude());
//            GoogleMapsUtilities.getAndDrawPath(this, mMap, userLatLng, latLng, false);
//        }
//
//
//    }
    private void showSearchedPlaceOnMap(LatLng latLng) {
        LatLng userLatLng = new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude());
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_sp_pin);

        userMarker = mMap.addMarker(new MarkerOptions().position(userLatLng).
                title(this.getResources().getString(R.string.mapyourLocation))
                .icon(icon));
        if (placeMarker == null) {
            if (Type == "hospital") {

                sendUserDestinationToGoogleAnalytics();
                if (currentUserLocation != null) {
                    placeMarker = mMap.addMarker(new MarkerOptions().position(latLng).
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital_pin)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
                }


            } else if (Type == "police") {

                sendUserDestinationToGoogleAnalytics();
                if (currentUserLocation != null) {
                    placeMarker = mMap.addMarker(new MarkerOptions().position(latLng).
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_police_pin)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                }
            }
        } else {
            placeMarker.setPosition(latLng);
        }

        if (currentUserLocation != null) {
            GoogleMapsUtilities.getAndDrawPath(this, mMap, userLatLng, latLng, false);
        }


    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(SafePlaceActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        mGoogleApiClient.connect();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    mGoogleApiClient.disconnect();

                }
            case RequestPermissionCode:
                if (grantResults.length > 0) {

                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        MediaRecorderReady();

                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                            mChronometer.start();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        mChronometer.stop();
                        ivStop.setEnabled(false);
                    }
                }

                break;


        }

    }


    @Override
    public void onBackPressed() {
        // do nothing.
    }


    private void build_retrofit_for_hospital_and_get_response(final String schoolText, double lat, double lon) {
        Log.i("latlng", lat + " , " + lon);

        String url = "https://maps.googleapis.com/maps/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface service = retrofit.create(ApiInterface.class);

        Call<Example> call = service.getNearbyPlaces(schoolText, lat + "," + lon, PROXIMITY_RADIUS);
        Log.i("latlng", lat + " , " + lon);
        Log.i("Type ", schoolText);


        call.enqueue(new Callback<Example>() {

            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {

                if (response.isSuccessful()) {
                    try {


                        //   mGoogleMap.clear();


                        // This loop will go through all the results and add marker on each location.
                        for (int i = 0; i < response.body().getResults().size(); i++) {


                            Log.d(" lat and long", response.body().getResults().get(i).getGeometry().getLocation().getLat() + " , " + response.body().getResults().get(i).getGeometry().getLocation().getLng());
                            Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
                            Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();
                            placeName = response.body().getResults().get(i).getName();
                            vicinity = response.body().getResults().get(i).getVicinity();
                            markerOptions = new MarkerOptions();
                            latLng = new LatLng(lat, lng);

                            LatLng userLatLng = new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude());
                            double distance = CalculationByDistance(userLatLng, latLng);

                            if (i == 0) {
                                closest_distance = distance;
                                nearest_place = latLng;
                            } else if (distance <= closest_distance) {
                                closest_distance = distance;
                                nearest_place = latLng;
                            }
                        }


                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital_pin);
                        markerOptions.position(nearest_place);
                        markerOptions.icon(icon);
                        markerOptions.title(placeName + " : " + vicinity);
                        mMap.addMarker(markerOptions);
                        Log.i("nearestPlace", nearest_place.toString());
                        showSearchedPlaceOnMap(nearest_place);


                    } catch (Exception e) {
                        Log.d("onResponse", "There is an error");
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(SafePlaceActivity.this, getResources().getString(R.string.we_cdnt_find_hospital_near_you), Toast.LENGTH_SHORT).show();

                }
            }


            @Override
            public void onFailure(Call<Example> call, Throwable t) {

            }


        });

    }

    private void build_retrofit_for_police_and_get_response(final String schoolText, double lat, double lon) {
        Log.i("latlng", lat + " , " + lon);

        String url = "https://maps.googleapis.com/maps/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface service = retrofit.create(ApiInterface.class);

        Call<Example> call = service.getNearbyPlaces(schoolText, lat + "," + lon, PROXIMITY_RADIUS);
        Log.i("latlng", lat + " , " + lon);
        Log.i("Type ", schoolText);


        call.enqueue(new Callback<Example>() {

            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {

                if (response.isSuccessful()) {
                    try {


                        //   mGoogleMap.clear();


                        // This loop will go through all the results and add marker on each location.
                        for (int i = 0; i < response.body().getResults().size(); i++) {


                            Log.d(" lat and long", response.body().getResults().get(i).getGeometry().getLocation().getLat() + " , " + response.body().getResults().get(i).getGeometry().getLocation().getLng());
                            Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
                            Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();
                            placeName = response.body().getResults().get(i).getName();
                            vicinity = response.body().getResults().get(i).getVicinity();
                            markerOptions = new MarkerOptions();
                            latLng = new LatLng(lat, lng);

                            LatLng userLatLng = new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude());
                            double distance = CalculationByDistance(userLatLng, latLng);

                            if (i == 0) {
                                closest_distance = distance;
                                nearest_place = latLng;
                            } else if (distance <= closest_distance) {
                                closest_distance = distance;
                                nearest_place = latLng;
                            }
                        }


                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_police_pin);
                        markerOptions.position(nearest_place);
                        markerOptions.title(placeName + " : " + vicinity);
                        markerOptions.icon(icon);
                        mMap.addMarker(markerOptions);
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        Log.i("nearestPlace", nearest_place.toString());
                        showSearchedPlaceOnMap(nearest_place);


                    } catch (Exception e) {
                        Log.d("onResponse", "There is an error");
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SafePlaceActivity.this, getResources().getString(R.string.we_cdnt_find_police_station_near_you), Toast.LENGTH_SHORT).show();

                }
            }


            @Override
            public void onFailure(Call<Example> call, Throwable t) {


            }


        });

    }


    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }


    public void MediaRecorderReady() {

        mediaRecorder = new MediaRecorder();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        mediaRecorder.setOutputFile(AudioSavePathInDevice);

    }


    private void requestPermission() {

        ActivityCompat.requestPermissions(SafePlaceActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);

    }

    //
    public boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }


    public void sendLocationViaSMS(String status, String trustedContactNumber) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String message = status + " \n http://maps.google.com/maps?q=" + lati + "," + lngi + "&ll=" + lati + "," + lngi + "&z=17";
            ArrayList<String> messageParts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(trustedContactNumber, null, messageParts, null, null);
            Toast.makeText(SafePlaceActivity.this, getApplicationContext().getResources().getString(R.string.we_have_sent_mess_to_your_trusted_contact), Toast.LENGTH_LONG).show();
            SendEventGoogleAnalytics("sendLocation", "SMS", "SMS sent");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mLocationProvider.connect();

        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");
//        String language = Locale.getDefault().getLanguage();
//        setLanguage(language);
        checkLanguage(language);
    }

    private void checkLanguage(String languageToLoad) {

        switch (languageToLoad) {
            case "ar":
                setRTL();
                break;
            case "en":
                setLTR();
                break;
            default:
                setLTR();
        }
    }

    //
    @Override
    public void onResume() {
        super.onResume();
        mLocationProvider.connect();

        languagepref = getSharedPreferences("language", MODE_PRIVATE);
        language = languagepref.getString("languageToLoad", "");
//        String language = Locale.getDefault().getLanguage();
//        setLanguage(language);
        checkLanguage(language);
    }


    //
    public void setRTL() {
        String languageToLoad = "ar"; // rtl language Arabic
//        setLanguage(languageToLoad);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        //layout direction
        Bidi b = new Bidi(languageToLoad, Bidi.DIRECTION_DEFAULT_RIGHT_TO_LEFT);
        b.isRightToLeft();

    }

    public void setLTR() {
        String languageToLoad = "en"; // ltr language English
//        setLanguage(languageToLoad);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        //layout direction
        Bidi b = new Bidi(languageToLoad, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
        b.isLeftToRight();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecording();
    }

    @Override
    public void handleNewLocation(Location location) {
        currentUserLocation = location;
        LatLng userLatLng = new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude());
        if (userMarker == null) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_sp_pin);

            userMarker = mMap.addMarker(new MarkerOptions()
                    .position(userLatLng)
                    .title(getApplicationContext().getResources().getString(R.string.mapyourLocation))
                    .icon(icon));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
        } else {
            userMarker.setPosition(userLatLng);
        }
        lati = currentUserLocation.getLatitude();
        lngi = currentUserLocation.getLongitude();
        if (Type == "hospital") {
            build_retrofit_for_hospital_and_get_response(Type, lati, lngi);

        } else {
            build_retrofit_for_police_and_get_response(Type, lati, lngi);
        }
    }

    private void InitGoogleAnalytics() {
        mGoogleHelper = new GoogleAnalyticsHelper();
        mGoogleHelper.init(this);
    }

    private void SendScreenNameGoogleAnalytics() {

        mGoogleHelper.SendScreenNameGoogleAnalytics("SafePlaceActivity", this);
    }

    private void SendEventGoogleAnalytics(String iCategoryId, String iActionId, String iLabelId) {

        mGoogleHelper.SendEventGoogleAnalytics(this, iCategoryId, iActionId, iLabelId);
    }
}




