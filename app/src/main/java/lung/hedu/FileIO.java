package lung.hedu;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.content.Context.*;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by Luuk on 7-8-2015.
 */
public class FileIO
{
    public static final int FILE_BMP = 0;
    public static final int FILE_XML = 1;
    public static final int FILE_TXT = 2;

    public void saveFile(Context context, File file, String fileName, int mode){
        OutputStream out = null;
        try
        {
            out = new BufferedOutputStream(new FileOutputStream(file));
        }
        catch(FileNotFoundException exception)
        {
            Log.e("SaveFile", "The file " + file.getPath() + " was not found..");
        }
        catch(IOException exception)
        {
            exception.printStackTrace();
            Log.e("SaveFile", "Caught exception: " + exception);
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch(Throwable ignore) {}
            }
        }
    }

    public void saveStringFilePrivate(Context context, String fileName, String extention, String Data){
        FileOutputStream out = null;
        try
        {
            out = context.openFileOutput(fileName +"."+ extention, context.MODE_PRIVATE);
            out.write(Data.getBytes());
        }
        catch(IOException exception)
        {
            exception.printStackTrace();
            Log.e("SaveFile", "Caught exception: " + exception);
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch(Throwable ignore) {}
            }
        }
    }

    public void loadStringFilePrivate(Context context, String fileName, String extention, String Data){
        FileInputStream in = null;
        InputStreamReader inS = null;
        BufferedReader read = null;
        StringBuilder result = new StringBuilder();
        String buf = null;
        try
        {
            in = context.openFileInput("fileName");
            inS = new InputStreamReader(in);
            read = new BufferedReader(inS);

            while( (buf = read.readLine()) != null)
            {
                result.append(buf);
            }
        }
        catch(IOException exception)
        {
            exception.printStackTrace();
            Log.e("SaveFile", "Caught exception: " + exception);
        }
        finally
        {
            if(read != null)
            {
                try
                {
                    read.close();
                }
                catch(Throwable ignore) {}
            }
            if (inS != null)
            {
                try
                {
                    inS.close();
                }
                catch(Throwable ignore) {}
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch(Throwable ignore) {}
            }
        }
    }

    public void saveBMP(Context context, Bitmap bmp, String fileName, Bitmap.CompressFormat format, int quality, int mode)
    {
        FileOutputStream out = null;
        try
        {
            out = context.openFileOutput(fileName, mode);
            bmp.compress(format, quality, out);
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
            Log.e("Main Activity", "Caught exception: " + exception);
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch(Throwable ignore) {}
            }
        }
    }

    public File saveBMPwithPath(Context context, Bitmap bmp, String fileName, Bitmap.CompressFormat format, int quality, int mode)
    {
        saveBMP(context, bmp, fileName, format, quality, mode);

        return context.getFileStreamPath(fileName);
    }

}
