package au.edu.unimelb.eresearch.happypets.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;

import androidx.core.content.FileProvider;
import au.edu.unimelb.eresearch.happypets.Constants;
import au.edu.unimelb.eresearch.happypets.R;
import au.edu.unimelb.eresearch.happypets.utils.GeneralUtils;

/**
 * Home fragment - lets user start predicting pet images
 */
public class HomeFragment extends BaseFragment
        implements ActivityCompat.OnRequestPermissionsResultCallback {
    private Button btnCamera;
    private Button btnGallery;

    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnCamera = view.findViewById(R.id.btn_camera);
        btnGallery = view.findViewById(R.id.btn_gallery);
        btnCamera.setOnClickListener(v -> openCamera());
        btnGallery.setOnClickListener(v -> openGallery());

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_PERMISSION_CAMERA:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }
            default:
                break;
        }
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.CAMERA }, Constants.REQUEST_PERMISSION_CAMERA);
            return;
        }

        // Create temporary file
        File imgFile = GeneralUtils.createTempImgFile(getFActivity());

        if (imgFile != null) {
            Uri imgUri = FileProvider.getUriForFile(getFActivity(),
                    "au.edu.unimelb.eresearch.happypets.fileprovider",
                    imgFile);
            GeneralUtils.tempImgPath = imgUri;
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
            getFActivity().startActivityForResult(intent, Constants.REQUEST_CAMERA);
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getFActivity().startActivityForResult(Intent.createChooser(intent, "Select a picture of your pet"),
                Constants.REQUEST_GALLERY);
    }
}
