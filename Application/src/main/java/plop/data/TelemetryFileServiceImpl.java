package plop.data;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;

import com.example.android.common.logger.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public class TelemetryFileServiceImpl
{
    private static final String TAG = "TelemetryFileService";

    private File currentFile;

    private WriteThread writeThread;

    private Context context;

    public TelemetryFileServiceImpl(Context context)
    {
        this.context = context;
    }

    public void openNewFile(String fileName)
    {
        if(currentFile != null)
        {
            //Existing file open, finalize
            finalizeFile(currentFile);
        }

        if(!isExternalStorageWritable())
        {
            Log.e(TAG, "Cannot write to external storage");
            return;
        }

        try
        {
            currentFile = getStorageFile(fileName);
            BufferedOutputStream bos = new BufferedOutputStream((new FileOutputStream(currentFile)));
            writeThread = new WriteThread(bos);
            writeThread.start();
        }
        catch(Exception e)
        {
            Log.e(TAG, "Failed to start thread for file writing", e);
            throw new RuntimeException(e);
        }
    }

    public File getCurrentFile()
    {
        return currentFile;
    }

    public void finalizeFile(File finalFile)
    {
        try {
            writeThread.cancel();
            writeThread.join(1000);
            if (!currentFile.getAbsolutePath().equals(finalFile.getAbsolutePath())) {
                currentFile.renameTo(finalFile);
            }

            //This is needed so the file updates as visible on some PCs...
            MediaScannerConnection.scanFile(context, new String[]{finalFile.getAbsolutePath()}, null, null);
        }
        catch(Exception e)
        {
            Log.e(TAG, "Failure while finalizing file", e);
        }
    }

    public void addLogEntry(String logEntry, boolean binary) {
        if (writeThread != null && writeThread.isAlive())
        {
            try
            {
                if(!binary) {
                    writeThread.write(new TelemetryData(logEntry));
                }
                else
                {
                    //TODO
                }
            }
            catch(Exception e)
            {
                Log.w(TAG, "Exception handling log entry, invalid data?", e);
            }
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getStorageFile(String fileName) throws IOException
    {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), fileName);

        if(file.exists())
        {
            file.delete();
            file.createNewFile();
        }

//        if (!file.mkdirs()) {
//            Log.e(TAG, "Directory not created");
//        }
        return file;
    }
}
