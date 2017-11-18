package com.example.shaad.downupml;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.rengwuxian.materialedittext.MaterialEditText;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    Button UploadToDownUp;
    MaterialEditText search;
    RecyclerView list;
    FirebaseStorage fbStore;
    DatabaseReference mDatabaseRef;


    private FirebaseRecyclerAdapter<DownUpFile, ViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fbStore = FirebaseStorage.getInstance();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        list = (RecyclerView) findViewById(R.id.list);
        search = (MaterialEditText) findViewById(R.id.search);
        UploadToDownUp = (Button) findViewById(R.id.UploadToDownUp);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                Toast.makeText(MainActivity.this, "" + s.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        mAdapter = new FirebaseRecyclerAdapter<DownUpFile, ViewHolder>(DownUpFile.class, R.layout.each_row, ViewHolder.class, mDatabaseRef) {

            @Override
            protected void populateViewHolder(ViewHolder viewHolder, final DownUpFile model, int position) {

                viewHolder.fname.setText(model.getmFileName());
                viewHolder.ftype.setText(model.getmFileType());
                viewHolder.fsize.setText(model.getmFileSize() + "Kb");
                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(MainActivity.this, "DELETE", Toast.LENGTH_SHORT).show();
                    }
                });
                viewHolder.download.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
                    @Override
                    public void onClick(View v) {
                        haveStoragePermission();


                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(model.getmFileDownload()));

                        request.setDescription(model.getmFileType());
                        request.setTitle(model.getmFileName());


                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, model.getmFileName() + "." + model.getmFileType().substring(model.getmFileType().lastIndexOf("/") + 1));


                        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);


                    }
                });

            }
        };

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        list.setLayoutManager(mLayoutManager);
        list.setItemAnimator(new DefaultItemAnimator());
        list.setHasFixedSize(true);
        list.setAdapter(mAdapter);


        UploadToDownUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(myIntent);


            }
        });
    }

    public boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error", "You have permission");
                return true;
            } else {

                Log.e("Permission error", "You have asked for permission");
                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error", "You already have the permission");
            return true;
        }
    }

    private void filter(String s) {
    }


}
