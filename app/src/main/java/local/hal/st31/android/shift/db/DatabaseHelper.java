package local.hal.st31.android.shift.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shift.db";
    private static final int DATABASE_VERSION = 1;
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
//    static final String SELF_SCHEDULE_SQL = "CREATE TABLE selfSchedule (_id INTEGER PRIMARY KEY AUTOINCREMENT,work TEXT,memo TEXT,start_time TEXT,end_time TEXT,date DATE)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE selfSchedule (");
        sb.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb.append("user_id INTEGER,");
        sb.append("work TEXT,");
        sb.append("memo TEXT,");
        sb.append("start_time TEXT,");
        sb.append("end_time TEXT,");
        sb.append("date DATE");
        sb.append(")");
        String sql = sb.toString();

        db.execSQL(sql);

        StringBuilder sb2 = new StringBuilder();
        sb2.append("CREATE TABLE shiftRequest (");
        sb2.append("id INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb2.append("user_id INTEGER,");
        sb2.append("shift_type_id INTEGER,");
        sb2.append("shift_id INTEGER,");
        sb2.append("date TEXT,");
        sb2.append("selected_flag INTEGER,");
        sb2.append("kaburu_flag INTEGER,");
        sb2.append("color_code INTEGER,");
        sb2.append("self_schedule_flag INTEGER");
        sb2.append(")");
        String sql2 = sb2.toString();

        db.execSQL(sql2);

        StringBuilder sb3 = new StringBuilder();
        sb3.append("CREATE TABLE shiftType (");
        sb3.append("shift_type_id INTEGER PRIMARY KEY,");
        sb3.append("shift_id INTEGER,");
        sb3.append("begin_time TEXT,");
        sb3.append("end_time TEXT,");
        sb3.append("type_name TEXT,");
        sb3.append("comment TEXT");
        sb3.append(")");
        String sql3 = sb3.toString();

        db.execSQL(sql3);

        StringBuilder sb4 = new StringBuilder();
        sb4.append("CREATE TABLE blackList (");
        sb4.append("black_user_id INTEGER PRIMARY KEY,");
        sb4.append("user_id INTEGER,");
        sb4.append("group_id INTEGER,");
        sb4.append("black_rank INTEGER,");
        sb4.append("color_code INTEGER,");
        sb4.append("nickname TEXT");
        sb4.append(")");
        String sql4 = sb4.toString();

        db.execSQL(sql4);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
