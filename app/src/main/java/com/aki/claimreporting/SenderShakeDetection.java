package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class SenderShakeDetection extends AppCompatActivity implements View.OnClickListener {


    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    private static final String TAG = "Sender_BT";
    private static final String APP_NAME = "ShakeDetection";
    // private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
    private static final UUID MY_UUID = UUID.fromString("4b84cd18-ed64-11ec-8ea0-0242ac120002");
    public CountDownTimer countdown;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    LinearLayout connectManually, proceedButton;
    TextView alertToManualConnect, status;
    ProgressDialog progressdialog;
    BluetoothDevice mBTDevice;
    Button listen, send, listDevices;
    ListView listView;
    TextView msg_box;
    ServerClass serverClass;
    EditText writeMsg;
    BluetoothConnectionService mBluetoothConnection;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;
    boolean shakedetect;
    SendReceive sendReceive;
    FirebaseCrashlytics mCrashlytics;
    BluetoothAdapter mBluetoothAdapter;
    int REQUEST_ENABLE_BLUETOOTH = 1;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case STATE_LISTENING:
                    //  status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    //  status.setText("Connecting");
                    try {
                        //MorningChange
                        Thread.sleep(3000);
                        finddeviclist();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case STATE_CONNECTED:
                    //  status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
//                    status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    msg_box.setText(tempMsg);
                    break;
            }
            return true;
        }
    });
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");
//            Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
//            String[] strings=new String[bt.size()];
//            btArray=new BluetoothDevice[bt.size()];
//            int index=0;

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getName() == null || device.getName() == "null" || device.getName() == "") {
                    Log.d(TAG, "onReceive: -  Device Name" + device.getName() + ": Device IP" + device.getAddress());
                } else {
                    //  mBTDevices.add(device);
                    Log.d(TAG, "onReceive: -  Device Name" + device.getName() + ": Device IP" + device.getAddress());
                    // mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                    //lvNewDevices.setAdapter(mDeviceListAdapter);
                    mBTDevices.add(device);
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        Log.d(TAG, "Trying to pair with " + device.getName());

                        String devname = device.getName();
                        String devname1 = devname;
                        mBTDevices.get(0).createBond();
                        mBTDevice = mBTDevices.get(0);
                        mBluetoothConnection = new BluetoothConnectionService(SenderShakeDetection.this);
                        try {

                            //MorningChange
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        serverlisten();


//                        for(BluetoothDevice devicenew : bt)
//                        {
//                            mBTDevices.get(0).createBond();
//                            mBTDevice = mBTDevices.get(0);
//                            mBluetoothConnection = new BluetoothConnectionService(Sender_BT.this);
//                            String testnew = devicenew.getName();;
//                            if(testnew.equals(device.getName()))
//                            {
//
//                            }
//                            else
//                            {
//                                mBTDevices.get(0).createBond();
//                                mBTDevice = mBTDevices.get(0);
//                                mBluetoothConnection = new BluetoothConnectionService(Sender_BT.this);
//                            }

                        // }

                    }
                }

            }
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//             serverlisten();
            //finddeviclist();
        }
    };
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > 15) {
                //Toast.makeText(getApplicationContext(), "Shake event detected", Toast.LENGTH_SHORT).show();
//                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
//                } else {
//                    //deprecated in API 26
//                    v.vibrate(500);
//                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if ((checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED)) {
                        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (mBluetoothAdapter == null) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(SenderShakeDetection.this);
                            alert.setMessage("Your Device Does Not Contain Bluetooth Click Ok to Share Qr Code!");
                            alert.setTitle("Alert");
                            alert.setCancelable(false);
                            alert.setNegativeButton("OK", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                startActivity(new Intent(SenderShakeDetection.this, ThirdPartyQrCodeGenerate.class));
                            });
                            alert.show();
                        } else {
                            mBluetoothAdapter.enable();
                            discoverdevice();
                        }
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(SenderShakeDetection.this);
                        alert.setMessage("Bluetooth Permission is Required to Continue!");
                        alert.setTitle("Alert");
                        alert.setCancelable(false);
                        alert.setNegativeButton("OK", (dialogInterface, i) -> {
                            startActivity(new Intent(
                                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null
                                    )));
                        });
                        alert.show();
                    }
                } else {
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter == null) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(SenderShakeDetection.this);
                        alert.setMessage("Your Device Does Not Contain Bluetooth Click Ok to Share Qr Code!");
                        alert.setTitle("Alert");
                        alert.setCancelable(false);
                        alert.setNegativeButton("OK", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            startActivity(new Intent(SenderShakeDetection.this, ThirdPartyQrCodeGenerate.class));
                        });
                        alert.show();
                    } else {
                        mBluetoothAdapter.enable();
                        discoverdevice();
                    }
                }
                //serverlisten();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_shake_detection);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Send Information");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        preventSizeChange(this, getSupportActionBar());
        shakedetect = true;
        init();

        displayOptionToManualConnect();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;


//         discoverdevice();
//         serverlisten();
//         serverlisten();
    }

    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    public void discoverdevice() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if (!mBluetoothAdapter.isDiscovering()) {

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    private void pairDevice(BluetoothDevice device) {
        try {

            //waitingForBonding = true;

            Method m = device.getClass()
                    .getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);

            // if (D)
            Log.d(TAG, "Pairing finished.");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    public void serverlisten() {
        serverClass = new ServerClass();
        serverClass.start();
    }

    public void finddeviclist() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        //Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
        Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
        // Set<BluetoothDevice> bt= getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        //    BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
        String[] strings = new String[bt.size()];
        btArray = new BluetoothDevice[bt.size()];
        int index = 0;

        if (bt.size() > 0) {
            for (BluetoothDevice device : bt) {
                btArray[index] = device;
                String testnew = device.getName();
                String testnew1 = testnew;
                String testnew2 = device.getAddress();
                strings[index] = device.getName();
                index++;
            }
            //  ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
            //listView.setAdapter(arrayAdapter);
        }
        if (bt.size() > 0) {
            for (int l = 0; l <= bt.size(); l++) {
                boolean newsh1 = shakedetect;
                if (shakedetect) {
                    try {
                        ClientClass clientClass = new ClientClass(btArray[l]);
                        clientClass.start();

                        //MorningChange
                        Thread.sleep(5000);
                        try {
                            //String string= String.valueOf(writeMsg.getText());
                            Thread.sleep(2000);
                            //   status.setText("Sending the Data");
                            sendReceive.write(ThirdPartyShare.ShareCode.getBytes());

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                            mCrashlytics.recordException(ex);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                        mCrashlytics.recordException(ex);
                    }
                } else {
                    ClientClass.currentThread().interrupt();
                    //  showSuccessDialogBox();
//                    Intent i = new Intent(SenderShakeDetection.this, ThirdPartyShare.class);
//                    startActivity(i);
                    break;

                }

            }

        }
    }

    private void findViewByIdes() {
        //listen=(Button) findViewById(R.id.listen);
        //  send=(Button) findViewById(R.id.send);
        // listView=(ListView) findViewById(R.id.listview);
        msg_box = (TextView) findViewById(R.id.msg);
        status = (TextView) findViewById(R.id.status);
        //writeMsg=(EditText) findViewById(R.id.writemsg);
        // listDevices=(Button) findViewById(R.id.listDevices);
    }

    private void displayOptionToManualConnect() {

        countdown = new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
//                long timerfunc = (millisUntilFinished / 1000);
//                if(timerfunc == 17)
//                {
//                    status.setText("Started.Please wait");
//                }
//                else if(timerfunc == 14)
//                {
//                    status.setText("Started.Next Step going on");
//                }
//                else if(timerfunc == 8)
//                {
//                    status.setText("Done. Almost.");
//                }
//                else if(timerfunc <= 8)
//                {
//                    status.setText("Going to Completed");
//                }

            }

            public void onFinish() {
                ClientClass.currentThread().interrupt();
                handler.removeCallbacks(null);
                connectManually.setVisibility(View.VISIBLE);
                shakedetect = false;
                String textForSender = alertToManualConnect.getText() + getString(R.string.textForSender);
                alertToManualConnect.setText(textForSender);
                showSuccessDialogBox();
            }
        }.start();
    }

    void init() {
        connectManually = findViewById(R.id.ConnectManually);
        proceedButton = findViewById(R.id.nextBtn);
        alertToManualConnect = findViewById(R.id.manualConnectText);
        status = findViewById(R.id.status);
        msg_box = (TextView) findViewById(R.id.msg);

        status.setText("We are sending your information to a third party, please wait for 15 seconds.");
        proceedButton.setOnClickListener(this);
        connectManually.setVisibility(View.GONE);
        msg_box.setVisibility(View.GONE);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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

    public void showSuccessDialogBox() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Alert!")
                .setMessage(getString(R.string.senddatadialog))
                .setPositiveButton("PROCEED",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(SenderShakeDetection.this, ClaimVisualArtifacts.class));
                            }
                        })
                .setNegativeButton("Show Qr Code",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .create();
        dialog.show();

    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(SenderShakeDetection.this, ThirdPartyQrCodeGenerate.class));
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
                Intent login = new Intent(SenderShakeDetection.this, SupportTicket.class);
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
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "ServerClass", e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        }

        public void run() {
            BluetoothSocket socket = null;

            while (socket == null) {
                if (shakedetect) {
                    try {
                        Message message = Message.obtain();
                        message.what = STATE_CONNECTING;
                        handler.sendMessage(message);
                        socket = serverSocket.accept();


                    } catch (IOException e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "ServerClass", e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                        ClientClass.currentThread().interrupt();
                        shakedetect = false;
//                    Intent i = new Intent(SenderShakeDetection.this, ThirdPartyShare.class);
//                    startActivity(i);
                        //displayOptionToManualConnect();
                        return;
//                    Message message=Message.obtain();
//                    message.what=STATE_CONNECTION_FAILED;
//                    handler.sendMessage(message);
                    }
                    if (socket != null) {
                        Message message = Message.obtain();
                        message.what = STATE_CONNECTED;
                        handler.sendMessage(message);

                        sendReceive = new SendReceive(socket);
                        sendReceive.start();

                    }
                } else {
                    return;
                }

            }
        }
    }

    private class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1) {
            device = device1;

            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "ClientClass", e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        }

        public void run() {
            try {
                if (socket != null) {
                    socket.connect();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                    shakedetect = false;
                }


            } catch (IOException e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "ClientClass", e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
                ClientClass.currentThread().interrupt();
                handler.removeCallbacks(null);
                shakedetect = false;
                return;
                //MorningChange
//                Message message=Message.obtain();
//                message.what=STATE_CONNECTION_FAILED;
//                handler.sendMessage(message);
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

            while (shakedetect) {
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
            if (bluetoothSocket.isConnected()) {
                try {
                    outputStream.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "Write", e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            }
        }
    }
}