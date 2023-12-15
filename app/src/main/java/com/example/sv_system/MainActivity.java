package com.example.sv_system;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainActivity extends AppCompatActivity {
    Button button;
    CardView scanBtn;
    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private static final int PERMISSION_REQUEST_STORAGE = 2;
    private ImageView imageView;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        scanBtn = findViewById(R.id.scanQR);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new IntentIntegrator(MainActivity.this)
//                        .setOrientationLocked(false)
//                        .setCaptureActivity(CustomScannerActivity.class) // Customize scanner UI if needed
//                        .initiateScan();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                    }else {
                        initQRCodeSanner();
                    }
                }
                else {
                    initQRCodeSanner();
                }
            }
        });

        button = findViewById(R.id.addnew);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
                    }else {
                        Intent intent = new Intent(MainActivity.this, GenerateStudentBarCode.class);
                        startActivity(intent);
                    }
                }
                else {
                    Intent intent = new Intent(MainActivity.this, GenerateStudentBarCode.class);
                    startActivity(intent);
                }
            }
        });
    }
    public void initQRCodeSanner(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setPrompt("Place the QR code inside the view to scan");
        intentIntegrator.initiateScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                initQRCodeSanner();
            }else{
                Toast.makeText(this, "Grant Camera Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void displaydetailsindialog(String result){
        String lines[] = result.split("\\r?\\n");
        TextView name, reg, dept;
//        ImageView imageView;

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.student_details_display_view, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        name = dialogLayout.findViewById(R.id.scanstudentname);
        reg = dialogLayout.findViewById(R.id.scanstudentReg);
        dept = dialogLayout.findViewById(R.id.scanstudentdepart);
        imageView = dialogLayout.findViewById(R.id.scanimageview);

        name.setText(lines[0]);
        dept.setText(lines[1]);
        reg.setText(lines[2]);

        loadUserImage(lines[2]);

        builder.setView(dialogLayout);
        builder.show();
    }
    public void loadUserImage(String name){
        StorageReference imageRef = storageRef.child("UserImages/"+name);

        try {
            // Download the image into a local file
            final File localFile = File.createTempFile("images", "jpg");

            imageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Local file created successfully, load the image to ImageView
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            imageView.setImageBitmap(bitmap);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle failed download
                            imageView.setImageResource(R.drawable.user_image);
                            Toast.makeText(MainActivity.this, "Failed:\n"+exception.getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Get the scan results
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // Handle canceled scan
                Toast.makeText(this, "Scan Canceled", Toast.LENGTH_SHORT).show();
            } else {
                // Process the scanned data (result.getContents())
                String scannedData = result.getContents();
                displaydetailsindialog(scannedData);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}