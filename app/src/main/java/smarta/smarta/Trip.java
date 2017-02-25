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

    public void setTripStartTime() {
        startTime = Calendar.getInstance().getTime();
    }

    public void setTripEndTime() {
        endTime = Calendar.getInstance().getTime();
    }

    public void setTrainID(int id) {
        trainID = id;
    }

    public void setFromSrc(String fname) {
        fromSrc = fname;
    }

    public void setToDest(String tname) {
        toDest = tname;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Long getDuration() {
        return endTime.getTime() - startTime.getTime();
    }

    public int getTrainID() {
        return trainID;
    }

    public String getFromSrc() {
        return fromSrc;
    }

    public String getToDest() {
        return toDest;
    }
}
