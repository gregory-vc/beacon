package com.beacon.auction.ami.auctionsbeacon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Listitemselected extends Activity{

    protected static final String TAG = "MonitoringActivity";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        this.setContentView(R.layout.list_item_view);

        TextView txtList_items = (TextView) findViewById(R.id.List_item);

        Intent i = getIntent();

        // getting attached intent data

        String List_items = i.getStringExtra("List_items");

        // displaying selected List name
        txtList_items.setText(List_items);
        Button btnOk = (Button) findViewById(R.id.button);
        String hash = md5Custom(List_items);
        String messages = makeGetRequest("http://162.243.23.91/app.php/api/v1/messages/" + hash + ".json");
        ArrayList<String> list = new ArrayList<String>();
        ListView lvMainComment = (ListView) findViewById(R.id.lvMainComment);
        try {
            JSONArray jsonMess = new JSONArray(messages);
            if (jsonMess != null) {
                int len = jsonMess.length();
                for (int t=0;t<len;t++){
                    JSONObject message = (JSONObject) jsonMess.get(t);
                    String myMessage = (String) message.get("message");
                    Log.i(TAG, myMessage);
                    byte[] data = Base64.decode(myMessage, Base64.DEFAULT);
                    String text = new String(data, "UTF-8");
                    list.add(text);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, list);
                lvMainComment.setAdapter(adapter);
            }
        } catch (JSONException e) {

        } catch (UnsupportedEncodingException e) {

        }

        Log.i(TAG, messages);
        View.OnClickListener oclBtnOk = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                String List_items = i.getStringExtra("List_items");
                TextView btn = (TextView) findViewById(R.id.editText);
                String hash = md5Custom(List_items);
                JSONObject data = new JSONObject();
                String text = btn.getText().toString();

                try {
//                    text = new String(text.getBytes("UTF-8"), "UTF-8");
                    byte[] base_data = text.getBytes("UTF-8");
                    text = Base64.encodeToString(base_data, Base64.DEFAULT);
                    Log.i(TAG, hash);
                    Log.i(TAG, text);
                    data.put("hash", hash);
                    data.put("message", text);
                    makePostRequest("http://162.243.23.91/app.php/api/v1/messages.json", data);
                    String messages = makeGetRequest("http://162.243.23.91/app.php/api/v1/messages/" + hash + ".json");
                    ArrayList<String> list = new ArrayList<String>();
                    ListView lvMainComment = (ListView) findViewById(R.id.lvMainComment);
                    try {
                        JSONArray jsonMess = new JSONArray(messages);
                        if (jsonMess != null) {
                            int len = jsonMess.length();
                            for (int t=0;t<len;t++){
                                JSONObject message = (JSONObject) jsonMess.get(t);
                                String myMessage = (String) message.get("message");
                                Log.i(TAG, myMessage);
                                byte[] dataAdd = Base64.decode(myMessage, Base64.DEFAULT);
                                text = new String(dataAdd, "UTF-8");
                                list.add(text);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Listitemselected.this,
                                    android.R.layout.simple_list_item_1, list);
                            lvMainComment.setAdapter(adapter);
                        }
                    } catch (JSONException e) {

                    }
                } catch (JSONException e) {
                    Log.i(TAG, e.getMessage());
                } catch (UnsupportedEncodingException e) {
                    Log.i(TAG, e.getMessage());
                }
            }
        };
        btnOk.setOnClickListener(oclBtnOk);
    }

    private String makePostRequest(String targetUrl, JSONObject jsonParam) {
        String dRetrun = "";
        try {
            URL url = null;
            String response = null;
            String parameters = "";
            url = new URL(targetUrl);
            //create the connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // hear you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            //get the output stream from the connection you created

            DataOutputStream request = new DataOutputStream(connection.getOutputStream());
            //write your data to the ouputstream
            request.writeBytes(jsonParam.toString());
            request.flush();
            request.close();
            String line = "";
            //create your inputsream
            InputStreamReader isr = new InputStreamReader(
                    connection.getInputStream());
            //read in the data from input stream, this can be done a variety of ways
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            dRetrun = sb.toString();
            Log.i(TAG, dRetrun);
            isr.close();
            reader.close();
        } catch (IOException e) {
            Log.e("HTTP GET:", e.toString());
        }
        return dRetrun;
    }

    private String makeGetRequest(String targetUrl) {
        String dRetrun = "";
        try {
            URL url;
            url = new URL(targetUrl);
            //create the connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoOutput(true);
            connection.setRequestProperty("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.63 Safari/537.36");
            connection.setRequestProperty("Accept","*/*");
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET"); // hear you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            connection.setRequestProperty("Content-Type", "text/html; charset=utf-8");
            String line = "";
            //create your inputsream
            InputStreamReader isr = new InputStreamReader(
                    connection.getInputStream());
            //read in the data from input stream, this can be done a variety of ways
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            dRetrun = sb.toString();
            Log.i(TAG, dRetrun);
            isr.close();
            reader.close();
        } catch (IOException e) {
            Log.e("HTTP GET:", e.toString());
        }
        return dRetrun;
    }

    public static String md5Custom(String st) {
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            // тут можно обработать ошибку
            // возникает она если в передаваемый алгоритм в getInstance(,,,) не существует
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while( md5Hex.length() < 32 ){
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }
}