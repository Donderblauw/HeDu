package lung.hedu;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * Created by Sarah on 15-8-2015.
 */
public class draw_field {



    public Context context_this = new ApplicationContextProvider();

    public void create_imageview_field (LinearLayout lin_lay_q)
    {
        lin_lay_q.removeAllViews();
        ImageView new_image_view = new ImageView(context_this);
        new_image_view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        new_image_view.canScrollVertically(1);
        new_image_view.setId(R.id.drawfield_id);
        lin_lay_q.addView(new_image_view);


    }

    public void create_bitmap_field (LinearLayout lin_lay_q)
    {


    }


}
