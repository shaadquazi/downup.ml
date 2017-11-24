package com.example.shaad.downupml;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.shaad.downupml.Model.DownUpFile;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    Button UploadToDownUp;
    MaterialEditText search;
    RecyclerView list;
    FirebaseStorage mFirebaseStorage;
    DatabaseReference mDatabaseRef;
    ImageButton refresh;
    private FirebaseRecyclerAdapter<DownUpFile, ViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        refresh = (ImageButton) findViewById(R.id.refresh);
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
                filter(s.toString());
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.notifyDataSetChanged();
            }
        });

        mAdapter = new FirebaseRecyclerAdapter<DownUpFile, ViewHolder>(DownUpFile.class, R.layout.each_row, ViewHolder.class, mDatabaseRef) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, final DownUpFile model, int position) {


                Resources res = getResources();
                String size = String.format(res.getString(R.string.download_unit), model.getFileSize());

                viewHolder.fname.setText(model.getFileName());
                viewHolder.ftype.setText(model.getFileType());
                viewHolder.fsize.setText(size);
                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Query query = mDatabaseRef.orderByChild("fileName").equalTo(model.getFileName());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot record : dataSnapshot.getChildren()) {
                                    record.getRef().removeValue();
                                }

                                StorageReference file_ref = mFirebaseStorage.getReferenceFromUrl(model.getFileDownload());
                                file_ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "File Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(MainActivity.this, "Failed! Try Again.", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });
                viewHolder.download.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
                    @Override
                    public void onClick(View v) {
                        haveStoragePermission();

                        String ext = model.getFileType().substring(model.getFileType().lastIndexOf("/") + 1);

                        switch (ext) {
                            case "vnd.android.package-archive":
                                ext = "apk";
                                break;
                            case "msword":
                                ext = "doc";
                                break;
                        }


                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(model.getFileDownload()));
                        request.setDescription(model.getFileType());
                        request.setTitle(model.getFileName());
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, model.getFileName() + "." + ext);
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
        } else {
            Log.e("Permission error", "You already have the permission");
            return true;
        }
    }

    private void filter(String s) {
    }


}
