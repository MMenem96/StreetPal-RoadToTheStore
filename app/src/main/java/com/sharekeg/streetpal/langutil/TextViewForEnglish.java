package com.sharekeg.streetpal.langutil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Lmis on 8/7/2017.
 */

@SuppressLint("AppCompatCustomView")
public class TextViewForEnglish extends TextView {
    public TextViewForEnglish(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        isInEditMode();
    }

    public TextViewForEnglish(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewForEnglish(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "Roboto-Regular.ttf");
        setTypeface(tf, 1);

    }
}
