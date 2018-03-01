package example.luisnava.com.firebaseexample;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import Classes.Stores;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFragment extends Fragment implements View.OnClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    private Button btnChoose, btnAdd;
    private ImageView imageView;
    private EditText inputTitle, inputAddress;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    View mView;

    private GoogleMap mGoogleMap;
    MapView mMapView;

    private Geocoder geocoder;
    private List<Address> addresses;
    private Double latitude;
    private Double longitude;

    FirebaseStorage storage;
    StorageReference storageReference;

    private DatabaseReference mDatabase;


    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_add, container, false);
        btnChoose = (Button) mView.findViewById(R.id.btnChoose);
        btnAdd = (Button) mView.findViewById(R.id.btnAdd);
        imageView = (ImageView) mView.findViewById(R.id.imgView);
        inputAddress = (EditText) mView.findViewById(R.id.address);
        inputTitle = (EditText) mView.findViewById(R.id.title);

        btnChoose.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        geocoder = new Geocoder(getContext(), Locale.getDefault());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMapView = (MapView) mView.findViewById(R.id.mapViewAdd);

        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setOnMapClickListener(this);

        }

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btnChoose) {
            chooseImage();
        } else if (view.getId() == R.id.btnAdd) {
            //uploadImage();
            if (TextUtils.isEmpty(inputTitle.getText().toString().trim())) {
                Toast.makeText(getContext(), "Enter store title!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(inputAddress.getText().toString().trim())) {
                Toast.makeText(getContext(), "Select an address, add a marker in the map!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (filePath == null) {
                Toast.makeText(getContext(), "Choose an Image!", Toast.LENGTH_SHORT).show();
                return;
            }

            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("Stores/"+ UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Stores store = new Stores(inputTitle.getText().toString().trim(), inputAddress.getText().toString().trim(),
                                    taskSnapshot.getDownloadUrl().toString(), latitude, longitude);

                            mDatabase.child("Stores").push().setValue(store);
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            clearFields();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }

    }

    private void clearFields() {
        inputAddress.setText("");
        inputTitle.setText("");
        mGoogleMap.clear();
        imageView.setImageDrawable(null);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng);
        marker.title("Selected Location");
        inputAddress.setText("");
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                inputAddress.setText(address);

            } else {
                inputAddress.setText("");
                Toast.makeText(getContext(), "Direction Not found",
                        Toast.LENGTH_LONG).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        mGoogleMap.clear();
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mGoogleMap.addMarker(marker);
    }

}
