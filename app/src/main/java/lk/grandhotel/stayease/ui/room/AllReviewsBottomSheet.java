package lk.grandhotel.stayease.ui.room;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.util.List;

import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.network.models.ReviewModel;

public class AllReviewsBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_REVIEWS = "reviews";

    public static AllReviewsBottomSheet newInstance(List<ReviewModel> reviews) {
        AllReviewsBottomSheet sheet = new AllReviewsBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_REVIEWS, (Serializable) reviews);
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv     = view.findViewById(R.id.rv_all_reviews);
        TextView     tvTitle = view.findViewById(R.id.tv_reviews_title);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        if (getArguments() != null) {
            @SuppressWarnings("unchecked")
            List<ReviewModel> reviews = (List<ReviewModel>) getArguments().getSerializable(ARG_REVIEWS);
            if (reviews != null) {
                tvTitle.setText("All Reviews (" + reviews.size() + ")");
                rv.setAdapter(new ReviewAdapter(reviews));
            }
        }
    }
}