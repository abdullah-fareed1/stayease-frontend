package lk.grandhotel.stayease.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import lk.grandhotel.stayease.R;

public class OnboardingFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    public static OnboardingFragment newInstance(int position) {
        OnboardingFragment fragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_slide, container, false);
        int position = getArguments() != null ? getArguments().getInt(ARG_POSITION, 0) : 0;

        ImageView image = view.findViewById(R.id.iv_onboarding);
        TextView headline = view.findViewById(R.id.tv_headline);
        TextView subtitle = view.findViewById(R.id.tv_subtitle);

        int[] images = {R.drawable.ic_onboard_browse, R.drawable.ic_onboard_booking, R.drawable.ic_onboard_payment};
        String[] headlines = {"Browse Rooms", "Easy Booking", "Secure Payments"};
        String[] subtitles = {
                "Explore our luxury rooms with detailed photos and amenities.",
                "Pick your dates, add guests and book in minutes.",
                "Pay securely with Stripe. Partial or full payment options."
        };

        image.setImageResource(images[position]);
        headline.setText(headlines[position]);
        subtitle.setText(subtitles[position]);

        return view;
    }
}