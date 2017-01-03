package com.example.my.facebookauth.models;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.my.facebookauth.R;
import com.google.firebase.database.DatabaseReference;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Owner on 2016-11-24.
 */

public class eventListAdapter extends FirebaseListAdapter<event> {
    @InjectView(R.id.event_item_title)
    TextView title;

    Context context;

    @InjectView(R.id.event_item_category)
    TextView category;

    @InjectView(R.id.event_item_description)
    TextView description;

    public eventListAdapter(DatabaseReference ref, Class<event> eventClass, int layout, Activity activity ) {
        super(ref, event.class, layout, activity);
        this.context = activity.getApplicationContext();
    }

    /**
     * Bind an instance of the ExampleObject class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single ExampleObject instance that represents the current data to bind.
     *
     * @param view    A view instance corresponding to the layout we passed to the constructor.
     * @param example An instance representing the current state of a message
     */
    @Override
    protected void populateView(View view, event example) {
        ButterKnife.inject(this, view);

        // populate the list element
        title.setText(example.getTitle());
        category.setText(example.getCategory());
        description.setText(example.getDescription());
    }

    @Override
    protected List<event> filters(List<event> models, CharSequence filter) {
        List<event> filterList = new ArrayList<>();
        for (int i = 0; i < models.size(); i++) {
            /* implement your own filtering logic
             * and then call  filterList.add(models.get(i));
             */
        }
        return filterList;
    }


    @Override
    protected Map<String, event> filterKeys(List<event> mModels) {
        //// TODO: 2016-11-25 needs to be updated to implement filtering
        return null;
    }
}
