package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    Button[] btns = new Button[4];
    String[] imgUrl = new String[100];
    String[] imgNames = new String[100];
    ImageView iv;
    int[] im = {-1,-1,-1,-1};
    int l = 0;
    private String getName(int[] im){
        Random r = new Random();
        int i = r.nextInt(100);
        for(int j=0;j<4;j++) {
            if (im[j] == i) {
                i = r.nextInt(100);
                j--;
            }
        }
        im[l] = i;
        return imgNames[i];
    }
    private void Init(){
        l=0;
        Random r = new Random();
        int k = r.nextInt(100);
        im[l] = k;
        GetImage imgGet = new GetImage();
        try {
            Bitmap bm = imgGet.execute(imgUrl[k]).get();
            iv.setImageBitmap(bm);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String name = imgNames[k];
        String url = imgUrl[k];
        int i = r.nextInt(4);
        btns[i].setText(name);
        for(int j=0;j<4;j++){
            if(j == i) continue;
            l++;
            btns[j].setText(getName(im));
        }
    }
    class GetImage extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection con = (HttpURLConnection)
                        url.openConnection();
                con.connect();
                InputStream is = con.getInputStream();
                Bitmap bm = BitmapFactory.decodeStream(is);
                return bm;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
    class Gettir extends AsyncTask<String,Void,Void>{
        @Override
        protected void onPostExecute(Void unused){
            super.onPostExecute(unused);
            Init();
        }
        @Override
        protected Void doInBackground(String... strings) {

            try {
                Document doc = Jsoup.connect(strings[0]).get();
                Elements imgs = doc.select("" +
                        ".lister-item-image img");
                int i = 0;
                for(Element im:imgs){
                    imgUrl[i] = im.attr("src");
                    imgNames[i] = im.attr("alt");
                    i++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            {

            }
            return null;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Gettir get = new Gettir();
        try {
            get.execute("https://www.imdb.com/list" +
                    "/ls059786955/?sort?list_order." +
                    "asc&mode=detail&page=1")
                    .get();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        btns[0]= findViewById(R.id.button0);
        btns[1]= findViewById(R.id.button1);
        btns[2]= findViewById(R.id.button2);
        btns[3]= findViewById(R.id.button3);
        iv = (ImageView) findViewById(R.id.imageView);
    }
    public void chosenCeleb(View v){
        Gettir get = new Gettir();
        Button b = (Button)v;
        if(equals(imgNames[im[0]])){
            Toast.makeText(MainActivity.this,
                    "True",Toast.LENGTH_LONG);
        }
        else{
            Toast.makeText(MainActivity.this,
                    "False",Toast.LENGTH_LONG);
        }
        Init();
    }
}