package lk.grandhotel.stayease.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.network.models.AdminRoomModel;

public class AdminRoomAdapter extends RecyclerView.Adapter<AdminRoomAdapter.RoomVH> {

    public interface OnRoomClickListener {
        void onRoomClick(AdminRoomModel room);
    }

    private List<AdminRoomModel> rooms = new ArrayList<>();
    private final OnRoomClickListener listener;

    public AdminRoomAdapter(OnRoomClickListener listener) {
        this.listener = listener;
    }

    public void setRooms(List<AdminRoomModel> newRooms) {
        this.rooms = newRooms != null ? new ArrayList<>(newRooms) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_room_card, parent, false);
        return new RoomVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomVH h, int position) {
        AdminRoomModel room = rooms.get(position);
        h.tvTitle.setText(room.title != null ? room.title : "");
        h.tvCategory.setText(room.category != null ? room.category : "");
        h.tvPrice.setText(String.format(Locale.getDefault(), "$%.0f /night", room.getPriceDouble()));
        h.tvMaxGuests.setText(room.maxGuests + " guests max");

        bindAvailability(h.tvAvailability, room.availabilityStatus);

        String imageUrl = (room.primaryImage != null) ? room.primaryImage.url : null;
        Glide.with(h.ivImage.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_onboard_browse)
                .centerCrop()
                .into(h.ivImage);

        h.itemView.setOnClickListener(v -> listener.onRoomClick(room));
    }

    private void bindAvailability(TextView tv, String status) {
        if (status == null) return;
        switch (status) {
            case "AVAILABLE":
                tv.setText("Available");
                tv.setBackgroundTintList(ContextCompat.getColorStateList(tv.getContext(), R.color.success));
                break;
            case "TEMP_UNAVAILABLE":
                tv.setText("Temp Unavailable");
                tv.setBackgroundTintList(ContextCompat.getColorStateList(tv.getContext(), R.color.warning));
                break;
            case "PERMANENTLY_UNAVAILABLE":
                tv.setText("Hidden");
                tv.setBackgroundTintList(ContextCompat.getColorStateList(tv.getContext(), R.color.error));
                break;
            default:
                tv.setText(status);
                break;
        }
    }

    @Override
    public int getItemCount() { return rooms.size(); }

    static class RoomVH extends RecyclerView.ViewHolder {
        ShapeableImageView ivImage;
        TextView tvTitle, tvCategory, tvPrice, tvMaxGuests, tvAvailability;

        RoomVH(@NonNull View v) {
            super(v);
            ivImage = v.findViewById(R.id.iv_admin_room_image);
            tvTitle = v.findViewById(R.id.tv_admin_room_title);
            tvCategory = v.findViewById(R.id.tv_admin_room_category);
            tvPrice = v.findViewById(R.id.tv_admin_room_price);
            tvMaxGuests = v.findViewById(R.id.tv_admin_room_guests);
            tvAvailability = v.findViewById(R.id.tv_admin_room_availability);
        }
    }
}