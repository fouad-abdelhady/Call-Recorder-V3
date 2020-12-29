package hk.ust.cse.comp107x.risecallrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Timer;

public class CallReceiver extends BroadcastReceiver {
    private static MediaRecorder recorder;
    private File audiofile;
    private String name, phonenumber;
    private String audio_format;
    private String Audio_Type;
    private int audioSource;
    private Context context;
    private Handler handler;
    private Timer timer;
    private Boolean offHook = false, ringing = false;
    private Toast toast;
    private Boolean isOffHook = false;
    private static boolean recordstarted = false;
    private static boolean wasRinging = false;

    private static final String ACTION_IN = "android.intent.action.PHONE_STATE";
    private static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";

    private Bundle bundle;
    private String state;
    private String inCall, outCall;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.i(MainActivity.LOG_D_KEY, "Receive successfully");
        bundle = intent.getExtras();
        if (intent.getAction().equals(ACTION_IN)) {
            Log.i(MainActivity.LOG_D_KEY, "ACTION IN");

            if (bundle != null) {

                Log.i(MainActivity.LOG_D_KEY, "Bundle != NULL");

                state = bundle.getString(TelephonyManager.EXTRA_STATE);

                Log.i(MainActivity.LOG_D_KEY, "state:" + state);

                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    Log.i(MainActivity.LOG_D_KEY, "Phone ringing");
                    inCall = bundle
                            .getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    wasRinging = true;
                    Toast.makeText(context, "IN : " + inCall, Toast.LENGTH_LONG)
                            .show();
                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    Log.i(MainActivity.LOG_D_KEY, "Phone off hook ");
                    if (wasRinging == true) {
                        recordNow();
                    }
                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    stopRecording();
                }
            }
        } else if (intent.getAction().equals(ACTION_OUT)) {
            Log.i(MainActivity.LOG_D_KEY, "ACTION_OUT");
            if ((bundle = intent.getExtras()) != null) {
                outCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Toast.makeText(context, "OUT : " + outCall, Toast.LENGTH_LONG)
                        .show();
            }
            if (!recordstarted) {
                recordNow();
                recordstarted = true;
            }
            state = bundle.getString(TelephonyManager.EXTRA_STATE);
            if (state != null && state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                stopRecording();
            }
        }

    }

    private void recordNow() {
        Log.i(MainActivity.LOG_D_KEY, "Phone was ringing");
        Toast.makeText(context, "ANSWERED", Toast.LENGTH_LONG)
                .show();

        File sampleDir = new File(
                Environment.getExternalStorageDirectory(),
                "/TestRecordingDasa");
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        String file_name = "Rec";
        try {
            audiofile = File.createTempFile(file_name, ".3gp",
                    sampleDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = Environment.getExternalStorageDirectory()
                .getAbsolutePath();

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        try {
            recorder.prepare();
            Log.i(MainActivity.LOG_D_KEY, "Prepared");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recordstarted = true;
        Log.i(MainActivity.LOG_D_KEY, "Start recording");
        recorder.start();
    }

    private void stopRecording() {
        Log.i(MainActivity.LOG_D_KEY, "State idle");
        wasRinging = false;
        Toast.makeText(context, "REJECT || DISCO",
                Toast.LENGTH_LONG).show();
        if (recordstarted) {
            recorder.stop();
            recorder.release();
            Log.i(MainActivity.LOG_D_KEY, "Record Stopped");
            recordstarted = false;
        }
    }
}
