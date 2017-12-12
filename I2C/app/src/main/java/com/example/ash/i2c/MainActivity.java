package com.example.ash.i2c;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    Intent options;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        options = new Intent(this, OptionsActivity.class);
        extras = new Bundle();

        Button btn_New = (Button)findViewById(R.id.btn_New);
        Button btn_exist = (Button)findViewById(R.id.btn_exist);
        Button btn_crop = (Button)findViewById(R.id.btn_crop);
        Button btn_album = (Button)findViewById(R.id.btn_album);


        btn_exist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                extras.putBoolean("newImg",false);
                options.putExtras(extras);
                startActivity(options);//,1);

                //Toast.makeText(getApplication(),"Here",Toast.LENGTH_SHORT).show();
            }
        });

        btn_New.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                extras.putBoolean("newImg",true);
                options.putExtras(extras);
                startActivity(options);//,1);

                //Toast.makeText(getApplication(),"Here",Toast.LENGTH_SHORT).show();
            }
        });

        btn_crop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //extras.putBoolean("newImg",false);
                //options.putExtras(extras);
                //startActivity(options);//,1);

                Toast.makeText(getApplication(),"ToDo_Crop",Toast.LENGTH_SHORT).show();
            }
        });

        btn_album.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //extras.putBoolean("newImg",true);
                //options.putExtras(extras);
                //startActivity(options);//,1);

                Toast.makeText(getApplication(),"ToDo_Album",Toast.LENGTH_SHORT).show();
            }
        });
    }


}
