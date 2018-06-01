package arsic.aleksa.chatapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {

    private final static int SUCCESS = 200;

    /* Should be careful when updating sessionId and responseMessage */
    public static String sessionId;
    public String responseMessage;

    /* HTTP Post method */
    public boolean postJsonObject(String stringURL, JSONObject jsonObject) throws IOException, JSONException{
        /* Abstract class extends URLconnection with specific features for HTTP */
        HttpURLConnection httpURLConnection = null;

        /* Pointer to a resource on World Wide Web */
        java.net.URL url = new URL(stringURL);
        /* Returns instance of a object specified object on WWW */
        httpURLConnection = (HttpURLConnection) url.openConnection();
        /* Set the method for the URL request */
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        /* Accept-header */
        httpURLConnection.setRequestProperty("Accept", "application/json");

        /* We want to be able to do input and output */
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);

        /* IOException indicates that some kind of I/O error occurred */
        try{
            httpURLConnection.connect();
        }catch (IOException e){
            return false;
        }

        /* Lets application write to output stream  */
        DataOutputStream outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
        outputStream.writeBytes(jsonObject.toString());
        outputStream.flush();
        outputStream.close();

        int responseCode = httpURLConnection.getResponseCode();
        sessionId = httpURLConnection.getHeaderField("sessionid");
        responseMessage = httpURLConnection.getResponseMessage();

        Log.d("STATUS: ", String.valueOf(httpURLConnection.getResponseCode()));
        Log.d("MESSAGE: ", httpURLConnection.getResponseMessage());

        httpURLConnection.disconnect();

        return (responseCode == SUCCESS);

    }


    /* HTTP Post method with new header field representing logged usser (sessionID) */
    public boolean postJsonObjectWithSessionID(String stringURL, JSONObject jsonObject, String sessionId) throws IOException, JSONException{
        HttpURLConnection httpURLConnection = null;
        java.net.URL url = new URL(stringURL);
        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.addRequestProperty("sessionid", sessionId);

        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);

        try{
            httpURLConnection.connect();
        }catch (IOException e){
            return false;
        }

        DataOutputStream outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
        outputStream.writeBytes(jsonObject.toString());
        outputStream.flush();
        outputStream.close();

        int responseCode = httpURLConnection.getResponseCode();
        responseMessage = httpURLConnection.getResponseMessage();

        Log.d("STATUS: ", String.valueOf(httpURLConnection.getResponseCode()));
        Log.d("MESSAGE: ", httpURLConnection.getResponseMessage());
        Log.d("session id:", sessionId);

        httpURLConnection.disconnect();

        return (responseCode == SUCCESS);

    }

    /* HTTP get JSON Array with sessionId as header argument */
    public JSONArray getJSONArrayFromURL(String urlString, String sessionId) throws IOException, JSONException{
        HttpURLConnection httpURLConnection = null;
        java.net.URL url = new URL(urlString);
        httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.addRequestProperty("sessionid", sessionId);
        httpURLConnection.setReadTimeout(10000);
        httpURLConnection.setConnectTimeout(15000);

        try{
            httpURLConnection.connect();
        }catch (IOException e ){
            return null;
        }

        int responseCode = httpURLConnection.getResponseCode();
        responseMessage = httpURLConnection.getResponseMessage();
        Log.d("STATUS: ", Integer.toString(responseCode));
        Log.d("MESSAGE: ", responseMessage);
        Log.d("sessionID: ", sessionId);

        /* BufferedReader reads text from character input stream  */
        /* getInputStream reads input stream from opened connection */
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        /* Reads line of text from bufferedReader and appends it to the stringBuilder */
        while((line = bufferedReader.readLine()) != null){
            stringBuilder.append(line + "\n");
        }

        bufferedReader.close();

        /* Make string from stringBuilder */
        String jsonString = stringBuilder.toString();

        responseCode = httpURLConnection.getResponseCode();
        responseMessage = httpURLConnection.getResponseMessage();
        Log.d("STATUS: ", Integer.toString(responseCode));
        Log.d("MESSAGE: ", responseMessage);

        httpURLConnection.disconnect();

        /* Return new JSONArray from jsonString if responseCode == SUCCESS */
        if(responseCode == SUCCESS) return new JSONArray(jsonString);
        else return null;
    }


    /* HTTP get Boolean if there is a new messages for the logged user */
    public boolean getNewMessageBooleanFromURLService(String urlString, String sessionId) throws IOException, JSONException{
        HttpURLConnection httpURLConnection = null;
        java.net.URL url = new URL(urlString);
        httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.addRequestProperty("sessionid", sessionId);
        httpURLConnection.setReadTimeout(10000);
        httpURLConnection.setConnectTimeout(15000);

        try{
            httpURLConnection.connect();
        }catch (IOException e ){
            return false;
        }

        int responseCode = httpURLConnection.getResponseCode();
        responseMessage = httpURLConnection.getResponseMessage();
        Log.d("STATUS: ", Integer.toString(responseCode));
        Log.d("MESSAGE: ", responseMessage);
        Log.d("sessionID: ", sessionId);

        /* BufferedReader reads text from character input stream  */
        /* getInputStream reads input stream from opened connection */
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        /* Reads line of text from bufferedReader and appends it to the stringBuilder */
        while((line = bufferedReader.readLine()) != null){
            stringBuilder.append(line);
        }

        bufferedReader.close();

        /* Make string from stringBuilder */
        String responseString = stringBuilder.toString();
        /* Parse Boolean */
        Boolean response = Boolean.parseBoolean(responseString);

        responseCode = httpURLConnection.getResponseCode();
        responseMessage = httpURLConnection.getResponseMessage();
        Log.d("STATUS: ", Integer.toString(responseCode));
        Log.d("MESSAGE: ", Integer.toString(httpURLConnection.getResponseCode()));

        httpURLConnection.disconnect();

        /* Return new JSONArray from jsonString if responseCode == SUCCESS */
        if(responseCode == SUCCESS) return response /*new JSONArray(jsonString)*/ ;
        else return false;
    }

    /*HTTP delete*/
    public boolean httpDelete(String urlString, JSONObject jsonObject, String sessionId) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        java.net.URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        urlConnection.setRequestProperty("Accept","application/json");
        urlConnection.addRequestProperty("sessionid", sessionId);

        try {
            urlConnection.connect();
        } catch (IOException e) {
            return false;
        }

        DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
        outputStream.writeBytes(jsonObject.toString());
        outputStream.flush();
        outputStream.close();

        int responseCode = urlConnection.getResponseCode();

        Log.d("STATUS delete", String.valueOf(responseCode));
        Log.d("MSG delete" , urlConnection.getResponseMessage());
        urlConnection.disconnect();
        return (responseCode==SUCCESS);
    }

}
