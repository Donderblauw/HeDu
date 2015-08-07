package lung.hedu;

import android.util.Log;
import android.content.Context.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Luuk on 7-8-2015.
 */
public class FileIO
{
    public void saveFile(File file, String fileName) throws IOException {
        OutputStream out = null;
        try
        {
            out = new BufferedOutputStream(new FileOutputStream(file));
        }
        catch(FileNotFoundException exception)
        {
            Log.e("SaveFile", "The file " + file.getPath() + " was not found.");
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
                out.close();
            }
        }
    }
}
