package com.radicalninja.filedialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.util.Observable;

public class ImageDialog extends Observable {

    private Bitmap mBitmap;
    private Context mContext;

    protected class ImagePayload {
        protected boolean isPositive;
        protected Bitmap bitmap;

        public ImagePayload(boolean isPositive, Bitmap bitmap) {
            this.isPositive = isPositive;
            this.bitmap = bitmap;
        }
    }

    public ImageDialog(Context context, String targetFilePath) {

        mContext = context;
        mBitmap = BitmapFactory.decodeFile(targetFilePath);
    }

    public ImageDialog(Context context, File targetFile) {

        mContext = context;
        mBitmap = BitmapFactory.decodeFile(targetFile.getAbsolutePath());
    }

    public ImageDialog(Context context, Bitmap bitmap) {

        mContext = context;
        mBitmap = bitmap;
    }

    public boolean isBitmapLoaded() {

        return (mBitmap != null);
    }

    // TODO: Finish up this method with positive and negative buttons.
    public Dialog showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        // Create image view
        LinearLayout wrapperView = new LinearLayout(mContext);
        LinearLayout.LayoutParams fillParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        wrapperView.setLayoutParams(fillParams);
        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(fillParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setAdjustViewBounds(true);
        imageView.setImageBitmap(mBitmap);
        wrapperView.addView(imageView);
        // Add image to dialog and display
        builder.setView(wrapperView);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ImageDialog.this.notifyObservers(new ImagePayload(true, mBitmap));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ImageDialog.this.notifyObservers(new ImagePayload(false, null));
            }
        });
        this.setChanged();  // For the observer
        return builder.show();
    }
}
