package com.radicalninja.filedialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FileDialog {

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
    private ListenerList<FileSelectedListener> fileListenerList = new ListenerList<FileDialog.FileSelectedListener>();
    private ListenerList<DirectorySelectedListener> dirListenerList = new ListenerList<FileDialog.DirectorySelectedListener>();
    private final Activity mActivity;
    private boolean selectDirectoryOption;
    private String[] mFileEndsWith;
    private Stack<String> mFileHistory;

    public FileDialog(Activity activity, File path) {
        mFileHistory = new Stack<String>();
        mActivity = activity;
        if (!path.exists()) path = Environment.getExternalStorageDirectory();
        loadFileList(path);
    }

    public FileDialog(Activity activity, File path, Stack<String> historyObject) {
        mFileHistory = historyObject;
        mActivity = activity;
        if (!path.exists()) path = Environment.getExternalStorageDirectory();
        loadFileList(path);
    }

    public FileDialog(Activity activity, File path, String fileEndsWith) {
        mFileHistory = new Stack<String>();
        mActivity = activity;
        setFileEndsWith(fileEndsWith);
        if (!path.exists()) path = Environment.getExternalStorageDirectory();
        loadFileList(path);
    }

    public FileDialog(Activity activity, File path, String fileEndsWith, Stack<String> historyObject) {
        mFileHistory = historyObject;
        mActivity = activity;
        setFileEndsWith(fileEndsWith);
        if (!path.exists()) path = Environment.getExternalStorageDirectory();
        loadFileList(path);
    }

    public FileDialog(Activity activity, File path, String[] fileEndsWith) {
        mFileHistory = new Stack<String>();
        mActivity = activity;
        setFileEndsWith(fileEndsWith);
        if (!path.exists()) path = Environment.getExternalStorageDirectory();
        loadFileList(path);
    }

    public FileDialog(Activity activity, File path, String[] fileEndsWith, Stack<String> historyObject) {
        mFileHistory = historyObject;
        mActivity = activity;
        setFileEndsWith(fileEndsWith);
        if (!path.exists()) path = Environment.getExternalStorageDirectory();
        loadFileList(path);
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

        builder.setItems(mFileList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String fileChosen = mFileList[which];
                File chosenFile = getChosenFile(fileChosen);
                if (chosenFile.isDirectory()) {
                    loadFileList(chosenFile);
                    dialog.cancel();
                    dialog.dismiss();
                    showDialog();
                } else fireFileSelectedEvent(chosenFile);
            }
        });

        dialog = builder.create();
        return dialog;
    }


    public void addFileListener(FileSelectedListener listener) {
        fileListenerList.add(listener);
    }

    public void removeFileListener(FileSelectedListener listener) {
        fileListenerList.remove(listener);
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
}

