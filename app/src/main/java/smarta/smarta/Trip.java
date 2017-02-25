package smarta.smarta;


import android.icu.util.Calendar;

import java.util.Date;

/**
 * Created by jiangshen on 2/25/17.
 */

public class Trip {
    private Date startTime;
    private Date endTime;
    private int trainID;

    private void setTripStartTime() {
        startTime = Calendar.getInstance().getTime();
    }

    private void setTripEndTime() {
        endTime = Calendar.getInstance().getTime();
    }

    private void setTrainID(int id) {
        trainID = id;
    }

    private Date getStartTime() {
        return startTime;
    }

    private Date getEndTime() {
        return endTime;
    }

    private Long getDuration() {
        return endTime.getTime() - startTime.getTime();
    }

    private int getTrainID() {
        return trainID;
    }
}
