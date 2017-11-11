package com.sharekeg.streetpal.homefragments;

import android.content.Context;
import android.content.pm.LabeledIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharekeg.streetpal.R;

/**
 * Created by Lmis on 7/27/2017.
 */

public class viewPagerAdapter extends PagerAdapter {
    private int[] Cases={R.string.static_guide_case1,R.string.static_guide_case2,R.string.static_guide_case3,
            R.string.static_guide_case4,R.string.static_guide_case5,R.string.static_guide_case6};
    private int[] instructions={R.string.static_guide_case1_instructions,R.string.static_guide_case2_instructions
            ,R.string.static_guide_case3_instructions,R.string.static_guide_case4_instructions,
            R.string.static_guide_case5_instructions,R.string.static_guide_case6_instructions};
    private Context context;
    private LayoutInflater layoutinflater;

    public viewPagerAdapter (Context ctx){
        this.context=ctx;
    }

    @Override
    public Object instantiateItem(ViewGroup container,int position){
        layoutinflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view=layoutinflater.inflate(R.layout.static_guide_case1,container,false);
        TextView cases=(TextView)item_view.findViewById(R.id.tvQs);
        TextView instruc=(TextView)item_view.findViewById(R.id.tvinsts);
        cases.setText((context.getResources().getString(Cases[position])));
        instruc.setText((context.getResources().getString(instructions[position])));
        container.addView(item_view);
        return item_view;
    }
    @Override
    public void destroyItem(ViewGroup container,int position,Object object){
        container.removeView((FrameLayout)object);
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(FrameLayout)object);
    }
}
