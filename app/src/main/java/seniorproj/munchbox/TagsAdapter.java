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

import java.util.List;

/**
 * Created by Eric on 2/23/2018.
 */

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {

    private List<String> tagsList;
    private Context context;

    public TagsAdapter(List<String> tagsList, Context context) {
        this.tagsList = tagsList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_in_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String tag = tagsList.get(position);
        holder.tagView.setText(tag);

    }

    @Override
    public int getItemCount() {
        return tagsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tagView;

        public ViewHolder(View itemView) {
            super(itemView);
            tagView = (TextView)itemView.findViewById(R.id.tagText);
        }
    }

}
