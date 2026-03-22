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
            int navigateTo = getIntent().getIntExtra("navigate_to", R.id.nav_home);
            loadFragment(fragmentForId(navigateTo));
            binding.bottomNav.setSelectedItemId(navigateTo);
        }

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home)     { loadFragment(new HomeFragment());     return true; }
            if (id == R.id.nav_search)   { loadFragment(new SearchFragment());   return true; }
            if (id == R.id.nav_bookings) { loadFragment(new BookingsFragment()); return true; }
            if (id == R.id.nav_map)      { loadFragment(new MapFragment());      return true; }
            if (id == R.id.nav_profile)  { loadFragment(new ProfileFragment());  return true; }
            return false;
        });
    }

    private Fragment fragmentForId(int id) {
        if (id == R.id.nav_search)   return new SearchFragment();
        if (id == R.id.nav_bookings) return new BookingsFragment();
        if (id == R.id.nav_map)      return new MapFragment();
        if (id == R.id.nav_profile)  return new ProfileFragment();
        return new HomeFragment();
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