package com.example.pavanboro.whtsweather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView resultTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText) findViewById(R.id.cityName);
        resultTextView = (TextView) findViewById(R.id.resultTextView);

    }


    public void findWeather(View view){
        Log.i("cityName",cityName.getText().toString());


        // Hide the keyboard
        InputMethodManager mgr = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");

            DownloadTask task = new DownloadTask();
            String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?q="+encodedCityName+"&appid=44929f2b3d13549960d30067dee3f481";
            task.execute(weatherUrl);
        }catch (Exception e){

            e.printStackTrace();

            Toast.makeText(getApplicationContext(), "Could not find the weather", Toast.LENGTH_LONG);
        }
    }



    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try{

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return  result;


            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Could not find the weather", Toast.LENGTH_LONG);
            }

            return null;
        }

        /* Updating UI thread using onPostExecute */
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            try {

                String message = "";

                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("weather");

                JSONArray arr = new JSONArray(weatherInfo);

                for(int i=0;i<arr.length();i++){
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if(main != "" && description != ""){

                        message += main + ": "+ description + "\r\n";

                    }
                }
                Log.i("Message: ",message);

                if(message != ""){
                    resultTextView.setText(message);
                }else {
                    Toast.makeText(getApplicationContext(), "Could not find the weather", Toast.LENGTH_LONG);
                }
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Could not find the weather", Toast.LENGTH_LONG);
            }
        }
    }
}
