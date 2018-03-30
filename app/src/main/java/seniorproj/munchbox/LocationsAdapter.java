package seniorproj.munchbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Eric on 2/23/2018.
 */

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    private List<String> locationsList;
    private Context context;

    public LocationsAdapter(List<String> tagsList, Context context) {
        this.locationsList = tagsList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_in_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String tag = locationsList.get(position);
        holder.locationTextView.setText(tag);
    }

    @Override
    public int getItemCount() {
        return locationsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView locationTextView;
        private EditText labelToUpdate;

        public ViewHolder(View itemView) {
            super(itemView);
            locationTextView = (TextView)itemView.findViewById(R.id.tagText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                EditEntry activity = (EditEntry) context;
                labelToUpdate = activity.findViewById(R.id.restaurant);
                labelToUpdate.setText(locationTextView.getText());
            }
        }
    }

}
