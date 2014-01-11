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
     */
    abstract void onFailure(String failureMessage);

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
    final public BootscreenHelperCallback invokeFailure() {

        onSuccess(mFailureMessage);
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
