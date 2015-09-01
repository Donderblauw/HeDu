package lung.hedu;

import lung.hedu.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
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
import java.util.Random;

import static lung.hedu.FileIO.loadStringFilePrivate;
import static lung.hedu.FileIO.saveStringFilePrivate;

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


    public Typeface font_face = null;
    public Integer font_size = 20;
    public Integer awnser_id = 102;
    public Integer squarre_size = 30;
    public Bitmap bitmap_field = null;
    public Integer y_row_atm = -1;

    Document XML_user_info_doc = null;
    public String this_world = "";
    public Boolean check_if_XMLisset = false;
    public String username = "";

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
     */
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

        if(username.equals(""))
        {
            username = find_value_in_xml("login_info", "name");
        }
        output = output.replace("|qname", username);

        return output;
    }

    public void XML_ini_q_or_map(String type_xml, String XML_file)
    {
        if(type_xml.equals("q"))
        {
            XML_ini_questionairre(XML_file);
        }
        else if(type_xml.equals("m"))
        {
            XML_ini_map(XML_file);
        }
    }
    public boolean check_req_type (String found_v, String req_type, String req_v, String then_tag_name, String then_id)
    {
        Boolean return_bool = false;
/*
        bt = bigger then xx
        st = smaller then xx
        eq = equal
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

                            String found_v = find_value_in_xml(req_tag_name, req_id);
                            tv_show[(level_parent_atm-1)] = false;
                            boolean test_result = check_req_type(found_v, req_type, req_v, then_tag_name, then_id);
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

    public String XML_ini_map(String XML_file_input) {
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
        String parents_xml[] = new String[9];
        Integer level_parent_atm = 0;
        String xml_atm = "";

        try {
            event = XmlPullParser_temp.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name = XmlPullParser_temp.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:
                        xml_atm = name;
                        level_parent_atm = level_parent_atm+1;
                        parents_xml[level_parent_atm] = xml_atm;

                        if(xml_atm.equals("map"))
                        {
                            remove_views();

                            Integer x_sqre = Integer.parseInt(XmlPullParser_temp.getAttributeValue(null, "x_sqre").toString());
                            Integer y_sqre = Integer.parseInt(XmlPullParser_temp.getAttributeValue(null, "y_sqre").toString());
                            set_squarre_size(x_sqre, y_sqre);

                            LinearLayout ll_temp = (LinearLayout) findViewById(R.id.linearLayout_questuinnaire_vert);
                            ImageView field_img_view = draw_field.create_imageview_field(ll_temp, squarre_size);


                            bitmap_field = draw_field.create_bitmap_field(((x_sqre) * (squarre_size + 2) + 2), ((y_sqre) * (squarre_size + 2) + 2));

                            field_img_view.setImageBitmap(bitmap_field);
                        }
                        else if(xml_atm.equals("row"))
                        {
                            y_row_atm = y_row_atm +1;
                        }
                        break;

                    case XmlPullParser.TEXT: {
                        if (xml_atm.equals("row")) {
                            read_rows(XmlPullParser_temp.getText(), y_row_atm);
                        }
                    }
                    break;

                    case XmlPullParser.END_TAG:
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
                    add_story_line("optionC", add_value, replace_add_bool, add_line);
                }
                // add_story_line(add_line, add_value);

                XML_ini_q_or_map(goto_id, output_questionfile);

            }
        } ) ;

        awnser_id = awnser_id +1;

        return question_tv;

    }

    public void set_squarre_size(Integer totx_squarres, Integer toty_squarres)
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


    }
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
        } ) ;

        awnser_id = awnser_id +1;

        return return_tv;
    }

    public TextView create_text_view_worlds_start(String world_adding, String prefix)
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
                temp_tv.setText("clicked "+world_to_load);
                // download_world(world_to_load); START WORLD
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

                XML_ini_q_or_map(qorm, world_name);

            }
        } ) ;

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


}
