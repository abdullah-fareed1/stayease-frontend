package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import lk.grandhotel.stayease.databinding.ActivityCartBinding;
import lk.grandhotel.stayease.network.models.BookingModel;
import lk.grandhotel.stayease.network.models.CartItemModel;
import lk.grandhotel.stayease.ui.cart.CartItemAdapter;
import lk.grandhotel.stayease.viewmodel.CartViewModel;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;
    private CartViewModel       viewModel;
    private CartItemAdapter     adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        adapter   = new CartItemAdapter();

        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCartItems.setAdapter(adapter);

        setupSwipeToDelete();

        binding.btnCheckout.setOnClickListener(v -> {
            showLoading(true);
            viewModel.checkout();
        });

        viewModel.cartData.observe(this, response -> {
            showLoading(false);
            if (response == null || response.data == null) return;

            List<CartItemModel> items = response.data.items;
            boolean empty = items == null || items.isEmpty();

            binding.tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.rvCartItems.setVisibility(empty ? View.GONE : View.VISIBLE);
            binding.btnCheckout.setEnabled(!empty);

            if (!empty) {
                adapter.setItems(items);
                updateTotal(items);
            } else {
                binding.tvCartTotal.setText("$0.00");
            }
        });

        viewModel.checkoutResult.observe(this, response -> {
            showLoading(false);
            if (response == null || response.data == null || response.data.isEmpty()) return;

            BookingModel first    = response.data.get(0);
            boolean isPartial     = binding.rbPartial.isChecked();
            double total          = first.totalAmount;
            double amountDue      = isPartial ? total * 0.5 : total;

            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("bookingId",   first.id);
            intent.putExtra("paymentType", isPartial ? "PARTIAL" : "FULL");
            intent.putExtra("amountDue",   amountDue);
            intent.putExtra("totalAmount", total);
            if (first.room != null) intent.putExtra("roomTitle", first.room.title);
            startActivity(intent);
            finish();
        });

        viewModel.cartError.observe(this, msg -> {
            showLoading(false);
            if (msg == null) return;
            if (msg.toLowerCase().contains("unavailable") || msg.toLowerCase().contains("conflict")) {
                new AlertDialog.Builder(this)
                        .setTitle("Some items are unavailable")
                        .setMessage(msg + "\n\nRemove unavailable items before checking out.")
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });

        showLoading(true);
        viewModel.loadCart();
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                CartItemModel removed = adapter.getItemAt(pos);
                adapter.removeAt(pos);

                Snackbar snack = Snackbar.make(binding.getRoot(), "Item removed", Snackbar.LENGTH_LONG);
                snack.setAction("Undo", v -> viewModel.loadCart());
                snack.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar sb, int event) {
                        if (event != DISMISS_EVENT_ACTION && removed != null) {
                            viewModel.removeItem(removed.id);
                        }
                    }
                });
                snack.show();
            }
        }).attachToRecyclerView(binding.rvCartItems);
    }

    private void updateTotal(List<CartItemModel> items) {
        double total = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        for (CartItemModel item : items) {
            if (item.room == null || item.checkIn == null || item.checkOut == null) continue;
            try {
                long diff   = sdf.parse(item.checkOut).getTime() - sdf.parse(item.checkIn).getTime();
                int  nights = (int) (diff / (1000L * 60 * 60 * 24));
                total += item.room.pricePerNight * nights;
            } catch (Exception ignored) {}
        }
        binding.tvCartTotal.setText(String.format(Locale.getDefault(), "$%.2f", total));
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnCheckout.setEnabled(!show);
    }
}