package local.hal.st31.android.shift.db;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import local.hal.st31.android.shift.beans.BlackListBean;
import local.hal.st31.android.shift.beans.SelfScheduleBean;
import local.hal.st31.android.shift.beans.ShiftRequestBean;
import local.hal.st31.android.shift.beans.ShiftTypeBean;

public class DataAccess {
    /**
     * selfSchedule の　INSERT
     * @param db selfSchedule　自分の予定を管理するDB
     * @param selfScheduleBean bean
     * @return  成功かどうか
     */
    public static long selfScheduleInsert(SQLiteDatabase db,SelfScheduleBean selfScheduleBean){
        String sql = "INSERT INTO selfSchedule(user_id,work,memo,start_time,end_time,date)VALUES(?,?,?,?,?,?)";
        SQLiteStatement stmt =db.compileStatement(sql);
        stmt.bindLong(1,selfScheduleBean.getUserId());
        stmt.bindString(2,selfScheduleBean.getWork());
        stmt.bindString(3,selfScheduleBean.getMemo());
        stmt.bindString(4,selfScheduleBean.getStartTime());
        stmt.bindString(5,selfScheduleBean.getEndTime());
        stmt.bindString(6,selfScheduleBean.getDate());
        return stmt.executeInsert();
    }

    public static ArrayList<SelfScheduleBean> selfScheduleSelectByDate(SQLiteDatabase db,String date,int user_id){
        ArrayList<SelfScheduleBean> list = new ArrayList<>();
        String sql = "SELECT * FROM selfSchedule WHERE date = ? AND user_id = ? ORDER BY start_time ASC";
        Cursor cursor = db.rawQuery(sql,new String[]{date,String.valueOf(user_id)});
        if(cursor.moveToFirst()){
            do{
                long id = cursor.getLong(cursor.getColumnIndex("_id"));
                String work = cursor.getString(cursor.getColumnIndex("work"));
                String memo = cursor.getString(cursor.getColumnIndex("memo"));
                String startTime = cursor.getString(cursor.getColumnIndex("start_time"));
                String endTime = cursor.getString(cursor.getColumnIndex("end_time"));
                String workDate = cursor.getString(cursor.getColumnIndex("date"));

                SelfScheduleBean bean = new SelfScheduleBean();
                bean.setId(id);
                bean.setWork(work);
                bean.setMemo(memo);
                bean.setStartTime(startTime);
                bean.setEndTime(endTime);
                bean.setDate(date);
                list.add(bean);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public static int selfScheduleDelete(SQLiteDatabase db,long id){
        String sql = "DELETE FROM selfSchedule WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindLong(1,id);
        return stmt.executeUpdateDelete();
    }

    public static int selfScheduleUpdate(SQLiteDatabase db,SelfScheduleBean selfScheduleBean,long id){
        String sql = "UPDATE selfSchedule SET user_id = ?, work = ?, memo = ?,start_time = ?, end_time = ?, date = ?  WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindLong(1,selfScheduleBean.getUserId());
        stmt.bindString(2,selfScheduleBean.getWork());
        stmt.bindString(3,selfScheduleBean.getMemo());
        stmt.bindString(4,selfScheduleBean.getStartTime());
        stmt.bindString(5,selfScheduleBean.getEndTime());
        stmt.bindString(6,selfScheduleBean.getDate());
        stmt.bindLong(7,id);
        return stmt.executeUpdateDelete();
    }

    public static LinkedList<String> getAvailableDate(SQLiteDatabase db){
        LinkedList<String> dates = new LinkedList<>();
        String sql = "SELECT DISTINCT date FROM selfSchedule";
        Cursor cursor = db.rawQuery(sql,new String[]{});
        if(cursor.moveToFirst()){
            do{
                String date = cursor.getString(cursor.getColumnIndex("date"));
                if(!dates.contains(date)){
                    dates.add(date);
                }
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return dates;
    }

    public static int getNumberOfSelfScheduleByDate(SQLiteDatabase db,String date,int user_id){
        int res = 0;
        String sql = "SELECT COUNT(*) AS number FROM selfSchedule WHERE date = ? AND user_id = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{date,String.valueOf(user_id)});
        if(cursor.moveToFirst()){
            do{
                res = cursor.getInt(cursor.getColumnIndex("number"));
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static void  shiftTypeReplace(SQLiteDatabase db, ShiftTypeBean bean){
        ContentValues values = new ContentValues();
        values.put("shift_type_id",bean.getShiftTypeId());
        values.put("shift_id",bean.getShiftId());
        values.put("begin_time",bean.getBeginTime());
        values.put("end_time",bean.getEndTime());
        values.put("type_name",bean.getTypeName());
        values.put("comment",bean.getComment());
        db.replace("shiftType",null,values);
        values.clear();
    }
    public static ShiftTypeBean getShiftTypeByTypeId(SQLiteDatabase db,int typeId){
        ShiftTypeBean bean = new ShiftTypeBean();
        String sql = "SELECT * FROM shiftType WHERE shift_type_id = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{String.valueOf(typeId)});
        if(cursor.moveToFirst()){
            int groupId = cursor.getInt(cursor.getColumnIndex("shift_id"));
            String beginTime = cursor.getString(cursor.getColumnIndex("begin_time"));
            String endTime = cursor.getString(cursor.getColumnIndex("end_time"));
            String typeName = cursor.getString(cursor.getColumnIndex("type_name"));
            String comment = cursor.getString(cursor.getColumnIndex("comment"));
            bean.setShiftId(groupId);
            bean.setShiftTypeId(typeId);
            bean.setBeginTime(beginTime);
            bean.setEndTime(endTime);
            bean.setTypeName(typeName);
            bean.setComment(comment);
        }
        cursor.close();
        return bean;
    }

    public static void deleteAllShiftType(SQLiteDatabase db){
        db.delete("shiftType","shift_id > ?",new String[]{"0"});
    }

    public static int getShiftTypeNum(SQLiteDatabase db,int shiftId){
        int num = 0;
        String sql = "SELECT COUNT(shift_type_id) AS num FROM shiftType WHERE shift_id = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{String.valueOf(shiftId)});
        if(cursor.moveToFirst()){
            do{
                num = cursor.getInt(cursor.getColumnIndex("num"));
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return num;
    }



    public static void shiftRequestReplace(SQLiteDatabase db, ShiftRequestBean bean){
        ContentValues values = new ContentValues();
        values.put("id",bean.getId());
        values.put("user_id",bean.getUserId());
        values.put("shift_type_id",bean.getShiftTypeId());
        values.put("shift_id",bean.getShiftId());
        values.put("date",bean.getDate());
        values.put("selected_flag",bean.getSelectedFlag());
        values.put("kaburu_flag",bean.getKaburuFlag());
        values.put("self_schedule_flag",bean.getSelfScheduleFlag());
        db.replace("shiftRequest",null,values);
        values.clear();
    }


    public static List<Map<String,Object>> getShiftRequestByShiftId(SQLiteDatabase db, int shiftId){
        List<Map<String,Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM shiftRequest r LEFT JOIN shiftType t on r.shift_type_id = t.shift_type_id WHERE r.shift_id = ? ORDER BY r.id";
        Cursor cursor = db.rawQuery(sql,new String[]{String.valueOf(shiftId)});
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int userId = cursor.getInt(cursor.getColumnIndex("user_id"));
                int shiftID = cursor.getInt(cursor.getColumnIndex("shift_id"));
                int shiftTypeId = cursor.getInt(cursor.getColumnIndex("shift_type_id"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                int selectedFlag = cursor.getInt(cursor.getColumnIndex("selected_flag"));
                String beginTime = cursor.getString(cursor.getColumnIndex("begin_time"));
                String endTime = cursor.getString(cursor.getColumnIndex("end_time"));
                String typeName = cursor.getString(cursor.getColumnIndex("type_name"));
                String comment = cursor.getString(cursor.getColumnIndex("comment"));
                int kaburuFlag = cursor.getInt(cursor.getColumnIndex("kaburu_flag"));
                int selfScheduleFlag = cursor.getInt(cursor.getColumnIndex("self_schedule_flag"));
                Map<String,Object> map = new HashMap<>();
                map.put("id",id);
                map.put("userId",userId);
                map.put("shiftId",shiftID);
                map.put("shiftTypeId",shiftTypeId);
                map.put("date",date);
                map.put("selectedFlag",selectedFlag);
                map.put("beginTime",beginTime);
                map.put("endTime",endTime);
                map.put("typeName",typeName);
                map.put("comment",comment);
                map.put("kaburuFlag",kaburuFlag);
                map.put("selfScheduleFlag",selfScheduleFlag);
                list.add(map);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    public static void  blackListReplace(SQLiteDatabase db, BlackListBean bean){
        ContentValues values = new ContentValues();
        values.put("black_user_id",bean.getUserId());
        values.put("group_id",bean.getGroupId());
        values.put("black_rank",bean.getBlackRank());
        values.put("color_code",bean.getColorCode());
        db.replace("shiftType",null,values);
        values.clear();
    }
}
