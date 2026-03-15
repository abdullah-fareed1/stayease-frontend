package lk.grandhotel.stayease.ui.room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.network.models.RoomModel;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder> {

    public interface OnImageClickListener {
        void onImageClick(int position);
    }

    private final List<RoomModel.ImageModel> images;
    private final OnImageClickListener       listener;

    public ImagePagerAdapter(List<RoomModel.ImageModel> images, OnImageClickListener listener) {
        this.images   = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room_image, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder h, int position) {
        Glide.with(h.imageView.getContext())
                .load(images.get(position).url)
                .placeholder(R.drawable.ic_onboard_browse)
                .centerCrop()
                .into(h.imageView);
        h.imageView.setOnClickListener(v -> listener.onImageClick(position));
    }

    @Override
    public int getItemCount() { return images.size(); }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageViewHolder(@NonNull View v) {
            super(v);
            imageView = v.findViewById(R.id.iv_room_carousel);
        }
    }
}