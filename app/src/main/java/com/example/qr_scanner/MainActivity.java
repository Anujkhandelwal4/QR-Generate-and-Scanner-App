package com.example.qr_scanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.qr_scanner.databinding.ActivityMainBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    QRGEncoder qrgEncoder;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.textView.getText().toString();
                if(text.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter some text to generate QR Code", Toast.LENGTH_SHORT).show();
                } else {

                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);


                    Display display = manager.getDefaultDisplay();


                    Point point = new Point();
                    display.getSize(point);


                    int width = point.x;
                    int height = point.y;

                    // generating dimension from width and height.
                    int dimen = width < height ? width : height;
                    dimen = dimen * 3 / 4;


                    qrgEncoder = new QRGEncoder(binding.editTextTextPersonName.getText().toString(), null, QRGContents.Type.TEXT, dimen);
                    try {
                        // getting our qrcode in the form of bitmap.
                        bitmap = qrgEncoder.encodeAsBitmap();
                        binding.qrCode.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        // this method is called for
                        // exception handling.
                        Log.e("Tag", e.toString());
                    }
                }
            }
        });

        binding.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setCameraId(0);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setPrompt("scanning");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null && intentResult.getContents() != null) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Scan Result")
                    .setMessage(intentResult.getContents())
                    .setPositiveButton("copy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData data =  ClipData.newPlainText("result", intentResult.getContents());
                            manager.setPrimaryClip(data);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}