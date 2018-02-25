package com.example.janetdo.toomapp;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.janetdo.toomapp.Helper.Catalog;
import com.example.janetdo.toomapp.Helper.Item;
import com.google.zxing.Result;

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
             catalog = new Catalog(getAssets().open("product_data.json"));
            catalog.initSortiment();
        } catch (Exception e) {
            System.out.println("Cannot read JSON file: " + e);
        }
        String resScanCode = rawResult.getText();
        Item item = catalog.getScannedItem(resScanCode);

        if(item != null){
            showPopupWindow(item);
        }

        mScannerView.resumeCameraPreview(this);
    }

    private void showPopupWindow(Item item) {

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, 1000, 400, focusable);

        popupWindow.showAtLocation(mScannerView, Gravity.CENTER, 0, 0);

        ImageView itemPic;
        TextView price = popupView.findViewById(R.id.price);
        TextView name = popupView.findViewById(R.id.textName);
        TextView desc = popupView.findViewById(R.id.textDesc);

        price.setTextColor(getResources().getColor(R.color.black));
        price.setText(Double.toString(item.getPrice()) + " €");
        itemPic = popupView.findViewById(R.id.itemPic);
        itemPic.setBackground(setItemPic(item));

        desc.setText(item.getDescription());
        name.setText(item.getName());
        price.setTextSize(40);
        name.setTextSize(35);
        desc.setTextSize(25);


        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private Drawable setItemPic(Item item) {
        ImageView view = new ImageView(this);
        Drawable drawable = null;
        switch (item.getCategory().toLowerCase()) {
            case "bodenbelag":
                drawable = getDrawable(R.drawable.bodenbelag);
                break;
            case "pflanzen":
                drawable = getDrawable(R.drawable.pflanzen);
                break;
            case "lacke":
                drawable = getDrawable(R.drawable.category_paint);
                break;
            case "garten":
                drawable = getDrawable(R.drawable.garten);
                break;
            case "zement":
                drawable = getDrawable(R.drawable.zement);
                break;
            case "bauzubehör":
                drawable = getDrawable(R.drawable.bauzubehoer);
                break;
            case "styroporleisten":
                drawable = getDrawable(R.drawable.styroporleisten);
                break;
            case "baustoffe":
                drawable = getDrawable(R.drawable.baustoffe);
                break;
            case "dämmstoffe":
                drawable = getDrawable(R.drawable.daemmstoffe);
                break;
            default:
                drawable = getDrawable(R.drawable.sonstiges);


        }
        return drawable;

    }
}