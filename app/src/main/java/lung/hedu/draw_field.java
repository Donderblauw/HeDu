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

                        field_bmp = draw_squarre(field_x, field_y, "#cccccc", field_bmp, squarre_size_q);
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

}
