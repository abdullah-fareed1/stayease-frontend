package lk.grandhotel.stayease.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.network.models.RoomModel;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    public interface OnRoomClickListener {
        void onRoomClick(RoomModel room);
    }

    private List<RoomModel> rooms = new ArrayList<>();
    private final OnRoomClickListener listener;

    public RoomAdapter(OnRoomClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<RoomModel> newList) {
        if (newList == null) newList = new ArrayList<>();
        final List<RoomModel> oldList = rooms;
        final List<RoomModel> finalNewList = newList;
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return oldList.size(); }
            @Override public int getNewListSize() { return finalNewList.size(); }
            @Override public boolean areItemsTheSame(int o, int n) {
                return oldList.get(o).id != null && oldList.get(o).id.equals(finalNewList.get(n).id);
            }
            @Override public boolean areContentsTheSame(int o, int n) {
                return areItemsTheSame(o, n);
            }
        });
        rooms = new ArrayList<>(newList);
        diff.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_card, parent, false);
        return new RoomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder h, int position) {
        h.bind(rooms.get(position), listener);
    }

    @Override
    public int getItemCount() { return rooms.size(); }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvCategory, tvTitle, tvGuests, tvPrice, tvRating;
        RatingBar ratingBar;

        RoomViewHolder(@NonNull View v) {
            super(v);
            ivImage    = v.findViewById(R.id.iv_room_image);
            tvCategory = v.findViewById(R.id.tv_room_category);
            tvTitle    = v.findViewById(R.id.tv_room_title);
            tvGuests   = v.findViewById(R.id.tv_room_guests);
            tvPrice    = v.findViewById(R.id.tv_room_price);
            tvRating   = v.findViewById(R.id.tv_rating);
            ratingBar  = v.findViewById(R.id.rating_bar);
        }

        void bind(RoomModel room, OnRoomClickListener listener) {
            tvCategory.setText(room.category != null ? room.category : "");
            tvTitle.setText(room.title != null ? room.title : "");
            tvGuests.setText(room.maxGuests + " Guests max");
            tvPrice.setText(String.format(Locale.getDefault(), "$%.0f /night", room.pricePerNight));

            if (room.averageRating != null && room.averageRating > 0) {
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setRating(room.averageRating.floatValue());
                tvRating.setText(String.format(Locale.getDefault(), "%.1f", room.averageRating));
            } else {
                ratingBar.setVisibility(View.INVISIBLE);
                tvRating.setText("");
            }

            String url = (room.primaryImage != null) ? room.primaryImage.url : null;
            Glide.with(itemView.getContext())
                    .load(url)
                    .placeholder(R.drawable.ic_onboard_browse)
                    .error(R.drawable.ic_onboard_browse)
                    .centerCrop()
                    .into(ivImage);

            itemView.setOnClickListener(v -> listener.onRoomClick(room));
        }
    }
}