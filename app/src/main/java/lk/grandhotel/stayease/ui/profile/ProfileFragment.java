package lk.grandhotel.stayease.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashMap;
import java.util.Map;

import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.activities.LoginActivity;
import lk.grandhotel.stayease.activities.MainActivity;
import lk.grandhotel.stayease.databinding.FragmentProfileBinding;
import lk.grandhotel.stayease.network.ApiClient;
import lk.grandhotel.stayease.network.models.ApiResponse;
import lk.grandhotel.stayease.utils.AppPrefs;
import lk.grandhotel.stayease.utils.TokenPrefs;
import lk.grandhotel.stayease.utils.UserPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        populateUserInfo();
        setupDarkModeToggle();
        setupClickListeners();
        setupObservers();

        viewModel.loadBookingCount();
        viewModel.loadHotelConfig();
    }

    private void populateUserInfo() {
        String name = UserPrefs.getUserName(requireContext());
        String email = UserPrefs.getUserEmail(requireContext());
        String phone = UserPrefs.getUserPhone(requireContext());

        if (name == null || name.isEmpty()) name = "Guest";

        binding.tvUserName.setText(name);
        binding.tvUserEmail.setText(email != null ? email : "");
        binding.tvUserPhone.setText((phone != null && !phone.isEmpty()) ? phone : "");
        binding.tvAvatar.setText(String.valueOf(name.charAt(0)).toUpperCase());
        binding.tvMemberSince.setText("Active");
    }

    private void setupDarkModeToggle() {
        // Read saved state — don't trigger the listener here
        boolean isDark = AppPrefs.isDarkMode(requireContext());
        binding.switchDarkMode.setOnCheckedChangeListener(null);
        binding.switchDarkMode.setChecked(isDark);

        binding.rowDarkMode.setOnClickListener(v -> {
            boolean newVal = !binding.switchDarkMode.isChecked();
            binding.switchDarkMode.setChecked(newVal);
            AppPrefs.setDarkMode(requireContext(), newVal);

            // MODE_NIGHT_NO keeps it light; MODE_NIGHT_YES forces dark
            AppCompatDelegate.setDefaultNightMode(
                    newVal
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
            // Activity recreates automatically — this is expected
        });
    }

    private void setupClickListeners() {
        binding.rowMyBookings.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTab(R.id.nav_bookings);
            }
        });

        binding.rowCallHotel.setOnClickListener(v -> callHotel());

        binding.btnSignOut.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton("Sign Out", (dialog, which) -> performLogout())
                        .setNegativeButton("Cancel", null)
                        .show());
    }

    private void setupObservers() {
        viewModel.bookingCount.observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                binding.tvBookingCount.setText(String.valueOf(count));
            }
        });
    }

    private void callHotel() {
        String phone = null;

        if (viewModel.hotelConfig.getValue() != null
                && viewModel.hotelConfig.getValue().data != null
                && viewModel.hotelConfig.getValue().data.config != null) {
            Object val = viewModel.hotelConfig.getValue().data.config.get("phone");
            if (val != null) phone = val.toString();
        }

        if (phone == null || phone.isEmpty()) {
            phone = getString(R.string.hotel_phone);
        }

        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
    }

    private void performLogout() {
        Map<String, String> emptyBody = new HashMap<>();
        ApiClient.getService(requireContext()).logout(emptyBody)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {}
                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {}
                });

        TokenPrefs.clearTokens(requireContext());
        UserPrefs.clear(requireContext());

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) getActivity().finishAffinity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}