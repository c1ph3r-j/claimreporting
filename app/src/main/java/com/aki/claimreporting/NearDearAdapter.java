package com.aki.claimreporting;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NearDearAdapter extends RecyclerView.Adapter<NearDearAdapter.MyViewHolder> {
    public List<NearDearInfo> arraynearList;

    public NearDearAdapter(List<NearDearInfo> arraynearList) {
        this.arraynearList = arraynearList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listdear, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NearDearInfo imgnear = arraynearList.get(position);
        holder.dearName.setText(imgnear.getNearname());
        holder.dearphno.setText("+254 " + imgnear.getNearphnum());
        holder.dearemail.setText(imgnear.getNearemailid());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newPosition = holder.getAdapterPosition();
                arraynearList.remove(newPosition);
                notifyItemRemoved(newPosition);
                notifyItemRangeChanged(newPosition, arraynearList.size());
                if (arraynearList.size() == 0) {
                    //Login.hiderecycler();
                }

            }
        });
        //   image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(), image.getHeight(), false));

    }

    private void removeItem(int position) {

    }

    @Override
    public int getItemCount() {
        return arraynearList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dearName, dearphno, dearemail;
        ImageView delete;

        MyViewHolder(View view) {
            super(view);
            dearName = (TextView) view.findViewById(R.id.txtnamenear);
            dearphno = (TextView) view.findViewById(R.id.txtnearval);
            dearemail = (TextView) view.findViewById(R.id.txtnearemailval);
            delete = (ImageView) view.findViewById(R.id.deletenearbuttonid);
        }
    }
}