package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.utils.AppPrefs;
import lk.grandhotel.stayease.utils.TokenPrefs;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!AppPrefs.isOnboardingDone(this)) {
                startActivity(new Intent(this, OnboardingActivity.class));
            } else if (TokenPrefs.hasTokens(this)) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 1500);
    }
}