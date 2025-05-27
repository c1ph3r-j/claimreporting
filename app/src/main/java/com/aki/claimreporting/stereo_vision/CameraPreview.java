package com.aki.claimreporting.stereo_vision;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.aki.claimreporting.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class CameraPreview extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private CaptureRequest.Builder captureRequestBuilder;
    private CameraCaptureSession captureSession;
    private Intent checkImagesIntent;
    private Size imageDimension;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private CameraDevice mTelephotoCameraDevice;
    private String mTelephotoCameraId;
    private ImageReader mTelephotoImageReader;
    private CameraDevice mWideCameraDevice;
    private String mWideCameraId;
    private ImageReader mWideImageReader;
    private CameraManager manager;
    private ProgressBar progressBar;
    private AutoFitTextureView textureView;
    private ImageView backBtn;

    private ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = null;
            FileOutputStream output = null;
            try {
                image = reader.acquireLatestImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);

                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "StereoImages");
                if (!dir.exists() && dir.mkdirs()) {
                    System.out.println("Directory Created!");
                }

                String storedImageName = "IMG_";
                if (mWideCameraDevice == null) {
                    storedImageName += "TEL_";
                } else {
                    storedImageName += "WID_";
                }
                storedImageName += new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";

                output = new FileOutputStream(new File(dir, storedImageName));
                output.write(bytes);

                if (mWideCameraDevice != null) {
                    checkImagesIntent.putExtra("WID_IMAGE", storedImageName);
                } else {
                    checkImagesIntent.putExtra("TEL_IMAGE", storedImageName);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (image != null) {
                    image.close();
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private CameraDevice.StateCallback mTelephotoStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mTelephotoCameraDevice = cameraDevice;
            startCaptureSession(mTelephotoCameraDevice, mTelephotoImageReader.getSurface(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    Log.d("CameraPreview", "Telephoto Image captured");
                    closeCamera(mTelephotoCameraDevice);
                    mTelephotoCameraDevice = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            startActivity(checkImagesIntent);
                            finish();
                        }
                    });
                }
            });
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
            mTelephotoCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            Log.e("CameraPreview", "Telephoto Camera error occurred: " + error);
            closeCamera(cameraDevice);
            mTelephotoCameraDevice = null;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    };

    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mWideCameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            mWideCameraDevice.close();
            mWideCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            mWideCameraDevice.close();
            mWideCameraDevice = null;
        }
    };

    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            closeCamera(mWideCameraDevice);
            closeCamera(mTelephotoCameraDevice);
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
        ActionBar support = getSupportActionBar();
        if(support != null) {
            support.hide();
        }

        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        try {
            textureView = findViewById(R.id.surfaceView);
            textureView.setSurfaceTextureListener(textureListener);
            progressBar = findViewById(R.id.progressBar);
            backBtn = findViewById(R.id.btnBack);

            backBtn.setOnClickListener(onClickBack ->
                    finish()
            );
            checkImagesIntent = new Intent(this, ImageCheckerActivity.class);
            initBackgroundThread();
            textureView.setAspectRatio(3, 4);

            CardView btnCapture = findViewById(R.id.btnCapture);
            btnCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        progressBar.setVisibility(View.VISIBLE);
                        captureImagesInBackground();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBackgroundThread() {
        if (mBackgroundThread != null) {
            mBackgroundThread.quitSafely();
            mBackgroundThread = null;
        }
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void closeCamera(CameraDevice cameraDevice) {
        if (cameraDevice != null) {
            cameraDevice.close();
        }
    }

    @Override
    protected void onDestroy() {
        closeCamera(mWideCameraDevice);
        closeCamera(mTelephotoCameraDevice);
        if (mBackgroundThread != null) {
            mBackgroundThread.quitSafely();
        }
        super.onDestroy();
    }

    private void openCamera() {
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (hasTelephotoLens()) {
                for (String cameraId : manager.getCameraIdList()) {
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    Integer lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    float[] focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                    if (lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK && focalLengths != null && focalLengths.length > 0) {
                        float focalLength = focalLengths[0];
                        System.out.println(focalLength + " FOCAL LENGTH");
                        if (focalLength < 3.0f) {
                            if (map != null) {
                                imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
                            }
                            mWideCameraId = cameraId;
                        } else {
                            mTelephotoCameraId = cameraId;
                        }
                    }
                }
                if (mWideCameraId != null) {
                    mWideImageReader = ImageReader.newInstance(1920, 1080, ImageFormat.JPEG, 2);
                    mWideImageReader.setOnImageAvailableListener(onImageAvailableListener, mBackgroundHandler);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        manager.openCamera(mWideCameraId, stateCallback, mBackgroundHandler);
                    } else {
                        return;
                    }
                }
                if (mTelephotoCameraId != null) {
                    mTelephotoImageReader = ImageReader.newInstance(1920, 1080, ImageFormat.JPEG, 2);
                    mTelephotoImageReader.setOnImageAvailableListener(onImageAvailableListener, mBackgroundHandler);
                }
            } else {
                showNoTelephotoLensDialog();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private boolean hasTelephotoLens() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIds = manager.getCameraIdList();
            if (cameraIds.length < 3) {
                return false;
            }
            for (String cameraId : cameraIds) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                Integer lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                float[] focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                if (lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK && focalLengths != null) {
                    for (float focalLength : focalLengths) {
                        if (focalLength > 5.0f) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showNoTelephotoLensDialog() {
        new AlertDialog.Builder(this)
                .setTitle("No Telephoto Camera")
                .setMessage("Your device does not have a telephoto camera. The app will now close.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            if (texture == null) {
                throw new AssertionError();
            }
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = mWideCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            mWideCameraDevice.createCaptureSession(Arrays.asList(surface, mWideImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (null == mWideCameraDevice) {
                        return;
                    }
                    captureSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(CameraPreview.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (mWideCameraDevice == null) {
            return;
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            captureSession.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    private void startCaptureSession(CameraDevice cameraDevice, Surface surface, final CameraCaptureSession.CaptureCallback captureCallback) {
        try {
            final CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        session.capture(captureRequestBuilder.build(), captureCallback, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(CameraPreview.this, "Capture session failed", Toast.LENGTH_SHORT).show();
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void captureImagesInBackground() {
        try {
            if (mWideCameraDevice == null || mTelephotoCameraId == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
                Toast.makeText(this, "Not Available", Toast.LENGTH_SHORT).show();
                return;
            }
            startCaptureSession(mWideCameraDevice, mWideImageReader.getSurface(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    Log.d("CameraPreview", "Wide Image captured");
                    closeCamera(mWideCameraDevice);
                    mWideCameraDevice = null;
                    try {
                        if (ActivityCompat.checkSelfPermission(CameraPreview.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            manager.openCamera(mTelephotoCameraId, mTelephotoStateCallback, mBackgroundHandler);
                        }
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopBackgroundThread() {
        if (mBackgroundThread != null) {
            mBackgroundThread.quitSafely();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        initBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeCamera(mWideCameraDevice);
        closeCamera(mTelephotoCameraDevice);
        stopBackgroundThread();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                progressBar.setVisibility(View.VISIBLE);
                captureImagesInBackground();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("Camera permission is required to capture images. Please grant the permission in app settings.")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.fromParts("package", getPackageName(), null));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setCancelable(false)
                        .show();
            }
        }
    }
}