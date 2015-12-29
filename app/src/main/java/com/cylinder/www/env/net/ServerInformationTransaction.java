package com.cylinder.www.env.net;

import android.util.Log;

import com.cylinder.www.env.Mode;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Alvin on 2015/3/2.
 */
public class ServerInformationTransaction {
    public JSONObject fetchJSONObjectFromServer(String networkPath) {
        JSONObject jsonObjectResult = null;

        // construct
        HttpGet httpGet = constructGet(networkPath);

        //	send request
        HttpResponse httpResponse = null;
        try {
            httpResponse = new DefaultHttpClient().execute(httpGet);
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            Log.e("error", "11111111" + e1.toString());
            e1.printStackTrace();
        }

        if (httpResponse == null) {
            Log.e("error", "httpResponse is null");
            return null;
        }

        // get response

        try {
            String retSrc = EntityUtils.toString(httpResponse.getEntity());
            if(retSrc.length()!=0) {
                JSONTokener jsonTokener = new JSONTokener(retSrc);
                try {
                    jsonObjectResult = (JSONObject) jsonTokener.nextValue();

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (Mode.isDebug) {
                        Log.e("error", "boolean no", e);
                    }
                }
            }

        } catch (ParseException e1) {
            e1.printStackTrace();
            Log.e("error", "boolean no", e1);
        } catch (IOException e1) {
            e1.printStackTrace();
            Log.e("error", "boolean no", e1);
        }

        return jsonObjectResult;
    }

    public JSONObject sendToServer(String networkPath, JSONObject jsonObject) {

        // construct
        HttpPost httpPost = constructPost(networkPath);

        StringEntity se = null;
        try {
            se = new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        httpPost.setEntity(se);


        //	send request
        HttpResponse httpResponse = null;
        try {
            httpResponse = new DefaultHttpClient().execute(httpPost);
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if (httpResponse == null) {
            Log.e("error", "httpResponse is null");
        } else {
            Log.e("error", "httpResponse is not null " + httpResponse.getStatusLine().getStatusCode());
        }

        //	Get response
        JSONObject jsonObjectResult = null;
        try {
            String retSrc = EntityUtils.toString(httpResponse.getEntity());
            JSONTokener jsonTokener = new JSONTokener(retSrc);
            Object obj = jsonTokener.nextValue();
            if (obj instanceof JSONObject) {
                jsonObjectResult = (JSONObject) obj;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObjectResult;
    }

    private HttpGet constructGet(String netWorkPath) {

        HttpGet request = new HttpGet(netWorkPath);
        request.setHeader("json", "application/json");
        request.setHeader("Content-Type", "application/json");
        return request;
    }

    private HttpPost constructPost(String netWorkPath) {

        HttpPost request = new HttpPost(netWorkPath);
        request.setHeader("json", "application/json");
        request.setHeader("Content-Type", "application/json");
        return request;
    }
}
