package rthree.infotech.r3infotech.mobile;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
/*import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;*/
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.Vector;

public class ViewApprovedOrder extends AppCompatActivity {
    private static final int STORAGE_CODE = 1000;
    ProgressDialog progressBar;
    ServerDatabase mydb;
    String usertype,username,billnumber,proid,pid,plevel,orderdate="";
    TextView txtname,txtgst,subtotal,txttotal;
    TextView etdiscount,etfreight,etremark;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    TextView spdispatch;
    private static final int REQUEST_READ_PHONE_STATE =1 ;
    private static final int INTERNET=1;
    Button save,view;
    ListView lstorderitems;
    Vector order,orderdet;
    ArrayList<String> customer;
    double finalsubtotal=0,finalgst=0;
    String pronames[],prorates[],proqty[],prototal[],productid[],message,productname[],disc[],filename="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_approved_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtname=(TextView)findViewById(R.id.txt_partyname);
        txtgst=(TextView)findViewById(R.id.txt_gst);
        txttotal=(TextView)findViewById(R.id.txt_total);
        subtotal=(TextView)findViewById(R.id.txt_subtotal);
        etdiscount=(TextView)findViewById(R.id.et_discount);
        etfreight=(TextView)findViewById(R.id.et_freight);
        etremark=(TextView)findViewById(R.id.et_remark);
        spdispatch=(TextView)findViewById(R.id.sp_dispatch);
        message="";
        lstorderitems=(ListView)findViewById(R.id.lstorderitems);
        try
        {
            order=new Vector();
            orderdet=new Vector();
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            usertype = b.getString("UserType");
            username = b.getString("UserName");
            billnumber=b.getString("BillNumber");
            plevel=b.getString("Plevel");
            new LoadOrder().execute("Order");
            new LoadOrderDetails().execute("OrderDetails");
            //loadOrder(txtname,billnumber);
            //loadOrderDetails(txtname,billnumber);
            txtname.setOnLongClickListener(new View.OnLongClickListener() {
                @TargetApi(Build.VERSION_CODES.N)
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public boolean onLongClick(View v) {
                    //   sendUsingWhatsapp();
                    sendUsingPDF();
                    return false;
                }
            });
            txtgst.setOnLongClickListener(new View.OnLongClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public boolean onLongClick(View v) {
                    //   sendUsingWhatsapp();
                    sendUsingPDF();
                    return false;
                }
            });
            txttotal.setOnLongClickListener(new View.OnLongClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public boolean onLongClick(View v) {
                    //   sendUsingWhatsapp();
                    sendUsingPDF();
                    return false;
                }
            });
            etdiscount.setOnLongClickListener(new View.OnLongClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public boolean onLongClick(View v) {
                    //   sendUsingWhatsapp();
                    sendUsingPDF();
                    return false;
                }
            });
            etremark.setOnLongClickListener(new View.OnLongClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public boolean onLongClick(View v) {
                    //   sendUsingWhatsapp();
                    sendUsingPDF();
                    return false;
                }
            });
            etfreight.setOnLongClickListener(new View.OnLongClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public boolean onLongClick(View v) {
                    //   sendUsingWhatsapp();
                    sendUsingPDF();
                    return false;
                }
            });
        }
        catch (Exception ex)
        {

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public  boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
       /* if(id == android.R.id.home)
        {
            this.finish();
            return true;
        }*/
        switch (id)
        {
            case R.id.action_date:
                //showDialog(DILOG_ID);
                break;
            case R.id.action_pdf:
                createPDFInvoice("");
                break;
            case android.R.id.home:
                this.finish();
                return true;
            //break;
            case R.id.action_home:
                Intent intent =new Intent(getApplicationContext(),Home.class);
                intent.putExtra("UserType", "" + usertype);
                intent.putExtra("UserName", "" + username);
                //finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }
    public void loadOrder(View v, final String ordernumber)
    {
        try {
            progressBar = new ProgressDialog(v.getContext());
            progressBar.setCancelable(true);
            progressBar.setMessage("Please Wait ...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setProgress(0);
            progressBar.setMax(100);
            progressBar.show();
            //reset progress bar and filesize status
            progressBarStatus = 0;
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            new Thread(new Runnable() {
                public void run() {
                    while (progressBarStatus < 1000) {
                        // performing operation
                        progressBarStatus = 1000;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                        }
                        // Updating the progress bar
                        progressBarHandler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(progressBarStatus);
                                try {
                                    try
                                    {
                                        try
                                        {
                                            if (android.os.Build.VERSION.SDK_INT > 9) {
                                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                                        .permitAll().build();
                                                StrictMode.setThreadPolicy(policy);
                                            }
                                            ////Log.e("Log2 ",""+username);
                                            JSONArray jArray = new JSONArray(new JsonBilder().getCurrentOrder(ordernumber));
                                            ////Log.e("Data ",""+jArray.toString());
                                            JSONObject json_data = null;
                                            ////Log.e("Log ","Log 3");
                                            ArrayList<String> arrlst = new ArrayList<String>();
                                            for (int i = 0; i < jArray.length(); i++)
                                            {
                                                json_data = jArray.getJSONObject(i);
                                                //  login.addElement(json_data.getString("ID"));
                                                order.addElement(json_data.getString("Customer_Name"));
                                                order.addElement(json_data.getString("Bill_No"));
                                                order.addElement(json_data.getString("CDate"));
                                                order.addElement(json_data.getString("Ins_Date"));
                                                order.addElement(json_data.getString("Total_amount"));
                                                order.addElement(json_data.getString("Status"));
                                                order.addElement(json_data.getString("paymode"));
                                                order.addElement(json_data.getString("salesman"));
                                                txtname.setText(""+json_data.getString("Customer_Name"));
                                                txtgst.setText(""+json_data.getString("Gst_Amount"));
                                                subtotal.setText(""+json_data.getString("Amount"));
                                                txttotal.setText(""+json_data.getString("Total_amount"));
                                                etdiscount.setText(""+json_data.getString("Tot"));
                                                etfreight.setText(""+json_data.getString("CDate"));
                                                etremark.setText(""+json_data.getString("CDate"));
                                            }

                                        } catch (Exception e) {
                                            //e.printStackTrace();
                                            Toast.makeText(getApplicationContext(),"Not Loaded ..!" ,Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                    catch(Exception ex)
                                    {
                                        Toast.makeText(getApplicationContext(), "Error "+ex.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception ex) {
                                    Toast.makeText(getBaseContext(), "Error ", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    // performing operation if file is downloaded,
                    if (progressBarStatus >= 100) {
                        // sleeping for 1 second after operation completed
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                        }
                        // close the progress bar dialog
                        progressBar.dismiss();
                    }

                }
            }).start();
        } catch (Exception ex) {
            Toast.makeText(getBaseContext(), "Error " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void loadOrderDetails(View v, final String ordernumber)
    {
        try {
            progressBar = new ProgressDialog(v.getContext());
            progressBar.setCancelable(true);
            progressBar.setMessage("Please Wait ...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setProgress(0);
            progressBar.setMax(100);
            progressBar.show();
            //reset progress bar and filesize status
            progressBarStatus = 0;
            new Thread(new Runnable() {
                public void run() {
                    while (progressBarStatus < 1000) {
                        // performing operation
                        progressBarStatus = 1000;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                        }
                        // Updating the progress bar
                        progressBarHandler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(progressBarStatus);
                                try {
                                    try
                                    {
                                        try
                                        {
                                            if (android.os.Build.VERSION.SDK_INT > 9) {
                                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                                        .permitAll().build();
                                                StrictMode.setThreadPolicy(policy);
                                            }
                                            ////Log.e("Log2 ",""+username);
                                            JSONArray jArray = new JSONArray(new JsonBilder().getCurrentOrderDet(ordernumber));
                                            ////Log.e("Data ",""+jArray.toString());
                                            JSONObject json_data = null;
                                            ////Log.e("Log ","Log 3");
                                            orderdet=new Vector();
                                            for (int i = 0; i < jArray.length(); i++)
                                            {
                                                json_data = jArray.getJSONObject(i);
                                                Vector orderdata=new Vector();
                                                orderdata.addElement(json_data.getString("Order_ID"));
                                                orderdata.addElement(json_data.getString("name"));
                                                orderdata.addElement(json_data.getString("Rate"));
                                                orderdata.addElement(json_data.getString("Qty"));
                                                orderdata.addElement(json_data.getString("Amount"));
                                                orderdata.addElement(json_data.getString("Status"));
                                                orderdata.addElement(json_data.getString("scheme"));
                                                orderdet.addElement(orderdata);
                                                Log.e("Det",""+orderdata.toString());
                                            }

                                            loadOrderDetailListItems();
                                        } catch (Exception e) {
                                            //e.printStackTrace();
                                            Toast.makeText(getApplicationContext(),"Not Loaded ..!" ,Toast.LENGTH_SHORT).show();
                                            progressBar.dismiss();
                                        }

                                    }
                                    catch(Exception ex)
                                    {
                                        Toast.makeText(getApplicationContext(), "Error "+ex.getMessage(),Toast.LENGTH_SHORT).show();
                                        progressBar.dismiss();
                                    }
                                } catch (Exception ex) {
                                    Toast.makeText(getBaseContext(), "Error " + ex.getMessage(), Toast.LENGTH_LONG).show();
                                    progressBar.dismiss();
                                }
                            }
                        });
                    }
                    // performing operation if file is downloaded,
                    if (progressBarStatus >= 100) {
                        // sleeping for 1 second after operation completed
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                        }
                        // close the progress bar dialog
                        progressBar.dismiss();
                    }
                    if(progressBarStatus==100)
                    {
                        progressBar.dismiss();
                    }

                }
            }).start();

        } catch (Exception ex) {
            Toast.makeText(getBaseContext(), "Error " + ex.getMessage(), Toast.LENGTH_LONG).show();
            progressBar.dismiss();
        }
    }
    private void loadOrderDetailListItems()
    {
        try
        {
            pronames=new String[orderdet.size()+1];
            prorates=new String[orderdet.size()+1];
            proqty=new String[orderdet.size()+1];
            prototal=new String[orderdet.size()+1];
            productid=new String[orderdet.size()+1];
            productname=new String[orderdet.size()+1];
            disc=new String[orderdet.size()+1];
            pronames[0]="Code";
            prorates[0]="Rate";
            proqty[0]="Quantity";
            prototal[0]="Total";
            productid[0]="";
            productname[0]="";
            disc[0]="Disc";
            message=message+" \n*Order Details* :\n";
            for(int i=0;i<orderdet.size();i++)
            {
                Vector data=(Vector)orderdet.elementAt(i);
                pronames[i+1]=""+data.elementAt(1);
                try {
                    if(data.elementAt(10).toString().trim().equals(""))
                    {
                        prorates[i+1]=""+roundTwo(Double.parseDouble(""+data.elementAt(2)));
                    }
                    else
                    {
                        prorates[i+1]=""+roundTwo((Double.parseDouble(""+data.elementAt(2))+(Double.parseDouble(""+data.elementAt(6)))));
                    }

                }
                catch (Exception ex){prorates[i+1]=""+roundTwo((Double.parseDouble(""+data.elementAt(2))));}
                proqty[i+1]=""+data.elementAt(3);
                prototal[i+1]=""+roundTwo((Double.parseDouble(""+data.elementAt(4))));
                productid[i+1]=""+data.elementAt(7).toString();
                productname[i+1]=""+data.elementAt(9).toString();
                try {
                    if (data.elementAt(10).toString().trim().equals("")) {
                        disc[i + 1] = "0";
                    } else {
                        disc[i + 1] = "" + data.elementAt(10).toString();
                    }
                }
                catch (Exception ex){disc[i + 1] = "0";}
                message=message+""+data.elementAt(1)+"\n"+" *Quantity* : "+data.elementAt(3)+" Nos";
                message=message+"\n *Rate* :"+roundTwo(Double.parseDouble(""+data.elementAt(2)))+"\n *Total* :"+roundTwo(Double.parseDouble(""+data.elementAt(4)))+"\n";
                // Log.e("Det"+i,""+data.toString());

            }
            //Log.e("Total Length ",""+orderdet.size());
            CustomAdapterNew adapter=new CustomAdapterNew(getApplicationContext(),pronames,prorates,proqty,prototal,productid,disc,productname);
            lstorderitems.setAdapter(adapter);
            new LoadCustomerDetails().execute("");
        }
        catch(Exception ex)
        {
            Toast.makeText(getApplicationContext(),"Fail to view Order "+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    public double roundTwo(double value)
    {
        try
        {

            DecimalFormat twoDForm = new DecimalFormat("#");
            return Double.valueOf(twoDForm.format(value));
        }
        catch (Exception ex)
        {
            return 0;
        }
    }
    private class LoadOrder extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(ViewApprovedOrder.this);


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
                //                Log.e("Bill Number 2 "," "+billnumber);
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                nameValuePairs.add(new BasicNameValuePair("ordernumber", billnumber));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectcurrentorder.php");
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
                    Log.i("Current Order -",""+result);

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
                Log.i("Result 1 :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;
                    order=new Vector();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        Log.e("Comming ","Sucess "+i);
                        jsonObject = jsonArray.getJSONObject(i);
                        //  login.addElement(json_data.getString("ID"));
                        order.addElement(jsonObject.getString("Customer_Name"));
                        order.addElement(jsonObject.getString("Bill_No"));
                        order.addElement(jsonObject.getString("CDate"));
                        order.addElement(jsonObject.getString("Ins_Date"));
                        order.addElement(jsonObject.getString("Total_Amount"));
                        order.addElement(jsonObject.getString("Gst"));
                        order.addElement(jsonObject.getString("Gst_Amount"));
                        order.addElement(jsonObject.getString("Total_Discount"));
                        order.addElement(jsonObject.getString("Total_Freight"));
                        order.addElement(jsonObject.getString("Status"));
                        order.addElement(jsonObject.getString("Paymode"));
                        order.addElement(jsonObject.getString("Total_Amount"));
                        order.addElement(jsonObject.getString("Salesman"));
                        order.addElement(jsonObject.getString("Remark"));
                        order.addElement(jsonObject.getString("Dispatch_Mode"));
                        finalsubtotal=Double.parseDouble(""+jsonObject.getString("Total_Amount"));
                        finalgst=Double.parseDouble(""+jsonObject.getString("Gst"));
                        Model model=Model.getInstance();
                        model.setGstAmount(finalgst);
                        model.setSubAmount(finalsubtotal);
                        txtname.setText(""+jsonObject.getString("Customer_Name"));
                        subtotal.setText(""+roundTwo(Double.parseDouble("0"+jsonObject.getString("Total_Amount"))));
                        txtgst.setText(""+roundTwo(Double.parseDouble("0"+jsonObject.getString("Gst"))));
                        etdiscount.setText(""+roundTwo(Double.parseDouble("0"+jsonObject.getString("Total_Discount"))));
                        etfreight.setText(""+roundTwo(Double.parseDouble("0"+jsonObject.getString("Total_Freight"))));
                        txttotal.setText(""+roundTwo(Double.parseDouble("0"+jsonObject.getString("Gst_Amount"))));
                        etremark.setText(""+jsonObject.getString("Remark"));
                        spdispatch.setText(jsonObject.getString("Dispatch_Mode"));

                        orderdate=""+jsonObject.getString("CDate");
                    }



                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No Any Order Found"+e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    private class LoadOrderDetails extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(ViewApprovedOrder.this);


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
                nameValuePairs.add(new BasicNameValuePair("ordernumber", billnumber));
                nameValuePairs.add(new BasicNameValuePair("plevel", plevel));


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectcurrentorderdet.php");
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
                    Log.i("Current Order -",""+result);

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
                Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;
                    orderdet=new Vector();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        jsonObject = jsonArray.getJSONObject(i);
                        Vector orderdata=new Vector();
                        orderdata.addElement(jsonObject.getString("Order_ID"));
                        orderdata.addElement(jsonObject.getString("name"));
                        orderdata.addElement(jsonObject.getString("Rate"));
                        orderdata.addElement(jsonObject.getString("Qty"));
                        orderdata.addElement(jsonObject.getString("Amount"));
                        orderdata.addElement(jsonObject.getString("Status"));
                        orderdata.addElement(jsonObject.getString("scheme"));
                        orderdata.addElement(jsonObject.getString("Product_ID"));
                        orderdata.addElement(jsonObject.getString("GST_Amount"));
                        orderdata.addElement(jsonObject.getString("product_name"));
                        orderdata.addElement(jsonObject.getString("scheme"));
                        orderdet.addElement(orderdata);
                    }

                    loadOrderDetailListItems();


                } catch (Exception e) {
                    Log.e("Order Det",""+e.getMessage());
                    //Toast.makeText(getApplicationContext(), "No Any Order Found", Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    private class MakeOrderConfirm extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(ViewApprovedOrder.this);


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
                nameValuePairs.add(new BasicNameValuePair("billnumber", billnumber));
                nameValuePairs.add(new BasicNameValuePair("discount", etdiscount.getText().toString().trim()));
                nameValuePairs.add(new BasicNameValuePair("freight", etfreight.getText().toString().trim()));
                nameValuePairs.add(new BasicNameValuePair("remark", etremark.getText().toString().trim()));
                nameValuePairs.add(new BasicNameValuePair("dispatch", spdispatch.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("gst",txtgst.getText().toString().trim()));
                nameValuePairs.add(new BasicNameValuePair("subtotal",subtotal.getText().toString().trim()));
                nameValuePairs.add(new BasicNameValuePair("gsttotal",txttotal.getText().toString().trim()));






                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "confirmorder.php");
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


            //  uiUpdate.setText("Output : "+Error);

            if (Error != null) {
            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                // Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;
                    orderdet=new Vector();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {

                        Toast.makeText(getApplicationContext(), "Order Confirm Successfully", Toast.LENGTH_LONG).show();
                        Intent intent =new Intent(getApplicationContext(),Home.class);
                        intent.putExtra("UserType", "" + usertype);
                        intent.putExtra("UserName", "" + username);
                        //   finish();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                    Toast.makeText(getApplicationContext(), "Order Confirm Successfully", Toast.LENGTH_LONG).show();
                    Intent intent =new Intent(getApplicationContext(),Home.class);
                    intent.putExtra("UserType", "" + usertype);
                    intent.putExtra("UserName", "" + username);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    //finish();
                    startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Sales Confirm Successfully.", Toast.LENGTH_LONG).show();
                    Intent intent =new Intent(getApplicationContext(),Home.class);
                    intent.putExtra("UserType", "" + usertype);
                    intent.putExtra("UserName", "" + username);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    //finish();
                    startActivity(intent);
                }

            }
        }

    }
    public class DeleteOrderDetails extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(ViewApprovedOrder.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait For Delete ");
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
                nameValuePairs.add(new BasicNameValuePair("billnumber", billnumber));
                nameValuePairs.add(new BasicNameValuePair("productid",""+getProductID()));


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "deleteproductorderdet.php");
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
                Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    public void setProductID(String pid)
    {
        this.pid=pid;
        //Log.e("pid "," "+pid);

    }
    public String getProductID()
    {
        return pid;
    }
    public void deleteRequest()
    {
        try
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewApprovedOrder.this);
            builder.setTitle("Deleteing . .");
            builder.setMessage("Are you sure to delete ?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    new DeleteOrderDetails().execute("Delete");
                    new LoadOrder().execute("Order");
                    new LoadOrderDetails().execute("OrderDetails");
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

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
    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(ViewApprovedOrder.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ViewApprovedOrder.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                //Log.e("Internet 1","Available");
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();


            }
            ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();

        } else {
            //TODO
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                //Log.e("Internet 1","Available");
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();

            }
            ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            //Log.e("Internet 3","Available");
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case STORAGE_CODE:{
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Permission Denied.....",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void exportData()
    {
        try
        {
            /**
             * Creating Document
             */
            Document document = new Document();
            String dest = Environment.getExternalStorageDirectory().getPath() + "/mypdf/";
// Location to save
            PdfWriter.getInstance(document, new FileOutputStream(dest));

// Open to write
            document.open();
            /***
             * Variables for further use....
             */
            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            float mHeadingFontSize = 20.0f;
            float mValueFontSize = 26.0f;
/**
 * How to USE FONT....
 */
            BaseFont urName = BaseFont.createFont(getResources().getFont(R.font.myriadpro_bold).toString(), "UTF-8", BaseFont.EMBEDDED);
            // LINE SEPARATOR
            //Typeface urName = ResourcesCompat.getFont(getApplicationContext(), R.font.myriadpro_bold);
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
            // Title Order Details...
// Adding Title....

            Font mOrderDetailsTitleFont = new Font(urName, 36.0f, Font.NORMAL, BaseColor.BLACK);
// Creating Chunk
            Chunk mOrderDetailsTitleChunk = new Chunk("Order Details", mOrderDetailsTitleFont);
// Creating Paragraph to add...
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
// Setting Alignment for Heading
            mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
// Finally Adding that Chunk
            document.add(mOrderDetailsTitleParagraph);
            // Fields of Order Details...
// Adding Chunks for Title and value
            Font mOrderIdFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderIdChunk = new Chunk("Order No:", mOrderIdFont);
            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderIdParagraph);
            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));
            document.close();
            Toast.makeText(getApplicationContext(),"Exported Sucessfully !",Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex){
            Toast.makeText(getApplicationContext(),"Error while exporting "+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf(String sometext){
        // create a new document
        PdfDocument document = new PdfDocument();
        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawCircle(50, 50, 30, paint);
        paint.setColor(Color.BLACK);
        canvas.drawText(sometext, 80, 50, paint);
        canvas.drawText(sometext,80,50,paint);
        canvas.drawText(sometext,80,50,paint);
        canvas.drawText(sometext,80,50,paint);
        //canvas.drawt
        // finish the page
        document.finishPage(page);
// draw text on the graphics object of the page
        // Create Page 2
        pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 2).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawCircle(100, 100, 100, paint);
        document.finishPage(page);
        // write the document content
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/mypdf/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String targetPdf = directory_path+"test-2.pdf";
        Log.e("Path",""+targetPdf);
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("main", "error "+e.toString());
            Toast.makeText(this, "Something wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();
    }
    public void sendUsingWhatsapp()
    {
        try
        {
            String shareBody="*"+txtname.getText().toString()+"* - "+orderdate;
            shareBody = shareBody+"\nOrder Details from *"+username+"*";
            shareBody=shareBody+"\n*Order Details :*\n";
            for(int i=0;i<orderdet.size();i++)
            {
                Vector data=(Vector)orderdet.elementAt(i);
                shareBody=shareBody+"\n*("+data.elementAt(1)+") "+data.elementAt(9)+"*";
                double rate=0,discount=0,total=0;
                try
                {
                    rate=Double.parseDouble(""+data.elementAt(2));
                    discount=Double.parseDouble("0"+data.elementAt(6));
                    total=rate+(discount);
                }
                catch (Exception ex){
                    rate=Double.parseDouble(""+data.elementAt(2));
                    discount=Double.parseDouble(""+data.elementAt(6));
                    total=rate+(discount);
                }
                shareBody=shareBody+" Rate="+roundTwo(total);
                shareBody=shareBody+" Qty="+data.elementAt(3);
                shareBody=shareBody+" Amount="+roundTwo(Double.parseDouble(""+data.elementAt(4)));
            }
            shareBody=shareBody+"\n\nBasic Amount ="+subtotal.getText();
            shareBody=shareBody+"\nDiscount ="+etdiscount.getText();
            shareBody=shareBody+"\nFreight ="+etfreight.getText();
            shareBody=shareBody+"\nGST ="+txtgst.getText();
            shareBody=shareBody+"\n*Total Amount ="+txttotal.getText()+"*";
            shareBody=shareBody+"\nDispach Mode ="+spdispatch.getText().toString();
            shareBody=shareBody+"\nRemark ="+etremark.getText();
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Order Details");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent,"Order Details"));
        }
        catch (Exception ex){Toast.makeText(getApplicationContext(),"Error while sending"+ex.getMessage(),Toast.LENGTH_SHORT).show();}
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createPDFInvoice(String str)
    {
        Document doc = new Document();


        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PdfData";

            File dir = new File(path);
            if(!dir.exists())
                dir.mkdirs();

            SimpleDateFormat Ins_Date = new SimpleDateFormat("d-MM-yyyy");
            String InsDate = Ins_Date.format(Calendar.getInstance().getTime());
            filename=""+txtname.getText()+" "+InsDate+".pdf";
            filename=filename.replace("/","");
            filename=filename.replace("\"","");
            filename=filename.replace(":","");
            filename=filename.replace("*","");
            filename=filename.replace("?","");
            filename=filename.replace("<","");
            filename=filename.replace(">","");
            filename=filename.replace("|","");


            File file = new File(dir, filename);
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.trueview);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , stream);
            Image myImg = Image.getInstance(stream.toByteArray());
            myImg.setAlignment(Image.MIDDLE);

            //add image to document
            // doc.add(myImg);
            addTitlePage(doc);


        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        }
        catch (Exception ex){Log.e("Error",""+ex.getMessage());}
        finally
        {
            doc.close();
        }

    }
    private class LoadCustomerDetails extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(ViewApprovedOrder.this);


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
                //                Log.e("Bill Number 2 "," "+billnumber);
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                nameValuePairs.add(new BasicNameValuePair("customer", ""+order.elementAt(0)));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectcurrentcustomer.php");
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
                    Log.i("Current Order -",""+result);

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
                Log.i("Result 1 :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;
                    customer=new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        Log.e("Comming ","Sucess "+i);
                        jsonObject = jsonArray.getJSONObject(i);
                        //  login.addElement(json_data.getString("ID"));

                        customer.add(jsonObject.getString("name"));
                        customer.add(jsonObject.getString("salesman"));
                        customer.add(jsonObject.getString("group_1"));
                        customer.add(jsonObject.getString("group_2"));
                        customer.add(jsonObject.getString("contact_no"));
                        customer.add(jsonObject.getString("opening_balance"));
                        customer.add(jsonObject.getString("closing_balance"));
                        customer.add(jsonObject.getString("price_level"));
                        customer.add(jsonObject.getString("address_1"));
                        customer.add(jsonObject.getString("address_2"));
                        customer.add(jsonObject.getString("address_3"));
                        customer.add(jsonObject.getString("address_4"));
                        customer.add(jsonObject.getString("address_5"));
                        customer.add(jsonObject.getString("party_gstin"));
                        customer.add(jsonObject.getString("state_name"));
                        customer.add(jsonObject.getString("pin_code"));

                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No Any Order Found"+e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addTitlePage(Document document) throws DocumentException
    {
// Font Style for Document
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD
                | Font.UNDERLINE, BaseColor.GRAY);
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

// Start New Paragraph
        Paragraph prHead = new Paragraph();
// Set Font in this Paragraph
        prHead.setFont(titleFont);
// Add item into Paragraph
        prHead.add("Warner Electronic (I) Pvt. Ltd."+"\n");

// Create Table into Document with 1 Row


        prHead.setFont(catFont);
        // prHead.add("\n"+txtname.getText()+"\n\n");
        // prHead.add("\n Proforma Invoice \n\n");
        prHead.setAlignment(Element.ALIGN_CENTER);



        document.add(prHead);

        Paragraph orddet = new Paragraph();
        orddet.setAlignment(Element.ALIGN_CENTER);
        orddet.add("\n Unit No. A-27, Ground Floor,Giriraj Industrial Estate,Mahakali Caves Road,\nAndheri East,Mumbai - 400093\nGSTIN/UIN : 27AABCL9688B1Z8.");
        orddet.setFont(smallBold);
        document.add(orddet);
        orddet=new Paragraph();
        orddet.setFont(smallBold);
        orddet.setAlignment(Element.ALIGN_RIGHT);
        orddet.add("\nOrder Date :  "+order.elementAt(2));
        orddet.add("\nOrder From  :  "+username);
        document.add(orddet);
        orddet = new Paragraph();
        orddet.setAlignment(Element.ALIGN_CENTER);
        orddet.add("\nProforma Invoice");
        document.add(orddet);
        orddet = new Paragraph();
        orddet.add("\nInvoice To,");
        String value="";
        customer.removeIf(Objects::isNull);
        try {
            value=customer.get(0);
            if(value.equals("null")||value.equals("NULL")||value.trim().equals(""))
            {
                //Log.i("0","is null");
            }
            else
            {
                orddet.add("\n" + customer.get(0));
            }
        }
        catch (Exception ex){Log.e("Location ","0");}
        try
        {
            value=customer.get(8);
            if(value.equals("null")||value.equals("NULL")||value.trim().equals(""))
            {
                //Log.i("0","is null");
            }
            else
            {
                orddet.add("\n" + customer.get(8));
            }
        }
        catch (Exception ex){Log.e("Location ","8");}
        try
        {
            value=customer.get(9);
            if(value.equals("null")||value.equals("NULL")||value.trim().equals(""))
            {
                //Log.i("0","is null");
            }
            else
            {
                orddet.add("\n" + customer.get(9));
            }
        }
        catch (Exception ex){Log.e("Location ","9");}
        try
        {
            value=customer.get(10);
            if(value.equals("null")||value.equals("NULL")||value.trim().equals(""))
            {
                //Log.i("0","is null");
            }
            else
            {
                orddet.add("\n" + customer.get(10));
            }
        }
        catch (Exception ex){Log.e("Location ","10");}
        try
        {
            value=customer.get(11);
            if(value.equals("null")||value.equals("NULL")||value.trim().equals(""))
            {
                //Log.i("0","is null");
            }
            else
            {
                orddet.add("\n" + customer.get(11));
            }
        }
        catch (Exception ex){Log.e("Location ","11");}
        try
        {
            value=customer.get(12);
            if(value.equals("null")||value.equals("NULL")||value.trim().equals(""))
            {
                //Log.i("0","is null");
            }
            else
            {
                orddet.add("\n" + customer.get(12));
            }
        }
        catch (Exception ex){Log.e("Location ","12");}
        try
        {
            value=customer.get(13);
            if(value.equals("null")||value.equals("NULL")||value.trim().equals(""))
            {
                //Log.i("0","is null");
            }
            else
            {
                orddet.add("\n" + customer.get(13));
            }
        }
        catch (Exception ex){Log.e("Location ","13");}
        try
        {
            value=customer.get(4);
            if(value.equals("null")||value.equals("NULL")||value.trim().equals(""))
            {
                //Log.i("0","is null");
            }
            else
            {
                orddet.add("\n" + customer.get(4));
            }
        }
        catch (Exception ex){Log.e("Location ","4");}
        try
        {
            value=customer.get(15);
            if(value.equals("null")||value.equals("NULL")||value.trim().equals(""))
            {
                //Log.i("0","is null");
            }
            else
            {
                orddet.add(" Pin Code - " + customer.get(15));
            }
        }
        catch (Exception ex){Log.e("Location ","15");}
        document.add(orddet);
        orddet=new Paragraph();
        orddet.setFont(normal);
        orddet.add("\nOrder Details\n\n");
        orddet.setFont(smallBold);
        document.add(orddet);


        //document.add(prPersinalInfo);
        //document.add(myTable);
        //document.add(myTable);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100.0f);
        table.setWidths(new int[]{1,4, 1, 1,2,2});
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(""));
        cell.setRowspan(6);
        cell.setBorder(Rectangle.BOX);
        table.addCell("Sr.No");
        table.addCell("Product Name");
        table.addCell("Code");
        table.addCell("Qty.");
        table.addCell("Rate");
        table.addCell("Total");
        int totqty=0;
        for(int i=0;i<orderdet.size();i++) {
            Vector data=(Vector)orderdet.elementAt(i);
            // cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(""+(i+1));
            table.addCell("( "+ data.elementAt(1)+" ) \n"+data.elementAt(9));
            table.addCell(""+data.elementAt(1));
            table.addCell(""+data.elementAt(3));
            try {
                totqty = totqty + Integer.parseInt("" + data.elementAt(3));
            }  catch (Exception ex){}
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            try
            {
                table.addCell("" + roundTwo(Double.parseDouble("" + data.elementAt(2)) + (Double.parseDouble("" + data.elementAt(6)))));
            }
            catch (Exception ex)
            {
                table.addCell("" + roundTwo(Double.parseDouble("" + data.elementAt(2))));
            }
            table.addCell(""+roundTwo(Double.parseDouble(""+data.elementAt(4))));

        }
        try {
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(""+totqty);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.BOX);
            table.addCell("Basic Amount ");
            table.addCell("" + subtotal.getText());
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            cell.setBorder(Rectangle.BOX);
            table.addCell("Discount");
            try {
                table.addCell("- " + roundTwo(Double.parseDouble("" + etdiscount.getText())));
            } catch (Exception ex) {
                table.addCell("- " + etdiscount.getText());
            }
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            cell.setBorder(Rectangle.BOX);
            table.addCell("Freight");
            try {
                table.addCell("" + roundTwo(Double.parseDouble("" + etfreight.getText())));
            } catch (Exception ex) {
                table.addCell("" + etfreight.getText());
            }
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            cell.setBorder(Rectangle.BOX);
            table.addCell("GST");
            table.addCell("" + txtgst.getText());
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            cell.setBorder(Rectangle.BOX);
            table.addCell("Total Amount ");
            table.addCell("" + txttotal.getText());
            document.add(table);

            Paragraph prPersinalInfo = new Paragraph();
            prPersinalInfo.setFont(smallBold);
            prPersinalInfo.add("Dispach Mode : " + spdispatch.getText());
            prPersinalInfo.add("\n");
            prPersinalInfo.add("Remark : " + etremark.getText());
            document.add(prPersinalInfo);

            Paragraph parabot = new Paragraph();
            parabot.setAlignment(Element.ALIGN_LEFT);
            parabot.add("\nCompany's Bank Details :");
            document.add(parabot);

            parabot=new Paragraph();
            parabot.setAlignment(Element.ALIGN_RIGHT);
            parabot.add(""+getResources().getString(R.string.Bank_Acc_Name) +":\n");
            document.add(parabot);

            parabot=new Paragraph();
            parabot.setAlignment(Element.ALIGN_LEFT);
            parabot.add("" +getResources().getString(R.string.Bank_Name)+ " :\n");
            parabot.add("A/c No. :"+getResources().getString(R.string.Account_No)+ "\n");
            parabot.add("IFS Code : "+getResources().getString(R.string.IFSC_Code));
            document.add(parabot);

            parabot=new Paragraph();
            parabot.setAlignment(Element.ALIGN_RIGHT);
            parabot.add("Authorised Signatory ");
            document.add(parabot);

            Paragraph paralast=new Paragraph();
            paralast.setAlignment(Element.ALIGN_LEFT);
            paralast.add("\nDeclaration :- We declare that this invoice shows the actual price of the goods described and that all perticulars are true and currect.");
            document.add(paralast);

            paralast=new Paragraph();
            paralast.setAlignment(Element.ALIGN_CENTER);
            paralast.add("\nSUBJECT TO AURANGABAD JURISDICTION.");
            document.add(paralast);
        }
        catch (Exception ex){Log.e("GST Error",""+ex.getMessage());}
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PdfData";
            File dir = new File(path);
            File file = new File(dir, filename);
            File outputFile = new File(path, filename);
            Uri uri = Uri.fromFile(file);
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Order Details.");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setType("application/pdf");
            startActivity(Intent.createChooser(shareIntent, "Share..."));
        }
        catch (Exception ex){Toast.makeText(getApplicationContext(),"Error "+ex.getMessage(),Toast.LENGTH_SHORT).show();}
        //Toast.makeText(getApplicationContext(),"File Exported Sucessfully",Toast.LENGTH_SHORT).show();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendUsingPDF()
    {
        try
        {
            createPDFInvoice("");
        }
        catch (Exception ex){Toast.makeText(getApplicationContext(),"Error while sending"+ex.getMessage(),Toast.LENGTH_SHORT).show();}
    }
}

