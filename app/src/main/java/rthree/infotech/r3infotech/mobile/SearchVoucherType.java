package rthree.infotech.r3infotech.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class SearchVoucherType extends AppCompatActivity {
    private ArrayAdapter adapter;
    String arrcustlist[];
    ListView lstcustomers;
    TextView txtresult;
    Model model = Model.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_voucher_type);
        lstcustomers = (ListView) findViewById(R.id.lst_customers);
        txtresult = (TextView) findViewById(R.id.txt_noresultmessage);
        try {
            arrcustlist = new String[10];
            arrcustlist[0] = "Anuron Sales";
            arrcustlist[1] = "SALE BTI";
            arrcustlist[2] = "SALE I MARK TV";
            arrcustlist[3] = "SALE REALMI";
            arrcustlist[4] = "Sales";
            arrcustlist[5] = "Sales - Crompton";
            arrcustlist[6] = "Sales - Lava";
            arrcustlist[7] = "Sales - Lava SP";
            arrcustlist[8] = "Sales -Mobile Plug";
            arrcustlist[9] = "Sales - Ottomate";
            arrcustlist[10] = "Sales Zebronics";
            model = Model.getInstance();
            adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, arrcustlist);
            lstcustomers.setAdapter(adapter);
            lstcustomers.setEmptyView(txtresult);
        } catch (Exception ex) {
            Log.e("Error 1", "" + ex.getLocalizedMessage());
        }

        try {

            lstcustomers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("Selected", "" + parent.getItemAtPosition(position).toString());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                    //Toast.makeText(getApplicationContext(),""+,Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ex) {
            Log.e("Search Cust Error", "" + ex.getLocalizedMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("Menu det", "" + menu.toString());
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_customer, menu);
        MenuItem menuItem = (MenuItem) menu.findItem(R.id.nav_searchcustomer);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setQueryHint("Search Customer");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}