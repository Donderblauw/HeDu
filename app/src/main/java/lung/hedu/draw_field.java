package lung.hedu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Created by Sarah on 15-8-2015.
 */
public class draw_field {

    public static ImageView create_imageview_field (LinearLayout lin_lay_q, final Integer squarre_size_temp) {
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

                        field_bmp = draw_squarre(field_x, field_y, 2, field_bmp, squarre_size_q);
                        imageView_temp.setImageBitmap(field_bmp);
                    }
                }
                return true;
            }
        });
//        new_image_view.setOnTouchListener

        lin_lay_q.addView(new_image_view);
        return new_image_view;
    }




    public static Bitmap create_bitmap_field(Integer x_width, Integer y_height) {
        Bitmap field_bmp = Bitmap.createBitmap((int) (x_width), (int) (y_height), Bitmap.Config.ARGB_8888);

        return field_bmp;
    }

    public static Bitmap draw_squarre(Integer field_x, Integer field_y, Integer found_value_i, Bitmap field_bmp, Integer squarre_size) {
//        imageView img_view_temp = (imageView)findViewById(R.id.drawfield_id);
//        Bitmap field_bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        Integer start_x = field_x * (squarre_size + 1);
        Integer start_y = field_y * (squarre_size + 1);

        String[] color_squarre = map_preset_res.set_map_colors();


        Integer tel_x = 0;
        while (tel_x < squarre_size) {
            Integer tel_y = 0;
            while (tel_y < squarre_size) {
                // Log.e("MAP", "field_x " + field_x+" tel_x "+tel_x);
                field_bmp.setPixel(start_x + tel_x, start_y + tel_y, Color.parseColor(color_squarre[found_value_i]));

                tel_y = tel_y + 1;
            }
            tel_x = tel_x + 1;
        }
        return field_bmp;
    }
/*
    public static void XML_ini_field(XmlPullParser_temp) {
        XmlPullParser XmlPullParser_temp = null;
        String text_return = "";


        try {
            XmlPullParser_temp = load_XML(output_questionfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        int event;
        String text = null;

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
                            onclick_temp = goto_temp;
                            // Log.e("temp", "setup " + onclick_temp);
                            tv_parents[level_parent_atm] = create_awnserview();
                            tv_show[level_parent_atm] = true;
                        }
                        else if(xml_atm.equals("req"))
                        {
                            String req_tag_name = XmlPullParser_temp.getAttributeValue(null, "req_tag_name").toString();
                            String req_id = XmlPullParser_temp.getAttributeValue(null, "req_id").toString();
                            String req_v = XmlPullParser_temp.getAttributeValue(null, "req_v").toString();
                            String found_v = find_value_in_xml(req_tag_name, req_id);
                            tv_show[(level_parent_atm-1)] = false;
                            if(found_v.equals(req_v))
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
                        else if(xml_atm.equals("map"))
                        {
                            remove_views();

                            Integer x_sqre = Integer.parseInt(XmlPullParser_temp.getAttributeValue(null, "x_sqre").toString());
                            Integer y_sqre = Integer.parseInt(XmlPullParser_temp.getAttributeValue(null, "y_sqre").toString());
                            set_squarre_size(x_sqre, y_sqre);

                            LinearLayout ll_temp = (LinearLayout) findViewById(R.id.linearLayout_questuinnaire_vert);
                            ImageView field_img_view = draw_field.create_imageview_field(ll_temp, squarre_size);


                            bitmap_field = draw_field.create_bitmap_field( ( (x_sqre ) * (squarre_size+2) +2 ) , ( (y_sqre) * (squarre_size+2)+2));

                            field_img_view.setImageBitmap(bitmap_field);
                        }
                        else if(xml_atm.equals("row"))
                        {
                            y_row_atm = y_row_atm +1;
                        }
                        break;

                    case XmlPullParser.TEXT: {
                        if (xml_atm.equals("question")) {
                            tv_parents[level_parent_atm].setText(XmlPullParser_temp.getText());
                        }
                        else if (xml_atm.equals("awnser")) {
                            tv_parents[level_parent_atm].setText(XmlPullParser_temp.getText());
                        }
                        else if (xml_atm.equals("row")) {
                            read_rows(XmlPullParser_temp.getText(), y_row_atm);
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

    }
    */

}
