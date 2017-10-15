package com.example.srv_twry.studentcompanion.Utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;

import com.example.srv_twry.studentcompanion.R;

/**
 * Created by rohan on 16/10/17.
 */


public class CustomTextView extends AppCompatTextView {
    private static final String TAG = "CustomTextView";
    /*
     * Caches typefaces based on their file path and name, so that they don't have to be created
     * every time when they are referenced.
     */
    public static Map<String, Typeface> mTypefaces;

    public CustomTextView(Context context) {
        super(context);
        setIncludeFontPadding(false);

    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
        setIncludeFontPadding(false);

    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
        setIncludeFontPadding(false);

    }



    private void setCustomFont(Context ctx, AttributeSet attrs) {
        if (mTypefaces == null)
            mTypefaces = new HashMap<>();

        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        String customFont;
        if (a.hasValue(R.styleable.CustomTextView_customFont))
            customFont = a.getString(R.styleable.CustomTextView_customFont);
        else
            customFont = "Roboto-Regular.ttf";
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public void setCustomFont(Context ctx, String path) {
        Typeface typeface;
        if (mTypefaces.containsKey(path)) {
            typeface = mTypefaces.get(path);
        } else {
            typeface = Typeface.createFromAsset(ctx.getAssets(), path);
            mTypefaces.put(path, typeface);
        }
        setTypeface(typeface);
    }
}
