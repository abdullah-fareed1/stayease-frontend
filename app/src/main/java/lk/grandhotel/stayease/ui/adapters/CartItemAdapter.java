package lk.grandhotel.stayease.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.network.models.CartItemModel;
import lk.grandhotel.stayease.utils.DateUtils;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {

    private List<CartItemModel> items = new ArrayList<>();

    public void setItems(List<CartItemModel> newItems) {
        this.items = newItems != null ? new ArrayList<>(newItems) : new ArrayList<>();
        notifyDataSetChanged();
    }

    public CartItemModel getItemAt(int position) {
        return items.get(position);
    }

    public void removeAt(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreAt(int position, CartItemModel item) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_item, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder h, int position) {
        CartItemModel item = items.get(position);

        String title = (item.room != null && item.room.title != null) ? item.room.title : "Room";
        h.tvTitle.setText(title);

        String checkIn = DateUtils.toDisplayString(item.checkIn);
        String checkOut = DateUtils.toDisplayString(item.checkOut);
        h.tvDates.setText(checkIn + "  →  " + checkOut);

        h.tvNights.setText(item.nights + (item.nights == 1 ? " night" : " nights"));
        h.tvGuests.setText(item.guestCount + " guest" + (item.guestCount > 1 ? "s" : ""));
        h.tvSubtotal.setText(String.format(Locale.getDefault(), "$%.2f", item.subtotal));

        String imageUrl = (item.room != null && item.room.primaryImage != null)
                ? item.room.primaryImage.url : null;
        Glide.with(h.ivImage.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_onboard_browse)
                .centerCrop()
                .into(h.ivImage);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvDates, tvNights, tvGuests, tvSubtotal;

        CartViewHolder(@NonNull View v) {
            super(v);
            ivImage    = v.findViewById(R.id.iv_cart_room_image);
            tvTitle    = v.findViewById(R.id.tv_cart_room_title);
            tvDates    = v.findViewById(R.id.tv_cart_dates);
            tvNights   = v.findViewById(R.id.tv_cart_nights);
            tvGuests   = v.findViewById(R.id.tv_cart_guests);
            tvSubtotal = v.findViewById(R.id.tv_cart_subtotal);
        }
    }
}