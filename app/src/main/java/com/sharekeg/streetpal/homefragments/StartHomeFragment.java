package com.sharekeg.streetpal.homefragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sharekeg.streetpal.R;

/**
 * Created by MMenem on 8/21/2017.
 */

public class StartHomeFragment extends Fragment {

    private ImageButton IV_message;
    private TextView hello, And, tvEducate;
    public StartHomeFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View startHomeFragmentView = inflater.inflate(R.layout.fragment_start_home, container, false);


        IV_message = (ImageButton) startHomeFragmentView.findViewById(R.id.IV_message);

        IV_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHomeTabFragment();
            }
        });
        hello = (TextView) startHomeFragmentView.findViewById(R.id.hello);
        And = (TextView) startHomeFragmentView.findViewById(R.id.And);
        tvEducate = (TextView) startHomeFragmentView.findViewById(R.id.tvEducate);
        return startHomeFragmentView;
    }

    private void startHomeTabFragment() {
        HomeTab homeTabFragment = new HomeTab();
        this.getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.rlFragments, homeTabFragment)
                .commit();
    }







}
