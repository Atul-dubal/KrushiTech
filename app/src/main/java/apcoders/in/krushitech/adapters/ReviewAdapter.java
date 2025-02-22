package apcoders.in.krushitech.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import apcoders.in.krushitech.R;
import apcoders.in.krushitech.models.ReviewModel;
import apcoders.in.krushitech.utils.FetchUserData;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    Context context;
    ArrayList<ReviewModel> ReviewList;

    public ReviewAdapter(Context context, ArrayList<ReviewModel> ReviewList) {
        this.context = context;
        this.ReviewList = ReviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_recyclerview_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FetchUserData.getUserById(ReviewList.get(position).getUserId(), new FetchUserData.GetUserByIdCallback() {
            @Override
            public void onCallback(String phone_Number) {
                holder.ReviewUser.setText("XXXXX X"+phone_Number.substring(6,10));
            }
        });

        holder.ReviewResponseText.setText(ReviewList.get(position).getReviewResponse());
        if (ReviewList.get(position).getRatingStars() == 1) {
            holder.review_star_1.setImageResource(R.drawable.review_star_color);
        } else if (ReviewList.get(position).getRatingStars() == 2) {
            holder.review_star_1.setImageResource(R.drawable.review_star_color);
            holder.review_star_2.setImageResource(R.drawable.review_star_color);
        } else if (ReviewList.get(position).getRatingStars() == 3) {
            holder.review_star_1.setImageResource(R.drawable.review_star_color);
            holder.review_star_2.setImageResource(R.drawable.review_star_color);
            holder.review_star_3.setImageResource(R.drawable.review_star_color);
        } else if (ReviewList.get(position).getRatingStars() == 4) {
            holder.review_star_1.setImageResource(R.drawable.review_star_color);
            holder.review_star_2.setImageResource(R.drawable.review_star_color);
            holder.review_star_3.setImageResource(R.drawable.review_star_color);
            holder.review_star_4.setImageResource(R.drawable.review_star_color);
        } else if (ReviewList.get(position).getRatingStars() == 5) {
            holder.review_star_1.setImageResource(R.drawable.review_star_color);
            holder.review_star_2.setImageResource(R.drawable.review_star_color);
            holder.review_star_3.setImageResource(R.drawable.review_star_color);
            holder.review_star_4.setImageResource(R.drawable.review_star_color);
            holder.review_star_5.setImageResource(R.drawable.review_star_color);
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return ReviewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ReviewUser, ReviewResponseText;
        ImageView review_star_1, review_star_2, review_star_3, review_star_4, review_star_5;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ReviewUser = itemView.findViewById(R.id.ReviewUser);
            ReviewResponseText = itemView.findViewById(R.id.ReviewResponseText);
            review_star_1 = itemView.findViewById(R.id.review_star_1);
            review_star_2 = itemView.findViewById(R.id.review_star_2);
            review_star_3 = itemView.findViewById(R.id.review_star_3);
            review_star_4 = itemView.findViewById(R.id.review_star_4);
            review_star_5 = itemView.findViewById(R.id.review_star_5);
        }
    }

    ;
}
