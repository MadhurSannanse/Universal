package rthree.infotech.r3infotech.mobile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
/*import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;*/import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class LedgerDetails extends AppCompatActivity {
    String partyname,username,billnumber,usertype;
    TextView txtamount,txtparty;
    private static final int REQUEST_READ_PHONE_STATE =1 ;
    private static final int INTERNET=1;
    ListView lstledger;
    double totalledger=0,open=0;
    ListView lstorderitems;
    Vector ledger;
    String Billno[],details[],total[],oid[];
    ArrayList <String> customer;
    private int WRITE_EXTERNAL_STORAGE_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtparty=(TextView) findViewById(R.id.txt_party);
        txtamount=(TextView)findViewById(R.id.txt_total);
        lstledger=(ListView)findViewById(R.id.lst_products);
        try
        {
            ledger=new Vector();
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            partyname = b.getString("Party");
            usertype="";
            try
            {
                open=Double.parseDouble(""+b.getString("Opening"));
            }
            catch (Exception ex){open=0;}
            new LoadledgertandingCount().execute("Load");
            new LoadCustomerDetails().execute("");
            lstledger.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    createPDFInvoice("");
                    return false;
                }
            });
            lstledger.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    createPDFInvoice("");
                }
            });
            txtparty.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    createPDFInvoice("");
                    return false;
                }
            });
            txtparty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createPDFInvoice("");
                }
            });
            txtamount.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    createPDFInvoice("");
                    return false;
                }
            });
            txtamount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createPDFInvoice("");
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
    private class LoadledgertandingCount extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(LedgerDetails.this);


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
                nameValuePairs.add(new BasicNameValuePair("party", partyname));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectledgerdetails.php");
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
                    Log.i("Current Order",""+result);

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
                Log.i("Result 2 :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;
                    ledger=new Vector();
                    Vector o;
                    ArrayList<String> arrlst = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        Log.e("Comming ","Sucess "+i);
                        jsonObject = jsonArray.getJSONObject(i);

                        //  login.addElement(json_data.getString("ID"));
                        o=new Vector();
                        o.addElement(jsonObject.getString("Doc_Type"));
                        o.addElement(jsonObject.getString("Doc_Number"));
                        String inputPattern = "yyyy-mm-dd";
                        String outputPattern = "dd-mm-yyyy";
                        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
                        Date date = null;
                        String str = "";
                        try {
                            date = inputFormat.parse(jsonObject.getString("Doc_Date"));
                            str = outputFormat.format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        o.addElement(str);
                        o.addElement(jsonObject.getString("Amount"));
                        o.addElement(jsonObject.getString("Party_Name"));
                        ledger.addElement(o);
                    }
                    loadledgerDetails();
                } catch (Exception e) {
                    Log.e("Error ledger",""+ e.getLocalizedMessage());
                    Toast.makeText(getApplicationContext(), "No Any Ledger Details Available"+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    private void loadledgerDetails()
    {
        try
        {
            Billno=new String[ledger.size()];
            details=new String[ledger.size()];
            total=new String[ledger.size()];
            oid=new String[ledger.size()];
            double tot=0,onacc=0,paidamount=0,bal=0;
            String overdue="";
            totalledger=0;
            for(int i=0;i<ledger.size();i++)
            {
                Vector data=(Vector)ledger.elementAt(i);
                try {
                    paidamount = Double.parseDouble(data.elementAt(5)
                            .toString().trim())
                            - Double.parseDouble(data.elementAt(3).toString()
                            .trim());
                }catch (Exception ex){paidamount=0;}
                overdue=new Database(getApplicationContext()).getDays(""+data.elementAt(1))+" Days";
                try {
                    bal = Double.parseDouble(""+data.elementAt(3));

                }catch (Exception ex){bal=0;}
                Billno[i]="Vch. Type : "+data.elementAt(0);
                details[i]="Bill No :"+data.elementAt(1).toString()+" | Date : "+data.elementAt(2);
                if(bal<0)
                {
                    total[i]="Dr  "+getResources().getString(R.string.Rs)+" "+Math.abs(bal);
                }
                else
                {
                    total[i]="Cr  "+getResources().getString(R.string.Rs)+" "+bal;
                }
                oid[i]=""+i;
                totalledger=totalledger+bal;
                txtparty.setText(""+data.elementAt(4));

            }
            Log.e("Opening ",""+open);
            totalledger=(totalledger)+(open);
            if(totalledger<0)
            {
                txtamount.setText("Dr  "+getResources().getString(R.string.Rs)+" "+Math.abs(totalledger));
            }
            else
            {
                txtamount.setText("Cr  "+getResources().getString(R.string.Rs)+" "+totalledger);
            }
            // txtamount.setText(""+getResources().getString(R.string.Rs)+" "+roundTwo((totalledger)));
            //txtamount.setText(""+getResources().getString(R.string.Rs)+" "+roundTwo((totalledger)));
            AdapterOutstanding adapter=new AdapterOutstanding(getApplicationContext(),Billno,details,total,oid);
            lstledger.setAdapter(adapter);
        }
        catch(Exception ex)
        {
            Log.e("Error ",""+ex.getLocalizedMessage());
            Toast.makeText(getApplicationContext(),"Fail to view Ledger ",Toast.LENGTH_SHORT).show();
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
    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(LedgerDetails.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LedgerDetails.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
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
    public void createPDFInvoice(String str)
    {
        Document doc = new Document();

        try {

            int permissionCheck = ContextCompat.checkSelfPermission(LedgerDetails.this, Manifest.permission.READ_PHONE_STATE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LedgerDetails.this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_READ_PHONE_STATE);
                //Log.i("1", "Phone State");

                // value=true;
            }
            else
            {
                //Log.i("Permission 1", "Granted");
            }

            permissionCheck = ContextCompat.checkSelfPermission(LedgerDetails.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LedgerDetails.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_PHONE_STATE);
                //Log.i("1", "Storage");

                // value=true;
            }
            else
            {
                //Log.i("Permission 2", "Granted");
            }
            String path="";
            File dir=null;

            String extStorageState = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
                //Log.i("Result","Read Only");
            }
            else
            {
                //Log.i("Result","Not Read Only");
            }

            extStorageState = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
                //Log.i("Result","Mounted");
            }
            else
            {
                //Log.i("Result","Not Mounted");
            }


            try {
                //Log.i("Version",""+android.os.Build.VERSION.SDK_INT);
                if(android.os.Build.VERSION.SDK_INT < 29) {
                    path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PdfData";
                    //Log.i("path 1", "" + path);
                    dir = new File(path);
                    if (!dir.exists())
                        dir.mkdirs();
                }
                else
                {
                    try {
                        //path = Environment.getExternalStoragePublicDirectory(null).getAbsolutePath() + "/PdfData";
                        path = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/PdfData";
                        //Log.i("path 2", "" + path);
                        dir = new File(path);
                        if (!dir.exists())
                            dir.mkdirs();
                    }catch (Exception ex){Log.e("Version error",""+ex.getLocalizedMessage());}
                }
            }
            catch (Exception ex)
            {

                path=Environment.getExternalStoragePublicDirectory(null).getAbsolutePath()+"/PdfData";
                //Log.i("path 2",""+path);
                dir = new File(path);
                if (!dir.exists())
                    dir.mkdirs();
                //path = Environment.getExternalFilesDir().getAbsolutePath() + "/PdfData";
            }
            // Log.d("PDFCreator", "PDF Path: " + path);
            SimpleDateFormat Ins_Date = new SimpleDateFormat("d-MM-yyyy");
            String InsDate = Ins_Date.format(Calendar.getInstance().getTime());
            String filename=""+partyname+" "+InsDate+".pdf";
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
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PdfData";
            File outputFile = new File(path, filename);
            Uri uri = Uri.fromFile(file);
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            // shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Order Details.");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setType("application/pdf");
            startActivity(Intent.createChooser(shareIntent, "Share..."));

        } catch (DocumentException de) {
            Toast.makeText(getApplicationContext(),"File Not Created",Toast.LENGTH_SHORT).show();
            Log.e("Error 1", "DocumentException:" + de);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"File Permission Denied",Toast.LENGTH_SHORT).show();
            Log.e("Error 2", "ioException:" + e);
        }
        catch (Exception ex){Log.e("Error 3",""+ex.getMessage());
            Toast.makeText(getApplicationContext(),"Error While Sending "+ex.getMessage(),Toast.LENGTH_SHORT).show();}
        finally
        {
            doc.close();
        }

    }
    public void addTitlePage(Document document) throws DocumentException
    {
// Font Style for Document
        Model model=Model.getInstance();
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
        prHead.add(""+model.getCompanyName()+"\n");

// Create Table into Document with 1 Row


        prHead.setFont(catFont);
        // prHead.add("\n"+txtname.getText()+"\n\n");
        // prHead.add("\n Proforma Invoice \n\n");
        prHead.setAlignment(Element.ALIGN_CENTER);



        document.add(prHead);

        Paragraph orddet = new Paragraph();
        orddet.setAlignment(Element.ALIGN_CENTER);
        orddet.add("\n"+model.getCompanyAddress());
        try {
            if (!model.getCompanyGSTIN().isEmpty()) {
                orddet.add("\n" + model.getCompanyGSTIN());
            }
        }catch (Exception ex){Log.e("GSTIN Error",""+ex.getLocalizedMessage());}
        orddet.setFont(smallBold);
        document.add(orddet);
        orddet=new Paragraph();
        orddet.setFont(smallBold);
        orddet.setAlignment(Element.ALIGN_RIGHT);
        orddet.add("\nDate :  1-4-2021");
        orddet.add("\nTo  :  31-3-2022");
        document.add(orddet);
        orddet = new Paragraph();
        orddet.setAlignment(Element.ALIGN_CENTER);
        orddet.add("\nLedger Details");
        document.add(orddet);
        orddet = new Paragraph();
        String value="";
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
        orddet.add("\nLedger Details\n\n");
        orddet.setFont(smallBold);
        document.add(orddet);


        //document.add(prPersinalInfo);
        //document.add(myTable);
        //document.add(myTable);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100.0f);
        table.setWidths(new int[]{2,1,2,2,2,2});
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(""));
        cell.setRowspan(6);
        cell.setBorder(Rectangle.BOX);
        table.addCell("Date");
        table.addCell("Perticulars");
        table.addCell("Vch Type");
        table.addCell("Vch No");
        table.addCell("Debit");
        table.addCell("Credit");
        double debit=0,credit=0,bal=0,closing=0;
        for(int i=0;i<ledger.size();i++) {
            bal=0;
            Vector data=(Vector)ledger.elementAt(i);
            // cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            try
            {
                bal=Double.parseDouble(""+data.elementAt(3));
                Log.i("Value amount",""+bal);
                if((bal<0))
                {
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(""+(data.elementAt(2)));
                    table.addCell("To" );
                    table.addCell(""+data.elementAt(0));
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(""+data.elementAt(1));
                    table.addCell("" + Math.abs(bal));
                    table.addCell("");
                    debit=debit+bal;
                }
                else
                {
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(""+(data.elementAt(2)));
                    table.addCell("By" );
                    table.addCell(""+data.elementAt(0));
                    table.addCell(""+data.elementAt(1));
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell("");
                    table.addCell("" + bal);
                    credit=credit+bal;
                }
            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),"Order Details Error In PDF",Toast.LENGTH_SHORT).show();
            }


        }

        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("Total");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell("" + Math.abs(debit));
        table.addCell("" + credit);
        try
        {
            double op=Double.parseDouble(customer.get(5));
            double cl=Double.parseDouble(customer.get(6));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("Opening ");
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            try
            {
                Log.i("Opening",""+op);
                if(op<0)
                {
                    debit=Math.abs(debit);
                    table.addCell(""+ Math.abs(op));
                    table.addCell("0");
                    closing=(credit+(op))-debit;

                }

                else {
                    debit=Math.abs(debit);

                    table.addCell("0" );
                    table.addCell("" + op);
                    closing=(debit+(op))-credit;


                }

            }
            catch (Exception ex)
            {
                debit=Math.abs(debit);
                table.addCell("0");
                table.addCell("0");
                closing=debit-credit;
            }
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("Closing Bal.");
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            if(cl<0)
            {
                table.addCell("" + Math.abs(roundTwo(cl)));
                table.addCell("0" );
            }
            else if(cl==0)
            {
                table.addCell("0");
                table.addCell("0");
            }
            else
            {
                table.addCell("0");
                table.addCell("" + roundTwo(cl));

            }

            document.add(table);

        }
        catch (Exception ex){}

        // Toast.makeText(getApplicationContext(),"File Exported Sucessfully",Toast.LENGTH_SHORT).show();
    }
    private class LoadCustomerDetails extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(LedgerDetails.this);


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
                nameValuePairs.add(new BasicNameValuePair("customer", ""+partyname));
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
                    Toast.makeText(getApplicationContext(), "Customer Details Not Found"+e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }
}
