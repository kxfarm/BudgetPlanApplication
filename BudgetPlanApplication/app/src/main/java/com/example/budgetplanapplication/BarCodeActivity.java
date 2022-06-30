package com.example.budgetplanapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class BarCodeActivity extends AppCompatActivity {
    private SurfaceView tvbarcode;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private TextView text;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGenerator ;
    private String barcodeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code);

        tvbarcode = findViewById(R.id.surfaceView);
        toneGenerator  = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        text = findViewById(R.id.demo);

        scanImage();
    }

    private void scanImage() {
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setRequestedPreviewSize(1920,1080).setAutoFocusEnabled(true).build();
        tvbarcode.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(BarCodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(tvbarcode.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(BarCodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    text.post(new Runnable() {

                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                text.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                                toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            } else {

                                barcodeData = barcodes.valueAt(0).displayValue;
                                toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            }
                            Intent i = new Intent();
                            i.putExtra("barID", String.valueOf(barcodeData));
                            setResult(Activity.RESULT_OK, i);
                            finish();
                        }
                    });

                }
            }


        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        getSupportActionBar().hide();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().hide();
        scanImage();
    }

}