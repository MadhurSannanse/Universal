package rthree.infotech.r3infotech.mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/*import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;*/

public class HomeScreen extends AppCompatActivity {
    ImageButton neworder,vieworder,outstanding,salesreport,ledger,exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        neworder=(ImageButton)findViewById(R.id.btn_neworder);
        vieworder=(ImageButton)findViewById(R.id.btn_vieworder);
        outstanding=(ImageButton)findViewById(R.id.btn_outstanding);
        salesreport=(ImageButton)findViewById(R.id.btn_collectionreport);
        ledger=(ImageButton)findViewById(R.id.btn_ledger);
        exit=(ImageButton)findViewById(R.id.btn_exit);
        neworder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"New Order",Toast.LENGTH_SHORT).show();
            }
        });
        vieworder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"View Order",Toast.LENGTH_SHORT).show();
            }
        });

    }

}
