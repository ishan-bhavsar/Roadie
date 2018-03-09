package com.example.gptsi.roadie.Util.web;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;

/**
 * Created by GPTSI on 05-01-2018.
 */

public class uploadFile {

    class uploadObj {

        public String sourceFileUri;
        public Boolean deleteSourceFile;
        public String serverFileName;

        public uploadObj(String sourceFileUri, Boolean deleteSourceFile, String serverFileName) {
            this.sourceFileUri = sourceFileUri;
            this.deleteSourceFile = deleteSourceFile;
            this.serverFileName = serverFileName;
        }
    }

    static final String TAG = uploadFile.class.getSimpleName();
    Queue<uploadObj> mUploadQueue;
    Boolean inProgress = false;
    int numFiles = 1;

    public uploadFile() {
        this.mUploadQueue = new LinkedList<uploadObj>();
    }

    public String upload(String sourceFileUri, final Boolean deleteSourceFile) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US).format(new Date());
        String fileName = "IMG" + timeStamp + ".jpg";
        String serverPath = IP_ADDRESS + "Uploads/" + fileName;
        uploadObj obj = new uploadObj(sourceFileUri, deleteSourceFile, fileName);
        mUploadQueue.add(obj);
        if (!inProgress)
            uploadHelper();
        return serverPath;
    }

    public void uploadHelper() {

        inProgress = true;

        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();

                while (mUploadQueue.size() > 0) {

                    uploadObj obj = mUploadQueue.poll();

                    final String filePath, fileName, serverPath;

                    final Boolean deleteSourceFile = obj.deleteSourceFile;
                    fileName = obj.serverFileName;
                    filePath = obj.sourceFileUri;
                    serverPath = IP_ADDRESS + "Uploads/" + fileName;

                    final File sourceFile;
                    sourceFile = new File(filePath);

                    if (!sourceFile.isFile()) {

                        Log.e(TAG, "Source File not exist :" + filePath);
                        continue;

                    } else {
                        Log.d(TAG, "Upload Helper : " + numFiles++);

                        int serverResponseCode;

                        HttpURLConnection conn = null;
                        DataOutputStream dos = null;
                        String lineEnd = "\r\n";
                        String twoHyphens = "--";
                        String boundary = "*****";
                        int bytesRead, bytesAvailable, bufferSize;
                        byte[] buffer;
                        int maxBufferSize = 1 * 1024 * 1024;
                        try {
                            // open a URL connection to the Servlet
                            FileInputStream fileInputStream = new FileInputStream(sourceFile);
                            URL url = new URL(IP_ADDRESS + "uploadfile.php");

                            // Open a HTTP  connection to  the URL
                            conn = (HttpURLConnection) url.openConnection();
                            conn.setDoInput(true); // Allow Inputs
                            conn.setDoOutput(true); // Allow Outputs
                            conn.setUseCaches(false); // Don't use a Cached Copy
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Connection", "Keep-Alive");
                            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                            conn.setRequestProperty("uploaded_file", filePath);

                            dos = new DataOutputStream(conn.getOutputStream());

                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                                    + fileName + "\"" + lineEnd);

                            dos.writeBytes(lineEnd);

                            // create a buffer of  maximum size
                            bytesAvailable = fileInputStream.available();

                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            buffer = new byte[bufferSize];

                            // read file and write it into form...
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                            while (bytesRead > 0) {

                                dos.write(buffer, 0, bufferSize);
                                bytesAvailable = fileInputStream.available();
                                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                            }

                            // send multipart form data necesssary after file data...
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                            // Responses from the server (code and message)
                            serverResponseCode = conn.getResponseCode();
                            String serverResponseMessage = conn.getResponseMessage();

                            Log.i("uploadFile", "HTTP Response is : "
                                    + serverResponseMessage + ": " + serverResponseCode);

                            if (serverResponseCode == 200) {

                                String msg = "File Upload Completed.\n\n See uploaded file here : \n\n" + serverPath;
                                Log.d(TAG, msg);
                            }

                            //close the streams //
                            fileInputStream.close();
                            dos.flush();
                            dos.close();

                            if (deleteSourceFile == true) {
                                sourceFile.delete();
                            }

                        } catch (MalformedURLException ex) {
                            ex.printStackTrace();
                            Log.e(TAG, "Upload file to server error: " + ex.getMessage(), ex);
                            mUploadQueue.add(obj);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "Got Exception : see logcat " + e.getMessage(), e);
                            mUploadQueue.add(obj);
                        }
                    }
                }
                inProgress = false;
                numFiles = 1;
            }
        };
        t.start();
    }
}
