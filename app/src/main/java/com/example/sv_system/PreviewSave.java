package com.example.sv_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class PreviewSave extends AppCompatActivity {

    CardView cardView, goback;
    Button save;
    TextView studentname, studentreg, studentdept;
    QRGEncoder qrgEncoder;
    private ImageView qrCodeIV;
    private Button generateQrBtn;
    Bitmap bitmap;
    String name, regno, department;
    Bitmap imageb;
    private StorageReference mStorageRef;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_save);


        qrCodeIV = findViewById(R.id.idIVQrcode);
        studentname = findViewById(R.id.studentname);
        studentdept = findViewById(R.id.studentdepart);
        studentreg = findViewById(R.id.studentReg);
        cardView = findViewById(R.id.card);
        goback = findViewById(R.id.goback);
        save = findViewById(R.id.save);
        progressBar = findViewById(R.id.uploadprogress);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                save.setText("Processing...");
                Toast.makeText(PreviewSave.this, "Process Started", Toast.LENGTH_SHORT).show();
                uploadAndGetReference();
            }
        });
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    Uri convertbitmaptoUri(){
        // Assuming 'bitmap' is your Bitmap object

        // Convert Bitmap to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageb.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // Create a ByteArrayInputStream from the byte array
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);

        // Get a content resolver instance
        ContentResolver contentResolver = getContentResolver();

        // Define a ContentValues object to hold the image metadata
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        // Insert the image data into a new URI using the content resolver
        Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // Write the input stream into the opened URI
        try {
            OutputStream outputStream = contentResolver.openOutputStream(uri);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
            save.setText("Please wait...");
            Toast.makeText(this, "Convert Successful", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Now you can use 'uri' as needed

        return uri;
    }

    public void uploadAndGetReference(){
        save.setText("Uploading...");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // Now we have converted the passed bitmap into a Uri file
        Uri file = convertbitmaptoUri();

        // Create a reference to "images/" + random UUID
        StorageReference imagesRef = mStorageRef.child("UserImages/" + regno);
//                UUID.randomUUID().toString());

        // Upload file to Firebase Storage
        imagesRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully
                        save.setText("Save");
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(PreviewSave.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        saveBitmapToFile(getBitmapFromView(cardView), regno.toString().toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle unsuccessful uploads
                        save.setText("Save");
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(PreviewSave.this, "Upload failed\n"+e.getMessage()
                                , Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        name = bundle.getString("name");
        regno = bundle.getString("regno");
        department = bundle.getString("department");
        if (intent != null){
            imageb = intent.getParcelableExtra("bitmap");
        }else {
            Toast.makeText(this, "Bitmap passed is null", Toast.LENGTH_SHORT).show();
        }

        studentname.setText(name);
        studentreg.setText(regno);
        studentdept.setText(department);
        // below line is for getting
        // the windowmanager service.
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(
                name+"\n"+
                        department+"\n"+
                        regno
                , null, QRGContents.Type.TEXT, dimen);
        qrgEncoder.setColorBlack(Color.WHITE);
        qrgEncoder.setColorWhite(Color.BLACK);
        try {
            // getting our qrcode in the form of bitmap.
            bitmap = qrgEncoder.getBitmap();
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            qrCodeIV.setImageBitmap(bitmap);
        } catch (Exception e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }
    }

    public Bitmap getBitmapFromView(View view) {
        // Measure the view and its content to determine the resulting bitmap size.
        int desiredWidth = 759; // in pixels
        int desiredHeight = 420;


        // Adjust layout parameters of the CardView
        ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
        layoutParams.width = desiredWidth;
        layoutParams.height = desiredHeight;
        cardView.setLayoutParams(layoutParams);

        // Measure and layout the view
        cardView.measure(View.MeasureSpec.makeMeasureSpec(desiredWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(desiredHeight, View.MeasureSpec.EXACTLY));
        cardView.layout(0, 0, cardView.getMeasuredWidth(), cardView.getMeasuredHeight());



        Bitmap bitmap = Bitmap.createBitmap(desiredWidth, desiredHeight, Bitmap.Config.ARGB_8888);

        // Create a canvas with the bitmap
        Canvas canvas = new Canvas(bitmap);
        cardView.layout(0, 0, desiredWidth, desiredHeight);
        cardView.draw(canvas);

        return bitmap;
    }
    public void saveBitmapToFile(Bitmap bitmap, String filename) {
        // Get external storage directory.
        File externalStorageDirectory = Environment.getExternalStorageDirectory();

        // Create a directory for storing images if it doesn't exist.
        File imagesDirectory = new File(externalStorageDirectory, "IdCards");
        if (!imagesDirectory.exists()) {
            Toast.makeText(this, "if condition", Toast.LENGTH_SHORT).show();
            imagesDirectory.getParentFile().mkdir();
        }

        // Create a file to save the Bitmap image.
        File imageFile = new File(imagesDirectory, filename+".jpg");

        // Open an OutputStream to write the Bitmap data.
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            // Inform the user that the image has been saved.
            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            save.setText("Save");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}