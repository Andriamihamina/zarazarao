package com.sebastienyannis.zarazarao.ui.ServeImagesPage;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sebastienyannis.zarazarao.R;

import java.util.List;



public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ImageItemViewHolder> {

    private final List<Uri> imageUris;
    private final Context context;

    public static class ImageItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageItemView;
        public ImageItemViewHolder(@NonNull View imageItemView) {
            super(imageItemView);
            this.imageItemView = imageItemView.findViewById(R.id.imageGrid);
        }
    }

    public ImageGridAdapter(Context context, List<Uri> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ImageItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageItemViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        Glide.with(context)
            .load(imageUri).centerCrop()
            .centerCrop()
            .into(holder.imageItemView);
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

}
