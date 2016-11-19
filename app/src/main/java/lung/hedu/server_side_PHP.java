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
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by groentje2015 on 8/23/2015.
 */
public class server_side_PHP {


    public String server = "http://hedu-free.uphero.com";


    public static ArrayList<String> get_dataarray_server(String php_file, String[] id_url_addon, String[] data_url_addon, String folder) throws ClassNotFoundException, URISyntaxException, IOException
    {
        String website_url = "http://hedu-free.uphero.com/"+folder+"/";
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
        link = link.replace("\r","").replace("\n","");
        Log.e("XML parser", link);

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        request.setURI(new URI(link));

        Log.e("php", "Link: " + link);

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
        // Log.e("php", "output_raw: " + sb_string);
        ArrayList<String> data_list = new ArrayList<String>();
        Integer offset = 0;
        tel = 0;
        boolean stop = false;
        while(stop == false)
        {
            Integer index_data_start = (sb_string.indexOf("%qa", offset) +5 );
            offset = index_data_start;
            // Log.e("php", "offset: " + offset);
            if(index_data_start == -1)
            {
                stop = true;
            }
            else
            {
                Integer index_data1_end = (sb_string.indexOf("%qa", index_data_start));
                // Log.e("php", "end: " + index_data1_end);
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
 //       String [] return_array = data_list.toArray(new String[data_list.size()]);
 //       Log.e("php", "output_return: " + return_array.toString() );

        return data_list;

    }

    public static Document load_wolrd_index(String next_idex, String map) throws IOException {
        String server = "http://hedu-free.uphero.com";
        String file_path = "/"+map+"/"+next_idex+".xml";
        String link = server + file_path;
        link = link.replaceAll(" ", "_");
        link = link.replace("\r","").replace("\n","");
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


    public static Document get_document_from_server(String file_name, String map) throws IOException {
        String server = "http://hedu-free.uphero.com";
        String file_path = "/"+map+"/"+file_name;
        String link = server + file_path;
        link = link.replaceAll(" ", "_");
        link = link.replace("\r","").replace("\n","");
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
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
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
        link = link.replaceAll(" ", "_");
        link = link.replace("\r","").replace("\n","");
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
            link = link.replaceAll(" ", "_");
            link = link.replace("\r","").replace("\n","");
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


    public static Document push_file_to_server(Document document, String php_file, String suffix, String send_to_server_flag, String map, Integer min_difference) throws IOException
    {
        // SPATIES!!!
        // Log.e("XML parser", "send_to_server_flag:" + send_to_server_flag + " php_file:" + php_file + " suffix:" + suffix);

        // if(send_to_server_flag.equals("true"))

        Boolean check_time_difference_bool = false;

        if(min_difference == null)
        {
            check_time_difference_bool = true;
        }
        else
        {
            check_time_difference_bool = check_time_difference(document, min_difference);
        }



        if(check_time_difference_bool == true)
        {
            set_date_version_number_server(document);

            suffix = suffix.replaceAll(" ", "_");
            String server = "http://hedu-free.uphero.com/"+map+"/"+php_file+".php?";
            String file_path = suffix;
            String link = server + file_path;
            link = link.replaceAll(" ", "_");
            link = link.replace("\r","").replace("\n","");

            String data_to_send = document_to_string(document);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(link);
            // Log.e("XML parser", "link:" + link);

            try {
                StringEntity data_to_send_se = new StringEntity( data_to_send, HTTP.UTF_8);
                data_to_send_se.setContentType("text/xml");

                httppost.setEntity(data_to_send_se );

                // Log.e("XML parser", data_to_send_se.toString());
                HttpResponse response = null;

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                response = httpclient.execute(httppost);

                // Log.e("XML parser", "Executed! ");

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


                // HttpEntity resEntity = httpresponse.getEntity();
                // String response = EntityUtils.toString(resEntity);
                //Log.e("XML parser", "response:" + sb_string);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("XML parser", "nope :( 1" );
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("XML parser", "nope :( 2" );
            }
            // document.

        }
        return document;
    }

    public static String document_to_string(Document document)
    {
        String return_string = "";

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;
        try
        {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException e)
        {
            e.printStackTrace();
        }
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        try
        {
            transformer.transform(new DOMSource(document), new StreamResult(writer));
        } catch (TransformerException e)
        {
            e.printStackTrace();
        }
        return_string = writer.getBuffer().toString();

        return return_string;
    }


    public static String curent_time_stamp()
    {
        String return_i = null;

        SimpleDateFormat format_date =  new SimpleDateFormat("yyyyMMddHHmm");
        String curent_date = format_date.format(new Date());
        return_i = curent_date;

        return return_i;
    }

    public static int time_difference(String begin_date)
    {
        int return_i = 0;

        SimpleDateFormat format_date =  new SimpleDateFormat("yyyyMMddHHmm");
        String curent_date = format_date.format(new Date());
        if(begin_date == null)
        {
            begin_date = "0";
        }

        try
        {
            Date dateStart_d = format_date.parse(begin_date);
            Date curent_date_d = format_date.parse(curent_date);
            long diff_min = (curent_date_d.getTime() - dateStart_d.getTime()) / (60 * 1000) % 60;
            return_i = (int) diff_min;

        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        return return_i;
    }


    public static Document set_date_version_number_server(Document input_document)
    {
        String date_atm = curent_time_stamp();

        input_document = XML_IO.set_value_document(input_document, "version", "date_server", date_atm, false);

        return input_document;
    }


    public static Document set_date_version_number_phone(Document input_document)
    {
        String date_atm = curent_time_stamp();

        Boolean temp = false;

        input_document = XML_IO.set_value_document(input_document, "version", "version_on_phone", date_atm, temp);

        return input_document;
    }


    public static Boolean check_time_difference(Document input_document, Integer min_difference)
    {
        Boolean return_bool = false;

        String found_date = XML_IO.find_value_in_doc(input_document, "version", "date_server");

        int time_difference_i = time_difference(found_date);

        if(time_difference_i > min_difference)
        {
            return_bool = true;
        }
        else
        {
            return_bool = false;
        }

        String version_push_pulled = XML_IO.find_value_in_doc(input_document, "version", "version_on_phone");
        if(version_push_pulled == found_date)
        {
            return_bool = false;
        }

        return return_bool;
    }

    public static Boolean check_new_uif_server(Document input_document, String user_id)
    {
        Boolean return_bool = false;

        Document found_document = null;
        // todo Check check_new_version.php
        // todo mismatch, ask change
        String found_date = XML_IO.find_value_in_doc(input_document, "version", "version_on_phone");
        // String found_date = XML_IO.find_value_in_doc(input_document, "version", "date_server");
        long found_date_i = 0;
        if(found_date != null)
        {
            found_date = found_date.replaceAll("\\D+","");
            found_date = found_date.trim();
            found_date_i = Long.parseLong(found_date);
        }

        String php_file = "check_new_version.php";
        String[] id_url_addon = {"qid"};
        String[] data_url_addon = {user_id};
        String folder = "uifiles";
        String found_date_from_server = "";
        try
        {
            ArrayList<String> found_date_from_server_al = get_dataarray_server(php_file, id_url_addon,  data_url_addon, folder);
            if(found_date_from_server_al.size() > 0)
            {
                found_date_from_server = found_date_from_server_al.get(0);
            }
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        } catch (URISyntaxException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        if(found_date_from_server == found_date)
        {
            return_bool = false;
        }
        else if (found_date_from_server.length() > 7)
        {
            long found_date_from_server_i = Long.parseLong(found_date_from_server);
            if(found_date_from_server_i > found_date_i)
            {
                return_bool = true;


               // server is new'er ask update of file.
                /*

                try
                {
                    found_document = get_document_from_server(user_id, "uifiles");
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                */
            }
            else
            {
                // mobiel nieuwe versie
            }
        }


        return return_bool;
    }


}
