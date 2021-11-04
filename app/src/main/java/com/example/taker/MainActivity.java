package com.example.taker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {

    Button btnCapture, btnSave;
    ImageView imgCard;

    private static final int REQUEST_PERM_WRITE_STORAGE = 102;
    private static final int CAPTURE_PHOTO = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCapture = findViewById(R.id.btnCapture);
        btnSave = findViewById(R.id.btnSave);
        imgCard = findViewById(R.id.imgCard);


        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                    }
                }

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERM_WRITE_STORAGE);
                } else {
                    takePicture();
                }

            }
        });

        SaveImage();

    }

    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_PHOTO);

    }


    public void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
        super.onActivityResult(requestCode, resultCode, returnIntent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case CAPTURE_PHOTO:

                    Bitmap bitmap = (Bitmap) returnIntent.getExtras().get("data");

                    imgCard.setImageBitmap(bitmap);
                    saveImageToGallery(bitmap);

                    break;
                default:
                    break;
            }
        }

    }


    private void saveImageToGallery(Bitmap finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Siemens";
        File myDir = new File(root + "/saveImage");
        myDir.mkdirs();
        String n = "Contagem";

        String imageName = "image-" + n + ".jpg";
        File file = new File(myDir, imageName);
        System.out.println(file.getAbsolutePath());
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            String resizeImage = file.getAbsolutePath();
            out.flush();
            out.close();

           new SendImageAPI().execute(file);

            Toast.makeText(MainActivity.this, "Sua foto foi salva!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(TakePicturesActivity.this, "Image captured", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setMessage("Imagem capturada com sucesso!");


            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();

        }


    }

    class SendImageAPI extends AsyncTask<File, Integer, JSONObject> {
        String TAG = getClass().getSimpleName();

        @Override
        protected JSONObject doInBackground(File... files) {
            Log.d(TAG + " DoINBackGround", "On doInBackground...");
            Conexao c = new Conexao();
            String response =  c.postDados("http://3.138.181.59:5000", files[0]);
            try {
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        }

        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            Intent Passar = new Intent(MainActivity.this, Resultado.class);
            try {
                Passar.putExtra("Total", String.valueOf(result.getInt("total")));
                Passar.putExtra("Image", result.getString("image"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            startActivity(Passar);
//
            Log.d(TAG + " onPostExecute", "" + result);
        }
    }


    public void SaveImage() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setMessage("Your picture has been saved.");


                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();

                Intent Passar = new Intent(MainActivity.this, Resultado.class);
                startActivity(Passar);
            }
        });



    }
}

