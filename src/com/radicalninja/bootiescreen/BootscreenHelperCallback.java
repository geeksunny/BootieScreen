package com.radicalninja.bootiescreen;

/**
 * The BootscreenHelperCallback object defines the events to take place in any given outcome of a
 * BootscreenHelper object's action.
 */
abstract class BootscreenHelperCallback {

    private String mSuccessMessage;
    private String mFailureMessage;
    private String mNeutralMessage;

    /**
     * Flag for encountering a situation where we cannot get root rights.
     */
    public static final int FLAG_NO_ROOT_RIGHTS = 1000;
    /**
     * Flag for encountering a situation where the bitmap file on disk is corrupt.
     */
    public static final int FLAG_BITMAP_CORRUPT = 1001;
    /**
     * Flag for encountering a fatal exception.
     */
    public static final int FLAG_EXCEPTION = 1002;
    /**
     * Flag for encountering a situation where the bitmap file could not be saved to disk.
     */
    public static final int FLAG_BITMAP_NOT_SAVED = 1003;
    /**
     * Flag for encountering a situation where the bitmap file is missing.
     */
    public static final int FLAG_BITMAP_MISSING = 1004;

    /**
     * Set the success message.
     *
     * @param successMessage The given message for your successful outcome.
     * @return Returns the current BootscreenHelperCallback object for method chaining.
     */
    final public BootscreenHelperCallback setSuccessMessage(String successMessage) {

        mSuccessMessage = successMessage;
        return this;
    }

    /**
     * Set the failure message.
     *
     * @param failureMessage The given message for your failure outcome.
     * @return Returns the current BootscreenHelperCallback object for method chaining.
     */
    final public BootscreenHelperCallback setFailureMessage(String failureMessage) {

        mFailureMessage = failureMessage;
        return this;
    }

    /**
     * Set the neutral message.
     *
     * @param neutralMessage The given message for your neutral outcome.
     * @return Returns the current BootscreenHelperCallback object for method chaining.
     */
    final public BootscreenHelperCallback setNeutralMessage(String neutralMessage) {

        mNeutralMessage = neutralMessage;
        return this;
    }

    /**
     * Called when the BootscreenHelper's action had a successful outcome.
     *
     * @param successMessage A success message provided by BootscreenHelper's current action.
     */
    abstract void onSuccess(String successMessage);

    /**
     * Called when the BootscreenHelper's action had a failed outcome.
     *
     * @param failureMessage A failure message provided by BootscreenHelper's current action.
     * @param flag An integer value representing the current state of error.
     */
    abstract void onFailure(String failureMessage, int flag);

    /**
     * Called when the BootscreenHelper's action had a neutral outcome.
     *
     * @param neutralMessage A neutral message provided by BootscreenHelper's current action.
     */
    abstract void onNeutral(String neutralMessage);

    /**
     * Invoke the abstract successful method in this object.
     *
     * @return Returns the current BootscreenHelperCallback object for method chaining.
     */
    final public BootscreenHelperCallback invokeSuccess() {

        onSuccess(mSuccessMessage);
        return this;
    }

    /**
     * Invoke the abstract failure method in this object.
     *
     * @return Returns the current BootscreenHelperCallback object for method chaining.
     */
    final public BootscreenHelperCallback invokeFailure(int flag) {

        onFailure(mFailureMessage, flag);
        return this;
    }

    /**
     * Invoke the abstract neutral method in this object.
     *
     * @return Returns the current BootscreenHelperCallback object for method chaining.
     */
    final public BootscreenHelperCallback invokeNeutral() {

        onSuccess(mNeutralMessage);
        return this;
    }
}
