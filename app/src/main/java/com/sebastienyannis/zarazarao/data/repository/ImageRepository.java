package com.sebastienyannis.zarazarao.data.repository;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Size;

import com.google.gson.Gson;
import com.sebastienyannis.zarazarao.backend.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;

public class ImageRepository {
    private static ImageRepository instance;

    public static ImageRepository getInstance() {
        if (instance == null) {
            instance = new ImageRepository();
        }
        return instance;
    }

    public List<Uri> loadImagesFromGallery(Context context) {
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

    //TODO move to an utility class
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    public String prepareJsonWithThumbnails(List<Uri> imageUris, Context context) {
        List<HttpServer.ImageInfo> imageInfos = new ArrayList<>();
        for (Uri uri: imageUris) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Bitmap thumbnail = context.getContentResolver().loadThumbnail(uri, new Size(200, 200), null);
                    String base64Thumb = bitmapToBase64(thumbnail);
                    imageInfos.add(new HttpServer.ImageInfo(uri.toString(), base64Thumb));
                }
            } catch (IOException e) {
                Log.e("ImageRepository", "Not able to build json data", e);
            }
        }
        return new Gson().toJson(imageInfos);
    }

    public String paginateImages(int offset, int limit, Context context) {
        ImageRepository imageRepository = ImageRepository.getInstance();
        List<Uri> allUris = imageRepository.loadImagesFromGallery(context);
        int total = allUris.size();

        int safeOffset = Math.max(0, offset);
        int safeLimit = Math.min(limit, total - safeOffset);

        List<Uri> pagedUris = allUris.subList(safeOffset, safeOffset + safeLimit);

        List<HttpServer.ImageInfo> results = new ArrayList<>();
        for (Uri uri : pagedUris) {
            try {
                Bitmap thumb = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    thumb = context.getContentResolver().loadThumbnail(uri, new Size(200, 200), null);
                }
                String base64Thumb = bitmapToBase64(thumb);
                results.add(new HttpServer.ImageInfo(uri.toString(), base64Thumb));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        HttpServer.PaginatedImages responseBody = new HttpServer.PaginatedImages(total, safeOffset, safeLimit, results);
        return new Gson().toJson(responseBody);

    }
}
