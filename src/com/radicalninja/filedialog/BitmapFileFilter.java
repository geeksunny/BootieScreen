package com.radicalninja.filedialog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileFilter;

/**
 * File filter used to validate a file against a list of supported Bitmap graphic file formats
 * and a target resolution.
 */
public class BitmapFileFilter implements FileFilter {

    private static final String LOG_TAG = "BitmapFileFilter";
    private int mTargetWidth, mTargetHeight;
    private Bitmap mStoredBitmap;
    private boolean mShouldStoreBitmap = false;
    private static final int VALIDATION_DISABLED = -1;
    private final String[] mSupportedExtensions =
            new String[] {"jpg","jpeg","gif","png","bmp","webp"};

    /**
     * Construct the BitmapFileFilter for Bitmap validation ONLY. Image resolution will not
     *     be validated.
     */
    public BitmapFileFilter() {

        disableResolutionValidation();
    }

    /**
     * Construct the BitmapFileFilter for image resolution validation with target dimensions.
     * @param targetWidth Given target width in pixels.
     * @param targetHeight Given target height in pixels.
     */
    public BitmapFileFilter(int targetWidth, int targetHeight) {

        setTargetResolution(targetWidth, targetHeight);
    }

    /**
     * Validates that the given file reference is a valid supported Bitmap graphic file and
     *     matches the target dimensions.
     * @param file The given file to validate. Should point to an image file.
     * @return Returns boolean true if the file is a supported format, a valid Bitmap, and matches
     *     the target dimensions. False if not all of this criteria can be met.
     */
    @Override
    public boolean accept(File file) {

        if (file.isFile() && checkFileExtension(file)) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null) {
                if (mShouldStoreBitmap && resolutionValidates(bitmap)) {
                    mStoredBitmap = bitmap;
                    return true;
                } else return resolutionValidates(bitmap);
            }
        }
        return false;
    }

    /**
     * Validates that the given bitmap reference is valid and passes resolution validation.
     *     Not at all useful if resolution validation is disabled.
     * @param bitmap The given bitmap object to validate.
     * @return Returns boolean true if the bitmap object passes resolution validation. Otherwise
     *     false.
     */
    public boolean accept(Bitmap bitmap) {

        return ((bitmap != null) && resolutionValidates(bitmap));
    }

    /**
     * Disable resolution validation on this BitmapFileFilter.
     */
    public void disableResolutionValidation() {

        mTargetWidth = VALIDATION_DISABLED;
        mTargetHeight = VALIDATION_DISABLED;
    }

    /**
     * Check's the given File reference's extension against the list of supported Bitmap file types.
     * @param file The given File object to check.
     * @return Returns boolean true if the file referenced passes the extension validation.
     *     Otherwise false.
     */
    public boolean checkFileExtension(File file) {

        String name = file.getName().toLowerCase();
        for (String ext : mSupportedExtensions) {
            if (name.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check's the given file path's extension against the list of supported Bitmap file types.
     * @param filePath The given file path to check.
     * @return Returns boolean true if the file referenced passes the extension validation.
     *     Otherwise false.
     */
    public boolean checkFileExtension(String filePath) {

        String name = filePath.toLowerCase();
        for (String ext : mSupportedExtensions) {
            if (name.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the current validation status of mBitmap's resolution.
     * @return Returns boolean true if mBitmap's resolution matches the target dimensions or if
     *     resolution validation is disabled. False if otherwise.
     */
    public boolean resolutionValidates(Bitmap bitmap) {

        return ((mTargetWidth == VALIDATION_DISABLED || bitmap.getWidth() == mTargetWidth) &&
                (mTargetHeight == VALIDATION_DISABLED || bitmap.getHeight() == mTargetHeight));
    }

    /**
     * Creates a mutable copy of the stored Bitmap object and returns it. The stored Bitmap object
     *     will be destroyed (i.e. set to null) as a result.
     * @return Returns a mutable copy of the stored Bitmap object, or null if a Bitmap is not stored.
     */
    public Bitmap retrieveStoredBitmap() {

        if (mStoredBitmap != null) {
            Bitmap returnCopy = mStoredBitmap.copy(mStoredBitmap.getConfig(), true);
            mStoredBitmap = null;
            return returnCopy;
        } else {
            return null;
        }
    }

    public void setShouldStoreBitmap(boolean isEnabled) {

        mShouldStoreBitmap = isEnabled;
    }

    /**
     * Sets the target dimensions for resolution validation.
     * @param targetWidth Given target width in pixels.
     * @param targetHeight Given target height in pixels.
     */
    public void setTargetResolution(int targetWidth, int targetHeight) {

        mTargetWidth = targetWidth;
        mTargetHeight = targetHeight;
    }

    public int getTargetWidth() {
        return mTargetWidth;
    }

    public int getTargetHeight() {
        return mTargetHeight;
    }
}
