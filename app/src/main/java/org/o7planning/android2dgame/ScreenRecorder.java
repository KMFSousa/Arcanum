package org.o7planning.android2dgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class ScreenRecorder {

    private MediaRecorder mediaRecorder;
    private Activity mainActivity;
    private static final int PERMISSION_CODE = 1;
    private int displayWidth;
    private int displayHeight;
    private int screenDensity;
    public boolean isRecording;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private MediaProjectionManager projectionManager;
    private MediaProjectionCallback mediaProjectionCallback;

    public ScreenRecorder(Activity mainActivity){
        DisplayMetrics metrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.isRecording = false;
        this.displayHeight = metrics.heightPixels;
        this.displayWidth = metrics.widthPixels;
        this.screenDensity = metrics.densityDpi;
        this.mainActivity = mainActivity;
        this.projectionManager = (MediaProjectionManager) mainActivity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        this.mediaProjectionCallback = new MediaProjectionCallback();
        initRecorder();
        prepareRecorder();

    }

    private void initRecorder() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setVideoEncodingBitRate(2000 * 1000);
        mediaRecorder.setVideoFrameRate(60);
        mediaRecorder.setMaxDuration(30000);
        mediaRecorder.setVideoSize(displayWidth, displayHeight);
        mediaRecorder.setOutputFile(getFilePath());

    }

    private void prepareRecorder() {
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    public void startRecording() {
        if (mediaProjection == null) {
            mainActivity.startActivityForResult(projectionManager.createScreenCaptureIntent(), PERMISSION_CODE);
            return;
        }
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
        isRecording = true;
    }

    public void stopRecording(){
        mediaRecorder.stop();
        isRecording = false;
        mediaRecorder.reset();
        Log.v("ScreenRecorder", "Recording Stopped");
        if (virtualDisplay == null) {
            return;
        }
        virtualDisplay.release();
        initRecorder();
        prepareRecorder();
    }

    public String getFilePath() {
        final String directory = Environment.getExternalStorageDirectory() + File.separator + "Arcanum_Recordings";
        Log.d("Storage", directory);
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(mainActivity, "Failed to get External Storage", Toast.LENGTH_SHORT).show();
            return null;
        }
        final File folder = new File(directory);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        String filePath;
        if (success) {
            String videoName = ("capture_" + getCurSysDate() + ".mp4");
            filePath = directory + File.separator + videoName;
        } else {
            Toast.makeText(mainActivity, "Failed to create Recordings directory", Toast.LENGTH_SHORT).show();
            return null;
        }
        return filePath;
    }

    public String getCurSysDate() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CANADA).format(new Date());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode != PERMISSION_CODE) {
            Log.e("ScreenRecorder", "Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(mainActivity, "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();

            return;
        }
        mediaProjection = projectionManager.getMediaProjection(resultCode, data);
        mediaProjection.registerCallback(mediaProjectionCallback, null);
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
        isRecording = true;
    }

    public void onDestroy(){
        if (mediaProjection != null) {
            mediaProjection.stop();
            mediaProjection = null;
        }
    }

    private VirtualDisplay createVirtualDisplay() {
        return mediaProjection.createVirtualDisplay("MainActivity",
                displayWidth, displayHeight, screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder.getSurface(), null /*Callbacks*/, null /*Handler*/);
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.v("ScreenRecorder", "Recording Stopped");
            initRecorder();
            prepareRecorder();
            mediaProjection = null;
            stopRecording();
            Log.i("ScreenRecorder", "MediaProjection Stopped");
        }
    }


}
