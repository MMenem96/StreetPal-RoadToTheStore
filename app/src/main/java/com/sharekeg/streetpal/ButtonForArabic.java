package com.sharekeg.streetpal;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Lmis on 8/7/2017.
 */

public class ButtonForArabic extends Button {
    public ButtonForArabic(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        isInEditMode();
    }

    public ButtonForArabic(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonForArabic(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "cocon-light.ttf");
        setTypeface(tf, 1);

    }
}
