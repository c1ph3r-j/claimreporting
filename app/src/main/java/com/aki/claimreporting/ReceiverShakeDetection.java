package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReceiverShakeDetection extends AppCompatActivity implements View.OnClickListener {

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    private static final String TAG = "Receiver_BT";
    private static final String APP_NAME = "ShakeDetection";
    // private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
    private static final UUID MY_UUID = UUID.fromString("4b84cd18-ed64-11ec-8ea0-0242ac120002");
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };
    public CountDownTimer countdown;
    LinearLayout connectManually, proceedButton;
    TextView alertToManualConnect;
    TextView msg_box, status;
    String QRCODE_VAL;
    ProgressDialog progressdialog;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;
    FirebaseCrashlytics mCrashlytics;
    SendReceive sendReceive;
    int REQUEST_ENABLE_BLUETOOTH = 1;
    BluetoothAdapter mBluetoothAdapter;

    //    private void displayOptionToManualConnect() {
//        Handler changeActivity = new Handler();
//        changeActivity.postDelayed(() -> {
//                    connectManually.setVisibility(View.VISIBLE);
//                    String textForReceiver = alertToManualConnect.getText() + getString(R.string.textForReceiver);
//                    alertToManualConnect.setText(textForReceiver);
//                }
//                , 20000);
//    }
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case STATE_LISTENING:
                    //  status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    //  status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    //   status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    //   status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    Log.i("Message", tempMsg + " RECEIVED");
                    QRCODE_VAL = tempMsg;
                    getQrCodeData();
//                    showDialogBox();

                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_shake_detection);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Receive Information");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        preventSizeChange(this, getSupportActionBar());

        mCrashlytics = FirebaseCrashlytics.getInstance();

        init();

        displayOptionToManualConnect();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(ReceiverShakeDetection.this);
            alert.setMessage("Your Device Does Not Contain Bluetooth Click Ok to Scan Qr Code!");
            alert.setTitle("Alert");
            alert.setCancelable(false);
            alert.setNegativeButton("OK", (dialogInterface, i) -> {
                startActivityForResult(new Intent(ReceiverShakeDetection.this, ScanQrCode.class), 0);
            });
            alert.show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
            enablediscover();
            receiveserverlisten();
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    public void displayOptionToManualConnect() {

        countdown = new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
//                long timerfunc = (millisUntilFinished / 1000);
//                if(timerfunc == 17)
//                {
//                   status.setText("Started.Please wait");
//               }
//              else if(timerfunc == 14)
//               {
//                   status.setText("Started.Next Step going on");
//               }
//                else if(timerfunc == 8)
//                {
//                   status.setText("Done. Almost.");
//              }
//                else if(timerfunc <= 8)
//              {
//                    status.setText("Going to Completed");
//               }
            }

            public void onFinish() {
                connectManually.setVisibility(View.VISIBLE);
                String textForReceiver = alertToManualConnect.getText() + getString(R.string.textForReceiver);
                alertToManualConnect.setText(textForReceiver);
            }
        }.start();
    }

    void init() {
        connectManually = findViewById(R.id.ConnectManually);
        proceedButton = findViewById(R.id.nextBtn);
        alertToManualConnect = findViewById(R.id.manualConnectText);
        msg_box = findViewById(R.id.msg);
        msg_box.setVisibility(View.GONE);
        status = findViewById(R.id.status);
        status.setText("We are receiving the third party information to your mobile, please wait for 15 seconds.");
        proceedButton.setOnClickListener(this);
        connectManually.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (proceedButton.getId() == view.getId()) {
            startActivityForResult(new Intent(ReceiverShakeDetection.this, ScanQrCode.class), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferences sharedpreferences = getSharedPreferences("ShareValPref", Context.MODE_PRIVATE);
        int status = sharedpreferences.getInt("status", 0);
        if (status == 1) {
            QRCODE_VAL = sharedpreferences.getString("QrCode", "");
            if (!(QRCODE_VAL == null)) {
                getQrCodeData();
//                showDialogBox();
            }
        } else {
            Toast.makeText(this, "please Scan a Qr code.", Toast.LENGTH_SHORT).show();
        }
    }

    public void enablediscover() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 5000);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2, intentFilter);
    }

    public void receiveserverlisten() {
        ServerClass serverClass = new ServerClass();
        serverClass.start();

    }

    public void getQrCodeData() {

        try {

            DatabaseHelper mydb = new DatabaseHelper(ReceiverShakeDetection.this);
            if (mydb.getTokendetails().getCount() != 0) {
                Cursor curseattachtoken = mydb.getTokendetails();
                int counttoken = curseattachtoken.getCount();
                if (counttoken >= 1) {
                    while (curseattachtoken.moveToNext()) {
                        MainActivity.stokenval = curseattachtoken.getString(1);
                    }
                }
            }

            if (isNetworkConnected()) {

                if (checkGPSStatus()) {
                    String rooteddevice;
                    if (RootUtil.isDeviceRooted()) {
                        rooteddevice = "1";
                    } else {
                        rooteddevice = "0";
                    }


                    String phnencrypted = "";

                    Thread thread = new Thread(() -> {
                        MainActivity.appurl = getString(R.string.uaturl) + "/app/IncidentsMapping/InsertIncidentsMapping";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");

                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();

                        JsonObject Details = new JsonObject();
                        try {
                            Details.addProperty("qRCode", QRCODE_VAL);
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "getQRCodeData", e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        String parms = MainActivity.InsertMobileParameters();
                        Request request = new Request.Builder()
                                .url(MainActivity.appurl)
                                .addHeader("Authorization", "Bearer " + MainActivity.stokenval)
                                .addHeader("MobileParameter", parms)
                                .post(body)
                                .build();

                        Response staticResponse = null;


                        try {
                            runOnUiThread(() -> progressdialog = ProgressDialog.show(ReceiverShakeDetection.this, "Loading", "Please wait...", true));
                            staticResponse = client.newCall(request).execute();
                            String staticRes = staticResponse.body().string();
                            Log.i(null, staticRes);
                            final JSONObject staticJsonObj = new JSONObject(staticRes);


                            if (staticJsonObj.getInt("rcode") == 1) {

                                runOnUiThread(() -> {
                                    progressdialog.dismiss();

                                    try {
                                        showDialogBox();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "getQrCodeData", e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }

                                });
                            } else if (staticJsonObj.getInt("rcode") == 401) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog.dismiss();
                                        return;
                                    }
                                });
                            } else {
                                try {
                                    runOnUiThread(progressdialog::dismiss);
                                    JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                    JSONObject index = rmsg.getJSONObject(0);
                                    runOnUiThread(() -> {
                                        String errorText = null;
                                        String trnId = null;
                                        try {
                                            errorText = index.getString("errorText");
                                            trnId = staticJsonObj.getString("trnID");
                                            AlertDialog.Builder alert = new AlertDialog.Builder(ReceiverShakeDetection.this);
                                            alert.setCancelable(false);
                                            alert.setMessage(errorText);
                                            alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                                            alert.show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (final IOException | JSONException e) {

                            //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressdialog.dismiss();
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "getQrCodeData", e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
//                                    MainActivity.MobileErrorLog("UserRegistration-getuserexistapi",ex.toString(),ex.toString());
//                                    mCrashlytics.recordException(ex);
                                    Toast.makeText(ReceiverShakeDetection.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "getQrCodeData", e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    });
                    thread.start();


                } else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ReceiverShakeDetection.this);
                    dialog.setMessage("GPS locations is not enabled.Please enable it");
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //this will navigate user to the device location settings screen
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    android.app.AlertDialog alert = dialog.create();
                    alert.show();
                }

            } else {
                Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //progressdialog.dismiss();
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "getQrCodeData", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
//            mCrashlytics.recordException(ex);
//            MainActivity.MobileErrorLog("UserRegistration-getuserexistapi",ex.toString(),ex.toString());
            Toast.makeText(ReceiverShakeDetection.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
            //MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }

    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean checkGPSStatus() {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled && !network_enabled) {
            return false;

        } else {
            return true;
        }

    }

    void showDialogBox() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Alert!")
                .setMessage("Information received from the Third Party has been successfully received. Click on the Proceed to continue.")
                .setPositiveButton(getString(R.string.proceed_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(ReceiverShakeDetection.this, ClaimVisualArtifacts.class));
                            }
                        })
//                .setNegativeButton("Receive Again",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                startActivity(new Intent(ReceiverShakeDetection.this, ThirdPartyShare.class));
//                            }
//                        })
                .create();
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        try {
            //noinspection SimplifiableIfStatement
            if (id == R.id.action_name) {

                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                View screenView = rootView.getRootView();
                screenView.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
                screenView.setDrawingCacheEnabled(false);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                String encodedimg = Base64.encodeToString(byteArray, Base64.DEFAULT);
                SharedPreferences sharedpreferences = getSharedPreferences("SupportPrefName", Context.MODE_PRIVATE);
                SharedPreferences.Editor supporteditor = sharedpreferences.edit();
                supporteditor.putString(MainActivity.ReferrenceURL, "Service Provider");
                supporteditor.apply();
                SharedPreferences sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
                SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
                supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
                supporteditorimg.apply();
                Intent login = new Intent(ReceiverShakeDetection.this, SupportTicket.class);
                startActivity(login);
                return true;

                // Do something

            } else {
                onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        return super.onOptionsItemSelected(item);
    }

    private class ServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                try {
                    serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
                } catch (IOException e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "ServerClass", e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            String methodName = new Object() {
            }.getClass().getEnclosingMethod().getName();
            BluetoothSocket socket = null;

            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if (socket != null) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive = new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1) {
            String methodName = Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName();

            device = device1;

            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        }


        public void run() {
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive = new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[2048];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "SendReceive", e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "write", e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        }
    }

}