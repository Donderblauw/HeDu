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


    public static Bitmap create_bitmap_field(Integer x_width, Integer y_height) {
        Bitmap field_bmp = Bitmap.createBitmap((int) (x_width), (int) (y_height), Bitmap.Config.ARGB_8888);

        return field_bmp;
    }



}

