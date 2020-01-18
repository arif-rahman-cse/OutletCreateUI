package com.smarifrahman.outletcreateui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.smarifrahman.outletcreateui.adapter.SearchViewAdapter;
import com.smarifrahman.outletcreateui.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = "MainActivity";

    //wiz
    private TextView capturePhoto, browseGallery;
    private MaterialButton closeBtn;
    private RecyclerView searchRecyclerView;
    private SearchView searchView;

    //Var
    private static final int AUTOCOMPLETE_REQUEST_CODE = 101;
    private static final int LOCATION_PERMISSION_ID = 102;
    private static final int GALLERY_REQUEST_CODE = 103;
    private static final int CAMERA_PERMISSION_CODE = 104;
    private static final int PICK_FROM_CAMERA = 105;
    private ActivityMainBinding mainBinding;
    private FusedLocationProviderClient mFusedLocationClient;
    private List<String> categories;
    private SearchViewAdapter searchViewAdapter;
    private ArrayAdapter<CharSequence> adapter;
    private PlacesClient placesClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Data binding
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Set Click listener
        mainBinding.addImage.setOnClickListener(this);
        mainBinding.outletAddressSearch.setOnClickListener(this);
        mainBinding.pickCurrentLocation.setOnClickListener(this);
        mainBinding.spinnerDealer.setOnItemSelectedListener(this);
        mainBinding.spinnerRoute.setOnItemSelectedListener(this);
        mainBinding.test.setOnClickListener(this);

        mainBinding.premier.setOnClickListener(this);
        mainBinding.nonPremier.setOnClickListener(this);
        mainBinding.submitBtn.setOnClickListener(this);
        mainBinding.steel.setOnClickListener(this);
        mainBinding.sand.setOnClickListener(this);
        mainBinding.brick.setOnClickListener(this);
        mainBinding.food.setOnClickListener(this);
        mainBinding.others.setOnClickListener(this);
        mainBinding.otherCategories.setOnClickListener(this);

        //Address
        mainBinding.outletUnionBazar.setEnabled(false);
        mainBinding.outletThana.setEnabled(false);
        mainBinding.outletDistricts.setEnabled(false);


        // Spinner Drop down elements
        categories = new ArrayList<>();
        categories.add("- Select Dealer -");
        categories.add("Samsung");
        categories.add("Foxconn");
        categories.add("Apple");
        categories.add("Oppo");
        categories.add("Nokia");
        categories.add("LYF");
        categories.add("Xiaomi");
        categories.add("Huawei");
        categories.add("Asus");
        categories.add("Lenovo");
        categories.add("Nokia");
        categories.add("LYF");
        categories.add("Xiaomi");


        // Creating ArrayAdapter using the string array and default spinner layout
        adapter = ArrayAdapter.createFromResource(this,
                R.array.mobile_manufacturers, android.R.layout.simple_spinner_item);

        // Specify layout to be used when list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Applying the adapter to spinner
        mainBinding.spinnerDealer.setAdapter(adapter);
        mainBinding.spinnerRoute.setAdapter(adapter);


        String mapApiKey = getString(R.string.MAP_API_KEY);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), mapApiKey);
        }

        // Create a new Places client instance.
        placesClient = Places.createClient(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        if (position == 0) {
            Log.d(TAG, "onItemSelected: first position of spinner");
        } else {

            // Showing selected spinner item
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //----------------------------------- Handel Click Listener -----------------------------------//
    @Override
    public void onClick(View v) {

        int clickedId = v.getId();


        switch (clickedId) {

            case R.id.test: {
                uopUpSearchView(v);
                break;
            }

            case R.id.add_image: {
                popUpOptionDialog(v);
                break;
            }


            case R.id.outlet_address_search: {

                Log.d(TAG, "onClick: Outlet Address: ");
                onSearchCalled();
                break;
            }

            case R.id.pick_current_Location: {
                Log.d(TAG, "onClick: Current location: ");
                getDeviceCurrentLocation();
                break;
            }

            //-------------------------- Check Box validation -------------------------------------//
            case R.id.premier: {

                if (mainBinding.premier.isChecked()) {
                    mainBinding.nonPremier.setChecked(false);
                    Toast.makeText(this, "Premier", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.non_premier: {

                if (mainBinding.nonPremier.isChecked()) {
                    mainBinding.premier.setChecked(false);
                    Toast.makeText(this, "Non Premier", Toast.LENGTH_SHORT).show();
                }
                break;

            }

            case R.id.steel: {

                if (mainBinding.steel.isChecked()) {
                    Toast.makeText(this, "Steel", Toast.LENGTH_SHORT).show();
                }
                break;

            }

            case R.id.sand: {

                if (mainBinding.sand.isChecked()) {
                    Toast.makeText(this, "Sand", Toast.LENGTH_SHORT).show();
                }
                break;

            }

            case R.id.brick: {

                if (mainBinding.brick.isChecked()) {
                    Toast.makeText(this, "Brick", Toast.LENGTH_SHORT).show();
                }
                break;

            }

            case R.id.stone: {

                if (mainBinding.stone.isChecked()) {
                    Toast.makeText(this, "Stone", Toast.LENGTH_SHORT).show();
                }
                break;

            }

            case R.id.food: {

                if (mainBinding.food.isChecked()) {
                    Toast.makeText(this, "Food", Toast.LENGTH_SHORT).show();
                }
                break;

            }

            case R.id.others: {

                if (mainBinding.others.isChecked()) {
                    Toast.makeText(this, "Others", Toast.LENGTH_SHORT).show();

                    mainBinding.otherCategories.setVisibility(View.VISIBLE);
                }

                if (!mainBinding.others.isChecked()) {
                    mainBinding.otherCategories.setVisibility(View.GONE);
                }
                break;

            }

            //----------------------------------- Submit Data -------------------------------------//
            case R.id.submit_btn: {
                //TODO
                break;
            }

        }

    }


    //------------------------------------- Search dealer and route -------------------------------//
    private void uopUpSearchView(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.search_view, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        initSearchXml(dialogView, alertDialog);
    }

    private void initSearchXml(View dialogView, final AlertDialog alertDialog) {

        closeBtn = dialogView.findViewById(R.id.search_close_btn);
        searchRecyclerView = dialogView.findViewById(R.id.search_view_rv);
        searchView = dialogView.findViewById(R.id.search_view);


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "clicked" + categories, Toast.LENGTH_LONG).show();
                Log.d(TAG, "onClick: Dialog Closed: " + categories);
                alertDialog.dismiss();
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchViewAdapter.getFilter().filter(newText);
                return true;
            }
        });

        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchViewAdapter = new SearchViewAdapter(this, categories);
        searchRecyclerView.setAdapter(searchViewAdapter);
        searchViewAdapter.notifyDataSetChanged();

    }


    //-------------------------------- popup option dialog for pic photo --------------------------//
    private void popUpOptionDialog(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.option_dialog_image_upload, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        initXml(dialogView, alertDialog);
    }

    private void initXml(View dialogView, final AlertDialog alertDialog) {

        capturePhoto = dialogView.findViewById(R.id.capture_photo_tv);
        browseGallery = dialogView.findViewById(R.id.browse_gallery_tv);

        capturePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Capture Photo");
                alertDialog.dismiss();
                checkCameraPermission();
            }
        });

        browseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Browse Gallery");
                alertDialog.dismiss();
                pickFromGallery();
            }
        });


    }

    //------------------------------- Image Capture form gallery and Camera-------------------------//

    private void pickFromGallery() {

        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE);

    }

    private void checkCameraPermission() {

        //openCamera();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, CAMERA_PERMISSION_CODE);

            } else {
                //Permission already granted
                openCamera();
            }
        } else {
            //System OS < Marshmallow
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
            Toast.makeText(this, "Camera is Calling", Toast.LENGTH_SHORT).show();
        }
    }


    //----------------------------------- Get User Current Location -------------------------------//
    @SuppressLint("MissingPermission")
    private void getDeviceCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient
                        .getLastLocation()
                        .addOnCompleteListener(
                                new OnCompleteListener<Location>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Location> task) {
                                        Location location = task.getResult();
                                        if (location == null) {
                                            requestNewLocationData();
                                        } else {
                                            Log.d(TAG, "getLastLocation: onComplete: " + location.getLatitude());
                                            Log.d(TAG, "getLastLocation: onComplete: " + location.getLongitude());

                                            getAddress(location.getLatitude(), location.getLongitude());
                                        }
                                    }
                                }
                        );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }


    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.d(TAG, "getLastLocation: onComplete: " + mLastLocation.getLatitude());
            Log.d(TAG, "getLastLocation: onComplete: " + mLastLocation.getLongitude());
            getAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    };


    //---------------------------------------- AutoComplete Location ------------------------------//
    private void onSearchCalled() {

        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("BD") //Bangladesh
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.d(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());

                String placeName = place.getName();
                String fullAddress = place.getAddress();

                mainBinding.outletAddress.setText(placeName);
                // do query with address

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(MainActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, "Status; " + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.d(TAG, "onActivityResult: User canceled the request");
            }
        }


        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Initialize a new ByteArrayStream
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
            byte[] byteArray = stream.toByteArray();
            Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            // Display the compressed bitmap in ImageView
            mainBinding.proprietorImage.setImageBitmap(compressedBitmap);
            Toast.makeText(this, "Compressed Image / JPEG 50% Quality.", Toast.LENGTH_SHORT).show();


        } else if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {

            Log.d(TAG, "onActivityResult: Pick form gallery");

            if (data != null) {
                Uri contentURI = data.getData();
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    mainBinding.proprietorImage.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }


        }


    }


    //------------------------------------------- Get Location  Permission --------------------------------------------------//
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission from pop is granted
                    openCamera();
                } else {
                    //Permission from pop is denied
                    Toast.makeText(this, "Permission from pop is denied", Toast.LENGTH_SHORT).show();
                }
            }
            case LOCATION_PERMISSION_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Granted. Start getting the location information
                    Log.d(TAG, "onRequestPermissionsResult: Start getting the location information");
                }
            }
        }

    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    //---------------- Get Location from user current latitude and longitude ----------------------//
    private void getAddress(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                String outletLocation = address.getAddressLine(0);
                String outletUnion = address.getLocality();
                String outletThana = address.getSubAdminArea();
                String outletDistrict = address.getAdminArea();

                Log.d(TAG, "getAddress: Outlent Address: "+ outletLocation+ " Union: "+ outletUnion+ " Thana: "+ outletThana+ " District: "+ outletDistrict);
                mainBinding.outletAddress.setText(outletLocation);
                mainBinding.outletUnionBazar.setText(outletUnion);
                mainBinding.outletThana.setText(outletThana);
                mainBinding.outletDistricts.setText(outletDistrict);
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

    }
}
