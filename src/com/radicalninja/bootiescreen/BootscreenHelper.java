package com.radicalninja.bootiescreen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Command;
import com.ultrasonic.android.image.bitmap.util.AndroidBmpUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BootscreenHelper {

    private Context mContext;
    private Bootscreen mBootscreen;
    private BootscreenHelperCallback mBootscreenHelperCallback;
    private String filePrefixDirectory;
    private boolean mActionIsFailed = false;

    private static final String FILENAME_DEVICE_BACKUP = "clogoDeviceBackup.bmp";
    private static final String FILENAME_WORKING_COPY = "clogo.bmp";
    private static final String LOG_TAG = "BootscreenHelper";


    public BootscreenHelper(Context context) {

        mContext = context;
        mBootscreen = new Bootscreen(context);

        // Setting the filePrefixDirectory variable.
        //TODO: Clean up the new /sdcard/ hard-coding with exception protection.
		/*
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
		*/
        filePrefixDirectory = "/sdcard/BootieScreen";
        Log.i(LOG_TAG, "Checking if directory exists on sdcard...");
        if (fileExists("")) {
            Log.i(LOG_TAG, "Good news, everyone! The directory exists on sdcard!");
        } else {
            Log.w(LOG_TAG, "Directory does not exist on sdcard -- Creating it now!");
            File dataDir = new File(filePrefixDirectory);
            dataDir.mkdirs();
            if (fileExists("")) {
                Log.i(LOG_TAG, "Directory was successfully created!");
            } else {
                Log.e(LOG_TAG, "Directory couldn't be created! Prepare to crash and burn!");
                // TODO: Handle this case properly and protect from crash.
            }
        }

    }

    /**
     * Attempts to load the DEVICE_BACKUP bitmap into the Bootscreen object.
     *
     * If a BootscreenHelperCallback is set, it will invoke either success or failure based on the
     * outcome of the bitmap loading.
     *
     * @return Returns the current BootscreenHelper object for method chaining.
     */
    public BootscreenHelper loadDeviceBootscreen() {

        if (!fileExists(FILENAME_DEVICE_BACKUP)) {
            // Since the file dies not exist yet, pull a copy from the device.
            pullBootscreenFromDevice(false);
            if (mActionIsFailed) {
                mActionIsFailed = false; // Reset the flag!
                return this;
            }
        }

        Bitmap bitmap = BitmapFactory.decodeFile(filePrefixDirectory + "/" + FILENAME_DEVICE_BACKUP);
        if (bitmap == null) {
            // Since it looks like the existing file might be corrupt in some way, we are going
            // to pull a new copy from the device.
            pullBootscreenFromDevice(true);
            bitmap = BitmapFactory.decodeFile(filePrefixDirectory + "/" + FILENAME_DEVICE_BACKUP);
            if (bitmap == null) {
                Log.e(LOG_TAG, "Could not read the bitmap file, even after re-pulling.");
                callbackFailure("Could not read the bootscreen's bitmap file.",
                                BootscreenHelperCallback.FLAG_BITMAP_CORRUPT, true);
                return this;
            }
        }
        mBootscreen.setOriginalState(bitmap, true);
        Log.i(LOG_TAG, "Device bitmap successfully loaded into the Bootscreen object.");
        callbackSuccess("Device bitmap successfully loaded into editor!", true);

        return this;
    }

    public BootscreenHelper setCallback(BootscreenHelperCallback callback) {

        mBootscreenHelperCallback = callback;
        return this;
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
     * Retrieves the Bootscreen object for direct editing of personalization options.
     *
     * @return Returns the current Bootscreen object.
     */
    public Bootscreen getBootscreen() {

        return mBootscreen;
    }

    /**
     * Retrieves the Bootscreen object's current working Bitmap.
     *
     * @return Returns the Bootscreen's working copy Bitmap.
     */
    public Bitmap getBitmap() {

        return mBootscreen.getBitmap();
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
            Bitmap bitmap = BitmapFactory.decodeFile(filePrefixDirectory + "/" + FILENAME_DEVICE_BACKUP);
            return bitmap;
        } else {
            if (shouldPullFromDeviceIfNoneOnFile) {
                if (pullBootscreenFromDevice(false)) {
                    Log.i(LOG_TAG, "Bootscreen is in the process of being backed up right now! The canvas will update when it finishes.");
                    return Bootscreen.createEmptyBitmap();
                } else {
                    Log.e(LOG_TAG, "Bootscreen could not be successfully pulled from the device!");
                    Log.i(LOG_TAG, "Creating a new empty bitmap!");
                    return Bootscreen.createEmptyBitmap();
                }
            }
        }
        // If we've somehow managed to get here, return an empty bitmap.
        Log.e(LOG_TAG, "bitmapFromDeviceBackup() FAIL STATE!");
        return Bootscreen.createEmptyBitmap();

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
        boolean isSaveResult = bmpUtil.save(mBootscreen.getBitmap(), filePrefixDirectory+"/"+FILENAME_WORKING_COPY);
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
                public void commandCompleted(int id, int arg1) { }

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
                // TODO: Log this to error log when implemented
                Log.i(LOG_TAG, "IOException!!");
                e.printStackTrace();
                callbackFailure("Bootscreen graphic could not be pulled from the device. [IOException]",
                                BootscreenHelperCallback.FLAG_EXCEPTION, true);
                mActionIsFailed = true;
            } catch (TimeoutException e) {
                // TODO: Log this to error log when implemented
                Log.i(LOG_TAG, "TimeoutException!!");
                e.printStackTrace();
                callbackFailure("Bootscreen graphic could not be pulled from the device. Could not get root rights.",
                                BootscreenHelperCallback.FLAG_EXCEPTION, true);
                mActionIsFailed = true;
            } catch (RootDeniedException e) {
                // TODO: Log this to error log when implemented
                Log.i(LOG_TAG, "RootDeniedException!!");
                e.printStackTrace();
                callbackFailure("Bootscreen graphic could not be pulled from the device. Could not get root rights.",
                                BootscreenHelperCallback.FLAG_EXCEPTION, true);
                mActionIsFailed = true;
            }
            if (mActionIsFailed) {
                return false;
            }
        } else {
            Log.e(LOG_TAG, "COULD NOT GET ROOT RIGHTS!!");
            callbackFailure("Bootscreen graphic could not be pulled from the device. Could not get root rights.",
                            BootscreenHelperCallback.FLAG_NO_ROOT_RIGHTS, true);
            mActionIsFailed = true;
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
    public boolean restoreDeviceOriginalBootscreen(final AlertDialog.Builder successHandlingAlertDialogBuilder,
                                                   final AlertDialog.Builder failureHandlingAlertDialogBuilder,
                                                   final DialogInterface.OnDismissListener ifNoRootDismissListener) {
        //TODO: upon successful restoration, reset all the interface controls to the default empty state. Save changes to PREFS.
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
            builder.setOnDismissListener(ifNoRootDismissListener);
            builder.setNeutralButton("Ok.", null);
            builder.create().show();
            return false;
        }

        return true;
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

    /**
     * Invoke the successful response on the BootscreenHelper's callback object if one is set.
     * @param message The given message to send to the successful response.
     * @param destroyAfter If true, the BootscreenHelper's callback object will be destroyed after use.
     */
    private void callbackSuccess(String message, boolean destroyAfter) {
        
        if (mBootscreenHelperCallback != null) {
            mBootscreenHelperCallback
                    .setSuccessMessage(message)
                    .invokeSuccess();
            if (destroyAfter) {
                mBootscreenHelperCallback = null;
            }
        }
    }

    /**
     * Invoke the failure response on the BootscreenHelper's callback object if one is set.
     * @param message The given message to send to the failure response.
     * @param flag The flag code that will be sent to .invokeFailure().
     * @param destroyAfter If true, the BootscreenHelper's callback object will be destroyed after use.
     */
    private void callbackFailure(String message, int flag, boolean destroyAfter) {

        if (mBootscreenHelperCallback != null) {
            mBootscreenHelperCallback
                    .setFailureMessage(message)
                    .invokeFailure(flag);
            if (destroyAfter) {
                mBootscreenHelperCallback = null;
            }
        }
    }

    /**
     * Invoke the neutral response on the BootscreenHelper's callback object if one is set.
     * @param message The given message to send to the neutral response.
     * @param destroyAfter If true, the BootscreenHelper's callback object will be destroyed after use.
     */
    private void callbackNeutral(String message, boolean destroyAfter) {

        if (mBootscreenHelperCallback != null) {
            mBootscreenHelperCallback
                    .setNeutralMessage(message)
                    .invokeNeutral();
            if (destroyAfter) {
                mBootscreenHelperCallback = null;
            }
        }
    }
}
