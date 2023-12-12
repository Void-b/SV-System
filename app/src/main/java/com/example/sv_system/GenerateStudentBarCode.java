package com.example.sv_system;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GenerateStudentBarCode extends AppCompatActivity {

    QRGEncoder qrgEncoder;
    private ImageView qrCodeIV;
    private EditText regno, name, department;
    private Button generateQrBtn;
    Bitmap bitmap;
    CardView cardView, printcard;
    TextView studentname, studentreg, studentdept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_student_barcode);
        // initializing all variables.
        qrCodeIV = findViewById(R.id.idIVQrcode);
        regno = findViewById(R.id.regno);
        name = findViewById(R.id.name);
        department = findViewById(R.id.department);
        studentname = findViewById(R.id.studentname);
        studentdept = findViewById(R.id.studentdepart);
        studentreg = findViewById(R.id.studentReg);
        generateQrBtn = findViewById(R.id.idBtnGenerateQR);
        printcard = findViewById(R.id.printCard);
        cardView = findViewById(R.id.card);

        // initializing onclick listener for button.
        generateQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(regno.getText().toString())) {

                    // if the edittext inputs are empty then execute
                    // this method showing a toast message.
                    Toast.makeText(GenerateStudentBarCode.this, "Enter some text to generate QR Code", Toast.LENGTH_SHORT).show();
                } else {
                    studentname.setText(name.getText().toString().trim());
                    studentreg.setText(regno.getText().toString().trim());
                    studentdept.setText(department.getText().toString().trim());
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
                            name.getText().toString()+"\n"+
                                    department.getText().toString()+"\n"+
                                    regno.getText().toString()
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
            }
        });

        printcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GenerateStudentBarCode.this, "Process Started", Toast.LENGTH_SHORT).show();
                saveBitmapToFile(getBitmapFromView(cardView), regno.getText().toString().toString());
            }
        });
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
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "gg "+e.getMessage(), Toast.LENGTH_SHORT).show();
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