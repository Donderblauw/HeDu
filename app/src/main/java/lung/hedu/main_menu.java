package lung.hedu;

import lung.hedu.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import lung.hedu.FileIO;

import static lung.hedu.FileIO.loadStringFilePrivate;
import static lung.hedu.FileIO.saveStringFilePrivate;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class main_menu extends Activity {

// for testing TeG
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
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

    public void update_text_mainmenu(View v)
    {
        textbox_mainmenu_tv = (TextView)findViewById(R.id.textbox_mainmenu);
        // String world_1_q1a = "\"<use_font value=\"niconne_regular.ttf\" set_size=\"28\"></use_font><world n=\"1\" value=\"test_world\"></world><question>Welcome, Choose a awnser.</question><awnser goto=\"test_world_q2a.xml\">This is option A.</awnser>wnser goto=\"test_world_q2b.xml\">This is option B.</awnser>\"";
        String world_1_q1a = "<world n=\"1\" value=\"test_world\"><use_font value=\"niconne_regular.ttf\" set_size=\"28\"></use_font><question>Welcome, Choose a awnser.</question><awnser goto=\"world_1_q1a.xml\">This is option A.</awnser><awnser goto=\"test_world_q2b.xml\">This is option B.</awnser></world>";
        new MyAsyncTask().execute(world_1_q1a);
        String world_1_q2a ="<world n=\"1\" value=\"test_world\"><use_font value=\"niconne_regular.ttf\" set_size=\"28\"></use_font><question>Selected A</question><awnser goto=\"world_1_q1a.xml\">This is option A.</awnser><awnser goto=\"test_world_q2b.xml\">This is option B.</awnser></world>";
        saveStringFilePrivate("test_world_q2b", "xml", world_1_q2a);
    }

    public class MyAsyncTask extends AsyncTask<String, String, String> {

        protected void onPreExecute()
        {
            textbox_mainmenu_tv.setText("Started, writing...");
        }
        protected String doInBackground(String... String_async_input) {

            saveStringFilePrivate("world_1_q1a", "xml", String_async_input[0]);
            publishProgress("Writing complete! reading...");
            // out_put_testfile = loadStringFilePrivate("First", "txt");
            // publishProgress("reading complete! proccesing...");
            return out_put_testfile;

        }

        protected void onProgressUpdate(String... String_async) {
            // Executes whenever publishProgress is called from doInBackground
            // Used to update the progress indicator
            textbox_mainmenu_tv.setText(textbox_mainmenu_tv.getText()+" "+String_async[0]);
        }

        protected void onPostExecute(String String_async) {
            // This method is executed in the UIThread
            // with access to the result of the long running task

            textbox_mainmenu_tv.setText(textbox_mainmenu_tv.getText()+" "+String_async);

        }
    }
    public void goto_questionnaire(View v)
    {
        Intent goto_questionnaire_intent = new Intent(this, Questionnaire.class);
        // versturen information between intents, test. ik snap het niet.
//        goto_questionnaire_intent.putExtra(give_text, "Gelder");
        startActivity(goto_questionnaire_intent);

        /* XML from WEB
        public String getXmlFromUrl(String url) {
        String xml = null;

        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return XML
        return xml;
    }
         */

    }

}
