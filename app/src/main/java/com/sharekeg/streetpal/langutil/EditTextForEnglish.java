package com.sharekeg.streetpal.langutil;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Lmis on 8/7/2017.
 */

public class EditTextForEnglish extends EditText {
    public EditTextForEnglish(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        isInEditMode();
    }

    public EditTextForEnglish(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextForEnglish(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "Roboto-Regular.ttf");
        setTypeface(tf, 1);

    }
}
