package com.aki.claimreporting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClaimImage extends Fragment implements AdapterView.OnItemSelectedListener {


    private static final int Capture_Accident_IMAGE = 1234;
    private static final int Upload_Accident_IMAGE = 5678;
    private static final int Upload_Accident_DOCS = 7890;
    private static final int Capture_Accident_DOCS = 6574;
    public static DatabaseHelper mydb;
    public static byte[] regByte = null;
    public static String uploadimage;
    public static String stokenval;
    public static byte[] arrayaccidentdocs;
    public static String reqidval;
    public static TextView comments;
    public static EditText addspotcomments, accspotcomments;
    public static String commentsval;
    public static RadioGroup claimaccGroup;
    public static Spinner documenttype;
    public static String uniqueiddoc;
    public static LinearLayout viewadddoclin;
    public static RadioButton claimaccyesButton, claimaccnoButton;
    private static FirebaseCrashlytics mCrashlytics;
    public String encryptedSHA;
    public ProgressDialog progressdialog;
    public Bitmap uploadaccidentimages;
    public Bitmap insurer;
    public View rootview;
    public byte[] arrayaccidentimages;
    public int optionvisual = 0;
    public ImageView moredocclaim, moreimgclaim;
    public ListView list;
    public LinearLayout claimaccimagelin;
    public LinearLayout claimaccdoclin;
    public Button claimproceed, submitbutton, submitdocbutton;
    SharedPreferences sharedpreferences;
    Activity activity;
    String currentPhotoPath;
    ArrayList<AdditionalAttachclaimlist> attachimgist = new ArrayList<AdditionalAttachclaimlist>();
    String dateTime;

    public ClaimImage() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String localToGMT() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        String finalDateString = "";
        String validfromdate = "";
        try {
            // Get the current date and time in GMT
            Date currentDate = new Date();
            SimpleDateFormat gmtFormatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.US);
            gmtFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));

            finalDateString = gmtFormatter.format(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            // localToGMTNew();
        }
        return finalDateString;

    }

    public static String localToGMTNew() {

        DateFormat dftime = DateFormat.getTimeInstance();
        DateFormat dfdate = DateFormat.getDateInstance();
        dftime.setTimeZone(TimeZone.getTimeZone("gmt"));
        dfdate.setTimeZone(TimeZone.getTimeZone("gmt"));
        String gmtTime = dfdate.format(new Date()) + " " + dftime.format(new Date());

        String strDate = gmtTime;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        Date convertedDate = new Date();
        String finalDateString = "";
        try {
            convertedDate = dateFormat.parse(strDate);
            SimpleDateFormat sdfnewformat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
            finalDateString = sdfnewformat.format(convertedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return finalDateString;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_claim_image, container, false);
        rootview = inflater.inflate(R.layout.claim_image_fragment, container, false);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        init();
        return rootview;
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            documenttype = (Spinner) rootview.findViewById(R.id.spinnerdocsupload);

            viewadddoclin = (LinearLayout) rootview.findViewById(R.id.viewadddoc);
            viewadddoclin.setVisibility(View.GONE);
            documenttype.setOnItemSelectedListener(this);

            claimaccimagelin = (LinearLayout) rootview.findViewById(R.id.accimageupload);
            claimaccdoclin = (LinearLayout) rootview.findViewById(R.id.accdocsupload);


            claimaccGroup = (RadioGroup) rootview.findViewById(R.id.captuplodradoigrp);
            claimaccyesButton = (RadioButton) rootview.findViewById(R.id.radioovehbelyour1);
            claimaccnoButton = (RadioButton) rootview.findViewById(R.id.radiovehbelyour2);
            try {
                claimaccGroup.setOnCheckedChangeListener((radioGroup, i) -> {
                    if (i == R.id.radiouploadimg) {
                        claimaccimagelin.setVisibility(View.VISIBLE);
                        claimaccdoclin.setVisibility(View.GONE);
                        submitbutton.setVisibility(View.VISIBLE);
                        submitdocbutton.setVisibility(View.GONE);
                    } else if (i == R.id.radiouploaddoc) {
                        claimaccimagelin.setVisibility(View.GONE);
                        claimaccdoclin.setVisibility(View.VISIBLE);
                        submitbutton.setVisibility(View.GONE);
                        submitdocbutton.setVisibility(View.VISIBLE);
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getClassName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            submitdocbutton = (Button) rootview.findViewById(R.id.Btnclaimdocsubmit);

            submitbutton = (Button) rootview.findViewById(R.id.Btnclaimimgsubmit);
            accspotcomments = (EditText) rootview.findViewById(R.id.editaccspotcomments);
            addspotcomments = (EditText) rootview.findViewById(R.id.editadddoccomments);

//        claimproceed = (Button) rootview.findViewById(R.id.imgclaimproceed);
//        claimproceed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //    Intent login = new Intent(getActivity(), MyClaims.class);
//                // startActivity(login);
//                mydb = new DatabaseHelper(getActivity());
//                mydb.deleteClaimImgmor();
//                getActivity().finish();
//            }
//        });
            try {
                submitbutton.setOnClickListener(onClickSubmit -> {


                    if (Objects.equals(currentPhotoPath, "") || currentPhotoPath == null || currentPhotoPath.length() == 0) {
                        Toast.makeText(getActivity(), getString(R.string.uploadimage), Toast.LENGTH_SHORT).show();
                    }
//                else if (comments.getText().toString() == "" || comments.getText().toString() == null || comments.getText().toString().length() == 0) {
//                    Toast.makeText(getActivity(), getString(R.string.uploadcomments), Toast.LENGTH_SHORT).show();
//                    return;
//                }
                    else {
                        uniqueiddoc = "60d90614-9b38-44e1-bd19-b65264fdc362";
                        mydb = new DatabaseHelper(getActivity());
                        boolean Isinserted = mydb.insertclaimmoreimg(currentPhotoPath, "", "");
                        if (Isinserted == true) {
                            boolean test = Isinserted;
                            Log.i(null, "Insertion Done");
                        } else {
                            boolean test = Isinserted;
                            Log.i(null, "Not Insertion Done");
                        }
                        commentsval = accspotcomments.getText().toString();
                        //getimageslist();
                        uploadclaimimages(getActivity());
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getClassName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                submitdocbutton.setOnClickListener(submitDocButton -> {

                    if (documenttype.getSelectedItem().toString().contains("Select")) {
                        Toast.makeText(getActivity(), getString(R.string.selectdocumentype), Toast.LENGTH_SHORT).show();
                    } else if (Objects.equals(currentPhotoPath, "") || currentPhotoPath == null || currentPhotoPath.length() == 0) {
                        Toast.makeText(getActivity(), getString(R.string.uploadimage), Toast.LENGTH_SHORT).show();
                    } else {
                        commentsval = addspotcomments.getText().toString();
                        uploadclaimimages(getActivity());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getClassName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


            try {
                moreimgclaim = (ImageView) rootview.findViewById(R.id.imagemoreimgclaim);
                moreimgclaim.setOnClickListener(onClickMore -> {

                    showaccimagesUpload();
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getClassName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                moredocclaim = (ImageView) rootview.findViewById(R.id.imagemoredocclaim);
                moredocclaim.setOnClickListener(onClickMoreDoc -> {
                    showaccdocsUpload();
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getClassName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getClassName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public void showaccimagesUpload() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
        dialog.setMessage(getString(R.string.upload_or_capture_accident_spot_image));
        dialog.setPositiveButton("Capture", (dialog1, which) -> {
            optionvisual = 1;
            dispatchTakePictureIntent();
        });
        dialog.setNegativeButton("Upload", (dialog12, which) -> {
            Intent intent = getFileChooserIntent();
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), Upload_Accident_IMAGE);
        });
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }

    public void successimagesUpload(String message) {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", (dialog1, which) -> dialog1.dismiss());
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }

    public void showaccdocsUpload() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
        dialog.setMessage(getString(R.string.upload_capture_additional_image));
        dialog.setPositiveButton("Capture", (dialog1, which) -> {

            optionvisual = 1;
            dispatchTakePictureIntentNew();
        });
        dialog.setNegativeButton("Upload", (dialog12, which) -> {
            //Intent intent = getFileChooserIntent();
            Intent intent = getFileChooserIntentNew();
            startActivityForResult(Intent.createChooser(intent, "Select Documents"), Upload_Accident_DOCS);
        });
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }

    private Intent getFileChooserIntent() {
        //String[] mimeTypes = {"image/*", "application/pdf"};
        String[] mimeTypes = {"image/*"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";

            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }

            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        return intent;
    }

    private Intent getFileChooserIntentNew() {
        String[] mimeTypes = {"image/*", "application/pdf"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";

            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }

            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        return intent;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.aki.claimreporting.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                if (optionvisual == 1) {
                    startActivityForResult(takePictureIntent, Capture_Accident_IMAGE);
                }

            }
        }else {
            //for pixel mobile phones
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        "com.aki.claimreporting.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, Capture_Accident_IMAGE);

            }
        }
    }

    private void dispatchTakePictureIntentNew() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.aki.claimreporting.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                if (optionvisual == 1) {
                    startActivityForResult(takePictureIntent, Capture_Accident_DOCS);
                }

            }
        }else {
            //for pixel mobile phones
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        "com.aki.claimreporting.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, Capture_Accident_DOCS);

            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    public void claiminvalidaccidenimg() {

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.file_invalid_image))
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })

                .create();
        dialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        if (requestCode == Capture_Accident_IMAGE) {
            insurer = null;
            insurer = getBitmap(currentPhotoPath);
            MainActivity.Global.img = insurer;
            uploadimage = "1";
            //String photopath = currentPhotoPath;
//            String encodedString = MainActivity.bitmapconverstion(insurer,getActivity());
//            String encodedString1 = encodedString;
//            insurer.recycle();
//            mydb = new DatabaseHelper(getActivity());
//            boolean Isinserted = mydb.insertclaimmoreimg(encodedString);
//            if(Isinserted == true)
//            {
//                boolean test = Isinserted;
//                Log.i(null,"Insertion Done");
//            }
//            else
//            {
//                boolean test = Isinserted;
//                Log.i(null,"Not Insertion Done");
//            }

            //insurer.recycle();


        }

        if (requestCode == Upload_Accident_IMAGE) {

            try {
                uploadimage = "1";
                Uri uri = data.getData();
                currentPhotoPath = data.getData().toString();

                /*String filenew = data.getType();
                String filetype;
                if (filenew == null || filenew == "null") {
                    filetype = uri.toString();
                } else {
                    filetype = data.getType();
                }*/

                ContentResolver contentResolver = getActivity().getContentResolver();
                String filetype = contentResolver.getType(uri);
                if (filetype.contains("image")) {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                    arrayaccidentimages = readBytes(inputStream);
//                    uploadaccidentimages  = BitmapFactory.decodeStream(inputStream);
                    String encoded1 = Base64.encodeToString(arrayaccidentimages, Base64.DEFAULT);
                    String encoded2 = encoded1;

                    // insurer  = BitmapFactory.decodeStream(inputStream);
                    insurer = BitmapFactory.decodeByteArray(arrayaccidentimages, 0, arrayaccidentimages.length);
                    MainActivity.Global.img = insurer;

                } else {
                    currentPhotoPath = "";
                    claiminvalidaccidenimg();

                }

            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getClassName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        }

        if (requestCode == Capture_Accident_DOCS) {
            uploadimage = "1";
            insurer = null;
            insurer = getBitmap(currentPhotoPath);
            MainActivity.Global.img = insurer;

        }

        if (requestCode == Upload_Accident_DOCS) {

            try {
                uploadimage = "1";
                Uri uri = data.getData();
                currentPhotoPath = data.getData().toString();
                /*String filenew = data.getType();
                String filetype;
                if (filenew == null || filenew == "null") {
                    filetype = uri.toString();
                } else {
                    filetype = data.getType();
                }*/
                ContentResolver contentResolver = getActivity().getContentResolver();
                String filetype = contentResolver.getType(uri);
                if (filetype.contains("image")) {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                    arrayaccidentimages = readBytes(inputStream);
//                    uploadaccidentimages  = BitmapFactory.decodeStream(inputStream);
                    String encoded1 = Base64.encodeToString(arrayaccidentimages, Base64.DEFAULT);
                    String encoded2 = encoded1;

                    // insurer  = BitmapFactory.decodeStream(inputStream);
                    insurer = BitmapFactory.decodeByteArray(arrayaccidentimages, 0, arrayaccidentimages.length);
                    MainActivity.Global.img = insurer;

                } else if (filetype.contains("pdf")) {
                    uploadimage = "2";
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                    arrayaccidentdocs = readBytes(inputStream);

                } else {
                    currentPhotoPath = "";
                    claiminvalidaccidenimg();
                }

            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getClassName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        }

    }

    public Bitmap getBitmap(String path) {
        Bitmap bitmap = null;
        try {
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            //image.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getClassName() + " - getBitmap", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        return bitmap;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean checkGPSStatus() {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if (locationManager == null) {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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

    public void uploadclaimimages(Activity activity) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            if (isNetworkConnected() == true) {

                if (checkGPSStatus() == true) {

                    mydb = new DatabaseHelper(activity);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }
                    SharedPreferences claimPref = activity.getSharedPreferences("ClaimDetailsView", Context.MODE_PRIVATE);
                    String incidenid = claimPref.getString(CustomClaimHistoryList.ClaimrefID, "");
                    final MediaType mediaType;
                    String filename;
                    if (uploadimage == "1") {
                        mediaType = MediaType.parse("image/jpeg");
                        Bitmap bitmap = MainActivity.Global.img;

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        try {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String outStr = MainActivity.Global.outputStr;
                        Log.i(null, String.valueOf(bitmap.getByteCount()));
                        byte[] byteArray = stream.toByteArray();
                        regByte = byteArray;
                        String imgData = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        String imgDatanew = imgData;
                        filename = "ClaimImg.jpg";
                    } else {

                        mediaType = MediaType.parse("application/pdf");
                        filename = "ClaimImg.pdf";
                        byte[] byteArray = arrayaccidentdocs;
                        regByte = byteArray;
                    }
                    dateTime = null;

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        dateTime = localToGMT();
                    }
                    String uniqueID = UUID.randomUUID().toString();
                    SharedPreferences locationPref = activity.getSharedPreferences("LocationPref", Context.MODE_PRIVATE);
                    String latval = locationPref.getString(MainActivity.Latitude, null);
                    String longval = locationPref.getString(MainActivity.Longitude, null);
                    String cardocuniq_id = uniqueiddoc;
                    //MainActivity.postURL =  activity.getString(R.string.uaturl) + "/app/Claim/UploadClaimFiles";
                    Thread thread = new Thread(new Runnable() {

                        public void run() {
                            MainActivity.postURL = activity.getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(120, TimeUnit.SECONDS)
                                    .writeTimeout(120, TimeUnit.SECONDS)
                                    .readTimeout(120, TimeUnit.SECONDS)
                                    .build();
//                            RequestBody body = new MultipartBody.Builder()
//                                    .setType(MultipartBody.FORM)
//                                    .addFormDataPart(
//                                            "Images","Additional.jpg",
//                                            RequestBody.create(mediaType, byteArray))
//                                    .addFormDataPart("incidentUniqueCode",incidenid)
//                                    .addFormDataPart("documentType",cardocuniq_id)
//                                    .addFormDataPart("description",comments.getText().toString())
//                                    .build();
                            RequestBody body = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("fileName", filename)
                                    .addFormDataPart(
                                            "image", filename,
                                            RequestBody.create(mediaType, regByte))
                                    .addFormDataPart("certificateNo", "")
                                    .addFormDataPart("incidentUniqueCode", incidenid)
                                    .addFormDataPart("geoTagLat", locationPref.getString(MainActivity.Latitude, null))
                                    .addFormDataPart("geoTagLon", locationPref.getString(MainActivity.Longitude, null))
                                    .addFormDataPart("captureAttachmentID", uniqueID)
                                    .addFormDataPart("captureDateTime", dateTime)
                                    .addFormDataPart("description", commentsval)
                                    .addFormDataPart("attachmentTypeID", cardocuniq_id)
                                    .addFormDataPart("isReturnURL", "false")
                                    .build();
                            Request request = new Request.Builder()
                                    .url(MainActivity.postURL)
                                    .method("POST", body)
                                    .addHeader("MobileParameter", MainActivity.InsertMobileParameters())
                                    .addHeader("Authorization", "Bearer " + stokenval)
                                    .build();
                            Response staticResponse = null;
                            try {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog = ProgressDialog.show(getActivity(), "Loading", "Please wait...", true);
                                    }
                                });
                                staticResponse = client.newCall(request).execute();
                                String staticRes = staticResponse.body().string();
                                Log.i(null, staticRes);
                                final JSONObject staticJsonObj = new JSONObject(staticRes);
                                try {
                                    reqidval = staticJsonObj.getString("reqID");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                                if (staticJsonObj.getInt("rcode") == 1) {
                                    getActivity().runOnUiThread(() -> {
                                        try {
                                            progressdialog.dismiss();
                                            commentsval = "";
                                            addspotcomments.setText("");
                                            accspotcomments.setText("");
                                            documenttype.setSelection(0);
                                            currentPhotoPath = "";
                                            successimagesUpload("Successfully uploaded document. Your Reference No. " + staticJsonObj.getJSONObject("rObj").getString("AttachmentID"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                    });
                                } else {
                                    try {
                                        JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                        JSONObject index = rmsg.getJSONObject(0);
                                        getActivity().runOnUiThread(() -> {
                                            String errorText;
                                            try {
                                                errorText = index.getString("errorText");
                                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                                alert.setCancelable(false);
                                                alert.setMessage(errorText);
                                                alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                                                alert.show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                    getActivity().runOnUiThread(() -> {
                                        if (progressdialog.isShowing()) {
                                            progressdialog.dismiss();
                                        }
                                    });
                                }
                            } catch (final IOException | JSONException e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                getActivity().runOnUiThread(() -> {
                                    if (progressdialog.isShowing()) {
                                        progressdialog.dismiss();
                                    }
                                });
                            }
                        }
                    });
                    thread.start();

                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setMessage(getString(R.string.gps_not_enabled));
                    dialog.setPositiveButton("Ok", (dialog1, which) -> {
                        //this will navigate user to the device location settings screen
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    });
                    AlertDialog alert = dialog.create();
                    alert.show();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            requireActivity().runOnUiThread(() -> {
                if (progressdialog.isShowing()) {
                    progressdialog.dismiss();
                }
            });
        }

    }

    public void getimageslist() {
        try {
            // comments.setText("");
            attachimgist.clear();
            mydb = new DatabaseHelper(getActivity());
            Cursor curseattach = mydb.getClaimImgmore();
            int counttest1 = curseattach.getCount();
            int counttest2 = counttest1;
            if (counttest1 >= 1) {
                while (curseattach.moveToNext()) {
                    insurer = null;
                    insurer = getBitmap(curseattach.getString(0));
                    String encodedString = MainActivity.convertBitmap(insurer, getActivity());
                    String encodedString1 = encodedString;
                    insurer.recycle();
                    AdditionalAttachclaimlist element = new AdditionalAttachclaimlist(
                            encodedString, curseattach.getString(1)

                    );
                    attachimgist.add(element);
                    String test = "test";
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //ArrayList<AllVehicleResponse> arrayOfUsers = allvehclist;
                        AdditionalAttachtClaimImg attachListView = new AdditionalAttachtClaimImg(getActivity(), attachimgist);
                        list = (ListView) rootview.findViewById(R.id.listmyclaimmoreimages);
                        list.setAdapter(attachListView);
                        currentPhotoPath = "";
                    }
                });
            } else {

            }
        } catch (Exception ex) {
            ex.getStackTrace();
            MainActivity.MobileErrorLog(reqidval, "ClaimImage-getclaimnfo", ex.toString());
            mCrashlytics.recordException(ex);

        }

    }

    public void getimageslistinitiate() {
        try {
            //  comments.setText("");
            attachimgist.clear();
            mydb = new DatabaseHelper(getActivity());
            Cursor curseattach = mydb.getClaimImgmore();
            int counttest1 = curseattach.getCount();
            int counttest2 = counttest1;
            if (counttest1 >= 1) {
                while (curseattach.moveToNext()) {
                    insurer = null;
                    insurer = getBitmap(curseattach.getString(0));
                    String encodedString = MainActivity.convertBitmap(insurer, getActivity());
                    String encodedString1 = encodedString;
                    insurer.recycle();
                    AdditionalAttachclaimlist element = new AdditionalAttachclaimlist(
                            encodedString, curseattach.getString(1)

                    );
                    attachimgist.add(element);
                    String test = "test";
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //ArrayList<AllVehicleResponse> arrayOfUsers = allvehclist;
                        AdditionalAttachtClaimImg attachListView = new AdditionalAttachtClaimImg(getActivity(), attachimgist);
                        list = (ListView) rootview.findViewById(R.id.listmyclaimmoreimages);
                        list.setAdapter(attachListView);
                    }
                });
            } else {

            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            MainActivity.MobileErrorLog(reqidval, "UserRegistration-getimageslistinitiate", ex.toString());

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (adapterView.getId() == R.id.spinnerdocsupload) {
            int spinneridval = position;

            if (spinneridval == 0) {
                viewadddoclin.setVisibility(View.GONE);
            }
            if (spinneridval == 1) {
                uniqueiddoc = "708b162e-0700-4470-a914-95b2a64e422d";
                viewadddoclin.setVisibility(View.VISIBLE);

            }
            if (spinneridval == 2) {
                uniqueiddoc = "dd8d24d5-123e-4499-8429-22f185368bf5";
                viewadddoclin.setVisibility(View.VISIBLE);
            }
            if (spinneridval == 3) {
                uniqueiddoc = "94a69c6e-5574-4e74-996c-5c61752e7ba4";
                viewadddoclin.setVisibility(View.VISIBLE);
            }
            if (spinneridval == 4) {
                uniqueiddoc = "1c7db73a-028a-4831-a848-cd1875ab195c";
                viewadddoclin.setVisibility(View.VISIBLE);
            }
            if (spinneridval == 5) {
                uniqueiddoc = "f17fd7f2-1789-47b2-824c-e28ee032db2a";
                viewadddoclin.setVisibility(View.VISIBLE);
            }
            if (spinneridval == 6) {
                uniqueiddoc = "e22bdbd4-be4a-461f-93aa-49b506e74bd6";
                viewadddoclin.setVisibility(View.VISIBLE);
            }
            if (spinneridval == 7) {
                uniqueiddoc = "60d90614-9b38-44e1-bd19-b65264fdc362";
                viewadddoclin.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}