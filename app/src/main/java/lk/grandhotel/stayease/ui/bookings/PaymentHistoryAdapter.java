package lk.grandhotel.stayease.ui.bookings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.network.models.PaymentModel;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.PaymentVH> {

    private final List<PaymentModel> payments;

    public PaymentHistoryAdapter(List<PaymentModel> payments) {
        this.payments = payments;
    }

    @NonNull
    @Override
    public PaymentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment_history, parent, false);
        return new PaymentVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentVH h, int position) {
        PaymentModel p = payments.get(position);
        h.tvType.setText(p.type != null ? p.type : "");
        h.tvAmount.setText(String.format(Locale.getDefault(), "$%.2f", p.getAmountDouble()));
        h.tvStatus.setText(p.status != null ? p.status : "");
        h.tvDate.setText(p.paidAt != null && p.paidAt.length() >= 10
                ? p.paidAt.substring(0, 10) : "Pending");
    }

    @Override
    public int getItemCount() { return payments != null ? payments.size() : 0; }

    static class PaymentVH extends RecyclerView.ViewHolder {
        TextView tvType, tvAmount, tvStatus, tvDate;
        PaymentVH(@NonNull View v) {
            super(v);
            tvType   = v.findViewById(R.id.tv_payment_type);
            tvAmount = v.findViewById(R.id.tv_payment_amount);
            tvStatus = v.findViewById(R.id.tv_payment_status);
            tvDate   = v.findViewById(R.id.tv_payment_date);
        }
    }
}