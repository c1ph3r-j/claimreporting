package com.aki.claimreporting;


import static com.aki.claimreporting.MainActivity.mCrashlytics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class AdditionalAttachtClaimImg extends ArrayAdapter<AdditionalAttachclaimlist> {

    ArrayList<AdditionalAttachclaimlist> arrayofimageattachments;

    public AdditionalAttachtClaimImg(Context context, ArrayList<AdditionalAttachclaimlist> arrayofimageattach) {
        super(context, R.layout.additionalattachclaimimg, arrayofimageattach);
        arrayofimageattachments = arrayofimageattach;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AdditionalAttachclaimlist responsenew = arrayofimageattachments.get(position);
        ViewHolder viewHolder; // view lookup cache stored in tag
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.additionalattachclaimimg, parent, false);
                viewHolder.tvattachbyte = (ImageView) convertView.findViewById(R.id.attachementallImg);
                viewHolder.comments = (TextView) convertView.findViewById(R.id.commentstxt);

                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data from the data object via the viewHolder object
            // into the template view.
            viewHolder.comments.setText("Comments : " + responsenew.getComments());
            try {
                String imgtest = responsenew.getAttachmentbyte();
                byte[] decodedString = Base64.decode(imgtest, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                //Bitmap bitmap = ObjectDetectorActivity.Global.img;
                viewHolder.tvattachbyte.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            // Return the completed view to render on screen
            return convertView;
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            return null;
        }
    }

    private static class ViewHolder {
        ImageView tvattachbyte;
        TextView comments;
    }
}