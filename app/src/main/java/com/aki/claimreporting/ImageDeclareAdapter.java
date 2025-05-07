package com.aki.claimreporting;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class ImageDeclareAdapter extends ArrayAdapter<AttachmentimageList> {

    public Activity mactivity;
    ArrayList<AttachmentimageList> arrayofimageattachments = new ArrayList<AttachmentimageList>();

    public ImageDeclareAdapter(Context context, ArrayList<AttachmentimageList> arrayofimageattach, Activity activity) {
        super(context, R.layout.imgdeclarelist, arrayofimageattach);
        mactivity = activity;
        arrayofimageattachments = arrayofimageattach;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AttachmentimageList responsenew = arrayofimageattachments.get(position);
        ImageDeclareAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ImageDeclareAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.imgdeclarelist, parent, false);
            viewHolder.tvattachbyte = (ImageView) convertView.findViewById(R.id.attachementallImg);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ImageDeclareAdapter.ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
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
                }

                Bitmap bitmapnew = bitmap;
                //Bitmap bitmap = ObjectDetectorActivity.Global.img;
                viewHolder.tvattachbyte.setImageBitmap(bitmapnew);
            } else {
                String imgtest = responsenew.getAttachmentbyte();
                byte[] decodedString = Base64.decode(imgtest, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                //Bitmap bitmap = ObjectDetectorActivity.Global.img;
                viewHolder.tvattachbyte.setImageBitmap(bitmap);
            }

        } catch (Exception ex) {
            ex.getStackTrace();
        }

        if (position == arrayofimageattachments.size() - 1) // last element is a Button
        {

            ImageDeclaration.enablelayout(mactivity);

        } else {
            ImageDeclaration.disablelayout(mactivity);
        }


        // Return the completed view to render on screen
        return convertView;
    }

    public static class ViewHolder {
        ImageView tvattachbyte;
    }
}