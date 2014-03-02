package com.radicalninja.filedialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radicalninja.bootiescreen.R;

public class IconItemView extends LinearLayout {

    private static final int PADDING_PARENT = 25;
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
        // LinearLayout params
        this.setPadding(PADDING_PARENT, PADDING_PARENT, PADDING_PARENT, PADDING_PARENT);
        this.setOrientation(LinearLayout.HORIZONTAL);
        // Layout children.
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.filedialog_iconitemview, this, true);
        mIconView = (FontIcon) findViewById(R.id.item_icon);
        mLabelView = (TextView) findViewById(R.id.item_label);
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
