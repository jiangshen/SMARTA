package smarta.smarta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private Region region;
    private ArrayList<String> redLineStops = new ArrayList<>();
    private ArrayList<String> goldLineStops = new ArrayList<>();
    private ArrayList<String> blueLineStops = new ArrayList<>();

    TextView stationTextView;

    private HashMap<String, String> beaconIdToBusIdHashMap = new HashMap<>();
    private static Map<String, String> STOPS_BY_BEACONS = null;

    static {
        Map<String, String> beaconIdToCurrentStopHashMap = new HashMap<>();
        beaconIdToCurrentStopHashMap.put("19272:3", "Stop 1"); //stop 1
        beaconIdToCurrentStopHashMap.put("19272:21858", "Stop 2"); //stop 2
        beaconIdToCurrentStopHashMap.put("19272:61798", "Stop 3"); //stop 3
        beaconIdToCurrentStopHashMap.put("19272:35026", "Stop 4"); //stop 4

        STOPS_BY_BEACONS = Collections.unmodifiableMap(beaconIdToCurrentStopHashMap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconIdToBusIdHashMap.put("19272:35107","52A32");
        //Adding stop strings to appropriate lists
        populateStops();

        stationTextView = (TextView) findViewById(R.id.stationTextView);

        beaconManager = new BeaconManager(this);
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);

                    String currentStop = currentStopFinder(nearestBeacon);

                    stationTextView.setText(currentStop);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
    }

    private String currentStopFinder(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());

        String currentStop = "Searching for current stop...";

        switch (beaconKey) {
            case "19272:3": return "Stop 1";
            case "19272:21858": return "Stop 2";
        }
        return currentStop;
    }

    /*
    * Red and Gold Line stops are ordered chronologically from South to North.
    * BLue Line stops are ordered West to East.
    * */
    private void populateStops(){
        redLineStops.add("Airport");
        redLineStops.add("College Park");
        redLineStops.add("East Point");
        redLineStops.add("LakeWood/Ft.McPherson");
        redLineStops.add("Oakland City");
        redLineStops.add("West End");
        redLineStops.add("Garnett");
        redLineStops.add("Five Points");
        redLineStops.add("Peachtree Center");
        redLineStops.add("Civic Center");
        redLineStops.add("North Avenue");
        redLineStops.add("Midtown");
        redLineStops.add("Arts Center");
        redLineStops.add("Lindbergh Center");
        redLineStops.add("Buckhead");
        redLineStops.add("Medical Center");
        redLineStops.add("Dunwoody");
        redLineStops.add("Sandy Springs");
        redLineStops.add("North Springs");

        goldLineStops.add("Airport");
        goldLineStops.add("College Park");
        goldLineStops.add("East Point");
        goldLineStops.add("LakeWood/Ft.McPherson");
        goldLineStops.add("Oakland City");
        goldLineStops.add("West End");
        goldLineStops.add("Garnett");
        goldLineStops.add("Five Points");
        goldLineStops.add("Peachtree Center");
        goldLineStops.add("Civic Center");
        goldLineStops.add("North Avenue");
        goldLineStops.add("Midtown");
        goldLineStops.add("Arts Center");
        goldLineStops.add("Lindbergh Center");
        goldLineStops.add("Lenox");
        goldLineStops.add("Brookhaven/Oglethorpe");
        goldLineStops.add("Chamblee");
        goldLineStops.add("Doraville");

        blueLineStops.add("H.E. Holmes");
        blueLineStops.add("West Lake");
        blueLineStops.add("Ashby");
        blueLineStops.add("Vine City");
        blueLineStops.add("DOME/GWCC");
        blueLineStops.add("Georgia State");
        blueLineStops.add("King Memorial");
        blueLineStops.add("Inman Park");
        blueLineStops.add("Edgewood");
        blueLineStops.add("East Lake");
        blueLineStops.add("Decatur");
        blueLineStops.add("Avondale");
        blueLineStops.add("Kensington");
        blueLineStops.add("Indian Creek");
    }
}
