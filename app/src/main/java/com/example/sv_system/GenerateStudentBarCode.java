package com.example.sv_system;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GenerateStudentBarCode extends AppCompatActivity {

    private EditText regno, name, department;
    private Button generateQrBtn;
    Bitmap bitmap;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    String base64Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_student_barcode);
        // initializing all variables.
        regno = findViewById(R.id.regno);
        name = findViewById(R.id.name);
        department = findViewById(R.id.department);
        generateQrBtn = findViewById(R.id.idBtnGenerateQR);
        imageView = findViewById(R.id.imageview);


        // initializing onclick listener for button.
        generateQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(regno.getText().toString()) &&
                        TextUtils.isEmpty(name.getText().toString())&&
                        TextUtils.isEmpty(department.getText().toString())) {
                    Toast.makeText(GenerateStudentBarCode.this, "Ensure no fields are left empty", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(GenerateStudentBarCode.this, PreviewSave.class);
                    intent.putExtra("name", name.getText().toString().trim());
                    intent.putExtra("regno", regno.getText().toString().trim());
                    intent.putExtra("bitmap", bitmap);
                    intent.putExtra("department", department.getText().toString().trim());
                    startActivity(intent);
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            takePictureIntent.putExtra("crop", "true");
//            takePictureIntent.putExtra("aspectX", 1);
//            takePictureIntent.putExtra("aspectY", 1);
//            takePictureIntent.putExtra("outputX", 1000); // Set your desired width here
//            takePictureIntent.putExtra("outputY", 1000); // Set your desired height here
//            takePictureIntent.putExtra("scale", true);
//            takePictureIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();

            bitmap = imageBitmap;
//                    imageView.getDrawingCache();

            // Convert the Bitmap to a byte array
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 30, byteArrayOutputStream);
//            byte[] byteArray = byteArrayOutputStream.toByteArray();

            // Convert the byte array to a Base64 string
//            base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
    }
}