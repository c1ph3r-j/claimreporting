package com.aki.claimreporting;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {
    public static final String PERMISSION_ACCESS_BACKGROUND_LOCATION = Manifest.permission.ACCESS_BACKGROUND_LOCATION;
    public static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_ACCESS_MEDIA_LOCATION = Manifest.permission.ACCESS_MEDIA_LOCATION;
    public static final String PERMISSION_ACCESS_NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
    public static final String PERMISSION_ACCESS_NOTIFICATION_POLICY = Manifest.permission.ACCESS_NOTIFICATION_POLICY;
    public static final String PERMISSION_ACCESS_WIFI_STATE = Manifest.permission.ACCESS_WIFI_STATE;
    public static final String PERMISSION_BLUETOOTH = Manifest.permission.BLUETOOTH;
    public static final String PERMISSION_BLUETOOTH_ADMIN = Manifest.permission.BLUETOOTH_ADMIN;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_CHANGE_WIFI_MULTICAST_STATE = Manifest.permission.CHANGE_WIFI_MULTICAST_STATE;
    public static final String PERMISSION_CHANGE_WIFI_STATE = Manifest.permission.CHANGE_WIFI_STATE;
    public static final String PERMISSION_DISABLE_KEYGUARD = Manifest.permission.DISABLE_KEYGUARD;
    public static final String PERMISSION_EXPAND_STATUS_BAR = Manifest.permission.EXPAND_STATUS_BAR;
    public static final String PERMISSION_FOREGROUND_SERVICE = Manifest.permission.FOREGROUND_SERVICE;
    public static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    public static final String PERMISSION_INSTALL_PACKAGES = Manifest.permission.INSTALL_PACKAGES;
    public static final String PERMISSION_INTERNET = Manifest.permission.INTERNET;
    public static final String PERMISSION_KILL_BACKGROUND_PROCESSES = Manifest.permission.KILL_BACKGROUND_PROCESSES;
    public static final String PERMISSION_LOCATION_HARDWARE = Manifest.permission.LOCATION_HARDWARE;
    public static final String PERMISSION_MANAGE_DOCUMENTS = Manifest.permission.MANAGE_DOCUMENTS;
    public static final String PERMISSION_MASTER_CLEAR = Manifest.permission.MASTER_CLEAR;
    public static final String PERMISSION_READ_CALENDAR = Manifest.permission.READ_CALENDAR;
    public static final String PERMISSION_READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_READ_PHONE_NUMBERS = Manifest.permission.READ_PHONE_NUMBERS;
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_READ_SMS = Manifest.permission.READ_SMS;
    public static final String PERMISSION_RECEIVE_MMS = Manifest.permission.RECEIVE_MMS;
    public static final String PERMISSION_RECEIVE_SMS = Manifest.permission.RECEIVE_SMS;
    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_REQUEST_INSTALL_PACKAGES = Manifest.permission.REQUEST_INSTALL_PACKAGES;
    public static final String PERMISSION_SEND_SMS = Manifest.permission.SEND_SMS;
    public static final String PERMISSION_SET_ALARM = Manifest.permission.SET_ALARM;
    public static final String PERMISSION_SET_WALLPAPER = Manifest.permission.SET_WALLPAPER;
    public static final String PERMISSION_VIBRATE = Manifest.permission.VIBRATE;
    public static final String PERMISSION_WAKE_LOCK = Manifest.permission.WAKE_LOCK;
    public static final String PERMISSION_WRITE_CALENDAR = Manifest.permission.WRITE_CALENDAR;
    public static final String PERMISSION_WRITE_CONTACTS = Manifest.permission.WRITE_CONTACTS;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final Activity activity;
    private final int requestCode = 1016;
    private PermissionResultListener listener;

    public PermissionManager(Activity activity) {
        this.activity = activity;
    }

    public boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    public void requestPermission(String permission) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }

    public void requestPermissions(String[] permissions) {
        if (!(getMissingPermissions(permissions).length == 0)) {
            ActivityCompat.requestPermissions(activity, getMissingPermissions(permissions), requestCode);
        }
    }

    public String[] getMissingPermissions(String[] permissions) {
        List<String> missingPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                missingPermissions.add(permission);
            }
        }
        return missingPermissions.toArray(new String[0]);
    }

    public void setPermissionResultListener(PermissionResultListener listener) {
        this.listener = listener;
    }

    public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
        boolean allPermissionGranted = true;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                allPermissionGranted = false;
                break;
            }
        }

        if (allPermissionGranted && listener != null) {
            listener.onPermissionGranted();
        } else if (!allPermissionGranted && listener != null) {
            listener.onPermissionDenied();
        }
    }

    public void handleSettingsActivityResult(String[] permissions, int requestCode, int resultCode) {
        if (requestCode == this.requestCode) {
            // Check permission status after returning from settings
            if (hasPermissions(permissions) && listener != null) {
                listener.onPermissionGranted();
            } else if (listener != null) {
                listener.onPermissionDenied();
            }
        }
    }


    public void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, requestCode);
    }


    public void showPermissionExplanationDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(R.string.permissions_required_title);
        dialog.setMessage(R.string.permissions_required);
        dialog.setPositiveButton("Go to Settings", (dialogInterface, i) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            startAppSettings(); // Start settings activity for result
        });
        dialog.setNegativeButton("Cancel", (dialogInterface, i) -> {
            activity.finish();
            dialogInterface.dismiss();
        });
        dialog.setCancelable(false);
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    public void showPermissionExplanationDialogC() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(R.string.permissions_required_title);
        dialog.setMessage(R.string.permissions_required);
        dialog.setPositiveButton("Go to Settings", (dialogInterface, i) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            startAppSettings(); // Start settings activity for result
        });
        dialog.setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        dialog.setCancelable(false);
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    public interface PermissionResultListener {
        void onPermissionGranted();

        void onPermissionDenied();
    }
}

