package lk.grandhotel.stayease.ui.bookings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.network.models.BookingModel;
import lk.grandhotel.stayease.utils.DateUtils;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    public interface OnBookingClickListener {
        void onBookingClick(BookingModel booking);
    }

    private List<BookingModel> items = new ArrayList<>();
    private final OnBookingClickListener listener;

    public BookingAdapter(OnBookingClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<BookingModel> newList) {
        if (newList == null) newList = new ArrayList<>();
        final List<BookingModel> oldList = items;
        final List<BookingModel> finalNew = newList;
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return oldList.size(); }
            @Override public int getNewListSize() { return finalNew.size(); }
            @Override public boolean areItemsTheSame(int o, int n) {
                return oldList.get(o).id != null && oldList.get(o).id.equals(finalNew.get(n).id);
            }
            @Override public boolean areContentsTheSame(int o, int n) {
                return oldList.get(o).status != null
                        && oldList.get(o).status.equals(finalNew.get(n).status);
            }
        });
        items = new ArrayList<>(newList);
        diff.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_card, parent, false);
        return new BookingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder h, int position) {
        h.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivRoom;
        TextView tvRoomTitle, tvDates, tvStatus, tvAmount;

        BookingViewHolder(@NonNull View v) {
            super(v);
            ivRoom      = v.findViewById(R.id.iv_booking_room);
            tvRoomTitle = v.findViewById(R.id.tv_booking_room_title);
            tvDates     = v.findViewById(R.id.tv_booking_dates);
            tvStatus    = v.findViewById(R.id.tv_booking_status);
            tvAmount    = v.findViewById(R.id.tv_booking_amount);
        }

        void bind(BookingModel b, OnBookingClickListener listener) {
            String title = (b.room != null && b.room.title != null) ? b.room.title : "Room";
            tvRoomTitle.setText(title);
            tvDates.setText(DateUtils.toDisplayString(b.checkIn)
                    + "  →  " + DateUtils.toDisplayString(b.checkOut));
            tvAmount.setText("$" + b.getTotalAmountDouble());
            bindStatus(tvStatus, b.status);

            String imageUrl = (b.room != null && b.room.primaryImage != null)
                    ? b.room.primaryImage.url : null;
            Glide.with(ivRoom.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_onboard_browse)
                    .centerCrop()
                    .into(ivRoom);

            itemView.setOnClickListener(v -> listener.onBookingClick(b));
        }

        private void bindStatus(TextView tv, String status) {
            if (status == null) return;
            Context ctx = tv.getContext();
            tv.setText(status.replace("_", " "));
            switch (status) {
                case "PENDING":
                    tv.setBackgroundTintList(ContextCompat.getColorStateList(ctx, R.color.warning));
                    tv.setTextColor(ContextCompat.getColor(ctx, android.R.color.white));
                    break;
                case "CONFIRMED":
                    tv.setBackgroundTintList(ContextCompat.getColorStateList(ctx, R.color.secondary));
                    tv.setTextColor(ContextCompat.getColor(ctx, android.R.color.white));
                    break;
                case "CHECKED_IN":
                    tv.setBackgroundTintList(ContextCompat.getColorStateList(ctx, R.color.success));
                    tv.setTextColor(ContextCompat.getColor(ctx, android.R.color.white));
                    break;
                case "CHECKED_OUT":
                    tv.setBackgroundTintList(ContextCompat.getColorStateList(ctx, R.color.on_surface_variant));
                    tv.setTextColor(ContextCompat.getColor(ctx, android.R.color.white));
                    break;
                case "CANCELLED":
                    tv.setBackgroundTintList(ContextCompat.getColorStateList(ctx, R.color.error));
                    tv.setTextColor(ContextCompat.getColor(ctx, android.R.color.white));
                    break;
                default:
                    tv.setBackgroundTintList(ContextCompat.getColorStateList(ctx, R.color.divider));
                    tv.setTextColor(ContextCompat.getColor(ctx, R.color.on_surface));
                    break;
            }
        }
    }
}