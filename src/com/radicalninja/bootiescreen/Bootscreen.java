package com.radicalninja.bootiescreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

public class Bootscreen extends Canvas {
	
	public static final int BOOTSCREEN_RESOLUTION_WIDTH = 720;
	public static final int BOOTSCREEN_RESOLUTION_HEIGHT = 1280;
	private static final String LOG_TAG = "Bootscreen";
    private static final Bitmap.Config BOOTSCREEN_BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
	private Context mContext;
	private Bitmap mOriginalState;
	private Bitmap mWorkingCopy;
	private Paint mPainter;
    private float mVerticalPosition = BOOTSCREEN_RESOLUTION_HEIGHT * 0.8f;
	
	/**
	 * Constructor method that takes the active application Context object.
	 * 
	 * @param context The current application Context object.
	 */
	public Bootscreen(Context context) {
		
		super();
		// Storing the application context & parent view variables.
		mContext = context;
		// Backing up this bitmap for easy reverting.
		mOriginalState = createEmptyBitmap();
		// Creating a self-contained working copy Bitmap
		setWorkingBitmap(createEmptyBitmap());
		// Creating the mPainter and initializing the default settings.
		mPainter = new Paint();
		mPainter.setAntiAlias(true);
		mPainter.setTextAlign(Paint.Align.CENTER);
		mPainter.setHinting(Paint.HINTING_ON);
		mPainter.setSubpixelText(true);
	}
	
	/**
	 * Set's the Bootscreen's active working copy bitmap object as well as the Bootscreen's active bitmap to draw in to. If a parent ImageView object is set, redraw the view.
	 *
	 * @param bitmap The given mutable Bitmap object.
     * @return Returns a reference to itself for method chaining.
	 */
	public Bootscreen setWorkingBitmap(Bitmap bitmap) {

		mWorkingCopy = bitmap;
		super.setBitmap(mWorkingCopy);
        return this;
	}
	
    /**
     * Convenience method for quickly creating an empty Bitmap object.
     * @return Returns an empty Bitmap object at the resolution specified by the object's constants.
     */
    public static Bitmap createEmptyBitmap() {
        return Bitmap.createBitmap(BOOTSCREEN_RESOLUTION_WIDTH, BOOTSCREEN_RESOLUTION_HEIGHT, BOOTSCREEN_BITMAP_CONFIG);
    }

	/**
	 * Getter method for accessing the current mWorkingCopy Bitmap object.
	 * @return Returns the bootscreen's mWorkingCopy Bitmap object.
	 */
	public Bitmap getBitmap() {

		return mWorkingCopy;
	}

	/**
	 * Resets the bootscreen's mWorkingCopy Bitmap object to it's mOriginalState.
     *
     * @return Returns a reference to itself for method chaining.
	 */
	public Bootscreen resetBitmap() {

		setWorkingBitmap(mOriginalState.copy(BOOTSCREEN_BITMAP_CONFIG, true));
        return this;
	}
	
    /**
	 * Set's the bootscreen's mPainter object's color to the given value.
	 * @param color An integer value for the Painter's color.
     * @return Returns a reference to itself for method chaining.
	 */
	public Bootscreen setColor(int color) {

		mPainter.setColor(color);
        return this;
	}

    /**
     * Set's the Bootscreen's original state bitmap. Optionally updates the working copy.
     *
     * @param bitmap The given bitmap object.
     * @param updateWorkingCopy If true, a mutable copy of bitmap will be loaded into mWorkingCopy.
     * @return Returns a reference to itself for method chaining.
     */
    public Bootscreen setOriginalState(Bitmap bitmap, boolean updateWorkingCopy) {

        mOriginalState = bitmap;
        if (updateWorkingCopy) {
            setWorkingBitmap(mOriginalState.copy(BOOTSCREEN_BITMAP_CONFIG, true));
        }
        return this;
    }
	
	/**
	 * Set's the bootscreen's mPainter object's TextSize setting to the given value.
	 * @param size A float value for the Painter's TextSize.
     * @return Returns a reference to itself for method chaining.
	 */
	public Bootscreen setTextSize(float size) {
		
		mPainter.setTextSize(size);
        return this;
	}
	
    /**
     * Set's the bootscreen's mPainter object's Typeface setting to the given Typeface object.
     * @param typeface The given Typeface object for the Painter's Typeface.
     * @return Returns a reference to itself for method chaining.
     */
	public Bootscreen setTypeface(Typeface typeface) {
		
		mPainter.setTypeface(typeface);
        return this;
	}

    /**
     * Sets the vertical position of the message text.
     * @param verticalPosition A float value of the given vertical position in pixels from the top.
     * @return Returns a reference to itself for method chaining.
     */
    public Bootscreen setVerticalPosition(float verticalPosition) {

        mVerticalPosition = verticalPosition;
        return this;
    }
	
	/**
	 * Personalize the mWorkingCopy bitmap with the given message string.
	 * @param message The given message string.
     * @return Returns a reference to itself for method chaining.
	 */
	public Bootscreen doPersonalization(String message) {
		
		// Write's nothing if message is empty string.
		// Essentially treats a blank message as a "Reset back to DEVICE_BACKUP."
		if (message != "") {
			float x = this.getWidth() / 2;  // Center of width

			this.drawText(message, x, mVerticalPosition, mPainter);
		}
        return this;
	}
	
}
