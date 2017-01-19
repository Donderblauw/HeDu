package lung.hedu;

import lung.hedu.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.hardware.camera2.CameraDevice;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

/*
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParserException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import java.nio.ByteBuffer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.util.List;
import java.util.Objects;

import lung.hedu.FileIO;

import static lung.hedu.FileIO.loadStringFilePrivate;
import static lung.hedu.FileIO.saveStringFilePrivate;

import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;

*/
import java.io.IOException;
import java.net.URISyntaxException;

import java.util.ArrayList;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class main_menu extends Activity
{

// for testing TeG

    protected CameraDevice cameraDevice;

    public TextView textbox_mainmenu_tv;
    public String out_put_testfile;

    // recieve info between intents, test.
    // public static final String give_text = "give_text_HeDu";


    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener()
                {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
                        {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0)
                            {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0)
                            {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else
                        {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE)
                        {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (TOGGLE_ON_CLICK)
                {
                    mSystemUiHider.toggle();
                } else
                {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);

        login();

    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            if (AUTO_HIDE)
            {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis)
    {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

//    Button button_teun = (Button)findViewById(R.id.button_teun);
    //   button_teun.setText("Dusch ");

    public void goToLuukTests(View v)
    {
        Intent intent = new Intent(this, LuukTests.class);
        startActivity(intent);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "main_menu Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://lung.hedu/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop()
    {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "main_menu Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://lung.hedu/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public class MyAsyncTask extends AsyncTask<String, String, String>
    {

        protected void onPreExecute()
        {
            textbox_mainmenu_tv.setText("Started, writing...");
        }

        protected String doInBackground(String... String_async_input)
        {

            // saveStringFilePrivate("world_1_q1a", "xml", String_async_input[0]);


            publishProgress("Writing complete! reading...");
            // out_put_testfile = loadStringFilePrivate("First", "txt");
            // publishProgress("reading complete! proccesing...");
            return out_put_testfile;

        }

        protected void onProgressUpdate(String... String_async)
        {
            // Executes whenever publishProgress is called from doInBackground
            // Used to update the progress indicator
            textbox_mainmenu_tv.setText(textbox_mainmenu_tv.getText() + " " + String_async[0]);
        }

        protected void onPostExecute(String String_async)
        {
            // This method is executed in the UIThread
            // with access to the result of the long running task

            textbox_mainmenu_tv.setText(textbox_mainmenu_tv.getText() + " " + String_async);

        }
    }

    public void goto_questionnaire(View v)
    {

        // TODO: 06/10/2016  check nieuwe versie



        String login_name = XML_IO.find_value_in_userxml("login_info", "name", "user_info");

        if (login_name != null)
        {
            Intent goto_questionnaire_intent = new Intent(this, Questionnaire.class);
            // versturen information between intents, test. ik snap het .
//        goto_questionnaire_intent.putExtra(give_text, "Gelder");
            startActivity(goto_questionnaire_intent);
        }
        else
        {
            TextView test_teun_tv = (TextView) v;
            test_teun_tv.setText("No loggin found.");
        }
    }




    static public void check_send_server_flag(String userid)
    {
        String[] data_url_addon = {userid, "send_server"};
        String[] id_url_addon = {"qid", "qnm"};
        String php_file = "select_flag.php";
        String folder = "phpfree";

        ArrayList<String> login_data = null;
        try
        {
            login_data = server_side_PHP.get_dataarray_server(php_file, id_url_addon, data_url_addon, folder);
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
        if (login_data.size() > 0)
        {
            XML_IO.set_value_user_info("login_info", "flag_send_server", login_data.get(0).toString());

            Log.e("temp", "login_data0 " + login_data.get(0).toString());
            // Log.e("temp", "login_data1 " + login_data.get(1).toString());
            // login_name = login_data.get(1).toString().replaceAll("_", " ");
            // XML_IO.set_value_user_info("login_info", "name", login_name);
        }
    }

    public void login()
    {
        String login_name = XML_IO.find_value_in_userxml("login_info", "name", "user_info");

        if (login_name == null)
        {

            String android_id = Settings.Secure.getString(ApplicationContextProvider.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

            String[] data_url_addon = {android_id};
            String[] id_url_addon = {"qid"};
            String php_file = "free_login.php";
            String folder = "phpfree";

            ArrayList<String> login_data = null;
            try
            {
                login_data = server_side_PHP.get_dataarray_server(php_file, id_url_addon, data_url_addon, folder);
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
            if (login_data.size() > 0)
            {
                // Log.e("temp", "login_data0 " + login_data.get(0).toString());
                if (login_data.get(0).toString().equals("NF"))
                {

                } else
                {
                    XML_IO.set_value_user_info("login_info", "id", login_data.get(0).toString());

                    // Log.e("temp", "login_data0 " + login_data.get(0).toString());
                    // Log.e("temp", "login_data1 " + login_data.get(1).toString());
                    login_name = login_data.get(1).toString().replaceAll("_", " ");
                    XML_IO.set_value_user_info("login_info", "name", login_name);
                    check_send_server_flag(login_data.get(0).toString());
                }
            }
        }
        if (login_name != null)
        {
            textbox_mainmenu_tv = (TextView) findViewById(R.id.textbox_mainmenu);
            textbox_mainmenu_tv.setText(login_name);
            LinearLayout ll_login = (LinearLayout) findViewById(R.id.linearLayout_login);
            ll_login.removeAllViews();
            TextView login_name_tv = new TextView(this);
            login_name_tv.setText(login_name);

            ll_login.addView(login_name_tv);

        }

    }

    public void create_new(View v)
    {
        // ERROR WHEN ANDROID ID ALREADY EXSIST!!!!
        String php_file = "free_login.php";
        String[] id_url_addon = {"qid", "nme"};

        EditText tv_login_name = (EditText) findViewById(R.id.login_name);

        String login_name = tv_login_name.getText().toString();
        if (login_name.equals(null))
        {
            login_name = "leeg";
        }
        textbox_mainmenu_tv = (TextView) findViewById(R.id.textbox_mainmenu);
        textbox_mainmenu_tv.setText(login_name);

        if (login_name.matches("[a-z A-Z 0-9 -]*") == true && login_name.contains(" ") == false)
        {
            if (login_name.length() > 2)
            {
                if (login_name.length() < 16)
                {

                    String android_id = Settings.Secure.getString(ApplicationContextProvider.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                    login_name = login_name.replaceAll(" ", "_");
                    // TODO check all " " "_" conversions
                    XML_IO.set_value_user_info("login_info", "name", login_name);

                    // Log.e("XML parser", "start");

                    String[] data_url_addon = {android_id, login_name};
                    String folder = "phpfree";
                    ArrayList<String> login_data = null;
                    try
                    {
                        login_data = server_side_PHP.get_dataarray_server(php_file, id_url_addon, data_url_addon, folder);
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
                     // Log.e("XML parser", "got");
                    XML_IO.set_value_user_info("login_info", "id", login_data.get(0).toString());
//                    Log.e("temp", "login_data1 " + login_data.get(1).toString());
                    login_name = login_name.replaceAll("_", " ");
                    check_send_server_flag(login_data.get(0).toString());
                    login();
               } else
                {
                    textbox_mainmenu_tv.setText("Too long");
                }
            } else
            {
                textbox_mainmenu_tv.setText("Too short");
            }
        } else
        {
            textbox_mainmenu_tv.setText("please only use normal Chars no spaces.");
        }
    }


    public void start_camera(View v)
    {
        ImageView imageviewer_new_picture = (ImageView) findViewById(R.id.imageview);

        camera_photo.start_camera_take_picture(imageviewer_new_picture, "test");

    }
}
