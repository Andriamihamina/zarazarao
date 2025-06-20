package com.sebastienyannis.zarazarao.ui;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sebastienyannis.zarazarao.R;

import java.util.List;



public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ImageItemViewHolder> {

    private final List<Uri> imageUris;
    private final Context context;

    public class ImageItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageItemView;
        public ImageItemViewHolder(@NonNull View imageItemView) {
            super(imageItemView);
            this.imageItemView = imageItemView.findViewById(R.id.imageView);
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
        holder.imageItemView.setImageURI(imageUri);
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

}
