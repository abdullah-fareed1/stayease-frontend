package lk.grandhotel.stayease.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.activities.RoomDetailActivity;
import lk.grandhotel.stayease.databinding.FragmentSearchBinding;
import lk.grandhotel.stayease.network.models.RoomModel;
import lk.grandhotel.stayease.ui.home.RoomAdapter;
import lk.grandhotel.stayease.utils.NetworkUtils;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private SearchViewModel       viewModel;
    private RoomAdapter           adapter;
    private List<RoomModel>       allRooms = new ArrayList<>();

    private String  activeCategory  = null;
    private boolean activeAvailable = true;

    private Snackbar offlineSnackbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        adapter = new RoomAdapter((room, sharedImageView) -> {
            Intent intent = new Intent(requireContext(), RoomDetailActivity.class);
            intent.putExtra("roomId", room.id);
            androidx.core.app.ActivityOptionsCompat options =
                    androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation(
                            requireActivity(),
                            sharedImageView,
                            "room_image_" + room.id);
            startActivity(intent, options.toBundle());
        });

        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSearchResults.setAdapter(adapter);

        viewModel.results.observe(getViewLifecycleOwner(), rooms -> {
            binding.progressBar.setVisibility(View.GONE);
            allRooms = rooms != null ? rooms : new ArrayList<>();
            applyLocalFilter();
        });

        viewModel.error.observe(getViewLifecycleOwner(), msg -> {
            binding.progressBar.setVisibility(View.GONE);
            if (msg != null) Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        });

        viewModel.history.observe(getViewLifecycleOwner(), this::renderHistoryChips);

        NetworkUtils.getIsOnlineLiveData().observe(getViewLifecycleOwner(), online -> {
            if (binding == null) return;
            if (Boolean.FALSE.equals(online)) {
                offlineSnackbar = Snackbar.make(
                        binding.getRoot(),
                        "No internet connection — showing cached data",
                        Snackbar.LENGTH_INDEFINITE);
                offlineSnackbar.show();
            } else {
                if (offlineSnackbar != null && offlineSnackbar.isShown()) {
                    offlineSnackbar.dismiss();
                }
            }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String q = s.toString().trim();
                binding.scrollHistory.setVisibility(q.isEmpty() ? View.VISIBLE : View.GONE);
                applyLocalFilter();
            }
        });

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String q = binding.etSearch.getText() != null
                        ? binding.etSearch.getText().toString().trim() : "";
                if (!q.isEmpty()) viewModel.saveQuery(q);
                return true;
            }
            return false;
        });

        binding.btnFilter.setOnClickListener(v -> showFilterSheet());

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.search(null, true);
        viewModel.loadHistory();
    }

    private void applyLocalFilter() {
        String query = binding.etSearch.getText() != null
                ? binding.etSearch.getText().toString().trim().toLowerCase(Locale.getDefault()) : "";

        List<RoomModel> filtered = allRooms.stream().filter(r -> {
            if (!query.isEmpty()) {
                boolean titleMatch    = r.title    != null && r.title.toLowerCase(Locale.getDefault()).contains(query);
                boolean categoryMatch = r.category != null && r.category.toLowerCase(Locale.getDefault()).contains(query);
                boolean amenityMatch  = r.amenities != null && r.amenities.stream()
                        .anyMatch(a -> a.toLowerCase(Locale.getDefault()).contains(query));
                if (!titleMatch && !categoryMatch && !amenityMatch) return false;
            }
            return true;
        }).collect(Collectors.toList());

        adapter.submitList(filtered);
        binding.tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        binding.rvSearchResults.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void renderHistoryChips(List<String> queries) {
        binding.llHistoryChips.removeAllViews();
        if (queries == null || queries.isEmpty()) {
            binding.scrollHistory.setVisibility(View.GONE);
            return;
        }
        binding.scrollHistory.setVisibility(View.VISIBLE);
        for (String q : queries) {
            Chip chip = new Chip(requireContext());
            chip.setText(q);
            chip.setCloseIconVisible(false);
            chip.setOnClickListener(v -> {
                binding.etSearch.setText(q);
                binding.etSearch.setSelection(q.length());
            });
            binding.llHistoryChips.addView(chip);
        }
        Chip clearChip = new Chip(requireContext());
        clearChip.setText("Clear history");
        clearChip.setCloseIconVisible(false);
        clearChip.setChipBackgroundColorResource(android.R.color.transparent);
        clearChip.setOnClickListener(v -> viewModel.clearHistory());
        binding.llHistoryChips.addView(clearChip);
    }

    private void showFilterSheet() {
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_filter, null);
        sheet.setContentView(sheetView);

        ChipGroup chipGroup   = sheetView.findViewById(R.id.chip_group_filter_category);
        SwitchMaterial swAvail = sheetView.findViewById(R.id.switch_available);
        swAvail.setChecked(activeAvailable);

        if      ("STANDARD".equals(activeCategory)) chipGroup.check(R.id.filter_standard);
        else if ("DELUXE"  .equals(activeCategory)) chipGroup.check(R.id.filter_deluxe);
        else if ("SUITE"   .equals(activeCategory)) chipGroup.check(R.id.filter_suite);
        else if ("FAMILY"  .equals(activeCategory)) chipGroup.check(R.id.filter_family);
        else                                         chipGroup.check(R.id.filter_all);

        sheetView.findViewById(R.id.btn_reset).setOnClickListener(v -> {
            chipGroup.check(R.id.filter_all);
            swAvail.setChecked(true);
        });

        sheetView.findViewById(R.id.btn_apply_filters).setOnClickListener(v -> {
            int checkedId = chipGroup.getCheckedChipId();
            if      (checkedId == R.id.filter_standard) activeCategory = "STANDARD";
            else if (checkedId == R.id.filter_deluxe)   activeCategory = "DELUXE";
            else if (checkedId == R.id.filter_suite)     activeCategory = "SUITE";
            else if (checkedId == R.id.filter_family)    activeCategory = "FAMILY";
            else                                          activeCategory = null;

            activeAvailable = swAvail.isChecked();
            binding.progressBar.setVisibility(View.VISIBLE);
            viewModel.search(activeCategory, activeAvailable);
            sheet.dismiss();
        });

        sheet.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (offlineSnackbar != null) offlineSnackbar.dismiss();
    }
}