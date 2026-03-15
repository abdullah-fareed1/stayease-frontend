package lk.grandhotel.stayease.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.databinding.ActivityMainBinding;
import lk.grandhotel.stayease.ui.bookings.BookingsFragment;
import lk.grandhotel.stayease.ui.home.HomeFragment;
import lk.grandhotel.stayease.ui.map.MapFragment;
import lk.grandhotel.stayease.ui.profile.ProfileFragment;
import lk.grandhotel.stayease.ui.search.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            binding.bottomNav.setSelectedItemId(R.id.nav_home);
        }

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) { loadFragment(new HomeFragment()); return true; }
            if (id == R.id.nav_search) { loadFragment(new SearchFragment()); return true; }
            if (id == R.id.nav_bookings) { loadFragment(new BookingsFragment()); return true; }
            if (id == R.id.nav_map) { loadFragment(new MapFragment()); return true; }
            if (id == R.id.nav_profile) { loadFragment(new ProfileFragment()); return true; }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void navigateToTab(int navItemId) {
        binding.bottomNav.setSelectedItemId(navItemId);
    }
}