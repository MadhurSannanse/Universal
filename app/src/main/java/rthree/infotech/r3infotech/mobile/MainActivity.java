package rthree.infotech.r3infotech.mobile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.StrictMode;
/*import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;*/import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    Model model;
    ServerDatabase mydb;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final int INTERNET = 1;
    private  static final int ACCESS_COARSE_LOCATION=1;
    ImageButton img=null;
    Animation animation=null;
    TextView version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img=(ImageButton)findViewById(R.id.img_name);
        version=(TextView)findViewById(R.id.lbl_versionname);
        animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade);
        if(getAllPermissions())
        {
            try {
                checkDatabase();
                try {
                     version.setText(version.getText()+"\n"+getImeiNumber().toString().trim());
                }
                catch (Exception ex){Log.e("","");}
                //img.startAnimation(animation);

                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // Toast.makeText(getApplicationContext(), "Animation Starts", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        finish();
                        try {
                            mydb = new ServerDatabase(getApplicationContext());
                            Vector userlogin = mydb.getUserLogin();
                            Log.e("User Details" + userlogin.size(), "" + userlogin.toString());
                            if (userlogin.size() == 0) {
                                Intent i = new Intent(getApplicationContext(), LoginRequest.class);
                                startActivity(i);
                            } else {
                                String usertype = "" + userlogin.elementAt(2).toString().trim();
                                if (usertype.equals("Customer")) {
                                    //   Toast.makeText(getApplicationContext(), "Logined As Customer Welcome " + userlogin.elementAt(0), Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(), Home.class);
                                    i.putExtra("UserName", "" + userlogin.elementAt(0));
                                    i.putExtra("UserType", "" + usertype);
                                    startActivity(i);
                                } else {
                                    if (userlogin.elementAt(6).equals("NO")) {
                                        Toast.makeText(getApplicationContext(), "Login Confirmation Is Pending ", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //  Toast.makeText(getApplicationContext(), "Logined Sucess Welcome " + userlogin.elementAt(0), Toast.LENGTH_SHORT).show();
                                        model=Model.getInstance();
                                        model.setUrl_address("http://www.r3infoservices.com/Offline/"+userlogin.elementAt(7)+"/");
                                        mydb.getCompanyNameDetails();

                                        Intent i = new Intent(getApplicationContext(), Home.class);
                                        i.putExtra("UserType", "" + usertype);
                                        i.putExtra("UserName", "" + userlogin.elementAt(6));
                                        i.putExtra("Salesman", "" + userlogin.elementAt(6));
                                        Log.i("Usr Det",""+userlogin.toString());
                                        startActivity(i);
                                    }
                                }
                            }

                        } catch (Exception ex) {
                            Intent i = new Intent(getApplicationContext(), LoginRequest.class);
                            startActivity(i);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
            catch (Exception ex){}}

    }
    private void checkDatabase() {
        try {
            model = Model.getInstance();
            model.setDbname("mobile");
            SQLiteDatabase sampledb = this.openOrCreateDatabase(model.getDbname(), MODE_PRIVATE, null);
            ServerDatabase db = new ServerDatabase(getApplicationContext());
            db.createTables(sampledb);
        } catch (Exception ex) {
            Log.e("Exception is", "" + ex.getMessage());
        }
    }

    void animationTest() {
        try {
            Thread splashTread;
            splashTread = new Thread() {
                @Override
                public void run() {
                    try {
                        int waited = 0;
                        // Splash screen pause time
                        while (waited < 3500) {
                            sleep(100);
                            waited += 100;
                        }
                        Intent intent = new Intent(MainActivity.this,
                                LoginRequest.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        MainActivity.this.finish();
                    } catch (InterruptedException e) {
                        // do nothing
                    } finally {
                        MainActivity.this.finish();
                    }

                }
            };
            splashTread.start();
        } catch (Exception ex) {
        }
    }

    public boolean getAllPermissions() {
        boolean value=false;
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            // value=true;
        }
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_READ_PHONE_STATE);
            Log.i("1", "Phone State");

            // value=true;
        }

        permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_PHONE_STATE);
            Log.i("1", "Storage");

            // value=true;
        }
        permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, REQUEST_READ_PHONE_STATE);
            Log.i("1", "Internet");

            // value=true;
        }
        permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_READ_PHONE_STATE);
            Log.i("1", "Location");
            img.startAnimation(animation);

            // value=true;
        }


        //TODO
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            img.startAnimation(animation);

                        }}
                }
            }



        }
        return  true;
    }
    public String getImeiNumber() {

        String deviceid = "";
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                Log.i("7", "Permitted 1");
            }
            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return ""+ Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
            if (android.os.Build.VERSION.RELEASE.startsWith("10")) {
                deviceid = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.i("1 IMEI" + Build.VERSION.SDK_INT, "" + deviceid);
            } else {
                deviceid = mngr.getDeviceId().toString().trim();
                Log.i("2 IMEI" + Build.VERSION.SDK_INT, "" + deviceid);
            }
            if (android.os.Build.VERSION.SDK_INT > 28) {
                deviceid = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.i("3 IMEI" + Build.VERSION.SDK_INT, "" + deviceid);
            } else {
                deviceid = mngr.getDeviceId().toString().trim();
                Log.i("4 IMEI" + Build.VERSION.SDK_INT, "" + deviceid);
            }

            return deviceid;


        } catch (Exception ex) {
            Log.e("Error IMEI", "" + ex.getLocalizedMessage());
            return deviceid;
        }
    }
}
