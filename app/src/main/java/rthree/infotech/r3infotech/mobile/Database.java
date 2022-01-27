package rthree.infotech.r3infotech.mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    final static String DBName = "jaikisan";
    final static int version = 1;
    public Database(Context context) {
        super(context, DBName, null, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String create_q = "CREATE TABLE demo (sid INTEGER PRIMARY KEY,I_name text,prize text)";
        db.execSQL(create_q);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS demo");
        onCreate(db);
    }

    public boolean addRecord(String fname, String lname) {
        try {

            SQLiteDatabase mydb = this.getWritableDatabase();

            ContentValues data = new ContentValues();
            data.put("i_name", fname);
            data.put("prize", lname);
            mydb.insert("demo", null, data);
            mydb.close();
            return true;
        } catch (Exception e) {
            return false;
        }

    }
    public long getDays(String date) {
        SQLiteDatabase mydb = null;

        long v = 0;

        try {
            mydb = this.getReadableDatabase();
            // Vector ov = viewAllOrderById(id);
            String q = " SELECT julianday('now') - julianday('" + date + "');";

            Cursor c1 = mydb.rawQuery(q, null);

            if (c1.moveToNext()) {

                v = (c1.getLong(0));

            }

            return v;
        } catch (Exception e) {

            return 0;
        } finally {
            mydb.close();
        }
    }
}
