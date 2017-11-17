package com.sharekeg.streetpal.langutil;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Lmis on 8/7/2017.
 */

public class ButtonForEnglish extends Button {
    public ButtonForEnglish(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        isInEditMode();
    }

    public ButtonForEnglish(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonForEnglish(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "Roboto-Regular.ttf");
        setTypeface(tf, 1);

    }
}
