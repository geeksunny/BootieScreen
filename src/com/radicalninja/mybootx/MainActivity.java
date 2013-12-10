package com.radicalninja.mybootx;

import java.util.HashMap;
import java.util.Map;

import com.stericson.RootTools.RootTools;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout;
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

	// TODO Add code that checks build.prop for device name and force quit if user does not have Moto X (Or other possibly compatible devices, like the mini or G maybe?)
	final MainActivity parent = this;
	@SuppressWarnings("unused")
	private static final String LOG_TAG = "MainActivity";
	
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
			public void onStopTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				textFontSizeValue.setText(String.format("%ddp", (int) parent.getFontSize() ));
			}
		});
		// - Color Selection
		inputColor = (Spinner) findViewById(R.id.inputColor);
		ArrayAdapter<CharSequence> adapterColor = ArrayAdapter.createFromResource(this, R.array.inputColor, android.R.layout.simple_spinner_item);
		adapterColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		inputColor.setAdapter(adapterColor);
		// - Typeface Selection
		inputTypeface = (Spinner) findViewById(R.id.inputTypeface);
		ArrayAdapter<CharSequence> adapterTypeface = ArrayAdapter.createFromResource(this, R.array.inputTypeface, android.R.layout.simple_spinner_item);
		adapterTypeface.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		inputTypeface.setAdapter(adapterTypeface);
		// - Preview Button
		buttonPreview = (Button) findViewById(R.id.buttonPreview);
		buttonPreview.setOnClickListener(previewButtonClicked);
		// - Save Button
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(saveButtonClicked);
		// Start off with the DEVICE_BACKUP image, automatically pulling one if it does not exist.
		loadImage();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// TODO: Build out the settings of this app.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * OnClickListener for the Preview button
	 */
	OnClickListener previewButtonClicked = new OnClickListener() {
		public void onClick(View v) {

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
			
			// AlertDialogs for handling the result of the installation procedure.
			DialogInterface.OnClickListener handleInstallationOutcome = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						// YES (success)
						RootTools.restartAndroid();
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						// NO (Success)
						Toast.makeText(parent, "Installation SUCCESS!", Toast.LENGTH_SHORT).show();
						drawerLayout.closeDrawer(leftDrawer);
						break;
					case DialogInterface.BUTTON_NEUTRAL:
						// NEUTRAL (Failure)
						Toast.makeText(parent, "Installation FAILURE!", Toast.LENGTH_SHORT).show();
						drawerLayout.closeDrawer(leftDrawer);
						break;
					}
				}
			};
			// - SUCCESSFUL INSTALLTION AlertDialog
			final AlertDialog.Builder handleInstallationSuccess = new AlertDialog.Builder(parent);
			handleInstallationSuccess
				.setMessage("Your personalized bootscreen was successfully installed! Would you like to reboot now and test it out?")
				.setPositiveButton("Yes", handleInstallationOutcome)
				.setNegativeButton("No", handleInstallationOutcome);
			// - FAILED INSTALLATION AlertDialog
			final AlertDialog.Builder handleInstallationFailure = new AlertDialog.Builder(parent);
			handleInstallationFailure
				.setMessage("Unfortunately it looks like your bootscreen could not be installed! Check the logs and submit a bug report or try again later!")
				.setNeutralButton("Ok", handleInstallationOutcome);
			
			// AlertDialog for proceeding with the bootscreen installation.
			DialogInterface.OnClickListener doInstallation = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						// Yes button clicked
						bootscreen.installPersonalizedBootscreen(handleInstallationSuccess, handleInstallationFailure);
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						// No button clicked
						Toast.makeText(parent, "Installation was CANCELLED!", Toast.LENGTH_SHORT).show();
						drawerLayout.closeDrawer(leftDrawer);
						break;
					}
				}
			};
			AlertDialog.Builder doInstallationDialog = new AlertDialog.Builder(parent);
			doInstallationDialog
				.setMessage("This will write your personalized bootscreen to your phone's clogo block device. ARE YOU SURE YOU WANT TO DO THIS?")
				.setPositiveButton("Yes", doInstallation)
				.setNegativeButton("No", doInstallation)
				.show();
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
		// Load the bitmap into the Bootscreen object
		bootscreen = new Bootscreen(parent);
		// Assign the bitmap to an ImageView in this Layout
		previewView.setImageBitmap(bootscreen.getBitmap());
	}

	/**
	 * Loads the given {@link com.android.graphics.Bitmap} into memory / the preview pane.
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
