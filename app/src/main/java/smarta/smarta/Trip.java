package smarta.smarta;


import java.util.Calendar;

import java.util.Date;

/**
 * Created by jiangshen on 2/25/17.
 */

public class Trip {
    private Date startTime;
    private Date endTime;
    private int trainID;
    private String fromSrc;
    private String toDest;

    private void setTripStartTime() {
        startTime = Calendar.getInstance().getTime();
    }

    private void setTripEndTime() {
        endTime = Calendar.getInstance().getTime();
    }

    private void setTrainID(int id) {
        trainID = id;
    }

    private void setFromSrc(String fname) {
        fromSrc = fname;
    }

    private void setToDest(String tname) {
        toDest = tname;
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

    public String getFromSrc() {
        return fromSrc;
    }

    public String getToDest() {
        return toDest;
    }
}
