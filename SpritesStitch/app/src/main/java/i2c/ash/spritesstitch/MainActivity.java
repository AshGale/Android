package i2c.ash.spritesstitch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    Intent GetImages;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetImages = new Intent(this, CameraActivity.class);
        extras = new Bundle();

        Button btn_New = (Button)findViewById(R.id.btn_New);
        Button btn_exist = (Button)findViewById(R.id.btn_exist);
        Button btn_crop = (Button)findViewById(R.id.btn_crop);
        Button btn_album = (Button)findViewById(R.id.btn_album);

        //DisplayMetrics displayMetrics = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //int height = displayMetrics.heightPixels;
        //int width = displayMetrics.widthPixels;
        //Toast.makeText(getApplication(),height +" "+width,Toast.LENGTH_LONG).show();


        btn_exist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                extras.putBoolean("newImg",false);
                GetImages.putExtras(extras);
                startActivity(GetImages);//,1);

                //Toast.makeText(getApplication(),"Here",Toast.LENGTH_SHORT).show();
            }
        });

        btn_New.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //extras.putBoolean("newImg",true);
                //options.putExtras(extras);
                //startActivity(options);//,1);

                Toast.makeText(getApplication(),"ToDo_New",Toast.LENGTH_SHORT).show();
            }
        });

        btn_crop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //extras.putBoolean("crop",false);
                //options.putExtras(extras);
                //startActivity(options);//,1);

                Toast.makeText(getApplication(),"ToDo_Crop",Toast.LENGTH_SHORT).show();
            }
        });

        btn_album.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //extras.putBoolean("album",true);
                //options.putExtras(extras);
                //startActivity(options);//,1);

                Toast.makeText(getApplication(),"ToDo_Album",Toast.LENGTH_SHORT).show();
            }
        });
    }

}

