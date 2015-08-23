package lung.hedu;

import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by groentje2015 on 8/23/2015.
 */
public class server_side_PHP {




    public static String [] get_dataarray_server(String php_file, String[] id_url_addon, String[] data_url_addon) throws ClassNotFoundException, URISyntaxException, IOException
    {
        String website_url = "hedu-free.uphero.com/phpfree/";
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

        ArrayList<String> data_list = new ArrayList<String>();
        Integer offset = 0;
        tel = 0;
        boolean stop = false;
        while(stop == false)
        {
            Integer index_data_start = (sb_string.indexOf("%qa", offset) +5 );
            offset = index_data_start;
            if(index_data_start == -1)
            {
                stop = true;
            }
            else
            {
                Integer index_data1_end = (sb_string.indexOf("%qa", index_data_start));
                String data = sb_string.substring(index_data_start, index_data1_end);
                data_list.add(data);
            }
        }
        String [] return_array = data_list.toArray(new String[data_list.size()]);

        return return_array;

    }


}
