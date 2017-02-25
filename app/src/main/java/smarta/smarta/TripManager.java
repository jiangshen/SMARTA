package smarta.smarta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangshen on 2/25/17.
 */

public class TripManager {
    private static List<Trip> tripListHistory;

    private static TripManager mInstance = null;

    private TripManager() {
        tripListHistory = new ArrayList<>();
    }

    public static TripManager getInstance() {
        if(mInstance == null)
        {
            mInstance = new TripManager();
        }
        return mInstance;
    }

    public static void addTrip(Trip aTrip) {
        tripListHistory.add(aTrip);
    }

    public static List<Trip> getTripListHistory() {
        return tripListHistory;
    }
}
