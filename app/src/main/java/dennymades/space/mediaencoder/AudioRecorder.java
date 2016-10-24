package dennymades.space.mediaencoder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by abrain on 10/24/16.
 * a wrapper around android's AudioRecord class
 * meant to record audio from microphone input
 * to be operated from the a HandlerThread (AudioRecorderHandlerThread)
 */
public class AudioRecorder {
    private static final String TAG = AudioRecorder.class.getSimpleName();
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private static final int SAMPLES_PER_FRAME = 1024;
    private static final int FRAMES_PER_BUFFER = 25;

    private int bufferSizeInBytes;

    private AudioRecord mAudioRecord;

    private boolean isRecording;

    public AudioRecorder(){
        bufferSizeInBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT);

        //todo understand this logic
        int bufferSize = SAMPLES_PER_FRAME * FRAMES_PER_BUFFER;
        if(bufferSize<bufferSizeInBytes){
            bufferSize = ((bufferSizeInBytes/SAMPLES_PER_FRAME)+1)*SAMPLES_PER_FRAME*2;
        }

        mAudioRecord = new AudioRecord(AUDIO_SOURCE,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSizeInBytes);
        isRecording = false;
    }

    public void startRecording(){
        StringBuilder string = new StringBuilder();

        mAudioRecord.startRecording();
        isRecording = true;

        final ByteBuffer bytebuffer = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
        int bufferReadResult;

        while(isRecording){
            bufferReadResult = mAudioRecord.read(bytebuffer,SAMPLES_PER_FRAME);

            if(bufferReadResult==AudioRecord.ERROR_INVALID_OPERATION || bufferReadResult==AudioRecord.ERROR_BAD_VALUE){
                Log.d(TAG, "audio record read error");
            }else if(bufferReadResult>0){
                Log.d(TAG, "bytes read "+bufferReadResult);
            }
            // todo send this byte array to an audio encoder
        }
    }

    public void stopRecording(){
        mAudioRecord.stop();
        mAudioRecord.release();
    }

    public void setIsRecordingFalse(){
        isRecording = false;
    }
}
