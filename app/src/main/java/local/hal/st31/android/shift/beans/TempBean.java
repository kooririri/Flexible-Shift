package local.hal.st31.android.shift.beans;

import androidx.recyclerview.widget.RecyclerView;

public class TempBean {
    private int day;
    private RecyclerView recyclerView;

    public TempBean(){

    }

    public TempBean(int day,RecyclerView recyclerView){
        this.day = day;
        this.recyclerView = recyclerView;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
}
