package local.hal.st31.android.shift.beans;

import androidx.annotation.NonNull;

public class ShiftRequestBean {

    private int id;//シフト希望ID
    private int userId;
    private int shiftId;
    private int shiftTypeId;//シフトの番タイプID
    private String date;
    private int selectedFlag;
    private int kaburuFlag;//blackListの人と被ってるかどうかを判定するフラグ
    private int selfScheduleFlag;//自分の私用と被ってるかどうかを判定するフラグ


    public ShiftRequestBean(){
        selfScheduleFlag = 0;
    }

    @Override
    public String toString() {
        return "ShiftRequestBean{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", shiftId=" + shiftId +
                ", shiftTypeId=" + shiftTypeId +
                ", date='" + date + '\'' +
                ", selectedFlag=" + selectedFlag +
                ", kaburuFlag=" + kaburuFlag +
                ", selfScheduleFlag=" + selfScheduleFlag +
                '}';
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @NonNull


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }

    public int getShiftTypeId() {
        return shiftTypeId;
    }

    public void setShiftTypeId(int shiftTypeId) {
        this.shiftTypeId = shiftTypeId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSelectedFlag() {
        return selectedFlag;
    }

    public void setSelectedFlag(int selectedFlag) {
        this.selectedFlag = selectedFlag;
    }

    public int getKaburuFlag() {
        return kaburuFlag;
    }

    public void setKaburuFlag(int kaburuFlag) {
        this.kaburuFlag = kaburuFlag;
    }

    public int getSelfScheduleFlag() {
        return selfScheduleFlag;
    }

    public void setSelfScheduleFlag(int selfScheduleFlag) {
        this.selfScheduleFlag = selfScheduleFlag;
    }
}
