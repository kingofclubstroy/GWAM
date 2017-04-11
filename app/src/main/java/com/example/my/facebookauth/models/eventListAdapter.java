package com.example.my.facebookauth.models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.my.facebookauth.R;
import com.example.my.facebookauth.utilities.scrollFilter.CircularArrayAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconHandler;

import static com.example.my.facebookauth.R.id.eventType;
import static com.example.my.facebookauth.R.id.top_layout;


/**
 * Created by Owner on 2016-11-24.
 */

public class eventListAdapter extends RecyclerView.Adapter<eventListAdapter.ViewHolder> {

    Context context;


    private static final String LOG_TAG = "FirebaseListAdapter";
    private DatabaseReference mRef;
    private Class<event> mModelClass;
    private int mLayout;
    private LayoutInflater mInflater;
    private List<event> mModels;
    private List<event> mFilteredModels;
    private Map<String, event> mModelKeys;
    private Map<String, event> mFilteredKeys;
    private ChildEventListener mListener;
    private Context mContext;
//    private ValueFilter valueFilter;



    public eventListAdapter(DatabaseReference ref, Class<event> eventClass, int layout, Activity activity, List<event> events ) {
        this.context = activity.getApplicationContext();
        this.mModels = events;
        this.mRef = mRef;
        this.mModelClass = mModelClass;
        this.mLayout = mLayout;
        mInflater = activity.getLayoutInflater();
        mModels = new ArrayList<>();
        mModelKeys = new HashMap<>();
    }

    @Override
    public eventListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_feed_list_item, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView eventType;
        TextView numberFriends;
        ImageView firstEmoji;
        ImageView secondEmoji;
        ImageView thirdEmoji;
        LinearLayout top_layout;

        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.eventTitle);
            eventType = (TextView) view.findViewById(R.id.eventType);
            numberFriends = (TextView) view.findViewById(R.id.number_Friends);
            firstEmoji = (ImageView) view.findViewById(R.id.first_Emoji);
            secondEmoji = (ImageView) view.findViewById(R.id.second_Emoji);
            thirdEmoji = (ImageView) view.findViewById(R.id.third_Emoji);
            top_layout = (LinearLayout) view.findViewById(R.id.top_layout);

        }
    }

    @Override
    public void onBindViewHolder(eventListAdapter.ViewHolder viewHolder, int i) {

        Log.e("this is running", " events = " + mModels);


        // populate the list element
        viewHolder.title.setText(mModels.get(i).getTitle());
        viewHolder.title.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaNeue-Medium (1).ttf"));
        viewHolder.eventType.setText(mModels.get(i).getCategory());
        viewHolder.firstEmoji.setImageDrawable(context.getResources().getDrawable(EmojiconHandler.getEmojiResource(0x1f37a)));
        viewHolder.secondEmoji.setImageDrawable(context.getResources().getDrawable(EmojiconHandler.getEmojiResource(0x1f61c)));
        viewHolder.thirdEmoji.setImageDrawable(context.getResources().getDrawable(EmojiconHandler.getEmojiResource(0x1f3c0)));

        switch (mModels.get(i).getCategory()) {

            case "Party":
                viewHolder.top_layout.setBackgroundColor(context.getResources().getColor(R.color.Party_color));
                break;
            case "Games":
                viewHolder.top_layout.setBackgroundColor(context.getResources().getColor(R.color.Games_color));
                break;
            case "Recreation":
                viewHolder.top_layout.setBackgroundColor(context.getResources().getColor(R.color.Rec_color));
                break;
            case "Music":
                viewHolder.top_layout.setBackgroundColor(context.getResources().getColor(R.color.Music_color));
                break;
            case "Food":
                viewHolder.top_layout.setBackgroundColor(context.getResources().getColor(R.color.Food_color));
                break;
        }


    }

//    @Override
//    protected List<event> filters(List<event> models, CharSequence filter) {
//        List<event> filterList = new ArrayList<>();
//        for (int i = 0; i < models.size(); i++) {
//            /* implement your own filtering logic
//             * and then call  filterList.add(models.get(i));
//             */
//        }
//        return filterList;
//    }


//    @Override
//    protected Map<String, event> filterKeys(List<event> mModels) {
//        //// TODO: 2016-11-25 needs to be updated to implement filtering
//        return null;
//    }

    @Override
    public int getItemCount() {
        Log.e("getItemCount", "called with num events = " + mModels.size());
        return mModels.size();
    }

    public boolean exists(String key) {
        return mModelKeys.containsKey(key);
    }

    public event addSingle(DataSnapshot snapshot) {
        event model = snapshot.getValue(event.class);
        mModelKeys.put(snapshot.getKey(), model);

        mModels.add(model);

        notifyItemInserted(mModels.size());


        return model;

    }

    public void update(DataSnapshot snapshot, String key) {
        event oldModel = mModelKeys.get(key);
        event newModel = snapshot.getValue(event.class);
        int index = mModels.indexOf(oldModel);

        if (index >= 0) {
            mModels.set(index, newModel);
            mModelKeys.put(key, newModel);

            notifyDataSetChanged();
        }
    }

    public void cleanup() {
        // We're being destroyed, let go of our mListener and forget about all of the mModels
        mRef.removeEventListener(mListener);
        mModels.clear();
        mModelKeys.clear();
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    public event getItemAtPosition(int position) {

        return mModels.get(position);
    }

    public void remove(String key) {
        event oldModel = mModelKeys.get(key);
        mModels.remove(oldModel);
        mModelKeys.remove(key);
       notifyDataSetChanged();
    }

    public void removeItem(int position) {

        String key = mModels.get(position).getId();
        mModels.remove(position);
        mModelKeys.remove(key);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mModels.size());
    }

//    protected abstract List<event> filters(List<event> models, CharSequence constraint);

//    private class ValueFilter extends Filter {
//
//        //Invoked in a worker thread to filter the data according to the constraint.
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            FilterResults results = new FilterResults();
//            if (mFilteredModels == null) {
//                mFilteredModels = new ArrayList<>(mModels); // saves the original data in mOriginalValues
//                mFilteredKeys = new HashMap<>(mModelKeys); // saves the original data in mOriginalValues
//            }
//            if (constraint != null && constraint.length() > 0) {
//                List<event> filtered = filters(mFilteredModels, constraint);
//
//                results.count = filtered.size();
//                results.values = filtered;
//                mModelKeys = filterKeys(mModels);
//            } else {
//                results.count = mFilteredModels.size();
//                results.values = mFilteredModels;
//                mModelKeys = mFilteredKeys;
//            }
//            return results;
//        }
//
//
//        //Invoked in the UI thread to publish the filtering results in the user interface.
//        @SuppressWarnings("unchecked")
//        @Override
//        protected void publishResults(CharSequence constraint,
//                                      FilterResults results) {
//            Log.d(LOG_TAG, "filter for " + constraint + ", results nr: " + results.count);
//            mModels = (List<event>) results.values;
//
//            notifyDataSetChanged();
//        }
//    }

//    @Override
//    public Filter getFilter() {
//        if (valueFilter == null) {
//            valueFilter = new ValueFilter();
//        }
//        return valueFilter;
//    }
}
