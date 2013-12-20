package com.radicalninja.mybootx;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Command;
import com.ultrasonic.android.image.bitmap.util.AndroidBmpUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

public class Bootscreen extends Canvas {
	
	private Context mContext;
	private ImageView mParentView;
	
	private Bitmap originalState;
	private Bitmap workingCopy;
	private Paint painter;
	private String filePrefixDirectory;
	
	private static final int BOOTSCREEN_RESOLUTION_WIDTH = 720;
	private static final int BOOTSCREEN_RESOLUTION_HEIGHT = 1280;
	private static final String FILENAME_DEVICE_BACKUP = "clogoDeviceBackup.bmp";
	private static final String FILENAME_WORKING_COPY = "clogo.bmp";
	private static final String LOG_TAG = "Bootscreen";
	
	/**
	 * Constructor method that takes the active application Context object and an ImageView that will be used to preview the Bootscreen. The Bitmaps are initialized to the DEVICE_BACKUP file.
	 * 
	 * @param context The current application Context object.
	 * @param parentView The parent ImageView object that the bootscreen will be used to preview with.
	 */
	public Bootscreen(Context context, ImageView parentView) {
		
		super();
		// Storing the application context & parent view variables.
		mContext = context;
		mParentView = parentView;
		// Setting the filePrefixDirectory variable.
		File prefixDir;
		try {
			prefixDir = mContext.getExternalFilesDir(null);
		} catch (java.lang.NullPointerException e) {
			Log.e(LOG_TAG, "The external storage folder could not be accessed! Make sure you aren't creating this Bootscreen from withing Activity.onCreate()!");
			Log.i(LOG_TAG, "External Storage State: "+Environment.getExternalStorageState());
			prefixDir = mContext.getFilesDir();
			//TODO: Bug-If the filePrefixDirectory is pointing at /data/data/..., all of my File objects that reference the raw filepath will fail with EACCES (Permission denied). Re-write these calls to respect permissions properly.
		}
		filePrefixDirectory = prefixDir.toString();
		// Backing up this bitmap for easy reverting.
		originalState = bitmapFromDeviceBackup(true);
		// Creating a self-contained working copy Bitmap
		setWorkingBitmap(originalState.copy(originalState.getConfig(), true));
		// Creating the painter and initializing the default settings.
		painter = new Paint();
		painter.setAntiAlias(true);
		painter.setTextAlign(Paint.Align.CENTER);
		painter.setHinting(Paint.HINTING_ON);
		painter.setSubpixelText(true);
	}
	
	/**
	 * Constructor method that accepts a Bitmap object. The originalState & workingCopy member Bitmap objects get set to this given Bitmap object.
	 * 
	 * @param bitmap The given bitmap object to initialize the canvas with.
	 * @param context The app's active Context object to utilize with member method operations.
	 * @param parentView The parent ImageView object that the bootscreen will be used to preview with.
	 */
	public Bootscreen(Bitmap bitmap, Context context, ImageView parentView) {
		
		super(bitmap);
		// Storing the application context & parent view variables.
		mContext = context;
		mParentView = parentView;
		// Setting the filePrefixDirectory variable.
		//TODO: Copy the try'catch from previous constructor.
		File prefixDir = (mContext.getExternalFilesDir(null) != null) ? mContext.getExternalFilesDir(null) : mContext.getFilesDir();
		filePrefixDirectory = prefixDir.toString();
		// Backing up this bitmap for easy reverting.
		originalState = bitmap.copy(bitmap.getConfig(), false);
		// Creating a self-contained working copy Bitmap
		setWorkingBitmap(bitmap.copy(bitmap.getConfig(), true));
		// Creating the painter and initializing the default settings.
		painter = new Paint();
		painter.setAntiAlias(true);
		painter.setTextAlign(Paint.Align.CENTER);
		painter.setHinting(Paint.HINTING_ON);
		painter.setSubpixelText(true);
	}
	
	/**
	 * Getter method for accessing the current workingCopy Bitmap object.
	 * @return Returns the bootscreen's workingCopy Bitmap object.
	 */
	public Bitmap getBitmap() {
		
		return workingCopy;
	}
	
	/**
	 * Resets the bootscreen's workingCopy Bitmap object to it's originalState.
	 */
	public void resetBitmap() {
		
		setWorkingBitmap(originalState.copy(originalState.getConfig(), true));
	}
	
	/**
	 * Set's the bootscreen's painter object's color to the given value.
	 * @param color An integer value for the Painter's color.
	 */
	public void setColor(int color) {
		
		painter.setColor(color);
	}
	
	/**
	 * Set's the Bootscreen's active bitmap to draw in to. If a parent ImageView object is set, redraw the view.
	 * 
	 * `Specify a bitmap for the canvas to draw into. All canvas state such as layers, filters, and the save/restore stack are reset with the exception of the current matrix and clip stack. Additionally, as a side-effect the canvas' target density is updated to match that of the bitmap.`
	 * 
	 * @param bitmap The given mutable Bitmap object. 
	 */
	@Override
	public void setBitmap(Bitmap bitmap) {
		
		super.setBitmap(bitmap);
		if (mParentView != null) {
			mParentView.setImageBitmap(bitmap);
		}
	}
	
	/**
	 * Set's the Bootscreen's active working copy bitmap object as well as the Bootscreen's active bitmap to draw in to. If a parent ImageView object is set, redraw the view.
	 * 
	 * @param bitmap The given mutable Bitmap object.
	 */
	public void setWorkingBitmap(Bitmap bitmap) {
		
		workingCopy = bitmap;
		super.setBitmap(workingCopy);
		if (mParentView != null) {
			mParentView.setImageBitmap(workingCopy);
		}
	}
	
	/**
	 * Set's a parent view object for self-refreshing, if desired.
	 * @param parent The givent parent view object.
	 */
	public void setParentView(ImageView parent) {
		
		mParentView = parent;
	}
	
	/**
	 * Set's the bootscreen's painter object's TextSize setting to the given value.
	 * @param size A float value for the Painter's TextSize.
	 */
	public void setTextSize(float size) {
		
		painter.setTextSize(size);
	}
	
	/**
	 * Set's the bootscreen's painter object's Typeface setting to the given Typeface object.
	 * @param typeface The given Typeface object for the Painter's Typeface.
	 */
	public void setTypeface(Typeface typeface) {
		
		painter.setTypeface(typeface);
	}
	
	/**
	 * Personalize the workingCopy bitmap with the given message string.
	 * @param message The given message string.
	 */
	public void doPersonalization(String message) {
		
		// Write's nothing if message is empty string.
		// Essentially treats a blank message as a "Reset back to DEVICE_BACKUP."
		if (message != "") {
			float x = this.getWidth() / 2;
			float y = this.getHeight() * 0.8f;
			
			this.drawText(message, x, y, painter);
		}
	}
	
	/**
	 * Personalize the workingCopy bitmap with the given message string and redraw the parent view.
	 * 
	 * @param message The given message string.
	 */
	public void doPersonalizationAndRedraw(String message) {
		
		doPersonalization(message);
		if (mParentView != null) {
			mParentView.setImageBitmap(workingCopy);
		}
	}
	
	/**
	 * Utilizes com.ultrasonic.android.image.bitmap.util to save a .bmp file to the SD card.
	 * @return Returns true on confirmed existence of the new .bmp file.
	 */
	public boolean saveBitmap() {
		
		// Delete any existing workingCopy .bmp file to ensure we are working with a clean slate.
		if (!deleteFileIfExists(FILENAME_WORKING_COPY)) {
			Log.e(LOG_TAG, "Could not delete an existing "+FILENAME_WORKING_COPY+" file.");
			return false;
		}
		// Compress and save workingCopy Bitmap to WORKING_COPY.
		AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
		boolean isSaveResult = bmpUtil.save(workingCopy, filePrefixDirectory+"/"+FILENAME_WORKING_COPY);
		if (isSaveResult && fileExists(FILENAME_WORKING_COPY)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Pulls the existing bootscreen data from your device.
	 * @param shouldOverwriteExistingBackup Boolean value on whether we should overwrite any existing DEVICE_BACKUP files or not.
	 * @return Returns a boolean true if the procedure succeeds without a hitch, and false if otherwise.
	 */
	public boolean pullBootscreenFromDevice(boolean shouldOverwriteExistingBackup) {
		
		// Handling the existing backup
		if (shouldOverwriteExistingBackup) {
			if (!deleteFileIfExists(FILENAME_DEVICE_BACKUP)) {
				Log.e(LOG_TAG, "For some reason we couldn't delete the existing DEVICE_BACKUP file! INVESTIGATE!!");
				return false;
			}
		} else {
			if (fileExists(FILENAME_DEVICE_BACKUP)) {
				Log.e(LOG_TAG, "An existing DEVICE_BACKUP image has been found and we asked to NOT overwrite anything!");
				return false;
			}
		}
		// Checking for Root access.
		if (RootTools.isAccessGiven()) {
			Log.i(LOG_TAG, "Root granted! About to pull bitmap...");
			String cmd = String.format("dd if=/dev/block/platform/msm_sdcc.1/by-name/clogo of=%s/%s", filePrefixDirectory, FILENAME_DEVICE_BACKUP);
			Log.i(LOG_TAG, "About to run this command...");
			Log.i(LOG_TAG, cmd);
			Command command = new Command(0, cmd) {

				@Override
				public void commandCompleted(int id, int arg1) {
					// Grabbing the now-completely backed up DEVICE_BACKUP file.
					originalState = BitmapFactory.decodeFile(filePrefixDirectory+"/"+FILENAME_DEVICE_BACKUP);
					// Creating a self-contained working copy Bitmap
					setWorkingBitmap(originalState.copy(originalState.getConfig(), true));
				}

				@Override
				public void commandOutput(int id, String line) {
					if (id == 0) {
						Log.i(LOG_TAG, "Command finished! This is the output!");
					}
					Log.i(LOG_TAG, id+": "+line);
				}

				@Override
				public void commandTerminated(int id, String line) { }
			};
			try {
				RootTools.getShell(true).add(command);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.i(LOG_TAG, "IOException!!");
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				Log.i(LOG_TAG, "TimeoutException!!");
				e.printStackTrace();
			} catch (RootDeniedException e) {
				// TODO Auto-generated catch block
				Log.i(LOG_TAG, "RootDeniedException!!");
				e.printStackTrace();
			}
		} else {
			//TODO: Present an AlertDialog notifying the user that the app did not recieve root rights.
			Log.e(LOG_TAG, "COULD NOT GET ROOT RIGHTS!!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Pushes the WORKING_COPY .bmp file to the clogo block device!
	 * TODO: Update this documentation
	 */
	public boolean pushBootscreenToDevice(final AlertDialog.Builder successHandlingAlertDialogBuilder,
											final AlertDialog.Builder failureHandlingAlertDialogBuilder,
											final DialogInterface.OnDismissListener ifNoRootDismissListener) {
		
		// Last-minute verification that the WORKING_COPY file does indeed exist. We don't want to attempt to write any weird null data to the block device!
		if (!fileExists(FILENAME_WORKING_COPY)) {
			Log.e(LOG_TAG, "CHOKE! We somehow have gotten to the pushBootscreenToDevice() stage and don't have a working copy on disk! INVESTIGATE");
			return false;
		}
		// Checking for Root access.
		if (RootTools.isAccessGiven()) {
			Log.i(LOG_TAG, "Root granted! About to PUSH bitmap...");
			String cmd = String.format("dd if=%s/%s of=/dev/block/platform/msm_sdcc.1/by-name/clogo", filePrefixDirectory, FILENAME_WORKING_COPY);
			Log.i(LOG_TAG, "About to run this command...");
			Log.i(LOG_TAG, cmd);
			Command command = new Command(0, cmd) {

				@Override
				public void commandCompleted(int arg0, int arg1) {
					successHandlingAlertDialogBuilder.show();
				}

				@Override
				public void commandOutput(int id, String line) {
					if (id == 0) {
						Log.i(LOG_TAG, "Command finished! This is the output!");
					}
					Log.i(LOG_TAG, id+": "+line);
				}

				@Override
				public void commandTerminated(int arg0, String arg1) {
					failureHandlingAlertDialogBuilder.show();
				}
			};
			try {
				RootTools.getShell(true).add(command);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.i(LOG_TAG, "IOException!!");
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				Log.i(LOG_TAG, "TimeoutException!!");
				e.printStackTrace();
			} catch (RootDeniedException e) {
				// TODO Auto-generated catch block
				Log.i(LOG_TAG, "RootDeniedException!!");
				e.printStackTrace();
			}
		} else {
			Log.e(LOG_TAG, "COULD NOT GET ROOT RIGHTS!!");
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
												.setCancelable(true)
												.setTitle("Root is required!")
												.setMessage("The app was not able to get the root privileges!")
												.setInverseBackgroundForced(true);
			builder.setOnDismissListener(ifNoRootDismissListener);
			builder.setNeutralButton("Ok.", null);
			builder.create().show();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Pushes the DEVICE_BACKUP .bmp file to the clogo block device!
	 */
	public boolean restoreDeviceOriginalBootscreen(final AlertDialog.Builder successHandlingAlertDialogBuilder, final AlertDialog.Builder failureHandlingAlertDialogBuilder) {
		
		// Last-minute verification that the DEVICE_BACKUP file does indeed exist. We don't want to attempt to write any weird null data to the block device!
		if (!fileExists(FILENAME_DEVICE_BACKUP)) {
			Log.e(LOG_TAG, "CHOKE! We somehow have gotten to the restoreDeviceOriginalBootscreen() stage and don't have an original to restore... How did this happen?");
			return false;
		}
		// Checking for Root access.
		if (RootTools.isAccessGiven()) {
			Log.i(LOG_TAG, "Root granted! About to PUSH bitmap...");
			String cmd = String.format("dd if=%s/%s of=/dev/block/platform/msm_sdcc.1/by-name/clogo", filePrefixDirectory, FILENAME_DEVICE_BACKUP);
			Log.i(LOG_TAG, "About to run this command...");
			Log.i(LOG_TAG, cmd);
			Command command = new Command(0, cmd) {

				@Override
				public void commandCompleted(int arg0, int arg1) {
					successHandlingAlertDialogBuilder.show();
				}

				@Override
				public void commandOutput(int id, String line) {
					if (id == 0) {
						Log.i(LOG_TAG, "Command finished! This is the output!");
					}
					Log.i(LOG_TAG, id+": "+line);
				}

				@Override
				public void commandTerminated(int arg0, String arg1) {
					failureHandlingAlertDialogBuilder.show();
				}
			};
			try {
				RootTools.getShell(true).add(command);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.i(LOG_TAG, "IOException!!");
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				Log.i(LOG_TAG, "TimeoutException!!");
				e.printStackTrace();
			} catch (RootDeniedException e) {
				// TODO Auto-generated catch block
				Log.i(LOG_TAG, "RootDeniedException!!");
				e.printStackTrace();
			}
		} else {
			Log.e(LOG_TAG, "COULD NOT GET ROOT RIGHTS!!");
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
												.setCancelable(true)
												.setTitle("Root is required!")
												.setMessage("The app was not able to get the root privileges!")
												.setInverseBackgroundForced(true);
			builder.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.i(LOG_TAG, "Could not restore DEVICE_BACKUP due to lack of root privileges.");
				}
			});
			builder.create().show();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks the application's data directory for a given filename.
	 * @param filename The given filename to look for in application's data directory.
	 * @return Returns true if given filename exists, false otherwise.
	 */
	public boolean fileExists(String filename) {
		
		File file = new File(filePrefixDirectory+"/"+filename);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Check's to see if a file exists in the app's data and subsequently attempts to delete it.
	 * @param filename The given filename to look for in application's data directory.
	 * @return Returns true if "ALL CLEAR!"; the file did not exist or was successfully deleted, false if a deletion attempt had failed.
	 */
	public boolean deleteFileIfExists(String filename) {
		
		File file = new File(filePrefixDirectory+"/"+filename);
		if (file.exists()) {
			if (file.delete()) {
				Log.i(LOG_TAG, "File deleted: "+filename);
				return true;
			} else {
				Log.e(LOG_TAG, "File could not be deleted!");
				return false;
			}
		} else {
			Log.i(LOG_TAG, "File does not exist: "+filename);
			return true;
		}
	}
	
	/**
	 * A method for retrieving the DEVICE_BACKUP's file content into a Bitmap object.
	 * @param shouldPullFromDeviceIfNoneOnFile A boolean value on whether or not we should pull the file from the device if one is not in the filesystem.
	 * @return Returns a Bitmap object with the contents of the DEVICE_BACKUP file, or null if something went wrong.
	 */
	public Bitmap bitmapFromDeviceBackup(boolean shouldPullFromDeviceIfNoneOnFile) {
		
		if (fileExists(FILENAME_DEVICE_BACKUP)) {
			Bitmap bitmap = BitmapFactory.decodeFile(filePrefixDirectory+"/"+FILENAME_DEVICE_BACKUP);
			return bitmap;
		} else {
			if (shouldPullFromDeviceIfNoneOnFile) {
				if (pullBootscreenFromDevice(false)) {
					Log.i(LOG_TAG, "Bootscreen is in the process of being backed up right now! The canvas will update when it finishes.");
					return createEmptyBitmap();
				} else {
					Log.e(LOG_TAG, "Bootscreen could not be successfully pulled from the device!");
					Log.i(LOG_TAG, "Creating a new empty bitmap!");
					return createEmptyBitmap();
				}
			}
		}
		// If we've somehow managed to get here, we probably deserve a null return value.
		return null;
	}
	
	/**
	 * Convenience method for quickly creating an empty Bitmap object.
	 * @return Returns an empty Bitmap object at the resolution specified by the object's constants.
	 */
	public Bitmap createEmptyBitmap() {
		return Bitmap.createBitmap(BOOTSCREEN_RESOLUTION_WIDTH, BOOTSCREEN_RESOLUTION_HEIGHT, Bitmap.Config.ARGB_8888);
	}
	
	public boolean installPersonalizedBootscreen(AlertDialog.Builder successHandlingAlertDialogBuilder,
													AlertDialog.Builder failureHandlingAlertDialogBuilder,
													DialogInterface.OnDismissListener ifNoRootDismissListener) {
		
		/*
		 * Proposed timeline: (Some of this hapens outside of this method)
		 * 1. save (Bitmap)workingCopy to a .bmp file in data directory
		 * 2. write this file to the blockdevice using dd
		 * 3. check outcome; if successful, offer to reboot.
		 */
		if (saveBitmap()) {
			Log.i(LOG_TAG, "Bitmap saved to disk! Now attempting to write it to the block device.");
			if (!pushBootscreenToDevice(successHandlingAlertDialogBuilder, failureHandlingAlertDialogBuilder, ifNoRootDismissListener)) {
				Log.e(LOG_TAG, "Looks like something didn't work on the installation! Check further up in the log for related details!");
				failureHandlingAlertDialogBuilder.show();
				return false;
			}
		} else {
			failureHandlingAlertDialogBuilder.show();
			return false;
		}
		return true;
	}
	
}
