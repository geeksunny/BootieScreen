package com.radicalninja.bootiescreen;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import android.util.Log;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Command;

public final class RootHelper {
	
	private static final String LOG_TAG = "RootHelper";
	
	/**
	 * Perform's a full system reboot to the Android device. Will displays the device's boot logo.
	 */
	public static void restartDevice() {
		// Checking for Root access.
		if (RootTools.isAccessGiven()) {
			Log.i(LOG_TAG, "Root granted!");
			Command command = new Command(0, "reboot") {

				@Override
				public void commandCompleted(int id, int exitCode) {
					Log.w(LOG_TAG, "Reboot command completed! If your device doesn't rebooted in the next few seconds, something probably didn't work right.");
				}

				@Override
				public void commandOutput(int id, String line) {
					Log.i(LOG_TAG, "Device reboot in progress.");
				}

				@Override
				public void commandTerminated(int id, String reason) { }
			};
			//TODO: Handle these exceptions properly.
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
		}
	}

	/**
	 * Perform's a full system halt on the Android device.
	 */
	public static void haltDevice() {
		// Checking for Root access.
		if (RootTools.isAccessGiven()) {
			Log.i(LOG_TAG, "Root granted!");
			Command command = new Command(0, "reboot -p") {

				@Override
				public void commandCompleted(int id, int exitCode) {
					Log.w(LOG_TAG, "Halt command completed! If your device doesn't shut off in the next few seconds,  something probably didn't work right.");
				}

				@Override
				public void commandOutput(int id, String line) {
					Log.i(LOG_TAG, "Device halt in progress.");
				}

				@Override
				public void commandTerminated(int id, String reason) { }
			};
			//TODO: Handle these exceptions properly.
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
		}
	}

    /**
     * Checks if the current device is included in a given list of device models.
     * @param deviceList A String[] array containing a list of potential device models.
     * @return Returns true if the current device is included in the deviceList, otherwise false.
     */
    public static boolean deviceInList(String[] deviceList) {

        for (String model: deviceList) {
            if (android.os.Build.DEVICE.equalsIgnoreCase(model) == true) {
                return true;
            }
        }
        return false;
    }

    /**
     * A shortcut method for checking if root access is available and allowed.
     * @return Returns true if root access is available and allowed by the user.
     */
    public static boolean rootIsAvailable() {

        if (RootTools.isRootAvailable() && RootTools.isAccessGiven()) {
            return true;
        } else {
            return false;
        }
    }
}
