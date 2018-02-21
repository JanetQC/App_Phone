package com.example.janetdo.toomapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.janetdo.toomapp.Helper.Catalog;
import com.example.janetdo.toomapp.Helper.CloudantService;
import com.example.janetdo.toomapp.Helper.Item;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        Catalog catalog = null;
        try {
             catalog = new Catalog(getAssets().open("rawData.json"));
            catalog.initSortiment();
        } catch (Exception e) {
            System.out.println("Cannot read JSON file: " + e);
        }
        String resScanCode = rawResult.getText();
        Item item = catalog.getScannedItem(resScanCode);

        if(item != null){
            Toast.makeText(getApplicationContext(), "Name: " +item.getName() + " Kategorie: "+ item.getCategory()+" Preis: "+ item.getPrice(),
                    Toast.LENGTH_LONG).show();
        }


        mScannerView.resumeCameraPreview(this);
    }
}