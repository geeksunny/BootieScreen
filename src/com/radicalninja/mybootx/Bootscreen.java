package com.radicalninja.mybootx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.tam.image.BitmapEx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

public class Bootscreen extends Canvas {
	
	private Bitmap originalState;
	private Bitmap workingCopy;
	private Paint painter;
	private static final String LOG_TAG = "Bootscreen";

	public Bootscreen() {
		// TODO Auto-generated constructor stub
	}

	public Bootscreen(Bitmap bitmap) {
		//super(bitmap);
		// Backing up this bitmap for easy reverting.
		originalState = bitmap.copy(bitmap.getConfig(), false);
		// Creating a self-contained working copy Bitmap
		workingCopy = bitmap.copy(bitmap.getConfig(), true);
		this.setBitmap(workingCopy);
		// Creating the painter and initializing the default settings.
		painter = new Paint();
		painter.setAntiAlias(true);
		painter.setTextAlign(Paint.Align.CENTER);
		painter.setHinting(Paint.HINTING_ON);
		painter.setSubpixelText(true);
	}
	
	public Bitmap getBitmap() {
		return workingCopy;
	}

	public void resetBitmap() {
		workingCopy = originalState.copy(originalState.getConfig(), true);
		this.setBitmap(workingCopy);
	}

	public void setColor(int color) {
		painter.setColor(color);
	}

	public void setTextSize(float size) {
		painter.setTextSize(size);
	}
	
	public void setTypeface(Typeface typeface) {
		painter.setTypeface(typeface);
	}
	
	/**
	 * Personalize the workingCopy bitmap with the given message string.
	 * 
	 * @param message The given message string.
	 */
	public void doPersonalization(String message) {
		float x = this.getWidth() / 2;
		float y = this.getHeight() * 0.8f;
		
		this.drawText(message, x, y, painter);
	}
	
	public boolean saveBitmap(Context context) {
		// TODO: Implement this? https://github.com/kswlee/Android-BitmapEx
		File prefixDir = (context.getExternalFilesDir(null) != null) ? context.getExternalFilesDir(null) : context.getFilesDir();
		Log.i("Bootscreen", "File path: "+prefixDir.toString()+"/"+"DemoFile.bmp");
		/*
		File file = new File(prefixDir, "DemoFile.png");
		boolean success = false;
		try {
			OutputStream os = new FileOutputStream(file);
			workingCopy.compress(Bitmap.CompressFormat.PNG, 100, os);
			os.flush();
			os.close();
			success = true;
			Log.i("Bootscreen", "PNG BITMAP SAVED TO STORAGE.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.i("Bootscreen", "ERROR! FileNotFoundException: "+e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("Bootscreen", "ERROR! IOException: "+e.toString());
		}
		*/
		BitmapEx bmpEx = new BitmapEx(Bitmap.createBitmap(workingCopy));
		try {
			bmpEx.saveAsBMP(new FileOutputStream(prefixDir.toString()+"/"+"DemoFile.bmp"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean success = true;
		
		return success;
	}
	
}
