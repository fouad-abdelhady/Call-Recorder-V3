package hk.ust.cse.comp107x.risecallrecorder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CallRecordingService extends Service {
    private static MediaRecorder rec;
    private boolean recordStarted;
    private File file;
    PhoneStateListener listener = new PhoneStateListener();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(MainActivity.LOG_D_KEY, "In on bind now");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(MainActivity.LOG_D_KEY, "in on start command before Record()");
        Record();

        return START_STICKY;
    }

    private void Record() {
        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
        CharSequence sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        rec = new MediaRecorder();
        rec.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        rec.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        rec.setOutputFile(file.getAbsolutePath()+"/"+sdf+"rec.3gp");
        rec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        TelephonyManager manager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        PhoneStateListener callStateListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber)
            {
                if(state == TelephonyManager.CALL_STATE_IDLE && rec == null){
                    Log.d(MainActivity.LOG_D_KEY, "Recording will stop");
                    rec.stop();
                    rec.reset();
                    rec.release();
                    Log.d(MainActivity.LOG_D_KEY, "recording stopped");
                    recordStarted = false;
                    stopSelf();
                }
                else if(state==TelephonyManager.CALL_STATE_OFFHOOK){
                    try {
                        Log.d(MainActivity.LOG_D_KEY, "prepare");
                        rec.prepare();
                    } catch (IOException e) {
                        Log.d(MainActivity.LOG_D_KEY, "ERROR DURING PREPARING PROCESS");
                        e.printStackTrace();
                    }
                    Log.d(MainActivity.LOG_D_KEY, "will Start recording");
                    rec.start();
                    Log.d(MainActivity.LOG_D_KEY, "recording started");
                    recordStarted = true;
                }

            }
        };
        manager.listen(callStateListener,PhoneStateListener.LISTEN_CALL_STATE);

    }

    @Override
    public void onDestroy() {
        try{
            rec.stop();
            rec.reset();
            rec.release();
            Log.d(MainActivity.LOG_D_KEY, "recording stopped and saved");
        }catch (Exception e){

        }
        super.onDestroy();
    }
}
