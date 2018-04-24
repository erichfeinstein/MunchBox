package seniorproj.munchbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ImageView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Eric on 2/23/2018.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    static final int THUMBSIZE = 200;

    private List<JournalEntry> entriesList;
    private Context context;

    public MyAdapter(ArrayList<JournalEntry> entriesList, Context context) {
        this.entriesList = entriesList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_in_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JournalEntry entry = entriesList.get(position);
        holder.textViewDish.setText(entry.getNameOfDish());
        holder.textViewRestaurant.setText(entry.getRestaurantName());
        holder.textViewRating.setText(entry.getRatingAsStars());
        holder.textViewDistance.setText(entry.getDistance());
        if (entry.isFavorite()) holder.favoriteImg.setVisibility(View.VISIBLE);
        else holder.favoriteImg.setVisibility(View.INVISIBLE);
        holder.id = entry.getIdentifier();
        Bitmap img = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(entry.getPhotoPath()), THUMBSIZE, THUMBSIZE);
        holder.thumb.setImageBitmap(img);
    }

    @Override
    public int getItemCount() {
        return entriesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textViewDish;
        public TextView textViewRestaurant;
        public TextView textViewRating;
        public TextView textViewDistance;
        private int id;
        public ImageView thumb;
        public ImageView favoriteImg;
        public String imgPath;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewDish = itemView.findViewById(R.id.dish);
            textViewRestaurant = itemView.findViewById(R.id.restaurant);
            textViewRating = itemView.findViewById(R.id.rating);
            textViewDistance = itemView.findViewById(R.id.distance);
            thumb = itemView.findViewById(R.id.foodThumbnail);
            favoriteImg = itemView.findViewById(R.id.favorite);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Intent intent = new Intent(context, ViewEntry.class);
                MainActivity activity = (MainActivity) context;
                if (activity != null) {
                    JournalEntry selectedEntry = new JournalEntry();
                    for (int i = 0; i < activity.getJournal().size(); i++) {
                        if (activity.getJournal().get(i).getIdentifier() == id) {
                            selectedEntry = activity.getJournal().get(i);
                        }
                    }
                    intent.putExtra("id", id);
                    intent.putExtra("dishName", selectedEntry.getNameOfDish());
                    intent.putExtra("restaurantName", selectedEntry.getRestaurantName());
                    Format formatter = new SimpleDateFormat("MMM dd yyyy");
                    Date date = selectedEntry.getEntryDate();
                    String dateString = formatter.format(date);
                    intent.putExtra("date", dateString);
                    intent.putExtra("description", selectedEntry.getDescription());
                    intent.putExtra("tagsList", selectedEntry.getTags());
                    intent.putExtra("rating", selectedEntry.getRating());
                    intent.putExtra("imgPath", selectedEntry.getPhotoPath());
                    intent.putExtra("id", selectedEntry.getIdentifier());
                    MainActivity.resetJournal(); //Always reset journal before leaving MainActivity
                    context.startActivity(intent);
                    System.out.println("Opening entry with ID: " + id);
                }
            }
        }

        public ImageView getThumb() { return thumb; }
    }

}
