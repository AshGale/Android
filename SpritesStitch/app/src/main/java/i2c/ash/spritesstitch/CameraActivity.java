package i2c.ash.spritesstitch;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ash on 04/12/2017.
 */

public class CameraActivity extends AppCompatActivity {

    Bundle extras;
    static final int REQUEST_IMAGES = 1;
    static final int REQUST_MULTI_URI = 2;
    static final int REQUEST_STITCH_RESULT = 3;
    ImageView imgV_selected;

    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imgV_selected = (ImageView) findViewById(R.id.imgV_selected);
        imgV_selected.setVisibility(View.VISIBLE);
        //Toast.makeText(getApplication(), "Testing", Toast.LENGTH_SHORT).show();

        extras = getIntent().getExtras();//gets from previous intent

        //new LongOperation().execute("");//todo this is a testing async task

        if (extras.getBoolean("newImg")) {
            //#TODo
            //be able to take pictues on the fly with the app
            //start the camera and send back the uri's in the same manner
        } else {

            Intent selectIntent = new Intent(Intent.ACTION_GET_CONTENT);
            selectIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            selectIntent.setType("image/*");
            startActivityForResult(selectIntent, REQUEST_IMAGES);
            //startActivityForResult(selectIntent, REQUST_MULTI_URI);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //check for result is OK and then check for your requestCode(1001) and then delete the file
        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case REQUEST_IMAGES:
                    try {
                        if (data.getData() != null) {
                            Toast.makeText(getApplication(), "Please select more than one picture", Toast.LENGTH_SHORT).show();
                            finish();
                            //this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                            //this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
                            //onBackPressed();

                        } else {
                            //Toast.makeText(getApplication(), "Processing images, this may take some time!", Toast.LENGTH_SHORT).show();
                            ClipData cd = data.getClipData();
                            int itemCount = cd.getItemCount();
                            int imageCount = 0;


                            ArrayList<Uri> uriArray = new ArrayList<>();

                            //loop though all the uri's returned and then save to the array
                            for (int i = 0; i < itemCount; i++) {
                                if (cd.getItemAt(i).getUri() != null) {
                                    Log.v("Log", "Get Clip:" + i);//Determine the size of the OutputFile

                                    //add the new image to the array to be added later
                                    uriArray.add(cd.getItemAt(i).getUri());
                                    imageCount++;//add and image to the counter to indicate how many images there was
                                }
                            }
                            extras.putInt("imageCount" ,imageCount);
                            extras.putParcelableArrayList("uriArray",uriArray);//ArrayList<Parcelable> uris = bundle.getParcelableArrayList(KEY_URIS); for (Parcelable p : uris) { Uri uri = (Uri) p; }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(extras.getInt("imageCount")%2 != 0 || data.getData() != null)//workaroud for processing activity opening
                    {
                        Toast.makeText(getApplication(), "Can't support odd numbers", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else {
                        Log.v("Log", "added uri's to the bundle. Now sending data to be processed");
                        Intent intentStitch = new Intent(this, ProcessImageActivity.class);
                        intentStitch.putExtras(extras);
                        startActivityForResult(intentStitch, REQUEST_STITCH_RESULT);//
                    }

                    break;
                case REQUEST_STITCH_RESULT:
                    //this result comes from cameraActivity and returns a URI

                    try {
                        Log.v("Log", "set the end result to the canvas");//set the end result to the canvas

                        try {
                            //data.getExtras().get
                            Uri uri = Uri.parse(data.getStringExtra("StitchResult"));
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);//done like this to get the full imae
                            imgV_selected.setImageBitmap(bitmap);//imgV_selected.setImageURI(uri);

                        } catch (Exception e) {
                            Toast.makeText(getApplication(), "Something went wrong with result Bundle ", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

    private class LongOperation extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < 100; i++) {
                //Thread.sleep(10);
                publishProgress(i);

            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            //TextView txt = (TextView) findViewById(R.id.output);
            //txt.setText("Executed"); // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.v("Log", "Progress: " + values[0]);//set the end result to the canvas
        }
    }

}
