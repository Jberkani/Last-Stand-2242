package com.jberdev.lastStand2242;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//A creer sur un thread different pour une meilleur optimisation ! TOUJOURS L'OPTIMISATION

public class InfiniteScrollerAdapter extends RecyclerView.Adapter<InfiniteScrollerAdapter.ViewHolder> {
    Drawable[] backgrounds;
    int mLayout = 0;

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        holder.getImageView().setImageDrawable(backgrounds[position % backgrounds.length]);
    }
    public InfiniteScrollerAdapter(Drawable[] drawable, int layoutId){
        backgrounds  = drawable;
        mLayout = layoutId;
    }


    @Override
    public int getItemCount()
    {
        return Integer.MAX_VALUE;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
        public ImageView getImageView(){
            return imageView;
        }



    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(mLayout, viewGroup, false);

        return new ViewHolder(view);
        }


    }
