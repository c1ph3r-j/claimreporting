package com.aki.claimreporting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Locale;

public class NotificationAdapter extends ArrayAdapter<NotificationDataModel> {

    Context mcontext;
    FragmentManager fragmentManager;
    ArrayList<NotificationDataModel> arrayofnotificationnew = new ArrayList<NotificationDataModel>();
    ArrayList<NotificationDataModel> arrayList;
    private String ViewNotification, notifictionid;

    public NotificationAdapter(Context context, ArrayList<NotificationDataModel> arrayofnotification) {
        super(context, R.layout.listnotification, arrayofnotification);

        this.arrayofnotificationnew = arrayofnotification;
        this.arrayList = new ArrayList<NotificationDataModel>();
        this.arrayList.addAll(arrayofnotificationnew);
        mcontext = context;
        this.fragmentManager = fragmentManager;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        NotificationDataModel responsenew = arrayofnotificationnew.get(position);
        NotificationAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new NotificationAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listnotification, parent, false);

            viewHolder.notificationtext = (TextView) convertView.findViewById(R.id.txtnotification);
            viewHolder.notificationdesc = (TextView) convertView.findViewById(R.id.txtdescription);
            viewHolder.notificationcolor = (View) convertView.findViewById(R.id.colornotification);
            viewHolder.viewnotification = (ImageView) convertView.findViewById(R.id.imgview);
            viewHolder.deletenotification = (ImageView) convertView.findViewById(R.id.deletenotification);

            // viewHolder.regimage = (ImageView) convertView.findViewById(R.id.viewbuttonid);
            // viewHolder.certimage = (ImageView) convertView.findViewById(R.id.deletebuttonid);
            //  viewHolder.makeimag = (ImageView) convertView.findViewById(R.id.deletebuttonid);
            //  viewHolder.cmpnyimg = (ImageView) convertView.findViewById(R.id.deletebuttonid);
            // viewHolder.radiosuggestion = (RadioButton) convertView.findViewById(R.id.Radiovehicle);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (NotificationAdapter.ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.

        notifictionid = responsenew.getNotificationID();
        viewHolder.notificationtext.setText(responsenew.getTitle());
        viewHolder.notificationdesc.setText(responsenew.getDescription());
//        if(responsenew.getNotificationisViewed() == false)
//        {
//            ColorDrawable colorDrawable
//                    = new ColorDrawable(Color.parseColor("#f8c471"));
//            viewHolder.notificationcolor.setBackground(colorDrawable);
//
//        }
//        else
//        {
//            ColorDrawable colorDrawable
//                    = new ColorDrawable(Color.parseColor("#29B653"));
//            viewHolder.notificationcolor.setBackground(colorDrawable);
//        }


        final View finalConvertView = convertView;
        viewHolder.viewnotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NotificationDataModel response = getItem(position);
//                String snotifyid = response.getNotificationid();
//                String snotifytitle = response.getNotificationtitle();
//                String snotifydescription = response.getNotificationdescription();
//                String snotifycretaedon     = response.getNotificationcreatedOn();

                SharedPreferences viewnotificationpreferences = mcontext.getSharedPreferences("ViewNotification", Context.MODE_PRIVATE);
                SharedPreferences.Editor viewnotifyeditor = viewnotificationpreferences.edit();
                viewnotifyeditor.putString(Notification.notificationidmap, response.getNotificationID());
                viewnotifyeditor.putString(Notification.notificationtitle, response.getTitle());
                viewnotifyeditor.putString(Notification.notificationdescription, response.getDescription());
                viewnotifyeditor.putString(Notification.notificationcreatedOn, response.getDescription());
                viewnotifyeditor.apply();
                Notification.Viewnotification(mcontext);

                Intent login = new Intent(getContext(), MyClaims.class);
                finalConvertView.getContext().startActivity(login);
            }
        });
        viewHolder.deletenotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*NotificationDataModel response = getItem(position);
                String notificationid = response.getNotificationid();*/


                SharedPreferences notificationsharedpreferences = mcontext.getSharedPreferences("Notification", Context.MODE_PRIVATE);
                SharedPreferences.Editor notificationeeditor = notificationsharedpreferences.edit();
                notificationeeditor.putString(Notification.notificationidmap, responsenew.getNotificationID());
                notificationeeditor.apply();

                Notification.Deletenotification(mcontext);
                arrayofnotificationnew.remove(position);
                notifyDataSetChanged();

                Notification.checkVisibility();

             /*  Notification.Getdeletenotification();
                int newPosition = viewHolder.getAdapterPosition();
                arrayofnotificationnew.remove(newPosition);
                notifyItemRemoved(newPosition);
                notifyItemRangeChanged(newPosition, arraydriverList.size());
                if(arraydriverList.size() == 0)
                {
                    RegistrationStep3.hiderecycler();
                }*/
            }
        });

        return convertView;

    }

    //filter
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayofnotificationnew.clear();
        if (charText.length() == 0) {
            arrayofnotificationnew.addAll(arrayList);
        } else {
            for (NotificationDataModel model : arrayList) {
                if (model.getTitle().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    arrayofnotificationnew.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView notificationtext;
        TextView notificationdesc;
        View notificationcolor;
        ImageView viewnotification;
        ImageView deletenotification;
    }
}