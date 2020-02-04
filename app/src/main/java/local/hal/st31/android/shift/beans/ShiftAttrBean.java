package local.hal.st31.android.shift.beans;

public class ShiftAttrBean {
    private int shiftId;
    private String shiftName;//シフト名
    private String submitDate;//2019-11-25　提出期限
    private String modifyDate;//修正期限
    private String finalDate;//最終発表時間

    public ShiftAttrBean(){

    }

    public ShiftAttrBean(int shiftId, String shiftName, String submitDate, String modifyDate, String finalDate){
        this.shiftId = shiftId;
        this.shiftName = shiftName;
        this.submitDate = submitDate;
        this.modifyDate = modifyDate;
        this.finalDate = finalDate;
    }

    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(String finalDate) {
        this.finalDate = finalDate;
    }
}
