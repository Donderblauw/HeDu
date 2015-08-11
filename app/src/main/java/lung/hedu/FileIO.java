package lung.hedu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Luuk on 7-8-2015.
 */
public class FileIO
{
    public static void saveStringFilePrivate(String fileName, String extention, String Data){
        FileOutputStream out = null;
        try
        {
            out = ApplicationContextProvider.getContext().openFileOutput(fileName + "." + extention, ApplicationContextProvider.getContext().MODE_PRIVATE);
            out.write(Data.getBytes());
        }
        catch(IOException exception)
        {
            exception.printStackTrace();
            Log.e("SaveFile", "Caught exception: " + exception.toString());
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

    public static String loadStringFilePrivate(String fileName, String extention){
        FileInputStream in = null;
        InputStreamReader inS = null;
        BufferedReader read = null;
        StringBuilder result = new StringBuilder();
        String buf = null;
        try
        {
            in = ApplicationContextProvider.getContext().openFileInput(fileName + "." + extention);
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
            Log.e("LoadFile", "Caught exception: " + exception.toString());
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
        return result.toString();
    }

    public static void saveBMPPrivate(String fileName, Bitmap bmp, Bitmap.CompressFormat format, int quality)
    {
        FileOutputStream out = null;
        try
        {
            out = ApplicationContextProvider.getContext().openFileOutput(fileName + ".bmp", ApplicationContextProvider.getContext().MODE_PRIVATE);
            bmp.compress(format, quality, out);
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
            Log.e("SaveBMP", "Caught exception: " + exception.toString());
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

    public static Bitmap loadBMPPrivate(String fileName)
    {
        FileInputStream in = null;
        BufferedInputStream buf = null;
        Bitmap result = null;

        try
        {
            in = ApplicationContextProvider.getContext().openFileInput(fileName + ".bmp");
            buf = new BufferedInputStream(in);
            result = BitmapFactory.decodeStream(buf);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            Log.e("LoadBMP", "Caught exception: " + exception.toString());
        }
        finally
        {
            if(buf != null)
            {
                try
                {
                    buf.close();
                }
                catch(Throwable ignore) {}
            }
            if(in != null)
            {
                try
                {
                    in.close();
                }
                catch (Throwable ignore) {}
            }
        }
        return result;
    }

    public static void savePDFPrivate(String fileName, PrintedPdfDocument pdf)
    {
        FileOutputStream out = null;
        try
        {
            out = ApplicationContextProvider.getContext().getApplicationContext().openFileOutput(fileName + ".pdf", ApplicationContextProvider.getContext().MODE_PRIVATE);
            pdf.writeTo(out);
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
            Log.e("SavePDF", "Caught exception: " + exception.toString());
        }
        finally
        {
            if(out != null)
            {
                try
                {
                    out.close();
                }
                catch (Throwable ignore) {}
            }
        }
    }
}
