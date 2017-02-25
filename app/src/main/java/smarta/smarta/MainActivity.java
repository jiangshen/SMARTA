package smarta.smarta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.Utils;

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
    final String[] accuracy = {""};

    private static Map<String, List<String>> PLACES_BY_BEACONS = null;

    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();
        placesByBeacons.put("19272:3", new ArrayList<String>() {{ // RN3
            add("Stop 1");
        }});
        placesByBeacons.put("19272:21858", new ArrayList<String>() {{ // RN1
            add("Stop 2");
        }});
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
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

        String currentStop = "Color not found in database.";

        switch (beaconKey) {
            case "19272:3": return "Stop 1";
            case "19272:21858": return "Stop 2";
        }
        return currentStop;
    }
}
