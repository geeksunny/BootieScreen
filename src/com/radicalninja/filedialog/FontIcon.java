package com.radicalninja.filedialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.radicalninja.bootiescreen.R;

import java.util.HashMap;
import java.util.Map;


public class FontIcon extends TextView {

    private static final String DEFAULT_ICON = "warning-sign";
    private static final String FONT_ASSET_FILENAME = "fontawesome-file-glyphs.ttf";
    private static final String LOG_TAG = "FontIcon";
    private static Map<String, String> mIconMap = new HashMap<String, String>();
    private static Typeface mFont;

    static {

        mIconMap.put("picture", "\ue600");
        mIconMap.put("folder-close", "\ue601");
        mIconMap.put("folder-open", "\ue602");
        mIconMap.put("file-alt", "\ue603");
        mIconMap.put("file", "\ue604");
        mIconMap.put("folder-close-alt", "\ue605");
        mIconMap.put("folder-open-alt", "\ue606");
        mIconMap.put("file-text", "\ue607");
        mIconMap.put("file2", "\ue608");
        mIconMap.put("warning-sign", "\ue609");
        mIconMap.put("circle-arrow-up", "\ue60a");
    }

    public FontIcon(Context context) {

        super(context);
        init(null);
    }

    public FontIcon(Context context, AttributeSet attrs) {

        super(context, attrs);
        init(attrs);
    }

    public FontIcon(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        readFont(getContext());
        this.setTypeface(mFont);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FontIcon);

        String targetIcon;
        try {
            targetIcon = a.getString(R.styleable.FontIcon_icon_name);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "TargetIcon is failed");
            targetIcon = DEFAULT_ICON;
        }
        setIcon(targetIcon);

        a.recycle();
    }

    private static void readFont(Context context) {

        if (mFont == null) {
            try {
                mFont = Typeface.createFromAsset(context.getAssets(), FONT_ASSET_FILENAME);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Could not get typeface: " + e.getMessage());
                mFont = Typeface.DEFAULT;
            }
        }
    }

    public void setIcon(String iconName) {

        String icon = mIconMap.get(iconName);
        if (icon == null) {
            Log.e(LOG_TAG, "iconName is failed.");
            icon = mIconMap.get(DEFAULT_ICON);
        }
        this.setText(icon);
    }
}
