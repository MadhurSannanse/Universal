package rthree.infotech.r3infotech.mobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
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

public class StockReport extends AppCompatActivity {
    String productname,username,billnumber,usertype;
    TextView txtamount,txtproduct;
    private static final int REQUEST_READ_PHONE_STATE =1 ;
    private static final int INTERNET=1;
    ListView lstproduct;
    Vector AllProduct;
    AutoCompleteTextView atvproduct;
    String proname[],details[],total[],pid[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_report);
        atvproduct=(AutoCompleteTextView) findViewById(R.id.atv_product);
        lstproduct=(ListView)findViewById(R.id.lst_products);
        try
        {
            AllProduct=new Vector();
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            usertype = b.getString("UserType");
            username = b.getString("UserName");
            new LoadProducts().execute("");

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
    private class LoadProducts extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(StockReport.this);


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
                nameValuePairs.add(new BasicNameValuePair("Plevel","Primary"));


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
                    AllProduct=new Vector();
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
                        AllProduct.addElement(data);
                    }
                    ArrayAdapter ard = new ArrayAdapter(getApplicationContext(), R.layout.activity_item__autocomplete,R.id.txt_name, arrlst);
                    ard.setDropDownViewResource(android.R.layout.select_dialog_item);
                    atvproduct.setAdapter(ard);
                    loadOrderDetails();
                } catch (Exception e) {
                    ArrayList<String> arrlst = new ArrayList<String>();
                    arrlst.add("Select Product");
                    ArrayAdapter ard = new ArrayAdapter(getApplicationContext(),R.layout.activity_item__autocomplete,R.id.txt_name, arrlst);
                    ard.setDropDownViewResource(android.R.layout.select_dialog_item);
                    atvproduct.setAdapter(ard);
                    Toast.makeText(getApplicationContext(), "No Any PriceList Assigned\n For This Customer", Toast.LENGTH_LONG).show();
                    loadOrderDetails();
                }

            }
        }

    }
    private void loadOrderDetails()
    {
        try
        {
            proname=new String[AllProduct.size()];
            details=new String[AllProduct.size()];
            total=new String[AllProduct.size()];
            pid=new String[AllProduct.size()];
            String[] prodet=new String[AllProduct.size()];
            double bal=0;
            if(AllProduct.size()==0)
            {
                AdapterOutstanding adapter1=new AdapterOutstanding(getApplicationContext());
                lstproduct.setAdapter(adapter1);
                // Log.i("Comming","Yes");

            }

            for(int i=0;i<AllProduct.size();i++)
            {
                Vector data=new Vector();
                data=(Vector)AllProduct.elementAt(i);
                proname[i]=" "+data.elementAt(1);
                details[i]="   Total Stock :  "+data.elementAt(5)+" | "+data.elementAt(7);
                try
                {
                    double closing=Double.parseDouble(data.elementAt(5).toString());
                    double rate=Double.parseDouble(data.elementAt(2).toString());
                    double gst=Double.parseDouble(data.elementAt(3).toString());
                    double totalvalue=closing*rate;
                    total[i]=""+getResources().getString(R.string.Rs)+" "+roundTwo((totalvalue)+((totalvalue*gst)/100));
                }
                catch (Exception ex){
                    Log.e("Error",""+ex.getLocalizedMessage());
                    total[i]=" 0 ";}
                pid[i]=""+Integer.parseInt(""+data.elementAt(0).toString().trim());
                prodet[i]="";
            }
            AdapterOutstanding adapter=new AdapterOutstanding(getApplicationContext(),proname,details,total,pid);
            lstproduct.setAdapter(adapter);

        }
        catch(Exception ex)
        {
            Log.e("Error ",""+ex.getLocalizedMessage());
            //Toast.makeText(getApplicationContext()," Not Any Order Today ! ",Toast.LENGTH_SHORT).show();

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
}
