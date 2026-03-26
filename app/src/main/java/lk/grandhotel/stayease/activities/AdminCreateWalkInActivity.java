package lk.grandhotel.stayease.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.databinding.ActivityAdminCreateWalkInBinding;
import lk.grandhotel.stayease.network.models.AdminRoomModel;
import lk.grandhotel.stayease.viewmodel.AdminBookingViewModel;
import lk.grandhotel.stayease.viewmodel.AdminRoomViewModel;

public class AdminCreateWalkInActivity extends AppCompatActivity {

    private ActivityAdminCreateWalkInBinding binding;
    private AdminBookingViewModel bookingViewModel;
    private AdminRoomViewModel roomViewModel;

    private List<AdminRoomModel> availableRooms = new ArrayList<>();
    private String selectedRoomId;
    private long checkInMs = 0;
    private long checkOutMs = 0;
    private int guestCount = 1;
    private String paymentType = "FULL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminCreateWalkInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        bookingViewModel = new ViewModelProvider(this).get(AdminBookingViewModel.class);
        roomViewModel = new ViewModelProvider(this).get(AdminRoomViewModel.class);

        setupObservers();
        setupGuestCounter();
        setupDatePicker();
        setupPaymentType();

        binding.btnSubmit.setOnClickListener(v -> attemptCreateWalkIn());

        loadAvailableRooms();
    }

    private void setupObservers() {
        roomViewModel.rooms.observe(this, rooms -> {
            hideLoading();
            if (rooms != null) {
                availableRooms = new ArrayList<>();
                for (AdminRoomModel room : rooms) {
                    if ("AVAILABLE".equals(room.availabilityStatus)) {
                        availableRooms.add(room);
                    }
                }
                setupRoomSpinner();
            }
        });

        roomViewModel.error.observe(this, msg -> {
            hideLoading();
            if (msg != null) Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        });

        bookingViewModel.bookingResult.observe(this, booking -> {
            hideLoading();
            if (booking != null) {
                Snackbar.make(binding.getRoot(), "Walk-in booking created successfully.", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        });

        bookingViewModel.error.observe(this, msg -> {
            hideLoading();
            if (msg != null) Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        });
    }

    private void loadAvailableRooms() {
        showLoading(true);
        roomViewModel.loadRooms();
    }

    private void setupRoomSpinner() {
        List<String> roomNames = new ArrayList<>();
        for (AdminRoomModel room : availableRooms) {
            roomNames.add(room.title + " - $" + room.getPriceDouble() + "/night");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRoom.setAdapter(adapter);

        binding.spinnerRoom.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedRoomId = availableRooms.get(position).id;
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedRoomId = null;
            }
        });
    }

    private void setupGuestCounter() {
        updateGuestDisplay();
        binding.btnGuestMinus.setOnClickListener(v -> {
            if (guestCount > 1) {
                guestCount--;
                updateGuestDisplay();
            }
        });
        binding.btnGuestPlus.setOnClickListener(v -> {
            AdminRoomModel selectedRoom = getSelectedRoom();
            if (selectedRoom != null && guestCount < selectedRoom.maxGuests) {
                guestCount++;
                updateGuestDisplay();
            } else {
                Snackbar.make(binding.getRoot(), "Maximum guests for this room is " + (selectedRoom != null ? selectedRoom.maxGuests : 1), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDatePicker() {
        binding.btnSelectDates.setOnClickListener(v -> {
            MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select Check-in and Check-out Dates")
                    .setCalendarConstraints(new CalendarConstraints.Builder()
                            .setValidator(DateValidatorPointForward.now())
                            .build())
                    .build();

            picker.addOnPositiveButtonClickListener(selection -> {
                checkInMs = selection.first;
                checkOutMs = selection.second;
                updateDateDisplay();
            });

            picker.show(getSupportFragmentManager(), "date_picker");
        });
    }

    private void setupPaymentType() {
        binding.rgPaymentType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_partial) {
                paymentType = "PARTIAL";
            } else if (checkedId == R.id.rb_full) {
                paymentType = "FULL";
            }
        });
    }

    private void updateGuestDisplay() {
        binding.tvGuestCount.setText(String.valueOf(guestCount));
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String checkInStr = checkInMs > 0 ? sdf.format(checkInMs) : "Select dates";
        String checkOutStr = checkOutMs > 0 ? sdf.format(checkOutMs) : "";
        binding.btnSelectDates.setText(checkInStr + (checkOutStr.isEmpty() ? "" : " - " + checkOutStr));
    }

    private AdminRoomModel getSelectedRoom() {
        if (selectedRoomId == null || availableRooms.isEmpty()) return null;
        for (AdminRoomModel room : availableRooms) {
            if (selectedRoomId.equals(room.id)) return room;
        }
        return null;
    }

    private void attemptCreateWalkIn() {
        if (!validateInputs()) return;

        String guestName = binding.etGuestName.getText().toString().trim();
        String guestEmail = binding.etGuestEmail.getText().toString().trim();
        String guestPhone = binding.etGuestPhone.getText().toString().trim();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String checkIn = sdf.format(checkInMs);
        String checkOut = sdf.format(checkOutMs);

        showLoading(true);
        bookingViewModel.createWalkIn(selectedRoomId, checkIn, checkOut, guestCount,
                                     paymentType, guestName, guestEmail, guestPhone);
    }

    private boolean validateInputs() {
        if (selectedRoomId == null) {
            Snackbar.make(binding.getRoot(), "Please select a room.", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (checkInMs == 0 || checkOutMs == 0) {
            Snackbar.make(binding.getRoot(), "Please select check-in and check-out dates.", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        String name = binding.etGuestName.getText().toString().trim();
        if (name.length() < 2) {
            binding.tilGuestName.setError("Name must be at least 2 characters");
            return false;
        } else {
            binding.tilGuestName.setError(null);
        }

        String email = binding.etGuestEmail.getText().toString().trim();
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilGuestEmail.setError("Please enter a valid email");
            return false;
        } else {
            binding.tilGuestEmail.setError(null);
        }

        String phone = binding.etGuestPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            binding.tilGuestPhone.setError("Phone number is required");
            return false;
        } else {
            binding.tilGuestPhone.setError(null);
        }

        return true;
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void hideLoading() {
        showLoading(false);
    }
}