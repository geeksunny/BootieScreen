package com.radicalninja.mybootx;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	final MainActivity parent = this;
	
	DrawerLayout drawerLayout;
	FrameLayout leftDrawer;
	ImageView previewView;
	Bootscreen bootscreen;
	
	EditText inputMessage;
	SeekBar inputFontSize;
	TextView textFontSizeValue;
	Spinner inputColor;
	Spinner inputTypeface;
	Button buttonPreview;
	Button buttonSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout);
		// Interface bindings
		LayoutInflater factory = getLayoutInflater();
		FrameLayout contentFrame = (FrameLayout) findViewById(R.id.content_frame);
		View bootControls = factory.inflate(R.layout.boot_controls, null);
		contentFrame.addView(bootControls);
		leftDrawer = (FrameLayout) findViewById(R.id.left_drawer);
		View bootPreview = factory.inflate(R.layout.boot_preview, null);
		leftDrawer.addView(bootPreview);
		// Spinner lists and other controls
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		previewView = (ImageView) findViewById(R.id.imagePreview);
		inputMessage = (EditText) findViewById(R.id.inputMessage);
		// - Font Size Selection
		inputFontSize = (SeekBar) findViewById(R.id.inputFontSize);
		textFontSizeValue = (TextView) findViewById(R.id.textFontSizeValue);
		inputFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				textFontSizeValue.setText(String.format("%ddp", (int) parent.getFontSize() ));
			}
		});
		// - Color Selection
		inputColor = (Spinner) findViewById(R.id.inputColor);
		ArrayAdapter<CharSequence> adapterColor = ArrayAdapter.createFromResource(this, R.array.inputColor, android.R.layout.simple_spinner_item);
		adapterColor.setDropDownViewResource(android.R.layout.simple_spinner_item);
		inputColor.setAdapter(adapterColor);
		// - Typeface Selection
		inputTypeface = (Spinner) findViewById(R.id.inputTypeface);
		ArrayAdapter<CharSequence> adapterTypeface = ArrayAdapter.createFromResource(this, R.array.inputTypeface, android.R.layout.simple_spinner_item);
		adapterTypeface.setDropDownViewResource(android.R.layout.simple_spinner_item);
		inputTypeface.setAdapter(adapterTypeface);
		// - Preview Button
		buttonPreview = (Button) findViewById(R.id.buttonPreview);
		buttonPreview.setOnClickListener(previewButtonClicked);
		// - Save Button
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(saveButtonClicked);
		// Start off with the default image. //TODO: THIS WILL PULL FROM DEVICE LATER.
		loadImage();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * OnClickListener for the Preview button
	 */
	OnClickListener previewButtonClicked = new OnClickListener() {
		public void onClick(View v) {
			// Just for debug purposes, a Toast!
			//Toast.makeText(getApplicationContext(), "Starting Test!", Toast.LENGTH_SHORT).show();

			// Reset the bootscreen to its original state for a new preview.
			bootscreen.resetBitmap();
			// Update settings for the bootscreen.
			bootscreen.setTextSize(getFontSize());
			bootscreen.setColor(getColor());
			bootscreen.setTypeface(getTypeface());
			// Write to the bootscreen.
			bootscreen.doPersonalization(inputMessage.getText().toString());
			// Update the preview.
			previewView.setImageBitmap(bootscreen.getBitmap());
			// Open the preview pane.
			drawerLayout.openDrawer(leftDrawer);
		}
	};
	
	/**
	 * OnClickListener for the Save button
	 */
	OnClickListener saveButtonClicked = new OnClickListener() {
		public void onClick(View v) {
			bootscreen.saveBitmap(getApplicationContext());
			drawerLayout.closeDrawers();
			Toast.makeText(getApplicationContext(), "Bootscreen saved to SD card!", Toast.LENGTH_SHORT).show();
			Log.i("MainActivity", "Saved bitmap!");
		}
	};
	
	/**
	 * Get float value of current selected font size.
	 */
	private float getFontSize() {
		int fontSize = inputFontSize.getProgress() + 36;
		return (float) fontSize;
	}
	
	/**
	 * Get integer value for the current selected color
	 */
	private int getColor() {
		Map<String, Integer> colorMap = new HashMap<String, Integer>();
		colorMap.put("BLACK", Color.BLACK);
		colorMap.put("BLUE", Color.BLUE);
		colorMap.put("CYAN", Color.CYAN);
		colorMap.put("DKGRAY", Color.DKGRAY);
		colorMap.put("GRAY", Color.GRAY);
		colorMap.put("GREEN", Color.GREEN);
		colorMap.put("LTGRAY", Color.LTGRAY);
		colorMap.put("MAGENTA", Color.MAGENTA);
		colorMap.put("RED", Color.RED);
		colorMap.put("WHITE", Color.WHITE);
		colorMap.put("YELLOW", Color.YELLOW);
		
		return colorMap.get((String) inputColor.getAdapter().getItem(inputColor.getSelectedItemPosition()));
	}
	

	/**
	 * Get integer value for the current selected typeface
	 */
	private Typeface getTypeface() {
		Map<String, Typeface> typefaceMap = new HashMap<String, Typeface>();
		typefaceMap.put("DEFAULT", Typeface.DEFAULT);
		typefaceMap.put("DEFAULT_BOLD", Typeface.DEFAULT_BOLD);
		typefaceMap.put("MONOSPACE", Typeface.MONOSPACE);
		typefaceMap.put("SANS_SERIF", Typeface.SANS_SERIF);
		typefaceMap.put("SERIF", Typeface.SERIF);
		
		return typefaceMap.get((String) inputTypeface.getAdapter().getItem(inputTypeface.getSelectedItemPosition()));
	}

	/**
	 * Loads the default[debug] boot image from the app's assets folder into memory / the preview pane.
	 */
	private void loadImage() {
		// Get the AssetManager
		final AssetManager manager = getAssets();
		// Read a bitmap from Assets
		try {
			InputStream open = manager.open("white.bmp");
			Bitmap bitmap = BitmapFactory.decodeStream(open);
			// Assign the bitmap to an ImageView in this Layout
			previewView.setImageBitmap(bitmap);
			// Load the bitmap into the Bootscreen object
			bootscreen = new Bootscreen(bitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the given {@link com.android.graphics.Bitmap} into memory / the preview pane.
	 * 
	 * @param	bitmap	The given Bitmap object to load.
	 */
	private void loadImage(Bitmap bitmap) {
		previewView.setImageBitmap(bitmap);
	}
	
	/**
	 * Handling the back-button when the preview pane is open.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (drawerLayout.isDrawerOpen(leftDrawer)) {
				drawerLayout.closeDrawers();
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
