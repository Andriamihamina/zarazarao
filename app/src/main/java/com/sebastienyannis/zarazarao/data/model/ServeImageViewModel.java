package com.sebastienyannis.zarazarao.data.model;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.lifecycle.ViewModel;

import com.sebastienyannis.zarazarao.data.repository.ImageRepository;

import java.util.ArrayList;
import java.util.List;

public class ServeImageViewModel extends ViewModel {
    private List<Uri> images;

    //TODO use LiveData
    public List<Uri> loadImagesFromGallery(Context context) {
        ImageRepository repository = ImageRepository.getInstance();
        images = repository.loadImagesFromGallery(context);
        return images;
    }

}
