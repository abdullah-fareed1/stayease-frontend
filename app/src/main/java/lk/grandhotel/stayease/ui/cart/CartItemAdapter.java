package lk.grandhotel.stayease.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.network.models.CartItemModel;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {

    private List<CartItemModel> items = new ArrayList<>();
    private final SimpleDateFormat parseFmt   = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private final SimpleDateFormat displayFmt = new SimpleDateFormat("dd MMM", Locale.getDefault());

    public void setItems(List<CartItemModel> newItems) {
        items = newItems != null ? new ArrayList<>(newItems) : new ArrayList<>();
        notifyDataSetChanged();
    }

    public CartItemModel getItemAt(int position) {
        return items.get(position);
    }

    public void removeAt(int position) {
        items.remove(position);
        notifyItemRemoved(position);
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
        if (item.room == null) return;

        h.tvRoomTitle.setText(item.room.title != null ? item.room.title : "");

        String imageUrl = item.room.primaryImage != null ? item.room.primaryImage.url : null;
        Glide.with(h.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_onboard_browse)
                .error(R.drawable.ic_onboard_browse)
                .centerCrop()
                .into(h.ivRoomImage);

        boolean available = "AVAILABLE".equals(item.room.availabilityStatus);
        h.tvUnavailableBadge.setVisibility(available ? View.GONE : View.VISIBLE);

        try {
            String ciStr  = displayFmt.format(parseFmt.parse(item.checkIn));
            String coStr  = displayFmt.format(parseFmt.parse(item.checkOut));
            h.tvDates.setText(ciStr + "  →  " + coStr);

            long diffMs = parseFmt.parse(item.checkOut).getTime()
                    - parseFmt.parse(item.checkIn).getTime();
            int nights = (int) (diffMs / (1000L * 60 * 60 * 24));
            h.tvNightsGuests.setText(
                    nights + " night" + (nights != 1 ? "s" : "")
                            + "  ·  " + item.guestCount + " guest" + (item.guestCount != 1 ? "s" : ""));

            double subtotal = item.room.pricePerNight * nights;
            h.tvSubtotal.setText(String.format(Locale.getDefault(), "$%.2f", subtotal));
        } catch (Exception ignored) {
            h.tvDates.setText("");
            h.tvNightsGuests.setText("");
            h.tvSubtotal.setText("");
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRoomImage;
        TextView  tvRoomTitle, tvDates, tvNightsGuests, tvSubtotal, tvUnavailableBadge;

        CartViewHolder(@NonNull View v) {
            super(v);
            ivRoomImage         = v.findViewById(R.id.iv_room_image);
            tvRoomTitle         = v.findViewById(R.id.tv_room_title);
            tvDates             = v.findViewById(R.id.tv_dates);
            tvNightsGuests      = v.findViewById(R.id.tv_nights_guests);
            tvSubtotal          = v.findViewById(R.id.tv_subtotal);
            tvUnavailableBadge  = v.findViewById(R.id.tv_unavailable_badge);
        }
    }
}