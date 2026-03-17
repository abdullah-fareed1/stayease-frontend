package lk.grandhotel.stayease.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;

import lk.grandhotel.stayease.R;

public class HomeHeaderAdapter extends RecyclerView.Adapter<HomeHeaderAdapter.HeaderViewHolder> {

    public interface OnCategorySelectedListener {
        void onCategorySelected(String category);
    }

    private String userName = "Guest";
    private String greeting = "Good morning,";
    private int roomCount = 0;
    private final OnCategorySelectedListener listener;

    public HomeHeaderAdapter(OnCategorySelectedListener listener) {
        this.listener = listener;
    }

    public void setUserName(String name) {
        this.userName = name != null ? name : "Guest";
        notifyItemChanged(0);
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
        notifyItemChanged(0);
    }

    public void setRoomCount(int count) {
        this.roomCount = count;
        notifyItemChanged(0);
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_home_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder h, int position) {
        h.tvGreeting.setText(greeting);
        h.tvUserName.setText(userName);
        h.tvAvatar.setText(userName.isEmpty() ? "G" : String.valueOf(userName.charAt(0)).toUpperCase());
        h.tvRoomCount.setText(roomCount > 0 ? roomCount + " rooms" : "");

        h.chipGroupCategory.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            String category = null;
            if (id == R.id.chip_standard) category = "STANDARD";
            else if (id == R.id.chip_deluxe) category = "DELUXE";
            else if (id == R.id.chip_suite) category = "SUITE";
            else if (id == R.id.chip_family) category = "FAMILY";
            listener.onCategorySelected(category);
        });
    }

    @Override
    public int getItemCount() { return 1; }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvGreeting, tvUserName, tvAvatar, tvRoomCount;
        ChipGroup chipGroupCategory;

        HeaderViewHolder(@NonNull View v) {
            super(v);
            tvGreeting       = v.findViewById(R.id.tv_greeting);
            tvUserName       = v.findViewById(R.id.tv_user_name);
            tvAvatar         = v.findViewById(R.id.tv_avatar);
            tvRoomCount      = v.findViewById(R.id.tv_room_count);
            chipGroupCategory = v.findViewById(R.id.chip_group_category);
        }
    }
}