package local.hal.st31.android.shift.beans;

import androidx.annotation.NonNull;

public class ShiftTypeBean {
    private int shiftId;//シフト自体ID
    private int shiftTypeId;//シフトの番タイプID　
    private String beginTime;//09:30　開始時間
    private String endTime;//終了時間
    private String typeName;//シフト番名 例:A勤
    private String comment;//説明とかメモ
    private int selectedFlag = 0;//選択されたら1、外したら0、デフォルトは0
    private int shiftRequestId;
    private String date;
    private int colorCode;
    private int kaburuFlag;//blackListの人と被ってるかどうかを判定するフラグ
    private int selfScheduleFlag;//自分の私用と被ってるかどうかを判定するフラグ


    public ShiftTypeBean(){
    }

    @Override
    public String toString() {
        return "ShiftTypeBean{" +
                "shiftId=" + shiftId +
                ", shiftTypeId=" + shiftTypeId +
                ", beginTime='" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", typeName='" + typeName + '\'' +
                ", comment='" + comment + '\'' +
                ", selectedFlag=" + selectedFlag +
                ", kaburuFlag=" + kaburuFlag +
                ", shiftRequestId=" + shiftRequestId +
                ", selfScheduleFlag=" + selfScheduleFlag +
                ", date='" + date + '\'' +
                '}';
    }


    @NonNull


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

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getSelectedFlag() {
        return selectedFlag;
    }

    public void setSelectedFlag(int selectedFlag) {
        this.selectedFlag = selectedFlag;
    }

    public int getShiftRequestId() {
        return shiftRequestId;
    }

    public void setShiftRequestId(int shiftRequestId) {
        this.shiftRequestId = shiftRequestId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getColorCode() {
        return colorCode;
    }

    public void setColorCode(int colorCode) {
        this.colorCode = colorCode;
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
