package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.ui.auth.OnboardingFragment;
import lk.grandhotel.stayease.utils.AppPrefs;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnNext, btnSkip;
    private LinearLayout dotsContainer;
    private final int SLIDE_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.view_pager);
        btnNext = findViewById(R.id.btn_next);
        btnSkip = findViewById(R.id.btn_skip);
        dotsContainer = findViewById(R.id.dots_container);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() { return SLIDE_COUNT; }

            @Override
            public Fragment createFragment(int position) {
                return OnboardingFragment.newInstance(position);
            }
        });

        setupDots(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setupDots(position);
                if (position == SLIDE_COUNT - 1) {
                    btnNext.setText(R.string.get_started);
                } else {
                    btnNext.setText(R.string.next);
                }
            }
        });

        btnNext.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current < SLIDE_COUNT - 1) {
                viewPager.setCurrentItem(current + 1);
            } else {
                finishOnboarding();
            }
        });

        btnSkip.setOnClickListener(v -> finishOnboarding());
    }

    private void setupDots(int current) {
        dotsContainer.removeAllViews();
        for (int i = 0; i < SLIDE_COUNT; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(i == current ? R.drawable.dot_active : R.drawable.dot_inactive);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            dotsContainer.addView(dot);
        }
    }

    private void finishOnboarding() {
        AppPrefs.setOnboardingDone(this);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}