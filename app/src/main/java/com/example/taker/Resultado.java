package com.example.taker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Resultado extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);
        String total = (String) getIntent().getSerializableExtra("Total");
        String image = (String) getIntent().getSerializableExtra("Image");

        TextView txt = (TextView) findViewById(R.id.textView2);
        txt.setText(total);


        Log.d("URL IMAGE", "http://192.168.15.9:5000/images/outputs/" + image + ".png");
            new LoadImageAPI(findViewById(R.id.imageView3)).execute(image);


    }

    public void voltar(View view){
        Intent Passar = new Intent(Resultado.this, MainActivity.class);
        startActivity(Passar);
    }
}


class LoadImageAPI extends AsyncTask<String, Integer, Bitmap> {
    String TAG = getClass().getSimpleName();
    private ImageView img;
    public LoadImageAPI(ImageView img){
        this.img=img;
    }

    @Override
    protected Bitmap doInBackground(String... str) {
        Log.d(TAG + " DoINBackGround", "On doInBackground...");
        URL url = null;
        try {
            url = new URL("http://3.138.181.59:5000/images/outputs/" + str[0] + ".png");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            Log.d("BITMAP_ERROR", e.getMessage());
            e.printStackTrace();
        }
        return bmp;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG + " PreExceute","On pre Exceute......");
    }

    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);


//        ImageView img = (ImageView) rootView.findViewById(R.id.imageView3);
        img.setImageBitmap(result);
//
        Log.d(TAG + " onPostExecute", "" + result);
    }
}