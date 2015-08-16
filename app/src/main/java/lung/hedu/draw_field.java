package lung.hedu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * Created by Sarah on 15-8-2015.
 */
public class draw_field {





    public static ImageView create_imageview_field (LinearLayout lin_lay_q)
    {
        lin_lay_q.removeAllViews();
        Context context_this = ApplicationContextProvider.getContext();
        ImageView new_image_view = new ImageView(context_this);
        new_image_view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        new_image_view.canScrollVertically(1);
        new_image_view.setId(R.id.drawfield_id);
        lin_lay_q.addView(new_image_view);
        return new_image_view;


    }

    public static Bitmap create_bitmap_field ( Integer x_width, Integer y_height)
    {
        Bitmap field_bmp = Bitmap.createBitmap((int) (x_width), (int) (y_height), Bitmap.Config.ARGB_8888);
 /*
        Integer tel_x = 0;
        Integer tel_y = 0;
        String color_temp = "";

        while (tel_x < x_width)
        {
            if (tel_x< y_height)
            {
                field_bmp.setPixel(tel_x, tel_x, Color.parseColor("#CCCCCC"));
            }
            tel_x = tel_x +1;
        }
*/
        return field_bmp;
    }

    public static Bitmap draw_squarre(Integer field_x, Integer field_y, Integer found_value_i, Bitmap field_bmp, Integer squarre_size)
    {

        Integer start_x = field_x * (squarre_size+1 ) ;
        Integer start_y = field_y * (squarre_size+1 ) ;

        String color_squarre = "#448844";
        if(found_value_i == 0)
        {
            color_squarre = "#444444";
        }

        Integer tel_x =0;
        while (tel_x <squarre_size )
        {
            Integer tel_y =0;
            while (tel_y <squarre_size )
            {
                // Log.e("MAP", "field_x " + field_x+" tel_x "+tel_x);
                field_bmp.setPixel(start_x + tel_x, start_y+ tel_y, Color.parseColor(color_squarre));

                tel_y = tel_y +1;
            }
            tel_x = tel_x +1;
        }
        return field_bmp;
    }


}
