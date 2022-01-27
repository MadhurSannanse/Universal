package rthree.infotech.r3infotech.mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Vector;

public class ServerDatabase extends SQLiteOpenHelper {
    final static String DBName = "mobile";
    final static int version = 1;
    Model model;
    //Model model;

    public ServerDatabase(Context context) {
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
    public void checkDatabase()
    {
        try
        {
            SQLiteDatabase sampledb = this.getWritableDatabase();
            createTables(sampledb);

        }
        catch(Exception ex)
        {
            Log.e("Exception is",""+ex.getMessage());
        }}
    public boolean checkTable() {
        SQLiteDatabase mydb = null;
        try {
            mydb = this.getReadableDatabase();
            String q = "select  * from tbl_loginmanagement";
            Cursor c = mydb.rawQuery(q, null);
            while (c.moveToNext()) {
                Log.e("Result =>","True");
                return true;
            }
            Log.e("Result =>","False");
            return false;
        } catch (Exception e) {
            Log.e("Result =>","False Ex"+e.getMessage());
            return false;
        } finally {
            mydb.close();

        }
    }
    public void createTables(SQLiteDatabase sampleDB)
    {
        try
        {
            int id=1;
            String value="";
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_loginmanagement(ID integer NOT NULL PRIMARY KEY AUTOINCREMENT,user_name TEXT,contact_no TEXT,user_type TEXT,imei_no TEXT,date TEXT,status TEXT,tally_name TEXT,url_address TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_order_det(Id INTEGER PRIMARY KEY AUTOINCREMENT,Order_id NUMERIC,Product_id INTEGER,Rate decimal(20,3),Amount decimal(20,3),Scheme TEXT,Quantity NUMERIC,Salesman TEXT,Description TEXT,Status TEXT,Extra_scheme decimal(20,3),Tally_ID TEXT,Ins_Date TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_outstanding(Billno Text,Billdate date,Party text,Amount decimal(20,3),Salesman text,Opening decimal(20,3),Onaccount decimal(20,3))");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_receipt(ID integer NOT NULL PRIMARY KEY AUTOINCREMENT,Rec_No TEXT,Rec_Date TEXT,Party TEXT,Salesman TEXT, Amount decimal(20,3),Paymode TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_customer(Id INTEGER PRIMARY KEY,Salesman text,Name text,Balence decimal(20,3),Serial TEXT )");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_login(Id text,Name text,Password text,Status text,Serial TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_order(Id integer NOT NULL PRIMARY KEY AUTOINCREMENT,Customer_name text,Cdate TEXT,Salesman text,Total_amount decimal(20,3),Status text,Ins_Date text,Tally_ID TEXT,Location Text)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_product(Id text,Rate decimal(20,3),Name TEXT,Catagiry TEXT,Sequence INTEGER)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_fcm_details(Notification_token Text,Name Text)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_company(ID bigint,company_name TEXT,contact_no TEXT,address TEXT,gstin_no TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_company_name(ID integer NOT NULL PRIMARY KEY,Name Text,Server Text)");

            // sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_rebate(Id INTEGER PRIMARY KEY,Party TEXT,Brand_Name TEXT,Cur_Date TEXT,Cases NUMERIC,Bottle NUMERIC,Rebate decimal(20,3),Company TEXT)");
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS tbl_update_date(Id INTEGER PRIMARY KEY,Update_Date TEXT)");
            Log.e("All tables"," Are created ");
            try
            {
                sampleDB.execSQL("Insert into tbl_update_date(Id,Update_Date)values(1,'1-3-2017')");
            }catch(Exception ex){}
            try
            {
                sampleDB.execSQL("Insert into tbl_company_name(ID,Name)values(1,'http://www.r3infoservices.com/Offline/arihant/')");
            }catch(Exception ex){}
            sampleDB.close();
        }
        catch(Exception ex)
        {
            Log.e("Error In Table","Not Created "+ex.getLocalizedMessage());
        }
    }
    public void updateCompany(String name,String server)
    {
        try
        {
            SQLiteDatabase mydb = this.getWritableDatabase();
            ContentValues data = new ContentValues();
            //Log.i("Name Comp",""+name);
            Model model=Model.getInstance();
            if(!name.equals(""))
            {
                // model.setConnectioPath("http://www.r3infoservices.com/Offline/RE_2/");
                data.put("Name",""+name);
                data.put("Server",""+server);
                model.setCompanyName(""+name);
                model.setUrl_address("http://www.r3infoservices.com/Offline/"+server+"/");
                Log.i("Name: "+name,"Server : "+server);
            }
            else {
                Log.i("One", " NOt match");
            }

            mydb.update("tbl_company_name",data,"Id=1",null);

            mydb.close();
        }
        catch (Exception ex)
        {

        }
    }
    public Vector  getCompanyNameDetails() {
        Log.i("Root","Comming");
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector al;
        try {
            String cols[] = { "Name","Server"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_company_name",cols,"ID=?",
                    new String[] { "1" }, null,
                    null, null);
            al=new Vector();
            while (c.moveToNext()) {
                al.addElement(c.getString(0).trim());
                al.addElement(c.getString(1).trim());
                model.setCompany(c.getString(0).trim());
                model.setUrl_address("http://www.r3infoservices.com/Offline/"+c.getString(1).trim()+"/");
                //Log.i("Root ",""+model.getRoot());
            }
            if(model.getCompany().equals("")) {
                model.setCompany("Universal");
                model.setSalesman("Sundry Debtors");
                model.setUrl_address("http://www.r3infoservices.com/Offline/arihant/");
            }
            return al;
        } catch (Exception e) {

            return null;
        } finally {
            mydb.close();
        }
    }
    public boolean addLoginManagement(Vector v) {
        try {

            SQLiteDatabase mydb = this.getWritableDatabase();

            ContentValues data = new ContentValues();
            data.put("user_name", v.elementAt(0).toString());
            data.put("contact_no", v.elementAt(1).toString());
            data.put("user_type", v.elementAt(2).toString());
            data.put("imei_no", v.elementAt(3).toString());
            data.put("date", v.elementAt(4).toString().trim());
            data.put("status", v.elementAt(4).toString().trim());
            data.put("tally_name", v.elementAt(4).toString().trim());
            mydb.insert("tbl_login", null, data);
            mydb.close();
            return true;
        } catch (Exception e) {
            return false;
        }

    }
    public boolean addFCMDetails(Vector v) {
        try {

            SQLiteDatabase mydb = this.getWritableDatabase();

            ContentValues data = new ContentValues();
            data.put("Notification_Token", v.elementAt(0).toString());
            data.put("Name", v.elementAt(1).toString());
            mydb.insert("tbl_fcm_details", null, data);
            mydb.close();
            return true;
        } catch (Exception e) {
            return false;
        }

    }
    public boolean deleteLogin()
    { SQLiteDatabase mydb=getReadableDatabase();
        try
        {
            mydb.delete("tbl_loginmanagement",null,null);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
        finally {
            mydb.close();
        }
    }
    public boolean deleteOrder()
    { SQLiteDatabase mydb=getReadableDatabase();
        try
        {
            mydb.delete("tbl_order",null,null);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
        finally {
            mydb.close();
        }
    }
    public boolean deleteOrderDet()
    { SQLiteDatabase mydb=getReadableDatabase();
        try
        {
            mydb.delete("tbl_order_det",null,null);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
        finally {
            mydb.close();
        }
    }
    public boolean deleteProduct()
    { SQLiteDatabase mydb=getReadableDatabase();
        try
        {
            mydb.delete("tbl_product",null,null);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
        finally {
            mydb.close();
        }
    }
    public boolean addLoginDetails(Vector v) {
        try {

            SQLiteDatabase mydb = this.getWritableDatabase();

            ContentValues data = new ContentValues();
            //data.put("Id", v.elementAt(0).toString());
            data.put("user_name", v.elementAt(0).toString());
            data.put("contact_no", v.elementAt(1).toString());
            data.put("user_type", v.elementAt(2).toString());
            data.put("imei_no", v.elementAt(3).toString().trim());
            data.put("date", v.elementAt(4).toString().trim());
            data.put("status", v.elementAt(5).toString().trim());
            data.put("tally_name", v.elementAt(6).toString().trim());
            data.put("url_address", v.elementAt(7).toString().trim());
            mydb.insert("tbl_loginmanagement", null, data);
            Log.e("User","Added");
            mydb.close();
            return true;
        } catch (Exception e) {
            Log.e("User","Not Added");
            return false;
        }

    }
    public boolean deleteCompanyDetails()
    { SQLiteDatabase mydb=getReadableDatabase();
        try
        {
            mydb.delete("tbl_company",null,null);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
        finally {
            mydb.close();
        }
    }
    public boolean addCompanyDetails(Vector v) {
        try {

            SQLiteDatabase mydb = this.getWritableDatabase();
            ContentValues data = new ContentValues();
            //data.put("Id", v.elementAt(0).toString());
            data.put("ID", v.elementAt(0).toString());
            data.put("company_name", v.elementAt(1).toString());
            data.put("contact_no", v.elementAt(2).toString());
            data.put("address", v.elementAt(3).toString().trim());
            data.put("gstin_no", v.elementAt(4).toString().trim());
            mydb.insert("tbl_company", null, data);
            Log.e("Company","Added");
            mydb.close();
            return true;
        } catch (Exception e) {
            Log.e("User","Not Added");
            return false;
        }

    }
    public Vector  getUserLogin() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector al;
        try {

            String cols[] = { "user_name","contact_no","user_type","imei_no","date","status","tally_name","url_address"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_loginmanagement",cols,"Status=?",
                    new String[] { "YES" }, null,
                    null, null);
            al=new Vector();
            while (c.moveToNext()) {
                al.addElement(c.getString(0).trim());
                al.addElement(c.getString(1).trim());
                al.addElement(c.getString(2).trim());
                al.addElement(c.getString(3).trim());
                al.addElement(c.getString(4).trim());
                al.addElement(c.getString(5).trim());
                al.addElement(c.getString(6).trim());
                al.addElement(c.getString(7).trim());
            }

            return al;
        } catch (Exception e) {

            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getCompanyDetails() {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector al;
        try {

            String cols[] = { "company_name","contact_no","address","gstin_no"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_company",cols,null,
                    null, null,
                    null, null);
            al=new Vector();
            while (c.moveToNext()) {
                al.addElement(c.getString(0).trim());
                al.addElement(c.getString(1).trim());
                al.addElement(c.getString(2).trim());
                al.addElement(c.getString(3).trim());

            }

            return al;
        } catch (Exception e) {

            return null;
        } finally {
            mydb.close();
        }
    }
    public Vector  getFCMDetails(String name) {
        SQLiteDatabase mydb = null;
        model = Model.getInstance();
        Vector al;
        try {

            String cols[] = { "Notification_token"};
            mydb = this.getReadableDatabase();
            Cursor c = mydb.query("tbl_fcm_details",cols,"Name=?",
                    new String[] { name }, null,
                    null, null);
            al=new Vector();
            while (c.moveToNext()) {
                al.addElement(c.getString(0).trim());
            }

            return al;
        } catch (Exception e) {

            return null;
        } finally {
            mydb.close();
        }
    }
}
