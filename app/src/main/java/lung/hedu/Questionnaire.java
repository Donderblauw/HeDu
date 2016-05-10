package lung.hedu;

import lung.hedu.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
// import android.support.v4.app.NavUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Typeface;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import org.w3c.dom.Document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Questionnaire extends Activity {

    // recieve info between intents, test.
    // for_main_menu_context = Intent.getStringExtra(main_menu.context_temp_across_activity);
    // public String from_main_menu = null;

    public Integer[][][] field_atm_array;

    public Integer[][] field_to_calculate_shortest_path_array;
    public Integer smallest_steps_found = -1;

    // array list 2d =
    // 0 = name
    // 1 = type (0 = empty 1 = normal 2 = player 3 = enemy 4 = neutral 5 = trigger)
    // 2 = color
    // 3 = trigger death
    // 4 = trigger talk
    public ArrayList<ArrayList<String>> field_ids_and_names = new ArrayList<ArrayList<String>>();

    public Typeface font_face = null;
    public Integer font_size = 20;
    public Integer awnser_id = 102;
    public Integer squarre_size = 30;
    public Bitmap bitmap_field = null;
    public ImageView bitmapview = null;

    Document XML_user_info_doc = null;
    public String this_world = "";
    public Boolean check_if_XMLisset = false;
    public ArrayList<String> username = new ArrayList();

    public Integer active_player_id = 0;
    public Integer target_field_id = -1;

//    public ArrayList<ArrayList<String>> objects_names_and_shorts = new ArrayList<ArrayList<String>>();

    public ArrayList<String> world_atribute_names = new ArrayList<String>();

    // id_object_player/enemy  <defs>
    public ArrayList<ArrayList<Integer>> id_def_atributs = new ArrayList<ArrayList<Integer>>();
    public ArrayList<String> names_objects_id_sync = new ArrayList<>();

    // id_object | Item id | atribute ID | value | extra
    public ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>  atribute_modifications = new ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> ();

    // x.x.0 = name | x.x.1 = math |  x.x.2 = req  |  x.x.3 = name interaction with
    public ArrayList<ArrayList<ArrayList<String>>>  enemy_interaction = new ArrayList<ArrayList<ArrayList<String>>> ();
    // public ArrayList<ArrayList<String>>  req_interaction_glob = new ArrayList<ArrayList<String>> ();
    // public ArrayList<String>  enemy_interaction_req_name = new ArrayList<String> () ;

    public ArrayList<ArrayList<String>>  enemy_turn = new ArrayList<ArrayList<String>> ();
    public ArrayList<ArrayList<String>>  req_enemy_turn_glob = new ArrayList<ArrayList<String>> ();

    public ArrayList<ArrayList<String>>  atribute_trigger = new ArrayList<ArrayList<String>> ();
    public ArrayList<ArrayList<String>>  req_atribute_trigger_glob = new ArrayList<ArrayList<String>> ();

    public ArrayList<ArrayList<String>>  no_enemy_left_trigger = new ArrayList<ArrayList<String>> ();

    public ArrayList<ArrayList<String>>  requerment_normalized = new ArrayList<ArrayList<String>> ();

    public Integer map_time = 0;
    public ArrayList<Integer> speed_atm = new ArrayList<Integer>();

    public TextToSpeech text_to_speak = new TextToSpeech(ApplicationContextProvider.getContext(), new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if(status != TextToSpeech.ERROR) {
                text_to_speak.setLanguage(Locale.UK);
            }
        }
    });

    public ArrayList<ArrayList<Integer>> atributs_send_php = new ArrayList<ArrayList<Integer>>();

    public String player_id_server = "";

    public Integer max_players_allowed_on_the_map = 0;

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

        // recieve info between intents, test.
        // from_main_menu = getIntent().getStringExtra(main_menu.give_text);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_questionnaire);
        // setupActionBar();
/*
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

        */

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

        // delayedHide(100);
        load_worlds_index();

        // recieve info between intents, test.

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.

 //   @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // TODO: If Settings has multiple levels, Up should navigate up
            // that hierarchy.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
     */
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    /*
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
*/
    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    /*
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
*/
    public String replace_q_texts (String input)
    {
        String output = input;
        String names = read_username ();
        output = output.replace("|qname", names);
        return output;
    }

    public String read_username ()
    {
        if(username.size() == 0)
        {

            String username_s = find_value_in_xml("login_info", "name");
            username.add(username_s);
            atribute_modifications.add(new ArrayList<ArrayList<ArrayList<Integer>>>());
//            objects_names_and_shorts.add(new ArrayList<String>());
//            objects_names_and_shorts.get( (objects_names_and_shorts.size()-1 )).add(username_s);
        }
        Integer tel = 0;
        String names = "";
        while(tel < username.size())
        {
            names = names+username.get(tel);
            tel = tel +1;
            if(tel < username.size())
            {
                names = names+" and ";
            }

        }
        return names;
    }

    public void XML_ini_q_or_map(String type_xml, String XML_file)
    {
        // player_id_server = find_value_in_xml(this_world, "saved_q_pos");
        XML_IO.set_value_user_info(this_world+"_save", "saved_xml_name", XML_file);
        XML_IO.set_value_user_info(this_world+"_save", "saved_q_or_m", type_xml);

        if(username.size() > 1)
        {
            username.clear();
            read_username();

            // NOT WELL CODED, CLEAR NAME LIST AFTER MAP. TO REDUCE FRIEND NAME DOUBLE
        }

        if(type_xml.equals("q"))
        {
            XML_ini_questionairre(XML_file);
        }
        else if(type_xml.equals("m"))
        {
            Document map_doc = null;
            try {
                map_doc = XML_IO.open_document_xml(this_world + "_" + XML_file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            Integer max_players_i = 1;
            String found_value_maxplayers = XML_IO.find_value_in_doc(map_doc, "map", "max_players");

            if(found_value_maxplayers != null) {
                max_players_i = Integer.parseInt( found_value_maxplayers.toString());
            }
            else{
                max_players_i = 1;
            }

            max_players_allowed_on_the_map = max_players_i;
            // Log.e("temp", "max_players_i " + max_players_i);

            if(max_players_i > 1) {

                invite_friend_to_game(XML_file );
            }
            else
            {
                XML_ini_map_new(XML_file);
            }

        }
    }

    public void invite_friend_to_game(String XML_file)
    {
        remove_views();

        LinearLayout lin_lay_q = (LinearLayout)findViewById(R.id.linearLayout_questuinnaire_vert);
        TextView click_inv_player = new TextView(this);
        click_inv_player.setId(301);
        click_inv_player.setTextSize(font_size);
        click_inv_player.setTypeface(font_face);
        click_inv_player.setText("Invite player:   Click here");

        click_inv_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // LinearLayout lin_lay_q = (LinearLayout) findViewById(R.id.linearLayout_questuinnaire_vert);

                // TextView temp_tv = (TextView) v;
                EditText text_name_new_player_click = (EditText)findViewById(302);
                String name_new_friend = text_name_new_player_click.getText().toString();
                add_friend_name(name_new_friend);
                    // add_story_line(this_world, add_value, replace_add_bool, add_line);

            }
        } ) ;

        lin_lay_q.addView(click_inv_player);

        EditText text_name_new_player = new EditText(this);
        text_name_new_player.setId(302);
        text_name_new_player.setTextSize(font_size);
        text_name_new_player.setTypeface(font_face);

        lin_lay_q.addView(text_name_new_player);


        TextView click_start_map = new TextView(this);
        click_start_map.setId(399);
        click_start_map.setTextSize(font_size);
        click_start_map.setTypeface(font_face);
        click_start_map.setText("Start: "+XML_file);

        click_start_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView temp_tv = (TextView) v;
                na_friends_start_map(temp_tv);
            }
        } ) ;

        lin_lay_q.addView(click_start_map);

        Integer tel_friends = 0;
        String found_friendname = find_value_in_xml("login_info", "friend_"+ tel_friends);
        while (found_friendname != "")
        {
            TextView click_inv_friend = new TextView(this);
            click_inv_friend.setId(303+tel_friends);
            click_inv_friend.setTextSize(font_size);
            click_inv_friend.setTypeface(font_face);
            click_inv_friend.setText(found_friendname);

            click_inv_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView temp_tv = (TextView) v;
                    // String name_new_friend = temp_tv.getText().toString();
                    add_friend_to_game(temp_tv);
                }
            } ) ;
            lin_lay_q.addView(click_inv_friend);
            tel_friends = tel_friends +1;
            found_friendname = find_value_in_xml("login_info", "friend_"+ tel_friends);
        }


    }

    public  void na_friends_start_map(TextView click_start_map_tv)
    {
        String XML_file = click_start_map_tv.getText().toString();
        XML_file = XML_file.substring(7, XML_file.length());
        // Log.e("temp", "XML_file " + XML_file);

        if(username.size() == 0)
        {
           String username_s = find_value_in_xml("login_info", "name");
            username.add(username_s);
            atribute_modifications.add(new ArrayList<ArrayList<ArrayList<Integer>>>());
        }

        Integer tel_friends = 0;
        String found_friendname = find_value_in_xml("login_info", "friend_"+ tel_friends);
        while (found_friendname != "")
        {
            TextView friends_selected = (TextView)findViewById(303+tel_friends);
            // Log.e("temp", "selected_friend " + selected_friend);
            if(friends_selected.getHint() != null)
            {
                if (friends_selected.getHint().toString().equals("selected")) {
                    String selected_friend = friends_selected.getText().toString();
                    // Log.e("temp", "selected_friend " + selected_friend);

                    username.add(selected_friend);
                }
            }
            tel_friends = tel_friends +1;
            found_friendname = find_value_in_xml("login_info", "friend_"+ tel_friends);
        }

        XML_ini_map_new(XML_file);
    }

    public void add_friend_name(String name_new_friend)
    {
        String tot_friends = find_value_in_xml("login_info", "tot_friends");
        Integer tot_friends_i = 0;
        if(tot_friends != "")
        {
            tot_friends_i = Integer.parseInt(tot_friends);
            tot_friends_i = tot_friends_i + 1;
        }
        Boolean check_if_playername_excist_bool = check_if_playername_excist(name_new_friend);
        Log.e("temp", "check_if_playername_excist_bool " + check_if_playername_excist_bool.toString());
        if(check_if_playername_excist_bool == true)
        {
            Log.e("temp", "friend_ " + tot_friends_i.toString() + " name" +name_new_friend);
            XML_IO.set_value_user_info("login_info", "friend_" + tot_friends_i.toString(), name_new_friend);
            XML_IO.set_value_user_info("login_info", "tot_friends", tot_friends_i.toString() );

            TextView click_inv_friend = new TextView(this);
            click_inv_friend.setId(303+tot_friends_i);
            click_inv_friend.setTextSize(font_size);
            click_inv_friend.setTypeface(font_face);

            click_inv_friend.setText(name_new_friend);

            click_inv_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView temp_tv = (TextView) v;
                    // String name_new_friend = temp_tv.getText().toString();
                    add_friend_to_game(temp_tv);
                }
            } ) ;

            EditText text_name_new_player_click = (EditText)findViewById(302);
            text_name_new_player_click.setText("");
            text_name_new_player_click.setHint("Player added");

            LinearLayout lin_lay_q = (LinearLayout)findViewById(R.id.linearLayout_questuinnaire_vert);
            lin_lay_q.addView(click_inv_friend);
        }
        else
        {
            EditText text_name_new_player_click = (EditText)findViewById(302);
            text_name_new_player_click.setText("");
            text_name_new_player_click.setHint("Not found");

        }
    }

    public boolean check_if_playername_excist(String name_player)
    {
        Boolean return_boolean = false;

        String[] data_url_addon = {name_player};
        String[] id_url_addon = {"qnm"};
        String php_file = "check_name_excist.php";

        ArrayList<String> login_data = null;
        try {
            login_data = server_side_PHP.get_dataarray_server(php_file, id_url_addon, data_url_addon);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (login_data.size() > 0) {

            // Log.e("temp", "login_data0 " + login_data.get(0).toString());
            if(login_data.get(0).toString().equals("1"))
            {
                // Log.e("temp", "hier" + login_data.get(0).toString());
                return_boolean = true;
            }

            // Log.e("temp", "login_data1 " + login_data.get(1).toString());
            // login_name = login_data.get(1).toString().replaceAll("_", " ");
            // XML_IO.set_value_user_info("login_info", "name", login_name);
        }

        return return_boolean;
    }

    public void add_friend_to_game(TextView name_new_friend_tv)
    {
        if(name_new_friend_tv.getHint() == null)
        {
            name_new_friend_tv.setHint("");
        }
        // Log.e("temp", "name_friend"+ name_new_friend_tv.getHint().toString());
        if(name_new_friend_tv.getHint().toString().equals("selected"))
        {
            name_new_friend_tv.setBackgroundColor(Color.parseColor("#ffffff"));
            name_new_friend_tv.setTextColor(Color.parseColor("#ee0000"));
            name_new_friend_tv.setHint("");
        }
        else
        {
            Integer tel_friends_views = 0;
            Integer total_players_selected = 1;
            TextView friends_selected = (TextView)findViewById(303+tel_friends_views);
            while (friends_selected != null)
            {
                //TextView friends_selected = (TextView)findViewById(303+tel_friends);
                // Log.e("temp", "selected_friend " + selected_friend);
                if(friends_selected.getHint() != null) {
                    if (friends_selected.getHint().toString().equals("selected")) {
                        total_players_selected = total_players_selected + 1;
                    }
                }

                tel_friends_views = tel_friends_views +1;
                friends_selected = (TextView)findViewById(303+tel_friends_views);
            }
            if(max_players_allowed_on_the_map > total_players_selected) {

                name_new_friend_tv.setBackgroundColor(Color.parseColor("#cccccc"));
                name_new_friend_tv.setTextColor(Color.parseColor("#00ee00"));
                name_new_friend_tv.setHint("selected");
            }
            else
            {
                EditText text_name_new_player_click = (EditText)findViewById(302);
                text_name_new_player_click.setText("");
                text_name_new_player_click.setHint("Max players = "+max_players_allowed_on_the_map);

            }
        }
    }


    public boolean check_req_type (String found_v, String req_type, String req_v, String then_tag_name, String then_id, Boolean equal_true)
    {
        Boolean return_bool = false;
/*
        bt = bigger then xx
        st = smaller then xx
        eq = equal
        nq = not equal
        bi = bigger > value
        sm = smaller < value
*/
        if(req_type.equals("eq"))
        {
            if(req_v.equals(found_v))
            {
                return_bool = true;
            }
        }
        else if(req_type.equals("nq"))
        {
            if(req_v.equals(found_v))
            {
            }
            else
            {
                return_bool = true;
            }
        }
        else if(req_type.equals("sm"))
        {
            Integer found_v_i = Integer.parseInt(found_v);
            Integer req_v_i = Integer.parseInt(req_v);
            if(found_v_i < req_v_i)
            {
                return_bool = true;
            }
        }
        else if(req_type.equals("bi"))
        {
            Integer found_v_i = Integer.parseInt(found_v);
            Integer req_v_i = Integer.parseInt(req_v);
            if(found_v_i > req_v_i)
            {
                return_bool = true;
            }
        }
        else
        {
            String found_v_then = find_value_in_xml(then_tag_name, then_id);
            Integer found_v_then_i = Integer.parseInt(found_v_then);
            Integer found_v_i = Integer.parseInt(found_v);
            if(req_type.equals("bt"))
            {
                if(found_v_i > found_v_then_i)
                {
                    return_bool = true;
                }
            }
            else if(req_type.equals("st"))
            {
                if(found_v_i < found_v_then_i)
                {
                    return_bool = true;
                }
            }
        }
        if(equal_true == true)
        {
            if(req_v.equals(found_v))
            {
                return_bool = true;
            }
        }

        return return_bool;
    }

    public boolean check_req_type_extended (ArrayList<Integer> req_arraylist)
    {
        Boolean return_bool = false;

        Integer tel_requerment_numbers = 0;

        while (tel_requerment_numbers < req_arraylist.size())
        {

            String req_name = requerment_normalized.get(req_arraylist.get(tel_requerment_numbers)).get(0);
            String req_type = requerment_normalized.get(req_arraylist.get(tel_requerment_numbers)).get(1);
            String req_then_name = requerment_normalized.get(req_arraylist.get(tel_requerment_numbers)).get(2);
            String extra_eq = requerment_normalized.get(req_arraylist.get(tel_requerment_numbers)).get(3);
            String must_s = requerment_normalized.get(req_arraylist.get(tel_requerment_numbers)).get(4);


            Integer[] req_1_id_array = get_object_rule_world_and_atribute_id(req_name);
            Integer value_1 = 0;
            if (req_1_id_array[0] == -1) {
                value_1 = req_1_id_array[2];
            } else {
                value_1 = count_atributs_mod_to_def(req_1_id_array[0], req_1_id_array[1], req_1_id_array[2]);
            }

            Integer[] req_then_id_array = get_object_rule_world_and_atribute_id(req_then_name);
            Integer value_then = 0;
            if (req_then_id_array[0] == -1) {
                value_then = req_then_id_array[2];
            } else {
                value_then = count_atributs_mod_to_def(req_then_id_array[0], req_then_id_array[1], req_then_id_array[2]);
            }

            Boolean equal_true = false;
            if (extra_eq.equals("t")) {
                equal_true = true;
            }


    /*
            bt = bigger then xx
            st = smaller then xx
            eq = equal
            nq = not equal
            bi = bigger > value
            sm = smaller < value
    */
            if (req_type.equals("eq")) {
                if (value_1.equals(value_then)) {
                    return_bool = true;
                }
            } else if (req_type.equals("nq")) {
                if (value_1.equals(value_then)) {
                } else {
                    return_bool = true;
                }
            } else if (req_type.equals("sm")) {
                if (value_1 < value_then) {
                    return_bool = true;
                }
            } else if (req_type.equals("bi")) {
                if (value_1 > value_then) {
                    return_bool = true;
                }
            }

            if (equal_true == true) {
                if (value_1.equals(value_then)) {
                    return_bool = true;
                }
            }
            // If the req is not met, but MUST == true, return false and escape.
            if(return_bool == false) {
                Boolean must_bool = false;
                if (must_s == null) {
                    must_bool = false;
                } else {
                    if (must_s.equals("t")) {

                        must_bool = true;
                    }
                }
                if (must_bool == true) {
                    return_bool = false;

                    // Escape loop
                    tel_requerment_numbers = req_arraylist.size();
                }
            }
            tel_requerment_numbers = tel_requerment_numbers +1;

        }

        return return_bool;
    }

    public String XML_ini_questionairre(String XML_file_input) {
        XmlPullParser XmlPullParser_temp = null;
        String text_return = "";
        String XML_file = this_world + "_" + XML_file_input;

        try {
            XmlPullParser_temp = load_XML(XML_file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        int event;
        String text = null;

        String temp = FileIO.loadStringFilePrivate(XML_file,".xml");

        String parents_xml[] = new String[9];
        Integer level_parent_atm = 0;
        String xml_atm = "";
        TextView tv_parents[] = new TextView[19];
        boolean tv_show[] = new boolean[19];

        try {
            event = XmlPullParser_temp.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name = XmlPullParser_temp.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:
                        xml_atm = name;
                        level_parent_atm = level_parent_atm+1;
                        parents_xml[level_parent_atm] = xml_atm;


                        if(xml_atm.equals("use_font"))
                        {
                            String new_font = XmlPullParser_temp.getAttributeValue(null, "value").toString();
                            font_size = Integer.parseInt(XmlPullParser_temp.getAttributeValue(null, "set_size").toString());
                            font_used(new_font);
                        }
                        else if(xml_atm.equals("question"))
                        {
                            remove_views();
                            tv_parents[level_parent_atm] = create_questionview_remove_views();
                        }
                        else if(xml_atm.equals("awnser"))
                        {
                            String goto_temp = XmlPullParser_temp.getAttributeValue(null, "goto").toString();
                            // onclick_temp = goto_temp;
                            String goto_temp_id = XmlPullParser_temp.getAttributeValue(null, "goto_id").toString();
                            // Log.e("temp", "setup " + onclick_temp);

                            tv_parents[level_parent_atm] = create_awnserview(goto_temp, goto_temp_id, XML_file_input);
                            tv_show[level_parent_atm] = true;
                        }
                        else if(xml_atm.equals("req"))
                        {
                            String req_tag_name = XmlPullParser_temp.getAttributeValue(null, "req_tag_name");
                            String req_id = XmlPullParser_temp.getAttributeValue(null, "req_id");
                            String req_v = XmlPullParser_temp.getAttributeValue(null, "req_v");
                            String req_type = XmlPullParser_temp.getAttributeValue(null, "req_type");
                            String then_tag_name = XmlPullParser_temp.getAttributeValue(null, "then_tag_name");
                            String then_id = XmlPullParser_temp.getAttributeValue(null, "then_id");
                            String extra_eq = XmlPullParser_temp.getAttributeValue("f", "extra_eq");
                            if(extra_eq == null)
                            {
                                extra_eq = "f";
                            }
                            Boolean extra_eq_b = false;
                            if(extra_eq.equals("t"))
                            {
                                extra_eq_b = true;
                            }

                            String found_v = find_value_in_xml(req_tag_name, req_id);
                            tv_show[(level_parent_atm-1)] = false;
                            boolean test_result = check_req_type(found_v, req_type, req_v, then_tag_name, then_id, extra_eq_b);
                            if(test_result == true)
                            {
                                tv_show[(level_parent_atm-1)] = true;
                            }

                        }
                        else if(xml_atm.equals("add_line"))
                        {
                            Bundle inputExtras = tv_parents[(level_parent_atm-1)].getInputExtras(true);
                            inputExtras.putString("add_line", XmlPullParser_temp.getAttributeValue(null, "line_id").toString());
                            inputExtras.putString("value", XmlPullParser_temp.getAttributeValue(null, "value").toString());
                            inputExtras.putString("replace_add", XmlPullParser_temp.getAttributeValue(null, "replace_add").toString());
                        }

                        break;

                    case XmlPullParser.TEXT: {
                        //|qname
                        String text_inside_replaced = replace_q_texts(XmlPullParser_temp.getText());
                        if (xml_atm.equals("question")) {
                            tv_parents[level_parent_atm].setText(text_inside_replaced);
                            // text_to_speak.setLanguage(Locale.US);
                            text_to_speak.speak(text_inside_replaced, TextToSpeech.QUEUE_ADD, null);
                        }
                        else if (xml_atm.equals("awnser")) {
                            tv_parents[level_parent_atm].setText(text_inside_replaced);
                        }
                    }
                        break;

                    case XmlPullParser.END_TAG:
                        if(xml_atm.equals("awnser"))
                        {
                            LinearLayout lin_lay_q = (LinearLayout)findViewById(R.id.linearLayout_questuinnaire_vert);
                            if(tv_show[level_parent_atm] == true) {
                                lin_lay_q.addView(tv_parents[level_parent_atm]);
                            }
                        }
                        level_parent_atm = level_parent_atm-1;
                        xml_atm = parents_xml[level_parent_atm];
                        break;
                }

                event = XmlPullParser_temp.next();

            }


        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text_return;
    }
    public void XML_ini_map_new(String XML_file_input)
    {
        String XML_file = this_world + "_" + XML_file_input;
        Document map_doc = null;
        try {
            map_doc = XML_IO.open_document_xml(XML_file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        remove_views();

        field_ids_and_names.add(new ArrayList());
        field_ids_and_names.get(0).add("Empty");
        field_ids_and_names.get(0).add("0");
        field_ids_and_names.get(0).add("#444444");

        field_ids_and_names.add(new ArrayList());
        field_ids_and_names.get(1).add("Open");
        field_ids_and_names.get(1).add("1");
        field_ids_and_names.get(1).add("#448844");

        Integer x_tot_sqre = Integer.parseInt(XML_IO.find_value_in_doc(map_doc, "map", "x_sqre").toString());
        Integer y_tot_sqre = Integer.parseInt(XML_IO.find_value_in_doc(map_doc, "map", "y_sqre").toString());
        Integer sqre_size = set_squarre_size(x_tot_sqre, y_tot_sqre);

        LinearLayout ll_temp = (LinearLayout) findViewById(R.id.linearLayout_questuinnaire_vert);
        ImageView field_img_view = create_imageview_field(ll_temp, sqre_size);
        bitmap_field = draw_field.create_bitmap_field(((x_tot_sqre) * (sqre_size + 2) + 2), ((y_tot_sqre) * (sqre_size + 2) + 2));
        bitmapview = field_img_view;

        NodeList cells_map = map_doc.getElementsByTagName("cell");
        Integer cells_tel = 0;
        while (cells_tel < cells_map.getLength()) {
            Node row_node_atm = cells_map.item(cells_tel);
            NamedNodeMap temp_atr = row_node_atm.getAttributes();

            Element row_element_atm = (Element) cells_map.item(cells_tel);
            NodeList cells_req = row_element_atm.getElementsByTagName("cell_req");

            Node node_temp_atr = temp_atr.getNamedItem("x");
            Integer x_pos_cell = Integer.parseInt(node_temp_atr.getTextContent().toString());
            node_temp_atr = temp_atr.getNamedItem("y");
            Integer y_pos_cell = Integer.parseInt(node_temp_atr.getTextContent().toString());
            node_temp_atr = temp_atr.getNamedItem("def");
            Integer def_cell = Integer.parseInt(node_temp_atr.getTextContent().toString());

            Integer tel_cells_req = 0;
            while (tel_cells_req < cells_req.getLength()) {
                Node reqnode_atm = cells_req.item(tel_cells_req);

                NamedNodeMap reqnode_atm_atr = reqnode_atm.getAttributes();
                Integer set_def = 0;
                if (reqnode_atm_atr.getNamedItem("set_def") == null) {
                } else {
                    set_def = Integer.parseInt(reqnode_atm_atr.getNamedItem("set_def").getTextContent());
                }

                boolean test_result = check_reqcuirements(reqnode_atm);
                if (test_result == true) {
                    def_cell = set_def;
                }

                tel_cells_req = tel_cells_req + 1;
            }

            field_atm_array[x_pos_cell][y_pos_cell][0] = def_cell;

            cells_tel = cells_tel + 1;
        }

        NodeList startposition_nodelist = map_doc.getElementsByTagName("startposition");
        Integer tel_startpos = 0;
        ArrayList strt_priority_list = new ArrayList();
        ArrayList strt_x_list = new ArrayList();
        ArrayList strt_y_list = new ArrayList();
        while(tel_startpos < startposition_nodelist.getLength())
        {
            Node start_pos_node = startposition_nodelist.item(tel_startpos);
            NamedNodeMap temp_atr = start_pos_node.getAttributes();

            Element start_pos_element_atm = (Element) startposition_nodelist.item(tel_startpos);
            NodeList start_pos_req = start_pos_element_atm.getElementsByTagName("req_startposition");

            Boolean add_start_pos = true;
            Integer tel_start_pos_req = 0;
            while (tel_start_pos_req < start_pos_req.getLength()) {
                Node reqnode_atm = start_pos_req.item(tel_start_pos_req);

                boolean test_result = check_reqcuirements(reqnode_atm);
                add_start_pos = test_result;

                tel_start_pos_req = tel_start_pos_req + 1;
            }

            if(add_start_pos == true)
            {
                Node node_temp_atr = temp_atr.getNamedItem("priority");
                Integer start_pos_prio = Integer.parseInt(node_temp_atr.getTextContent().toString());
                strt_priority_list.add(start_pos_prio);
                node_temp_atr = temp_atr.getNamedItem("x");
                Integer start_pos_x = Integer.parseInt(node_temp_atr.getTextContent().toString());
                strt_x_list.add(start_pos_x);
                node_temp_atr = temp_atr.getNamedItem("y");
                Integer start_pos_y = Integer.parseInt(node_temp_atr.getTextContent().toString());
                strt_y_list.add(start_pos_y);
            }

            tel_startpos = tel_startpos+1;
        }

        Integer found_startpos_id = find_highest_prio(strt_priority_list);

        Integer tel = 0;
        if (username.size() == 0) {
            read_username();
        }
        while (tel < username.size()) {
            field_ids_and_names.add(new ArrayList());
            Integer tot_arraylist = (field_ids_and_names.size()-1);
            while(tot_arraylist <= atribute_modifications.size())
            {
                atribute_modifications.add(new ArrayList<ArrayList<ArrayList<Integer>>>());
            }

            field_ids_and_names.get(tot_arraylist).add(String.valueOf(username.get(tel)));
            field_ids_and_names.get(tot_arraylist).add("2");
            Integer new_color = 99-(22*tel);
            if(new_color < 10)
            {
                new_color = 99;
            }
            String new_color_s = "#"+new_color.toString()+"ff"+new_color.toString();
            field_ids_and_names.get(tot_arraylist).add(new_color_s);

            Integer tel_possible_start_places = 0;
            Boolean found_place = false;
            Boolean stop_loop = false;
            Integer x_player = null;
            Integer y_player = null;
            while(stop_loop == false)
            {
                if(tel_possible_start_places < strt_x_list.size())
                {
                    x_player = (Integer) strt_x_list.get(found_startpos_id + tel_possible_start_places);
                    y_player = (Integer) strt_y_list.get(found_startpos_id + tel_possible_start_places);
                    found_place = suitable_place( x_player,  y_player);
                    stop_loop = found_place;
                }
                else
                {
                    stop_loop = true;
                }
                tel_possible_start_places = tel_possible_start_places+1;
            }
            if(found_place == true)
            {
                field_atm_array[x_player][y_player][0] = tot_arraylist;
                active_player_id = tot_arraylist;
            }
            // set position field_atm_array
            tel = tel + 1;
        }

        NodeList enemys_nodelist = map_doc.getElementsByTagName("enemy");
        Integer tel_enemys = 0;
        while (tel_enemys < enemys_nodelist.getLength())
        {
            Node enemy_node = enemys_nodelist.item(tel_enemys);
            NamedNodeMap temp_atr = enemy_node.getAttributes();

            Node node_temp_atr = temp_atr.getNamedItem("level");
            Integer enemy_lvl = Integer.parseInt(node_temp_atr.getTextContent().toString());
            node_temp_atr = temp_atr.getNamedItem("x_spawn");
            Integer enemy_x_spawn = Integer.parseInt(node_temp_atr.getTextContent().toString());
            node_temp_atr = temp_atr.getNamedItem("y_spawn");
            Integer enemy_y_spawn = Integer.parseInt(node_temp_atr.getTextContent().toString());
            node_temp_atr = temp_atr.getNamedItem("name");
            String enemy_name = node_temp_atr.getTextContent().toString();

            Element enemy_element_atm = (Element) enemys_nodelist.item(tel_enemys);
            NodeList enemy_req = enemy_element_atm.getElementsByTagName("req_enemy");

            Boolean add_enemy = false;
            Integer tel_enemy_req = 0;
            while (tel_enemy_req < enemy_req.getLength()) {
                Node reqnode_atm = enemy_req.item(tel_enemy_req);

                boolean test_result = check_reqcuirements(reqnode_atm);
                if (test_result == true) {
                    add_enemy = true;
                }
                else
                {
                    Node reqnode_atm_atr = reqnode_atm.getAttributes().getNamedItem("must");
                    Boolean must_bool = false;
                    if(reqnode_atm_atr == null)
                    {
                        must_bool = false;
                    }
                    else
                    {
                        if(reqnode_atm_atr.getTextContent().equals("t"))
                        {

                            must_bool = true;
                        }
                    }


                    // ALL req. must say yes.
                    if(must_bool == true)
                    {
                        add_enemy = false;
                        // Escape loop
                        tel_enemy_req = enemy_req.getLength();
                    }

                }

                tel_enemy_req = tel_enemy_req + 1;
            }
            if(add_enemy == true)
            {
                atribute_modifications.add(new ArrayList<ArrayList<ArrayList<Integer>>>());
                add_random_enemy(enemy_lvl,enemy_x_spawn,enemy_y_spawn, enemy_name);
            }

            tel_enemys = tel_enemys+1;
        }

        draw_field_squarres();

        Integer tel_no_enemy_triggers = 0;
        NodeList no_enemy_triggers_nl = map_doc.getElementsByTagName("no_enemy_trigger");
        while (tel_no_enemy_triggers < no_enemy_triggers_nl.getLength())
        {
            Element no_enemy_element_atm = (Element) no_enemy_triggers_nl.item(tel_no_enemy_triggers);
            NodeList temp_nl = no_enemy_element_atm.getElementsByTagName("goto_t");
            if(temp_nl.getLength() >0)
            {
                NamedNodeMap temp_atributes = temp_nl.item(0).getAttributes();


                no_enemy_left_trigger.add(new ArrayList<String>());
                Integer new_arraylist_i =  ( no_enemy_left_trigger.size() -1);
                no_enemy_left_trigger.get(new_arraylist_i).add("goto");
                no_enemy_left_trigger.get(new_arraylist_i).add(temp_atributes.getNamedItem("goto").getTextContent());
                no_enemy_left_trigger.get(new_arraylist_i).add(temp_atributes.getNamedItem("goto_id").getTextContent());
            }


            tel_no_enemy_triggers = tel_no_enemy_triggers+1;
        }
        upload_field();

    }

    public Boolean suitable_place(Integer x_player, Integer y_player)
    {
        Integer id_position = field_atm_array[x_player][y_player][0];
        String id_type = field_ids_and_names.get(id_position).get(1);
        Boolean good_place = false;
        if(field_atm_array[x_player][y_player][0] == 1)
        {
            good_place = true;
        }
        else if(id_type.equals("5"))
        {
            good_place = true;
        }

        return good_place;
    }

    public Integer find_highest_prio(ArrayList priority_list)
    {
        Integer tel = 0;
        ArrayList rank_pos_start = new ArrayList();
        ArrayList rank_pos_start_priosync = new ArrayList();


        while(tel < priority_list.size())
        {
            Integer temp = (Integer) priority_list.get(tel);
            Integer tel2 = 0;
            Boolean last = true;

            while(tel2 < rank_pos_start.size())
            {
                Integer temp2 = (Integer) rank_pos_start_priosync.get(tel2);
                if(temp>temp2)
                {
                    rank_pos_start.add(tel2, tel);
                    rank_pos_start_priosync.add(tel2, temp);
                    tel2 = rank_pos_start.size();
                    last = false;
                }
                else if(temp.equals(temp2))
                {
                    if((Math.random() < 0.5) == true)
                    {
                        rank_pos_start.add(tel2, tel);
                        rank_pos_start_priosync.add(tel2, temp);
                        tel2 = rank_pos_start.size();
                    }
                }
                tel2 = tel2+1;
            }
            if(last == true)
            {
                rank_pos_start.add(tel);
                rank_pos_start_priosync.add(temp);
            }

            tel = tel+1;
        }
        Integer return_i = (Integer) rank_pos_start.get(0);
        return return_i;
    }

    public void draw_field_squarres()
    {
        Integer tel_x = 0;
        Integer tel_y = 0;
        while (tel_x<field_atm_array.length)
        {
            tel_y = 0;
            while(tel_y<field_atm_array[tel_x].length)
            {
                Integer id_ofcell_type = field_atm_array[tel_x][tel_y][0];
                if(id_ofcell_type == null)
                {
                    id_ofcell_type = 0;
                }
                else {
                    draw_squarre(tel_x, tel_y, field_ids_and_names.get(id_ofcell_type).get(2), bitmap_field, squarre_size);
                }
                tel_y = tel_y +1;
            }
            tel_x = tel_x +1;
        }
        LinearLayout lin_lay_q = (LinearLayout)findViewById(R.id.linearLayout_questuinnaire_vert);
        lin_lay_q.removeAllViews();
        lin_lay_q.addView(bitmapview);
        bitmapview.setImageBitmap(bitmap_field);
 //       field_img_view.setImageBitmap(bitmap_field);

    }
    public void add_random_enemy(Integer level, Integer x_spawn, Integer y_spawn, String name)
    {
        // Log.e("add", "x="+x_spawn+" y="+y_spawn);
        field_ids_and_names.add(new ArrayList());
        Integer new_enemy_id = (field_ids_and_names.size()-1);
        field_ids_and_names.get(new_enemy_id).add(name);
        field_ids_and_names.get(new_enemy_id).add("3");
        field_ids_and_names.get(new_enemy_id).add("#ff6666");
        // field_ids_and_names.get(new_enemy_id).add(name);
        // doen met lvl;


        field_atm_array[x_spawn][y_spawn][0] = new_enemy_id;

    }

    public Boolean check_reqcuirements(Node input_node)
    {
        Boolean return_bool = false;

        NamedNodeMap reqnode_atm_atr = input_node.getAttributes();
        String req_tag_name = reqnode_atm_atr.getNamedItem("req_tag_name").getTextContent();
        String req_id = reqnode_atm_atr.getNamedItem("req_id").getTextContent();
        String req_v = reqnode_atm_atr.getNamedItem("req_v").getTextContent();
        String req_type = reqnode_atm_atr.getNamedItem("req_type").getTextContent();
        Node extra_eq = reqnode_atm_atr.getNamedItem("extra_eq");
        Boolean extra_eq_b = false;
        if(extra_eq == null)
        {
            extra_eq_b = false;
        }
        else
        {
            if(extra_eq.getTextContent().equals("t"))
            {

                extra_eq_b = true;
            }
        }


        String then_tag_name = null;
        if (reqnode_atm_atr.getNamedItem("then_tag_name") == null) {
        } else {
            then_tag_name = reqnode_atm_atr.getNamedItem("then_tag_name").getTextContent();
        }
        String then_id = null;
        if (reqnode_atm_atr.getNamedItem("then_id") == null) {
        } else {
            then_tag_name = reqnode_atm_atr.getNamedItem("then_id").getTextContent();
        }

        // Check for specific information instead of tag's in the user info file.
        String found_v = "";
        // Log.e("temp", "req_tag_name:" + req_tag_name);
        if(req_tag_name.equals("xTotal_players"))
        {
            Integer temp = username.size();
            found_v = temp.toString();
            // Log.e("temp", "found_v:" + found_v);
        }
        else {
            found_v = find_value_in_xml(req_tag_name, req_id);
        }
        return_bool = check_req_type(found_v, req_type, req_v, then_tag_name, then_id, extra_eq_b);

        return return_bool;
    }

    public XmlPullParser load_XML(String input) throws FileNotFoundException, XmlPullParserException {

        FileInputStream in = null;
        in = ApplicationContextProvider.getContext().openFileInput(input+".xml");

        XmlPullParserFactory xmlFactoryObject;

        xmlFactoryObject = XmlPullParserFactory.newInstance();
        XmlPullParser myparser = xmlFactoryObject.newPullParser();

        myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        myparser.setInput(in, null);

        return myparser;
    }

    public void font_used(String new_font)
    {
        font_face = Typeface.createFromAsset(getAssets(), new_font);
    }
    public TextView create_questionview_remove_views()
    {
        LinearLayout lin_lay_q = (LinearLayout)findViewById(R.id.linearLayout_questuinnaire_vert);
        TextView question_tv = new TextView(this);
        question_tv.setId(101);
        question_tv.setTextSize(font_size);
        question_tv.setTypeface(font_face);
        // public Typeface font_face = null;
        // public Integer font_size = 20;

        awnser_id = 102;
        lin_lay_q.addView(question_tv);
        return question_tv;

    }

    public void remove_views()
    {
        LinearLayout lin_lay_q = (LinearLayout)findViewById(R.id.linearLayout_questuinnaire_vert);
        lin_lay_q.removeAllViews();

    }
    public TextView create_awnserview(String onclick_temp, String goto_id, String XML_file_input)
    {

        TextView question_tv = new TextView(this);
        question_tv.setId(awnser_id);
        question_tv.setTextSize(font_size);
        question_tv.setTypeface(font_face);
        Bundle inputExtras = question_tv.getInputExtras(true);
        inputExtras.putString("onclick_temp", onclick_temp);
        inputExtras.putString("goto_id",goto_id);
        inputExtras.putString("XML_file_input",XML_file_input);

        //IF a awnser with the same  goto_id is added on the same awnser_id OR a different question with the same goto_id shifts.. serverside world_scores are scewed.
        inputExtras.putString("to_server",goto_id+awnser_id);


        question_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout lin_lay_q = (LinearLayout) findViewById(R.id.linearLayout_questuinnaire_vert);


                TextView temp_tv = (TextView) v;
                Bundle inputExtras = temp_tv.getInputExtras(true);
                String output_questionfile = inputExtras.getString("onclick_temp", "");
                String goto_id = inputExtras.getString("goto_id", "");

                // only one add_line possible.

                String add_line = inputExtras.getString("add_line", "");
                if(add_line != "") {
                    String add_value = inputExtras.getString("value", "");
                    String replace_add = inputExtras.getString("replace_add", "false");
                    boolean replace_add_bool = false;
                    if (replace_add.equals("true")) {
                        replace_add_bool = true;
                    }
                    add_story_line(this_world, add_value, replace_add_bool, add_line);
                }
                // add_story_line(add_line, add_value);

                String to_server_awnser_id = inputExtras.getString("to_server", "");
                String XML_file_input = inputExtras.getString("XML_file_input", "");
                if(to_server_awnser_id != "")
                {
                    add_awnser_personality_scores(to_server_awnser_id, XML_file_input);
                }


                XML_ini_q_or_map(goto_id, output_questionfile);

            }
        } ) ;

        awnser_id = awnser_id +1;

        return question_tv;

    }

    public void add_awnser_personality_scores (String to_server_awnser_id, String question_name)
    {
        if(player_id_server == "")
        {
            player_id_server = find_value_in_xml("login_info", "id");
        }

        String php_file = "add_awn_scr.php";
        String[] id_url_addon = {"qid", "qwn", "qnm", "qai"};
        String[] data_url_addon = {player_id_server, this_world, question_name, to_server_awnser_id};

        ArrayList<String> login_data = null;
        try {
            login_data = server_side_PHP.get_dataarray_server(php_file, id_url_addon, data_url_addon);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Integer set_squarre_size(Integer totx_squarres, Integer toty_squarres)
    {
        View fullscreen_element = findViewById(R.id.frame_layout_q);
        Integer height = ( fullscreen_element.getHeight() -3) / toty_squarres;
        Integer width = (fullscreen_element.getWidth() -3) / totx_squarres;
        if(height> width)
        {
            squarre_size = width;
        }
        else
        {
            squarre_size = height;
        }
//        Log.e("MAP", "height " + height);
//        Log.e("MAP", "width " + width);
        squarre_size =squarre_size -2;
        if(squarre_size < 10)
        {
            squarre_size = 10;
        }
        field_atm_array = new Integer[totx_squarres][toty_squarres][10];
        field_to_calculate_shortest_path_array = new Integer[totx_squarres][toty_squarres];
        return squarre_size;

    }
    /*
    public void read_rows(String input_xml, Integer y_tel_squarres)
    {
        input_xml = input_xml.replace("\n", "");
        input_xml = input_xml.replace("\t", "");
        input_xml = input_xml.replace(" ", "");
        Integer tel_field_x = 0;
        int found_value_i = 0;
        int index_data_komma = (input_xml.indexOf(","));
        while (index_data_komma != -1)
        {
            String found_value_s = input_xml.substring(0, index_data_komma);
            found_value_i = Integer.parseInt(found_value_s);

            input_xml = input_xml.substring((index_data_komma + 1), (input_xml.length()));

            draw_field.draw_squarre(tel_field_x, y_tel_squarres, found_value_i, bitmap_field, squarre_size);

            tel_field_x = tel_field_x+1;
            index_data_komma = (input_xml.indexOf(","));
        }
    }
*/

    public String find_value_in_xml(String add_line_id, String value_id)
    {
        String return_string = "";
        Document reterned_doc = null;
        if(XML_user_info_doc == null) {
            try {
                reterned_doc = XML_IO.open_document_xml("user_info");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        } else {
            reterned_doc = XML_user_info_doc;
        }
        NodeList nodes_find = reterned_doc.getElementsByTagName(add_line_id);
        if(nodes_find != null)
        {
            Node node_find = nodes_find.item(0);
            if(node_find != null) {
                NamedNodeMap temp_atr = node_find.getAttributes();

                Node node_temp_atr = temp_atr.getNamedItem(value_id);
                if (node_temp_atr != null) {
                    return_string = node_temp_atr.getTextContent();

                }
            }
        }

        return return_string;
    }

    public void check_world_excists()
    {
        if(XML_user_info_doc == null) {
            try {
                XML_user_info_doc = XML_IO.open_document_xml("user_info");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        if(XML_user_info_doc == null) {
            Element new_info_node = XML_user_info_doc.createElement("info");
            Node info_node = XML_user_info_doc.appendChild(new_info_node);

            Element new_worlds = XML_user_info_doc.createElement("worlds");
            Node worlds_node = info_node.appendChild(new_worlds);

            Element new_world_lines = XML_user_info_doc.createElement(this_world + "_story_lines");
            Node parent_lines_node = worlds_node.appendChild(new_world_lines);
        }

        NodeList info_list = XML_user_info_doc.getElementsByTagName("info");
        Node info_node = null;
        if(info_list.getLength() == 0)
        {
            Element new_info = XML_user_info_doc.createElement("info");
            info_node = XML_user_info_doc.appendChild(new_info);
        }
        else
        {
            info_node = info_list.item(0);
        }

        NodeList worlds_list = XML_user_info_doc.getElementsByTagName("worlds");
        Node worlds_node = null;
        if(worlds_list.getLength() == 0)
        {
            Element new_worlds = XML_user_info_doc.createElement("worlds");
            worlds_node = info_node.appendChild(new_worlds);
        }
        else
        {
            worlds_node = worlds_list.item(0);
        }

        NodeList nodes_world_lines = XML_user_info_doc.getElementsByTagName(this_world + "_story_lines");
        Node parent_lines_node = null;
        if(nodes_world_lines.getLength() == 0)
        {
            Element new_world_lines = XML_user_info_doc.createElement(this_world + "_story_lines");
            parent_lines_node = worlds_node.appendChild(new_world_lines);
            try {
                XML_IO.save_XML("user_info", XML_user_info_doc);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }

    }

    public void add_story_line(String add_line_id, String add_value, Boolean Freplace_or_Tadd, String value_id)
    {
        if(check_if_XMLisset == false)
        {
            check_world_excists();
            check_if_XMLisset = true;
        }
        NodeList nodes_world_lines = XML_user_info_doc.getElementsByTagName(this_world+"_story_lines");
        Node parent_lines_node = nodes_world_lines.item(0);

        NodeList nodes_new_line = XML_user_info_doc.getElementsByTagName(add_line_id);
        if(nodes_new_line.getLength() == 0)
        {
            Element new_line = XML_user_info_doc.createElement(add_line_id);
            new_line.setAttribute(value_id, add_value);
            parent_lines_node.appendChild(new_line);
        }
        else
        {
            Node node_found = nodes_new_line.item(0);

            NamedNodeMap temp_atr = node_found.getAttributes();
            Node node_temp_atr = temp_atr.getNamedItem(value_id);
            if(node_temp_atr == null)
            {
                //k
                Log.e("XML parser", "add_line_id:"+add_line_id+" value:" + add_value + " Value ID " + value_id);
                Attr atribute_new = XML_user_info_doc.createAttribute(add_value);
                // misschien moet dit node_found.appendChild(atribute_new) zijn.
                node_temp_atr.appendChild(atribute_new);
            }
            else
            {
                node_temp_atr.setTextContent(add_value);
            }

            if(Freplace_or_Tadd == true)
            {
                String node_temp_atr_s = node_temp_atr.getTextContent();

                Integer node_temp_atr_i = Integer.parseInt(node_temp_atr_s);
                node_temp_atr_i = node_temp_atr_i + Integer.parseInt(add_value);
                node_temp_atr.setTextContent(node_temp_atr_i.toString());
            }
        }

        try {
            XML_IO.save_XML("user_info", XML_user_info_doc);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }


    }

    public void load_worlds_index()
    {
        Document index_worlds_user = null;
        LinearLayout lin_lay_q = (LinearLayout)findViewById(R.id.linearLayout_questuinnaire_vert);
        try {
            index_worlds_user = XML_IO.open_document_xml("index_worlds");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        String worlds_index_version_user = XML_IO.find_value_in_doc(index_worlds_user, "worlds", "version");
        if(worlds_index_version_user == null)
        {
            worlds_index_version_user = "0";
        }
        else
        {
            NodeList worlds_nodelist = index_worlds_user.getElementsByTagName("world");
            Integer tel = 0;

            lin_lay_q.removeAllViews();

            while (tel < worlds_nodelist.getLength())
            {
                String world_name = worlds_nodelist.item(tel).getAttributes().getNamedItem("name").getTextContent();
                TextView tv_adding_worlds = create_text_view_worlds_start(world_name, "Enter: ");
                lin_lay_q.addView(tv_adding_worlds);
                tel = tel+1;
            }
        }

        TextView return_tv = new TextView(this);
        return_tv.setId(awnser_id);
        return_tv.setTextSize(font_size);
        return_tv.setText("Load changes from server.");

        return_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load_worlds_index_server();
            }
        } ) ;

        lin_lay_q.addView(return_tv);

     }

    public void load_worlds_index_server()
    {

        Document index_worlds_user = null;

        try {
            index_worlds_user = XML_IO.open_document_xml("index_worlds");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        String worlds_index_version_user = XML_IO.find_value_in_doc(index_worlds_user, "worlds", "version");
        if(worlds_index_version_user == null)
        {
            worlds_index_version_user = "0";
        }

        String worlds_index_version_server = "x";

        {
            Document index_worlds = null;
            try {
                index_worlds = server_side_PHP.load_wolrd_index("index_worlds");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(index_worlds == null)
            {
//                XML_IO.find_value_in_doc(index_worlds, "world", "name");
            }
            else {
//                String found_world = XML_IO.find_value_in_doc(index_worlds, "world", "name");
                NodeList worlds_nodelist = index_worlds.getElementsByTagName("world");
                Integer tel = 0;
                LinearLayout lin_lay_q = (LinearLayout)findViewById(R.id.linearLayout_questuinnaire_vert);
                lin_lay_q.removeAllViews();

                Document user_world_index = null;

                while (tel < worlds_nodelist.getLength())
                {
                    String world_name = worlds_nodelist.item(tel).getAttributes().getNamedItem("name").getTextContent();
                    String world_version = worlds_nodelist.item(tel).getAttributes().getNamedItem("version").getTextContent();

                    String link_file_user = world_name+"_index";
                    try {
                        user_world_index = XML_IO.open_document_xml(link_file_user);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }
                    String world_version_user = XML_IO.find_value_in_doc(user_world_index, "xml_files", "version");
                    if(world_version.equals(world_version_user))
                    {
                        //start
                        TextView tv_adding_worlds = create_text_view_worlds_start(world_name, "Enter: ");
                        lin_lay_q.addView(tv_adding_worlds);
                    }
                    else {
                        TextView tv_adding_worlds = create_text_view_worlds(world_name, "Download: ");
                        lin_lay_q.addView(tv_adding_worlds);

                    }
                    tel = tel + 1;
                }

                try {
                    XML_IO.save_XML("index_worlds", index_worlds);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }

        }
        // textbox_mainmenu_tv.setText(result_tv);
    }

    public TextView create_text_view_worlds(String world_adding, String prefix) {
        TextView return_tv = new TextView(this);
        return_tv.setId(awnser_id);
        return_tv.setTextSize(font_size);
        return_tv.setText(prefix + world_adding);
        Bundle inputExtras = return_tv.getInputExtras(true);
        inputExtras.putString("onclick_temp", world_adding);

        return_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout lin_lay_q = (LinearLayout) findViewById(R.id.linearLayout_questuinnaire_vert);

                TextView temp_tv = (TextView) v;
                Bundle inputExtras = temp_tv.getInputExtras(true);
                String world_to_load = inputExtras.getString("onclick_temp", "");
                download_world(world_to_load);

            }
        }) ;

        awnser_id = awnser_id +1;

        return return_tv;
    }

    public void ini_first_question_or_map(String world_to_load)
    {
        String document_world_index_s = world_to_load+"_index";
        Document world_index = null;
        try {
            world_index = XML_IO.open_document_xml(document_world_index_s);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        this_world = world_to_load;

        String saved_xml_name = find_value_in_xml(this_world+"_save", "saved_xml_name");
        String saved_q_or_m = find_value_in_xml(this_world+"_save", "saved_q_or_m");

        if(saved_xml_name =="") {

            NodeList worlds_nodelist = world_index.getElementsByTagName("start_file");
            Integer tel = 0;

            Random rand = new Random();
            int n = rand.nextInt(worlds_nodelist.getLength());

            saved_xml_name = worlds_nodelist.item(n).getAttributes().getNamedItem("name").getTextContent();
            saved_q_or_m = worlds_nodelist.item(n).getAttributes().getNamedItem("qorm").getTextContent();
        }
        else
        {

        }
        read_map_rules();

        XML_ini_q_or_map(saved_q_or_m, saved_xml_name);
    }

    public TextView create_text_view_worlds_start(String world_adding, String prefix)
    {
        TextView return_tv = new TextView(this);
        return_tv.setId(awnser_id);
        return_tv.setTextSize(font_size);
        return_tv.setText(prefix+world_adding);
        Bundle inputExtras = return_tv.getInputExtras(true);
        inputExtras.putString("onclick_temp", world_adding);

        return_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout lin_lay_q = (LinearLayout) findViewById(R.id.linearLayout_questuinnaire_vert);

                TextView temp_tv = (TextView) v;
                Bundle inputExtras = temp_tv.getInputExtras(true);
                String world_to_load = inputExtras.getString("onclick_temp", "");
                // temp_tv.setText("clicked "+world_to_load);
                // download_world(world_to_load); START WORLD
                ini_first_question_or_map(world_to_load);

            }
        }) ;

        awnser_id = awnser_id +1;

        return return_tv;
    }

    public void download_world(String world_adding)
    {
        String link = world_adding + "/" + world_adding + "_index";
        Document world_selected_index = null;
        try {
            world_selected_index = server_side_PHP.load_wolrd_index(link);
        } catch (IOException e) {
            e.printStackTrace();
        }

        NodeList worlds_nodelist = world_selected_index.getElementsByTagName("xml_file");
        Integer tel = 0;
        LinearLayout lin_lay_q = (LinearLayout)findViewById(R.id.linearLayout_questuinnaire_vert);
        lin_lay_q.removeAllViews();

        while (tel < worlds_nodelist.getLength())
        {
            String file_name = worlds_nodelist.item(tel).getAttributes().getNamedItem("name").getTextContent();
//            TextView new_downloaded_tv = new TextView(this);
            String link_file_adding =  world_adding + "/" + file_name;
            String file_new_name =  world_adding + "_" + file_name;

            String data_from_server = null;
            try {
                data_from_server = server_side_PHP.load_wolrd_index_string(link_file_adding);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileIO.saveStringFilePrivate(file_new_name, "xml",data_from_server);

            tel = tel+1;
        }

        try {
            XML_IO.save_XML(world_adding+"_index", world_selected_index);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        load_worlds_index();

    }

    public ImageView create_imageview_field (LinearLayout lin_lay_q, final Integer squarre_size_temp) {
        lin_lay_q.removeAllViews();
        Context context_this = ApplicationContextProvider.getContext();
        ImageView new_image_view = new ImageView(context_this);
        new_image_view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        new_image_view.canScrollVertically(1);
        new_image_view.setId(R.id.drawfield_id);

        new_image_view.setOnTouchListener(new ImageView.OnTouchListener() {
            final Integer squarre_size_q = squarre_size_temp;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
//            	System.out.println("klikt");
//                Log.e("MAP", "klik ");
                // Integer squarre_size_q = Questionnaire.get_squarresize_q();

                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    ImageView imageView = ((ImageView) v);

                    if (imageView.getId() == R.id.drawfield_id) {

                        ImageView imageView_temp = ((ImageView) v);
                        float x_axis_f = event.getX() - imageView.getX();
                        float y_axis_f = event.getY() - imageView.getY();
                        Integer x_axis_I = (int) x_axis_f;
                        Integer y_axis_I = (int) y_axis_f;
                        Integer field_x = (int) Math.floor((double) x_axis_I / (double) (squarre_size_q + 1));
                        Integer field_y = (int) Math.floor((double) y_axis_I / (double) (squarre_size_q + 1));


                        Bitmap field_bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
// HIER MOET IETS
                        // field_bmp = draw_squarre(field_x, field_y, "#cccccc", field_bmp, squarre_size_q);
                        // imageView_temp.setImageBitmap(field_bmp);

                        clicked_for_info(field_x, field_y);

                    }
                }
                return true;
            }
        });
//        new_image_view.setOnTouchListener

        lin_lay_q.addView(new_image_view);
        return new_image_view;
    }

    public void clicked_for_info(Integer x_clicked, Integer y_clicked)
    {
        LinearLayout lin_lay_q = (LinearLayout)findViewById(R.id.linearLayout_questuinnaire_vert);

        Integer[] pos_active_player_id = xy_pos_id(active_player_id);
        Integer id_ofcell_type_clicked = field_atm_array[x_clicked][y_clicked][0];

        if(id_ofcell_type_clicked != null) {

            // 1 = type (0 = empty 1 = normal 2 = player 3 = enemy 4 = neutral 5 = trigger)
            String type_clicked = field_ids_and_names.get(id_ofcell_type_clicked).get(1);
            String total_text = "";

            Integer id_rulebook = from_object_name_to_rule_number(field_ids_and_names.get(id_ofcell_type_clicked).get(0));
            if (id_rulebook != -1) {

                total_text = total_text + "Name" + " : " + field_ids_and_names.get(id_ofcell_type_clicked).get(0) + "\n";

                Integer tel_atributes = 0;
                while (tel_atributes < world_atribute_names.size()) {
                    String atribut_name = world_atribute_names.get(tel_atributes);
                    Integer total_atribute_value = count_atributs_mod_to_def(id_rulebook, id_ofcell_type_clicked, tel_atributes);

                    total_text = total_text + atribut_name + " : " + total_atribute_value + "\n";

                    tel_atributes = tel_atributes + 1;
                }
            }

            // TECTVIEW IS SHWON AT THE END OF THIS FUNCTION

            Integer distance = 0;
            if (pos_active_player_id[2] == 1) {
                distance = range_to(pos_active_player_id[0], pos_active_player_id[1], x_clicked, y_clicked);
                target_field_id = id_ofcell_type_clicked;
            }
            boolean walkable = false;
            if (type_clicked.equals("1")) {
                walkable = true;
            } else if (type_clicked.equals("5")) {
                walkable = true;
            }
            if (walkable == true) {
                Integer id_walk_range = find_atribute_if_from_string("Walk range");


                // PLAYER ID = altijd null?!

                Integer walk_range = count_atributs_mod_to_def(0, active_player_id, id_walk_range);

                if (distance <= walk_range) {
                    move_player(x_clicked, y_clicked, pos_active_player_id[0], pos_active_player_id[1]);
                }
            }
            if (type_clicked.equals("3")) {
                //
                Integer tel_already_set_views = 0;
                View alreadyset_interaction_view = findViewById(201+tel_already_set_views);
                while (alreadyset_interaction_view != null) {
                    lin_lay_q.removeView(alreadyset_interaction_view);
                    tel_already_set_views = tel_already_set_views+1;
                    alreadyset_interaction_view = findViewById(201+tel_already_set_views);
                }

                Integer tel_interactions = 0;
                while (tel_interactions < enemy_interaction.size()) {
                    // Log.e("temp", "enemy_interaction_req_name:" + enemy_interaction.get(tel_interactions).get(0).get(0) +" ckicked:" +field_ids_and_names.get(id_ofcell_type_clicked).get(0)+ "reqname = " + enemy_interaction.get(tel_interactions).get(3).get(0).toString());


                    if(enemy_interaction.get(tel_interactions).get(3).get(0).toString().equals(field_ids_and_names.get(id_ofcell_type_clicked).get(0)))
                    {
                        Integer tel_req_interactions = 0;
                        ArrayList<Integer> req_norm_numbers = new ArrayList<Integer>();

                        while (tel_req_interactions < enemy_interaction.get(tel_interactions).get(2).size())
                        {
                            Integer temp_int_req_norm_number = Integer.parseInt(enemy_interaction.get(tel_interactions).get(2).get(tel_req_interactions));
                            req_norm_numbers.add(temp_int_req_norm_number);
                            tel_req_interactions = tel_req_interactions +1;
                        }
                        Boolean check_req_bool = check_req_type_extended (req_norm_numbers);
                        // Log.e("temp", "check_req_bool:" + check_req_bool +" ckicked:" +field_ids_and_names.get(id_ofcell_type_clicked).get(0));
                        if(check_req_bool == true)
                        {

                            TextView add_enemy_interactions = new TextView(this);
                            add_enemy_interactions.setId((201 + tel_interactions));
                            String name_interaction = enemy_interaction.get(tel_interactions).get(0).get(0);
                            add_enemy_interactions.setTextSize(font_size);
                            add_enemy_interactions.setText(name_interaction);

                            Bundle inputExtras = add_enemy_interactions.getInputExtras(true);
                            inputExtras.putString("id_interactions", tel_interactions.toString());
                            inputExtras.putString("id_enemy", id_ofcell_type_clicked.toString());
                            inputExtras.putString("active_player", active_player_id.toString());

                            add_enemy_interactions.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TextView temp_tv = (TextView) v;
                                    Bundle inputExtras = temp_tv.getInputExtras(true);
                                    String id_interaction_s = inputExtras.getString("id_interactions", "");
                                    if (id_interaction_s != "") {
                                        Integer id_interaction = Integer.parseInt(id_interaction_s);
                                        Integer id_enemy = Integer.parseInt(inputExtras.getString("id_enemy", ""));
                                        Integer id_active_player = Integer.parseInt(inputExtras.getString("active_player", ""));


                                        target_field_id = id_enemy;
                                        Integer tel_math_problems = 0;
                                        while (tel_math_problems < enemy_interaction.get(id_interaction).get(1).size()) {
                                            String math_problem = enemy_interaction.get(id_interaction).get(1).get(tel_math_problems);
                                            // Log.e("temp", "math_problem:" + math_problem);
                                            aply_math_to_interaction(math_problem);
                                            tel_math_problems = tel_math_problems + 1;
                                        }

                                        // aply_math_to_interaction(math_problem);

                                    }

                                    end_turn();
                                }
                            });

                            lin_lay_q.addView(add_enemy_interactions);
                        }


                    }
/*
                    while (tel_req_interactions < enemy_interaction.get(tel_interactions).get(3))

                    if(enemy_interaction_req_name.get(tel_interactions).equals(field_ids_and_names.get(id_ofcell_type_clicked).get(0)))
                    {
                        // Log.e("temp", "Hij komt hier!");




                        k // normalize



                        String req_name = req_interaction_glob.get(tel_interactions).get(1);
                        String req_type = req_interaction_glob.get(tel_interactions).get(2);
                        String req_then_name = req_interaction_glob.get(tel_interactions).get(3);
                        String extra_eq = req_interaction_glob.get(tel_interactions).get(4);
                        String must = req_interaction_glob.get(tel_interactions).get(5);
                        Integer[] req_1_id_array = get_object_rule_world_and_atribute_id(req_name);
                        Integer value_1 = 0;
                        if (req_1_id_array[0] == -1) {
                            value_1 = req_1_id_array[2];
                        } else {
                            value_1 = count_atributs_mod_to_def(req_1_id_array[0], req_1_id_array[1], req_1_id_array[2]);
                        }

                        Integer[] req_then_id_array = get_object_rule_world_and_atribute_id(req_then_name);
                        Integer value_then = 0;
                        if (req_then_id_array[0] == -1) {
                            value_then = req_then_id_array[2];
                        } else {
                            value_then = count_atributs_mod_to_def(req_then_id_array[0], req_then_id_array[1], req_then_id_array[2]);
                        }

                        Boolean equal_true = false;
                        if (extra_eq.equals("t")) {
                            equal_true = true;
                        }

                        Boolean check_req = check_req_type(value_1.toString(), req_type, value_then.toString(), null, null, equal_true);
                        if (check_req == true)
                        {

                            k // MUST moet hier nog



                            TextView add_enemy_interactions = new TextView(this);
                            add_enemy_interactions.setId((201 + tel_interactions));
                            String name_interaction = enemy_interaction.get(tel_interactions).get(0);
                            add_enemy_interactions.setTextSize(font_size);
                            add_enemy_interactions.setText(name_interaction);

                            Bundle inputExtras = add_enemy_interactions.getInputExtras(true);
                            inputExtras.putString("id_interactions", tel_interactions.toString());
                            inputExtras.putString("id_enemy", id_ofcell_type_clicked.toString());
                            inputExtras.putString("active_player", active_player_id.toString());

                            add_enemy_interactions.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TextView temp_tv = (TextView) v;
                                    Bundle inputExtras = temp_tv.getInputExtras(true);
                                    String id_interaction_s = inputExtras.getString("id_interactions", "");
                                    if (id_interaction_s != "") {
                                        Integer id_interaction = Integer.parseInt(id_interaction_s);
                                        Integer id_enemy = Integer.parseInt(inputExtras.getString("id_enemy", ""));
                                        Integer id_active_player = Integer.parseInt(inputExtras.getString("active_player", ""));


                                        target_field_id = id_enemy;
                                        Integer tel_math_problems = 1;
                                        while (tel_math_problems < enemy_interaction.get(id_interaction).size()) {
                                            String math_problem = enemy_interaction.get(id_interaction).get(tel_math_problems);
                                            aply_math_to_interaction(math_problem);
                                            tel_math_problems = tel_math_problems + 1;
                                        }

                                        // aply_math_to_interaction(math_problem);

                                    }

                                    end_turn();
                                }
                            });

                            lin_lay_q.addView(add_enemy_interactions);

                        }
                        else
                        {
                            Boolean must_bool = false;

                            if (must.equals("t"))
                            {
                                must_bool = true;
                            }



                            // ALL req. must say yes.
                            if (must_bool == true)
                            {
                                //add_enemy = false;
                                // Escape loop
                                //tel_enemy_req = enemy_req.getLength();
                            }
                        }

                    }
                    */

                    tel_interactions = tel_interactions + 1;
                }

            }
            View alreadyset = findViewById(301);
            if (alreadyset != null) {
                lin_lay_q.removeView(alreadyset);
            }

            TextView add_target_atribute = new TextView(this);
            add_target_atribute.setId(301);
            add_target_atribute.setTextSize(font_size);
//        add_target_atribute.setsc

            add_target_atribute.setText(total_text);
            lin_lay_q.addView(add_target_atribute);
        }

    }



    public void aply_math_to_interaction(String math_problem)
    {

        // String math_problem = enemy_interaction.get(id_interaction).get(1);
//        String math_problemoriginal = math_problem;
        math_problem = math_problem.replaceAll("\n", "");
        math_problem = math_problem.replaceAll("\t", "");
        Integer first_quote = 0;
        Integer second_quote = 0;

        // first resolve randoms
        Integer first_open_squarre = math_problem.indexOf("[");
        Integer first_close_squarre = math_problem.indexOf("]");
        while(first_open_squarre != -1)
        {
            String inside_squarre = math_problem.substring((first_open_squarre + 1), first_close_squarre);
            String awnser = replace_brackets(inside_squarre);
            math_problem = math_problem.substring(0, first_open_squarre) + awnser + math_problem.substring(first_close_squarre + 1);
            first_open_squarre = math_problem.indexOf("[");
        }

        first_quote = (math_problem.indexOf("\""));
        math_problem = math_problem.substring(first_quote + 1);
        first_quote = (math_problem.indexOf("\""));
        Integer end_name = (math_problem.indexOf("|"));
        String type_ofirst_perator = math_problem.substring(0, end_name);
        String name_first_atribute = math_problem.substring((end_name+1), first_quote);
        math_problem = math_problem.substring(first_quote +1);
        Integer atribute_to_set = find_atribute_if_from_string(name_first_atribute);

        // solve () first.
        Integer first_open_round = math_problem.indexOf("(");
        while(first_open_round != -1)
        {
            first_open_round = math_problem.indexOf("(");
            Integer first_close_round = math_problem.indexOf(")");

            Boolean stop_loop = false;
            while (stop_loop == false)
            {
                stop_loop = true;
                Integer next_open_tag = math_problem.indexOf("(", first_open_round + 1);
                if(next_open_tag < first_close_round ) {
                    if(next_open_tag != -1)
                    {
                        first_open_round = next_open_tag;
                        stop_loop = false;
                    }
                }

            }

            Integer round_brace_awnser_i = calculate_from_string(math_problem.substring((first_open_round + 1), first_close_round));
            String round_brace_awnser_s = "\"v|" + round_brace_awnser_i.toString() + "\"";

            math_problem = math_problem.substring(0, first_open_round) + round_brace_awnser_s + math_problem.substring(first_close_round+1);
            first_open_round = math_problem.indexOf("(");

        }

        Integer awnser_mathproblem = calculate_from_string(math_problem);

        Integer id_found_of_object = from_name_to_id_field(type_ofirst_perator);


        atribute_modifications.get(id_found_of_object).add(new ArrayList<ArrayList<Integer>>());
        Integer new_mod = atribute_modifications.get(id_found_of_object).size() -1;

/*        while (new_mod >= atribute_modifications.get(id_enemy_field).size())
        {
            atribute_modifications.get(id_enemy_field).add(new ArrayList<ArrayList<Integer>>());
        }
        */

        ArrayList temp = new ArrayList<Integer>();
        temp.add(0, awnser_mathproblem);
        ArrayList<ArrayList<Integer>> temp_new = new ArrayList<ArrayList<Integer>>();
        temp_new.add(atribute_to_set, temp);

        atribute_modifications.get(id_found_of_object).set(new_mod, temp_new);


    }

    public void atribute_triggers()
    {
        Integer tel_atribute_trigers = 0;
        while(tel_atribute_trigers < atribute_trigger.size())
        {

            String req_name = req_atribute_trigger_glob.get(tel_atribute_trigers).get(1);
            String req_type = req_atribute_trigger_glob.get(tel_atribute_trigers).get(2);
            String req_then_name = req_atribute_trigger_glob.get(tel_atribute_trigers).get(3);
            String eq_true_s = req_atribute_trigger_glob.get(tel_atribute_trigers).get(4);

            Integer[] req_value1_array = get_object_rule_world_and_atribute_id(req_name);
            Integer value1 = 0;
            if (req_value1_array[0] == -1) {
                value1 = req_value1_array[2];
            } else {
                value1 = count_atributs_mod_to_def(req_value1_array[0], req_value1_array[1], req_value1_array[2]);
            }

            Integer[] req_then_array = get_object_rule_world_and_atribute_id(req_then_name);
            Integer value_then = 0;
            if (req_then_array[0] == -1) {
                value_then = req_then_array[2];
            } else {
                value_then = count_atributs_mod_to_def(req_then_array[0], req_then_array[1], req_then_array[2]);
            }

            Boolean equals_true = false;
            if (eq_true_s.equals("t")) {
                equals_true = true;
            }
            Boolean req_pass = false;
            req_pass = check_req_type(value1.toString(), req_type, value_then.toString(), null, null, equals_true);
            // Log.e("XML parser", " 1:"+value1.toString() +" 2:"+ req_type + " 2:" + value_then.toString());
            if(req_pass == true)
            {
                String temp = atribute_trigger.get(tel_atribute_trigers).get(1);
                Log.e("XML parser", "TRUE: "+ temp);
                if(temp.equals("remove"))
                {
                    //
                    remove_object(target_field_id);
                }

                //something
            }
            tel_atribute_trigers = tel_atribute_trigers+1;
        }

    }

    public void remove_object(Integer object_id)
    {

        field_ids_and_names.get(object_id).set(1, "1");
        Integer[] xy_pos = xy_pos_id(object_id);

        field_atm_array[xy_pos[0]][xy_pos[1]][0] = 1;
        String player_id_server = find_value_in_xml("login_info", "id");
        String string_to_send_to_testPHP = "qid="+player_id_server+"&qty=rem&qcx="+xy_pos[0]+"&qcy="+xy_pos[1]+"&qcv="+"0"+"&qct="+"1";

        String send_to_server_flag = find_value_in_xml("login_info", "flag_send_server");

        try {
            server_side_PHP.push_to_testphp(string_to_send_to_testPHP, send_to_server_flag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //k
    }

    public Integer[] get_user_personality()
    {
        Integer return_int[] = new Integer[6];
        String norm_extra_s = XML_IO.find_value_in_userxml("login_info", "normalized_Extraversion");
        if (norm_extra_s == null)
        {
            if(player_id_server == "")
            {
                player_id_server = find_value_in_xml("login_info", "id");
            }

            String[] data_url_addon = {player_id_server};
            String[] id_url_addon = {"qid"};
            String php_file = "user_personality_out.php";

            ArrayList<String> login_data = null;
            try {
                login_data = server_side_PHP.get_dataarray_server(php_file, id_url_addon, data_url_addon);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (login_data.size() > 0)
            {
                Integer total_numbers = 0;
                Integer tel = 0;
                while(tel < 5)
                {
                    return_int[tel] = Integer.parseInt(login_data.get(tel));
                    total_numbers = total_numbers + return_int[tel];
                    tel = tel +1;
                }
                Integer correction = ((100/total_numbers)*1000);
                tel = 0;
                while(tel < 5)
                {
                    return_int[tel] = Math.round(return_int[tel] * (correction / 1000));
                }
                XML_IO.set_value_user_info("login_info", "normalized_Extraversion", return_int[0].toString() );
                XML_IO.set_value_user_info("login_info", "normalized_Agreeableness", return_int[1].toString() );
                XML_IO.set_value_user_info("login_info", "normalized_Conscientiousness", return_int[2].toString() );
                XML_IO.set_value_user_info("login_info", "normalized_Neuroticism", return_int[3].toString() );
                XML_IO.set_value_user_info("login_info", "normalized_Openness", return_int[4].toString() );
                Log.e("temp", "normalized_Extraversion " + return_int[0].toString());
                Log.e("temp", "normalized_Openness " + return_int[4].toString());
            }


        }
        else
        {
            return_int[0] = Integer.parseInt(XML_IO.find_value_in_userxml("login_info", "normalized_Extraversion"));
            return_int[1] = Integer.parseInt(XML_IO.find_value_in_userxml("login_info", "normalized_Agreeableness"));
            return_int[2] = Integer.parseInt(XML_IO.find_value_in_userxml("login_info", "normalized_Conscientiousness"));
            return_int[3] = Integer.parseInt(XML_IO.find_value_in_userxml("login_info", "normalized_Neuroticism"));
            return_int[4] = Integer.parseInt(XML_IO.find_value_in_userxml("login_info", "normalized_Openness"));
            Log.e("temp", "normalized_Extraversion " + return_int[0].toString());
            Log.e("temp", "normalized_Openness " + return_int[4].toString());
        }

        return return_int;
    }

    public Integer[] get_object_rule_world_and_atribute_id(String input)
    {
        Integer[] return_array = {-1,-1, -1};
        Integer end_name = (input.indexOf("|"));
        String type_operator_is = input.substring(0, end_name);
        String name_atribute_is = input.substring((end_name + 1), input.length());
        // add u_info en u_person
        // k
        if(name_atribute_is.equals("distance"))
        {
            return_array[0] = -1;
            return_array[1] = -1;
            Integer[] xy_pos_target = xy_pos_id(target_field_id);
            Integer[] xy_pos_player = xy_pos_id(active_player_id);
            Integer distance = range_to(xy_pos_player[0], xy_pos_player[1], xy_pos_target[0], xy_pos_target[1]);
            return_array[2] = distance;
        }
        else if(type_operator_is.equals("v"))
        {
            return_array[0] = -1;
            return_array[1] = -1;
            return_array[2] = Integer.parseInt(name_atribute_is);
        }
        else if(type_operator_is.equals("u_info"))
        {

            return_array[0] = -1;
            return_array[1] = -1;
            Integer find_u_info = Integer.parseInt(XML_IO.find_value_in_userxml("login_info", name_atribute_is));
            return_array[2] = find_u_info;
        }
        else if(type_operator_is.equals("tag_world"))
        {
            Integer end_name_2 = (name_atribute_is.indexOf("|"));
            String world_tag = name_atribute_is.substring(0, end_name_2);
            String tag_name = name_atribute_is.substring((end_name_2 + 1), name_atribute_is.length());

            return_array[0] = -1;
            return_array[1] = -1;
            Integer tag_value = Integer.parseInt(XML_IO.find_value_in_userxml(world_tag, tag_name));
            return_array[2] = tag_value;
        }
        else if(type_operator_is.equals("u_person"))
        {
            return_array[0] = -1;
            return_array[1] = -1;
            if(name_atribute_is.equals("Extraversion"))
            {
                return_array[2] = get_user_personality()[0];
            }
            else if(name_atribute_is.equals("Agreeableness"))
            {
                return_array[2] = get_user_personality()[1];

            }
            else if(name_atribute_is.equals("Conscientiousness"))
            {
                return_array[2] = get_user_personality()[2];

            }
            else if(name_atribute_is.equals("Neuroticism"))
            {
                return_array[2] = get_user_personality()[3];

            }
            else if(name_atribute_is.equals("Openness"))
            {
                return_array[2] = get_user_personality()[4];
            }
            else
            {
                return_array[2] = 0;
            }

        }
        else
        {
            Integer rulebook_id = from_object_name_to_rule_number(type_operator_is);
            Integer atribute_nr = find_atribute_if_from_string(name_atribute_is);
            Integer field_id =from_name_to_id_field(type_operator_is);
            return_array[0] = rulebook_id;
            return_array[1] = field_id;
            return_array[2] = atribute_nr;
        }


        return return_array;
    }

    public ArrayList set_array_list_string(ArrayList input, Integer index, String value) {
        while (index >= input.size())
        {
            input.add(new ArrayList<>());
        }
        input.set(index, value);
        return input;
    }


    public Integer from_name_to_id_field(String name)
    {
        Integer return_id = -1;
        if(name.equals("pl"))
        {
            return_id = active_player_id;
        }
        if(name.equals("target"))
        {
            return_id = target_field_id;
        }
        Integer tel_names = 0;
        while(tel_names < field_ids_and_names.size())
        {
            String found_name = field_ids_and_names.get(tel_names).get(0);
            if(name.equals(found_name))
            {
                return_id = tel_names;
            }
            tel_names = tel_names +1;
        }

        return return_id;
    }

    public Integer from_object_name_to_rule_number(String name)
    {
        Integer return_id = -1;
        if(name.equals("pl"))
        {
            name = field_ids_and_names.get(active_player_id).get(0);
            return_id = 0;
        }
        if(name.equals("xx"))
        {
            return_id = -1;
        }
        if(name.equals("target"))
        {
            name = field_ids_and_names.get(target_field_id).get(0);
            return_id = 0;
        }
        Integer tel_usernames = 0;
        while(tel_usernames < username.size())
        {
            if(username.get(tel_usernames).equals(name))
            {
                return_id = 0;
            }
            tel_usernames = tel_usernames+1;
        }

        Integer tel_names = 0;
        while(tel_names < names_objects_id_sync.size())
        {
            String found_name = names_objects_id_sync.get(tel_names);
            if(name.equals(found_name))
            {
                return_id = tel_names;
            }
            tel_names = tel_names +1;
        }
        return return_id;
    }

    public String replace_brackets(String input)
    {
        String return_string = "";
        Integer total_chance = 0;
        ArrayList<Integer> chance_synchrome_id = new ArrayList<Integer>();
        ArrayList<Integer> atribute_synchrome_id = new ArrayList<Integer>();
        ArrayList<Integer> awnser_synchrome_id = new ArrayList<Integer>();
        Integer first_quote_inside_squarre = (input.indexOf("\""));
        Integer first_open_curve = input.indexOf("{");

        while(first_open_curve != -1)
        {
            first_quote_inside_squarre = (input.indexOf("\""));
            input = input.substring(first_quote_inside_squarre + 1);
            first_quote_inside_squarre = (input.indexOf("\""));

            Integer end_name = (input.indexOf("|"));
            String type_operator_is = input.substring(0, end_name);
            String name_atribute_is = input.substring((end_name+1), first_quote_inside_squarre);
            Integer atribute_id_temp_is = find_atribute_if_from_string(name_atribute_is);

            atribute_synchrome_id.add(atribute_id_temp_is);

            Integer id_found_object_field = from_name_to_id_field(type_operator_is);
            Integer id_found_object_rulebook =from_object_name_to_rule_number(type_operator_is);

            Integer total_atribute_value = id_def_atributs.get(id_found_object_rulebook).get(atribute_id_temp_is);
            Integer size = atribute_modifications.size();

            while(active_player_id >= size)
            {
                size = size+1;
                atribute_modifications.add(new ArrayList<ArrayList<ArrayList<Integer>>>());
            }

            Integer tel_modifications = 0;
            while(tel_modifications < atribute_modifications.get(active_player_id).size())
            {
                if(atribute_modifications.get(id_found_object_field).get(tel_modifications).size()>= atribute_id_temp_is)
                {
                    total_atribute_value = total_atribute_value + atribute_modifications.get(id_found_object_field).get(tel_modifications).get(atribute_id_temp_is).get(0);
                }
                tel_modifications = tel_modifications +1 ;
            }
            chance_synchrome_id.add( total_atribute_value);
            total_chance = total_chance +  total_atribute_value;


            first_open_curve = input.indexOf("{");
            Integer first_close_curve = input.indexOf("}");

            String inside_curve = input.substring((first_open_curve + 1), first_close_curve);
            Integer awnser_inside_curve = calculate_from_string(inside_curve);

            // input = input.substring(0, first_open_curve) + input.substring(first_close_curve + 1);

            awnser_synchrome_id.add(awnser_inside_curve);
            input = input.substring(first_close_curve + 1);

            first_open_curve = input.indexOf("{");
        }

        Random rand = new Random();
        int determained_random = rand.nextInt(total_chance);

        Integer count_tot_chances = 0;
        Integer tel_temp = 0;
        while(return_string.equals(""))
        {
            count_tot_chances = count_tot_chances + chance_synchrome_id.get(tel_temp);
            if(count_tot_chances > determained_random)
            {
                return_string = "\"v|"+awnser_synchrome_id.get(tel_temp).toString()+ "\"";

            }
            tel_temp = tel_temp +1;
            if(tel_temp >= (chance_synchrome_id.size()-1))
            {
                return_string = "\"v|"+awnser_synchrome_id.get(tel_temp).toString()+ "\"";
            }
        }

        return return_string;
    }

    public Integer calculate_from_string (String input)
    {
        Integer awnser = 0;
        Integer first_quote = input.indexOf("\"");

        Integer found_atribute_value = 0;
        String privious_opperator = "=";

        while (first_quote != -1)
        {
            first_quote = (input.indexOf("\""));
            input = input.substring(first_quote + 1);
            first_quote = (input.indexOf("\""));

            Integer atribute_id_temp = 0;
            Integer id_found_object_field = -1;
            Integer id_found_object_rulebook = 0;

            String type_operator;
            String name_atribute = null;
            boolean do_the_math = false;
            if(first_quote <=2)
            {
                type_operator = input.substring(0, 1);
            }
            else {
                Integer end_name = (input.indexOf("|"));
                type_operator = input.substring(0, end_name);
                name_atribute = input.substring((end_name + 1), first_quote);
                atribute_id_temp = find_atribute_if_from_string(name_atribute);

                id_found_object_field = from_name_to_id_field(type_operator);
                id_found_object_rulebook = from_object_name_to_rule_number(type_operator);
            }


            if(id_found_object_field > -1)
            {
/*
                Integer total_atribute_value = id_def_atributs.get(id_found_object_rulebook).get(atribute_id_temp);
                Integer size = atribute_modifications.size();
                while(id_found_object_field>= size)
                {
                    size = atribute_modifications.size();
                    atribute_modifications.add(new ArrayList<ArrayList<ArrayList<Integer>>>());
                }
                Integer tel_modifications = 0;
                while(tel_modifications < atribute_modifications.get(id_found_object_field).size())
                {
                    if(atribute_modifications.get(id_found_object_field).get(tel_modifications).size()> atribute_id_temp)
                    {
                        total_atribute_value = total_atribute_value + atribute_modifications.get(id_found_object_field).get(tel_modifications).get(atribute_id_temp).get(0);
                    }
                    tel_modifications = tel_modifications +1 ;
                }
                found_atribute_value = total_atribute_value;
                */

                found_atribute_value = count_atributs_mod_to_def(id_found_object_rulebook, id_found_object_field, atribute_id_temp);
                do_the_math = true;

            }
            else if(type_operator.equals("v"))
            {
                found_atribute_value = Integer.parseInt(name_atribute);
                // awnser = awnser + Integer.parseInt(name_atribute);
                do_the_math = true;
            }
            else if(type_operator.equals("xx"))
            {
                if(name_atribute.equals("distance"))
                {
                    Integer[] xy_pos_target = xy_pos_id(target_field_id);
                    Integer[] xy_pos_player = xy_pos_id(active_player_id);
                    Integer distance = range_to(xy_pos_player[0], xy_pos_player[1], xy_pos_target[0], xy_pos_target[0]);

                    awnser = distance;
                }
            }
            else
            {
                privious_opperator = type_operator;
            }

            if(do_the_math == true)
            {
                if(privious_opperator.equals("="))
                {
                    awnser = found_atribute_value;
                }
                else if(privious_opperator.equals("+"))
                {
                    awnser = awnser + found_atribute_value;
                }
                else if(privious_opperator.equals("-"))
                {
                    awnser = awnser - found_atribute_value;
                }

            }
            input = input.substring(first_quote + 1);
            first_quote = (input.indexOf("\""));
        }
        return awnser;
    }

    public Integer count_atributs_mod_to_def(Integer id_player_rulebook, Integer id_player_field, Integer id_atribute)
    {

        // if id_player_rulebook == -1 then return_total == id_atribute
        Integer return_total = 0;

        Integer total_atribute_value = id_def_atributs.get(id_player_rulebook).get(id_atribute);
        Integer size = atribute_modifications.size();
        while(id_player_field>= size)
        {
            size = atribute_modifications.size();
            atribute_modifications.add(new ArrayList<ArrayList<ArrayList<Integer>>>());
        }
        Integer tel_modifications = 0;
        while(tel_modifications < atribute_modifications.get(id_player_field).size())
        {
            if(atribute_modifications.get(id_player_field).get(tel_modifications).size()> id_atribute)
            {
                total_atribute_value = total_atribute_value + atribute_modifications.get(id_player_field).get(tel_modifications).get(id_atribute).get(0);
            }
            tel_modifications = tel_modifications +1 ;
        }
        return_total = total_atribute_value;
        return return_total;
    }

    public Integer find_atribute_if_from_string(String name_atribute)
    {
        Integer return_int =-1;
        Integer tel = 0;
        while(tel < world_atribute_names.size())
        {
            if( name_atribute.equals(world_atribute_names.get(tel)) )
            {
                return_int = tel;
            }
            tel = tel+1;
        }
        return return_int;
    }

    public void move_player(Integer x_to, Integer y_to, Integer x_from, Integer y_from)
    {
        if(field_atm_array[x_to][y_to][0] == 1|| field_atm_array[x_to][y_to][0] == 5) {
            field_atm_array[x_to][y_to][0] = field_atm_array[x_from][y_from][0];
            field_atm_array[x_from][y_from][0] = 1;

            String player_id_server = find_value_in_xml("login_info", "id");
            String string_to_send_to_testPHP = "qid="+player_id_server+"&qty=cha&qcx="+x_from+"&qcy="+y_from+"&qtgx="+x_to+"&qtgy="+y_to;
            String send_to_server_flag = find_value_in_xml("login_info", "flag_send_server");
            try {
                server_side_PHP.push_to_testphp(string_to_send_to_testPHP, send_to_server_flag);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        end_turn();
    }

    public void end_turn()
    {
        atribute_triggers();
        Integer tel_ids = 0;
        // ArrayList<Integer> speeds = new ArrayList<Integer>();
        Integer speed_atribute_id = find_atribute_if_from_string("Speed");
        Integer smallest_difference_time = -100;
        Integer new_active_player = -1;
        while(speed_atm.size() < field_ids_and_names.size())
        {
            speed_atm.add(0);
        }
        while(tel_ids < field_ids_and_names.size())
        {
            // speeds.add(-1);
            Integer type_id_atm = Integer.parseInt(field_ids_and_names.get(tel_ids).get(1));
            if(type_id_atm == 2 || type_id_atm == 3)
            {
                String name = field_ids_and_names.get(tel_ids).get(0);
                Integer rulebook_id = from_object_name_to_rule_number(name);
                Float temp_total_speed = Float.valueOf(count_atributs_mod_to_def(rulebook_id, tel_ids, speed_atribute_id));
                Float speed_found_inv_10000_f = (1 / temp_total_speed ) * 10000;
                Integer speed_found_inv_10000 = Math.round(speed_found_inv_10000_f);
                Integer difference_time = speed_found_inv_10000 - speed_atm.get(tel_ids);
                if(difference_time < smallest_difference_time || smallest_difference_time == -100)
                {
                    smallest_difference_time = difference_time;
                    new_active_player = tel_ids;
                }

                check_send_atributes(tel_ids.toString(), name);

            }

            tel_ids = tel_ids +1;
        }

        Integer tel_speed = 0;
        while(tel_speed < speed_atm.size())
        {
            Integer new_speed = speed_atm.get(tel_speed) + smallest_difference_time ;
            speed_atm.set(tel_speed, new_speed);
            tel_speed = tel_speed +1 ;
        }
        speed_atm.set(new_active_player, 0);

        LinearLayout lin_lay_q = (LinearLayout)findViewById(R.id.linearLayout_questuinnaire_vert);
        lin_lay_q.removeAllViews();

        active_player_id = new_active_player;


        if(field_ids_and_names.get(new_active_player).get(1).equals("2"))
        {

            draw_field_squarres();

            TextView active_player_name_tv = new TextView(this);
            active_player_name_tv.setId(299);
            active_player_name_tv.setTextSize(font_size);
//        add_target_atribute.setsc

            active_player_name_tv.setText("Active Player: "+field_ids_and_names.get(new_active_player).get(0));
            lin_lay_q.addView(active_player_name_tv);
        }
        else
        {
            enemy_turn();
        }
        Boolean temp = find_if_enemy_left();
    }
    public Boolean find_if_players_left()
    {
        Boolean return_b = false;

        Integer tel_ids = 0;
        while(tel_ids < field_ids_and_names.size())
        {
            Integer type_id_atm = Integer.parseInt(field_ids_and_names.get(tel_ids).get(1));

            // if(type_id_atm == 2 || type_id_atm == 3)
            if(type_id_atm == 2 )
            {
                return_b = true;
            }
            tel_ids = tel_ids+1;
        }

        return return_b;
    }

    public Boolean find_if_enemy_left()
    {
        Boolean return_b = false;

        Integer tel_ids = 0;
        while(tel_ids < field_ids_and_names.size())
        {
            Integer type_id_atm = Integer.parseInt(field_ids_and_names.get(tel_ids).get(1));

            // if(type_id_atm == 2 || type_id_atm == 3)
            if(type_id_atm == 3)
            {
                // l
                return_b = true;
            }
            tel_ids = tel_ids+1;
        }
        if(return_b == false)
        {
            Integer no_enemy_left_tel = 0;
            Boolean stop = false;
            while(no_enemy_left_tel < no_enemy_left_trigger.size() && stop == false)
            {
                if(no_enemy_left_trigger.get(no_enemy_left_tel).get(0).equals("goto"))
                {
                    XML_ini_q_or_map(no_enemy_left_trigger.get(no_enemy_left_tel).get(2), no_enemy_left_trigger.get(no_enemy_left_tel).get(1));
                    stop = true;

                }

                no_enemy_left_tel = no_enemy_left_tel+1;
            }

        }

        return return_b;
    }

    public static Bitmap draw_squarre(Integer field_x, Integer field_y, String color_given, Bitmap field_bmp, Integer squarre_size) {
//        imageView img_view_temp = (imageView)findViewById(R.id.drawfield_id);
//        Bitmap field_bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        Integer start_x = field_x * (squarre_size + 1);
        Integer start_y = field_y * (squarre_size + 1);

        // String[] color_squarre = map_preset_res.set_map_colors();
        int color_sqre = Color.parseColor(color_given);

        Integer tel_x = 0;
        while (tel_x < squarre_size) {
            Integer tel_y = 0;
            while (tel_y < squarre_size) {
                // Log.e("MAP", "field_x " + field_x+" tel_x "+tel_x);
                field_bmp.setPixel(start_x + tel_x, start_y + tel_y, color_sqre);

                tel_y = tel_y + 1;
            }
            tel_x = tel_x + 1;
        }
        return field_bmp;
    }

    public Integer range_to(Integer x_ini, Integer y_ini, Integer x_target, Integer y_target)
    {
        Integer difference_x_abs = Math.abs(x_ini - x_target);
        Integer difference_y_abs = Math.abs(y_ini - y_target);
        Integer return_is_next = difference_x_abs + difference_y_abs;
        return return_is_next;
    }

    public Integer[] xy_pos_id(Integer id_to_find)
    {
        Integer[] return_xy = {0,0,0};
        Integer tel_x = 0;
        Integer tel_y = 0;
        while (tel_x<field_atm_array.length)
        {
            tel_y = 0;
            while(tel_y<field_atm_array[tel_x].length)
            {
                Integer id_ofcell_type = field_atm_array[tel_x][tel_y][0];
                if(id_ofcell_type == id_to_find)
                {
                    return_xy[0] = tel_x;
                    return_xy[1] = tel_y;
                    return_xy[2] = 1;
                }

                tel_y = tel_y +1;
            }
            tel_x = tel_x +1;
        }
        return return_xy;
    }

    public void read_map_rules()
    {
        Document map_rules = null;
        try {
            map_rules = XML_IO.open_document_xml(this_world + "_map_rules");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        NodeList atribute_nodelist = map_rules.getElementsByTagName("atribute");
        Integer tel_atributes =0;

        names_objects_id_sync.add("Player");
        id_def_atributs.add( new ArrayList() );

        while(tel_atributes < atribute_nodelist.getLength())
        {
            NamedNodeMap temp_atribut = atribute_nodelist.item(tel_atributes).getAttributes();
            String atribute_name_temp = temp_atribut.getNamedItem("name").getTextContent();
            String atribute_user_def_temp = temp_atribut.getNamedItem("user_def").getTextContent();
            // String atribute_enemy_def_temp = temp_atribut.getNamedItem("enemy_def").getTextContent();
            world_atribute_names.add(atribute_name_temp);

            id_def_atributs.get(0).add(Integer.parseInt(atribute_user_def_temp));

            tel_atributes = tel_atributes+1;
        }


        NodeList moveable_objects_nl = map_rules.getElementsByTagName("moveable_object");
        Integer tel_moveable_objects =0;
        while(tel_moveable_objects < moveable_objects_nl.getLength())
        {
            NamedNodeMap temp_atributes = moveable_objects_nl.item(tel_moveable_objects).getAttributes();
            String name_movable_object = temp_atributes.getNamedItem("name").getTextContent();

            names_objects_id_sync.add(name_movable_object);
            id_def_atributs.add(new ArrayList());
            Integer id_obj_atm = (names_objects_id_sync.size() -1);

            Element moveable_object_element_atm = (Element) moveable_objects_nl.item(tel_moveable_objects);
            NodeList atribute_def = moveable_object_element_atm.getElementsByTagName("atribute_def");

            Integer tel_atributes_def = 0;
            while(tel_atributes_def < atribute_def.getLength())
            {
                NamedNodeMap temp_atributes_inside = atribute_def.item(tel_atributes_def).getAttributes();
                String atribute_def_name = temp_atributes_inside.getNamedItem("name").getTextContent();
                String devault_value = temp_atributes_inside.getNamedItem("enemy_def").getTextContent();
                Integer id_atribute_def = find_atribute_if_from_string(atribute_def_name);

                id_def_atributs.get(id_obj_atm).add(id_atribute_def, Integer.parseInt(devault_value));

                tel_atributes_def = tel_atributes_def+1;
            }


            tel_moveable_objects = tel_moveable_objects+1;
        }


        NodeList enemy_interactions_nl = map_rules.getElementsByTagName("Enemy_interaction");
        Integer tel_enemy_interaction =0;
        while(tel_enemy_interaction < enemy_interactions_nl.getLength())
        {
            NamedNodeMap temp_atribut = enemy_interactions_nl.item(tel_enemy_interaction).getAttributes();
            String enemy_interactions_name_temp = temp_atribut.getNamedItem("name").getTextContent();
            String enemy_interaction_req_name_temp = temp_atribut.getNamedItem("interaction_with").getTextContent();
            // enemy_interaction_req_name.add(enemy_interaction_req_name_temp);

            // req_interaction_glob.add(new ArrayList());
            // Integer arraylist_req_atm = (req_interaction_glob.size() - 1);

            enemy_interaction.add(new ArrayList());
            Integer arraylist_atm = (enemy_interaction.size() - 1);
            enemy_interaction.get(arraylist_atm).add(new ArrayList());
            enemy_interaction.get(arraylist_atm).get(0).add(enemy_interactions_name_temp);

            Element interaction_element_atm = (Element) enemy_interactions_nl.item(tel_enemy_interaction);
            NodeList math_interaction = interaction_element_atm.getElementsByTagName("math");

            enemy_interaction.get(arraylist_atm).add(new ArrayList());
            Integer tel_math_problems = 0;
            while (tel_math_problems < math_interaction.getLength())
            {
                String math_problem_s = math_interaction.item(tel_math_problems).getTextContent();
                enemy_interaction.get(arraylist_atm).get(1).add(math_problem_s);
                tel_math_problems = tel_math_problems+1;
            }

            enemy_interaction.get(arraylist_atm).add(new ArrayList());
            Element req_interaction_element_atm = (Element) enemy_interactions_nl.item(tel_enemy_interaction);
            NodeList req_interaction_nl = req_interaction_element_atm.getElementsByTagName("req");
            if(req_interaction_nl.getLength() > 0)
            {
                NamedNodeMap temp_atribut_req = req_interaction_nl.item(0).getAttributes();
                Integer reference_req = add_req_tag(temp_atribut_req);
                enemy_interaction.get(arraylist_atm).get(2).add(reference_req.toString());
            }

            enemy_interaction.get(arraylist_atm).add(new ArrayList());
            enemy_interaction.get(arraylist_atm).get(3).add(enemy_interaction_req_name_temp);


            tel_enemy_interaction = tel_enemy_interaction+1;
        }

        NodeList atribute_trigger_nl = map_rules.getElementsByTagName("atribute_trigger");
        Integer tel_atribute_trigger =0;
        while(tel_atribute_trigger < atribute_trigger_nl.getLength())
        {
            NamedNodeMap temp_atribut = atribute_trigger_nl.item(tel_atribute_trigger).getAttributes();
            String atribute_trigger_name_temp = temp_atribut.getNamedItem("name").getTextContent();

            atribute_trigger.add(new ArrayList());
            Integer arraylist_atm = (atribute_trigger.size() - 1);

            req_atribute_trigger_glob.add(new ArrayList<String>());
            Integer array_list_req_atm = (req_atribute_trigger_glob.size() -1);

            atribute_trigger.get(arraylist_atm).add(atribute_trigger_name_temp);

            Integer tel_req = 0;
            Element atribute_trigger_e = (Element) atribute_trigger_nl.item(tel_atribute_trigger);
            NodeList req_nl = atribute_trigger_e.getElementsByTagName("req_enemy_move");
            while(tel_req < req_nl.getLength())
            {
                NamedNodeMap temp_atribute_2 = req_nl.item(tel_req).getAttributes();

                String req_name = temp_atribute_2.getNamedItem("req_name").getTextContent();
                String req_type = temp_atribute_2.getNamedItem("req_type").getTextContent();
                String req_then_name = temp_atribute_2.getNamedItem("req_then_name").getTextContent();
                Node extra_eq_n = temp_atribute_2.getNamedItem("extra_eq");

                String extra_eq = "f";
                if (extra_eq_n == null) {
                    extra_eq = "f";
                } else {
                    if (extra_eq_n.getTextContent().equals("t")) {
                        extra_eq = "t";
                    }
                }

                req_atribute_trigger_glob.get(array_list_req_atm).add("req_interaction");
                req_atribute_trigger_glob.get(array_list_req_atm).add(req_name);
                req_atribute_trigger_glob.get(array_list_req_atm).add(req_type);
                req_atribute_trigger_glob.get(array_list_req_atm).add(req_then_name);
                req_atribute_trigger_glob.get(array_list_req_atm).add(extra_eq);

                tel_req =tel_req +1;
            }
            NodeList remove_object = atribute_trigger_e.getElementsByTagName("remove_object");
            if(remove_object.getLength() > 0)
            {
                NamedNodeMap temp_atribute_2 = remove_object.item(0).getAttributes();
                atribute_trigger.get(arraylist_atm).add("remove");
            }

            tel_atribute_trigger = tel_atribute_trigger+1;
        }

        NodeList enemy_turn_activity = map_rules.getElementsByTagName("Enemy_turn");
        Integer tel_enemy_turn_activitys = 0;
        while(tel_enemy_turn_activitys < enemy_turn_activity.getLength())
        {
            enemy_turn.add(new ArrayList());
            req_enemy_turn_glob.add(new ArrayList());
            Integer array_list_atm = (enemy_turn.size() - 1 );

            Node activity_atm_node = enemy_turn_activity.item(tel_enemy_turn_activitys);
            Element activity_atm_element = (Element) activity_atm_node;


            NamedNodeMap temp_atribut = activity_atm_node.getAttributes();
            String enemy_turn_activity_name = temp_atribut.getNamedItem("name").getTextContent();
            String enemy_turn_activity_name_enemy = temp_atribut.getNamedItem("name_enemy").getTextContent();
            String enemy_turn_activity_priority = temp_atribut.getNamedItem("priority").getTextContent();

            enemy_turn.get(array_list_atm).add(enemy_turn_activity_name);
            enemy_turn.get(array_list_atm).add(enemy_turn_activity_name_enemy);
            enemy_turn.get(array_list_atm).add(enemy_turn_activity_priority);

            // MOVE ENEMY INI
            NodeList found_move = activity_atm_element.getElementsByTagName("move");
            if(found_move.getLength() > 0)
            {
                NamedNodeMap temp_atribut2 = found_move.item(0).getAttributes();
                enemy_turn.get(array_list_atm).add("move");
                enemy_turn.get(array_list_atm).add(temp_atribut2.getNamedItem("move_id").getTextContent());
                enemy_turn.get(array_list_atm).add(temp_atribut2.getNamedItem("move_to").getTextContent());
            }

            // Math ENEMY INI
            NodeList found_math = activity_atm_element.getElementsByTagName("math");
            if(found_math.getLength() > 0)
            {
                String math_problem_s = found_math.item(0).getTextContent();

                // NamedNodeMap temp_atribut2 = found_move.item(0).getAttributes();

                enemy_turn.get(array_list_atm).add("interact");
                enemy_turn.get(array_list_atm).add(math_problem_s);

                NamedNodeMap temp_atribut2 = found_math.item(0).getAttributes();
                enemy_turn.get(array_list_atm).add(temp_atribut2.getNamedItem("target").getTextContent());
            }

            // NOT SPECIFIC FOR MOVE.
            NodeList found_reg = activity_atm_element.getElementsByTagName("req_enemy_move");
            if(found_reg.getLength() > 0)
            {
                NamedNodeMap temp_atribut2 = found_reg.item(0).getAttributes();

                Node extra_eq_n = temp_atribut2.getNamedItem("extra_eq");
                String extra_eq = "f";
                if (extra_eq_n == null) {
                    extra_eq = "f";
                } else {
                    if (extra_eq_n.getTextContent().equals("t")) {

                        extra_eq = "t";
                    }
                }
                req_enemy_turn_glob.get(array_list_atm).add("req_move");
                req_enemy_turn_glob.get(array_list_atm).add(temp_atribut2.getNamedItem("req_name").getTextContent());
                req_enemy_turn_glob.get(array_list_atm).add(temp_atribut2.getNamedItem("req_type").getTextContent());
                req_enemy_turn_glob.get(array_list_atm).add(temp_atribut2.getNamedItem("req_then_name").getTextContent());
                req_enemy_turn_glob.get(array_list_atm).add(extra_eq);
            }


            tel_enemy_turn_activitys = tel_enemy_turn_activitys +1 ;
        }



    }
    public Integer add_req_tag(NamedNodeMap temp_atribute_2)
    {
        String req_name = temp_atribute_2.getNamedItem("req_name").getTextContent();
        String req_type = temp_atribute_2.getNamedItem("req_type").getTextContent();
        String req_then_name = temp_atribute_2.getNamedItem("req_then_name").getTextContent();
        Node extra_eq_node = temp_atribute_2.getNamedItem("extra_eq");
        String extra_eq = "f";
        if(extra_eq_node != null)
        {
            extra_eq = extra_eq_node.getTextContent();
        }

        Node must_node = temp_atribute_2.getNamedItem("must");
        String must = "f";
        if(must_node != null)
        {
            must = must_node.getTextContent();
        }

        Integer return_interger = 0;
        requerment_normalized.add(new ArrayList<String>());
        return_interger = requerment_normalized.size()-1;
        requerment_normalized.get(return_interger).add(req_name);
        requerment_normalized.get(return_interger).add(req_type);
        requerment_normalized.get(return_interger).add(req_then_name);
        requerment_normalized.get(return_interger).add(extra_eq);
        requerment_normalized.get(return_interger).add(must);

        return return_interger;
    }

/*
    public void set_closest_player_to_target()
    {
        Integer smallest_id = -1;
        Integer smallest_distance = -1;
        Integer tel_field_ids = 0;
        while (tel_field_ids < field_ids_and_names.size() )
        {
            if(field_ids_and_names.get(tel_field_ids).get(1).equals("2"))
            {
                Integer[] xy_pos_target = xy_pos_id(target_field_id);
                Integer[] xy_pos_player = xy_pos_id(active_player_id);
                Integer distance = range_to(xy_pos_player[0], xy_pos_player[1], xy_pos_target[0], xy_pos_target[0]);
                if(smallest_distance == -1 || distance < smallest_distance)
                {
                    smallest_id = tel_field_ids;
                }
            }
            tel_field_ids = tel_field_ids+1;
        }
        target_field_id = smallest_id;
    }
*/
    public void enemy_turn()
    {
        Integer tel_move_options = 0;
        String name_active_player = field_ids_and_names.get(active_player_id).get(0);
        ArrayList<Integer> id_action_prio_sync = new ArrayList<Integer>();
        ArrayList<Integer> prio_sync = new ArrayList<Integer>();
        Boolean atleast_found_one = false;

        set_target_to_closest_target();

        while (tel_move_options < enemy_turn.size())
        {
            if(enemy_turn.get(tel_move_options).get(1).equals(name_active_player))
            {
                Boolean req_pass = true;
                if(req_enemy_turn_glob.get(tel_move_options).size() > 0)
                {

                    String req_name = req_enemy_turn_glob.get(tel_move_options).get(1);
                    String req_type = req_enemy_turn_glob.get(tel_move_options).get(2);
                    String req_then_name = req_enemy_turn_glob.get(tel_move_options).get(3);
                    String eq_true_s = req_enemy_turn_glob.get(tel_move_options).get(4);

                    Integer[] req_value1_array = get_object_rule_world_and_atribute_id(req_name);
                    Integer value1 = 0;
                    if(req_value1_array[0] == -1)
                    {
                        value1 = req_value1_array[2];
                    }
                    else
                    {
                        value1 = count_atributs_mod_to_def(req_value1_array[0], req_value1_array[1], req_value1_array[2]);
                    }

                    Integer[] req_then_array = get_object_rule_world_and_atribute_id(req_then_name);
                    Integer value_then = 0;
                    if(req_then_array[0] == -1)
                    {
                        value_then = req_then_array[2];
                    }
                    else
                    {
                        value_then = count_atributs_mod_to_def(req_then_array[0], req_then_array[1], req_then_array[2]);
                    }

                    Boolean equals_true = false;
                    if(eq_true_s.equals("t"))
                    {
                        equals_true = true;
                    }

                    req_pass = check_req_type(value1.toString(), req_type, value_then.toString(), null, null, equals_true);

                }

                if(req_pass == true)
                {
                    Integer priority = Integer.parseInt(enemy_turn.get(tel_move_options).get(2));
                    prio_sync.add(priority);
                    id_action_prio_sync.add(tel_move_options);
                    atleast_found_one = true;
                }
            }
            tel_move_options = tel_move_options +1;
        }
        if(atleast_found_one == true)
        {
            Integer temp_found_prio = find_highest_prio(prio_sync);
            Integer move_id_choosen = id_action_prio_sync.get(temp_found_prio);
            String type_activity = enemy_turn.get(move_id_choosen).get(3);
            if(type_activity.equals("move"))
            {
                String move_id = enemy_turn.get(move_id_choosen).get(4);
                String move_to = enemy_turn.get(move_id_choosen).get(5);
                if(move_to.equals("clossest_player"))
                {
                    Integer[] active_player_xy = xy_pos_id(active_player_id);
                    Integer[] move_to_found = get_firststep_to_player(active_player_xy[0], active_player_xy[1]);
                    move_player(move_to_found[0], move_to_found[1], active_player_xy[0], active_player_xy[1]);

                }
            }
            else if(type_activity.equals("interact"))
            {
                Integer tel_math_problems = 0;
                Integer offset_tel = 2;
                Integer multiplier = 3;
                Integer start_item_value_ATM_tel = (tel_math_problems * multiplier) + offset_tel;
                while(start_item_value_ATM_tel < (enemy_turn.get(move_id_choosen).size() -1))
                {
                    String target_s = enemy_turn.get(move_id_choosen).get(start_item_value_ATM_tel+3);

                    if (target_s.equals("closest player"))
                    {
                        String math_problem = enemy_turn.get(move_id_choosen).get(start_item_value_ATM_tel +2);
                        aply_math_to_interaction(math_problem);
                    }
                    end_turn();

                    tel_math_problems = tel_math_problems +1;
                    start_item_value_ATM_tel = (tel_math_problems * multiplier) + offset_tel;
                }
            }
        }
        else
        {
            end_turn();
            //enemy_turn();
        }
    }

    public void set_target_to_closest_target()
    {
        Integer smallest_id = -1;
        Integer smallest_distance = -1;
        Integer tel_field_ids = 0;

        while (tel_field_ids < field_ids_and_names.size() )
        {
            if(field_ids_and_names.get(tel_field_ids).get(1).equals("2"))
            {
                Integer[] xy_pos_target = xy_pos_id(tel_field_ids);
                Integer[] xy_pos_player = xy_pos_id(active_player_id);
                Integer distance = range_to(xy_pos_player[0], xy_pos_player[1], xy_pos_target[0], xy_pos_target[0]);
                if(smallest_distance == -1 || distance < smallest_distance)
                {
                    smallest_distance = distance;
                    smallest_id = tel_field_ids;
                }
            }
            tel_field_ids = tel_field_ids+1;
        }
        target_field_id = smallest_id;
    }

    public Integer[] get_firststep_to_player(Integer x_pos_start, Integer y_pos_start)
    {
        Integer[] return_firststep = {-1, -1};



        // ini field_to_calculate_shortest_path_array
        Integer tel_x = 0;
        while (tel_x < field_to_calculate_shortest_path_array.length) {
            Integer tel_y = 0;
            while (tel_y < field_to_calculate_shortest_path_array[0].length)
            {
                field_to_calculate_shortest_path_array[tel_x][tel_y] = -1;
                tel_y = tel_y + 1;
            }
            tel_x = tel_x + 1;
        }

        Integer x_length_field = field_to_calculate_shortest_path_array.length;
        Integer y_length_field = field_to_calculate_shortest_path_array[0].length;
        Integer top_walk_steps = -1;
        Integer bot_walk_steps = -1;
        Integer left_walk_steps = -1;
        Integer right_walk_steps = -1;
        Integer[] smallest_steps_and_id = {-1,-1};
        smallest_steps_found = -1;

        if ((x_pos_start - 1) > -1)
        {
            left_walk_steps = calculate_shortest_path( (x_pos_start-1), y_pos_start, 0);
            if(left_walk_steps > -1)
            {
                smallest_steps_and_id[0] = left_walk_steps;
                return_firststep[0] = (x_pos_start-1);
                return_firststep[1] = y_pos_start;
            }
            // x-1
        }

        if ((x_pos_start + 1) < x_length_field)
        {
            right_walk_steps = calculate_shortest_path( (x_pos_start+1), y_pos_start, 0);
            if(right_walk_steps > -1) {
                if (right_walk_steps < smallest_steps_and_id[0] || smallest_steps_and_id[0] == -1) {
                    smallest_steps_and_id[0] = right_walk_steps;
                    return_firststep[0] = (x_pos_start + 1);
                    return_firststep[1] = y_pos_start;

                }
            }
            // x+1
        }
        if ((y_pos_start - 1) > -1) {
            top_walk_steps = calculate_shortest_path( x_pos_start, (y_pos_start -1), 0);
            if(top_walk_steps > -1) {
                if (top_walk_steps < smallest_steps_and_id[0] || smallest_steps_and_id[0] == -1) {
                    smallest_steps_and_id[0] = top_walk_steps;
                    return_firststep[0] = x_pos_start;
                    return_firststep[1] = (y_pos_start - 1);
                }
            }
            //y-1
        }
        if ((y_pos_start + 1) < y_length_field) {
            bot_walk_steps = calculate_shortest_path( x_pos_start, (y_pos_start +1), 0);
            if(bot_walk_steps > -1) {
                if (bot_walk_steps < smallest_steps_and_id[0] || smallest_steps_and_id[0] == -1) {
                    smallest_steps_and_id[0] = bot_walk_steps;
                    return_firststep[0] = x_pos_start;
                    return_firststep[1] = (y_pos_start + 1);
                }
            }
            //y+1
        }

        return return_firststep;
    }
    public Integer calculate_shortest_path(Integer x_pos_start, Integer y_pos_start, Integer steps_taken)
    {
        Integer found_player_range = -1;
        steps_taken = steps_taken +1;

        if(steps_taken < field_to_calculate_shortest_path_array[x_pos_start][y_pos_start] || field_to_calculate_shortest_path_array[x_pos_start][y_pos_start] == -1)
        {


            if(field_atm_array[x_pos_start][y_pos_start][0] != null) {
                Integer id_field_found = Integer.parseInt(field_ids_and_names.get(field_atm_array[x_pos_start][y_pos_start][0]).get(1));

                Integer x_length_field = field_to_calculate_shortest_path_array.length;
                Integer y_length_field = field_to_calculate_shortest_path_array[0].length;

                if (id_field_found == 1 || id_field_found == 5) {
                    field_to_calculate_shortest_path_array[x_pos_start][y_pos_start] = steps_taken;
                    if ((x_pos_start - 1) > -1) {
                        calculate_shortest_path((x_pos_start - 1), y_pos_start, steps_taken);
                        // x-1
                    }

                    if ((x_pos_start + 1) < x_length_field) {
                        calculate_shortest_path((x_pos_start + 1), y_pos_start, steps_taken);
                        // x+1
                    }
                    if ((y_pos_start - 1) > -1) {
                        calculate_shortest_path(x_pos_start, (y_pos_start - 1), steps_taken);
                        //y-1
                    }
                    if ((y_pos_start + 1) < y_length_field) {
                        calculate_shortest_path(x_pos_start, (y_pos_start + 1), steps_taken);
                        //y+1
                    }
                } else if (id_field_found == 2) {
                    if (smallest_steps_found == -1 || smallest_steps_found > steps_taken) {
                        smallest_steps_found = steps_taken;
                    }

                }
            }
        }

        return smallest_steps_found;
    }
/*
    public interface AsyncResponse {
        void processFinish(Integer[] output);
    }

*/
    public void run_sound(View v)
    {
        Log.e("result", "start");
        new record_voice.time_counter().execute();
        Log.e("result", "end");

    }


    public void sound_record_no(View v)
    {
        sound_record_initiate("NO", 0, 0);
    }

    public void sound_record_yes(View v)
    {
        sound_record_initiate("Yes", 1, 1);
/*
        text_to_speak.speak("say yes", TextToSpeech.QUEUE_FLUSH, null);
        Integer[] found_yes_parameters = record_voice.record_sound();

        Integer retry = 3;
        if(found_yes_parameters[7] == 0)
        {
            if(retry >0 )
            {
                found_yes_parameters = record_voice.record_sound();
                retry = retry-1;
            }
            else
            {
                text_to_speak.speak("I did not hear your, hit the button to try again", TextToSpeech.QUEUE_FLUSH, null);
            }
        }

        if(found_yes_parameters[7] > 0)
        {
            Integer tel = 0;
            while(tel < 12)
            {
                text_to_speak.speak("Thank you", TextToSpeech.QUEUE_FLUSH, null);
                XML_IO.set_value_user_info("recorded_yes", "yes" + tel.toString(), found_yes_parameters[tel].toString());
                tel = tel +1;
            }

        }
        */

    }

    public void sound_record_initiate(String word_input, Integer action, Integer overwrite )
    {
        text_to_speak.speak("say "+word_input, TextToSpeech.QUEUE_FLUSH, null);
        Integer[] found_parameters = record_voice.record_sound();

        Integer retry = 3;
        if(found_parameters[7] == 0)
        {
            if(retry >0 )
            {
                found_parameters = record_voice.record_sound();
                retry = retry-1;
            }
            else
            {
                text_to_speak.speak("I did not hear your, hit the button to try again", TextToSpeech.QUEUE_FLUSH, null);
            }
        }
        Log.e("test", "sound "+word_input+" number "+overwrite);
        if(found_parameters[7] > 0)
        {
            text_to_speak.speak("Thank you", TextToSpeech.QUEUE_FLUSH, null);
            // Integer nr_recorded_words = 0;
            Integer nr_recorded_words = 0;
            String test_snr_recorded_words =  XML_IO.find_value_in_userxml("recorded", "total_nr");
            Log.e("test", "sound nr_recorded_words "+test_snr_recorded_words);
            if(test_snr_recorded_words != null)
            {
                nr_recorded_words = Integer.valueOf(test_snr_recorded_words);
            }

            if(overwrite == null)
            {
                // nr_recorded_words;
                if (nr_recorded_words == null) {
                    nr_recorded_words = 0;
                }
                nr_recorded_words = nr_recorded_words +1;
                XML_IO.set_value_user_info("recorded", "total_nr", nr_recorded_words.toString());
                nr_recorded_words = nr_recorded_words -1;
            }
            else
            {
                if(nr_recorded_words < overwrite){
                    // PROBLEM
                }

                if(nr_recorded_words < overwrite)
                {
                    XML_IO.set_value_user_info("recorded", "total_nr", overwrite.toString());
                }

                nr_recorded_words = overwrite;

            }

            XML_IO.set_value_user_info("recorded_" + nr_recorded_words, "sound", word_input);
            XML_IO.set_value_user_info("recorded_" + nr_recorded_words, "action", action.toString());

            Integer tel = 0;
            while(tel < 12)
            {
                //System.out.println("tel "+tel+" value"+found_parameters[tel].toString());
                Log.e("temp", "tel " + tel + " value" + found_parameters[tel].toString());
                XML_IO.set_value_user_info("recorded_" + nr_recorded_words, "i" + tel.toString(), found_parameters[tel].toString());
                tel = tel +1;
            }



        }

    }



    public void check_spoken(View v)
    {
        text_to_speak.speak("Yes?", TextToSpeech.QUEUE_FLUSH, null);
        Integer[] spoken = record_voice.record_sound();
        // int value_return = 0;

        // Integer number_of_sounds = 2;
        Integer sound_parameters = 12;

        // loop throug known sounds TODO.
        Integer nr_recorded_words = 0 ;

        String test_snr_recorded_words =  XML_IO.find_value_in_userxml("recorded", "total_nr");
        if(test_snr_recorded_words != null)
        {
            nr_recorded_words = Integer.valueOf(test_snr_recorded_words) +1;
        }

        //Integer nr_recorded_words = Integer.valueOf(XML_IO.find_value_in_userxml("recorded", "total_nr"));
        // Log.e("test", "sound nr_recorded_words "+nr_recorded_words);
        // String find_set_value = "yes";
        Integer tel_word_nr = 0;
        Integer tel_sound_id = 0;

        Integer known_sounds[][] = new Integer[nr_recorded_words][sound_parameters+1];
        while(tel_word_nr < nr_recorded_words) {
            tel_sound_id = 0;

            String testy = XML_IO.find_value_in_userxml("recorded_" + tel_word_nr, "sound");
            Log.e("temp", "testy " + testy);

            while (tel_sound_id < sound_parameters) {
                String known_sound_s = XML_IO.find_value_in_userxml("recorded_" + tel_word_nr, "i" + tel_sound_id.toString());
                if(known_sound_s == null)
                {
                    known_sound_s = "0";
                }
                // Log.e("temp", "tel " + tel_sound_id+" :"+known_sound_s);

                known_sounds[tel_word_nr][tel_sound_id] = Integer.valueOf(known_sound_s);
                tel_sound_id = tel_sound_id + 1;
            }

            tel_word_nr = tel_word_nr +1;
        }

        Integer tel_unique_mod = 0;
        Integer distance_to_spoken[][] = new Integer[nr_recorded_words][sound_parameters+1];
        Integer distance_to_spoken_tot[] = new Integer[nr_recorded_words];
        Integer distance_tot_min = 0;
        Integer distance_tot_max = 0;
        Integer distance_tot_avg = 0;

        Integer distance_tot_min_nr = 0;
        Integer distance_tot_max_nr = 0;

        // use unique_mod to increase the importance of a variable. Must be > 1 preferably whole numbers
        Integer unique_mod[] =  new Integer[sound_parameters+1];

        // home made info
        unique_mod[0] = 40;
        unique_mod[1] = 20;
        unique_mod[2] = 160;
        unique_mod[3] = 40;
        unique_mod[4] = 1;
        unique_mod[5] = 80;
        unique_mod[6] = 160;
        unique_mod[7] = 40;
        unique_mod[8] = 1;
        unique_mod[9] = 160;
        unique_mod[10] = 1;
        unique_mod[11] = 160;


        // uniqueness moddifier and distance of spoke sound to known sound calculation

        while (tel_unique_mod < 12)
        {
            Integer average_dist_temp = 0;
            Integer dist_temp = 0;
            Integer dist_temp_abs = 0;
            Integer min_temp = null;
            Integer max_temp = null;
            Integer tel_sound_unique = 0;
            // Log.e("temp", "tel " + tel_unique_mod + " value" + spoken[tel_unique_mod].toString());
            while(tel_sound_unique<nr_recorded_words)
            {
                Integer mod_atm = known_sounds[tel_sound_unique][tel_unique_mod];
                dist_temp = spoken[tel_unique_mod] - mod_atm;
                dist_temp_abs = Math.abs(spoken[tel_unique_mod] - mod_atm);
                distance_to_spoken[tel_sound_unique][tel_unique_mod] = dist_temp;

                average_dist_temp = average_dist_temp + dist_temp_abs;
                // abs!!?

                if( min_temp == null || mod_atm < min_temp)
                {
                    min_temp = mod_atm;
                }
                if( max_temp == null || mod_atm > max_temp)
                {
                    max_temp = mod_atm;
                }

                tel_sound_unique++;
            }

            // check of spoken geen extreem is. CHECK ABS VALUE OR RELATIVE TO SPOKEN

            if(spoken[tel_unique_mod] > max_temp)
            {
                max_temp = spoken[tel_unique_mod];
            }
            if(spoken[tel_unique_mod] < min_temp)
            {
                min_temp = spoken[tel_unique_mod];
            }


            average_dist_temp = average_dist_temp / nr_recorded_words;
            Integer dif_range = max_temp - min_temp;

            if(unique_mod[tel_unique_mod] == null || unique_mod[tel_unique_mod] < 1)
            {
                unique_mod[tel_unique_mod] = 1;
            }
            // Double unique_mod_temp_double = ((double) average_dist_temp / (double)( dif_range * (24 * nr_recorded_words )));
            Double unique_mod_temp_double = ( (double)(  (24 * nr_recorded_words )) / (double) dif_range );

            // KLOPT NIET dif_range

            if(dif_range == 0)
            {
                unique_mod_temp_double = 0.0;
            }
            tel_sound_unique = 0;
            while(tel_sound_unique<nr_recorded_words)
            {
                Double temp = ((distance_to_spoken[tel_sound_unique][tel_unique_mod] * unique_mod_temp_double) * unique_mod[tel_unique_mod]);
                distance_to_spoken[tel_sound_unique][tel_unique_mod] = temp.intValue();
                if(distance_to_spoken_tot[tel_sound_unique] == null)
                {
                    distance_to_spoken_tot[tel_sound_unique] = 0;
                }
                distance_to_spoken_tot[tel_sound_unique] = distance_to_spoken_tot[tel_sound_unique] + Math.abs(distance_to_spoken[tel_sound_unique][tel_unique_mod]);
                // Log.e("temp", "tel_unique_mod:" + tel_unique_mod+" distance:"+distance_to_spoken[tel_sound_unique][tel_unique_mod]+" min:"+min_temp+" max:"+max_temp+" spoken:"+spoken[tel_unique_mod]);
                Log.e("temp", "mod="+tel_unique_mod+" spoken="+spoken[tel_unique_mod]+ " atribute=" +known_sounds[tel_sound_unique][tel_unique_mod]+" min="+ min_temp+" max="+max_temp+" uniq_mod="+unique_mod_temp_double+" dist="+distance_to_spoken[tel_sound_unique][tel_unique_mod] );
                tel_sound_unique++;
            }

            tel_unique_mod++;
        }
        //
        Integer tel_sound_unique = 0;
        while(tel_sound_unique<nr_recorded_words)
        {
            if(distance_to_spoken_tot[tel_sound_unique] < distance_tot_min || distance_tot_min == 0)
            {
                distance_tot_min =distance_to_spoken_tot[tel_sound_unique];
                distance_tot_min_nr = tel_sound_unique;
            }
            if(distance_to_spoken_tot[tel_sound_unique] > distance_tot_max || distance_tot_max == 0)
            {
                distance_tot_max =distance_to_spoken_tot[tel_sound_unique];
                distance_tot_max_nr = tel_sound_unique;
            }
            distance_tot_avg = distance_tot_avg + distance_to_spoken_tot[tel_sound_unique];

            Log.e("test", "sound nr "+tel_sound_unique+" distance"+distance_to_spoken_tot[tel_sound_unique]);
            tel_sound_unique++;

        }
        //System.out.println("sound spoken "+distance_tot_min_nr+" distance"+distance_to_spoken_tot[tel_sound_unique]);
        Log.e("test", "sound spoken "+distance_tot_min_nr);
        distance_tot_avg = distance_tot_avg / nr_recorded_words;


        /*
        Integer deltas_sounds[][] =  new Integer[4][13];

            int chance_yes = 0;
            int chance_no = 0;
            int tot_delta_yes_no = 0;
            int tel = 0;
            Integer modifier[] =  new Integer[13];
            modifier[0] = 4;
            modifier[1] = 1;
            modifier[2] = 4;
            modifier[3] = 1;
            modifier[4] = 1;
            modifier[5] = 1;
            modifier[6] = 4;
            modifier[7] = 1;
            modifier[8] = 2;
            modifier[9] = 1;
            modifier[10] = 2;
            modifier[11] = 1;

            while(tel <12)
            {
                delta_yes_no[tel] = Math.abs(known_yes[tel] - known_no[tel]);
                int avg_all_three = Math.abs(known_yes[tel] + known_no[tel] + spoken[tel])/3;
                tot_delta_yes_no = (int)((double)tot_delta_yes_no + (double)((delta_yes_no[tel] * modifier[tel])/(double)(avg_all_three)));
                int delta_yes = Math.abs(spoken[tel]-known_yes[tel]);
                int delta_no = Math.abs(spoken[tel]-known_no[tel]);
                if(delta_yes < delta_no)
                {
                    if(delta_yes > 0)
                    {
                        chance_yes = chance_yes + (delta_yes_no[tel] / delta_yes)*modifier[tel];
                    }
                    else
                    {
                        chance_yes = chance_yes + (delta_yes_no[tel])*modifier[tel];
                    }
                }
                else
                {
                    if(delta_no > 0)
                    {
                        chance_no = chance_no +  (delta_yes_no[tel] / delta_no)*modifier[tel];
                    }
                    else
                    {
                        chance_no = chance_no +  (delta_yes_no[tel])*modifier[tel];
                    }
                }
                System.out.println("spoken:" + spoken[tel]+" yes:"+known_yes[tel]+" no:"+known_no[tel] +" Cyes:"+chance_yes +" Cno:"+chance_no+" delta"+tot_delta_yes_no);
                tel = tel +1;
            }
            int delta_chance = Math.abs(chance_yes-chance_no);

            int req_noise = (int) (((double)tot_delta_yes_no/ 1)*1);
            if(req_noise < delta_chance)
            {
                if(chance_yes > chance_no)
                {
                    value_return = 2;
                }
                else
                {
                    value_return = 3;
                }
                System.out.println("Good! yes"+chance_yes+" no"+chance_no+ " noise:"+delta_chance+ " > "+req_noise);
            }
            else
            {
                value_return = 1;
                System.out.println("nope! yes"+chance_yes+" no"+chance_no+ " noise:"+delta_chance+ " < "+req_noise);
            }

*/
        }

        // return value_return;

        // 0 = none set
        // 1 = to mutch noise
        // 2 = yes
        // 3 = no

    public void upload_field()
    {
        if(player_id_server == "")
        {
            player_id_server = find_value_in_xml("login_info", "id");
        }
        String string_to_send_to_testPHP = "";

        // DELETE PREVIOUS XML
        string_to_send_to_testPHP = "qid="+player_id_server+"&qty=DELETE";
        String send_to_server_flag = find_value_in_xml("login_info", "flag_send_server");

        try {
            server_side_PHP.push_to_testphp(string_to_send_to_testPHP, send_to_server_flag);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int tel_x = 0;
        String id_name_field_cell = "";
        String type_field_cell = "";

        while (tel_x < field_atm_array.length )
        {
            int tel_y = 0;
            while (tel_y < field_atm_array[tel_x].length )
            {
                Integer id_field_cell = field_atm_array[tel_x][tel_y][0];
                if(id_field_cell == null)
                {

                }
                else
                {
                    id_name_field_cell = field_ids_and_names.get(id_field_cell).get(0);
                    type_field_cell = field_ids_and_names.get(id_field_cell).get(1);
                    if(type_field_cell == "1")
                    {
                        id_name_field_cell = "0";
                    }
                    // Log.e("XML parser", id_name_field_cell);
                    if(type_field_cell != "0")
                    {
                        if(type_field_cell != "1")
                        {
                            // INT TO STRING USELESS
                            send_object(id_field_cell.toString(), id_name_field_cell);
                            check_send_atributes(id_field_cell.toString(), id_name_field_cell);
                        }
                        string_to_send_to_testPHP = "qid="+player_id_server+"&qty=cell&qcx="+tel_x+"&qcy="+tel_y+"&qcv="+id_field_cell+"&qct="+type_field_cell;

                        // NAME MOET NOG  id_name_field_cell
                        try {
                            server_side_PHP.push_to_testphp(string_to_send_to_testPHP, send_to_server_flag);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                tel_y = tel_y+1;

            }
            tel_x = tel_x+1;
        }
        // hedu-free.uphero.com/active_games/test.php?qid=9&qty=cell&qcx=1&qcy=2&qcv=2
        // hedu-free.uphero.com/active_games/test.php?qid=9&qty=enemy&nme=testobj&fid=2
        // hedu-free.uphero.com/active_games/test.php?qid=9&qty=atri&nme=test_stat&fid=2&atv=10
    }

    public void check_send_atributes(String id_object_field_s, String name_object_field_s)
    {
        if(player_id_server == "")
        {
            player_id_server = find_value_in_xml("login_info", "id");
        }
        String send_to_server_flag = find_value_in_xml("login_info", "flag_send_server");
        /*
            Integer id_rulebook = from_object_name_to_rule_number(field_ids_and_names.get(id_ofcell_type_clicked).get(0));
            */
            Integer id_rulebook = from_object_name_to_rule_number(name_object_field_s);
            Integer id_object_field_i = Integer.parseInt(id_object_field_s);
            if (id_object_field_i > 0) {

                Integer tel_atributes = 0;
                while (tel_atributes < world_atribute_names.size()) {
                    String atribut_name = world_atribute_names.get(tel_atributes);
                    Integer total_atribute_value = count_atributs_mod_to_def(id_rulebook, id_object_field_i, tel_atributes);

                    while(id_object_field_i >= atributs_send_php.size())
                    {
                        atributs_send_php.add(new ArrayList<Integer>());
                    }
                    while(tel_atributes >= atributs_send_php.get(id_object_field_i).size())
                    {
                        atributs_send_php.get(id_object_field_i).add(0);
                    }
                    // Log.e("XML parser", "tot value:" + total_atribute_value +" alredy set:"+atributs_send_php.get(id_object_field_i).get(tel_atributes));
                    if(total_atribute_value != atributs_send_php.get(id_object_field_i).get(tel_atributes))
                        {
                            atributs_send_php.get(id_object_field_i).set(tel_atributes, total_atribute_value);
                            String string_to_send_to_testPHP = "qid=" + player_id_server + "&qty=atri&nme=" + atribut_name + "&fid=" + id_object_field_s + "&atv=" + total_atribute_value;
                        try {
                            server_side_PHP.push_to_testphp(string_to_send_to_testPHP, send_to_server_flag);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    // hedu-free.uphero.com/active_games/test.php?qid=9&qty=atri&nme=test_stat&fid=2&atv=10

                    // total_text = total_text + atribut_name + " : " + total_atribute_value + "\n";

                    tel_atributes = tel_atributes + 1;
                }
            }

    }


    // hedu-free.uphero.com/active_games/test.php?qid=9&qty=enemy&nme=testobj&fid=2
    public void send_object(String id_object_field_s, String name_object_field_s)
    {
        String send_to_server_flag = find_value_in_xml("login_info", "flag_send_server");
        if(player_id_server == "")
        {
            player_id_server = find_value_in_xml("login_info", "id");
        }
        String string_to_send_to_testPHP = "qid=" + player_id_server + "&qty=enemy&nme=" + name_object_field_s + "&fid=" + id_object_field_s;
        try {
            server_side_PHP.push_to_testphp(string_to_send_to_testPHP, send_to_server_flag);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
//TODO teaxt to speech: http://code.tutsplus.com/tutorials/android-sdk-using-the-text-to-speech-engine--mobile-8540