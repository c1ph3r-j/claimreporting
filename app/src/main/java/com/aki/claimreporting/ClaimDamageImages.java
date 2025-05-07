package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class ClaimDamageImages extends ArrayAdapter<Bitmap> {

    Context context;
    ArrayList<Bitmap> arrayofimageattachments = new ArrayList<>();

    public ClaimDamageImages(Context context, ArrayList<Bitmap> arrayofimageattach) {
        super(context, R.layout.claimimagelist, arrayofimageattach);
        this.context = context;
        arrayofimageattachments = arrayofimageattach;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Bitmap responsenew = arrayofimageattachments.get(position);
        ViewHolder viewHolder;// view lookup cache stored in tag
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.claimimagelist, parent, false);
            viewHolder.tvattachbyte = (ImageView) convertView.findViewById(R.id.imgclaimdamage);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        try {
//            URL url = null;
//            try {
//                url = new URL(responsenew.getImageURL());
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                mCrashlytics.recordException(e);
//            }
//            ByteArrayOutputStream output = new ByteArrayOutputStream();
//
//            try {
//                try (InputStream inputStream = url.openStream()) {
//                    int n = 0;
//                    byte[] buffer = new byte[1024];
//                    while (-1 != (n = inputStream.read(buffer))) {
//                        output.write(buffer, 0, n);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                mCrashlytics.recordException(e);
//            }
//
//            byte[] imgdmg = output.toByteArray();
//            byte[] imgdmg1 = imgdmg;
//            Bitmap bmp = BitmapFactory.decodeByteArray(imgdmg, 0, imgdmg.length);
//            System.out.println(bmp);

            viewHolder.tvattachbyte.setImageBitmap(Bitmap.createScaledBitmap(responsenew, responsenew.getWidth(), responsenew.getHeight(), false));
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        // Return the completed view to render on screen
        return convertView;
    }

    private static class ViewHolder {
        ImageView tvattachbyte;
    }
}