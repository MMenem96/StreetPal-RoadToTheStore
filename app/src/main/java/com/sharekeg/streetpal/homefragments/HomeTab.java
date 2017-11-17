package com.sharekeg.streetpal.homefragments;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sharekeg.streetpal.R;
import com.sharekeg.streetpal.chatcomponents.UserGuide;

import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeTab extends Fragment implements View.OnClickListener {
    private static final int SEND_SMS_PERMISSIONS_REQUEST = 64;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 984;
    Bundle bundle = new Bundle();
    private ImageView imageView1, imageView2, imageView3;
    private View myFragmentView;
    private TextView tvWelcomeUser, tvHint, textView1, textView2, textView3;
    private Context context;
    private RelativeLayout firstCardLayout, secondCardLayout, thirdCardLayout;
    private String userName;

    public HomeTab() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                new android.app.AlertDialog.Builder(getActivity())
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


    private void EnableLocation() {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
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
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getContext();
        myFragmentView = inflater.inflate(R.layout.fragment_home_tab, container, false);


        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (mypreference.getBoolean("loggedIn", false)) {
            userName = mypreference.getString("myUserName", "User Name");
        }

        tvWelcomeUser = (TextView) myFragmentView.findViewById(R.id.tv_welcome_user);

        tvWelcomeUser.setText(getContext().getResources().getString(R.string.Welcome) + " " + userName);
        tvHint = (TextView) myFragmentView.findViewById(R.id.hint);
        textView1 = (TextView) myFragmentView.findViewById(R.id.textView1);
        textView2 = (TextView) myFragmentView.findViewById(R.id.textView2);
        textView3 = (TextView) myFragmentView.findViewById(R.id.textView3);
        imageView1 = (ImageView) myFragmentView.findViewById(R.id.imageView1);
        imageView2 = (ImageView) myFragmentView.findViewById(R.id.imageView2);
        imageView3 = (ImageView) myFragmentView.findViewById(R.id.imageView3);

        firstCardLayout = (RelativeLayout) myFragmentView.findViewById(R.id.first_card);
        firstCardLayout.setOnClickListener(this);

        secondCardLayout = (RelativeLayout) myFragmentView.findViewById(R.id.second_card);
        secondCardLayout.setOnClickListener(this);

        thirdCardLayout = (RelativeLayout) myFragmentView.findViewById(R.id.third_card);
        thirdCardLayout.setOnClickListener(this);


        return myFragmentView;
    }

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(getActivity(), permission);
        return checkPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void startStreetPalGuide() {

        //Starting the street pal fragment when user click call for help button

        StreetPalGuide streetPalGuideFragment = new StreetPalGuide();
        streetPalGuideFragment.setArguments(bundle);
        this.getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.rlFragments, streetPalGuideFragment)
                .commit();

    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_card:
                bundle.putInt("userCase", UserGuide.USER_FEELS_FOLLOWED);
                startStreetPalGuide();
             //   SendEventGoogleAnalytics("HomeTab","btnUserFeel","Hospital selected from maptab" );


                break;
            case R.id.second_card:
                bundle.putInt("userCase", UserGuide.USER_FEELS_IN_DANGER);
                startStreetPalGuide();

                break;
            case R.id.third_card:
                startListOfChoicesFragment();
                break;

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case SEND_SMS_PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;
            case MY_PERMISSIONS_REQUEST_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                    }

                } else {

                }
                break;
        }
    }

    private void startListOfChoicesFragment() {


        ListOfChoices listOfChoicesFragment = new ListOfChoices();
        this.getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.rlFragments, listOfChoicesFragment)
                .commit();


    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.
                PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            checkLocationPermission();


        } else {
            EnableLocation();

        }
        if (checkPermission(Manifest.permission.SEND_SMS)) {

        } else {


            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSIONS_REQUEST);
        }

    }
}