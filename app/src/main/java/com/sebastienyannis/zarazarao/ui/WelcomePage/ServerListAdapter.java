package com.sebastienyannis.zarazarao.ui.WelcomePage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sebastienyannis.zarazarao.R;

import java.util.Arrays;
import java.util.List;

import javax.jmdns.ServiceInfo;

public class ServerListAdapter extends ListAdapter<ServiceInfo, ServerListAdapter.ServerItemViewHolder> {

    public ServerListAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<ServiceInfo> DIFF_CALLBACK = new DiffUtil.ItemCallback<ServiceInfo>() {
        @Override
        public boolean areItemsTheSame(@NonNull ServiceInfo oldItem, @NonNull ServiceInfo newItem) {
            // Match by service name
            return oldItem.getName().equals(newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ServiceInfo oldItem, @NonNull ServiceInfo newItem) {
            // Match by full content (may customize later)
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getPort() == newItem.getPort() &&
                    Arrays.equals(oldItem.getInetAddresses(), newItem.getInetAddresses());
        }
    };

    @NonNull
    @Override
    public ServerItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_info_item, parent, false);
        return new ServerItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServerItemViewHolder holder, int position) {
        ServiceInfo info = getItem(position);
        holder.bind(info);
    }

    static class ServerItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ServerItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.server_name); // Make sure this ID exists
        }

        public void bind(ServiceInfo info) {
            String name = info.getName();
            if (name == null || name.isEmpty()) {
                name = info.getQualifiedName();
            }
            textView.setText(name);
        }
    }
}

