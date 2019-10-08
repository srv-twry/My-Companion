package com.example.srv_twry.studentcompanion.Adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.srv_twry.studentcompanion.POJOs.Feature;
import com.example.srv_twry.studentcompanion.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by srv_twry on 17/6/17.
 * The recyclerview adapter for the Features Recyclerview in the HomeActivity.
 */

public class FeaturesRecyclerViewAdapter extends RecyclerView.Adapter<FeaturesRecyclerViewAdapter.ViewHolder>  {

    private final ArrayList<Feature> featureArrayList;
    private final FeaturesOnClickListener featuresOnClickListener;

    public FeaturesRecyclerViewAdapter(ArrayList<Feature> featureArrayList,FeaturesOnClickListener featuresOnClickListener){
        this.featureArrayList = featureArrayList;
        this.featuresOnClickListener = featuresOnClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.features_view_holder_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return featureArrayList.size();
    }

    public interface FeaturesOnClickListener{
        void onFeatureClicked(int position);
    }

    // The view holder for the Features recycler view.
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.features_view_holder_title)
        TextView title;
        @BindView(R.id.features_view_holder_image)
        ImageView image;

        public ViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
            view.setOnClickListener(this);
        }

        public void bind(int position) {
            title.setText(featureArrayList.get(position).getTitle());
            image.setImageResource(featureArrayList.get(position).getImageResourceId());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            featuresOnClickListener.onFeatureClicked(position);
        }
    }
}
