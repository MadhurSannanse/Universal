package rthree.infotech.r3infotech.mobile;

import android.os.AsyncTask;
//import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by USER on 10/01/2017.
 */

public class JsonBilder extends AsyncTask<URL,String,String> {
    // String HOSTNAME = "http://www.r3infoservices.com/Offline/demoall_2/";
    Model model=Model.getInstance();
    String HOSTNAME = ""+model.getUrl_address();
    String allproduct = "";
    String allorder = "";
    String allreceipt="";
    String allorder_det = "";
    // HostName host=new HostName();
    // String HOSTNAME=host.MyHost;
    @Override
    protected String doInBackground(URL... params) {
        InputStream is = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        // HOSTNAME=host.MyHost;
        nameValuePairs.add(new BasicNameValuePair("name", "s"));
        nameValuePairs.add(new BasicNameValuePair("order", "s"));
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(HOSTNAME + "selectCustomer.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            // Log.e("Customer", "Connection success ");
        } catch (Exception e) {
            // Log.e("Customer", "Connection Not success ");
            // Toast.makeText(getApplicationContext(), "Invalid IP Address",
            // Toast.LENGTH_LONG).show();
        }

        String result = "";
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

            Log.e("Customer ", "Download success ");
        } catch (Exception e) {
            Log.e("Customer ", "Download Not success ");

        }
        Log.e("Result ",""+result);
        return result;

    }



    public String getAllCustomer() {
        InputStream is = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        // nameValuePairs.add(new BasicNameValuePair("id", id));
        nameValuePairs.add(new BasicNameValuePair("name", "s"));
        nameValuePairs.add(new BasicNameValuePair("order", "s"));
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(HOSTNAME + "selectCustomer.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            // Log.e("Customer", "Connection success ");
        } catch (Exception e) {
            // Log.e("Customer", "Connection Not success ");
            // Toast.makeText(getApplicationContext(), "Invalid IP Address",
            // Toast.LENGTH_LONG).show();
        }

        String result = "";
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

            Log.e("Customer", "Download success ");
        } catch (Exception e) {
            Log.e("Customer", "Download Not success ");

        }

        return result;
    }

    public String insertLoginManagement(Vector m) {
        try {
            String request="";
            for (int i = 0; i < m.size(); i++) {
                if(""+m.elementAt(i)==""||""+m.elementAt(i)==null)
                {
                    //m.add(i,"0");
                }
                request += m.elementAt(i).toString().trim() ;
                if(i==m.size()-1)
                {
                    // continue;
                }
                else
                {
                    request=request+"~~~";
                }

            }
            request=request+"^^^";
            Log.e("Request",""+request);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("details", request));
            nameValuePairs.add(new BasicNameValuePair("order", "1"));
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(HOSTNAME + "insert_loginmanagement.php");
                // Log.e("Http",""+httppost.getURI());
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                // is = entity.getContent();
                Log.e("JsonBuilder 0", "connection success ");

            } catch (Exception e) {
                Log.e("Fail JsonBuilder1 ", e.toString());

            }
            return null;
        } catch (Exception e) {
            Log.e("Fail JsonBuilder2", e.toString());

            return null;
        }

    }
    public String getLoginmanagement(String imei) {
        InputStream is = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        // nameValuePairs.add(new BasicNameValuePair("id", id));

        nameValuePairs.add(new BasicNameValuePair("imei", imei));
        nameValuePairs.add(new BasicNameValuePair("order", "s"));
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(HOSTNAME + "selectloginman.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            // Log.e("Customer", "Connection success ");
        } catch (Exception e) {
            Log.e("Customer", "Connection Not success ");
            //Toast.makeText(getApplicationContext(), "Invalid IP Address",
            // Toast.LENGTH_LONG).show();
        }

        String result = "";
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

            Log.e("Customer", "Download success ");
        } catch (Exception e) {
            Log.e("Customer", "Download Not success ");

        }

        return result;
    }
    public String insertcustomer(Vector m) {
        try {
            String request="";
            for (int i = 0; i < m.size(); i++) {
                if(""+m.elementAt(i)==""||""+m.elementAt(i)==null)
                {
                    //m.add(i,"0");
                }
                request += m.elementAt(i).toString().trim() ;
                if(i==m.size()-1)
                {
                    // continue;
                }
                else
                {
                    request=request+"~~~";
                }

            }
            request=request+"^^^";
            Log.e("Request",""+request);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("details", request));
            nameValuePairs.add(new BasicNameValuePair("order", "1"));
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(HOSTNAME + "insert_customer.php");
                // Log.e("Http",""+httppost.getURI());
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                // is = entity.getContent();
                Log.e("JsonBuilder 0", "connection success ");

            } catch (Exception e) {
                Log.e("Fail JsonBuilder1 ", e.toString());

            }
            return null;
        } catch (Exception e) {
            Log.e("Fail JsonBuilder2", e.toString());

            return null;
        }

    }
    public String insertOrder(Vector m) {
        try {
            String request="";
            for (int i = 0; i < m.size(); i++) {
                if(""+m.elementAt(i)==""||""+m.elementAt(i)==null)
                {
                    //m.add(i,"0");
                }
                request += m.elementAt(i).toString().trim() ;
                if(i==m.size()-1)
                {
                    // continue;
                }
                else
                {
                    request=request+"~~~";
                }

            }
            request=request+"^^^";
            Log.e("Request",""+request);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("details", request));
            nameValuePairs.add(new BasicNameValuePair("order", "1"));
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(HOSTNAME + "insertorder.php");
                // Log.e("Http",""+httppost.getURI());
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                // is = entity.getContent();
                Log.e("JsonBuilder 0", "connection success ");

            } catch (Exception e) {
                Log.e("Fail JsonBuilder1 ", e.toString());

            }
            return null;
        } catch (Exception e) {
            Log.e("Fail JsonBuilder2", e.toString());

            return null;
        }

    }
    public String getLastBillNumber() {
        InputStream is = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        // nameValuePairs.add(new BasicNameValuePair("id", id));

        nameValuePairs.add(new BasicNameValuePair("order", "s"));
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(HOSTNAME + "selectlastbillnumber.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("Bill Number", "Connection success ");
        } catch (Exception e) {
            Log.e("Bill Number", "Connection Not success ");
            //Toast.makeText(getApplicationContext(), "Invalid IP Address",
            // Toast.LENGTH_LONG).show();
        }

        String result = "";
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

            Log.e("Customer", "Download success ");
        } catch (Exception e) {
            Log.e("Customer", "Download Not success ");

        }

        return result;
    }
    public String getCustomerNames(String username) {
        InputStream is = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        // nameValuePairs.add(new BasicNameValuePair("id", id));

        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("order", "s"));
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(HOSTNAME + "selectucustomer.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            // Log.e("Customer", "Connection success ");
        } catch (Exception e) {
            Log.e("Customer", "Connection Not success ");
            //Toast.makeText(getApplicationContext(), "Invalid IP Address",
            // Toast.LENGTH_LONG).show();
        }

        String result = "";
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

            Log.e("Customer", "Download success ");
        } catch (Exception e) {
            Log.e("Customer", "Download Not success ");

        }

        return result;
    }
    public String getAllDistributor() {
        InputStream is = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        // nameValuePairs.add(new BasicNameValuePair("id", id));

        nameValuePairs.add(new BasicNameValuePair("order", "s"));
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(HOSTNAME + "selectdistributor.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            // Log.e("Customer", "Connection success ");
        } catch (Exception e) {
            Log.e("Customer", "Connection Not success ");
            //Toast.makeText(getApplicationContext(), "Invalid IP Address",
            // Toast.LENGTH_LONG).show();
        }

        String result = "";
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

            Log.e("Customer", "Download success ");
        } catch (Exception e) {
            Log.e("Customer", "Download Not success ");

        }

        return result;
    }
    public String getAllProducts() {
        InputStream is = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        // nameValuePairs.add(new BasicNameValuePair("id", id));

        nameValuePairs.add(new BasicNameValuePair("order", "s"));
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(HOSTNAME + "selectproduct.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            // Log.e("Customer", "Connection success ");
        } catch (Exception e) {
            Log.e("Product", "Connection Not success ");
            //Toast.makeText(getApplicationContext(), "Invalid IP Address",
            // Toast.LENGTH_LONG).show();
        }

        String result = "";
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

            Log.e("Product", "Download success ");
        } catch (Exception e) {
            Log.e("Product", "Download Not success ");

        }

        return result;
    }
    public String insertOrderDetails(Vector m) {
        try {
            String request="";
            for (int i = 0; i < m.size(); i++) {
                if(""+m.elementAt(i)==""||""+m.elementAt(i)==null)
                {
                    //m.add(i,"0");
                }
                request += m.elementAt(i).toString().trim() ;
                if(i==m.size()-1)
                {
                    // continue;
                }
                else
                {
                    request=request+"~~~";
                }

            }
            request=request+"^^^";
            Log.e("Request",""+request);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("details", request));
            nameValuePairs.add(new BasicNameValuePair("order", "1"));
            //String is="";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(HOSTNAME + "insertorderdetails.php");
                // Log.e("Http",""+httppost.getURI());
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                //is =""+ entity.getContent();
                Log.e("Order Details 0", "connection success ");

            } catch (Exception e) {
                Log.e("Fail Order Details ", e.toString());

            }
            return "sucess";
        } catch (Exception e) {
            Log.e("Fail Order Details 2", e.toString());

            return null;
        }

    }
    public String getCurrentOrder(String ordernumber) {
        InputStream is = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        // nameValuePairs.add(new BasicNameValuePair("id", id));

        nameValuePairs.add(new BasicNameValuePair("ordernumber", ordernumber));
        nameValuePairs.add(new BasicNameValuePair("order", "s"));
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(HOSTNAME + "selectcurrentorder.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            // Log.e("Customer", "Connection success ");
        } catch (Exception e) {
            Log.e("Customer", "Connection Not success ");
            //Toast.makeText(getApplicationContext(), "Invalid IP Address",
            // Toast.LENGTH_LONG).show();
        }

        String result = "";
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

            Log.e("Order", "Download success ");
        } catch (Exception e) {
            Log.e("Order", "Download Not success ");

        }

        return result;
    }
    public String getCurrentOrderDet(String ordernumber) {
        InputStream is = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        // nameValuePairs.add(new BasicNameValuePair("id", id));

        nameValuePairs.add(new BasicNameValuePair("ordernumber", ordernumber));
        nameValuePairs.add(new BasicNameValuePair("order", "s"));
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(HOSTNAME + "selectcurrentorderdet.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            // Log.e("Customer", "Connection success ");
        } catch (Exception e) {
            Log.e("Customer", "Connection Not success ");
            //Toast.makeText(getApplicationContext(), "Invalid IP Address",
            // Toast.LENGTH_LONG).show();
        }

        String result = "";
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

            Log.e("Order", "Download success ");
        } catch (Exception e) {
            Log.e("Order", "Download Not success ");

        }

        return result;
    }

    public String getHostName() {
        return HOSTNAME;
    }
}
