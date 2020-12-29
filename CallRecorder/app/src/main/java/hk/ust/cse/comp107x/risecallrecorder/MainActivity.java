package hk.ust.cse.comp107x.risecallrecorder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_D_KEY = "check123";
    ToggleButton recordingButton ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recordingButton = findViewById(R.id.toggleButton);
    }

    public void recording(View view) {
        Intent service = new Intent(this, CallRecordingService.class);
        if(recordingButton.isChecked()){
            Log.d(LOG_D_KEY, "starting service");
            startService(service);

        }
        else{
            Log.d(MainActivity.LOG_D_KEY, "stopping service");
            stopService(service);
        }
    }
}
