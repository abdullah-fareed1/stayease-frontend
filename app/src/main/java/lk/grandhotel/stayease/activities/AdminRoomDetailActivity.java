package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.databinding.ActivityAdminRoomDetailBinding;
import lk.grandhotel.stayease.network.models.AdminRoomModel;
import lk.grandhotel.stayease.network.models.RoomModel;
import lk.grandhotel.stayease.ui.room.RoomDetailViewModel;
import lk.grandhotel.stayease.viewmodel.AdminRoomViewModel;

public class AdminRoomDetailActivity extends AppCompatActivity {

    private ActivityAdminRoomDetailBinding binding;
    private AdminRoomViewModel adminViewModel;
    private RoomDetailViewModel roomDetailViewModel;
    private String roomId;
    private AdminRoomModel currentRoom;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) confirmAndUpload(uri);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminRoomDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        roomId = getIntent().getStringExtra("roomId");

        adminViewModel = new ViewModelProvider(this).get(AdminRoomViewModel.class);
        roomDetailViewModel = new ViewModelProvider(this).get(RoomDetailViewModel.class);

        setupObservers();

        binding.btnEditRoom.setOnClickListener(v -> { if (currentRoom != null) showEditBottomSheet(); });
        binding.btnAvailability.setOnClickListener(v -> { if (currentRoom != null) showAvailabilityDialog(); });
        binding.btnAddImage.setOnClickListener(v -> imagePicker.launch("image/*"));

        showLoading(true);
        roomDetailViewModel.loadRoom(roomId);
    }

    private void setupObservers() {
        roomDetailViewModel.room.observe(this, room -> {
            hideLoading();
            if (room != null) {
                currentRoom = mapToAdminModel(room);
                populateDetail(currentRoom);
            }
        });

        roomDetailViewModel.error.observe(this, msg -> {
            hideLoading();
            if (msg != null) Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        });

        adminViewModel.roomResult.observe(this, room -> {
            hideLoading();
            if (room != null) {
                currentRoom = room;
                populateDetail(room);
                Snackbar.make(binding.getRoot(), "Room updated.", Snackbar.LENGTH_SHORT).show();
            }
        });

        adminViewModel.actionSuccess.observe(this, success -> {
            hideLoading();
            if (Boolean.TRUE.equals(success)) {
                Snackbar.make(binding.getRoot(), "Done.", Snackbar.LENGTH_SHORT).show();
                showLoading(true);
                roomDetailViewModel.loadRoom(roomId);
            }
        });

        adminViewModel.error.observe(this, msg -> {
            hideLoading();
            if (msg != null) Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        });
    }

    private void populateDetail(AdminRoomModel room) {
        binding.tvRoomTitle.setText(room.title != null ? room.title : "");
        binding.tvCategory.setText(room.category != null ? room.category : "");
        binding.tvPrice.setText(String.format(Locale.getDefault(), "$%.2f / night", room.getPriceDouble()));
        binding.tvMaxGuests.setText(room.maxGuests + " guests max");
        binding.tvDescription.setText(room.description != null ? room.description : "");
        binding.tvAvailabilityStatus.setText(
                room.availabilityStatus != null ? room.availabilityStatus.replace("_", " ") : "");

        binding.chipGroupAmenities.removeAllViews();
        if (room.amenities != null) {
            for (String amenity : room.amenities) {
                Chip chip = new Chip(this);
                chip.setText(amenity);
                chip.setCheckable(false);
                chip.setClickable(false);
                binding.chipGroupAmenities.addView(chip);
            }
        }

        renderImages(room.images);
    }

    private void renderImages(List<RoomModel.ImageModel> images) {
        binding.layoutImages.removeAllViews();
        if (images == null || images.isEmpty()) return;
        for (RoomModel.ImageModel img : images) {
            View itemView = getLayoutInflater().inflate(R.layout.item_admin_image, binding.layoutImages, false);
            ImageView iv = itemView.findViewById(R.id.iv_admin_image);
            Glide.with(this).load(img.url).centerCrop().into(iv);
            itemView.findViewById(R.id.btn_delete_image).setOnClickListener(v ->
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Delete Image")
                            .setMessage("Remove this image from the room?")
                            .setPositiveButton("Delete", (d, w) -> {
                                showLoading(true);
                                adminViewModel.deleteImage(roomId, img.id);
                            })
                            .setNegativeButton("Cancel", null)
                            .show());
            binding.layoutImages.addView(itemView);
        }
    }

    private void showEditBottomSheet() {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_room, null);
        sheet.setContentView(view);

        TextInputEditText etTitle = view.findViewById(R.id.et_edit_title);
        TextInputEditText etDescription = view.findViewById(R.id.et_edit_description);
        TextInputEditText etPrice = view.findViewById(R.id.et_edit_price);
        TextInputEditText etMaxGuests = view.findViewById(R.id.et_edit_max_guests);

        etTitle.setText(currentRoom.title);
        etDescription.setText(currentRoom.description);
        etPrice.setText(String.valueOf(currentRoom.getPriceDouble()));
        etMaxGuests.setText(String.valueOf(currentRoom.maxGuests));

        view.findViewById(R.id.btn_save_edit).setOnClickListener(v -> {
            Map<String, Object> fields = new HashMap<>();
            String title = getText(etTitle);
            String desc = getText(etDescription);
            String priceStr = getText(etPrice);
            String guestsStr = getText(etMaxGuests);

            if (!title.isEmpty()) fields.put("title", title);
            if (!desc.isEmpty()) fields.put("description", desc);
            if (!priceStr.isEmpty()) {
                try { fields.put("pricePerNight", Double.parseDouble(priceStr)); }
                catch (NumberFormatException ignored) {}
            }
            if (!guestsStr.isEmpty()) {
                try { fields.put("maxGuests", Integer.parseInt(guestsStr)); }
                catch (NumberFormatException ignored) {}
            }
            if (fields.isEmpty()) { sheet.dismiss(); return; }
            sheet.dismiss();
            showLoading(true);
            adminViewModel.updateRoom(roomId, fields);
        });

        view.findViewById(R.id.btn_cancel_edit).setOnClickListener(v -> sheet.dismiss());
        sheet.show();
    }

    private void showAvailabilityDialog() {
        String[] options = {"Available", "Temporarily Unavailable", "Permanently Hidden"};
        String[] values = {"AVAILABLE", "TEMP_UNAVAILABLE", "PERMANENTLY_UNAVAILABLE"};

        new MaterialAlertDialogBuilder(this)
                .setTitle("Set Room Availability")
                .setItems(options, (dialog, which) ->
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Confirm")
                                .setMessage("Change status to: " + options[which] + "?")
                                .setPositiveButton("Confirm", (d, w) -> {
                                    showLoading(true);
                                    adminViewModel.setAvailability(roomId, values[which]);
                                })
                                .setNegativeButton("Cancel", null)
                                .show())
                .show();
    }

    private void confirmAndUpload(Uri uri) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Upload Image")
                .setMessage("Set as primary image?")
                .setPositiveButton("Yes, set primary", (d, w) -> {
                    showLoading(true);
                    adminViewModel.uploadImage(roomId, uri, true);
                })
                .setNegativeButton("No, just add", (d, w) -> {
                    showLoading(true);
                    adminViewModel.uploadImage(roomId, uri, false);
                })
                .show();
    }

    private AdminRoomModel mapToAdminModel(lk.grandhotel.stayease.network.models.RoomModel room) {
        AdminRoomModel m = new AdminRoomModel();
        m.id = room.id;
        m.title = room.title;
        m.description = room.description;
        m.category = room.category;
        m.pricePerNight = String.valueOf(room.pricePerNight);
        m.maxGuests = room.maxGuests;
        m.amenities = room.amenities;
        m.availabilityStatus = room.availabilityStatus;
        m.images = room.images;
        m.primaryImage = room.primaryImage;
        m.averageRating = room.averageRating;
        m.reviewCount = room.reviewCount;
        return m;
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.layoutContent.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
        binding.layoutContent.setVisibility(View.VISIBLE);
    }
}