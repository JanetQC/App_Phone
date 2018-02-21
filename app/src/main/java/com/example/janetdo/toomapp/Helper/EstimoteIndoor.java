package com.example.janetdo.toomapp.Helper;

import android.content.Context;

import com.estimote.cloud_plugin.common.EstimoteCloudCredentials;
import com.estimote.indoorsdk.IndoorLocationManagerBuilder;
import com.estimote.indoorsdk_module.algorithm.OnPositionUpdateListener;
import com.estimote.indoorsdk_module.algorithm.ScanningIndoorLocationManager;
import com.estimote.indoorsdk_module.cloud.CloudCallback;
import com.estimote.indoorsdk_module.cloud.EstimoteCloudException;
import com.estimote.indoorsdk_module.cloud.IndoorCloudManager;
import com.estimote.indoorsdk_module.cloud.IndoorCloudManagerFactory;
import com.estimote.indoorsdk_module.cloud.Location;
import com.estimote.indoorsdk_module.cloud.LocationBeacon;
import com.estimote.indoorsdk_module.cloud.LocationBeaconColor;
import com.estimote.indoorsdk_module.cloud.LocationBeaconData;
import com.estimote.indoorsdk_module.cloud.LocationPosition;
import com.estimote.indoorsdk_module.cloud.LocationWall;
import com.estimote.indoorsdk_module.view.IndoorLocationView;
import com.estimote.internal_plugins_api.cloud.CloudCredentials;
import com.example.janetdo.toomapp.Credentials.Credentials;
import com.example.janetdo.toomapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by janetdo on 27.12.17.
 */

public class EstimoteIndoor {
    private static Location currentLocation = new Location();
    private ScanningIndoorLocationManager indoorLocationManager;

    public void accessBeacons(Context context) {

        CloudCredentials cloudCredentials = new EstimoteCloudCredentials(Credentials.ESTIMOTE_APP_ID, Credentials.ESTIMOTE_TOKEN);
        IndoorCloudManager cloudManager = new IndoorCloudManagerFactory().create(context, cloudCredentials);
        cloudManager.getLocation(Credentials.ESTIMOTE_LOCATION, new CloudCallback<Location>()

        {
            @Override
            public void success(Location location) {


                indoorLocationManager =
                        new IndoorLocationManagerBuilder(context, location, cloudCredentials)
                                .withDefaultScanner()
                                .build();

                indoorLocationManager.setOnPositionUpdateListener(new OnPositionUpdateListener() {
                    @Override
                    public void onPositionUpdate(LocationPosition position) {
                        // here, we update the IndoorLocationView with the current position,
                        // but you can use the position for anything you want
                        // indoorView.updatePosition(position);
                        System.out.println("X: " + position.getX() + "Y: " + position.getY());
                    }

                    @Override
                    public void onPositionOutsideLocation() {
                        // indoorView.hidePosition();
                        System.out.println("OUTSIDE");
                    }


                });

                indoorLocationManager.startPositioning();
                // do something with your Location object here.
                // You will need it to initialise IndoorLocationManager!
                //  indoorLocationView = (IndoorLocationView) findViewById(R.id.indoor_view);
                //  indoorLocationView.setLocation(location);

            }

            @Override
            public void failure(EstimoteCloudException e) {
                System.out.println("Having problems with estimote cloud: " + e);
            }
        });


    }

    public ScanningIndoorLocationManager getIndoorLocationManager() {
        return indoorLocationManager;
    }
}
