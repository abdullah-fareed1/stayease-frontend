package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;

import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.databinding.ActivityCartBinding;
import lk.grandhotel.stayease.network.models.CartItemModel;
import lk.grandhotel.stayease.network.models.CartResponse;
import lk.grandhotel.stayease.network.models.CheckoutResponse;
import lk.grandhotel.stayease.ui.adapters.CartItemAdapter;
import lk.grandhotel.stayease.viewmodel.CartViewModel;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;
    private CartViewModel viewModel;
    private CartItemAdapter adapter;
    private String pendingPaymentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        adapter = new CartItemAdapter();

        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCartItems.setAdapter(adapter);

        attachSwipeToDelete();
        setupObservers();

        binding.btnCheckout.setOnClickListener(v -> showCheckoutDialog());

        viewModel.loadCart();
    }

    private void attachSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
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
                updateTotal();

                Snackbar.make(binding.getRoot(), "Item removed", Snackbar.LENGTH_LONG)
                        .setAnchorView(binding.layoutTotal)
                        .setAction("Undo", v -> {
                            adapter.restoreAt(pos, removed);
                            updateTotal();
                        })
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar sb, int event) {
                                if (event != DISMISS_EVENT_ACTION) {
                                    viewModel.removeItem(removed.id);
                                }
                            }
                        })
                        .show();
            }
        }).attachToRecyclerView(binding.rvCartItems);
    }

    private void setupObservers() {
        viewModel.cartResult.observe(this, response -> {
            if (response != null && response.data != null && response.data.cart != null) {
                renderCart(response.data.cart);
            }
        });

        viewModel.checkoutResult.observe(this, response -> {
            if (response != null && response.status && response.data != null
                    && response.data.bookings != null && !response.data.bookings.isEmpty()) {
                CheckoutResponse.CheckoutBooking first = response.data.bookings.get(0);
                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra("bookingId", first.bookingId);
                intent.putExtra("totalAmount", first.totalAmount);
                intent.putExtra("paymentAmount", first.paymentAmount);
                intent.putExtra("paymentType", pendingPaymentType != null ? pendingPaymentType : "PARTIAL");
                intent.putExtra("roomTitle", response.data.bookings.size() > 1
                        ? response.data.bookings.size() + " rooms" : "");
                startActivity(intent);
            }
        });

        viewModel.error.observe(this, msg -> {
            if (msg != null) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG)
                        .setAnchorView(binding.layoutTotal)
                        .show();
            }
        });

        viewModel.loading.observe(this, loading ->
                binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE));
    }

    private void renderCart(CartResponse.CartDetail cart) {
        List<CartItemModel> items = cart.items;
        if (items == null || items.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.rvCartItems.setVisibility(View.GONE);
            binding.layoutTotal.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.rvCartItems.setVisibility(View.VISIBLE);
            binding.layoutTotal.setVisibility(View.VISIBLE);
            adapter.setItems(items);
            binding.tvCartTotal.setText(String.format(Locale.getDefault(), "$%.2f", cart.cartTotal));
        }
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            total += adapter.getItemAt(i).subtotal;
        }
        binding.tvCartTotal.setText(String.format(Locale.getDefault(), "$%.2f", total));
        if (adapter.getItemCount() == 0) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.rvCartItems.setVisibility(View.GONE);
            binding.layoutTotal.setVisibility(View.GONE);
        }
    }

    private void showCheckoutDialog() {
        if (adapter.getItemCount() == 0) {
            Snackbar.make(binding.getRoot(), "Your cart is empty.", Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.layoutTotal)
                    .show();
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle("Payment Option")
                .setItems(new String[]{"Pay 50% now (Partial)", "Pay full amount"}, (dialog, which) -> {
                    pendingPaymentType = which == 0 ? "PARTIAL" : "FULL";
                    viewModel.checkout(pendingPaymentType);
                })
                .show();
    }
}