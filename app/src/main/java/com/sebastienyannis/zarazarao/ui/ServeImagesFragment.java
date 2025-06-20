package com.sebastienyannis.zarazarao.ui;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sebastienyannis.zarazarao.databinding.FragmentServeImagesBinding;
import com.sebastienyannis.zarazarao.databinding.FragmentWelcomeBinding;

import java.util.ArrayList;
import java.util.List;


public class ServeImagesFragment extends Fragment {

    private FragmentServeImagesBinding binding;
    private ActivityResultLauncher<String> permissionLauncher;


    public ServeImagesFragment() {

    }

    public static ServeImagesFragment newInstance() {
        ServeImagesFragment fragment = new ServeImagesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentServeImagesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    void setupRecyclerView() {
        RecyclerView recyclerView = binding.imageGrid;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        List<Uri> imageUris = this.loadImagesFromGallery(requireContext());
        recyclerView.setAdapter(new ImageGridAdapter(getContext(), imageUris));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        setupRecyclerView();
                    } else {
                        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED) {
            setupRecyclerView(); // Permission already granted
        } else {
            permissionLauncher.launch(permission); // Request permission
        }

    }




    private List<Uri> loadImagesFromGallery(Context context) {
        List<Uri> imageUris = new ArrayList<>();
        Uri collection;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[]{
                MediaStore.Images.Media._ID
        };

        try (Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        )) {
            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    Uri contentUri = ContentUris.withAppendedId(collection, id); // fixed
                    imageUris.add(contentUri);
                }
            }
        }

        return imageUris;
    }
}