package lk.grandhotel.stayease.ui.room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lk.grandhotel.stayease.R;
import lk.grandhotel.stayease.network.models.ReviewModel;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final List<ReviewModel> reviews;

    public ReviewAdapter(List<ReviewModel> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder h, int position) {
        ReviewModel r = reviews.get(position);
        String name = (r.user != null && r.user.name != null) ? r.user.name : "Guest";
        h.tvAvatar.setText(String.valueOf(name.charAt(0)).toUpperCase());
        h.tvName.setText(name);
        h.tvComment.setText(r.comment != null ? r.comment : "");
        h.ratingBar.setRating(r.rating);
        if (r.createdAt != null && r.createdAt.length() >= 10) {
            h.tvDate.setText(r.createdAt.substring(0, 10));
        }
    }

    @Override
    public int getItemCount() { return reviews.size(); }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView  tvAvatar, tvName, tvComment, tvDate;
        RatingBar ratingBar;

        ReviewViewHolder(@NonNull View v) {
            super(v);
            tvAvatar  = v.findViewById(R.id.tv_review_avatar);
            tvName    = v.findViewById(R.id.tv_review_name);
            tvComment = v.findViewById(R.id.tv_review_comment);
            tvDate    = v.findViewById(R.id.tv_review_date);
            ratingBar = v.findViewById(R.id.rating_bar_review);
        }
    }
}