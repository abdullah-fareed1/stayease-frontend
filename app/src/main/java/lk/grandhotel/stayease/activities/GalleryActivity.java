package lk.grandhotel.stayease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

import lk.grandhotel.stayease.R;
import com.bumptech.glide.Glide;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ViewPager2 viewPager    = findViewById(R.id.view_pager_gallery);
        TextView   tvCounter    = findViewById(R.id.tv_image_counter);

        ArrayList<String> urls = getIntent().getStringArrayListExtra("imageUrls");
        if (urls == null) urls = new ArrayList<>();
        int startPos = getIntent().getIntExtra("startPosition", 0);

        final ArrayList<String> finalUrls = urls;
        final int total = finalUrls.size();

        viewPager.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                PhotoView pv = new PhotoView(parent.getContext());
                pv.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                return new RecyclerView.ViewHolder(pv) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                Glide.with(holder.itemView.getContext())
                        .load(finalUrls.get(position))
                        .into((PhotoView) holder.itemView);
            }

            @Override
            public int getItemCount() { return finalUrls.size(); }
        });

        viewPager.setCurrentItem(startPos, false);
        tvCounter.setText((startPos + 1) + " / " + total);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tvCounter.setText((position + 1) + " / " + total);
            }
        });

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());

        findViewById(R.id.btn_share).setOnClickListener(v -> {
            int pos = viewPager.getCurrentItem();
            if (pos < finalUrls.size()) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, finalUrls.get(pos));
                startActivity(Intent.createChooser(share, "Share Image"));
            }
        });
    }
}