package com.example.ash.i2c;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by Ash on 29/11/2017.
 */

public class OptionsActivity extends AppCompatActivity {

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        extras = getIntent().getExtras();//gets from previous intent

        Button btn_confirm = (Button)findViewById(R.id.btn_confirm);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Toast.makeText(getApplication(),"Just a moment!",Toast.LENGTH_SHORT).show();
                //thread start?

                EditText edit_imgCount = (EditText)findViewById(R.id.edit_imgCount);
                EditText edit_blockSize = (EditText)findViewById(R.id.edit_blockSize);
                CheckBox cb_width = (CheckBox)findViewById(R.id.cb_width);
                EditText edit_stitchRows = (EditText)findViewById(R.id.edit_stitchRows);
                EditText edit_borderWidth = (EditText)findViewById(R.id.edit_borderWidth);
                EditText edit_fileName = (EditText)findViewById(R.id.edit_fileName);

                extras.putInt("edit_imgCount",Integer.parseInt(edit_imgCount.getText().toString()));
                extras.putInt("edit_blockSize",Integer.parseInt(edit_blockSize.getText().toString()));
                extras.putInt("edit_stitchRows",Integer.parseInt(edit_stitchRows.getText().toString()));
                extras.putInt("edit_borderWidth",Integer.parseInt(edit_borderWidth.getText().toString()));
                extras.putInt("edit_fileName",Integer.parseInt(edit_fileName.getText().toString()));
                extras.putBoolean("cb_width",Boolean.parseBoolean(cb_width.getText().toString()));//check ToDo

                Intent aCamera = new Intent(v.getContext(), CameraActivity.class);//check ToDo
                aCamera.putExtras(extras);

                if(extras.getBoolean("newImg"))
                {
                    //new image
                    //this intent will launch an activity that will take the ammount of images specified
                    //then a new background intent will run to convert the image to mozaic and stitch them together
                    startActivity(aCamera);//,1);
                }
                else
                {
                    //existing image
                    //start and activity for a result that selects the selected amount of pictures

                }

                //Toast.makeText(getApplication(),"Here",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
