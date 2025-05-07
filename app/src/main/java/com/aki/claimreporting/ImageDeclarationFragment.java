package com.aki.claimreporting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;

public class ImageDeclarationFragment extends Fragment {

    AttachmentimageList responsenew;
    FirebaseCrashlytics mCrashlytics;

    public ImageDeclarationFragment() {
        // Required empty public constructor
    }

    public ImageDeclarationFragment(AttachmentimageList responsenew) {
        this.responsenew = responsenew;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_declaration, container, false);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        ImageView imageView = view.findViewById(R.id.imageDeclarationImageView);

        try {

            if (responsenew.getAttachmentbyte().contains("storage")) {
                Bitmap bitmap = null;
                try {
                    File f = new File(responsenew.getAttachmentbyte());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
                    //image.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }

                Bitmap bitmapnew = bitmap;
                //Bitmap bitmap = ObjectDetectorActivity.Global.img;
                imageView.setImageBitmap(bitmapnew);
            } else {
                String imgtest = responsenew.getAttachmentbyte();
                byte[] decodedString = Base64.decode(imgtest, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                //Bitmap bitmap = ObjectDetectorActivity.Global.img;
                imageView.setImageBitmap(bitmap);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }

        return view;
    }
}