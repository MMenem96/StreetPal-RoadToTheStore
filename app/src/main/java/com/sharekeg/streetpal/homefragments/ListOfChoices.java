package com.sharekeg.streetpal.homefragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sharekeg.streetpal.R;
import com.sharekeg.streetpal.chatcomponents.UserGuide;

/**
 * Created by MMenem on 8/10/2017.
 */

public class ListOfChoices extends Fragment implements View.OnClickListener {
    Bundle bundle = new Bundle();

    private View myFragmentView;
    private TextView tvHint, tvWelcomeUser, textView1, textView2, textView3, textView4, textView5;
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5;
    private RelativeLayout firstCardLayout, secondCardLayout, thirdCardLayout, fourthCardLayout, fifthCardLayout;

    public ListOfChoices() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_list_of_choices, container, false);

//        tvWelcomeUser = (TextView) myFragmentView.findViewById(R.id.tv_welcome_user);
//        tvHint = (TextView) myFragmentView.findViewById(R.id.hint);
//        textView1 = (TextView) myFragmentView.findViewById(R.id.textView1);
//        textView2 = (TextView) myFragmentView.findViewById(R.id.textView2);
//        textView3 = (TextView) myFragmentView.findViewById(R.id.textView3);
//        textView4 = (TextView) myFragmentView.findViewById(R.id.textView4);
//        textView5 = (TextView) myFragmentView.findViewById(R.id.textView5);
//
//        imageView1 = (ImageView) myFragmentView.findViewById(R.id.imageView1);
//        imageView2 = (ImageView) myFragmentView.findViewById(R.id.imageView2);
//        imageView3 = (ImageView) myFragmentView.findViewById(R.id.imageView3);
//        imageView4 = (ImageView) myFragmentView.findViewById(R.id.imageView3);
//        imageView5 = (ImageView) myFragmentView.findViewById(R.id.imageView3);


        firstCardLayout = (RelativeLayout) myFragmentView.findViewById(R.id.first_card_from_list);
        firstCardLayout.setOnClickListener(this);

        secondCardLayout = (RelativeLayout) myFragmentView.findViewById(R.id.second_card_from_list);
        secondCardLayout.setOnClickListener(this);

        thirdCardLayout = (RelativeLayout) myFragmentView.findViewById(R.id.third_card_from_list);
        thirdCardLayout.setOnClickListener(this);

        fourthCardLayout = (RelativeLayout) myFragmentView.findViewById(R.id.fourth_card_from_list);
        fourthCardLayout.setOnClickListener(this);

        fifthCardLayout = (RelativeLayout) myFragmentView.findViewById(R.id.fifth_card_from_list);
        fifthCardLayout.setOnClickListener(this);


        return myFragmentView;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_card_from_list:
                bundle.putInt("userCase", UserGuide.USER_HARASSED_WHAT_TO_DO);
                startStreetPalGuide();

                break;
            case R.id.second_card_from_list:
                bundle.putInt("userCase", UserGuide.USER_IN_POLICE_STATION);
                startStreetPalGuide();

                break;
            case R.id.third_card_from_list:
                bundle.putInt("userCase", UserGuide.USER_FILED_REPORT);
                startStreetPalGuide();
                break;
            case R.id.fourth_card_from_list:
                bundle.putInt("userCase", UserGuide.USER_IS_INJURED);
                startStreetPalGuide();
                break;
            case R.id.fifth_card_from_list:
                bundle.putInt("userCase", UserGuide.USER_NEEDS_TO_TALK);
                startStreetPalGuide();
                break;

        }
    }


    private void startStreetPalGuide() {

        //Starting StreetPal fragment when user click call for help button

        StreetPalGuide streetPalGuideFragment = new StreetPalGuide();
        streetPalGuideFragment.setArguments(bundle);
        this.getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.rlFragments, streetPalGuideFragment)
                .commit();

    }


}
