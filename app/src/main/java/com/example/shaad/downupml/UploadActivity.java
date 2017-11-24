package com.example.shaad.downupml;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shaad.downupml.Model.DownUpFile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

public class UploadActivity extends AppCompatActivity {
    private static final int FILE_SELECT_CODE = 0;
    Button choose, upload;
    MaterialEditText fileName;
    String fileName_str;
    TextView details;
    Uri uri;
    StorageReference mDatabase;
    DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        choose = (Button) findViewById(R.id.choose);
        fileName = (MaterialEditText) findViewById(R.id.fileName);
        upload = (Button) findViewById(R.id.upload);

        mDatabase = FirebaseStorage.getInstance().getReference();

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName_str = fileName.getText().toString();
                uploadFile();
            }
        });

    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        details = (TextView) findViewById(R.id.details);
        uri = data.getData();
        details.setText(uri.toString());
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void uploadFile() {

        mDatabase = FirebaseStorage.getInstance().getReference();
        if (this.fileName_str.isEmpty()) {
            this.fileName_str = getFileName(uri);
        }

        StorageReference riversRef = mDatabase.child(this.fileName_str);

        UploadTask uploadTask = riversRef.putFile(uri);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressDialog.dismiss();
                Toast.makeText(UploadActivity.this, "uploadFail", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();


                details = (TextView) findViewById(R.id.details);
                Resources res = getResources();
                String upload_msg = res.getString(R.string.upload_msg);
                details.setText(upload_msg);
                fileName = (MaterialEditText) findViewById(R.id.fileName);
                fileName.setText("");

                @SuppressWarnings("VisibleForTests") String name = taskSnapshot.getMetadata().getName();
                @SuppressWarnings("VisibleForTests") String type = taskSnapshot.getMetadata().getContentType();
                @SuppressWarnings("VisibleForTests") long size = taskSnapshot.getMetadata().getSizeBytes();
                @SuppressWarnings("VisibleForTests") String download_url = taskSnapshot.getDownloadUrl().toString();


                writeNewImageInfoToDB(name, type, size, download_url);

            }
        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
            }
        });

    }

    private void writeNewImageInfoToDB(String name, String type, long size, String download_url) {

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        DownUpFile down = new DownUpFile(name, type, String.valueOf((float) size / 1000), download_url);

        mDatabaseRef.push().setValue(down);

    }


}
