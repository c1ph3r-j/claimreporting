package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class QRCodeImage extends AppCompatActivity {

    private static final int IMAGE_DrivingLIC_CODE = 1234;
    public static Bitmap qcodeimg, qcodeimgnew;
    public static ImageView imgcapt;
    static FirebaseCrashlytics mCrashlytics;
    String currentPhotoPath;
    Button camcapt;
    Context mycontext;

    public static String decodeQRImage(String path) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        Bitmap bMap = BitmapFactory.decodeFile(path);
        String decoded = null;
        Bitmap generatedQRCode = null;
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                generatedQRCode = rotateImage(bMap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                generatedQRCode = rotateImage(bMap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                generatedQRCode = rotateImage(bMap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                generatedQRCode = qcodeimg;
        }

        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),
                bMap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),
                bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new QRCodeReader();
        try {
            Result result = reader.decode(bitmap);
            decoded = result.getText();
        } catch (NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        return decoded;
    }

//    public static String decodeQRImage(String path) {
//      //  Bitmap bMap = BitmapFactory.decodeFile(path);
//        Bitmap bMap = null;
//
//        qcodeimg  = getBitmap(path);
//        ExifInterface ei = null;
//        try {
//            ei = new ExifInterface(path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                ExifInterface.ORIENTATION_UNDEFINED);
//        switch(orientation) {
//
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                bMap = rotateImage(qcodeimg, 90);
//                break;
//
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                bMap = rotateImage(qcodeimg, 180);
//                break;
//
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                bMap = rotateImage(qcodeimg, 270);
//                break;
//
//            case ExifInterface.ORIENTATION_NORMAL:
//            default:
//                bMap = qcodeimg;
//        }
//
//        imgcapt.setImageBitmap(bMap);
//        String decoded = null;
//
//        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
//        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),
//                bMap.getHeight());
//        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),
//                bMap.getHeight(), intArray);
//        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//
//        Reader reader = new QRCodeReader();
//        try {
//            Result result = reader.decode(bitmap);
//            decoded = result.getText();
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//        } catch (ChecksumException e) {
//            e.printStackTrace();
//        } catch (FormatException e) {
//            e.printStackTrace();
//        }
//        return decoded;
//    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

//    public static String readQRCode(String filePath, String charset, Map hintMap)
//            throws FileNotFoundException, IOException, NotFoundException {
//
//
//        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
//                new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(filePath)))));
//        Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, hintMap);
//        return qrCodeResult.getText();
//    }

//    public String decodeQRImage(String path) {
//
//        String contents = null;
//
//        Bitmap generatedQRCode = null;
//        qcodeimg  = getBitmap(path);
//        ExifInterface ei = null;
//        try {
//            ei = new ExifInterface(path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                ExifInterface.ORIENTATION_UNDEFINED);
//        switch(orientation) {
//
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                generatedQRCode = rotateImage(qcodeimg, 90);
//                break;
//
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                generatedQRCode = rotateImage(qcodeimg, 180);
//                break;
//
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                generatedQRCode = rotateImage(qcodeimg, 270);
//                break;
//
//            case ExifInterface.ORIENTATION_NORMAL:
//            default:
//                generatedQRCode = qcodeimg;
//        }
//
////        BarcodeDetector detector =
////                new BarcodeDetector.Builder(mycontext)
////                        .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
////                        .build();
////        if(!detector.isOperational()){
////            Log.d("QR_READ","Could not set up the detector!");
////        }
////        Frame frame = new Frame.Builder().setBitmap(qcodeimg).build();
////        SparseArray<Barcode> barcodes = detector.detect(frame);
////        Log.d("QR_READ","-barcodeLength-"+barcodes.size());
////        Barcode thisCode=null;
////        if(barcodes.size()==0){
////            Log.d("QR_VALUE","--NODATA");
////        }
////        else if(barcodes.size()==1){
////            thisCode = barcodes.valueAt(0);
////            Log.d("QR_VALUE","--"+thisCode.rawValue);
////        }
////        else{
////            for(int iter=0;iter<barcodes.size();iter++) {
////                thisCode = barcodes.valueAt(iter);
////                Log.d("QR_VALUE","--"+thisCode.rawValue);
////            }
////        }
//
//        imgcapt.setImageBitmap(generatedQRCode);
//        int width = generatedQRCode.getWidth();
//        int height = generatedQRCode.getHeight();
//        int[] pixels = new int[width * height];
//       // MultiFormatReader reader = new MultiFormatReader();// use this otherwise
//
//       // Result result = reader.decodeWithState(generatedQRCode);
//        LuminanceSource source = new RGBLuminanceSource(width,height,pixels);
//        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//        MultiFormatReader reader = new MultiFormatReader();
//        try {
//            Result result = reader.decode(bitmap);
//            contents = result.getText();
//            byte[] rawBytes = result.getRawBytes();
//            BarcodeFormat format = result.getBarcodeFormat();
//            ResultPoint[] points = result.getResultPoints();
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//        }
//        return contents;
//    }


//    public static String decodeQRImage(String path) {
//        //  Bitmap bMap = BitmapFactory.decodeFile(path);
//
//        Bitmap generatedQRCode;
//        qcodeimg  = getBitmap(path);
//        ExifInterface ei = null;
//        try {
//            ei = new ExifInterface(path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                ExifInterface.ORIENTATION_UNDEFINED);
//        switch(orientation) {
//
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                generatedQRCode = rotateImage(qcodeimg, 90);
//                break;
//
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                generatedQRCode = rotateImage(qcodeimg, 180);
//                break;
//
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                generatedQRCode = rotateImage(qcodeimg, 270);
//                break;
//
//            case ExifInterface.ORIENTATION_NORMAL:
//            default:
//                generatedQRCode = qcodeimg;
//        }
//
//        imgcapt.setImageBitmap(generatedQRCode);
//        int width = generatedQRCode.getWidth();
//        int height = generatedQRCode.getHeight();
//        int[] pixels = new int[width * height];
//        generatedQRCode.getPixels(pixels, 0, width, 0, 0, width, height);
//
//        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
//
//        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
//
//        Reader reader = new MultiFormatReader();
//        Result result = null;
//        try {
//            result = reader.decode(binaryBitmap);
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//        } catch (ChecksumException e) {
//            e.printStackTrace();
//        } catch (FormatException e) {
//            e.printStackTrace();
//        }
//        String decoded = result.getText();
//        String decoded1 = decoded;
//
////
////
////        String decoded = null;
////
////        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
////        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),
////                bMap.getHeight());
////        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),
////                bMap.getHeight(), intArray);
////        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
////
////        Reader reader = new QRCodeReader();
////        try {
////            Result result = reader.decode(bitmap);
////            decoded = result.getText();
////        } catch (NotFoundException e) {
////            e.printStackTrace();
////        } catch (ChecksumException e) {
////            e.printStackTrace();
////        } catch (FormatException e) {
////            e.printStackTrace();
////        }
//        return decoded;
//    }

    public static Bitmap getBitmap(String path) {

        Bitmap bitmap = null;
        try {
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            //image.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Adhar Info");

        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        setContentView(R.layout.activity_qr_code_image);
        preventSizeChange(this, getSupportActionBar());
        try {
            camcapt = (Button) findViewById(R.id.camcapt);
            mycontext = this;
            imgcapt = (ImageView) findViewById(R.id.imgcapt);
            try {
                camcapt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent i = new Intent(QRCodeImage.this, ScanCertificate.class);
                        startActivity(i);

                        //  dispatchTakePictureIntent();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        //   decodeQRImage("/storage/emulated/0/Android/data/com.aki.claimreporting/files/Pictures/JPEG_20221205_173552_1171820945913372400.jpg");
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.aki.claimreporting.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, IMAGE_DrivingLIC_CODE);


            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_DrivingLIC_CODE) {
            //insurer = null;
            try {
                if (getValidBitmap(currentPhotoPath) != null) {


                    decodeQRImage(currentPhotoPath);
                    //String test = "test1";
                    // finish();
                }

            } catch (Exception ex) {
                ex.getStackTrace();

            }
            //  insurer.recycle();
        }
    }

    public Bitmap getValidBitmap(String path) {
        Bitmap bitmap = null;
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);

            //image.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        return bitmap;
    }
}