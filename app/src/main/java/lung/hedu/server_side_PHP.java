package lung.hedu;

import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
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
        link = link.replaceAll(" ", "_");
        Log.e("XML parser", link);

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
        // Log.e("XML parser", sb_string);
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

    public static Document load_wolrd_index(String next_idex, String map) throws IOException {
        String server = "http://hedu-free.uphero.com";
        String file_path = "/"+map+"/"+next_idex+".xml";
        String link = server + file_path;
        link = link.replaceAll(" ", "_");
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

    public static void push_to_testphp(String suffix, String send_to_server_flag) throws IOException {
        // SPATIES!!!

        if(send_to_server_flag.equals("true"))
        {

            suffix = suffix.replaceAll(" ", "_");
            String server = "http://hedu-free.uphero.com/active_games/test.php?";
            String file_path = suffix;
            String link = server + file_path;
            // Log.e("XML parser", link);
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
        }

    }

    public static void push_to_server_file(Document document, String php_file, String suffix, String send_to_server_flag) throws IOException {
        // SPATIES!!!
        Log.e("XML parser", "send_to_server_flag:"+send_to_server_flag);
        Log.e("XML parser", "php_file:"+php_file);
        Log.e("XML parser", "suffix:"+suffix);
        // if(send_to_server_flag.equals("true"))
        {
            String document_s = document.toString();

            suffix = suffix.replaceAll(" ", "_");
            String server = "http://hedu-free.uphero.com/phpfree/"+php_file+".php?";
            String file_path = suffix;
            String link = server + file_path;
            URL url_link = new URL(link);

            DefaultHttpClient http_client = new DefaultHttpClient();
            HttpPost http_post = new HttpPost(link);

            StringEntity document_entity = new StringEntity(document.toString(), HTTP.UTF_8);
            document_entity.setContentType("text/xml");
            Log.e("XML parser", "document.toString():"+document.toString());

            http_post.setEntity(document_entity);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HttpResponse httpresponse = http_client.execute(http_post);

/*
            HttpEntity resEntity = httpresponse.getEntity();

            String result1 = EntityUtils.toString(resEntity);


            URLConnection url_connection = url_link.openConnection();
            HttpURLConnection http_connection = (HttpURLConnection) url_connection;
            http_connection.setDoInput(true);
            http_connection.setDoOutput(true);
            http_connection.setRequestMethod("POST");
            http_connection.setRequestProperty("Content-type", "text/xml");

            PrintWriter output_stream = new PrintWriter(http_connection.getOutputStream());
            output_stream.write(document_s);
            output_stream.close();

*/
            /*
            suffix = suffix.replaceAll(" ", "_");
            String server = "http://hedu-free.uphero.com/phpfree/php_file";
            String file_path = suffix;
            String link = server + file_path;
            URL url_link = new URL(link);
            URLConnection url_connection = url_link.openConnection();
            HttpURLConnection http_connection = (HttpURLConnection) url_connection;
            http_connection.setDoInput(true);
            http_connection.setDoOutput(true);
            http_connection.setRequestMethod("POST");
            http_connection.setRequestProperty("Content-type", "text/xml");

            PrintWriter output_stream = new PrintWriter(http_connection.getOutputStream());
            output_stream.write(document_s);
            output_stream.close();
*/

            /*
            Log.e("XML parser", link);
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();

            try {
                request.setURI(new URI(link));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            */



        }

    }
}
