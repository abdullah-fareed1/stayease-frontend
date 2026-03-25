package lk.grandhotel.stayease.activities;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;
import lk.grandhotel.stayease.databinding.ActivityAdminCreateRoomBinding;
import lk.grandhotel.stayease.viewmodel.AdminRoomViewModel;

public class AdminCreateRoomActivity extends AppCompatActivity {

    private ActivityAdminCreateRoomBinding binding;
    private AdminRoomViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminCreateRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(AdminRoomViewModel.class);

        binding.btnAddAmenity.setOnClickListener(v -> addAmenityChip());

        binding.btnCreate.setOnClickListener(v -> attemptCreate());

        viewModel.roomResult.observe(this, room -> {
            showLoading(false);
            if (room != null) {
                Snackbar.make(binding.getRoot(), "Room created successfully.", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.error.observe(this, msg -> {
            showLoading(false);
            if (msg != null) Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        });
    }

    private void addAmenityChip() {
        String text = binding.etAmenity.getText() != null
                ? binding.etAmenity.getText().toString().trim() : "";
        if (text.isEmpty()) return;
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> binding.chipGroupAmenities.removeView(chip));
        binding.chipGroupAmenities.addView(chip);
        binding.etAmenity.setText("");
    }

    private void attemptCreate() {
        String title = getText(binding.etTitle);
        String description = getText(binding.etDescription);
        String priceStr = getText(binding.etPrice);
        String maxGuestsStr = getText(binding.etMaxGuests);

        binding.tilTitle.setError(null);
        binding.tilDescription.setError(null);
        binding.tilPrice.setError(null);
        binding.tilMaxGuests.setError(null);

        if (title.length() < 3) { binding.tilTitle.setError("Min 3 characters."); return; }
        if (description.length() < 20) { binding.tilDescription.setError("Min 20 characters."); return; }

        double price;
        try { price = Double.parseDouble(priceStr); if (price <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { binding.tilPrice.setError("Enter a valid price."); return; }

        int maxGuests;
        try { maxGuests = Integer.parseInt(maxGuestsStr); if (maxGuests < 1 || maxGuests > 20) throw new NumberFormatException(); }
        catch (NumberFormatException e) { binding.tilMaxGuests.setError("Enter 1–20."); return; }

        int checkedId = binding.rgCategory.getCheckedRadioButtonId();
        if (checkedId == -1) { Snackbar.make(binding.getRoot(), "Select a category.", Snackbar.LENGTH_SHORT).show(); return; }

        String category;
        if (checkedId == lk.grandhotel.stayease.R.id.rb_standard) category = "STANDARD";
        else if (checkedId == lk.grandhotel.stayease.R.id.rb_deluxe) category = "DELUXE";
        else if (checkedId == lk.grandhotel.stayease.R.id.rb_suite) category = "SUITE";
        else category = "FAMILY";

        List<String> amenities = new ArrayList<>();
        for (int i = 0; i < binding.chipGroupAmenities.getChildCount(); i++) {
            View child = binding.chipGroupAmenities.getChildAt(i);
            if (child instanceof Chip) amenities.add(((Chip) child).getText().toString());
        }

        showLoading(true);
        viewModel.createRoom(title, category, description, price, maxGuests, amenities);
    }

    private String getText(com.google.android.material.textfield.TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnCreate.setEnabled(!show);
    }
}