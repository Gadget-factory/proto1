package plop.plop.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.bluetoothchat.MainActivity;
import com.example.android.bluetoothchat.R;
import com.scorchworks.demo.SimpleFileDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import plop.data.TelemetryFileServiceImpl;

/**
 *
 */
public class RecordButtonClickListener implements View.OnClickListener {

    private boolean recording = false;


    private Context context;
    private TelemetryFileServiceImpl telemetryFileService;

    public RecordButtonClickListener(Context context, TelemetryFileServiceImpl telemetryFileService)
    {
        this.context = context;
        this.telemetryFileService = telemetryFileService;
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v.findViewById(R.id.record);

        if(!recording) {
            recording = true;
            String dateStr = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            telemetryFileService.openNewFile("wheelemetrics_log_" + dateStr + ".txt");
            button.setText(R.string.stop_record);
        }
        else
        {
            recording = false;
            button.setText(R.string.record);
            SimpleFileDialog dialog = new SimpleFileDialog(context, "FileSave",
                    new SimpleFileDialog.SimpleFileDialogListener()
                    {

                        @Override
                        public void onChosenDir(String chosenDir)
                        {
                            File file = new File(chosenDir.trim());
                            telemetryFileService.finalizeFile(file);
                        }
                    });

            dialog.chooseFile_or_Dir(telemetryFileService.getCurrentFile().getParent());
        }
    }
}
