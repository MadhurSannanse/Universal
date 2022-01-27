package rthree.infotech.r3infotech.mobile;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
/*import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;*/
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.navigation.NavigationView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;


public class Home extends AppCompatActivity implements LocationListener, NavigationView.OnNavigationItemSelectedListener {
    ImageButton neworder,vieworder,outstanding,salesreport,ledger,exit,receipt;
    private static final int INTERNET = 1;
    Vector order;
    int lastbillno;
    String usertype, username;
    LocationManager locationManager;
    static String locationstring="";
    ServerDatabase mydb;
    private MenuItem item;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    String fcmtoken="",imeiid="";
    boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        // navigationView.setNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        neworder=(ImageButton)findViewById(R.id.btn_neworder);
        vieworder=(ImageButton)findViewById(R.id.btn_vieworder);
        outstanding=(ImageButton)findViewById(R.id.btn_outstanding);
        salesreport=(ImageButton)findViewById(R.id.btn_collectionreport);
        ledger=(ImageButton)findViewById(R.id.btn_ledger);
        receipt=(ImageButton)findViewById(R.id.btn_receipt);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        usertype = b.getString("UserType");
        username = b.getString("UserName");
        mydb=new ServerDatabase(getApplicationContext());
        try
        {
            Toast.makeText(getApplicationContext(),""+username,Toast.LENGTH_SHORT).show();
            checkCompanyDetails();
            processLocation();
            sendToken();

        }
        catch (Exception Ex){Log.i("Error",""+ Ex.getLocalizedMessage());}
        // new SendNotification().execute("");
        neworder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    if(usertype.equals("Admin"))
                    {
                        Intent intent = new Intent(getApplicationContext(), AdminNewOrder.class);
                        intent.putExtra("UserType", "" + usertype);
                        intent.putExtra("UserName", "" + username);
                        intent.putExtra("Location",""+locationstring);
                        startActivity(intent);
                    }
                    else if(usertype.equals("TeamLeader"))
                    {
                        Intent intent = new Intent(getApplicationContext(), NewOrder.class);
                        intent.putExtra("UserType", "" + usertype);
                        intent.putExtra("UserName", "" + username);
                        intent.putExtra("Location",""+locationstring);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(getApplicationContext(), NewOrder.class);
                        intent.putExtra("UserType", "" + usertype);
                        intent.putExtra("UserName", "" + username);
                        intent.putExtra("Location",""+locationstring);
                        startActivity(intent);
                    }
                   /*else {
                        new GetLastBillNumber().execute("LastBill");
                    }*/
                } else {
                    Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
                }
            }
        });
        vieworder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    if(usertype.equals("Admin"))
                    {
                        Intent i = new Intent(getApplicationContext(), AdminViewAllOrders.class);
                        i.putExtra("UserType", "" + usertype);
                        i.putExtra("UserName", "" + username);
                        startActivity(i);
                    }
                    else if(usertype.equals("TeamLeader"))
                    {
                        Intent i = new Intent(getApplicationContext(), TeamLeaderViewOrder.class);
                        i.putExtra("UserType", "" + usertype);
                        i.putExtra("UserName", "" + username);
                        startActivity(i);
                    }
                    else
                    {
                        Intent i = new Intent(getApplicationContext(), ViewAllOrders.class);
                        i.putExtra("UserType", "" + usertype);
                        i.putExtra("UserName", "" + username);
                        startActivity(i);
                    }

                }
                else
                {
                    Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
                }
            }
        });
        salesreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    if(usertype.equals("Admin"))
                    {
                        Intent intent = new Intent(getApplicationContext(), AdminCollection.class);
                        intent.putExtra("UserType", "" + usertype);
                        intent.putExtra("UserName", "" + username);
                        intent.putExtra("Location",""+locationstring);
                        startActivity(intent);
                    }
                    else if(usertype.equals("TeamLeader"))
                    {
                        Intent intent = new Intent(getApplicationContext(), NewOrder.class);
                        intent.putExtra("UserType", "" + usertype);
                        intent.putExtra("UserName", "" + username);
                        intent.putExtra("Location",""+locationstring);
                       // startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(getApplicationContext(), AdminCollection.class);
                        intent.putExtra("UserType", "" + usertype);
                        intent.putExtra("UserName", "" + username);
                        intent.putExtra("Location",""+locationstring);
                        startActivity(intent);
                    }
                   /*else {
                        new GetLastBillNumber().execute("LastBill");
                    }*/
                } else {
                    Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
                }
            }
        });
        outstanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    if(usertype.equals("Admin"))
                    {
                        Intent i = new Intent(getApplicationContext(), AdminOutstanding.class);
                        i.putExtra("UserType", "" + usertype);
                        i.putExtra("UserName", "" + username);
                        startActivity(i);
                    }
                    else if(usertype.equals("TeamLeader"))
                    {
                        Intent i = new Intent(getApplicationContext(), TeamLeaderOutstanding.class);
                        i.putExtra("UserType", "" + usertype);
                        i.putExtra("UserName", "" + username);
                        startActivity(i);
                    }
                    else
                    {
                        Intent i = new Intent(getApplicationContext(), PartyOutstanding.class);
                        i.putExtra("UserType", "" + usertype);
                        i.putExtra("UserName", "" + username);
                        i.putExtra("Salesman", "" + username);
                        startActivity(i);
                    }

                }

                else
                {
                    Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
                }
            }
        });
        receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                    builder.setTitle("Process . .");
                    builder.setMessage("Select Payment Type");

                    builder.setPositiveButton("Cash Receipt", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            if (isNetworkAvailable()) {
                                if(usertype.equals("Admin"))
                                {
                                    Intent intent = new Intent(getApplicationContext(), AdminCashReceipt.class);
                                    intent.putExtra("UserType", "" + usertype);
                                    intent.putExtra("UserName", "" + username);
                                    intent.putExtra("Location",""+locationstring);
                                    intent.putExtra("Payment_Type","CASH");
                                    startActivity(intent);
                                }
                                else if(usertype.equals("TeamLeader"))
                                {
                                    Intent intent = new Intent(getApplicationContext(), CashReceipt.class);
                                    intent.putExtra("UserType", "" + usertype);
                                    intent.putExtra("UserName", "" + username);
                                    intent.putExtra("Location",""+locationstring);
                                    intent.putExtra("Payment_Type","CASH");
                                    startActivity(intent);
                                }
                                else
                                {
                                    Intent intent = new Intent(getApplicationContext(), CashReceipt.class);
                                    intent.putExtra("UserType", "" + usertype);
                                    intent.putExtra("UserName", "" + username);
                                    intent.putExtra("Location",""+locationstring);
                                    intent.putExtra("Payment_Type","CASH");
                                    startActivity(intent);
                                }

                            }
                            else
                            {
                                Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
                            }

                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("Credit Receipt", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isNetworkAvailable()) {
                                if(usertype.equals("Admin"))
                                {
                                    Intent intent = new Intent(getApplicationContext(), AdminReceipt.class);
                                    intent.putExtra("UserType", "" + usertype);
                                    intent.putExtra("UserName", "" + username);
                                    intent.putExtra("Location",""+locationstring);
                                    intent.putExtra("Payment_Type","CREDIT");
                                    startActivity(intent);
                                }
                                else if(usertype.equals("TeamLeader"))
                                {
                                    Intent intent = new Intent(getApplicationContext(), Receipt.class);
                                    intent.putExtra("UserType", "" + usertype);
                                    intent.putExtra("UserName", "" + username);
                                    intent.putExtra("Location",""+locationstring);
                                    intent.putExtra("Payment_Type","CREDIT");
                                    startActivity(intent);
                                }
                                else
                                {
                                    Intent intent = new Intent(getApplicationContext(), Receipt.class);
                                    intent.putExtra("UserType", "" + usertype);
                                    intent.putExtra("UserName", "" + username);
                                    intent.putExtra("Location",""+locationstring);
                                    intent.putExtra("Payment_Type","CREDIT");
                                    startActivity(intent);
                                }

                            }
                            else
                            {
                                Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
                            }

                            // Do nothing
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    //Log.e("Check 1","Ckecked ");
                }
                catch (Exception ex){
                    //Log.e("Exception ",""+ex.getLocalizedMessage());Toast.makeText(ConfirmOrder.this," पचका "+ex.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
             /* if (isNetworkAvailable()) {
                  if(usertype.equals("Admin"))
                  {
                      Intent intent = new Intent(getApplicationContext(), AdminReceipt.class);
                      intent.putExtra("UserType", "" + usertype);
                      intent.putExtra("UserName", "" + username);
                      intent.putExtra("Location",""+locationstring);
                      startActivity(intent);
                  }
                  else if(usertype.equals("TeamLeader"))
                  {
                      Intent intent = new Intent(getApplicationContext(), Receipt.class);
                      intent.putExtra("UserType", "" + usertype);
                      intent.putExtra("UserName", "" + username);
                      intent.putExtra("Location",""+locationstring);
                      startActivity(intent);
                  }
                  else
                  {
                      Intent intent = new Intent(getApplicationContext(), Receipt.class);
                      intent.putExtra("UserType", "" + usertype);
                      intent.putExtra("UserName", "" + username);
                      intent.putExtra("Location",""+locationstring);
                      startActivity(intent);
                  }

              }
              else
              {
                  Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
              }*/


            }
        });
        ledger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    if(usertype.equals("Admin"))
                    {
                        Intent intent = new Intent(getApplicationContext(), AdminLedger.class);
                        intent.putExtra("UserType", "" + usertype);
                        intent.putExtra("UserName", "" + username);
                        intent.putExtra("Location",""+locationstring);
                        startActivity(intent);
                    }
                    else if(usertype.equals("TeamLeader"))
                    {
                        Intent intent = new Intent(getApplicationContext(), TeamLeaderLedger.class);
                        intent.putExtra("UserType", "" + usertype);
                        intent.putExtra("UserName", "" + username);
                        intent.putExtra("Location",""+locationstring);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(getApplicationContext(), PartyLedger.class);
                        intent.putExtra("UserType", "" + usertype);
                        intent.putExtra("UserName", "" + username);
                        intent.putExtra("Salesman", "" + username);
                        intent.putExtra("Location",""+locationstring);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onNavigationItemSelected(MenuItem item) {
        this.item = item;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_neworder) {
            if (isNetworkAvailable()) {
                if(usertype.equals("Admin"))
                {
                    Intent intent = new Intent(getApplicationContext(), AdminNewOrder.class);
                    intent.putExtra("UserType", "" + usertype);
                    intent.putExtra("UserName", "" + username);
                    intent.putExtra("Location",""+locationstring);
                    startActivity(intent);
                }
                else if(usertype.equals("TeamLeader"))
                {
                    Intent intent = new Intent(getApplicationContext(), NewOrder.class);
                    intent.putExtra("UserType", "" + usertype);
                    intent.putExtra("UserName", "" + username);
                    intent.putExtra("Location",""+locationstring);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), NewOrder.class);
                    intent.putExtra("UserType", "" + usertype);
                    intent.putExtra("UserName", "" + username);
                    intent.putExtra("Location",""+locationstring);
                    startActivity(intent);
                }
                   /*else {
                        new GetLastBillNumber().execute("LastBill");
                    }*/
            } else {
                Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
            }
        }
        else if(id==R.id.nav_receipt)
        {
            try
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setTitle("Process . .");
                builder.setMessage("Select Payment Type");

                builder.setPositiveButton("Cash Receipt", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        if (isNetworkAvailable()) {
                            if(usertype.equals("Admin"))
                            {
                                Intent intent = new Intent(getApplicationContext(), AdminCashReceipt.class);
                                intent.putExtra("UserType", "" + usertype);
                                intent.putExtra("UserName", "" + username);
                                intent.putExtra("Location",""+locationstring);
                                intent.putExtra("Payment_Type","CASH");
                                startActivity(intent);
                            }
                            else if(usertype.equals("TeamLeader"))
                            {
                                Intent intent = new Intent(getApplicationContext(), CashReceipt.class);
                                intent.putExtra("UserType", "" + usertype);
                                intent.putExtra("UserName", "" + username);
                                intent.putExtra("Location",""+locationstring);
                                intent.putExtra("Payment_Type","CASH");
                                startActivity(intent);
                            }
                            else
                            {
                                Intent intent = new Intent(getApplicationContext(), CashReceipt.class);
                                intent.putExtra("UserType", "" + usertype);
                                intent.putExtra("UserName", "" + username);
                                intent.putExtra("Location",""+locationstring);
                                intent.putExtra("Payment_Type","CASH");
                                startActivity(intent);
                            }

                        }
                        else
                        {
                            Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Credit Receipt", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isNetworkAvailable()) {
                            if(usertype.equals("Admin"))
                            {
                                Intent intent = new Intent(getApplicationContext(), AdminReceipt.class);
                                intent.putExtra("UserType", "" + usertype);
                                intent.putExtra("UserName", "" + username);
                                intent.putExtra("Location",""+locationstring);
                                intent.putExtra("Payment_Type","CREDIT");
                                startActivity(intent);
                            }
                            else if(usertype.equals("TeamLeader"))
                            {
                                Intent intent = new Intent(getApplicationContext(), Receipt.class);
                                intent.putExtra("UserType", "" + usertype);
                                intent.putExtra("UserName", "" + username);
                                intent.putExtra("Location",""+locationstring);
                                intent.putExtra("Payment_Type","CREDIT");
                                startActivity(intent);
                            }
                            else
                            {
                                Intent intent = new Intent(getApplicationContext(), Receipt.class);
                                intent.putExtra("UserType", "" + usertype);
                                intent.putExtra("UserName", "" + username);
                                intent.putExtra("Location",""+locationstring);
                                intent.putExtra("Payment_Type","CREDIT");
                                startActivity(intent);
                            }

                        }
                        else
                        {
                            Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
                        }

                        // Do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                //Log.e("Check 1","Ckecked ");
            }
            catch (Exception ex){
                //Log.e("Exception ",""+ex.getLocalizedMessage());Toast.makeText(ConfirmOrder.this," पचका "+ex.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }

        }

        else if (id == R.id.nav_orderreport) {
            if (isNetworkAvailable()) {
                if(usertype.equals("Admin"))
                {
                    Intent i = new Intent(getApplicationContext(), AdminViewAllOrders.class);
                    i.putExtra("UserType", "" + usertype);
                    i.putExtra("UserName", "" + username);
                    startActivity(i);
                }
                else if(usertype.equals("TeamLeader"))
                {
                    Intent i = new Intent(getApplicationContext(), TeamLeaderViewOrder.class);
                    i.putExtra("UserType", "" + usertype);
                    i.putExtra("UserName", "" + username);
                    startActivity(i);
                }
                else
                {
                    Intent i = new Intent(getApplicationContext(), ViewAllOrders.class);
                    i.putExtra("UserType", "" + usertype);
                    i.putExtra("UserName", "" + username);
                    startActivity(i);
                }

            }
            else if(id==R.id.nav_ledger)
            {
                if (isNetworkAvailable()) {
                    if(usertype.equals("Admin"))
                    {
                        Intent intent = new Intent(getApplicationContext(), AdminLedger.class);
                        intent.putExtra("UserType", "" + usertype);
                        intent.putExtra("UserName", "" + username);
                        intent.putExtra("Location",""+locationstring);
                        startActivity(intent);
                    }
                    else if(usertype.equals("TeamLeader"))
                    {
                        Intent intent = new Intent(getApplicationContext(), TeamLeaderLedger.class);
                        intent.putExtra("UserType", "" + usertype);
                        intent.putExtra("UserName", "" + username);
                        intent.putExtra("Location",""+locationstring);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(getApplicationContext(), PartyLedger.class);
                        intent.putExtra("UserType", "" + usertype);
                        intent.putExtra("UserName", "" + username);
                        intent.putExtra("Location",""+locationstring);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
            }

        }



        else if (id == R.id.nav_outstanding) {
            /*if (isNetworkAvailable()) {
                if (!usertype.equals("Customer")) {
                    Intent intent = new Intent(getApplicationContext(), PartyOutstanding.class);
                    intent.putExtra("UserType", "" + usertype);
                    intent.putExtra("UserName", "" + username);
                    intent.putExtra("Location",""+locationstring);
                    startActivity(intent);
                } else {
                    new GetLastBillNumber().execute("LastBill");
                }
            } else {
                Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
            }*/
            if (isNetworkAvailable()) {
                if(usertype.equals("Admin"))
                {
                    Intent i = new Intent(getApplicationContext(), AdminOutstanding.class);
                    i.putExtra("UserType", "" + usertype);
                    i.putExtra("UserName", "" + username);
                    startActivity(i);
                }
                else if(usertype.equals("TeamLeader"))
                {
                    Intent i = new Intent(getApplicationContext(), TeamLeaderOutstanding.class);
                    i.putExtra("UserType", "" + usertype);
                    i.putExtra("UserName", "" + username);
                    startActivity(i);
                }
                else
                {
                    Intent i = new Intent(getApplicationContext(), PartyOutstanding.class);
                    i.putExtra("UserType", "" + usertype);
                    i.putExtra("UserName", "" + username);
                    startActivity(i);
                }

            }

            else
            {
                Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_ledger) {
            if (isNetworkAvailable()) {
                if(usertype.equals("Admin"))
                {
                    Intent intent = new Intent(getApplicationContext(), AdminLedger.class);
                    intent.putExtra("UserType", "" + usertype);
                    intent.putExtra("UserName", "" + username);
                    intent.putExtra("Location",""+locationstring);
                    startActivity(intent);
                }
                else if(usertype.equals("TeamLeader"))
                {
                    Intent intent = new Intent(getApplicationContext(), TeamLeaderLedger.class);
                    intent.putExtra("UserType", "" + usertype);
                    intent.putExtra("UserName", "" + username);
                    intent.putExtra("Location",""+locationstring);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), PartyLedger.class);
                    intent.putExtra("UserType", "" + usertype);
                    intent.putExtra("UserName", "" + username);
                    intent.putExtra("Location",""+locationstring);
                    startActivity(intent);
                }

            } else {
                Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
            }


        } else if (id == R.id.nav_camera) {
            Intent i=new Intent(getApplicationContext(),CaptureImage.class);
            startActivity(i);

        } else if (id == R.id.nav_exit) {
            if (isNetworkAvailable()) {
                finish();
            } else {
                finish();
                //Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
            }

        }
        else if(id==R.id.nav_receiptreport)
        {
            if (isNetworkAvailable()) {
                if(usertype.equals("Admin"))
                {
                    Intent i = new Intent(getApplicationContext(), AdminReceiptView.class);
                    i.putExtra("UserType", "" + usertype);
                    i.putExtra("UserName", "" + username);
                    startActivity(i);
                }
                else if(usertype.equals("TeamLeader"))
                {
                    Intent i = new Intent(getApplicationContext(), TeamLederReceiptView.class);
                    i.putExtra("UserType", "" + usertype);
                    i.putExtra("UserName", "" + username);
                    startActivity(i);
                }
                else
                {
                    Intent i = new Intent(getApplicationContext(), ViewAllReceipts.class);
                    i.putExtra("UserType", "" + usertype);
                    i.putExtra("UserName", "" + username);
                    i.putExtra("Salesman", "" + username);
                    startActivity(i);
                }

            }
            else
            {
                Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
            }
        }
        else if(id==R.id.nav_setCompany)
        {
            if (isNetworkAvailable()) {
                Intent i = new Intent(getApplicationContext(), SelectCompany.class);
                i.putExtra("UserType", "" + usertype);
                i.putExtra("UserName", "" + username);
                i.putExtra("Salesman", "" + username);
                startActivity(i);
            }
            else
            {
                Toast.makeText(Home.this, "No Internte", Toast.LENGTH_SHORT).show();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void processLocation() {
        Log.i("Process","Yes");
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        getLocation();

    }

    void getLocation() {
        try {
            Log.i("Location","YES");
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

            }
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, (LocationListener) this);
            // get the last know location from your location manager.
            //Location location= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
           /* locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            Toast.makeText(Home_Screen.this, "Logitude " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            Toast.makeText(Home_Screen.this, "Latitude " + location.getLatitude(), Toast.LENGTH_SHORT).show();


            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String loc = "" + addresses.get(0).getAddressLine(0) + ", " +
                        addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2);
                Toast.makeText(Home_Screen.this, "Loc " + loc, LENGTH_SHORT).show();
                Log.i("Test Location ", " " + loc);
            } catch (Exception e) {
                Log.e("Err 1", "" + e.getLocalizedMessage());
            }*/


        } catch (SecurityException e) {
            Log.e("Error ", "" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        try {
            // Log.i("Listener","Called");
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String loc = "" + addresses.get(0).getAddressLine(0) + ", " +
                    addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2);
            if(loc.length()>11) {
                locationstring = ""+loc.substring(0,loc.length()-10);
            }
            else
            {
                locationstring = loc;
            }
            //Toast.makeText(Home_Screen.this, "Loc " + loc, Toast. LENGTH_SHORT).show();
            //  Log.i("Currect Location ", " " + loc);
        } catch (Exception e) {
            Log.e("Err ", "" + e.getLocalizedMessage());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(Home.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    public static boolean isNetworkOnline(Context con) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);

            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);

                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    status = true;
                } else {
                    status = false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return status;
    }

    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(Home.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                //Log.e("Internet 1","Available");
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();


            }
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();

        } else {
            //TODO
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                //Log.e("Internet 1","Available");
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();

            }
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            //Log.e("Internet 3","Available");
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();

        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class GetLastBillNumber extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(Home.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait ");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {

                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.

                // Server url call by GET method


                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectlastbillnumber.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // ////Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    ////Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // ////Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    ////Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                //Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray = new JSONArray(result.trim());
                    JSONObject jsonObject = null;
                    int no = 1;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        //  login.addElement(json_data.getString("ID"));
                        String bill = jsonObject.getString("max").toString().trim();
                        if (!bill.equals("")) {
                            try {
                                no = Integer.parseInt(bill);
                                no = no + 1;
                                lastbillno = no;
                                if (usertype.equals("Customer")) {
                                    isCustomer();
                                } else {
                                    if(usertype.equals("Admin")) {
                                        Intent intent = new Intent(getApplicationContext(), AdminNewOrder.class);
                                        intent.putExtra("UserType", "" + usertype);
                                        intent.putExtra("UserName", "" + username);
                                        intent.putExtra("Location", "" + locationstring);
                                        startActivity(intent);
                                    }
                                    if(usertype.equals("Salesman")) {
                                        Intent intent = new Intent(getApplicationContext(), NewOrder.class);
                                        intent.putExtra("UserType", "" + usertype);
                                        intent.putExtra("UserName", "" + username);
                                        intent.putExtra("Location", "" + locationstring);
                                        startActivity(intent);
                                    }
                                    if(usertype.equals("TeamLeader")) {
                                        Intent intent = new Intent(getApplicationContext(), NewOrder.class);
                                        intent.putExtra("UserType", "" + usertype);
                                        intent.putExtra("UserName", "" + username);
                                        intent.putExtra("Location", "" + locationstring);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Intent intent = new Intent(getApplicationContext(), NewOrder.class);
                                        intent.putExtra("UserType", "" + usertype);
                                        intent.putExtra("UserName", "" + username);
                                        intent.putExtra("Location", "" + locationstring);
                                        startActivity(intent);
                                    }
                                }

                            } catch (Exception ex) {
                                Toast.makeText(getApplicationContext(), "Error in bill number", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            no = 1;
                            lastbillno = no;
                            if (usertype.equals("Customer")) {
                                isCustomer();
                            } else {
                                Intent intent = new Intent(getApplicationContext(), NewOrder.class);
                                intent.putExtra("UserType", "" + usertype);
                                intent.putExtra("UserName", "" + username);
                                intent.putExtra("Location",""+locationstring);
                                startActivity(intent);
                            }

                        }

                    }


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No Any Distributor Found", Toast.LENGTH_LONG).show();
                }

            }
        }

    }

    private void isCustomer() {
        try {
            try {
                SimpleDateFormat df = new SimpleDateFormat("d/MM/yyyy hh:mm a");
                SimpleDateFormat Ins_Date = new SimpleDateFormat("d/MM/yyyy");
                String partyname = username.toString().trim();
                String curdate = Ins_Date.format(Calendar.getInstance().getTime());
                String paymode = "Cash";
                String distname = usertype.toString().trim();
                String date = df.format(Calendar.getInstance().getTime());
                String InsDate = Ins_Date.format(Calendar.getInstance().getTime());
                order = new Vector();
                order.addElement("" + partyname);
                order.addElement("" + lastbillno);
                order.addElement("" + curdate);
                order.addElement("" + InsDate);
                order.addElement("0");
                order.addElement("NO");
                order.addElement("" + paymode);
                order.addElement("" + username);
                order.addElement("" + distname);
                order.addElement(""+locationstring);
                //Log.e("Order 1",""+order.toString());
                new SaveOrder().execute("Save");
                //////Log.e("Bill 1 ",""+billno);
                // serverOrder(view, details);
                Intent i = new Intent(getApplicationContext(), ItemDisplay.class);
                i.putExtra("UserType", "" + usertype);
                i.putExtra("UserName", "" + username);
                i.putExtra("BillNumber", "" + lastbillno);
                // this.finish();
                startActivity(i);


            } catch (Exception ex) {

            }
        } catch (Exception ex) {
        }
    }

    private class SaveOrder extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(Home.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait ");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {

                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.

                // Server url call by GET method

                String request = "";
                for (int i = 0; i < order.size(); i++) {
                    if ("" + order.elementAt(i) == "" || "" + order.elementAt(i) == null) {
                        //m.add(i,"0");
                    }
                    request += order.elementAt(i).toString().trim();
                    if (i == order.size() - 1) {
                        // continue;
                    } else {
                        request = request + "~~~";
                    }

                }
                request = request + "^^^";
                //Log.e("Order 2",""+request);
                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                nameValuePairs.add(new BasicNameValuePair("details", request));


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "insertorder.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // ////Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    ////Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // ////Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    ////Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                //Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray = new JSONArray(result.trim());
                    JSONObject jsonObject = null;

                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(), "No Any Found"+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    private class SendToken extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(Home.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait ");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {

                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.

                // Server url call by GET method


                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                nameValuePairs.add(new BasicNameValuePair("fcm_token", fcmtoken));
                nameValuePairs.add(new BasicNameValuePair("Name", username));


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "notification_insert.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                //Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;

                } catch (Exception e) {
                }

            }
        }

    }
    private void sendToken()
    {
        try
        {
            int permissionCheck = ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_PHONE_STATE);


            SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
            fcmtoken=sharedPreferences.getString(getString(R.string.FCM_TOKEN),"");
            //Log.e("Token",""+fcmtoken);
            ServerDatabase mydb=new ServerDatabase(getApplicationContext());
            Vector det;
            det=mydb.getFCMDetails(getImeiNumber());
            if(det.size()==0)
            {
                new SendToken().execute("");
                det=new Vector();
                det.addElement(fcmtoken);
                det.addElement(getImeiNumber());
                mydb.addFCMDetails(det);
            }

            StringRequest stringRequest=new StringRequest(Request.Method.GET, new JsonBilder().getHostName(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params=new HashMap<String,String>();
                    params.put("Name", getImeiNumber());
                    params.put("fcm_token", fcmtoken);
                    return params;
                }
            };
            MySingleton.getInstance(Home.this).addToRequestqueue(stringRequest);
        }
        catch (Exception ex)
        {
            Log.e("Error in "," Send Token "+ex.getLocalizedMessage());
        }
    }
    private class SendNotification extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(Home.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait ");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {

                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.

                // Server url call by GET method


                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                nameValuePairs.add(new BasicNameValuePair("Name", ""+getImeiNumber()));
                nameValuePairs.add(new BasicNameValuePair("Message", "Checking"));
                nameValuePairs.add(new BasicNameValuePair("Title", "Jaikisan"));


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "notification_send.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                //Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;

                } catch (Exception e) {
                }

            }
        }

    }
    public String getImeiNumber() {
        String deviceid = "";
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(Home.this, android.Manifest.permission.READ_PHONE_STATE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(Home.this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                Log.i("7", "Permitted 1");
            }
            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.RELEASE.startsWith("10")) {
                deviceid = Settings.Secure.getString(Home.this.getContentResolver(), Settings.Secure.ANDROID_ID);
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return "";
                }
                deviceid = mngr.getDeviceId().toString().trim();
            }
            if(Build.VERSION.SDK_INT>=28)
            {
                deviceid= Settings.Secure.getString(Home.this.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            {
                // deviceid=mngr.getDeviceId().toString().trim();
            }
            Log.i("IMEI",""+deviceid);
            return deviceid;
        }
        catch (Exception ex){Log.e("Error IMEI",""+ex.getLocalizedMessage());return deviceid;}
    }
    public void checkCompanyDetails()
    {
        try
        {
            ServerDatabase mydb = new ServerDatabase(getApplicationContext());
            Vector comp = mydb.getCompanyDetails();
            Log.i("Company Det",""+comp.toString());
            if(comp.size()==0)
            {
                new LoadCompanyDetails().execute("");
            }
            else
            {
                Model model=Model.getInstance();
                model.setCompanyName(""+comp.elementAt(0));
                model.setCompanyContact(""+comp.elementAt(1));
                model.setCompanyAddress(""+comp.elementAt(2));
                model.setCompanyGSTIN(""+comp.elementAt(3));
            }
        }
        catch (Exception ex){Log.i("Comp error",""+ex.getLocalizedMessage());}
    }


    private class LoadCompanyDetails extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(Home.this);
        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait.");
            Dialog.show();
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {

                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.

                // Server url call by GET method


                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                nameValuePairs.add(new BasicNameValuePair("username", username));


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectCompany.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // ////Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    ////Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // ////Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    ////Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                //Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;
                    mydb=new ServerDatabase(getApplicationContext());
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        jsonObject = jsonArray.getJSONObject(i);
                        Vector company=new Vector();
                        company.addElement(jsonObject.getString("ID"));
                        company.addElement(jsonObject.getString("company_name"));
                        company.addElement(jsonObject.getString("contact_no"));
                        company.addElement(jsonObject.getString("address"));
                        company.addElement(jsonObject.getString("gstin_no"));
                        mydb.deleteCompanyDetails();
                        if (mydb.addCompanyDetails(company)) {
                            checkCompanyDetails();

                        }
                    }

                } catch (Exception e) {
                    Log.e("Company", "No Company "+e.getLocalizedMessage());
                    //Toast.makeText(getApplicationContext(), "No Any Company Details Found ", Toast.LENGTH_LONG).show();

                }

            }
        }

    }
}
