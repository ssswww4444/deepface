package au.edu.unimelb.eresearch.happypets.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import au.edu.unimelb.eresearch.happypets.R;
import au.edu.unimelb.eresearch.happypets.model.Breed;
import au.edu.unimelb.eresearch.happypets.ui.BreedDetailActivity;

public class BreedRVAdapter extends RecyclerView.Adapter<BreedRVAdapter.ViewHolder> {
    private List<Breed> mData;
    private Context mActivity;

    public BreedRVAdapter(List<Breed> data, Activity activity) {
        this.mData = data;
        this.mActivity = activity;
    }

    @Override
    public BreedRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row_breed,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Breed breed = mData.get(position);
        holder.setData(breed);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateData(List<Breed> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout container;
        ImageView imgBreed;
        TextView tvBreed;

        Breed breed;

        ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            imgBreed = itemView.findViewById(R.id.img_breed);
            tvBreed = itemView.findViewById(R.id.tv_breed);

            container.setOnClickListener((v) -> {
                Intent intent = new Intent(mActivity, BreedDetailActivity.class);
                intent.putExtra(BreedDetailActivity.EXTRA_BREED, breed);
                mActivity.startActivity(intent);
            });
        }

        public void setData(Breed breed) {
            this.breed = breed;

            CircularProgressDrawable progressDrawable = new CircularProgressDrawable(mActivity);
            progressDrawable.setStrokeWidth(2);
            progressDrawable.setCenterRadius(20);

            Glide.with(mActivity)
                    .load(breed.getImage())
                    .placeholder(progressDrawable)
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .into(this.imgBreed);
            this.tvBreed.setText(breed.getBreed());
        }
    }
}
