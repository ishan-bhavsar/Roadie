package com.example.gptsi.roadie.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.gptsi.roadie.Adapter.adapter_items;
import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.image_picker;
import com.example.gptsi.roadie.Util.web.uploadFile;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.example.gptsi.roadie.Util.image_picker.PICK_MULTIPLE_IMAGE_REQUEST;
import static com.example.gptsi.roadie.Util.image_picker.REQUEST_IMAGE_CAPTURE;

public class Items{

    Context mContext;
    View mView;
    Button camera,gallery;
    ImageButton delete;
    Toolbar toolbar;
    RecyclerView recyclerView;
    adapter_items adapter;
    ArrayList<String> mList;
    ArrayList<String> mPathList;
    SharedPreferences sharedPreferences;
    SharedPreferences itemPref;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor itemEditor;
    image_picker picker;
    String TAG = Items.class.getSimpleName();

    public Items(Context context, View view){

        this.mContext = context;
        this.mView = view;

        camera = mView.findViewById(R.id.items_cam);
        gallery = mView.findViewById(R.id.items_gal);
        recyclerView = mView.findViewById(R.id.items_recycler_view);
        delete = mView.findViewById(R.id.items_delete);
        toolbar = mView.findViewById(R.id.appbar);

        toolbar.setTitle("Items");
//TODO        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Items.this,Add_Request.class));
//                finish();
//            }
//        });

        sharedPreferences = mContext.getSharedPreferences("AddRequest",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        itemPref = mContext.getSharedPreferences("Items",MODE_PRIVATE);
        itemEditor = itemPref.edit();

        mList = new ArrayList<>();
        mPathList = new ArrayList<>();
        if(restore().size()>0) {
            mPathList = restore();
         }

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext,2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new adapter_items(mContext, mPathList);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        picker= new image_picker(mContext);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.openCamera();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.openGallery(true);
            }
        });

       delete.setVisibility(View.GONE);
       delete.setColorFilter(Color.WHITE);
       delete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               camera.setEnabled(true);
               gallery.setEnabled(true);
               mPathList.removeAll(adapter.deleteImages());
               savePref(mPathList);
               adapter.notifyDataSetChanged();
               delete.setVisibility(View.GONE);
           }
       });
    }

    public void submit(){
        editor.putString("item_count","" + mPathList.size());
        editor.commit();
//TODO        itemEditor.clear();
//        itemEditor.commit();
        uploadFile file = new uploadFile();
        for (String path: mPathList){
               file.upload(path,false);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        picker.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_MULTIPLE_IMAGE_REQUEST) {
            mList = picker.getPathList();
            if(mList.size()>0) {
                mPathList.addAll(mList);
                savePref(mPathList);
            }
        }else if(requestCode == REQUEST_IMAGE_CAPTURE){
            String path = picker.getPath();
            if(path.length()>0) {
                mPathList.add(path);
                savePref(mPathList);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void savePref(ArrayList<String> pathList){
        itemEditor.clear();
        for (int i = 0;i<pathList.size();i++) {
            itemEditor.putString(""+i,pathList.get(i));
        }
        itemEditor.commit();
    }

    private ArrayList<String> restore(){
        ArrayList<String> pathList = new ArrayList<>();

        for (int i = 0;i<itemPref.getAll().size();i++) {
            pathList.add(itemPref.getString(""+i,""));
        }
        return pathList;
    }
}


//TODO
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        // Save the user's current game state
//        savedInstanceState.putStringArrayList("PathList", mPathList);
//        Log.d(TAG,"List"+mPathList);
//        // Always call the superclass so it can save the view hierarchy state
//        super.onSaveInstanceState(savedInstanceState);
//    }
//
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        // Always call the superclass so it can restore the view hierarchy
//        super.onRestoreInstanceState(savedInstanceState);
//
//
//        // Restore state members from saved instance
//        mPathList.addAll(savedInstanceState.getStringArrayList("PathList"));
//        Log.d(TAG,"List"+mPathList);
//        adapter.notifyDataSetChanged();
//    }
