package local.hal.st31.android.shift.beans;

import java.io.Serializable;

public class SelfScheduleBean implements Serializable {
    private long _id;
    private int userId;
    private String work;
    private String memo;
    private String startTime;
    private String endTime;
    private String date;

    public SelfScheduleBean(){

    }

    @Override
    public String toString() {
        return "SelfScheduleBean{" +
                "_id=" + _id +
                ", userId=" + userId +
                ", work='" + work + '\'' +
                ", memo='" + memo + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
