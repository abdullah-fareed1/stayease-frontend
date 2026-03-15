package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.network.models.ReviewModel;
import lk.grandhotel.stayease.network.models.RoomModel;
import lk.grandhotel.stayease.ui.room.AllReviewsBottomSheet;
import lk.grandhotel.stayease.ui.room.ImagePagerAdapter;
import lk.grandhotel.stayease.ui.room.ReviewAdapter;
import lk.grandhotel.stayease.ui.room.RoomDetailViewModel;
import lk.grandhotel.stayease.utils.TokenPrefs;

public class RoomDetailActivity extends AppCompatActivity {

    private RoomDetailViewModel viewModel;

    private ViewPager2             viewPager;
    private LinearLayout           dotsContainer;
    private CollapsingToolbarLayout collapsingToolbar;
    private Chip                   chipCategory;
    private RatingBar              ratingBar;
    private TextView               tvRating, tvRoomTitle, tvGuests, tvReviewCount;
    private TextView               tvDescription, tvPrice, tvViewAllReviews, tvNoReviews;
    private ChipGroup              chipGroupAmenities;
    private RecyclerView           rvReviews;
    private MaterialButton         btnBookNow;
    private View                   progressBar;

    private RoomModel currentRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        bindViews();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(RoomDetailViewModel.class);

        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setNestedScrollingEnabled(false);

        viewModel.loading.observe(this, loading ->
                progressBar.setVisibility(loading ? View.VISIBLE : View.GONE));

        viewModel.room.observe(this, room -> {
            if (room != null) {
                currentRoom = room;
                bindRoom(room);
            }
        });

        viewModel.error.observe(this, msg -> {
            if (msg != null) {
                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
            }
        });

        btnBookNow.setOnClickListener(v -> handleBookNow());
        tvViewAllReviews.setOnClickListener(v -> openReviewsSheet());

        String roomId = getIntent().getStringExtra("roomId");
        if (roomId != null) viewModel.loadRoom(roomId);
    }

    private void bindViews() {
        viewPager          = findViewById(R.id.view_pager_images);
        dotsContainer      = findViewById(R.id.dots_container);
        collapsingToolbar  = findViewById(R.id.collapsing_toolbar);
        chipCategory       = findViewById(R.id.chip_category);
        ratingBar          = findViewById(R.id.rating_bar);
        tvRating           = findViewById(R.id.tv_rating);
        tvRoomTitle        = findViewById(R.id.tv_room_title);
        tvGuests           = findViewById(R.id.tv_guests);
        tvReviewCount      = findViewById(R.id.tv_review_count);
        tvDescription      = findViewById(R.id.tv_description);
        tvPrice            = findViewById(R.id.tv_price);
        tvViewAllReviews   = findViewById(R.id.tv_view_all_reviews);
        tvNoReviews        = findViewById(R.id.tv_no_reviews);
        chipGroupAmenities = findViewById(R.id.chip_group_amenities);
        rvReviews          = findViewById(R.id.rv_reviews);
        btnBookNow         = findViewById(R.id.btn_book_now);
        progressBar        = findViewById(R.id.progress_bar);
    }

    private void bindRoom(RoomModel room) {
        tvRoomTitle.setText(room.title);
        tvDescription.setText(room.description);
        chipCategory.setText(room.category != null ? room.category : "");
        tvGuests.setText(room.maxGuests + " Guests max");
        tvPrice.setText(String.format(Locale.getDefault(), "$%.0f", room.pricePerNight));
        tvReviewCount.setText(room.reviewCount + " reviews");

        if (room.averageRating != null && room.averageRating > 0) {
            ratingBar.setRating(room.averageRating.floatValue());
            tvRating.setText(String.format(Locale.getDefault(), "%.1f", room.averageRating));
            ratingBar.setVisibility(View.VISIBLE);
        } else {
            ratingBar.setVisibility(View.GONE);
            tvRating.setVisibility(View.GONE);
        }

        chipGroupAmenities.removeAllViews();
        if (room.amenities != null) {
            for (String amenity : room.amenities) {
                Chip chip = new Chip(this);
                chip.setText(amenity);
                chip.setCheckable(false);
                chip.setClickable(false);
                chipGroupAmenities.addView(chip);
            }
        }

        if (room.images != null && !room.images.isEmpty()) {
            viewPager.setAdapter(new ImagePagerAdapter(room.images, position -> {
                ArrayList<String> urls = new ArrayList<>();
                for (RoomModel.ImageModel img : room.images) urls.add(img.url);
                Intent intent = new Intent(this, GalleryActivity.class);
                intent.putStringArrayListExtra("imageUrls", urls);
                intent.putExtra("startPosition", position);
                startActivity(intent);
            }));
            updateDots(room.images.size(), 0);
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    updateDots(room.images.size(), position);
                }
            });
        }

        if (room.reviews != null && !room.reviews.isEmpty()) {
            List<ReviewModel> preview = room.reviews.size() > 5
                    ? room.reviews.subList(0, 5) : room.reviews;
            rvReviews.setAdapter(new ReviewAdapter(preview));
            rvReviews.setVisibility(View.VISIBLE);
            tvNoReviews.setVisibility(View.GONE);
            tvViewAllReviews.setVisibility(room.reviews.size() > 5 ? View.VISIBLE : View.GONE);
        } else {
            rvReviews.setVisibility(View.GONE);
            tvNoReviews.setVisibility(View.VISIBLE);
            tvViewAllReviews.setVisibility(View.GONE);
        }
    }

    private void updateDots(int count, int current) {
        dotsContainer.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(i == current ? R.drawable.dot_active : R.drawable.dot_inactive);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            p.setMargins(6, 0, 6, 0);
            dot.setLayoutParams(p);
            dotsContainer.addView(dot);
        }
    }

    private void handleBookNow() {
        if (!TokenPrefs.hasTokens(this)) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Login Required")
                    .setMessage("Please log in to book this room.")
                    .setPositiveButton("Sign In", (d, w) -> {
                        startActivity(new Intent(this, LoginActivity.class));
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return;
        }
        if (currentRoom == null) return;
        if (!"AVAILABLE".equals(currentRoom.availabilityStatus)) {
            Snackbar.make(findViewById(android.R.id.content),
                    "This room is currently unavailable.", Snackbar.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("roomId", currentRoom.id);
        intent.putExtra("roomTitle", currentRoom.title);
        intent.putExtra("pricePerNight", currentRoom.pricePerNight);
        intent.putExtra("maxGuests", currentRoom.maxGuests);
        if (currentRoom.primaryImage != null) {
            intent.putExtra("roomImageUrl", currentRoom.primaryImage.url);
        }
        startActivity(intent);
    }

    private void openReviewsSheet() {
        if (currentRoom == null || currentRoom.reviews == null || currentRoom.reviews.isEmpty()) return;
        AllReviewsBottomSheet sheet = AllReviewsBottomSheet.newInstance(currentRoom.reviews);
        sheet.show(getSupportFragmentManager(), "all_reviews");
    }
}