package com.example.imagegallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{

    private Context context;
    private ImageList photosList;

    public ImageAdapter(Context context, ImageList list) {
        this.context= context;
        this.photosList = list;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
        View view = inflater.inflate(R.layout.list_item,parent,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Photo photo = photosList.getPhotos().getPhoto().get(position);

        Glide.with(context).load(photo.getUrlS())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);

        holder.title.setText(photo.getTitle());
    }
    @Override
    public int getItemCount() {
        return photosList.getPhotos().getPhoto().size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.idImage);
            title = itemView.findViewById(R.id.idTitle);
        }
    }

}
