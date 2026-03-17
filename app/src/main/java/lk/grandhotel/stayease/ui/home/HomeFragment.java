package lk.grandhotel.stayease.ui.home;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.List;

import lk.grandhotel.stayease.activities.RoomDetailActivity;
import lk.grandhotel.stayease.databinding.FragmentHomeBinding;
import lk.grandhotel.stayease.network.models.RoomModel;
import lk.grandhotel.stayease.utils.ShakeDetector;
import lk.grandhotel.stayease.utils.UserPrefs;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding   binding;
    private HomeViewModel         viewModel;
    private RoomAdapter           roomAdapter;
    private HomeHeaderAdapter     headerAdapter;
    private SensorManager         sensorManager;
    private ShakeDetector         shakeDetector;
    private String                selectedCategory = null;
    private boolean refreshedByShake = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        roomAdapter = new RoomAdapter(room -> {
            Intent intent = new Intent(requireContext(), RoomDetailActivity.class);
            intent.putExtra("roomId", room.id);
            startActivity(intent);
        });

        headerAdapter = new HomeHeaderAdapter(category -> {
            selectedCategory = category;
            viewModel.loadRooms(category);
        });

        String name = UserPrefs.getUserName(requireContext());
        headerAdapter.setUserName(name);
        headerAdapter.setGreeting(getGreeting());

        ConcatAdapter concatAdapter = new ConcatAdapter(headerAdapter, roomAdapter);
        binding.rvRooms.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRooms.setAdapter(concatAdapter);

        binding.swipeRefresh.setColorSchemeResources(lk.grandhotel.stayease.R.color.primary);
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.refresh());

        viewModel.rooms.observe(getViewLifecycleOwner(), this::onRoomsLoaded);
        viewModel.error.observe(getViewLifecycleOwner(), this::onError);
        viewModel.loading.observe(getViewLifecycleOwner(), loading -> {
            if (!loading) binding.swipeRefresh.setRefreshing(false);
        });

        setupShakeDetector();
        viewModel.loadRooms(null);
    }

    private void onRoomsLoaded(List<RoomModel> rooms) {
        binding.swipeRefresh.setRefreshing(false);
        if (rooms != null) {
            roomAdapter.submitList(rooms);
            headerAdapter.setRoomCount(rooms.size());
        }
    }

    private void onError(String msg) {
        binding.swipeRefresh.setRefreshing(false);
        if (msg != null && getView() != null) {
            Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        }
    }

    private void setupShakeDetector() {
        sensorManager = (SensorManager) requireContext().getSystemService(android.content.Context.SENSOR_SERVICE);
        shakeDetector = new ShakeDetector(() -> {
            if (getView() != null) {
                refreshedByShake = true;
                Snackbar.make(binding.getRoot(), "Shake detected! Refreshing rooms...", Snackbar.LENGTH_SHORT).show();
                viewModel.refresh();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accel != null) {
            sensorManager.registerListener(shakeDetector, accel, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(shakeDetector);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private String getGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) return "Good morning,";
        if (hour < 17) return "Good afternoon,";
        return "Good evening,";
    }
}