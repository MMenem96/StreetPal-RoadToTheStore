package com.sharekeg.streetpal;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Lmis on 8/7/2017.
 */

public class EditTextForArabic extends EditText{
    public EditTextForArabic(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        isInEditMode();
    }

    public EditTextForArabic(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextForArabic(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "cocon-light.ttf");
        setTypeface(tf, 1);

    }
}
