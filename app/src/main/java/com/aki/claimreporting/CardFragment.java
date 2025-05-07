package com.aki.claimreporting;

import static com.aki.claimreporting.ClaimVehicleSelection.listOfVehiclesDetail;
import static com.aki.claimreporting.ClaimVehicleSelection.pagerAdapter;
import static com.aki.claimreporting.ClaimVehicleSelection.selectedVehicleLayout;
import static com.aki.claimreporting.ClaimVehicleSelection.selectedVehicleModel;
import static com.aki.claimreporting.ClaimVehicleSelection.setNewVehicleSelected;
import static com.aki.claimreporting.ClaimVehicleSelection.valuelistadpt;
import static com.aki.claimreporting.MainActivity.mCrashlytics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class CardFragment extends Fragment {

    Button addVehicleBtn;
    ClaimVehicleSelection claimVehicleSelection;
    private CardView cardView;
    private LinearLayout vehicleDetailsLayout, addVehiclesLayout;

    public static Fragment getInstance(int position) {
        CardFragment f = new CardFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        f.setArguments(args);

        return f;
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_viewpager, container, false);

        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        // int position = getArguments().getInt("position");
        vehicleDetailsLayout = view.findViewById(R.id.VehicleDetailsLayout);
        cardView = view.findViewById(R.id.cardView);
        claimVehicleSelection = pagerAdapter.getClaimVehicleSelection();
        addVehiclesLayout = view.findViewById(R.id.addVehiclesLayout);
        LinearLayout addVehiclesToTheList = view.findViewById(R.id.addVehiclesToTheListView);
        LinearLayout addVehiclesView = view.findViewById(R.id.alterUserToAddVehicleView);
        addVehiclesToTheList.setVisibility(View.GONE);
        addVehiclesView.setVisibility(View.VISIBLE);
        //  cardView.setMaxCardElevation(cardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);
        //  SharedPreferences certpref = requireContext().getSharedPreferences("ClaimInsert", Context.MODE_PRIVATE);
        //  SharedPreferences.Editor certeditor = certpref.edit();


        if (valuelistadpt != 1) {
            try {
                assert getArguments() != null;
                int position = getArguments().getInt("position");
                if (position == 0) {
                    initAddVehiclesView(view, addVehiclesToTheList, addVehiclesView);
                } else {
                    position--;
                    addVehiclesLayout.setVisibility(View.GONE);
                    vehicleDetailsLayout.setVisibility(View.VISIBLE);
                    assert getArguments() != null;
                    cardView.setMaxCardElevation(cardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);
                    SharedPreferences certpref = requireContext().getSharedPreferences("ClaimInsert", Context.MODE_PRIVATE);
                    SharedPreferences.Editor certeditor = certpref.edit();


                    TextView title = view.findViewById(R.id.title);
                    TextView makeval = view.findViewById(R.id.makeval);

                    // Button button = (Button)view.findViewById(R.id.button);

//          title.setText(String.format("Card %d", getArguments().getInt("position")));

                    title.setText(listOfVehiclesDetail.get(position).getRegistrationNo());
                    String vehicleName = listOfVehiclesDetail.get(position).getVehicleMake() + "-" + listOfVehiclesDetail.get(position).getVehicleModel();
                    makeval.setText(vehicleName);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Button in Card " + getArguments().getInt("position")
//                        + "Clicked!", Toast.LENGTH_SHORT).show();
//            }
//        });


                    try {
                        int finalPosition = position;
                        cardView.setOnClickListener(onClickCardView -> {
                            vehicleDetailsLayout.setVisibility(View.VISIBLE);
                            addVehiclesLayout.setVisibility(View.GONE);
                            if (selectedVehicleLayout != vehicleDetailsLayout) {
                                if (selectedVehicleLayout != null) {
                                    selectedVehicleLayout.setBackgroundColor(requireContext().getColor(R.color.white));
                                    selectedVehicleLayout = vehicleDetailsLayout;
                                    selectedVehicleModel.setTextColor(requireContext().getColor(R.color.black));
                                }
                                vehicleDetailsLayout.setBackgroundColor(requireContext().getColor(R.color.purple_500));
                                makeval.setTextColor(requireContext().getColor(R.color.white));
                                selectedVehicleLayout = vehicleDetailsLayout;
                                selectedVehicleModel = makeval;
                                ClaimVehicleSelection.insuranceid = listOfVehiclesDetail.get(finalPosition).getInsurerID();
                                ClaimVehicleSelection.insurancename = listOfVehiclesDetail.get(finalPosition).getInsuranceCompanyName();
                                certeditor.putString("CertificateID", listOfVehiclesDetail.get(finalPosition).getCertificateNo());
                                certeditor.putString("Vechilerefid", listOfVehiclesDetail.get(finalPosition).getVehicleRefID());
                                certeditor.apply();
                            } else {
                                selectedVehicleLayout.setBackgroundColor(requireContext().getColor(R.color.white));
                                selectedVehicleModel.setTextColor(requireContext().getColor(R.color.black));
                                selectedVehicleLayout = null;
                                certeditor.putString("CertificateID", "null");
                                certeditor.putString("Vechilerefid", "null");
                                certeditor.apply();
                            }
                        });
                        if (setNewVehicleSelected && position == 0) {
                            selectedVehicleLayout = null;
                            cardView.performClick();
                        }

                        if (setNewVehicleSelected) {
                            selectedVehicleLayout = vehicleDetailsLayout;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                }
//                if (position < valuelistadpt - 1) {
//
//                } else {
//                    initAddVehiclesView(view, addVehiclesToTheList, addVehiclesView);
//                }
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        } else {
            try {
                initAddVehiclesView(view, addVehiclesToTheList, addVehiclesView);
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        }

        return view;
    }

    void initAddVehiclesView(View view, LinearLayout addVehiclesToTheList, LinearLayout addVehiclesView) {
        try {
            ImageView closeView = view.findViewById(R.id.closeAddVehiclesToTheListView);
            Button addVehiclesToTheListBtn = view.findViewById(R.id.addVehicleToTheListBtn);
            EditText regNoField = view.findViewById(R.id.regNoField);
            vehicleDetailsLayout.setVisibility(View.GONE);
            addVehiclesLayout.setVisibility(View.VISIBLE);
            addVehicleBtn = view.findViewById(R.id.addVehicleBtn);
            addVehicleBtn.setOnClickListener(onClickAddVehicleBtn -> {
                if(selectedVehicleLayout != null) {
                    selectedVehicleLayout.setBackgroundColor(requireContext().getColor(R.color.white));
                    selectedVehicleLayout = vehicleDetailsLayout;
                    selectedVehicleModel.setTextColor(requireContext().getColor(R.color.black));
                    selectedVehicleLayout = null;
                }
                addVehiclesToTheList.setVisibility(View.VISIBLE);
                addVehiclesView.setVisibility(View.GONE);
            });
            closeView.setOnClickListener(onClickClose -> {
                addVehiclesToTheList.setVisibility(View.GONE);
                addVehiclesView.setVisibility(View.VISIBLE);
            });
            addVehiclesToTheListBtn.setOnClickListener(onClickAddVehicle -> {
                //TODO To Add Vehicle Flow.
                String regNo = regNoField.getText().toString().trim();
                regNo = regNo.replace(" ", "").trim();
                if (regNo.isEmpty()) {
                    Toast.makeText(claimVehicleSelection, "Please enter the Register Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                claimVehicleSelection.getInsuranceCompanyApi(regNo);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CardView getCardView() {
        return cardView;
    }
}