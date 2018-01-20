package com.example.janetdo.toomapp;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

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
import com.example.janetdo.toomapp.Helper.EstimoteIndoor;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Created by janetdo on 05.12.17.
 */

public class MapActivity extends AppCompatActivity implements BeaconConsumer {
    private IndoorLocationView indoorLocationView;
    private ScanningIndoorLocationManager indoorLocationManager;
    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        indoorLocationView = (IndoorLocationView) findViewById(R.id.indoor_view);
      //  initBeacons(this);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(" m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i(TAG, "Amount of beacons detected"+beacons.size());
                    Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
                    Log.i(TAG, "The beacon getDataFields "+beacons.iterator().next().getDataFields());
                    Log.i(TAG, "The beacon getExtraDataFields "+beacons.iterator().next().getExtraDataFields());
                    Log.i(TAG, "The beacon id1 "+beacons.iterator().next().getId1());
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }

    public void initBeacons(Context context) {

        Location virtualLocation = initLocation();
        indoorLocationView.setLocation(virtualLocation);
        CloudCredentials cloudCredentials = new EstimoteCloudCredentials("janet-do-ibm-com-s-your-ow-4hk", "61de76f8398536550a88adcb8255c5ce");
        IndoorCloudManager cloudManager = new IndoorCloudManagerFactory().create(context, cloudCredentials);
        cloudManager.getLocation("janet-do-s-location-iiz", new CloudCallback<Location>()

        {
            @Override
            public void success(Location location) {

                indoorLocationManager =
                        new IndoorLocationManagerBuilder(context, virtualLocation, cloudCredentials)
                                .withDefaultScanner()
                                .build();

                indoorLocationManager.setOnPositionUpdateListener(new OnPositionUpdateListener() {
                    @Override
                    public void onPositionUpdate(LocationPosition position) {
                        // here, we update the IndoorLocationView with the current position,
                        // but you can use the position for anything you want
                        indoorLocationView.updatePosition(position);
                        System.out.println("X: " + position.getX() + "Y: " + position.getY());
                    }

                    @Override
                    public void onPositionOutsideLocation() {
                        // indoorView.hidePosition();
                        System.out.println("OUTSIDE");
                    }


                });
                indoorLocationManager.startPositioning();

            }

            @Override
            public void failure(EstimoteCloudException e) {
                System.out.println("Having problems with estimote cloud: " + e);
            }
        });

    }


    private Location initLocation(){
        List<LocationBeacon> beacons = new ArrayList<>();
        List<LocationWall> walls = new ArrayList<>();
        LocationPosition positionYellow = new LocationPosition(0.0, 2.5, 80.0);
        LocationBeaconData dataYellow = new LocationBeaconData("yellow", LocationBeaconColor.LEMON_TART);
        LocationBeacon beaconYellowLeft = new LocationBeacon(dataYellow, positionYellow);

        LocationPosition positionPink = new LocationPosition(10.0, 2.5, 40.0);
        LocationBeaconData dataPink = new LocationBeaconData("floss", LocationBeaconColor.CANDY_FLOSS);
        LocationBeacon beaconPinkRight = new LocationBeacon(dataPink, positionPink);


        LocationPosition positionBlueberry = new LocationPosition(5.0, 5.0, 35.0);
        LocationBeaconData dataBlueberry = new LocationBeaconData("blueberry", LocationBeaconColor.BLUEBERRY_PIE);
        LocationBeacon beaconBlueberryTop = new LocationBeacon(dataBlueberry, positionBlueberry);

        LocationPosition positionBeetroot = new LocationPosition(5.0, 0.0, 0.0);
        LocationBeaconData dataBeetroot = new LocationBeaconData("beetroot", LocationBeaconColor.SWEET_BEETROOT);
        LocationBeacon beaconBeetrootBottom = new LocationBeacon(dataBeetroot, positionBeetroot);

        LocationWall wallLeft = new LocationWall(0.0, 0.0, 0.0, 5.0, 0);
        LocationWall wallBottom = new LocationWall(0.0, 0.0, 10.0, 0.0, 0);
        LocationWall wallTop = new LocationWall(0.0, 5.0, 10.0, 5.0, 0);
        LocationWall wallRight = new LocationWall(10.0, 0.0, 10.0, 5.0, 0);

        walls.add(wallLeft);
        walls.add(wallBottom);
        walls.add(wallTop);
        walls.add(wallRight);

        beacons.add(beaconYellowLeft);
        beacons.add(beaconPinkRight);
        beacons.add(beaconBlueberryTop);
        beacons.add(beaconBeetrootBottom);

        Location loc = new Location("name", "identifier", true, "", 0.0, walls, beacons, emptyList());
        return loc;
    }
}
