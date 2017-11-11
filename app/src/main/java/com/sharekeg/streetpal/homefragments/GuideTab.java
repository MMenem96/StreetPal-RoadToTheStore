package com.sharekeg.streetpal.homefragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.sharekeg.streetpal.Home.HomeActivity;
import com.sharekeg.streetpal.R;

import java.util.Timer;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.zip.Inflater;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class GuideTab extends Fragment {
    private ViewPager mviewpager;
    private viewPagerAdapter adapter;
    private View guideTabView;
    private static int currentPage=0;
    private static int numPages=5;


    private OnFragmentInteractionListener mListener;

    public GuideTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        guideTabView=inflater.inflate(R.layout.fragment_guide_tab,container,false);

        mviewpager=(ViewPager)guideTabView.findViewById(R.id.vpPager);
        adapter=new viewPagerAdapter(getContext());
        mviewpager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) guideTabView.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mviewpager, true);
//        CircleIndicator indicator = (CircleIndicator) mviewpager.findViewById(R.id.indicator);
//        indicator.setViewPager(mviewpager);
//        adapter.registerDataSetObserver(indicator.getDataSetObserver());
        // Inflate the layout for this fragment
        return guideTabView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
