package lk.grandhotel.stayease.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.network.models.AdminBookingModel;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.BookingVH> {

    public interface OnBookingClickListener {
        void onBookingClick(AdminBookingModel booking);
    }

    private List<AdminBookingModel> bookings = new ArrayList<>();
    private final OnBookingClickListener listener;

    public AdminBookingAdapter(OnBookingClickListener listener) {
        this.listener = listener;
    }

    public void setBookings(List<AdminBookingModel> newBookings) {
        this.bookings = newBookings != null ? new ArrayList<>(newBookings) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_booking_card, parent, false);
        return new BookingVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingVH h, int position) {
        AdminBookingModel booking = bookings.get(position);
        h.tvGuestName.setText(booking.guestName != null ? booking.guestName : "");
        h.tvGuestEmail.setText(booking.guestEmail != null ? booking.guestEmail : "");
        h.tvRoomTitle.setText(booking.roomTitle != null ? booking.roomTitle : "");
        h.tvDates.setText(formatDates(booking.checkIn, booking.checkOut));
        h.tvTotalAmount.setText(String.format(Locale.getDefault(), "$%.2f", booking.getTotalAmountDouble()));

        bindStatusChip(h.chipStatus, booking.status);

        h.itemView.setOnClickListener(v -> listener.onBookingClick(booking));
    }

    private String formatDates(String checkIn, String checkOut) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        try {
            Date in = inputFormat.parse(checkIn);
            Date out = inputFormat.parse(checkOut);
            return outputFormat.format(in) + " - " + outputFormat.format(out);
        } catch (ParseException e) {
            return checkIn + " - " + checkOut;
        }
    }

    private void bindStatusChip(Chip chip, String status) {
        if (status == null) return;
        chip.setText(status);
        switch (status.toUpperCase()) {
            case "PENDING":
                chip.setChipBackgroundColorResource(R.color.warning);
                break;
            case "CONFIRMED":
                chip.setChipBackgroundColorResource(R.color.success);
                break;
            case "CHECKED_IN":
                chip.setChipBackgroundColorResource(R.color.success);
                break;
            case "CHECKED_OUT":
                chip.setChipBackgroundColorResource(R.color.primary);
                break;
            case "CANCELLED":
                chip.setChipBackgroundColorResource(R.color.error);
                break;
            default:
                chip.setChipBackgroundColorResource(R.color.on_surface_variant);
                break;
        }
    }

    @Override
    public int getItemCount() { return bookings.size(); }

    static class BookingVH extends RecyclerView.ViewHolder {
        TextView tvGuestName, tvGuestEmail, tvRoomTitle, tvDates, tvTotalAmount;
        Chip chipStatus;

        BookingVH(@NonNull View v) {
            super(v);
            tvGuestName = v.findViewById(R.id.tv_guest_name);
            tvGuestEmail = v.findViewById(R.id.tv_guest_email);
            tvRoomTitle = v.findViewById(R.id.tv_room_title);
            tvDates = v.findViewById(R.id.tv_dates);
            tvTotalAmount = v.findViewById(R.id.tv_total_amount);
            chipStatus = v.findViewById(R.id.chip_status);
        }
    }
}