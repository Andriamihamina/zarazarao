package com.sebastienyannis.zarazarao.ui.ServeImagesPage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sebastienyannis.zarazarao.databinding.FragmentServeImagesBinding;
import com.sebastienyannis.zarazarao.data.model.ServeImageViewModel;

import java.util.List;


public class ServeImagesFragment extends Fragment {

    private FragmentServeImagesBinding binding;
    private ServeImageViewModel imageViewModel;


    public ServeImagesFragment() {
        imageViewModel = new ServeImageViewModel();
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
        List<Uri> imageUris = this.imageViewModel.loadImagesFromGallery(requireContext());
        recyclerView.setAdapter(new ImageGridAdapter(getContext(), imageUris));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
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



    //TODO move to model

}