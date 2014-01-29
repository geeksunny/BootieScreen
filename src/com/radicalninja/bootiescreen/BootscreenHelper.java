package com.radicalninja.bootiescreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Command;
import com.ultrasonic.android.image.bitmap.util.AndroidBmpUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BootscreenHelper {

    private static final String LOG_TAG = "BootscreenHelper";
    private Context mContext;
    private Bootscreen mBootscreen;
    private BootscreenHelperCallback mBootscreenHelperCallback;
    protected static final String FILENAME_DEVICE_BACKUP = "clogoDeviceBackup.bmp";
    protected static final String FILENAME_WORKING_COPY = "clogo.bmp";
    protected static final String PREFIX_FILE_DIRECTORY
            = String.format("%s/BootieScreen", Environment.getExternalStorageDirectory());


    public BootscreenHelper(Context context) {

        mContext = context;
        mBootscreen = new Bootscreen(context);

        // Setting the PREFIX_FILE_DIRECTORY variable.
        //TODO: Clean up the new /sdcard/ hard-coding with exception protection.
        Log.i(LOG_TAG, "Checking if directory exists on sdcard...");
        if (fileExists("")) {
            Log.i(LOG_TAG, "Good news, everyone! The directory exists on sdcard!");
        } else {
            Log.w(LOG_TAG, "Directory does not exist on sdcard -- Creating it now!");
            File dataDir = new File(PREFIX_FILE_DIRECTORY);
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
     * Checks the application's data directory for a given filename.
     * @param filename The given filename to look for in application's data directory.
     * @return Returns true if given filename exists, false otherwise.
     */
    public boolean fileExists(String filename) {

        File file = new File(PREFIX_FILE_DIRECTORY +"/"+filename);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Set the BootscreenHelper's current BootscreenHelperCallback object.
     * @param callback The given BootscreenHelperCallback object to use.
     * @return Returns the current BootscreenHelper object for method chaining.
     */
    public BootscreenHelper setCallback(BootscreenHelperCallback callback) {

        mBootscreenHelperCallback = callback;
        return this;
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

    /**
     * Attempts to load the DEVICE_BACKUP bitmap into the Bootscreen object.
     *
     * If a BootscreenHelperCallback is set, it will invoke either success or failure based on the
     * outcome of the bitmap loading.
     *
     * @param forceNewPull If true, the DEVICE_BACKUP bootscreen graphic will be pulled from the device even if one exists on the SD card.
     * @return Returns the current BootscreenHelper object for method chaining.
     */
    public BootscreenHelper loadDeviceBootscreen(boolean forceNewPull) {

        if (!fileExists(FILENAME_DEVICE_BACKUP)) {
            // Since the file does not exist yet, pull a copy from the device.
            if (!pullBootscreenFromDevice(false)) {
                Log.e(LOG_TAG, "Failed to pull bootscreen from device. Aborting loadDeviceBootscreen()");
            }
        } else if (forceNewPull) {
            // The file exists, but we want to replace it with a fresh copy from the device.
            if (!pullBootscreenFromDevice(true)) {
                Log.e(LOG_TAG, "Failed to pull bootscreen from device. Aborting loadDeviceBootscreen(true)");
            }
        } else {
            // The file exists! Load it into the Bootscreen object.
            bitmapToBootscreen();
        }

        return this;
    }

    /**
     * Called when loading DEVICE_COPY from the SD Card into the Bootscreen object.
     */
    private void bitmapToBootscreen() {

        Bitmap bitmap = BitmapFactory.decodeFile(PREFIX_FILE_DIRECTORY + "/" + FILENAME_DEVICE_BACKUP);
        if (bitmap == null) {
            Log.e(LOG_TAG, "Could not read the bitmap file! It may be corrupt. Pull a new copy from the device.");
            callbackFailure("Could not read the bootscreen's bitmap file. It may be corrupt. Try pulling a new copy.",
                    BootscreenHelperCallback.FLAG_BITMAP_CORRUPT, true);
            return;
        }
        mBootscreen.setOriginalState(bitmap, true);
        Log.i(LOG_TAG, "Device bitmap successfully loaded into the Bootscreen object.");
        callbackSuccess("Device bitmap successfully loaded into editor!", true);
    }

    /**
     * Pulls the existing bootscreen data from your device.
     * @param shouldOverwriteExistingBackup Boolean value on whether we should overwrite any existing DEVICE_BACKUP files or not.
     * @return Returns a boolean true if the procedure succeeds without a hitch, and false if otherwise.
     */
    private boolean pullBootscreenFromDevice(boolean shouldOverwriteExistingBackup) {

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
            String cmd = String.format("dd if=/dev/block/platform/msm_sdcc.1/by-name/clogo of=%s/%s", PREFIX_FILE_DIRECTORY, FILENAME_DEVICE_BACKUP);
            Log.i(LOG_TAG, "About to run this command...");
            Log.i(LOG_TAG, cmd);
            Command command = new Command(0, cmd) {

                @Override
                public void commandCompleted(int id, int arg1) {
                    // The bitmap SHOULD have been pulled to the SD card by now. Verify and load.
                    if (fileExists(FILENAME_DEVICE_BACKUP)) {
                        bitmapToBootscreen();
                    } else {
                        callbackFailure("Bootscreen graphic could not be saved to the SD card.",
                                        BootscreenHelperCallback.FLAG_BITMAP_MISSING, true);
                    }
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
                // TODO: Log this to error log when implemented
                Log.i(LOG_TAG, "IOException!!");
                e.printStackTrace();
                callbackFailure("Bootscreen graphic could not be pulled from the device. [IOException]",
                                BootscreenHelperCallback.FLAG_EXCEPTION, true);
                return false;
            } catch (TimeoutException e) {
                // TODO: Log this to error log when implemented
                Log.i(LOG_TAG, "TimeoutException!!");
                e.printStackTrace();
                callbackFailure("Bootscreen graphic could not be pulled from the device. Could not get root rights.",
                                BootscreenHelperCallback.FLAG_EXCEPTION, true);
                return false;
            } catch (RootDeniedException e) {
                // TODO: Log this to error log when implemented
                Log.i(LOG_TAG, "RootDeniedException!!");
                e.printStackTrace();
                callbackFailure("Bootscreen graphic could not be pulled from the device. Could not get root rights.",
                                BootscreenHelperCallback.FLAG_EXCEPTION, true);
                return false;
            }
        } else {
            Log.e(LOG_TAG, "COULD NOT GET ROOT RIGHTS!!");
            callbackFailure("Bootscreen graphic could not be pulled from the device. Could not get root rights.",
                            BootscreenHelperCallback.FLAG_NO_ROOT_RIGHTS, true);
            return false;
        }

        return true;
    }

    /**
     * Check's to see if a file exists in the app's data and subsequently attempts to delete it.
     * @param filename The given filename to look for in application's data directory.
     * @return Returns true if "ALL CLEAR!"; the file did not exist or was successfully deleted, false if a deletion attempt had failed.
     */
    public boolean deleteFileIfExists(String filename) {

        File file = new File(PREFIX_FILE_DIRECTORY +"/"+filename);
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
     * Pushes the original bootscreen to your device. Writes the DEVICE_BACKUP .bmp file to clogo.
     * @return Returns the current BootscreenHelper object for method chaining.
     */
    public BootscreenHelper restoreDeviceOriginalBootscreen() {

        // Last-minute verification that the DEVICE_BACKUP file does indeed exist. We don't want to attempt to write any weird null data to the block device!
        if (!fileExists(FILENAME_DEVICE_BACKUP)) {
            Log.e(LOG_TAG, "CHOKE! We somehow have gotten to the restoreDeviceOriginalBootscreen() stage and don't have an original to restore... How did this happen?");
            callbackFailure("There was a problem restoring your bootscreen. Your original bootscreen graphic file is missing. Restore the file or pull from your device again.",
                            BootscreenHelperCallback.FLAG_BITMAP_MISSING, true);
            return this;
        }
        // Checking for Root access.
        if (RootTools.isAccessGiven()) {
            Log.i(LOG_TAG, "Root granted! About to PUSH bitmap...");
            String cmd = String.format("dd if=%s/%s of=/dev/block/platform/msm_sdcc.1/by-name/clogo", PREFIX_FILE_DIRECTORY, FILENAME_DEVICE_BACKUP);
            Log.i(LOG_TAG, "About to run this command...");
            Log.i(LOG_TAG, cmd);
            Command command = new Command(0, cmd) {

                @Override
                public void commandCompleted(int arg0, int arg1) { }

                @Override
                public void commandOutput(int id, String line) {
                    if (id == 0) {
                        Log.i(LOG_TAG, "Command finished! This is the output!");
                    }
                    Log.i(LOG_TAG, id+": "+line);
                }

                @Override
                public void commandTerminated(int arg0, String arg1) { }
            };
            try {
                RootTools.getShell(true).add(command);
            } catch (IOException e) {
                // TODO: Log this to error log when implemented
                Log.i(LOG_TAG, "IOException!!");
                e.printStackTrace();
                callbackFailure("Bootscreen graphic could not be restored to the device. [IOException]",
                                BootscreenHelperCallback.FLAG_EXCEPTION, true);
                return this;
            } catch (TimeoutException e) {
                // TODO: Log this to error log when implemented
                Log.i(LOG_TAG, "TimeoutException!!");
                e.printStackTrace();
                callbackFailure("Bootscreen graphic could not be restored to the device. Could not get root rights.",
                        BootscreenHelperCallback.FLAG_EXCEPTION, true);
                return this;
            } catch (RootDeniedException e) {
                // TODO: Log this to error log when implemented
                Log.i(LOG_TAG, "RootDeniedException!!");
                e.printStackTrace();
                callbackFailure("Bootscreen graphic could not be restored to the device. Could not get root rights.",
                        BootscreenHelperCallback.FLAG_EXCEPTION, true);
                return this;
            }
        } else {
            Log.e(LOG_TAG, "COULD NOT GET ROOT RIGHTS!!");
            callbackFailure("Bootscreen graphic could not be restored to the device. Could not get root rights.",
                            BootscreenHelperCallback.FLAG_NO_ROOT_RIGHTS, true);
            return this;
        }
        callbackSuccess("Your original bootscreen was successfully restored!", true);
        return this;
    }

    public BootscreenHelper installPersonalizedBootscreen() {

		/*
		 * Proposed timeline: (Some of this hapens outside of this method)
		 * 1. save (Bitmap)workingCopy to a .bmp file in data directory
		 * 2. write this file to the blockdevice using dd
		 * 3. check outcome; if successful, offer to reboot.
		 */
        if (saveBitmap()) {
            Log.i(LOG_TAG, "Bitmap saved to disk! Now attempting to write it to the block device.");
            if (!pushBootscreenToDevice()) {
                Log.e(LOG_TAG, "Looks like something didn't work on the installation! Check further up in the log for related details!");
                // callbackFailure() will have been called in pushBootscreenToDevice().
                return this;
            }
        } else {
            callbackFailure("There was a problem saving your bitmap. The bootscreen was not installed.",
                            BootscreenHelperCallback.FLAG_BITMAP_NOT_SAVED, true);
            return this;
        }
        callbackSuccess("Your customized bootscreen was successfully installed!", true);
        return this;
    }

    /**
     * Pushes the customized bootscreen to your device. Writes the WORKING_COPY .bmp file to clogo.
     * @return Returns a boolean true if the procedure succeeds without a hitch, and false if otherwise.
     */
    private boolean pushBootscreenToDevice() {

        // Last-minute verification that the WORKING_COPY file does indeed exist. We don't want to attempt to write any weird null data to the block device!
        if (!fileExists(FILENAME_WORKING_COPY)) {
            Log.e(LOG_TAG, "CHOKE! We somehow have gotten to the pushBootscreenToDevice() stage and don't have a working copy on disk! INVESTIGATE");
            callbackFailure("There was a problem saving your bitmap. The bootscreen was not installed.",
                            BootscreenHelperCallback.FLAG_BITMAP_NOT_SAVED, true);
            return false;
        }
        // Checking for Root access.
        if (RootTools.isAccessGiven()) {
            Log.i(LOG_TAG, "Root granted! About to PUSH bitmap...");
            String cmd = String.format("dd if=%s/%s of=/dev/block/platform/msm_sdcc.1/by-name/clogo", PREFIX_FILE_DIRECTORY, FILENAME_WORKING_COPY);
            Log.i(LOG_TAG, "About to run this command...");
            Log.i(LOG_TAG, cmd);
            Command command = new Command(0, cmd) {

                @Override
                public void commandCompleted(int arg0, int arg1) { }

                @Override
                public void commandOutput(int id, String line) {
                    if (id == 0) {
                        Log.i(LOG_TAG, "Command finished! This is the output!");
                    }
                    Log.i(LOG_TAG, id+": "+line);
                }

                @Override
                public void commandTerminated(int arg0, String arg1) { }
            };
            try {
                RootTools.getShell(true).add(command);
            } catch (IOException e) {
                // TODO: Log this to error log when implemented
                Log.i(LOG_TAG, "IOException!!");
                e.printStackTrace();
                callbackFailure("Bootscreen graphic could not be pushed to the device. [IOException]",
                        BootscreenHelperCallback.FLAG_EXCEPTION, true);
                return false;
            } catch (TimeoutException e) {
                // TODO: Log this to error log when implemented
                Log.i(LOG_TAG, "TimeoutException!!");
                e.printStackTrace();
                callbackFailure("Bootscreen graphic could not be pushed to the device. Could not get root rights.",
                        BootscreenHelperCallback.FLAG_EXCEPTION, true);
                return false;
            } catch (RootDeniedException e) {
                // TODO: Log this to error log when implemented
                Log.i(LOG_TAG, "RootDeniedException!!");
                e.printStackTrace();
                callbackFailure("Bootscreen graphic could not be pushed to the device. Could not get root rights.",
                        BootscreenHelperCallback.FLAG_EXCEPTION, true);
                return false;
            }
        } else {
            Log.e(LOG_TAG, "COULD NOT GET ROOT RIGHTS!!");
            callbackFailure("Bootscreen graphic could not be pulled from the device. Could not get root rights.",
                    BootscreenHelperCallback.FLAG_NO_ROOT_RIGHTS, true);
            return false;
        }

        return true;
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
        boolean isSaveResult = bmpUtil.save(mBootscreen.getBitmap(), PREFIX_FILE_DIRECTORY +"/"+FILENAME_WORKING_COPY);
        if (isSaveResult && fileExists(FILENAME_WORKING_COPY)) {
            return true;
        } else {
            return false;
        }
    }
}
