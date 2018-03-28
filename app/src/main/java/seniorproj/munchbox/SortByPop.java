package seniorproj.munchbox;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

/**
 * Created by Eric on 3/28/2018.
 */

public class SortByPop extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sort_by_pop);

        DisplayMetrics popup = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(popup);

        int width = popup.widthPixels;
        int height = popup.heightPixels;

        getWindow().setLayout((int)(width*0.7), (int)(height*0.5));
    }

}
