package com.example.gptsi.roadie.Util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by GPTSI on 02-01-2018.
 */

public class image_picker {

    String mPath = null;
    ArrayList<String> mPathList = null;
    Bitmap mImage = null;
    ArrayList<Bitmap> mImageList = null;
    Context mContext;

    final static String TAG = image_picker.class.getSimpleName();

    public static final int PICK_IMAGE_REQUEST = 149;
    public static final int PICK_MULTIPLE_IMAGE_REQUEST = 150;
    public static final int REQUEST_IMAGE_CAPTURE = 151;

    public image_picker(Context context) {
        this.mContext = context;
        mPathList = new ArrayList<>();
        mImageList = new ArrayList<>();
        askPermission();
    }

    public ArrayList<String> getPathList() {
        return mPathList;
    }

    public String getPath() {
        return mPath;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public ArrayList<Bitmap> getImageList() {
        return mImageList;
    }


    public boolean askPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Check if we have permission
            int permission = ActivityCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.CAMERA);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                ((Activity) mContext).requestPermissions(
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        0
                );
                return false;
            }
        }
        return true;
    }

    public void openGallery(Boolean multipleImages) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if(multipleImages){
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            ((Activity) mContext).startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_MULTIPLE_IMAGE_REQUEST);
        }else
            ((Activity) mContext).startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

        return;
    }

    public void openCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            ((Activity) mContext).startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }

        return;
    }

    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if (requestCode == PICK_MULTIPLE_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {

            mPathList = getFilePath(mContext, data,true);
            for (int i=0;i<mPathList.size();i++) {
                mImageList.add(decodeFile(mPathList.get(i)));
            }


        } if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            mPath = getFilePath(mContext, data,false).get(0);
            mImage = decodeFile(mPath);

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {

            mImage = (Bitmap) data.getExtras().get("data");
            mPath = getFilePath(mContext,mImage);
        }

        String msg = "File Path: " + mPath+"\nImage"+mImage+"\nPathList: "+mPathList+"\nImageList: "+mImageList;
        Log.d(TAG, msg);
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

//          For Testing:
//
//            new Thread(new Runnable() {
//                public void run() {
//
//                    Log.d(TAG,"uploading started.....");
//                    uploadFile u = new uploadFile();
//                    Log.d(TAG,"Server Path: "+u.upload(mPath,false));
//
//                }
//            }).start();
    }

    /**
     * Get the file path for Camera
     *
     * @param context    The context.
     * @param image      The Bitmap image.
     * @return           file path.
     */
    public String getFilePath(Context context, Bitmap image) {

        if(image!=null) {

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "Title", null);

            Cursor cursor = mContext.getContentResolver().query(Uri.parse(path), null, null, null, null);
            cursor.moveToFirst();
            int id = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(id);
        }else
            return null;
    }

    /**
     * Get the file path for Gallery
     *
     * @param context    The context.
     * @param data       The Intent of onActivityResult.
     * @return           file path.
     */
    @SuppressLint("NewApi")
    private static ArrayList<String> getFilePath(final Context context, Intent data, Boolean multiple) {

        ArrayList<String> path = new ArrayList<>();
        Uri uri;

        if(multiple) {

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Log.e(TAG,"Data :\n"+data);
            Log.e(TAG,"Data.getdata :\n"+data.getData());
            Log.e(TAG,"Data.getclipdata :\n"+data.getClipData());
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    uri = item.getUri();
                    mArrayUri.add(uri);
                    // Get the cursor
                    Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    path.add(cursor.getString(columnIndex));
                    cursor.close();
                }
                Log.v(TAG, "Selected Images" + mArrayUri.size());
                return path;
            }else if(data.getData()!=null){

                Uri mImageUri=data.getData();

                // Get the cursor
                Cursor cursor = context.getContentResolver().query(mImageUri,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                path.add( cursor.getString(columnIndex));
                cursor.close();
                return path;
            }
            else {
                return null;
            }
        }else{

            uri = data.getData();

            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        path.add(Environment.getExternalStorageDirectory() + "/" + split[1]);
                        return path;
                    }
                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    path.add(getDataColumn(context, contentUri, null, null));
                    return path;
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    path.add(getDataColumn(context, contentUri, selection, selectionArgs));
                    return path;
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                path.add(getDataColumn(context, uri, null, null));
                return path;
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                path.add(uri.getPath());
                return path;
            }
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}