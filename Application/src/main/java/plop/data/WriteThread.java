package plop.data;

import com.example.android.common.logger.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 */
public class WriteThread extends Thread
{
    private static final String TAG = "WriteThread";

    private final OutputStream outStream;

    public void setSyncEveryNSamples(int syncEveryNSamples) {
        this.syncEveryNSamples = syncEveryNSamples;
    }

    private Queue<LoggableData> writeQueue = new ConcurrentLinkedQueue<>();

    private volatile boolean running;

    private int syncEveryNSamples = 5000;

    private int sampleCountSinceSync = 0;

    public WriteThread(OutputStream outputStream) throws IOException
    {
//        if(file.exists())
//        {
//            if(!file.createNewFile())
//            {
//                throw new IOException("Cannot create file: " + file);
//            }
//        }
//        else if(!file.canWrite())
//        {
//            throw new IOException("Cannot write to file: " + file + ", needs write-permission");
//        }

        this.outStream = outputStream;
    }

    public void write(LoggableData data)
    {
        writeQueue.add(data);
    }

    public void cancel()
    {
        this.running = false;
    }

    @Override
    public void run() {
        running = true;
        while(running)
        {
            LoggableData data = writeQueue.poll();
            if(data == null)
            {
                try
                {
                    Thread.sleep(100);
                }
                catch(InterruptedException e)
                {
                    running = false;
                }
            }
            else
            {
                try
                {
                    writeToFile(data);
                    sampleCountSinceSync++;
                    if(sampleCountSinceSync >= syncEveryNSamples)
                    {
                        sampleCountSinceSync = 0;
                        outStream.flush();
                    }
                }
                catch(Exception e)
                {
                    Log.e(TAG, "Exception writing to file", e);
                }
            }
        }

        try
        {
            //Write the rest of the entries still possibly in the queue
            LoggableData data = writeQueue.poll();
            while(data != null)
            {
                writeToFile(data);
                data = writeQueue.poll();
            }

            outStream.flush();
            outStream.close();
        }
        catch(IOException e)
        {
            Log.e(TAG, "Exception while flushing & closing file", e);
        }
    }

    private void writeToFile(LoggableData data) throws IOException {
        switch(data.getLogDataType())
        {
            case BINARY:
                outStream.write(data.getLogData());
                break;

            case STRING:
                outStream.write(data.getLogEntry().getBytes());
                break;

            default:
                Log.w(TAG, "Unknown or unsupported log data type: " + data.getLogDataType().name());
                break;
        }
    }
}
