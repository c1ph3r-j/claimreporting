package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class ClaimStolenImageAdapter extends RecyclerView.Adapter<ClaimStolenImageAdapter.MyViewHolder> {
    public DatabaseHelper mynewdb;
    public Context newcontext;
    ArrayList<AdditionalAttachclaimlist> arrayofvehiclenew = new ArrayList<AdditionalAttachclaimlist>();
    ArrayList<AdditionalAttachclaimlist> arrayList;

    public ClaimStolenImageAdapter(Context context, ArrayList<AdditionalAttachclaimlist> arrayofvehicle) {
        this.newcontext = context;
        this.arrayofvehiclenew = arrayofvehicle;
        this.arrayList = new ArrayList<AdditionalAttachclaimlist>();
        this.arrayList.addAll(arrayofvehiclenew);

    }

    @NonNull
    @Override
    public ClaimStolenImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.liststolenimage, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClaimStolenImageAdapter.MyViewHolder holder, int position) {
        AdditionalAttachclaimlist responsenew = arrayofvehiclenew.get(position);
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            String imgtest = responsenew.getAttachmentbyte();
            byte[] decodedString = Base64.decode(imgtest, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            //Bitmap bitmap = ObjectDetectorActivity.Global.img;
            holder.imgstolen.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    private void removeItem(int position) {

    }

    @Override
    public int getItemCount() {
        return arrayofvehiclenew.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgstolen;

        MyViewHolder(View view) {
            super(view);
            imgstolen = (ImageView) view.findViewById(R.id.attachementstolenImg);


        }

    }
}