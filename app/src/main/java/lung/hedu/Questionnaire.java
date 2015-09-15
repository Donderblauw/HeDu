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
import android.view.MotionEvent;
import android.view.View;
// import android.support.v4.app.NavUtils;
import android.view.ViewGroup;
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
import java.util.ArrayList;
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
    public Integer distance_to_clicked = 0;

//    public ArrayList<ArrayList<String>> objects_names_and_shorts = new ArrayList<ArrayList<String>>();

    public ArrayList<String> world_atribute_names = new ArrayList<String>();

    // id_object_player/enemy  <defs>
    public ArrayList<ArrayList<Integer>> id_def_atributs = new ArrayList<ArrayList<Integer>>();
    public ArrayList<String> names_objects_id_sync = new ArrayList<>();

    // id_object | Item id | atribute ID | value | extra
    public ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>  atribute_modifications = new ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> ();


    public ArrayList<ArrayList<String>>  enemy_interaction = new ArrayList<ArrayList<String>> ();
    public ArrayList<ArrayList<String>>  atribute_trigger = new ArrayList<ArrayList<String>> ();

    public ArrayList<ArrayList<String>>  req_interaction_glob = new ArrayList<ArrayList<String>> ();

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
        if(type_xml.equals("q"))
        {
            XML_ini_questionairre(XML_file);
        }
        else if(type_xml.equals("m"))
        {
            XML_ini_map_new(XML_file);
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
                            tv_parents[level_parent_atm] = create_awnserview(goto_temp, goto_temp_id);
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
    public void XML_ini_map_new(String XML_file_input) {
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
            field_ids_and_names.get(tot_arraylist).add("#66ff66");


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

        String found_v = find_value_in_xml(req_tag_name, req_id);
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
    public TextView create_awnserview(String onclick_temp, String goto_id)
    {

        TextView question_tv = new TextView(this);
        question_tv.setId(awnser_id);
        question_tv.setTextSize(font_size);
        question_tv.setTypeface(font_face);
        Bundle inputExtras = question_tv.getInputExtras(true);
        inputExtras.putString("onclick_temp",onclick_temp);
        inputExtras.putString("goto_id",goto_id);


        question_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout lin_lay_q = (LinearLayout) findViewById(R.id.linearLayout_questuinnaire_vert);


                TextView temp_tv = (TextView) v;
                Bundle inputExtras = temp_tv.getInputExtras(true);
                String output_questionfile = inputExtras.getString("onclick_temp", "");
                String goto_id = inputExtras.getString("goto_id", "");

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

                XML_ini_q_or_map(goto_id, output_questionfile);

            }
        } ) ;

        awnser_id = awnser_id +1;

        return question_tv;

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
        }
        else {
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

    public TextView create_text_view_worlds(String world_adding, String prefix)
    {
        TextView return_tv = new TextView(this);
        return_tv.setId(awnser_id);
        return_tv.setTextSize(font_size);
        return_tv.setText(prefix+world_adding);
        Bundle inputExtras = return_tv.getInputExtras(true);
        inputExtras.putString("onclick_temp",world_adding);

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

        NodeList worlds_nodelist = world_index.getElementsByTagName("start_file");
        Integer tel = 0;

        Random rand = new Random();
        int n = rand.nextInt(worlds_nodelist.getLength());

        String world_name = worlds_nodelist.item(n).getAttributes().getNamedItem("name").getTextContent();
        String qorm = worlds_nodelist.item(n).getAttributes().getNamedItem("qorm").getTextContent();

        read_map_rules();

        XML_ini_q_or_map(qorm, world_name);
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
        Integer[] pos_active_player_id = xy_pos_id(active_player_id);
        Integer id_ofcell_type_clicked = field_atm_array[x_clicked][y_clicked][0];
        // 1 = type (0 = empty 1 = normal 2 = player 3 = enemy 4 = neutral 5 = trigger)
        String type_clicked = field_ids_and_names.get(id_ofcell_type_clicked).get(1);

        Integer distance = 0;
        if(pos_active_player_id[2] == 1)
        {
            distance = range_to(pos_active_player_id[0], pos_active_player_id[1], x_clicked, y_clicked);
            distance_to_clicked = distance;
        }
        boolean walkable = false;
        if(type_clicked.equals("1"))
        {
            walkable=true;
        }
        else if(type_clicked.equals("5"))
        {
            walkable=true;
        }
        if(walkable == true)
        {
            Integer id_walk_range = find_atribute_if_from_string("Walk range");


            // PLAYER ID = altijd null?!
            Integer walk_range = id_def_atributs.get(0).get(id_walk_range);

            Integer tel_modifications_player = 0;
            while(tel_modifications_player < atribute_modifications.get(active_player_id).size())
            {

                walk_range = walk_range +atribute_modifications.get(active_player_id).get(tel_modifications_player).get(id_walk_range).get(0);
                tel_modifications_player = tel_modifications_player +1 ;
            }

            if(distance <= walk_range)
            {
                move_player(x_clicked, y_clicked, pos_active_player_id[0], pos_active_player_id[1]);
            }
        }
        if(type_clicked.equals("3"))
        {
            //
            LinearLayout lin_lay_q = (LinearLayout)findViewById(R.id.linearLayout_questuinnaire_vert);

            Integer tel_interactions = 0;
            while (tel_interactions < enemy_interaction.size())
            {
                String req_name = req_interaction_glob.get(tel_interactions).get(1);
                String req_type = req_interaction_glob.get(tel_interactions).get(2);
                String req_then_name = req_interaction_glob.get(tel_interactions).get(3);
                String extra_eq = req_interaction_glob.get(tel_interactions).get(4);
                Integer[] req_1_id_array = get_object_rule_world_and_atribute_id(req_name);
                Integer value_1 = 0;
                if(req_1_id_array[0] == -1)
                {
                    value_1 = req_1_id_array[2];
                }
                else
                {
                    value_1 = count_atributs_mod_to_def(req_1_id_array[0], req_1_id_array[1], req_1_id_array[2]);
                }

                Integer[] req_then_id_array = get_object_rule_world_and_atribute_id(req_then_name);
                Integer value_then = 0;
                if(req_then_id_array[0] == -1)
                {
                    value_then = req_then_id_array[2];
                }
                else
                {
                    value_then = count_atributs_mod_to_def(req_then_id_array[0], req_then_id_array[1], req_then_id_array[2]);
                }

                Boolean equal_true = false;
                if(extra_eq.equals("t"))
                {
                    equal_true = true;
                }

                Boolean check_req = check_req_type(value_1.toString(), req_type, value_then.toString(), null, null, equal_true);
                if(check_req == true) {

                    TextView add_enemy_interactions = new TextView(this);
                    add_enemy_interactions.setId((201 + tel_interactions));
                    String name_interaction = enemy_interaction.get(tel_interactions).get(0);
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
                                aply_math_to_interaction(id_interaction, id_enemy, id_active_player);

                            }

                            end_turn();
                        }
                    });

                    lin_lay_q.addView(add_enemy_interactions);

                }



                tel_interactions = tel_interactions +1;
            }

        }

    }



    public void aply_math_to_interaction(Integer id_interaction, Integer id_enemy_field, Integer id_active_player_field)
    {

        String math_problem = enemy_interaction.get(id_interaction).get(1);
        String math_problemoriginal = math_problem;
        math_problem = math_problem.replace("\n", "");
        math_problem = math_problem.replace("\t", "");
        Integer first_quote = 0;
        Integer second_quote = 0;

        // first resolve randoms
        Integer first_open_squarre = math_problem.indexOf("[");
        Integer first_close_squarre = math_problem.indexOf("]");

        String inside_squarre = math_problem.substring((first_open_squarre + 1), first_close_squarre);
        String awnser = replace_brackets(inside_squarre);
        math_problem = math_problem.substring(0, first_open_squarre) + awnser + math_problem.substring(first_close_squarre+1);


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


        atribute_modifications.get(id_enemy_field).add(new ArrayList<ArrayList<Integer>>());
        Integer new_mod = atribute_modifications.get(id_enemy_field).size() -1;

/*        while (new_mod >= atribute_modifications.get(id_enemy_field).size())
        {
            atribute_modifications.get(id_enemy_field).add(new ArrayList<ArrayList<Integer>>());
        }
        */

        ArrayList temp = new ArrayList<Integer>();
        temp.add(0, awnser_mathproblem);
        ArrayList<ArrayList<Integer>> temp_new = new ArrayList<ArrayList<Integer>>();
        temp_new.add(atribute_to_set, temp);

        atribute_modifications.get(id_enemy_field).set(new_mod, temp_new);

    }

    public Integer[] get_object_rule_world_and_atribute_id(String input)
    {
        Integer[] return_array = {-1,-1, -1};
        Integer end_name = (input.indexOf("|"));
        String type_operator_is = input.substring(0, end_name);
        String name_atribute_is = input.substring((end_name + 1), input.length());

        if(name_atribute_is.equals("distance"))
        {
            return_array[0] = -1;
            return_array[1] = -1;
            return_array[2] = distance_to_clicked;
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
            return_id = 0;
        }
        else if(name.equals("xx"))
        {
            return_id = -1;
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
            boolean do_the_math = false;

            if(id_found_object_field > -1)
            {

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
                    if(atribute_modifications.get(id_found_object_field).get(tel_modifications).size()>= atribute_id_temp)
                    {
                        total_atribute_value = total_atribute_value + atribute_modifications.get(id_found_object_field).get(tel_modifications).get(atribute_id_temp).get(0);
                    }
                    tel_modifications = tel_modifications +1 ;
                }
                found_atribute_value = total_atribute_value;
                do_the_math = true;

            }

            else if(type_operator.equals("v"))
            {
                awnser = awnser + Integer.parseInt(name_atribute);
            }
            else if(type_operator.equals("xx"))
            {
                if(name_atribute.equals("distance"))
                {
                    awnser = distance_to_clicked;
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
            if(atribute_modifications.get(id_player_field).get(tel_modifications).size()>= id_atribute)
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
        field_atm_array[x_to][y_to][0] = field_atm_array[x_from][y_from][0];
        field_atm_array[x_from][y_from][0] = 1;
        end_turn();
    }

    public void end_turn()
    {
        draw_field_squarres();
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
/*
        // set_userneme to keep synchrome
        Integer tel_users = 0;
        while (tel_users < username.size())
        {
            username.get(tel_users));
            tel_users = tel_users +1;
        }
*/
        names_objects_id_sync.add("Player");
        id_def_atributs.add( new ArrayList() );

        while(tel_atributes < atribute_nodelist.getLength())
        {
            NamedNodeMap temp_atribut = atribute_nodelist.item(tel_atributes).getAttributes();
            String atribute_name_temp = temp_atribut.getNamedItem("name").getTextContent();
            String atribute_user_def_temp = temp_atribut.getNamedItem("user_def").getTextContent();
            // String atribute_enemy_def_temp = temp_atribut.getNamedItem("enemy_def").getTextContent();
            world_atribute_names.add(atribute_name_temp);

            /*
            tel_users = 0;
            while (tel_users < username.size())
            {
                id_def_atributs.get(tel_users).add(Integer.parseInt(atribute_user_def_temp));
                tel_users = tel_users +1;
            }
            */

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
            id_def_atributs.add( new ArrayList() );
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


            req_interaction_glob.add(new ArrayList());
            Integer arraylist_req_atm = (req_interaction_glob.size() - 1);

            Element req_interaction_element_atm = (Element) enemy_interactions_nl.item(tel_enemy_interaction);
            NodeList req_interaction_nl = req_interaction_element_atm.getElementsByTagName("req_interaction");
            if(req_interaction_nl.getLength() > 0)
            {
                NamedNodeMap temp_atribut_req = req_interaction_nl.item(0).getAttributes();
                String req_name = temp_atribut_req.getNamedItem("req_name").getTextContent();
                String req_type = temp_atribut_req.getNamedItem("req_type").getTextContent();
                String req_then_name = temp_atribut_req.getNamedItem("req_then_name").getTextContent();

                Node extra_eq_n = temp_atribut_req.getNamedItem("extra_eq");
                String extra_eq = "f";
                if (extra_eq_n == null) {
                    extra_eq = "f";
                } else {
                    if (extra_eq_n.getTextContent().equals("t")) {

                        extra_eq = "t";
                    }
                }

                req_interaction_glob.get(arraylist_req_atm).add("req_interaction");
                req_interaction_glob.get(arraylist_req_atm).add(req_name);
                req_interaction_glob.get(arraylist_req_atm).add(req_type);
                req_interaction_glob.get(arraylist_req_atm).add(req_then_name);
                req_interaction_glob.get(arraylist_req_atm).add(extra_eq);
            }



            enemy_interaction.add(new ArrayList());
            Integer arraylist_atm = (enemy_interaction.size() - 1);
            enemy_interaction.get(arraylist_atm).add(enemy_interactions_name_temp);

            Element interaction_element_atm = (Element) enemy_interactions_nl.item(tel_enemy_interaction);
            NodeList math_interaction = interaction_element_atm.getElementsByTagName("math");

            Integer tel_math_problems = 0;
            while (tel_math_problems < math_interaction.getLength())
            {
                String math_problem_s = math_interaction.item(tel_math_problems).getTextContent();
                enemy_interaction.get(arraylist_atm).add(math_problem_s);
                tel_math_problems = tel_math_problems+1;
            }

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
            atribute_trigger.get(arraylist_atm).add(atribute_trigger_name_temp);

            tel_atribute_trigger = tel_atribute_trigger+1;
        }

    }
}
//TODO teaxt to speech: http://code.tutsplus.com/tutorials/android-sdk-using-the-text-to-speech-engine--mobile-8540