package seniorproj.munchbox;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Eric on 2/23/2018.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<JournalEntry> entriesList;
    private Context context;

    public MyAdapter(List<JournalEntry> entriesList, Context context) {
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
    }

    @Override
    public int getItemCount() {
        return entriesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewDish;
        public TextView textViewRestaurant;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewDish = (TextView)itemView.findViewById(R.id.dish);
            textViewRestaurant = (TextView)itemView.findViewById(R.id.restaurant);
        }
    }

}
