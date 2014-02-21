package com.radicalninja.filedialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

public class FileDialog implements Observer {

    private static final String PARENT_DIR = "..";
    private final String TAG = getClass().getName();
    private String[] mFileList;
    private File mCurrentPath;
    public interface FileSelectedListener {
        void fileSelected(File file);
    }
    public interface DirectorySelectedListener {
        void directorySelected(File directory);
    }
    public interface BitmapSelectedListener {
        void bitmapSelected(Bitmap bitmap);
    }
    private ListenerList<FileSelectedListener> fileListenerList = new ListenerList<FileDialog.FileSelectedListener>();
    private ListenerList<DirectorySelectedListener> dirListenerList = new ListenerList<FileDialog.DirectorySelectedListener>();
    private ListenerList<BitmapSelectedListener> bitmapListenerList = new ListenerList<FileDialog.BitmapSelectedListener>();
    private final Activity mActivity;
    private boolean selectDirectoryOption;
    private String[] mFileEndsWith;
    private boolean fileHistoryEnabled = false;
    private DialogInterface.OnClickListener cancelButtonClickListener;
    private Stack<String> mFileHistory;
    private boolean imagePreviewEnabled = false;
    private BitmapFileFilter mBitmapFileFilter;
    // TODO: Build out image file preview window
    // TODO: Add icon assets for files vs folders and implement a custom listview adapter.

    public FileDialog(Activity activity, File path) {
        mActivity = activity;
        if (!path.exists()) path = Environment.getExternalStorageDirectory();
        loadFileList(path);
    }

    public FileDialog(Activity activity, File path, String fileEndsWith) {
        mActivity = activity;
        setFileEndsWith(fileEndsWith);
        if (!path.exists()) path = Environment.getExternalStorageDirectory();
        loadFileList(path);
    }

    public FileDialog(Activity activity, File path, String[] fileEndsWith) {
        mActivity = activity;
        setFileEndsWith(fileEndsWith);
        if (!path.exists()) path = Environment.getExternalStorageDirectory();
        loadFileList(path);
    }

    @Override
    public void update(Observable observable, Object o) {
        // This assumes o is a ImageDialog.ImagePayload from ImageDialog. This will change if/when more observables are introduced.
        ImageDialog.ImagePayload payload = (ImageDialog.ImagePayload) o;
        if (payload.isPositive) {
            fireBitmapSelectedEvent(payload.bitmap);
        } else {
            showDialog();
        }
    }

    /**
     * @return file dialog
     */
    public Dialog createFileDialog() {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(mCurrentPath.getPath());
        if (selectDirectoryOption) {
            builder.setPositiveButton("Select directory", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG, mCurrentPath.getPath());
                    fireDirectorySelectedEvent(mCurrentPath);
                }
            });
        }

        if (fileHistoryEnabled && historyCanGoBack()) {
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            String parent = getHistoryGoBackPath();
                            File newFile = new File(parent);
                            loadFileList(newFile);
                            dialog.cancel();
                            dialog.dismiss();
                            showDialog();
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

        builder.setItems(mFileList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String fileChosen = mFileList[which];
                File chosenFile = getChosenFile(fileChosen);
                if (chosenFile.isDirectory()) {
                    loadFileList(chosenFile);
                    dialog.cancel();
                    dialog.dismiss();
                    showDialog();
                } else if (imagePreviewEnabled) {
                    if (passesImageFilterCheck(chosenFile)) {
                        //TODO: Launch imageDialog here.
                        Bitmap imageFileBitmap = mBitmapFileFilter.retrieveStoredBitmap();
                        ImageDialog imgDialog = new ImageDialog(mActivity, imageFileBitmap);
                        imgDialog.addObserver(FileDialog.this);
                        imgDialog.showDialog();
                    } else {
                        String message = String.format(
                                "The image you selected does not meet the resolution target of %d x %d. Would you like to try again?",
                                mBitmapFileFilter.getTargetWidth(),
                                mBitmapFileFilter.getTargetHeight());
                        DialogInterface.OnClickListener onButtonClick =
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    showDialog();
                                } else {
                                    Log.i(TAG, "Image Loading Failure: User cancelled the file dialog.");
                                }
                            }
                        };
                        new AlertDialog.Builder(mActivity)
                                .setTitle("Image Failure")
                                .setMessage(message)
                                .setPositiveButton("Yes", onButtonClick)
                                .setNegativeButton("No", onButtonClick)
                                .create()
                                .show();
                    }
                } else fireFileSelectedEvent(chosenFile);
            }
        });

        if (cancelButtonClickListener != null) {
            builder.setNegativeButton("Cancel", cancelButtonClickListener);
        }

        dialog = builder.create();
        return dialog;
    }


    public void addFileListener(FileSelectedListener listener) {
        fileListenerList.add(listener);
    }

    public void removeFileListener(FileSelectedListener listener) {
        fileListenerList.remove(listener);
    }

    /**
     * Set whether the file history feature is enabled or disabled on your file dialog.
     * @param isEnabled Boolean true if you want the file history feature enabled, or false if otherwise.
     */
    public void setFileHistoryEnabled(boolean isEnabled) {

        if (fileHistoryEnabled != isEnabled) {
            if (isEnabled) {
                fileHistoryEnabled = true;
                mFileHistory = new Stack<String>();
                historyAddCurrent();
            } else {
                fileHistoryEnabled = false;
                mFileHistory = null;
            }
        }
    }

    public void setCancelButtonClickListener(DialogInterface.OnClickListener listener) {

        cancelButtonClickListener = listener;
    }

    public void setSelectDirectoryOption(boolean selectDirectoryOption) {
        this.selectDirectoryOption = selectDirectoryOption;
    }

    public void addDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.add(listener);
    }

    public void removeDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.remove(listener);
    }

    /**
     * Set whether the image preview feature is enabled or disabled on your file dialog.
     * @param isEnabled Boolean true if you want the image preview feature enabled, or false if otherwise.
     */
    public void setImagePreviewEnabled(boolean isEnabled) {

        if (imagePreviewEnabled != isEnabled) {
            if (isEnabled) {
                imagePreviewEnabled = true;
                mBitmapFileFilter = new BitmapFileFilter();
                mBitmapFileFilter.setShouldStoreBitmap(true);
            } else {
                imagePreviewEnabled = false;
                mBitmapFileFilter = null;
            }
        }
    }

    public void setImageResolutionFilter(int targetWidth, int targetHeight) {

        if (!imagePreviewEnabled) {
            setImagePreviewEnabled(true);
        }
        mBitmapFileFilter.setTargetResolution(targetWidth, targetHeight);
    }

    public void addBitmapListener(BitmapSelectedListener listener) {
        bitmapListenerList.add(listener);
    }

    public void removeBitmapListener(BitmapSelectedListener listener) {
        bitmapListenerList.remove(listener);
    }

    /**
     * Show file dialog
     */
    public void showDialog() {
        createFileDialog().show();
    }

    private void fireFileSelectedEvent(final File file) {
        fileListenerList.fireEvent(new ListenerList.FireHandler<FileSelectedListener>() {
            public void fireEvent(FileSelectedListener listener) {
                listener.fileSelected(file);
            }
        });
    }

    private void fireDirectorySelectedEvent(final File directory) {
        dirListenerList.fireEvent(new ListenerList.FireHandler<DirectorySelectedListener>() {
            public void fireEvent(DirectorySelectedListener listener) {
                listener.directorySelected(directory);
            }
        });
    }

    private void fireBitmapSelectedEvent(final Bitmap bitmap) {
        bitmapListenerList.fireEvent(new ListenerList.FireHandler<BitmapSelectedListener>() {
            public void fireEvent(BitmapSelectedListener listener) {
                listener.bitmapSelected(bitmap);
            }
        });
    }

    private void loadFileList(File path) {
        this.mCurrentPath = path;
        List<String> r = new ArrayList<String>();
        if (path.exists()) {
            if (path.getParentFile() != null) r.add(PARENT_DIR);
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    if (!sel.canRead()) return false;
                    if (selectDirectoryOption) return sel.isDirectory();
                    else {
                        boolean endsWith = checkFileEndsWith(filename);
                        return endsWith || sel.isDirectory();
                    }
                }
            };
            String[] fileList1 = path.list(filter);
            for (String file : fileList1) {
                r.add(file);
            }
        }
        if (fileHistoryEnabled) {
            historyAddCurrent();
        }
        mFileList = (String[]) r.toArray(new String[]{});
    }

    private File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) return mCurrentPath.getParentFile();
        else return new File(mCurrentPath, fileChosen);
    }

    public void setFileEndsWith(String fileEndsWith) {

        if (fileEndsWith != null) {
            this.mFileEndsWith = new String[1];
            this.mFileEndsWith[0] = fileEndsWith.toLowerCase();
        } else {
            this.mFileEndsWith = null;
        }
    }

    public void setFileEndsWith(String[] fileEndsWith) {

        this.mFileEndsWith = fileEndsWith;
    }

    private boolean checkFileEndsWith(String filename) {

        for (String endsWith : this.mFileEndsWith) {
            if (filename.toLowerCase().endsWith(endsWith)) {
                return true;
            }
        }
        return false;
    }

    private boolean passesImageFilterCheck(File file) {

        return mBitmapFileFilter.accept(file);
    }

    /**
     * Checks to see if you have a history object to navigate back to.
     * @return Returns true if you can go back, false if not.
     */
    private boolean historyCanGoBack() {

        return (mFileHistory.size() > 1);
    }

    /**
     * Removes the last two history items and returns the parent item.
     * @return Returns the prior item in your file history stack.
     */
    private String getHistoryGoBackPath() {

        // Removing the CURRENT directory item from history!
        mFileHistory.pop();
        // Removing and RETURNING the parent history item! (Where you're probably going?)
        return mFileHistory.pop();
    }

    /**
     * Add the current path to the top of the history stack.
     */
    private void historyAddCurrent() {

        if (mCurrentPath != null) {
            mFileHistory.push(mCurrentPath.getPath());
        }
    }
}

