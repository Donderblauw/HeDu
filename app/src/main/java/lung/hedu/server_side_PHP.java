package lung.hedu;

import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by groentje2015 on 8/23/2015.
 */
public class server_side_PHP {


    public String server = "http://hedu-free.uphero.com";


    public static ArrayList<String> get_dataarray_server(String php_file, String[] id_url_addon, String[] data_url_addon) throws ClassNotFoundException, URISyntaxException, IOException
    {
        String website_url = "http://hedu-free.uphero.com/phpfree/";
/*        String login_name = "";
        if(login_name.matches("[a-zA-Z]*") ==true)
        {
            String android_id = Settings.Secure.getString(ApplicationContextProvider.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
*/
        String url_addon = "";
        Integer tel = 0;
        while (tel < id_url_addon.length)
        {
            url_addon = url_addon + id_url_addon[tel];
            url_addon = url_addon + "=";
            url_addon = url_addon + data_url_addon[tel];
            url_addon = url_addon + "&";
            tel = tel +1;
        }
        String link = website_url + php_file + "?"+ url_addon;

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        request.setURI(new URI(link));

//        Log.e("php", "Link: " + link);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpResponse response = client.execute(request);

        BufferedReader in = new BufferedReader
                (new InputStreamReader(response.getEntity().getContent()));

        StringBuffer sb = new StringBuffer("");
        String line="";
        while ((line = in.readLine()) != null) {
            sb.append(line);
            break;
        }
        in.close();
        String sb_string = sb.toString();
 //       Log.e("php", "output_raw: " + sb_string);
        ArrayList<String> data_list = new ArrayList<String>();
        Integer offset = 0;
        tel = 0;
        boolean stop = false;
        while(stop == false)
        {
            Integer index_data_start = (sb_string.indexOf("%qa", offset) +5 );
            offset = index_data_start;
//            Log.e("php", "offset: " + offset);
            if(index_data_start == -1)
            {
                stop = true;
            }
            else
            {
                Integer index_data1_end = (sb_string.indexOf("%qa", index_data_start));
//                Log.e("php", "end: " + index_data1_end);
                if(index_data1_end == -1)
                {
                    stop = true;
                }
                else
                {
                    String data = sb_string.substring(index_data_start, index_data1_end);
                    data_list.add(data);
                }
            }
        }
//        String [] return_array = data_list.toArray(new String[data_list.size()]);
//        Log.e("php", "output_return: " + return_array.toString() );

        return data_list;

    }

    public static Document load_wolrd_index(String next_idex) throws IOException {
        String server = "http://hedu-free.uphero.com";
        String file_path = "/xml_words/"+next_idex+".xml";
        String link = server + file_path;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        try {
            request.setURI(new URI(link));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

//        Log.e("php", "Link: " + link);
        HttpResponse response = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            response = client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Document return_doc = (Document) response;


        BufferedReader in = null;
        try {
            in = new BufferedReader
                    (new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuffer sb = new StringBuffer("");
        String line="";
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String sb_string = sb.toString();
//        Log.e("php", "output_raw: " + sb_string);
        Document return_doc = null;
        if(sb_string.equals(0))
        {

        }
        else {
            DocumentBuilder db = null;
            try {
                db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(sb_string));

            try {
                return_doc = db.parse(is);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return return_doc;
    }

    public static String load_wolrd_index_string(String next_idex) throws IOException {
        String server = "http://hedu-free.uphero.com";
        String file_path = "/xml_words/"+next_idex+".xml";
        String link = server + file_path;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        try {
            request.setURI(new URI(link));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpResponse response = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            response = client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader
                    (new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuffer sb = new StringBuffer("");
        String line="";
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String sb_string = sb.toString();

        return sb_string;
    }


}
