package seniorproj.munchbox;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ImageView;

import java.util.ArrayList;
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

    public void addTag(String tag) {
        tagsList.add(tag);
        notifyDataSetChanged();
    }

    public void setTagsList(List<String> tagsList) {
        this.tagsList = tagsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == R.layout.tag_in_list) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_in_list, parent, false);
            return new ViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_tag_in_list, parent, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position == tagsList.size()) {
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditEntry a = (EditEntry) context;
                    a.openAddTag(holder.button);
                }
            });
        } else {
            String tag = tagsList.get(position);
            holder.tagView.setText(tag);
        }
    }

    //Add tag button on end
    @Override
    public int getItemViewType(int position) {
        return (position == tagsList.size()) ? R.layout.add_tag_in_list : R.layout.tag_in_list;
    }

    @Override
    public int getItemCount() {
        return tagsList.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tagView;
        private ImageView button;

        public ViewHolder(View itemView) {
            super(itemView);
            tagView = (TextView)itemView.findViewById(R.id.tagText);
            button = (ImageView)itemView.findViewById(R.id.cancel);
            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                tagsList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, tagsList.size());
            }
        }
    }

}
