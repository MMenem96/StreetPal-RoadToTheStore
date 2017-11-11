package com.sharekeg.streetpal;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Lmis on 8/7/2017.
 */

public class TextViewForArabic extends TextView {

    public TextViewForArabic(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        isInEditMode();
    }

    public TextViewForArabic(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewForArabic(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "cocon-light.ttf");
        setTypeface(tf, 1);

    }
}
