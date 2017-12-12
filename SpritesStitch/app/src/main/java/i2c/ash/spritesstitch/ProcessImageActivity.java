package i2c.ash.spritesstitch;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ash on 04/12/2017.
 */

public class ProcessImageActivity extends AppCompatActivity {

    Intent aCamera;
    Bundle extras;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processimage);

        final Button btn_confirm = (Button)findViewById(R.id.btn_confirm);
        EditText edit_imgCount = (EditText)findViewById(R.id.edit_imgCount);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);

        extras = getIntent().getExtras();//gets from previous intent
        edit_imgCount.setText(extras.getInt("imageCount") +"");//this is set in CameraActivity
        edit_imgCount.setEnabled(false);


        btn_confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Toast.makeText(getApplication(), "Processing images, this may take some time!", Toast.LENGTH_LONG).show();//dose seem to show up here
                //thread start?
                btn_confirm.setEnabled(false);
                //EditText edit_imgCount = (EditText)findViewById(R.id.edit_imgCount);
                //todo add %option ie blocksize/height *100
                EditText edit_blockSize = (EditText)findViewById(R.id.edit_blockSize);
                CheckBox cb_width = (CheckBox)findViewById(R.id.cb_width);
                EditText edit_stitchRows = (EditText)findViewById(R.id.edit_stitchRows);
                EditText edit_borderWidth = (EditText)findViewById(R.id.edit_borderWidth);
                EditText edit_fileName = (EditText)findViewById(R.id.edit_fileName);

                //extras.putInt("edit_imgCount",Integer.parseInt(edit_imgCount.getText().toString()));
                extras.putInt("edit_blockSize",Integer.parseInt(edit_blockSize.getText().toString()));
                extras.putInt("edit_stitchRows",Integer.parseInt(edit_stitchRows.getText().toString()));
                extras.putInt("edit_borderWidth",Integer.parseInt(edit_borderWidth.getText().toString()));
                extras.putString("edit_fileName",(edit_fileName.getText().toString()));
                extras.putBoolean("cb_width",  cb_width.isChecked());


                aCamera = new Intent(v.getContext(), CameraActivity.class);//check ToDo

                aCamera.putExtras(extras);

                if(extras.getBoolean("newImg"))
                {
                    //new image
                    //this intent will launch an activity that will take the ammount of images specified
                    //then a new background intent will run to convert the image to mozaic and stitch them together

                }
                else
                {

                    //ToDo//check to see if the vales are illigal, eg stich rows <1

                    //Bitmap bitmap = processImages();
                    Toast.makeText(getApplication(), "Processing images, this may take some time!", Toast.LENGTH_LONG).show();
                    new processImagesThread().execute("");


                }

                //Toast.makeText(getApplication(),"Here",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public Bitmap processImages(){
        try {


            int imageCount = extras.getInt("imageCount") ;//todo set files to android:inputType="numberDecimal"

            //Todo//add thread
            //todo change image count from options to a counter from returned uri's for loop

            //Create array list for bitmaps
            ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();//ArrayList<ArrayList<Bitmap>> bitmap2d = new ArrayList<ArrayList<Bitmap>>();
            ArrayList<Parcelable> ArrayUris = extras.getParcelableArrayList("uriArray"); for (Parcelable i : ArrayUris) { Uri uri = (Uri) i; }


            //loop though all the uri's returned and then save to the array
            for (Parcelable i : ArrayUris) {
                int alt_blockSize = 0;
                if(bitmapArray.size() ==1)//workaround for images at different sizes scaling differantly
                {
                    alt_blockSize = bitmapArray.get(0).getHeight();
                }
                //resolve the bitmap at the URI
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), (Uri) i);
                //determine scale factor for uneven images
                Integer scale = (bitmap.getWidth() / extras.getInt("edit_blockSize"));
                //make new bitmap based on scaled details
                Bitmap scaledBitmap;// = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / scale, bitmap.getHeight() / scale, false);

                //Log.v("Log", "Scale  = width // blocksize: " + scale + " " + bitmap.getWidth() + " " + extras.getInt("edit_blockSize"));
                //Log.v("Log", "result" + scaledBitmap.getWidth() + " "+ scaledBitmap.getHeight());


                if(extras.getBoolean("cb_width",true))//to accout for error in conversion of scale//todo revise, causing
                {
                    //scaledBitmap.setWidth(extras.getInt("edit_blockSize"));//seems to warp the image if not even
                    if (alt_blockSize != 0) {
                        scaledBitmap = Bitmap.createScaledBitmap(bitmap, extras.getInt("edit_blockSize"), alt_blockSize, false);
                    }
                    else
                    {
                        scaledBitmap = Bitmap.createScaledBitmap(bitmap, extras.getInt("edit_blockSize"), bitmap.getHeight() / scale, false);
                    }
                }
                else                                    {

                    scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / scale, extras.getInt("edit_blockSize"), false);
                }

                //add the new image to the array to be added later
                bitmapArray.add(scaledBitmap);
                Log.v("Log", "Processing Image: " + bitmapArray.size());//Determine the size of the OutputFile

            }

            Log.v("Log", "Determine the size of the OutputFile");//Determine the size of the OutputFile
            int SpriteSheetx = 1;
            int SpriteSheety = 1;
            //todo there needs to be error handing here to make sure the app runs as expted

            int edit_blockSize = extras.getInt("edit_blockSize");
            int edit_blockSizeAlt = bitmapArray.get(0).getHeight();//todo not longterm solution
            int edit_stitchRows = extras.getInt("edit_stitchRows");
            int edit_borderWidth = extras.getInt("edit_borderWidth");

            Log.v("Log", "algorithm for how wide the image should be");//algorithm for how wide the image should be
            if (edit_borderWidth == 0) {
                Log.d("Here", "math: " + imageCount + "*" + edit_blockSize + " /" + edit_stitchRows, null);
                SpriteSheetx = (imageCount * edit_blockSize) / edit_stitchRows;
                SpriteSheety = edit_blockSizeAlt * edit_stitchRows;
                Log.d("Here", "math: x " + SpriteSheetx + " , y " + SpriteSheety, null);
            } else {
                SpriteSheetx = (imageCount * edit_blockSize) / edit_stitchRows + (edit_borderWidth * imageCount) / edit_stitchRows - edit_borderWidth;
                SpriteSheety = edit_blockSizeAlt * edit_stitchRows + (edit_borderWidth * edit_stitchRows) - edit_borderWidth;
            }

            Log.v("Log", "make new outputfile");//make new outputfile
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap SpriteSheet = Bitmap.createBitmap(SpriteSheetx, SpriteSheety, conf);

            int maxCol = imageCount / edit_stitchRows;
            int row = -1;//account for initila itteration//performance
            int columb = 0;
            Log.v("Log", "set images from array list to output file");//set images from array list to output file
            //void setPixels (int[] pixels,int offset,int stride,int SpriteSheetx,int SpriteSheety,int width,int height)
            for (int i = 0; i < bitmapArray.size(); i++) {
                //determine row and columb
                if (i % maxCol == 0) {
                    row++;
                }
                columb = i % maxCol;

                //convert bitmap to int array
                Bitmap bitmap = bitmapArray.get(i);
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int[] bitmapStore = new int[width * height];
                //store pixles into int array not offset in sorce or store

                bitmap.getPixels(bitmapStore, 0, width, 0, 0, width, height);

                int x = (columb == maxCol) ? width * columb : (width + edit_borderWidth) * columb;//determine if to include boder at edges x
                int y = (row == edit_stitchRows) ? height * row : (height + edit_borderWidth) * row;//determine if to include boder at edges y
                //set pixels to the oupt file
                Log.v("Log", "adding image: " + row +" " + columb + " starting at: " + x +"," +y+ "  : width : " +width + " height " + height);
                SpriteSheet.setPixels(bitmapStore, 0, width, x, y, width, height);
            }

            return SpriteSheet;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class processImagesThread extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {


            try {

                publishProgress(0);
                int imageCount = extras.getInt("imageCount") ;//todo set files to android:inputType="numberDecimal"
                float progress = 0;
                //Todo//add thread
                //todo change image count from options to a counter from returned uri's for loop

                //Create array list for bitmaps
                ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();//ArrayList<ArrayList<Bitmap>> bitmap2d = new ArrayList<ArrayList<Bitmap>>();
                ArrayList<Parcelable> ArrayUris = extras.getParcelableArrayList("uriArray"); for (Parcelable i : ArrayUris) { Uri uri = (Uri) i; }


                //loop though all the uri's returned and then save to the array
                for (Parcelable i : ArrayUris) {
                    int alt_blockSize = 0;
                    if(bitmapArray.size() ==1)//workaround for images at different sizes scaling differantly
                    {
                        alt_blockSize = bitmapArray.get(0).getHeight();
                    }
                    //resolve the bitmap at the URI
                    ContentResolver mContentResolver;
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), (Uri) i);
                    //determine scale factor for uneven images
                    Integer scale = (bitmap.getWidth() / extras.getInt("edit_blockSize"));
                    //make new bitmap based on scaled details
                    Bitmap scaledBitmap;// = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / scale, bitmap.getHeight() / scale, false);

                    //Log.v("Log", "Scale  = width // blocksize: " + scale + " " + bitmap.getWidth() + " " + extras.getInt("edit_blockSize"));
                    //Log.v("Log", "result" + scaledBitmap.getWidth() + " "+ scaledBitmap.getHeight());


                    if(extras.getBoolean("cb_width",true))//to accout for error in conversion of scale//todo revise, causing
                    {
                        //scaledBitmap.setWidth(extras.getInt("edit_blockSize"));//seems to warp the image if not even
                        if (alt_blockSize != 0) {
                            scaledBitmap = Bitmap.createScaledBitmap(bitmap, extras.getInt("edit_blockSize"), alt_blockSize, false);
                        }
                        else
                        {
                            scaledBitmap = Bitmap.createScaledBitmap(bitmap, extras.getInt("edit_blockSize"), bitmap.getHeight() / scale, false);
                        }
                    }
                    else                                    {

                        scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / scale, extras.getInt("edit_blockSize"), false);
                    }

                    progress = Float.valueOf(bitmapArray.size())/Float.valueOf(ArrayUris.size()) * 40;
                    publishProgress(Math.round(progress));
                    //add the new image to the array to be added later
                    bitmapArray.add(scaledBitmap);


                }

                publishProgress(40);
                Log.v("Log", "Determine the size of the OutputFile");//Determine the size of the OutputFile
                int SpriteSheetx = 1;
                int SpriteSheety = 1;
                //todo there needs to be error handing here to make sure the app runs as expted

                int edit_blockSize = extras.getInt("edit_blockSize");
                int edit_blockSizeAlt = bitmapArray.get(0).getHeight();//todo not longterm solution
                int edit_stitchRows = extras.getInt("edit_stitchRows");
                int edit_borderWidth = extras.getInt("edit_borderWidth");

                Log.v("Log", "algorithm for how wide the image should be");//algorithm for how wide the image should be
                if (edit_borderWidth == 0) {
                    Log.d("Log", "math: " + imageCount + "*" + edit_blockSize + " /" + edit_stitchRows, null);
                    if((imageCount%2)==0)//even
                    {

                    }
                    else
                    {

                    }
                    SpriteSheetx = (imageCount * edit_blockSize) / edit_stitchRows;
                    SpriteSheety = edit_blockSizeAlt * edit_stitchRows;
                    Log.d("Log", "math: x " + SpriteSheetx + " , y " + SpriteSheety, null);
                } else {
                    SpriteSheetx = (imageCount * edit_blockSize) / edit_stitchRows + (edit_borderWidth * imageCount) / edit_stitchRows - edit_borderWidth;
                    SpriteSheety = edit_blockSizeAlt * edit_stitchRows + (edit_borderWidth * edit_stitchRows) - edit_borderWidth;
                }

                Log.v("Log", "make new outputfile");//make new outputfile
                Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                Bitmap SpriteSheet = Bitmap.createBitmap(SpriteSheetx, SpriteSheety, conf);

                int maxCol = imageCount / edit_stitchRows;
                int row = -1;//account for initila itteration//performance
                int columb = 0;
                Log.v("Log", "set images from array list to output file");//set images from array list to output file
                //void setPixels (int[] pixels,int offset,int stride,int SpriteSheetx,int SpriteSheety,int width,int height)
                publishProgress(50);
                for (int i = 0; i < bitmapArray.size(); i++) {
                    //determine row and columb
                    if (i % maxCol == 0) {
                        row++;
                    }
                    columb = i % maxCol;

                    //convert bitmap to int array
                    Bitmap bitmap = bitmapArray.get(i);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int[] bitmapStore = new int[width * height];
                    //store pixles into int array not offset in sorce or store

                    bitmap.getPixels(bitmapStore, 0, width, 0, 0, width, height);

                    int x = (columb == maxCol) ? width * columb : (width + edit_borderWidth) * columb;//determine if to include boder at edges x
                    int y = (row == edit_stitchRows) ? height * row : (height + edit_borderWidth) * row;//determine if to include boder at edges y
                    //set pixels to the oupt file
                    Log.v("Log", "adding image: " + row +" " + columb + " starting at: " + x +"," +y+ "  : width : " +width + " height " + height);
                    SpriteSheet.setPixels(bitmapStore, 0, width, x, y, width, height);


                    progress = Float.valueOf(i)/Float.valueOf(bitmapArray.size()) * 40;
                    publishProgress(Math.round(progress+50));
                }

                return SpriteSheet;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            publishProgress(90);
            //save the image for future consumptin//if above went ok, then this will bo ok too
            Log.v("Log", "save the file returned");//save the file that was just created
            Log.v("Log", "make new outputfile");//make new outputfile

            Bitmap StitchResult = null;
            StitchResult = Bitmap.createBitmap(bitmap);

            File directoryResult = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/SpriteStitch");//todo make this a reusable string
            if (!directoryResult.exists()) {
                directoryResult.mkdir();
                Log.d("Here", "Made: " + directoryResult, null);
            }
            //make a file shell
            EditText edit_fileName = (EditText)findViewById(R.id.edit_fileName);
            File img_fileResult = new File(directoryResult, new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + "_" + edit_fileName.getText().toString() + ".jpg");//deafult

            Log.v("Log", "write the file out");//write the file out
            try {
                publishProgress(94);
                //Convert bitmap to byte array
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                StitchResult.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                Log.d("Here", "Here: " + img_fileResult.getAbsolutePath(), null);
                //write the bytes in file
                FileOutputStream fos = new FileOutputStream(img_fileResult);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            publishProgress(100);
            Toast.makeText(getApplication(), "Processing images compleated", Toast.LENGTH_SHORT).show();
            Log.d("Log", "Seding back to cameraActivity: " + Uri.fromFile(img_fileResult), null);
            Intent resultIntent = new Intent();
            Uri uri = Uri.fromFile(img_fileResult);
            resultIntent.putExtra("StitchResult", uri.toString());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();

        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.v("Log", "Progress: " + values[0]);
            progressBar.setProgress(values[0]);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        final Button btn_confirm = (Button)findViewById(R.id.btn_confirm);
        btn_confirm.setEnabled(true);
    }
}
