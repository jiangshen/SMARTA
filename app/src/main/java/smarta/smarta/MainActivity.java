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

    TextView stationTextView;

    private static Map<String, String> STOPS_BY_BEACONS = null;

    static {
        Map<String, String> beaconIdToCurrentStopHashMap = new HashMap<>();
        beaconIdToCurrentStopHashMap.put("19272:3", "Stop 1");
        beaconIdToCurrentStopHashMap.put("19272:21858", "Stop 2");

        STOPS_BY_BEACONS = Collections.unmodifiableMap(beaconIdToCurrentStopHashMap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

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
}
