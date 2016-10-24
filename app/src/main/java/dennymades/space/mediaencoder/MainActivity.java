package dennymades.space.mediaencoder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import util.Compatibility;
import util.Constants;
import util.Messages;
import util.Permission;

public class MainActivity extends AppCompatActivity implements Handler.Callback{
    private static final String TAG = MainActivity.class.getSimpleName();
    private String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};
    private TextView tv;

    private AudioRecorderHandlerThread audioRecorderHandlerThread;

    private Handler UIHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean permissionGranted = Permission.checkPermission(this, permissions);
        if(!permissionGranted){
            Permission.seekPermission(this, permissions, Permission.PERMISSION_ALL);
        }

        tv = (TextView) findViewById(R.id.textView);
        UIHandler = new Handler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        audioRecorderHandlerThread.setCallback(null);
        if(Compatibility.isCompatible(16))
            audioRecorderHandlerThread.quit();
        else
            audioRecorderHandlerThread.quitSafely();

    }

    @Override
    protected void onResume() {
        super.onResume();
        audioRecorderHandlerThread = new AudioRecorderHandlerThread("Audio Recorder Thread");
        audioRecorderHandlerThread.setCallback(UIHandler);
        audioRecorderHandlerThread.start();
    }

    /**
     * Callback for the result from requesting mediaencoder. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the mediaencoder request interaction
     * with the user is interrupted. In this case you will receive empty mediaencoder
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested mediaencoder. Never null.
     * @param grantResults The grant results for the corresponding mediaencoder
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case Permission.PERMISSION_ALL:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "camera permission granted");
                }
                if(grantResults.length>0 && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "storage write permission granted");
                }
                if(grantResults.length>0 && grantResults[2]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "audio permission granted");
                }
                break;
        }
    }

    /* Callback method to recieve messages on the UI Thread from other handlers */

    @Override
    public boolean handleMessage(Message message) {
        switch(message.what){
            case(Messages.MSG_RECORDING_START_CALLBACK):
                Log.d(TAG, "message recording started callback in the UI thread");
                break;
            case(Messages.MSG_RECORDING_STOP_CALLBACK):
                Log.d(TAG, "message recording stopped callback in the UI thread");
                break;
        }
        return false;
    }

    public void onUIBtnClicked(View v){
        Log.d(TAG, "clicked");
        int t = Integer.parseInt(v.getTag().toString());
        switch(t){
            case Constants.ButtonConstants.AUDIO_START_RECORD_BUTTON:
                Log.d(TAG, "record start clicked");
                audioRecorderHandlerThread.startRecording();
                break;
            case Constants.ButtonConstants.AUDIO_STOP_RECORD_BUTTON:
                Log.d(TAG, "record stop clicked");
                audioRecorderHandlerThread.stopRecording();
                break;
        }
    }
}
