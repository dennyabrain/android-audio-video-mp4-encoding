package dennymades.space.mediaencoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

import util.FileManager;

/**
 * Created by abrain on 10/25/16.
 */
public class MediaMuxerWrapper {
    private static final String TAG = MediaMuxerWrapper.class.getSimpleName();
    private MediaMuxer muxer;
    private boolean isMuxing;
    private String outputFile;
    private MediaFormat audioFormat;
    private int audioTrackIndex;

    public MediaMuxerWrapper(){
        outputFile = FileManager.getOutputMediaFile(2).toString();
        try {
            muxer = new MediaMuxer(outputFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            Log.d(TAG, "exception creating new media muxer ", e);
        }
    }

    public void addAudioEncoder(AudioEncoder encoder){
        audioFormat = encoder.getEncoder().getOutputFormat();
        audioTrackIndex = muxer.addTrack(audioFormat);
        Log.d(TAG, "added audio track");
    }

    public void startMuxing(){
        isMuxing = true;
        muxer.start();
    }

    public void stopMuxing(){
        isMuxing = false;
        muxer.stop();
        muxer.release();
    }

    public void muxAudio(ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo){
        try{
            muxer.writeSampleData(audioTrackIndex, buffer, bufferInfo);
        }catch(IllegalArgumentException e){
            Log.d(TAG, "argument to writeSampleData incorrect : ",e);
        }catch(IllegalStateException e){
            Log.d(TAG, "muxer in illegal state : ",e);
        }
    }
}
