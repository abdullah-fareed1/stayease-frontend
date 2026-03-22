package lk.grandhotel.stayease.ui.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import lk.grandhotel.stayease.activities.BookingDetailActivity;
import lk.grandhotel.stayease.databinding.FragmentBookingTabBinding;
import lk.grandhotel.stayease.network.models.BookingModel;
import lk.grandhotel.stayease.viewmodel.BookingsViewModel;

public class BookingTabFragment extends Fragment {

    private static final String ARG_FILTER = "filter";
    private static final String FILTER_UPCOMING  = "UPCOMING";
    private static final String FILTER_PAST      = "PAST";
    private static final String FILTER_CANCELLED = "CANCELLED";

    private FragmentBookingTabBinding binding;
    private BookingsViewModel viewModel;
    private BookingAdapter adapter;
    private String filter;

    public static BookingTabFragment newInstance(String filter) {
        BookingTabFragment f = new BookingTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILTER, filter);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingTabBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        filter = getArguments() != null ? getArguments().getString(ARG_FILTER, FILTER_UPCOMING) : FILTER_UPCOMING;

        viewModel = new ViewModelProvider(requireParentFragment()).get(BookingsViewModel.class);

        adapter = new BookingAdapter(booking -> {
            Intent intent = new Intent(requireContext(), BookingDetailActivity.class);
            intent.putExtra("bookingId", booking.id);
            startActivity(intent);
        });

        binding.rvBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBookings.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.loadAllBookings());

        viewModel.bookings.observe(getViewLifecycleOwner(), this::onBookingsLoaded);
        viewModel.loading.observe(getViewLifecycleOwner(), loading ->
                binding.swipeRefresh.setRefreshing(Boolean.TRUE.equals(loading)));

        if (viewModel.bookings.getValue() == null) {
            viewModel.loadAllBookings();
        } else {
            onBookingsLoaded(viewModel.bookings.getValue());
        }
    }

    private void onBookingsLoaded(List<BookingModel> all) {
        binding.swipeRefresh.setRefreshing(false);
        if (all == null) {
            showEmpty();
            return;
        }

        List<BookingModel> filtered = new ArrayList<>();
        for (BookingModel b : all) {
            if (b.status == null) continue;
            switch (filter) {
                case FILTER_UPCOMING:
                    if (b.status.equals("PENDING") || b.status.equals("CONFIRMED")
                            || b.status.equals("CHECKED_IN"))
                        filtered.add(b);
                    break;
                case FILTER_PAST:
                    if (b.status.equals("CHECKED_OUT"))
                        filtered.add(b);
                    break;
                case FILTER_CANCELLED:
                    if (b.status.equals("CANCELLED"))
                        filtered.add(b);
                    break;
            }
        }

        adapter.submitList(filtered);
        if (filtered.isEmpty()) showEmpty();
        else showList();
    }

    private void showEmpty() {
        binding.tvEmpty.setVisibility(View.VISIBLE);
        binding.rvBookings.setVisibility(View.GONE);
    }

    private void showList() {
        binding.tvEmpty.setVisibility(View.GONE);
        binding.rvBookings.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}