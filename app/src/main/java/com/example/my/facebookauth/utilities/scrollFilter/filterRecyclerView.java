package com.example.my.facebookauth.utilities.scrollFilter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Owner on 2017-04-04.
 */

public class filterRecyclerView extends RecyclerView {

    private static boolean smoothScroll = false;

    public filterRecyclerView(Context context) {
        super(context);
    }

    public filterRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public filterRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onScrollStateChanged(int state) {

        if (state == SCROLL_STATE_IDLE && getSmoothScroll()) {

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();

            int center = (Resources.getSystem().getDisplayMetrics().widthPixels)/2;

            int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
            int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

            Log.e("first and last", " = " + firstVisibleItem + ": = " + lastVisibleItem);

            int moveAmount = Integer.MAX_VALUE;
            int positionMove = 0;



            for (int i = firstVisibleItem; i <= lastVisibleItem; i++) {

                View currentView = linearLayoutManager.findViewByPosition(i);

                int centerView = currentView.getRight() - currentView.getLeft();


                int currentMove = center - centerView;

                if (Math.abs(currentMove) < Math.abs(moveAmount)) {

                    moveAmount = currentMove;
                    positionMove = i;
                }
            }

            Log.e("currentMove", " = " + moveAmount);
            smoothScrollToPosition(positionMove);
            setSmoothScroll(false);
            Log.e("smoothScrolling", "i am smoothScrolling");



        }


    }

    public int getCenter(View view) {

        int center = (view.getRight() - view.getLeft()/2);

        return (view.getRight() - view.getLeft()/2);

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        setSmoothScroll(true);
        return super.onTouchEvent(e);


    }

    public boolean getSmoothScroll() {
        return smoothScroll;
    }

    public void setSmoothScroll(Boolean bool) {
        smoothScroll = bool;
    }



}
