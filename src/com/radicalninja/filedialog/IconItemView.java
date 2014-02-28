package com.radicalninja.filedialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IconItemView extends LinearLayout {

    // TODO: Move a lot of this stuff into an xml layout file when this code gets moved into a library project.
    private static final int PADDING_PARENT = 25;
    private static final int PADDING_CHILDREN = 10;
    // TODO: Adjust bounds and gravity of both the icon and the label to align them properly with one another.
    private static final int FONT_SIZE_ICON = 20;
    private static final int FONT_SIZE_LABEL = 14;
    protected FontIcon mIconView;
    protected TextView mLabelView;
    private Context mContext;

    public static class IconItem {
        public static enum IconType {
            CURRENT_DIRECTORY(-1), PARENT_DIRECTORY(0), DIRECTORY(1), FILE(2), IMAGE(3);
            public static final int size = values().length;
            private final int value;
            private IconType(int value) {
                this.value = value;
            }
            public int getValue() {
                return value;
            }
        }
        public IconType type;
        public String label;
        public IconItem(String label, IconType type) {
            this.label = label;
            this.type = type;
        }
    }

    public IconItemView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public IconItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public IconItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    private void init() {
        // Icon view
        mIconView = new FontIcon(mContext);
        mIconView.setPadding(PADDING_CHILDREN, PADDING_CHILDREN, PADDING_CHILDREN, PADDING_CHILDREN);
        mIconView.setTextSize(FONT_SIZE_ICON);
        // Label view
        mLabelView = new TextView(mContext);
        mLabelView.setPadding(PADDING_CHILDREN, PADDING_CHILDREN, PADDING_CHILDREN, PADDING_CHILDREN);
        mLabelView.setTextSize(FONT_SIZE_LABEL);
        // Parent
        this.setPadding(PADDING_PARENT, PADDING_PARENT, PADDING_PARENT, PADDING_PARENT);
        this.addView(mIconView);
        this.addView(mLabelView);
    }

    public void bind(IconItem item) {
        mLabelView.setText(item.label);
        switch (item.type) {
            case CURRENT_DIRECTORY:
                mIconView.setIcon("folder-open");
                break;
            case PARENT_DIRECTORY:
                mIconView.setIcon("folder-close");
                break;
            case DIRECTORY:
                mIconView.setIcon("folder-close-alt");
                break;
            case FILE:
                mIconView.setIcon("file-alt");
                break;
            case IMAGE:
                mIconView.setIcon("picture");
                break;
        }
    }
}
