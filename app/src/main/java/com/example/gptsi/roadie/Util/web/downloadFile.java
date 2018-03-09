package com.example.gptsi.roadie.Util.web;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by GPTSI on 24-01-2018.
 */

/**
 * Background Async Task to download file
 */
public class downloadFile extends AsyncTask<String, String, String> {
    private Context mContext;
    private ProgressDialog mDialog;
    private Boolean mShowDialog = false;
    private String mPath;
    static final String TAG = downloadFile.class.getSimpleName();

    public downloadFile(Context context){
        this.mContext = context;
        askPermission();
    }

    public boolean askPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Check if we have permission
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                // If don't have permission so prompt the user.
                ((Activity)mContext).requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        0
                );
                return false;
            }
        }
        return true;
    }

    public void setShowDialog(Context mContext, Boolean mShowDialog) {
        this.mShowDialog = mShowDialog;
        if(mShowDialog) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Downloading file. Please wait...");
            mDialog.setIndeterminate(false);
            mDialog.setMax(100);
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setCancelable(true);
        }
    }

    public String download(String file_url){
        try {

            URL url = new URL(file_url);

            File SDCardRoot = Environment.getExternalStorageDirectory(); // location where you want to store
            File directory = new File(SDCardRoot, "/Roadie/"); //create directory to keep your downloaded file
            if (!directory.exists())
            {
                directory.mkdir();
            }
            String filePath = url.getFile();
            String fileName = filePath.substring(filePath.lastIndexOf('/'));
            mPath = directory + fileName;
            execute(file_url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return mPath;
    }
    /**
     * Before starting background thread Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(mShowDialog)
            mDialog.show();
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {

            URL url = new URL(f_url[0]);
            URLConnection conn = url.openConnection();
            conn.connect();

            // this will be useful so that you can show a typical 0-100%
            // progress bar
            int lenghtOfFile = conn.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);

//TODO            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());

            // Output stream
            OutputStream output = new FileOutputStream(mPath);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e(TAG,"Error: "+e.getMessage());
        }

        return null;
    }

    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        if(mShowDialog)
            mDialog.setProgress(Integer.parseInt(progress[0]));
    }

    /**
     * After completing background task Dismiss the progress dialog
     **/
    @Override
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after the file was downloaded
        if(mShowDialog)
            mDialog.dismiss();
    }
}
