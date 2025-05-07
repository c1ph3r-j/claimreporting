package com.aki.claimreporting;

import static com.aki.claimreporting.ClaimLocation.incidentselecteddate;
import static com.aki.claimreporting.ClaimLocation.locationupdate;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MapsView extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final LatLng FALLBACK = new LatLng(12.7198163, 77.8202776);

    private android.widget.TextView dateTime;
    private ImageView back;
    private android.widget.TextView cityName;
    private android.widget.TextView StreetName;
    private android.view.View resetBanner;
    private android.view.View confirmLocationButton;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) checkGpsAndInitMap();
                else handlePermanentDenial();
            });

    private void init() {
        dateTime = findViewById(R.id.dateTime);
        back = findViewById(R.id.back);
        cityName = findViewById(R.id.cityName);
        StreetName = findViewById(R.id.StreetName);
        resetBanner = findViewById(R.id.resetBanner);
        confirmLocationButton = findViewById(R.id.confirmLocationButton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_view);
        init();

        back.setOnClickListener(v -> finish());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initActionBar();
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

        // Set up date time picker on EditText with maximum date and AM/PM format
        dateTime.setFocusable(false);
        dateTime.setClickable(true);
        dateTime.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(MapsView.this, (view, selectedYear, selectedMonth, selectedDay) -> {
                calendar.set(Calendar.YEAR, selectedYear);
                calendar.set(Calendar.MONTH, selectedMonth);
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                // Open TimePicker in 12-hour format (is24HourView = false)
                TimePickerDialog timePickerDialog = new TimePickerDialog(MapsView.this, (view1, selectedHour, selectedMinute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    calendar.set(Calendar.MINUTE, selectedMinute);
                    // Validate that the selected date/time is not in the future
                    if (calendar.getTime().after(Calendar.getInstance().getTime())) {
                        Toast.makeText(MapsView.this, "Future date/time not allowed", Toast.LENGTH_SHORT).show();
                        dateTime.setText("");
                    } else {
                        // Format the date/time with AM/PM at the end
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
                        String formattedDate = sdf.format(calendar.getTime());
                        dateTime.setText(formattedDate);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }, year, month, day);
            // Set the maximum date to today to prevent selecting a future date
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
        // Initialize with current date/time
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdfDefault = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        dateTime.setText(sdfDefault.format(now.getTime()));
    }

    private void checkGpsAndInitMap() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gpsOn = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsOn) {
            new AlertDialog.Builder(this)
                    .setTitle("Enable Location")
                    .setMessage("GPS is off. Please enable it to continue.")
                    .setPositiveButton("Open Settings", (d, w) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("Cancel", (d, w) -> finish())
                    .show();
        } else initMap();
    }

    private void initMap() {
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (fragment != null) fragment.getMapAsync(this);
    }

    private void handlePermanentDenial() {
        new AlertDialog.Builder(this)
                .setTitle("Location Permission Required")
                .setMessage("Please enable location permission in Settings")
                .setPositiveButton("Open Settings", (d, w) -> startActivity(
                        new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null))))
                .setNegativeButton("Cancel", (d, w) -> finish())
                .show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(loc -> {
                    LatLng pos = loc != null
                            ? new LatLng(loc.getLatitude(), loc.getLongitude())
                            : FALLBACK;
                    mMap.addMarker(new MarkerOptions().position(pos).title("You are here"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
                    updateAddress(pos);
                })
                .addOnFailureListener(e -> {
                    Log.e("MapsView", "Location fetch failed", e);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(FALLBACK, 15));
                });

        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Pinned Location"));
            updateAddress(latLng);
        });

        resetBanner.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                return;
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(loc -> {
                        LatLng current = loc != null
                                ? new LatLng(loc.getLatitude(), loc.getLongitude())
                                : FALLBACK;
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
                        updateAddress(current);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MapsView", "Failed to get current location", e);
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(FALLBACK).title("Default Location"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(FALLBACK, 15));
                        updateAddress(FALLBACK);
                    });
        });

        confirmLocationButton.setOnClickListener(v -> {
            // Validate if a date and time have been selected
            String selectedDateTimeStr = dateTime.getText().toString().trim();
            if (selectedDateTimeStr.isEmpty()) {
                Toast.makeText(MapsView.this, "Please select date and time", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parse the selected date/time (format: "dd/MM/yyyy hh:mm a")
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
            try {
                Date incidentDate = inputFormat.parse(selectedDateTimeStr);

                // Format date as "dd-MMM-yyyy" and time as "HH:mm"
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String formattedDate = dateFormatter.format(incidentDate);
                String formattedTime = timeFormatter.format(incidentDate);

                // Update UI and incidentselecteddate
                dateTime.setText(formattedDate + " " + formattedTime);
                incidentselecteddate = formattedDate + " " + formattedTime;

                // Proceed with location and navigation
                LatLng target = mMap.getCameraPosition().target;
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(target.latitude, target.longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    locationupdate = address.getAddressLine(0);
                    Intent redirect = new Intent(MapsView.this, ClaimVehicleSelection.class);
                    startActivity(redirect);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    Toast.makeText(this, "Unable to fetch address", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
                Toast.makeText(this, "Error processing date/time", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateAddress(LatLng latLng) {
        try {
            List<Address> list = new Geocoder(this, Locale.getDefault())
                    .getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (list != null && !list.isEmpty()) {
                Address a = list.get(0);
                cityName.setText(a.getLocality());
                StreetName.setText(a.getAddressLine(0));
            }
        } catch (IOException e) {
            Log.e("MapsView", "Geocoder error", e);
        }
    }

    private void initActionBar() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
            ab.setBackgroundDrawable(new ColorDrawable(getColor(R.color.purple_500)));
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
