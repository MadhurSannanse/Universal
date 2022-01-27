package rthree.infotech.r3infotech.mobile;

import static com.itextpdf.text.BaseColor.BLACK;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
/*import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;*/import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPHeaderCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

public class CashReceipt extends AppCompatActivity {
  private static final int REQUEST_READ_PHONE_STATE = 1;
  Button btn_process;
  private static final int INTERNET=1;
  AutoCompleteTextView party;
  String usertype,username,locationstr,paymenttype;
  Vector customer,refrences,addredref;
  EditText txt_amount,txt_chq_number,txt_recnumber;
  Vector receipt;
  int year_x,month_x,day_x;
  static final int DILOG_ID=0;
  double refamount=0;
  Model model;
  int receiptNumber=1;
  TextView txt_date,refdetails,billrefrence;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_cash_receipt);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    txt_amount=(EditText)findViewById(R.id.txt_amount);
   // txt_chq_number=(EditText)findViewById(R.id.txt_chqnumber);
    txt_recnumber=(EditText)findViewById(R.id.et_recnumber);
    btn_process=(Button)findViewById(R.id.btn_process);
    party=(AutoCompleteTextView)findViewById(R.id.atv_party);
    txt_date=(TextView)findViewById(R.id.et_recdate);
    refdetails=(TextView)findViewById(R.id.txt_refnodet);
    billrefrence=(TextView)findViewById(R.id.txt_refno);
    loadTodaysDate();
    customer=new Vector();
    try
    {
      Intent intent = getIntent();
      Bundle b = intent.getExtras();
      usertype = b.getString("UserType");
      username = b.getString("UserName");
      locationstr=b.getString("Location");
      paymenttype=b.getString("Payment_Type");
      receipt=new Vector();
      model=Model.getInstance();
      final Calendar cal=Calendar.getInstance();
      year_x=cal.get(Calendar.YEAR);
      month_x=cal.get(Calendar.MONTH);
      day_x=cal.get(Calendar.DAY_OF_MONTH);
      showDateDialog();
      if(isNetworkAvailable()) {
        new LoadReceiptNumber().execute("");
        new LoadPartyNames().execute("Party");
      }
      else
      {
        Toast.makeText(getApplicationContext(),"No Internet Connection.",Toast.LENGTH_SHORT).show();
      }
      billrefrence.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          try {//Log.i("Refrence amt",""+refamount);
            if (refamount < Double.parseDouble("" + txt_amount.getText())) {

              loadRefrenceNumber();
            } else {
              Toast.makeText(getApplicationContext(), "Possible Bill Refrences Are Added", Toast.LENGTH_SHORT).show();
            }
          }
          catch (Exception ex){Toast.makeText(getApplicationContext(), "Invalid Amount For Bill Refrence", Toast.LENGTH_SHORT).show();}
        }
      });
      party.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          refamount=0;
          addredref=new Vector();
          // new GetLastReceiptNumber().execute("");
          new LoadReferenceFromServer().execute("");
        }
      });
      btn_process.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          try {
            if (party.getText().toString().trim().equals("Select Party")) {
              Toast.makeText(getApplicationContext(), "Select Party Name For Order", Toast.LENGTH_SHORT).show();
            }
            if (party.getText().toString().trim().equals("Select Party")) {
              Toast.makeText(getApplicationContext(), "Select Party Name For Order", Toast.LENGTH_SHORT).show();
            }
            else if (txt_recnumber.getText().toString().trim().equals("")) {
              Toast.makeText(getApplicationContext(), "Invalid Receipt Number", Toast.LENGTH_SHORT).show();
            }else if (txt_amount.getText().toString().trim().equals("")) {
              Toast.makeText(getApplicationContext(), "Invalid Amount", Toast.LENGTH_SHORT).show();
            } else if (txt_date.getText().toString().trim().equals("Select Date")) {
              Toast.makeText(getApplicationContext(), "Select Receipt Date", Toast.LENGTH_SHORT).show();
            } else {
              boolean valid = false;
              for (int i = 0; i < customer.size(); i++) {
                if (customer.elementAt(i).toString().trim().equals(party.getText().toString().trim())) {
                  valid = true;
                  break;
                }
              }
              if (valid) {
                model=Model.getInstance();
                String partyname = party.getText().toString().trim();
                String amount = txt_amount.getText().toString().trim();
                //String chnumber = txt_chq_number.getText().toString().trim();
                SimpleDateFormat Ins_Date = new SimpleDateFormat("dd/MMM/yyyy hh:mm a");
                String InsDate = txt_date.getText().toString().trim();
                receipt = new Vector();
                receipt.addElement("" + partyname);
                receipt.addElement("" + InsDate);
                receipt.addElement("" + amount);
                receipt.addElement("" + "");
                receipt.addElement("" + locationstr);
                receipt.addElement("" + username);
                receipt.addElement("" + paymenttype);
                receipt.addElement("" + getTodaysDate());
                receipt.addElement(model.getReceiptRefNo());
                int i = 0;
                for (i = 0; i < addredref.size(); i++) {
                  Vector dt = (Vector) addredref.elementAt(i);
                  receipt.addElement(dt.elementAt(0));
                }
                for (int j = i; j < 5; j++) {
                  receipt.addElement("");
                }
                for (i = 0; i < addredref.size(); i++) {
                  Vector dt = (Vector) addredref.elementAt(i);
                  receipt.addElement(dt.elementAt(1));
                }
                for (int j = i; j < 5; j++) {
                  receipt.addElement("0");
                }
                receipt.addElement(txt_recnumber.getText().toString().trim());
                Log.e("Receipt Details", "" + addredref.toString());
                if (isNetworkAvailable()) {
                  new SaveReceipt().execute("Save");
                } else {
                  Toast.makeText(getApplicationContext(), "No Internet Connection.", Toast.LENGTH_SHORT).show();
                }
              } else {
                Toast.makeText(getApplicationContext(), "Invalid Party Name !", Toast.LENGTH_SHORT).show();
              }
            }
          } catch (Exception ex) {

          }
        }
      });
    }
    catch (Exception ex)
    {
      ////////Log.e("Error ","Main "+ex.getLocalizedMessage());
    }

  }
  @Override
  public  boolean onOptionsItemSelected(MenuItem item)
  {
    int id=item.getItemId();
    if(id == android.R.id.home)
    {
      this.finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  @Override
  public void onBackPressed() {
    try {
      Intent intent = new Intent(getApplicationContext(), Home.class);
      intent.putExtra("UserType", "" + usertype);
      intent.putExtra("UserName", "" + username);
      //intent.putExtra("Location",""+locationstr);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
      finish();
      startActivity(intent);
    } catch (Exception ex) {
    }
  }
  public void showDateDialog()
  {
    try {
      txt_date.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          showDialog(DILOG_ID);
        }
      });
    }
    catch (Exception ex)
    {
      Toast.makeText(getApplicationContext(),"Error in show Dialog"+ex.getMessage(),Toast.LENGTH_SHORT).show();
    }
  }
  @Override
  protected Dialog onCreateDialog(int id)
  {
    try {
      if (id == DILOG_ID) {
        return new DatePickerDialog(this, dpickerlistener, year_x, month_x, day_x);
      }
      return null;
    }
    catch (Exception ex)
    {
      Toast.makeText(getApplicationContext(),"Error in show Dialog 1"+ex.getMessage(),Toast.LENGTH_SHORT).show();
      return null;
    }
  }
  protected DatePickerDialog.OnDateSetListener dpickerlistener=new DatePickerDialog.OnDateSetListener() {
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
      try
      {
        year_x = year;
        month_x = monthOfYear + 1;
        String day_x = ""+dayOfMonth;
        String monstr="";
        if(dayOfMonth<10)
        {
          day_x="0"+dayOfMonth;
        }
        if(month_x==1)
        {
          monstr="Jan";
        }
        if(month_x==2)
        {
          monstr="Feb";
        }
        if(month_x==3)
        {
          monstr="Mar";
        }
        if(month_x==4)
        {
          monstr="Apr";
        }
        if(month_x==5)
        {
          monstr="May";
        }
        if(month_x==6)
        {
          monstr="Jun";
        }
        if(month_x==7)
        {
          monstr="Jul";
        }
        if(month_x==8)
        {
          monstr="Aug";
        }
        if(month_x==9)
        {
          monstr="Sep";
        }
        if(month_x==10)
        {
          monstr="Oct";
        }
        if(month_x==11)
        {
          monstr="Nov";
        }
        if(month_x==12)
        {
          monstr="Dec";
        }

        txt_date.setText("" + day_x + "/" + monstr + "/" + year_x);
      }
      catch (Exception ex)
      {
        Toast.makeText(getApplicationContext(),"Error in show date"+ex.getMessage(),Toast.LENGTH_SHORT).show();
      }

    }
  };
  private class LoadPartyNames extends AsyncTask<String, Void, Void> {

    private final HttpClient Client = new DefaultHttpClient();
    private String Content;
    String result = "";
    private String Error = null;
    private ProgressDialog Dialog = new ProgressDialog(CashReceipt.this);


    protected void onPreExecute() {
      // NOTE: You can call UI Element here.

      //UI Element
      //   uiUpdate.setText("Output : ");
      Dialog.setMessage("Please Wait Loading Party Names ");
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


        //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
        //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
        try {

          HttpClient httpclient = new DefaultHttpClient();
          HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectucustomer.php");
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
          ArrayList<String> arrlst = new ArrayList<String>();
          // arrlst.add("Select Party");
          customer=new Vector();
          for (int i = 0; i < jsonArray.length(); i++)
          {
            jsonObject = jsonArray.getJSONObject(i);
            //  login.addElement(json_data.getString("ID"));
            customer.addElement(jsonObject.getString("name"));
            arrlst.add(jsonObject.getString("name"));
          }
          ArrayAdapter ard = new ArrayAdapter(getApplicationContext(), R.layout.activity_item__autocomplete,R.id.txt_name, arrlst);
          ard.setDropDownViewResource(android.R.layout.select_dialog_item);
          party.setAdapter(ard);



        } catch (Exception e) {
          ArrayList<String> arrlst = new ArrayList<String>();
          //arrlst.add("Select Party");
          ArrayAdapter ard = new ArrayAdapter(getApplicationContext(),R.layout.activity_item__autocomplete,R.id.txt_name, arrlst);
          ard.setDropDownViewResource(android.R.layout.select_dialog_item);
          party.setAdapter(ard);
          Log.e("Error",""+e.getMessage());
          Toast.makeText(getApplicationContext(), "No Any Party Found", Toast.LENGTH_LONG).show();
        }

      }
    }

  }

  private class SaveReceipt extends AsyncTask<String, Void, Void> {

    private final HttpClient Client = new DefaultHttpClient();
    private String Content;
    String result = "";
    private String Error = null;
    private ProgressDialog Dialog = new ProgressDialog(CashReceipt.this);


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
        if (android.os.Build.VERSION.SDK_INT > 9) {
          StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                  .permitAll().build();
          StrictMode.setThreadPolicy(policy);
        }
        // Call long running operations here (perform background computation)
        // NOTE: Don't call UI Element here.

        // Server url call by GET method
        Log.i("Check Order"," Checking");
        String request="";
        for (int i = 0; i < receipt.size(); i++) {
          if(""+receipt.elementAt(i)==""||""+receipt.elementAt(i)==null)
          {
            //m.add(i,"0");
          }
          request += receipt.elementAt(i).toString().trim() ;
          if(i==receipt.size()-1)
          {
            // continue;
          }
          else
          {
            request=request+"~~~";
          }

        }
        request=request+"^^^";
        Log.e("Order 2",""+request);
        InputStream is = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
        nameValuePairs.add(new BasicNameValuePair("details", request));


        //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
        //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
        try {

          HttpClient httpclient = new DefaultHttpClient();
          HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "insertreceipt.php");
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
          txt_amount.setText("");
          refdetails.setText("");
          party.setText("");
          txt_date.setText("Select Date");
          txt_recnumber.setText("");
          Toast.makeText(getApplicationContext(),"Receipt Saved Sucessfully !",Toast.LENGTH_SHORT).show();
          createPDFInvoice("");
        } catch (Exception e) {
          txt_amount.setText("");
          refdetails.setText("");
          party.setText("");
          txt_recnumber.setText("");
          txt_date.setText("Select Date");
          Toast.makeText(getApplicationContext(),"Receipt Saved Sucessfully !",Toast.LENGTH_SHORT).show();
          //Toast.makeText(getApplicationContext(), "No Any Found"+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
          createPDFInvoice("");
        }

      }
    }

  }
  private boolean isNetworkAvailable() {
    int permissionCheck = ContextCompat.checkSelfPermission(CashReceipt.this, Manifest.permission.INTERNET);

    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(CashReceipt.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
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
  private void loadTodaysDate()
  {
    try
    {
      Calendar c=Calendar.getInstance();
      SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
      String formattedDate = df.format(c.getTime());
      txt_date.setText(""+formattedDate);

    }
    catch (Exception ex){}
  }
  private String getTodaysDate()
  {
    try
    {
      Calendar c=Calendar.getInstance();
      SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
      String formattedDate = df.format(c.getTime());
      return formattedDate;

    }
    catch (Exception ex){return "";}
  }
  public void loadRefrenceNumber()
  {
    try
    {
      AlertDialog.Builder  builderSingle = new AlertDialog.Builder(CashReceipt.this);
      builderSingle.setIcon(R.drawable.ic_reclist);
      builderSingle.setTitle("Select As Refrences ");

      final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CashReceipt.this, android.R.layout.simple_list_item_multiple_choice);
      for(int i=0;i<refrences.size();i++)
      {
        Vector data=(Vector)refrences.elementAt(i);
        double amt=Double.parseDouble(""+data.elementAt(3));
        arrayAdapter.add(""+data.elementAt(0)+"   "+data.elementAt(1)+"\n      "+Math.abs(amt)+"\n");

      }

      builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
        }
      });

      builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          String strName = arrayAdapter.getItem(which);
          AlertDialog.Builder builderInner = new AlertDialog.Builder(CashReceipt.this);
          builderInner.setMessage(""+strName);
          Vector v=(Vector)refrences.elementAt(which);
          Vector tst=new Vector();
          double amt=Math.abs(Double.parseDouble(""+v.elementAt(3)));

          //  Log.i("Ref Amt - -  ",""+refamount);
          tst.addElement(v.elementAt(0));

          if((Double.parseDouble(""+txt_amount.getText())-refamount)<=amt)
          {
            // Log.i("TST -   "+amt,"IF "+(Double.parseDouble(""+txt_amount.getText())-refamount));
            tst.addElement(Double.parseDouble(""+txt_amount.getText())-refamount);
          }
          else
          {
            //  Log.i("TST -   "+amt,"Else "+(Double.parseDouble(""+txt_amount.getText())-refamount));
            tst.addElement(amt);
            refrences.removeElementAt(which);
          }
          refamount=refamount+amt;

          Log.i("Ref Amt - -   ",""+refamount);

          addredref.addElement(tst);
          refdetails.setText("");
          for(int i=0;i<addredref.size();i++)
          {
            Vector temp=(Vector)addredref.elementAt(i);
            refdetails.setText(refdetails.getText()+" |"+temp.elementAt(0));
          }
          builderInner.setTitle("Your Selected Refrence is");
          builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
              dialog.dismiss();
            }
          });
          builderInner.show();
        }
      });
      builderSingle.show();
    }
    catch (Exception ex){}
  }
  private class LoadReferenceFromServer extends AsyncTask<String, Void, Void> {

    private final HttpClient Client = new DefaultHttpClient();
    private String Content;
    String result = "";
    private String Error = null;
    private ProgressDialog Dialog = new ProgressDialog(CashReceipt.this);


    protected void onPreExecute() {
      // NOTE: You can call UI Element here.

      //UI Element
      //   uiUpdate.setText("Output : ");
      Dialog.setMessage("Please Wait Refrence List ");
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
        // Log.i("Party name",""+party.getText());
        nameValuePairs.add(new BasicNameValuePair("partyname",party.getText().toString().trim()));


        //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
        //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
        try {

          HttpClient httpclient = new DefaultHttpClient();
          HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectrefnumber.php");
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
          ArrayList<String> arrlst = new ArrayList<String>();
          // arrlst.add("Select Party");
          refrences=new Vector();
          for (int i = 0; i < jsonArray.length(); i++)
          {
            jsonObject = jsonArray.getJSONObject(i);
            //  login.addElement(json_data.getString("ID"));
            Vector data=new Vector();
            data.addElement(jsonObject.getString("Billno"));
            data.addElement(jsonObject.getString("Billdate"));
            data.addElement(jsonObject.getString("party"));
            data.addElement(jsonObject.getString("Amount"));
            data.addElement(jsonObject.getString("Salesman"));
            data.addElement(jsonObject.getString("Opening"));
            data.addElement(jsonObject.getString("Onaccount"));
            refrences.addElement(data);

          }




        } catch (Exception e) {
          Log.e("Ref errror",""+e.getMessage());
          Toast.makeText(getApplicationContext(), "No Any Refrence Found", Toast.LENGTH_LONG).show();
        }

      }
    }

  }
  public void createPDFInvoice(String str) {
    Document doc = new Document();
    // btnconfirm.setEnabled(true);


    try {

      int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE);
      if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_READ_PHONE_STATE);
      }
      permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
      if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_PHONE_STATE);
      }
      String path = "";
      File dir = null;

      String extStorageState = Environment.getExternalStorageState();
      if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
      }
      extStorageState = Environment.getExternalStorageState();
      if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
      }
      try {
        //Log.i("Version",""+android.os.Build.VERSION.SDK_INT);
        if (android.os.Build.VERSION.SDK_INT < 29) {
          path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PdfData";
          //Log.i("path 1", "" + path);
          dir = new File(path);
          if (!dir.exists())
            dir.mkdirs();
        } else {
          try {
            //path = Environment.getExternalStoragePublicDirectory(null).getAbsolutePath() + "/PdfData";
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/PdfData";
            //Log.i("path 2", "" + path);
            dir = new File(path);
            if (!dir.exists())
              dir.mkdirs();
          } catch (Exception ex) {
            Log.e("Version error", "" + ex.getLocalizedMessage());
          }
        }
      } catch (Exception ex) {

        path = Environment.getExternalStoragePublicDirectory(null).getAbsolutePath() + "/PdfData";
        //Log.i("path 2",""+path);
        dir = new File(path);
        if (!dir.exists())
          dir.mkdirs();
        //path = Environment.getExternalFilesDir().getAbsolutePath() + "/PdfData";
      }
      Log.d("PDFCreator", "PDF Path: " + path);
      SimpleDateFormat Ins_Date = new SimpleDateFormat("d-MM-yyyy ss");
      String InsDate = Ins_Date.format(Calendar.getInstance().getTime());
      String filename = "" + receipt.elementAt(0) + " " + InsDate + " R.pdf";
      Log.i("File Name", "" + filename);
      filename = filename.replace("/", "");
      filename = filename.replace("\"", "");
      filename = filename.replace(":", "");
      filename = filename.replace("*", "");
      filename = filename.replace("?", "");
      filename = filename.replace("<", "");
      filename = filename.replace(">", "");
      filename = filename.replace("|", "");

      File file = new File(dir, filename);
      FileOutputStream fOut = new FileOutputStream(file);

      PdfWriter.getInstance(doc, fOut);

      //open the document
      doc.open();

      //  ByteArrayOutputStream stream = new ByteArrayOutputStream();
      // Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_addtask);
      // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
      // Image myImg = Image.getInstance(stream.toByteArray());
      // myImg.setAlignment(Image.MIDDLE);

      //add image to document
      // doc.add(myImg);
      Log.i("Comming Hear", "YES " + Build.VERSION.SDK_INT);
      //  for (int i = 0; i < receipt.size(); i++) {
      addTitlePage(doc, 0);
      //Log.i("Comming Hear","YES 2 "+(i+1));
      // }
      Log.i("File", "" + file.toString());
      Log.i("Doc", "" + doc.toString());
      StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
      StrictMode.setVmPolicy(builder.build());
      path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PdfData";
      File outputFile = new File(path, filename);
      Uri uri = Uri.fromFile(file);

      //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
      Log.i("Version ", "30");
      if (Build.VERSION.SDK_INT >= 30) {
        // uri=Uri.parse(file.getPath());
        uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
      } else {
        uri = Uri.fromFile(file);
      }
      // Log.i("URI",""+uri.toString());

      try {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        // shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Receipt Details.");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("application/pdf");
        startActivity(Intent.createChooser(shareIntent, "Share..."));
      } catch (Exception ex) {
        Log.e("Sharing Eror", "" + ex.getLocalizedMessage());
      }

    } catch (DocumentException de) {
      Log.e("Error 1", "DocumentException:" + de);
    } catch (IOException e) {
      Log.e("Error 2", "ioException:" + e);
    } catch (Exception ex) {
      Log.e("Error 3", "" + ex.getMessage());
    } finally {
      doc.close();
    }

  }

  public void addTitlePage(Document document, int position) throws DocumentException {
// Font Style for Document
    if (position != 0) {
      document.newPage();
    }
    model=Model.getInstance();
    Vector invdata = receipt;
    //Log.i("Pos",""+invdata.toString());
    Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD
            | Font.UNDERLINE, BaseColor.GRAY);
    final Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD);
    Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

// Start New Paragraph
    Paragraph prHead = new Paragraph();
// Set Font in this Paragraph
    prHead.setFont(titleFont);
// Add item into Paragraph
    prHead.add("" + model.getCompanyName()+ "\n");

// Create Table into Document with 1 Row


    prHead.setFont(catFont);
    // prHead.add("\n"+txtname.getText()+"\n\n");
    // prHead.add("\n Proforma Invoice \n\n");
    prHead.setAlignment(Element.ALIGN_CENTER);


    document.add(prHead);

    Paragraph orddet = new Paragraph();
    orddet.setAlignment(Element.ALIGN_CENTER);
    orddet.add("\n " + model.getCompanyAddress());
    orddet.setFont(smallBold);
    document.add(orddet);
    orddet = new Paragraph();
    orddet.setFont(smallBold);


    document.add(orddet);
    orddet = new Paragraph();
    orddet.setFont(smallBold);
    orddet.setAlignment(Element.ALIGN_CENTER);
    orddet.add("\n\nReceipt Voucher");
    document.add(orddet);
    orddet = new Paragraph();
    orddet.setAlignment(Element.ALIGN_LEFT);
    //orddet.add("\nReceipt No. :  " + invdata.elementAt(0));
    document.add(orddet);
   /* orddet = new Paragraph();
    orddet.setAlignment(Element.ALIGN_RIGHT);
    orddet.add("Lic No. :  " + invdata.elementAt(1));
    document.add(orddet);*/
    orddet = new Paragraph();
    orddet.setAlignment(Element.ALIGN_LEFT);
    orddet.add("Receipt No.  :  " + invdata.elementAt(19).toString()+"\nThrough  :  " + invdata.elementAt(5));
    document.add(orddet);
    orddet = new Paragraph();
    orddet.setAlignment(Element.ALIGN_LEFT);
    orddet.add("\n");
    document.add(orddet);

    PdfPTable table = new PdfPTable(2);
    table.setWidthPercentage(100.0f);
    table.setWidths(new int[]{6, 3});
    PdfPCell cell, cell1;
    BaseFont bf = null;
    try {
      bf = BaseFont.createFont(
              BaseFont.TIMES_ROMAN,
              BaseFont.CP1252,
              BaseFont.EMBEDDED);

    } catch (Exception ex) {
    }
    Font font = new Font(bf, 14);
    Font ft;
    ft = new Font(bf, 14, Font.BOLD, BLACK);
    cell = new PdfPCell(new Phrase(""));
    cell1 = new PdfPCell(new Phrase(""));
    //cell.setRowspan(2);
    // cell.setBorder(Rectangle.BOX);
    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
    PdfPHeaderCell headerCell = new PdfPHeaderCell();
    table.getDefaultCell().setBorderWidth(2f);
    table.addCell("Particulars\n");
    table.addCell("  Amount\n");
    table.getDefaultCell().setBorderWidth(0f);
    // cell.setBorder(Rectangle.NO_BORDER);
    // cell.setHorizontalAlignment(Element.ALIGN_LEFT);
    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
    String sgref = "", agref = "", account = "";
       /* try {
            if (invdata.elementAt(6).toString().equals("Cash")) {
                account = "   Cash ";
            } else {
                account = "   Cheque No." + invdata.elementAt(7) + " Dated :" + invdata.elementAt(8);
            }
        } catch (Exception ex) {
            Log.e("Acc Err", "" + ex.getLocalizedMessage());
        }*/
    account = "   Cash ";
    try {
      agref = agref + "\n  " + account + "       " + invdata.elementAt(2) + " Cr";
    } catch (Exception ex) {
      Log.e("Ref error", "" + ex.getMessage());
    }
    sgref = "" + "\nAccount :\n\n       " + invdata.elementAt(0 )+ "\n";
    String ref="\n\nRefrence Added : ";
    try{
      if(!invdata.elementAt(14).toString().equals("0")){ref+=""+invdata.elementAt(9)+" : "+invdata.elementAt(14)+" Cr.\n";}}catch (Exception ex){}
    try{
      if(!invdata.elementAt(15).toString().equals("0")){ref+=""+invdata.elementAt(10)+" : "+invdata.elementAt(15)+" Cr.\n";}}catch (Exception ex){}
    try{
      if(!invdata.elementAt(16).toString().equals("0")){ref+=""+invdata.elementAt(11)+" : "+invdata.elementAt(16)+" Cr.\n";}}catch (Exception ex){}
    try{
      if(!invdata.elementAt(17).toString().equals("0")){ref+=""+invdata.elementAt(12)+" : "+invdata.elementAt(17)+" Cr.\n";}}catch (Exception ex){}
    try{
      if(!invdata.elementAt(18).toString().equals("0")){ref+=""+invdata.elementAt(13)+" : "+invdata.elementAt(18)+" Cr.\n";}}catch (Exception ex){}
    table.addCell(sgref +ref+ agref + "\n\n");

    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell("\n" + invdata.elementAt(2));

    try {
      //  cell.setBorder(Rectangle.NO_BORDER);
      table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
      String amount_word = EnglishNumberToWords.convert(Long.parseLong("" + invdata.elementAt(2)));
      table.addCell("\n\n\n\nAmount (in words)\n      " + amount_word + " INR Only.\n\n");
      table.addCell("");
      // cell.setBorder(Rectangle.BOX);
      table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell("");
      table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
      //cell.setBorder(Rectangle.BOX);
      table.getDefaultCell().setBorderWidth(2f);

      table.addCell("" + getResources().getString(R.string.Rs) + " " + invdata.elementAt(2));

      document.add(table);

      Paragraph parabot = new Paragraph();
      parabot.setAlignment(Element.ALIGN_LEFT);
      parabot.add("\n");
      document.add(parabot);

      parabot = new Paragraph();
      parabot.setAlignment(Element.ALIGN_RIGHT);
      parabot.add("\n");
      document.add(parabot);

      parabot = new Paragraph();
      parabot.setAlignment(Element.ALIGN_RIGHT);
      parabot.add("Authorised Signatory \n\n\n\n");
      document.add(parabot);
      parabot = new Paragraph();
      parabot.setAlignment(Element.ALIGN_CENTER);
      //  parabot.add("-----------------------------------------------------------------\n");
      // parabot.add("*****************************************************************\n");
      //parabot.add("-----------------------------------------------------------------\n\n\n\n");
      parabot.add("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
      document.add(parabot);

    } catch (Exception ex) {
      Log.e("Receipt Export Error", "" + ex.getMessage());
    }
    // Toast.makeText(getApplicationContext(),"File Exported Sucessfully",Toast.LENGTH_SHORT).show();
  }
  private class LoadReceiptNumber extends AsyncTask<String, Void, Void> {

    private final HttpClient Client = new DefaultHttpClient();
    private String Content;
    String result = "";
    private String Error = null;
    private ProgressDialog Dialog = new ProgressDialog(CashReceipt.this);


    protected void onPreExecute() {
      // NOTE: You can call UI Element here.

      //UI Element
      //   uiUpdate.setText("Output : ");
      Dialog.setMessage("Please Wait While Loading Receipt Number ");
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


        //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
        //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
        try {

          HttpClient httpclient = new DefaultHttpClient();
          HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectlastreceiptnumber.php");
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
          ArrayList<String> arrlst = new ArrayList<String>();
          // arrlst.add("Select Party");
          customer=new Vector();
          for (int i = 0; i < jsonArray.length(); i++)
          {
            jsonObject = jsonArray.getJSONObject(i);
            //  login.addElement(json_data.getString("ID"));
            receiptNumber=Integer.parseInt(jsonObject.getString("max"));
            receiptNumber++;
            txt_recnumber.setText(""+receiptNumber);

          }



        } catch (Exception e) {
          receiptNumber=1;
          txt_recnumber.setText(""+receiptNumber);
        }

      }
    }

  }
}
