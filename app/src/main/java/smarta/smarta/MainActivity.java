package smarta.smarta;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private final int animDuration = 500;
    private final int redColor = Color.parseColor("#D32F2F");
    private final int blueColor = Color.parseColor("#1484ca");
    private final int goldColor = Color.parseColor("#efc12c");

    private CardView cardGold;
    private CardView cardBlue;
    private CardView cardRed;
    private LinearLayout llBlueBar;
    private LinearLayout llRedBar;
    private LinearLayout llGoldBar;

    private LinearLayout llmain;

    private ArrayList<Trip> trips;

    private Spinner spinner;

    private Spinner numStops;

    // Red = 0, Gold = 1, Blue = 2
    private int lineNum = 1;

    //keep track of number of stops left until destination
    private int numStopsLeft;

    private int numStopsNotify;

    private BeaconManager beaconManager;
    private Region region;
    private ArrayList<String> redLineStops = new ArrayList<>();
    private ArrayList<String> goldLineStops = new ArrayList<>();
    private ArrayList<String> blueLineStops = new ArrayList<>();
    private List<String> currentLine;
    private String currentStation;


    private String destinationStation;

    private ArrayAdapter<String> arrAdapter;
    private ArrayAdapter<String> arrayAdapter;

    TextView stationTextView;
    TextView currentStationTextView;
    TextView numStopsTextView;

    TextView currTrainTV;

    private Map<String, String> beaconIdToTrainIdHashMap = new HashMap<>();
    private Map<String, String> beaconIdToStationIdHashMap = new HashMap<>();

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        llmain = (LinearLayout) findViewById(R.id.activity_main);
        llBlueBar = (LinearLayout) findViewById(R.id.ll_blue_bar);
        llRedBar = (LinearLayout) findViewById(R.id.ll_red_bar);
        llGoldBar = (LinearLayout) findViewById(R.id.ll_gold_bar);

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconIdToTrainIdHashMap.put("19272:35107","Train B");
        beaconIdToTrainIdHashMap.put("19272:35026", "Train A"); //stop 4

        beaconIdToStationIdHashMap.put("19272:3", "Airport"); //stop 1
        beaconIdToStationIdHashMap.put("19272:21858", "College Park"); //stop 2
        beaconIdToStationIdHashMap.put("19272:61798", "East Point"); //stop 3

        //Adding stop strings to appropriate lists
        populateStops();

        stationTextView = (TextView) findViewById(R.id.stationTextView);
        currentStationTextView = (TextView) findViewById(R.id.curr_stn);
        numStopsTextView = (TextView) findViewById(R.id.numStopsTextViewXML);

        beaconManager = new BeaconManager(this);
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);

                    String nearestBeaconKey = String.format("%d:%d", nearestBeacon.getMajor(),
                            nearestBeacon.getMinor());

                    if(list.size() > 1) {
                        Beacon currentStationBeacon = list.get(1);
                        String currentStationBeaconKey = Integer.toString(currentStationBeacon.getMajor()) + ":" + Integer.toString(currentStationBeacon.getMinor());
                        if (beaconIdToStationIdHashMap.containsKey(currentStationBeaconKey)) {

                            String newStation = beaconIdToStationIdHashMap.get(currentStationBeaconKey);

                            if (currentStation != null) {
                                if (!newStation.equals(currentStation)){

                                    if (beaconIdToTrainIdHashMap.containsKey(nearestBeaconKey)) {
                                        currTrainTV.setText(beaconIdToTrainIdHashMap.get
                                                (nearestBeaconKey));
                                    }

                                    numStopsLeft = Math.abs(currentLine.indexOf(destinationStation) - currentLine.indexOf(newStation)) - numStopsNotify;
                                    if (numStopsLeft == 0){
                                        Log.d("NOTIFY", "HEY REACHED!!!!!!!!");
                                        displayNotificationAndVibrate("You have reached your destination!");
                                        speak("You have reached " + destinationStation);
                                    }
                                    numStopsTextView.setText(String.valueOf(numStopsLeft));
                                }
                            }

                            currentStation = newStation;

                            currentStationTextView.setText(currentStation);
                        }
                    }
                }
            }
        });

        /* TTS */
        tts = new TextToSpeech(getApplicationContext(), this);

        cardGold = (CardView) findViewById(R.id.card_gold);
        cardBlue = (CardView) findViewById(R.id.card_blue);
        cardRed = (CardView) findViewById(R.id.card_red);

        //SET ADAPTER ON BUTTON PRESS
        Trip t = new Trip(12345,"Airport","East Point");
        t.setTripStartTime();
        t.setTripEndTime();
        trips = new ArrayList<>();
        trips.add(t);


        spinner = (Spinner) findViewById(R.id.spinning);
        arrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                goldLineStops);
        arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrAdapter);
        currentLine = goldLineStops;

        //Detect change in destination station
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                destinationStation = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        currTrainTV = (TextView) findViewById(R.id.curr_train_tv);

        ArrayList<String> stopCount = new ArrayList<>();
        for(int i = 0; i < 5;i++){
            stopCount.add(Integer.toString(i));
        }
        numStops = (Spinner) findViewById(R.id.num_stops);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,stopCount);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numStops.setAdapter(arrayAdapter);

        numStops.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numStopsNotify = Integer.parseInt(numStops.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        numStopsLeft = 10;
        numStopsNotify = 0;
        currentStation = "Airport";
        destinationStation = currentLine.get(0);

    }

    public void card_gold_clicked(View view) {

        int colorSource = (lineNum == 0) ? redColor : (lineNum == 2) ? blueColor : 0;
        if (lineNum != 1) {
//            cardGold.setBackgroundColor(Color.parseColor("#455A64"));
//            cardBlue.setBackgroundColor(Color.parseColor("#ffffff"));
//            cardRed.setBackgroundColor(Color.parseColor("#ffffff"));
//            llmain.setBackgroundColor(Color.parseColor("#FFC107"));

            llGoldBar.setVisibility(View.VISIBLE);
            if (lineNum == 0){
                llRedBar.setVisibility(View.INVISIBLE);
            } else {
                llBlueBar.setVisibility(View.INVISIBLE);
            }

            arrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                    goldLineStops);
            arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrAdapter);
            lineNum = 1;
            currentLine = goldLineStops;

            int colorFrom = colorSource;
            int colorTo = goldColor;
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(animDuration); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    llmain.setBackgroundColor((int) animator.getAnimatedValue());
                }

            });
            colorAnimation.start();
        }
    }

    public void card_blue_clicked(View view) {
        int colorSource = (lineNum == 0) ? redColor : (lineNum == 1) ? goldColor : 0;
        if (lineNum != 2) {
//            cardBlue.setBackgroundColor(Color.parseColor("#455A64"));
//            cardGold.setBackgroundColor(Color.parseColor("#ffffff"));
//            cardRed.setBackgroundColor(Color.parseColor("#ffffff"));

            llBlueBar.setVisibility(View.VISIBLE);
            if (lineNum == 0){
                llRedBar.setVisibility(View.INVISIBLE);
            } else {
                llGoldBar.setVisibility(View.INVISIBLE);
            }

            /**
             *
             */
            arrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                    blueLineStops);
            arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrAdapter);
            lineNum = 2;
            currentLine = blueLineStops;

            /**
             * Background color and transitions
             */
            int colorFrom = colorSource;
            int colorTo = blueColor;
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(animDuration); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    llmain.setBackgroundColor((int) animator.getAnimatedValue());
                }

            });
            colorAnimation.start();
        }
    }

    public void card_red_clicked(View view) {
        int colorSource = (lineNum == 2) ? blueColor : (lineNum == 1) ? goldColor : 0;
        if (lineNum != 0) {
//            cardRed.setBackgroundColor(Color.parseColor("#455A64"));
//            cardGold.setBackgroundColor(Color.parseColor("#ffffff"));
//            cardBlue.setBackgroundColor(Color.parseColor("#ffffff"));

            llRedBar.setVisibility(View.VISIBLE);
            if (lineNum == 1){
                llGoldBar.setVisibility(View.INVISIBLE);
            } else {
                llBlueBar.setVisibility(View.INVISIBLE);
            }

            arrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                    redLineStops);
            arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrAdapter);
//            llmain.setBackgroundColor(Color.parseColor("#D32F2F"));
            lineNum = 0;
            currentLine = redLineStops;

            int colorFrom = colorSource;
            int colorTo = redColor;
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(animDuration); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    llmain.setBackgroundColor((int) animator.getAnimatedValue());
                }

            });
            colorAnimation.start();
        }
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

    private void displayNotificationAndVibrate(String s) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_travel)
                        .setContentTitle("SMARTA")
                        .setContentText(s);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int mId = 0;
        mNotificationManager.notify(mId, mBuilder.build());

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1000);
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

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.ENGLISH);
        }
    }

    public void speak(String s) {
        tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}
