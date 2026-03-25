package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.snackbar.Snackbar;
import lk.grandhotel.stayease.databinding.ActivityAdminRoomsBinding;
import lk.grandhotel.stayease.ui.admin.AdminRoomAdapter;
import lk.grandhotel.stayease.viewmodel.AdminRoomViewModel;

public class AdminRoomsActivity extends AppCompatActivity {

    private ActivityAdminRoomsBinding binding;
    private AdminRoomViewModel viewModel;
    private AdminRoomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminRoomsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(AdminRoomViewModel.class);

        adapter = new AdminRoomAdapter(room -> {
            Intent intent = new Intent(this, AdminRoomDetailActivity.class);
            intent.putExtra("roomId", room.id);
            startActivity(intent);
        });

        binding.rvRooms.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRooms.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.loadRooms());

        binding.fabAddRoom.setOnClickListener(v ->
                startActivity(new Intent(this, AdminCreateRoomActivity.class)));

        viewModel.rooms.observe(this, rooms -> {
            binding.swipeRefresh.setRefreshing(false);
            binding.progressBar.setVisibility(View.GONE);
            if (rooms == null || rooms.isEmpty()) {
                binding.tvEmpty.setVisibility(View.VISIBLE);
                binding.rvRooms.setVisibility(View.GONE);
            } else {
                binding.tvEmpty.setVisibility(View.GONE);
                binding.rvRooms.setVisibility(View.VISIBLE);
                adapter.setRooms(rooms);
            }
        });

        viewModel.error.observe(this, msg -> {
            binding.swipeRefresh.setRefreshing(false);
            binding.progressBar.setVisibility(View.GONE);
            if (msg != null) Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        });

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.loadRooms();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadRooms();
    }
}