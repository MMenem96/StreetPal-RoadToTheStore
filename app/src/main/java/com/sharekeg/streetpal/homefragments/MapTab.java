package com.sharekeg.streetpal.homefragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sharekeg.streetpal.Androidversionapi.ApiInterface;
import com.sharekeg.streetpal.Home.HomeActivity;
import com.sharekeg.streetpal.Language.ChooseLanguage;
import com.sharekeg.streetpal.R;
import com.sharekeg.streetpal.googleanalytics.GoogleAnalyticsHelper;
import com.sharekeg.streetpal.safeplace.GoogleMapsUtilities;
import com.sharekeg.streetpal.safeplace.SafePlaceActivity;
import com.sharekeg.streetpal.safeplace.nearbyplaceutil.Example;
import com.sharekeg.streetpal.splashscreeen.SplashScreen;


import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapTab extends Fragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int RequestPermissionCode = 1;
    private static final String TAG = MapTab.class.getSimpleName();
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    private static final int RESULT_CANCELED = 14;
    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    double closest_distance = 0;
    Marker placeMarker;
    ArrayList<LatLng> pointList = new ArrayList<LatLng>();
    ProgressDialog mprogressDialog;
    Marker userMarker;
    private OnFragmentInteractionListener mListener;
    private int PROXIMITY_RADIUS = 5000;
    private String policeType = "police", hospitalType = "hospital";
    private LatLng nearest_place, latLng;
    private MarkerOptions markerOptions;
    private String placeName;
    private String vicinity;
    private ImageButton iBtnHospital, iBtnPolice;
    private String Type;
    private double lati;
    private double lngi;
    private LatLng userLatLng;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private Location mLastLocation;
    private Context context;
    private GoogleAnalyticsHelper mGoogleHelper;
    private Animation mEnterAnimation, mExitAnimation;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sAnalytics = GoogleAnalytics.getInstance(getActivity());
        InitGoogleAnalytics();
        SendScreenNameGoogleAnalytics();

        /* setup enter and exit animation */
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);
        context = getContext();
        mprogressDialog = new ProgressDialog(getActivity());

        if (checkPlayServices()) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)
                    .setFastestInterval(1 * 1000);
        }
        Log.d(TAG, "onCreate");
    }


    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_tab, container, false);
        iBtnPolice = (ImageButton) rootView.findViewById(R.id.image_button_police);
        iBtnPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNearstPoliceStation();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (nearest_place != null) {
                            showAlertDialougeToNavigate();
                            SendEventGoogleAnalytics("Nearest PoliceStation", "SelectedFromMapTab", "Nearest PoliceStation is shown to  the user");
                            mprogressDialog.dismiss();


                        } else {
                            Toast.makeText(context, getResources().getString(R.string.we_cdnt_find_police_station_near_you), Toast.LENGTH_SHORT).show();
                            SendEventGoogleAnalytics("Nearest PoliceStation", "SelectedFromMapTab", "Couldn't find nearest PoliceStation ");
                            mprogressDialog.dismiss();
                        }
                    }
                }, 2000);
            }
        });
        iBtnHospital = (ImageButton) rootView.findViewById(R.id.image_button_hospital);
        iBtnHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNearstHospital();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (nearest_place != null) {
                            SendEventGoogleAnalytics("Nearest Hospital", "SelectedFromMapTab", "Nearest Hospital is shown to  the user");
                            showAlertDialougeToNavigate();

                        } else {
                            SendEventGoogleAnalytics("Nearest Hospital", "SelectedFromMapTab", "Couldn't find nearest Hospital ");
                            Toast.makeText(context, getResources().getString(R.string.we_cdnt_find_hospital_near_you), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 2000);
            }
        });
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean previouslyStarted = prefs.getBoolean("map_tab_previously_started", false);
        if (!previouslyStarted)
        {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("map_tab_previously_started", Boolean.TRUE);
            edit.commit();
            runOverlay_ContinueMethod();
        } else {
        }
        Log.d(TAG, "onCreateView");
        return rootView;
    }

    private void showNearstHospital() {
        mprogressDialog = new ProgressDialog(getActivity());
        mprogressDialog.setMessage(getActivity().getApplicationContext().getString(R.string.maptab_progress_dialoge_hospital));
        mprogressDialog.setCancelable(true);
        mprogressDialog.show();
        Type = hospitalType;
        mGoogleMap.clear();
        build_retrofit_for_hospital_and_get_response(Type, lati, lngi);

    }

    private void showNearstPoliceStation() {
        mprogressDialog = new ProgressDialog(getActivity());
        mprogressDialog.setMessage(getActivity().getApplicationContext().getString(R.string.maptab_progress_dialoge_police));
        mprogressDialog.setCancelable(true);
        mprogressDialog.show();
        Type = policeType;
        mGoogleMap.clear();
        build_retrofit_for_police_and_get_response(Type, lati, lngi);
    }

    private void runOverlay_ContinueMethod(){
        // the return handler is used to manipulate the cleanup of all the tutorial elements
        ChainTourGuide tourGuide1 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setTitle("Nearest Police Station")
                        .setDescription("Tap here to find the nearest police station to you.")
                        .setGravity(Gravity.BOTTOM)
                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(iBtnPolice);

        ChainTourGuide tourGuide2 = ChainTourGuide.init(getActivity())
                .setToolTip(new ToolTip()
                        .setTitle("Nearest Hospital")
                        .setDescription("Tap here to find the nearest hospital to you.")
                        .setGravity(Gravity.BOTTOM | Gravity.LEFT)
//                        .setBackgroundColor(Color.parseColor("#c0392b"))
                )
                .setOverlay(new Overlay()
//                        .setBackgroundColor(Color.parseColor("#EE2c3e50"))
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                )
                .playLater(iBtnHospital);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2)
                .setDefaultOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.OVERLAY)
                .build();


        ChainTourGuide.init(getActivity()).playInSequence(sequence);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.
                PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            checkLocationPermission();


        } else {
            EnableLocation();

        }
        mGoogleApiClient.connect();
        mMapView.onResume();
        setUpMap();

        Log.d(TAG, "onResume");
    }

    private void setupMapIfNeeded() {
        if (mGoogleMap != null) {
            mMapView.getMapAsync(this);
        } else {
            buildGoogleApiClient();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        setUpMap();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        mMapView.onPause();
        try {
            MapStateManeger mgr = new MapStateManeger(getActivity());
            mgr.saveMapState(mGoogleMap);
        } catch (Exception e) {

        }

        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
//        SharedPreferences sharedpref = getActivity().getSharedPreferences("Markers", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedpref.edit();
//        editor.remove("Markers");
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();

        Log.d(TAG, "onLowMemory");
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            MapStateManeger mgr = new MapStateManeger(getActivity());
            mgr.saveMapState(mGoogleMap);
        } catch (Exception e) {

        }
//        Toast.makeText(getActivity(), "Map State has been save?", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.
                PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation == null)
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            else
                handleNewLocation(mLastLocation);


            Log.d(TAG, "onConnected");
        }

    }

    private void InitGoogleAnalytics() {
        mGoogleHelper = new GoogleAnalyticsHelper();
        mGoogleHelper.init(getContext());
    }

    private void SendScreenNameGoogleAnalytics() {

        mGoogleHelper.SendScreenNameGoogleAnalytics("MapTabFragment", getContext());
    }

    private void SendEventGoogleAnalytics(String iCategoryId, String iActionId, String iLabelId) {

        mGoogleHelper.SendEventGoogleAnalytics(getContext(), iCategoryId, iActionId, iLabelId);
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
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
                    if (ContextCompat.checkSelfPermission(getActivity(),
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
                break;


        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void handleNewLocation(Location location) {
        userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (userMarker == null) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_sp_pin);

            userMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(userLatLng)
                    .title(context.getResources().getString(R.string.mapyourLocation))
                    .icon(icon));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
        } else {
            userMarker.setPosition(userLatLng);
        }
        try {
            lati = mLastLocation.getLatitude();
            lngi = mLastLocation.getLongitude();
            if (Type == "hospital") {
                build_retrofit_for_hospital_and_get_response(Type, lati, lngi);
            } else {
                build_retrofit_for_police_and_get_response(Type, lati, lngi);
            }
        } catch (Exception e) {

        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        try {
            MapStateManeger mgr = new MapStateManeger(getActivity());
            CameraPosition position = mgr.getSavedCameraPosition();

            if (position != null) {
                CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
//            Toast.makeText(getActivity(), "entering Resume State", Toast.LENGTH_SHORT).show();
                mGoogleMap.moveCamera(update);

                mGoogleMap.setMapType(mgr.getSavedMapType());

            }
        } catch (Exception e) {

        }


    }

    private void showSearchedPlaceOnMap(LatLng latLng) {
        userLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_sp_pin);

        userMarker = mGoogleMap.addMarker(new MarkerOptions().position(userLatLng).
                title(getActivity().getResources().getString(R.string.mapyourLocation))
                .icon(icon));
        if (placeMarker == null) {
            if (Type == "hospital") {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    placeMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital_pin)));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
                }


            } else if (Type == "police") {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    placeMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_police_pin)));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                }
            }
        } else {
            placeMarker.setPosition(latLng);
        }

        if (mLastLocation != null) {
            LatLng userLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            GoogleMapsUtilities.getAndDrawPath(getActivity(), mGoogleMap, userLatLng, latLng, false);
        }


    }

    private void getLocation() {


        try {
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                handleNewLocation(mLastLocation);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });


    }

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(getActivity());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(getActivity(), resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(getActivity(),
                        R.string.device_not_supported, Toast.LENGTH_LONG)
                        .show();

            }
            return false;
        }
        return true;
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

                            LatLng userLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            double distance = CalculationByDistance(userLatLng, latLng);

                            if (i == 0) {
                                closest_distance = distance;
                                nearest_place = latLng;
                            } else if (distance <= closest_distance) {
                                closest_distance = distance;
                                nearest_place = latLng;
                            }
                            //  showAlertDialougeToNavigate();

                        }


                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital_pin);
                        markerOptions.position(nearest_place);
                        markerOptions.icon(icon);
                        markerOptions.title(placeName + " : " + vicinity);
                        mGoogleMap.addMarker(markerOptions);
                        Log.i("nearestPlace", nearest_place.toString());
                        showSearchedPlaceOnMap(nearest_place);

                        try {
                            mprogressDialog.dismiss();
                        } catch (Exception e) {

                        }
                        Toast.makeText(getActivity(), R.string.nearest_hospital, Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Log.d("onResponse", "There is an error");
                        e.printStackTrace();
                    }
                } else {
                    try {
                        mprogressDialog.dismiss();
                    } catch (Exception e) {

                    }
                    Toast.makeText(getActivity(), R.string.failed_to_get_hospital, Toast.LENGTH_SHORT).show();

                }
            }


            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                try {
                    mprogressDialog.dismiss();
                } catch (Exception e) {

                }
                Toast.makeText(getActivity(), R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();
            }


        });

    }

    private void showAlertDialougeToNavigate() {
        final AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());

        if (Type == "hospital") {
            adb.setTitle(getActivity().getResources().getString(R.string.navigate_to_nearest_hospital));
        } else {
            // adb.setIcon(R.drawable.st)
            adb.setTitle(getActivity().getResources().getString(R.string.navigate_to_nearest_police));

        }


        adb.setPositiveButton(this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                try {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&daddr=" + nearest_place.latitude + "," + nearest_place.longitude));
                    startActivity(intent);
                    dialog.dismiss();

                } catch (Exception e) {
                    Toast.makeText(context, getResources().getString(R.string.error_while_navigation), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();


                }


            }
        });


        adb.setNegativeButton(this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        adb.show();


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

                            LatLng userLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            double distance = CalculationByDistance(userLatLng, latLng);

                            if (i == 0) {
                                closest_distance = distance;
                                nearest_place = latLng;
                            } else if (distance <= closest_distance) {
                                closest_distance = distance;
                                nearest_place = latLng;
                            }

                            //  showAlertDialougeToNavigate();
                        }


                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_police_pin);
                        markerOptions.position(nearest_place);
                        markerOptions.title(placeName + " : " + vicinity);
                        markerOptions.icon(icon);
                        mGoogleMap.addMarker(markerOptions);
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        Log.i("nearestPlace", nearest_place.toString());
                        showSearchedPlaceOnMap(nearest_place);
                        try {
                            mprogressDialog.dismiss();
                        } catch (Exception e) {

                        }
                        Toast.makeText(getActivity(), R.string.nearest_police, Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Log.d("onResponse", "There is an error");
                        e.printStackTrace();
                    }
                } else {
//                    Toast.makeText(getActivity(), "Error : " + response.code(), Toast.LENGTH_SHORT).show();
                    try {
                        mprogressDialog.dismiss();
                    } catch (Exception e) {

                    }
                    Toast.makeText(getActivity(), R.string.failed_to_get_police, Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                try {
                    mprogressDialog.dismiss();
                } catch (Exception e) {

                }
                Toast.makeText(getActivity(), R.string.smthing_went_wrong, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);

        Log.d(TAG, "onLocationChanged");
    }

    private void setUpMap() {
        if (mGoogleMap == null) {
            mMapView.getMapAsync(this);
        }
    }

    private void drawMarker(LatLng point) {
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);

        // Setting a title for this marker
        markerOptions.title("Lat:" + point.latitude + "," + "Lng:" + point.longitude);

        // Adding marker on the Google Map
        mGoogleMap.addMarker(markerOptions);
    }

    private void EnableLocation() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }


    //
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}

