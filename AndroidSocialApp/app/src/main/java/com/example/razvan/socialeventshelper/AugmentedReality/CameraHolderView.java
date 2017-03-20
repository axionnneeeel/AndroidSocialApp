package com.example.razvan.socialeventshelper.AugmentedReality;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.List;

/**
 * Created by Razvan on 3/11/2017.
 */


@SuppressWarnings("deprecation")
public class CameraHolderView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 1;

    private Camera cameraInstance;
    private SurfaceHolder surfaceHolder;
    private Activity myActivity;
    private Context context;

    public CameraHolderView(Context context, Activity activity) {
        super(context);

        myActivity = activity;
        this.context = context;
        surfaceHolder = getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        cameraInstance = Camera.open();

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, cameraInfo);

        int rotation = myActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        cameraInstance.setDisplayOrientation((cameraInfo.orientation - degrees + 360) % 360);

        try {
            cameraInstance.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters cameraParams = cameraInstance.getParameters();
        List<Camera.Size> prevSizes = cameraParams.getSupportedPreviewSizes();
        for (Camera.Size s : prevSizes) {
            if ((s.height <= height) && (s.width <= width)) {
                cameraParams.setPreviewSize(s.width, s.height);
                break;
            }

        }

        cameraInstance.setParameters(cameraParams);
        cameraInstance.startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraInstance.stopPreview();
        cameraInstance.release();
    }

}