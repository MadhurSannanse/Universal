package rthree.infotech.r3infotech.mobile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
/*import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;*/
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

@SuppressWarnings("ALL")
public class ItemDisplay extends AppCompatActivity {
    ProgressDialog progressBar;
    ServerDatabase mydb;
    String usertype,username,billnumber,proid,plevel,customername;
    private int progressBarStatus = 0;
    private static final int REQUEST_READ_PHONE_STATE =1 ;
    private static final int INTERNET=1;
    private Handler progressBarHandler = new Handler();
    AutoCompleteTextView spproduct;
    EditText rate,quantity,total,scheme,gst;
    Button save,view;
    Vector product,details;
    TextView proname;
    int gstrate;
    double basicamount=0,gstamount=0;
    boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spproduct=(AutoCompleteTextView) findViewById(R.id.select_product);
        rate=(EditText)findViewById(R.id.et_rate);
        quantity=(EditText)findViewById(R.id.et_quantity);
        total=(EditText)findViewById(R.id.et_total);
        save=(Button)findViewById(R.id.btn_additem);
        view=(Button)findViewById(R.id.btn_view);
        scheme=(EditText)findViewById(R.id.et_scheme);
        proname=(TextView)findViewById(R.id.productname);
        gst=(EditText)findViewById(R.id.et_gst);
        proid="";gstrate=0;
        product=new Vector();
        try
        {
            details=new Vector();
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            usertype = b.getString("UserType");
            username = b.getString("UserName");
            billnumber=b.getString("BillNumber");
            plevel=b.getString("Plevel");
            new LoadProducts().execute("Products");
        }
        catch (Exception ex){
            Log.e("Error 1",""+ex.getLocalizedMessage());}
        spproduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int k=0;
                for(int j=0;j<product.size();j++)
                {
                    Vector curproduct = (Vector) product.elementAt(j);
                    if((curproduct.elementAt(1).toString().equals(""+spproduct.getText().toString().trim()))||(curproduct.elementAt(1).toString()==""+spproduct.getText().toString().trim()))
                    {
                        String itemrate = "" + curproduct.elementAt(2);
                        double itemrate1=0;
                        try
                        {
                            itemrate1=Double.parseDouble("0"+itemrate);
                            itemrate1=roundTwo(itemrate1);
                            gstrate = Integer.parseInt(""+curproduct.elementAt(3));
                        }
                        catch (Exception ex){itemrate1=0;gstrate=0;}
                        proid = "" + curproduct.elementAt(0);
                        rate.setText("" + itemrate1);
                       // proname.setText("Total Stock :  "+curproduct.elementAt(5)+"  "+curproduct.elementAt(7));
                        quantity.setText("");
                        gst.setText("");
                        //proname.setText("");
                        scheme.setText("");
                        total.setText("");
                        k++;
                    }
                }
                if(k==0)
                {
                    Toast.makeText(getApplicationContext(),"Invalid Product Selection",Toast.LENGTH_SHORT).show();
                    rate.setText("");
                    quantity.setText("");
                    //proname.setText("");
                    proname.setText("");
                    scheme.setText("");
                    gst.setText("");
                    total.setText("");
                }

            }
        });
        spproduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int k=0;
                if(product.size()>0) {
                    if(position!=0) {
                        Vector curproduct = (Vector) product.elementAt((position - 1));
                        String itemrate = "" + curproduct.elementAt(2);
                        proid = "" + curproduct.elementAt(0);
                        rate.setText("" + itemrate);
                        quantity.setText("");
                        // proname.setText("");
                        scheme.setText("");
                        total.setText("");
                        gst.setText("");
                        proname.setText("Total Stock :  "+curproduct.elementAt(5)+"  "+curproduct.elementAt(7));
                        gstrate = Integer.parseInt(""+curproduct.elementAt(3));
                        k++;
                    }
                    else if(k==0)
                    {
                        Toast.makeText(getApplicationContext(),"Invalid Product Selection",Toast.LENGTH_SHORT).show();
                        rate.setText("");
                        quantity.setText("");
                        //proname.setText("Product Name");
                        scheme.setText("");
                        proname.setText("");
                        gst.setText("");
                        total.setText("");
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Invalid Selection",Toast.LENGTH_SHORT).show();
                        rate.setText("");
                        quantity.setText("");
                        scheme.setText("");
                        proname.setText("");
                        //proname.setText("Product Name");
                        gst.setText("");
                        total.setText("");
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Product Not Available", Toast.LENGTH_SHORT).show();
                    rate.setText("");
                    quantity.setText("");
                    proname.setText("");
                    scheme.setText("");
                    gst.setText("");
                    //proname.setText("Product Name");
                    total.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try
                {
                    if(!spproduct.getText().toString().trim().equals("")) {
                        double sch=0;
                        try
                        {
                            sch=Double.parseDouble("" + scheme.getText());
                        }
                        catch (Exception ex){sch=0;}
                        sch = (sch) * Double.parseDouble("0" + quantity.getText());
                        double totalval = (Double.parseDouble("0" + rate.getText()) * Double.parseDouble("0" + quantity.getText())) +(sch);
                        basicamount=totalval;
                        gstamount=(totalval*gstrate)/100;
                        gst.setText(""+roundfour(gstamount));
                        total.setText("" + roundfour(gstamount+totalval));
                    } }
                catch (Exception ex){}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        scheme.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try
                {
                    if(!spproduct.getText().toString().trim().equals("")) {
                        double sch=0;
                        try
                        {
                            sch=Double.parseDouble("" + scheme.getText());
                            if(sch>0)
                            {
                                scheme.setTextColor(getResources().getColor(R.color.Text_Color_Green));
                            }
                            else
                            {
                                scheme.setTextColor(getResources().getColor(R.color.red));
                            }
                        }
                        catch (Exception ex){sch=0;}
                        sch = (sch) * Double.parseDouble("0" + quantity.getText());
                        double totalval = (Double.parseDouble("0" + rate.getText()) * Double.parseDouble("0" + quantity.getText())) +(sch);
                        basicamount=totalval;
                        gstamount=(totalval*gstrate)/100;
                        gst.setText(""+roundfour(gstamount));
                        total.setText("" + roundfour(gstamount+totalval));
                    } }
                catch (Exception ex){}

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), Confirm_Order.class);
                i.putExtra("UserType",""+usertype);
                i.putExtra("UserName",""+username);
                i.putExtra("BillNumber",""+billnumber);
                i.putExtra("Plevel",""+plevel);
                startActivity(i);
            }
        });
        rate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try
                {
                    if(!spproduct.getText().toString().trim().equals("")) {
                        double sch=0;
                        try
                        {
                            sch=Double.parseDouble("" + scheme.getText());

                        }
                        catch (Exception ex){sch=0;}
                        sch = (sch) * Double.parseDouble("0" + quantity.getText());
                        double totalval = (Double.parseDouble("0" + rate.getText()) * Double.parseDouble("0" + quantity.getText())) +(sch);
                        basicamount=totalval;
                        gstamount=(totalval*gstrate)/100;
                        gst.setText(""+roundfour(gstamount));
                        total.setText("" + roundfour(gstamount+totalval));
                    } }
                catch (Exception ex){}

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String proname=spproduct.getText().toString().trim();
                String prorate=rate.getText().toString().trim();
                String proqty=quantity.getText().toString().trim();
                String prototal=total.getText().toString().trim();
                String sch=scheme.getText().toString().trim();
                String gstamt=gst.getText().toString().trim();
                if(spproduct.getText().toString()=="")
                {
                    Toast.makeText(getApplicationContext(),"Please Select Product",Toast.LENGTH_SHORT).show();
                }
                else if(!proqty.equals("")&&!proqty.equals("0")&&!proid.equals(""))
                {
                    details=new Vector();
                    details.addElement(billnumber);
                    details.addElement(proid);
                    details.addElement(plevel);
                    details.addElement(prorate);
                    details.addElement(proqty);
                    details.addElement(basicamount);
                    details.addElement("YES");
                    details.addElement(""+sch);
                    details.addElement(""+gstamt);
                    details.addElement(prototal);
                    // Log.e("Detais ",""+details.toString());
                    //insertOrderDetails(view,details);
                    new SaveOrderDetails().execute("Save");

                }
                else {
                    Toast.makeText(getApplicationContext(),"Invalid Product Or Quantity",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }
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
    public void loadProduct(View v)
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
                                            JSONArray jArray = new JSONArray(new JsonBilder().getAllProducts());
                                            ////Log.e("Data ",""+jArray.toString());
                                            JSONObject json_data = null;
                                            ////Log.e("Log ","Log 3");
                                            ArrayList<String> arrlst = new ArrayList<String>();
                                            for (int i = 0; i < jArray.length(); i++)
                                            {
                                                Vector data=new Vector();
                                                json_data = jArray.getJSONObject(i);
                                                //  login.addElement(json_data.getString("ID"));
                                                data.addElement(json_data.getString("ID"));
                                                data.addElement(json_data.getString("name"));
                                                data.addElement(json_data.getString("rate"));
                                                arrlst.add(json_data.getString("name"));
                                                product.addElement(data);
                                            }
                                            ArrayAdapter ard = new ArrayAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, arrlst);
                                            ard.setDropDownViewResource(android.R.layout.select_dialog_item);
                                            spproduct.setAdapter(ard);
                                            ////Log.e("Log ","Log 4");
                                            //Toast.makeText(getApplicationContext(),"Login Names Updated Sucessfully ", Toast.LENGTH_SHORT).show();

                                        } catch (Exception e) {
                                            //e.printStackTrace();
                                            Toast.makeText(getApplicationContext(),
                                                    "Customer Not Found ..!" ,
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                    catch(Exception ex)
                                    {
                                        Toast.makeText(getApplicationContext(), "Error "+ex.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception ex) {
                                    Toast.makeText(getBaseContext(), "Error " + ex.getMessage(), Toast.LENGTH_LONG).show();
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
    private void insertOrderDetails(View v, final Vector details) {
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
                        final boolean post = progressBarHandler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(progressBarStatus);
                                try {
                                    String st="";
                                    try {
                                        st = new JsonBilder().insertOrderDetails(details);
                                        spproduct.setText("");
                                        rate.setText("");
                                        quantity.setText("");
                                        gst.setText("");
                                        total.setText("");
                                        proname.setText("");
                                        // proname.setText("Product Name");
                                        scheme.setText("");

                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), "Failed To Add Order " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    if (st.equals("sucess")) {
                                        Toast.makeText(getBaseContext(), "Added Successfully", Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(),"Added Successfully ",Toast.LENGTH_LONG).show();
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
    public double roundfour(double value)
    {
        try
        {
            DecimalFormat twoDForm = new DecimalFormat("#.####");
            return Double.valueOf(twoDForm.format(value));
        }
        catch (Exception ex)
        {
            return 0;
        }
    }
    public double roundTwo(double value)
    {
        try
        {
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            return Double.valueOf(twoDForm.format(value));
        }
        catch (Exception ex)
        {
            return 0;
        }
    }
    private class LoadProducts extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(ItemDisplay.this);


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
                nameValuePairs.add(new BasicNameValuePair("username", username));
                nameValuePairs.add(new BasicNameValuePair("Plevel",plevel));


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectproduct.php");
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
                    ArrayList<String> arrlst = new ArrayList<String>();
                    arrlst.add("Select Product");
                    product=new Vector();
                    String productdet="",closing="",closing1="",unit="";
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        Vector data=new Vector();
                        productdet="";
                        jsonObject = jsonArray.getJSONObject(i);
                        //  login.addElement(json_data.getString("ID"));
                        data.addElement(jsonObject.getString("ID"));
                        data.addElement(jsonObject.getString("name"));
                        data.addElement(jsonObject.getString("rate"));
                        data.addElement(jsonObject.getString("gst_rate"));
                        data.addElement(jsonObject.getString("product_name"));
                        try
                        {
                            productdet=jsonObject.getString("name");
                            data.addElement(jsonObject.getString("closing_balance"));
                            closing=jsonObject.getString("closing_balance");
                        }
                        catch (Exception ex){Log.e("Error 12",""+ex.getMessage());}
                        try
                        {
                            data.addElement(jsonObject.getString("closing_balance_1"));
                            closing1=jsonObject.getString("closing_balance_1");
                        }
                        catch (Exception ex){Log.e("Error 123",""+ex.getMessage());}
                        try  {
                            data.addElement(jsonObject.getString("base_unit"));
                            unit=""+jsonObject.getString("base_unit");
                        }
                        catch (Exception ex){Log.e("Error 1234",""+ex.getMessage());unit="";}
                        //  productdet=productdet+"\n      Shendra "+closing+" Pcs / Mumbai "+closing1+" Pcs";
                        // Log.i("Product",""+productdet);
                        if(closing.equals("0"))
                        {
                            arrlst.add(productdet);
                        }
                        else
                        {
                            arrlst.add(productdet);
                        }
                        product.addElement(data);
                    }
                    ArrayAdapter ard = new ArrayAdapter(getApplicationContext(), R.layout.activity_item__autocomplete,R.id.txt_name, arrlst);
                    ard.setDropDownViewResource(android.R.layout.select_dialog_item);
                    spproduct.setAdapter(ard);
                } catch (Exception e) {
                    ArrayList<String> arrlst = new ArrayList<String>();
                    arrlst.add("Select Product");
                    ArrayAdapter ard = new ArrayAdapter(getApplicationContext(),R.layout.activity_item__autocomplete,R.id.txt_name, arrlst);
                    ard.setDropDownViewResource(android.R.layout.select_dialog_item);
                    spproduct.setAdapter(ard);
                    Toast.makeText(getApplicationContext(), "No Any PriceList Assigned\n For This Customer", Toast.LENGTH_LONG).show();
                }

            }
        }

    }

    private class SaveOrderDetails extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(ItemDisplay.this);


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
                String request="";
                for (int i = 0; i < details.size(); i++) {
                    if(""+details.elementAt(i)==""||""+details.elementAt(i)==null)
                    {
                        //m.add(i,"0");
                    }
                    request += details.elementAt(i).toString().trim() ;
                    if(i==details.size()-1)
                    {
                        // continue;
                    }
                    else
                    {
                        request=request+"~~~";
                    }

                }
                request=request+"^^^";
                Log.e("O Details",""+request);
                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                nameValuePairs.add(new BasicNameValuePair("details", request));


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "insertorderdetails.php");
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
                    spproduct.setText("");
                    proname.setText("");
                    rate.setText("");
                    quantity.setText("");
                    //  proname.setText("Product Name");
                    total.setText("");
                    scheme.setText("");
                    Toast.makeText(getApplicationContext(),"Added Sucessfully !",Toast.LENGTH_SHORT).show();



                } catch (Exception e) {
                    spproduct.setText("");
                    proname.setText("");
                    rate.setText("");
                    gst.setText("");
                    quantity.setText("");
                    // proname.setText("Product Name");
                    total.setText("");
                    scheme.setText("");
                    Toast.makeText(getApplicationContext(),"Added Sucessfully !",Toast.LENGTH_SHORT).show();
                }

            }
        }

    }
    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(ItemDisplay.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ItemDisplay.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
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
}
