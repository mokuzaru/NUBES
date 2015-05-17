package deustodia.app.conexion;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Victor Casas on 17/05/2015.
 */
public class ServidorDeustodia {

    public static String servidor = "http://deustodia.com/apis/nubes/api.php";

    public static String getBanos(){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("accion", "listarBanos"));
        return GET(nameValuePairs);
    }

    public static String GET(List<NameValuePair> nameValuePairs){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(servidor);
            try {
                nameValuePairs.add(new BasicNameValuePair("wooka", "wujuu"));
                nameValuePairs.add(new BasicNameValuePair("lang", Locale.getDefault().getLanguage().toString().toLowerCase()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpclient.execute(httppost);
                inputStream = httpResponse.getEntity().getContent();

                if(inputStream != null){
                    result = convertInputStreamToString(inputStream);
                }else{
                    result = "Did not work!";
                }
                System.out.println("result "+result);

            } catch (ClientProtocolException e) {
                System.out.println("result ClientProtocolException");
            } catch (IOException e) {
                System.out.println("result IOException");
            }
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            System.out.println("result error superior");
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
