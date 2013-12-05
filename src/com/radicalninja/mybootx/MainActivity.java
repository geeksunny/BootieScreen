package com.radicalninja.mybootx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Button buttonTest = (Button) findViewById(R.id.button_test);
		final ImageView view = (ImageView) findViewById(R.id.imageView1);
		final EditText inputText = (EditText) findViewById(R.id.editText1);
		
		// Get the AssetManager
		final AssetManager manager = getAssets();
		// Read a bitmap from Assets
		try {
			InputStream open = manager.open("white_test.png");
			final Bitmap bitmap = BitmapFactory.decodeStream(open);
			// Assign the bitmap to an ImageView in this Layout
			view.setImageBitmap(bitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}

		buttonTest.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Starting Test!", Toast.LENGTH_SHORT).show();

				try {
					InputStream open = manager.open("white_test.png");
					Bitmap target = BitmapFactory.decodeStream(open);
					Bitmap mTarget = target.copy(target.getConfig(), true);
					
					Canvas scratch = new Canvas(mTarget);
					Paint paint = new Paint();

					paint.setColor(Color.RED);
					paint.setTextSize(48f);	//12 pt?
					paint.setTypeface(Typeface.SANS_SERIF);
					paint.setAntiAlias(true);
					paint.setTextAlign(Paint.Align.CENTER);

					// Use math here to determine how big the text printing will be, and thus where the middle ground on where to print it is.
					float x = mTarget.getWidth() / 2;
					float y = mTarget.getHeight() * 0.8f;
					
					// Print the text to the photo, then display on the imageView.
					scratch.drawText(inputText.getText().toString(), x, y, paint);
					view.setImageBitmap(mTarget);
					
					// Write out to a file on external storage
					Log.i("PNG_SAVE", "FilesDir: "+ getFilesDir());	//-/data/data/com.radicalninja.mybootx/files
					Log.i("PNG_SAVE", "ExternalFilesDir: "+ getExternalFilesDir(null));
					File file = new File(getFilesDir(), "DemoFile.png");
					try {
						OutputStream os = new FileOutputStream(file);
						mTarget.compress(Bitmap.CompressFormat.PNG, 100, os);
						os.flush();
						os.close();
						Log.i("PNG_SAVE", "PNG BITMAP SAVED TO SD CARD.");
						Log.i("Hub", "height = "+ mTarget.getHeight() + ", width = " + mTarget.getWidth());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						Log.i("PNG_SAVE", "FileNotFoundException: "+ e.toString());
					} catch (IOException e) {
						e.printStackTrace();
						Log.i("PNG_SAVE", "IOException: "+ e.toString());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				Toast.makeText(getApplicationContext(), "Test Over!", Toast.LENGTH_SHORT).show();
			}
		});

}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
