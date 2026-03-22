package lk.grandhotel.stayease.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.network.models.HotelConfigResponse;
import lk.grandhotel.stayease.utils.Constants;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final LatLng HOTEL_LATLNG = new LatLng(Constants.HOTEL_LAT, Constants.HOTEL_LNG);

    private MapViewModel viewModel;
    private GoogleMap googleMap;
    private View rootView;
    private FusedLocationProviderClient fusedLocationClient;

    private String hotelPhone = null;
    private boolean mapReady = false;
    private boolean configLoaded = false;

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    enableMyLocation();
                    openNavigationToHotel();
                } else {
                    openNavigationToHotel();
                }
            });

    private boolean pendingNavigation = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        viewModel = new ViewModelProvider(this).get(MapViewModel.class);

        rootView.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        viewModel.hotelConfig.observe(getViewLifecycleOwner(), this::onConfigLoaded);
        viewModel.error.observe(getViewLifecycleOwner(), msg -> {
            rootView.findViewById(R.id.progress_bar).setVisibility(View.GONE);
            if (msg != null) {
                showBottomSheetFallback();
            }
        });

        viewModel.loadHotelConfig();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        mapReady = true;

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        placeHotelMarker();
        enableMyLocationIfGranted();

        rootView.findViewById(R.id.progress_bar).setVisibility(View.GONE);

        googleMap.setOnMarkerClickListener(marker -> {
            rootView.findViewById(R.id.bottom_sheet).setVisibility(View.VISIBLE);
            return false;
        });

        if (configLoaded) {
            setupButtons();
        }
    }

    private void onConfigLoaded(HotelConfigResponse response) {
        rootView.findViewById(R.id.progress_bar).setVisibility(View.GONE);
        configLoaded = true;

        String name = "Grand Horizon Hotels";
        String address = "";

        if (response != null && response.data != null && response.data.config != null) {
            Map<String, Object> config = response.data.config;
            String n = getStringVal(config, "name");
            String a = getStringVal(config, "address");
            hotelPhone = getStringVal(config, "phone");
            if (n != null) name = n;
            if (a != null) address = a;
        }

        showBottomSheet(name, address);
        setupButtons();
    }

    private void placeHotelMarker() {
        if (googleMap == null) return;
        googleMap.addMarker(new MarkerOptions()
                .position(HOTEL_LATLNG)
                .title("Grand Horizon Hotels")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(HOTEL_LATLNG, 15f));
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (googleMap == null) return;
        googleMap.setMyLocationEnabled(true);
    }

    private void enableMyLocationIfGranted() {
        if (hasLocationPermission()) {
            enableMyLocation();
        }
    }

    private void showBottomSheet(String name, String address) {
        if (rootView == null) return;
        View sheet = rootView.findViewById(R.id.bottom_sheet);
        sheet.setVisibility(View.VISIBLE);
        ((TextView) rootView.findViewById(R.id.tv_hotel_name)).setText(name);
        ((TextView) rootView.findViewById(R.id.tv_hotel_address)).setText(address);
    }

    private void showBottomSheetFallback() {
        showBottomSheet("Grand Horizon Hotels", "Colombo, Sri Lanka");
        setupButtons();
    }

    private void setupButtons() {
        if (rootView == null) return;

        rootView.findViewById(R.id.fab_directions).setOnClickListener(v -> handleDirectionsClick());
        rootView.findViewById(R.id.btn_directions).setOnClickListener(v -> handleDirectionsClick());

        rootView.findViewById(R.id.btn_call).setOnClickListener(v -> {
            String phone = hotelPhone != null && !hotelPhone.isEmpty()
                    ? hotelPhone
                    : getString(R.string.hotel_phone);
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
        });
    }

    private void handleDirectionsClick() {
        if (hasLocationPermission()) {
            openNavigationToHotel();
        } else {
            pendingNavigation = true;
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    private void openNavigationToHotel() {
        if (hasLocationPermission()) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                launchGoogleMapsNavigation();
            }).addOnFailureListener(e -> launchGoogleMapsNavigation());
        } else {
            launchGoogleMapsNavigation();
        }
    }

    private void launchGoogleMapsNavigation() {
        Uri gmmIntentUri = Uri.parse(
                "google.navigation:q=" + Constants.HOTEL_LAT + "," + Constants.HOTEL_LNG + "&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Uri fallback = Uri.parse(
                    "https://maps.google.com/maps?daddr=" + Constants.HOTEL_LAT + "," + Constants.HOTEL_LNG);
            startActivity(new Intent(Intent.ACTION_VIEW, fallback));
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private String getStringVal(Map<String, Object> config, String key) {
        Object val = config.get(key);
        return val != null ? val.toString() : null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
        googleMap = null;
    }
}