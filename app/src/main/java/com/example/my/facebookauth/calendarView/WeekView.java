package com.example.my.facebookauth.calendarView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.OverScroller;

import com.example.my.facebookauth.R;
import com.example.my.facebookauth.models.block;
import com.example.my.facebookauth.models.day;
import com.example.my.facebookauth.models.event;
import com.example.my.facebookauth.models.eventBox;
import com.example.my.facebookauth.models.hourText;
import com.example.my.facebookauth.utilities.savedPreferences;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


import static com.example.my.facebookauth.R.id.endTime;
import static com.example.my.facebookauth.calendarView.WeekViewUtil.*;
import static java.lang.Math.abs;
import static java.lang.Math.floor;

/**
 * Created by Raquib-ul-Alam Kanak on 7/21/2014.
 * Website: http://alamkanak.github.io/
 */
public class WeekView extends View {

    private enum Direction {
        NONE, LEFT, RIGHT, VERTICAL
    }

    @Deprecated
    public static final int LENGTH_SHORT = 1;
    @Deprecated
    public static final int LENGTH_LONG = 2;
    private final Context mContext;
    private Paint mTimeTextPaint;
    private float mTimeTextWidth;
    private float mTimeTextHeight;
    private Paint mHeaderTextPaint;
    private float mHeaderTextHeight;
    private float mHeaderHeight;
    private GestureDetectorCompat mGestureDetector;
    private OverScroller mScroller;
    private PointF mCurrentOrigin = new PointF(0f, 0f);
    private Direction mCurrentScrollDirection = Direction.NONE;
    private Paint mHeaderBackgroundPaint;
    private float mWidthPerDay;
    private Paint mDayBackgroundPaint;
    private Paint mHourSeparatorPaint;
    private float mHeaderMarginBottom;
    private Paint mTodayBackgroundPaint;
    private Paint mFutureBackgroundPaint;
    private Paint mPastBackgroundPaint;
    private Paint mFutureWeekendBackgroundPaint;
    private Paint mPastWeekendBackgroundPaint;
    private Paint mNowLinePaint;
    private Paint mTodayHeaderTextPaint;
    private Paint mEventBackgroundPaint;
    private float mHeaderColumnWidth;
    private List<EventRect> mEventRects;
    private List<? extends WeekViewEvent> mPreviousPeriodEvents;
    private List<? extends WeekViewEvent> mCurrentPeriodEvents;
    private List<? extends WeekViewEvent> mNextPeriodEvents;
    private TextPaint mEventTextPaint;
    private Paint mHeaderColumnBackgroundPaint;
    private int mFetchedPeriod = -1; // the middle period the calendar has fetched.
    private boolean mRefreshEvents = false;
    private Direction mCurrentFlingDirection = Direction.NONE;
    private ScaleGestureDetector mScaleDetector;
    private boolean mIsZooming;
    private Calendar mFirstVisibleDay;
    private Calendar mLastVisibleDay;
    private boolean mShowFirstDayOfWeekFirst = false;
    private int mDefaultEventColor;
    private int mMinimumFlingVelocity = 0;
    private int mScaledTouchSlop = 0;
    // Attributes and their default values.
    private static int mHourHeight = 1000;
    private int mNewHourHeight = -1;
    private int mMinHourHeight = 0; //no minimum specified (will be dynamic, based on screen)
    private int mEffectiveMinHourHeight = mMinHourHeight; //compensates for the fact that you can't keep zooming out.
    private int mMaxHourHeight = 250;
    private int mColumnGap = 10;
    private int mFirstDayOfWeek = Calendar.MONDAY;
    private int mTextSize = 2700;
    private int mHeaderColumnPadding = 10;
    private int mHeaderColumnTextColor = Color.BLACK;
    private int mNumberOfVisibleDays = 3;
    private int mHeaderRowPadding = 10;
    private int mHeaderRowBackgroundColor = Color.WHITE;
    private int mDayBackgroundColor = Color.rgb(245, 245, 245);
    private int mPastBackgroundColor = Color.rgb(227, 227, 227);
    private int mFutureBackgroundColor = Color.rgb(245, 245, 245);
    private int mPastWeekendBackgroundColor = 0;
    private int mFutureWeekendBackgroundColor = 0;
    private int mNowLineColor = Color.rgb(56, 186, 100);
    private int mNowLineThickness = 5;
    private int mHourSeparatorColor = Color.rgb(230, 230, 230);
    private int mTodayBackgroundColor = Color.rgb(239, 247, 254);
    private int mHourSeparatorHeight = 2;
    private int mTodayHeaderTextColor = Color.rgb(39, 137, 228);
    private int mEventTextSize = 12;
    private int mEventTextColor = Color.BLACK;
    private int mEventPadding = 25;
    private int mHeaderColumnBackgroundColor = Color.WHITE;
    private boolean mIsFirstDraw = true;
    private boolean mAreDimensionsInvalid = true;
    @Deprecated private int mDayNameLength = LENGTH_LONG;
    private int mOverlappingEventGap = 0;
    private int mEventMarginVertical = 0;
    private float mXScrollingSpeed = 1f;
    private Calendar mScrollToDay = null;
    private double mScrollToHour = -1;
    private int mEventCornerRadius = 0;
    private boolean mShowDistinctWeekendColor = false;
    private boolean mShowNowLine = true;
    private boolean mShowDistinctPastFutureColor = false;
    private boolean mHorizontalFlingEnabled = true;
    private boolean mVerticalFlingEnabled = true;
    private int mAllDayEventHeight = 100;
    private int mScrollDuration = 250;

    //my variables
    private int currentRightSide = getWidth();
    private int dayWidth = 1000;
    private static List<event> events;
    private static List<day> days = new ArrayList<>();
    private int dateSpace = 75;
    private float eventFlagHeight = 500;
    private float poleWidth = 30;
    private static List<List<eventBox>> handledEvents = new ArrayList<>();
    //private List<List<eventBox>> blocks = new ArrayList<>();
    private SharedPreferences settings;
    private static boolean restart = true;
    private Paint mTimeTextAMPMPaint;
    private static float previousDayStart;
    private static float nextDayStart;
    private static int currentDayInt;
    private float lastPosition = 0;
    private float newPosition = 0;
    private static int daysLength;
    private block currentBlock;
    private float headerHeight = getHeight() / 12;
    private float headerTextDist = headerHeight / 4;
    private static float bottomScroll;

    //variables for flinging blocks
    private Interpolator animateInterpolator;
    private long startTime;
    private float friction = (float) 0.1;
    private static float currentXVelocity = -1;
    private float firstXPostion = 0;
    private static float lastXPosition = 0;
    private long endTime;




    //List of blocks of colliding event keys to allow horizontal scrolling of the
    //blocks, separated by days
    private static List<List<block>> collidingEvents;

    // Listeners.
    private EventClickListener mEventClickListener;
    private EventLongPressListener mEventLongPressListener;
    private WeekViewLoader mWeekViewLoader;
    private EmptyViewClickListener mEmptyViewClickListener;
    private EmptyViewLongPressListener mEmptyViewLongPressListener;
    private DateTimeInterpreter mDateTimeInterpreter;
    private ScrollListener mScrollListener;

    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {


        @Override
        public boolean onDown(MotionEvent e) {
            //goToNearestOrigin();
            mScroller.forceFinished(true);
            return true;
        }

        //todo will need to fix this
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            Log.e("distanceX", " = " + distanceX);

            lastPosition = newPosition;

            newPosition = mScroller.getCurrY();


            // Check if view is zoomed.
            if (mIsZooming)
                return true;

            //todo have to fix choppy scrolling, by limiting scrolling to the movement of events
            if (abs(distanceX) > abs(distanceY)) {

                boolean isBroke = false;

                float position = e1.getY() - mCurrentOrigin.y;

                int lastDayInt = currentDayInt == days.size() - 1 ? currentDayInt : currentDayInt + 1;

                int workingday = currentDayInt;

                if (currentDayInt == days.size() - 1) {
                    lastDayInt --;
                }

                while ( workingday <= lastDayInt) {

                    for (block currentBlock : days.get(workingday).getBlocks()) {

                        if (position >= currentBlock.getTop() && position <= currentBlock.getBottom()) {

                            int id = currentBlock.getId();

                            currentBlock.setOffset( - distanceX);

                            for (eventBox event : days.get(workingday).getHandledEvents()) {

                                if (event.getId() == id) {

                                    event.setOffset( - distanceX);

                                }


                            }

                            isBroke = true;
                            break;
                        }




                    }

                    if (isBroke) {
                        break;
                    }


                    workingday++;



                }

                ViewCompat.postInvalidateOnAnimation(WeekView.this);

//                if (currentRightSide >= dayWidth ) {
//
//                    mCurrentOrigin.x = dayWidth;
                    //Log.e("distanceX: ", "running" + distanceX );
//                    if (distanceX <= 0) {

                        //Log.e("running:", "");

                       // mCurrentOrigin.x -= distanceX * mXScrollingSpeed;
                        //ViewCompat.postInvalidateOnAnimation(WeekView.this);
//                        currentRightSide += distanceX * mXScrollingSpeed;

//                    }
//                    else {
                        //Log.e("not running", "");
            }


//                }
//                else if (mCurrentOrigin.x >= 0 ) {
//
//                    mCurrentOrigin.x = 0;
//                    if (distanceX > 0) {
//                        mCurrentOrigin.x -= distanceX * mXScrollingSpeed;
//                        ViewCompat.postInvalidateOnAnimation(WeekView.this);
//                        currentRightSide += distanceX * mXScrollingSpeed;
//                    }
//
//                    else {
//
//                    }

                //}

//               else {
//                    mCurrentOrigin.x -= distanceX * mXScrollingSpeed;
//                    //currentRightSide += distanceX * mXScrollingSpeed;
//
//                    ViewCompat.postInvalidateOnAnimation(WeekView.this);
//                }




             else {

                Log.e("currentOrign.y", " = " + mCurrentOrigin.y);
                Log.e("bottomScroll", " = " + bottomScroll);

                if (mCurrentOrigin.y - distanceY >= 0) {
                    mCurrentOrigin.y = 0;
                }

                else if ( mCurrentOrigin.y - getHeight() - distanceY <=  -bottomScroll) {

                    mCurrentOrigin.y =  - bottomScroll;
                }
                else {

                    mCurrentOrigin.y -= distanceY;

                }
                ViewCompat.postInvalidateOnAnimation(WeekView.this);
            }
//            switch (mCurrentScrollDirection) {
//                case NONE: {
//                    // Allow scrolling only in one direction. maybe bound the horizontal scrolling here
//                    if (Math.abs(distanceX) > Math.abs(distanceY)) {
//                        if (distanceX > 0) {
//                            mCurrentScrollDirection = Direction.LEFT;
//                        } else {
//                            mCurrentScrollDirection = Direction.RIGHT;
//                        }
//                    } else {
//                        mCurrentScrollDirection = Direction.VERTICAL;
//                    }
//                    break;
//                }
//                case LEFT: {
//                    // Change direction if there was enough change.
//                    if (Math.abs(distanceX) > Math.abs(distanceY) && (distanceX < -mScaledTouchSlop)) {
//                        mCurrentScrollDirection = Direction.RIGHT;
//                    }
//                    break;
//                }
//                case RIGHT: {
//                    // Change direction if there was enough change.
//                    if (Math.abs(distanceX) > Math.abs(distanceY) && (distanceX > mScaledTouchSlop)) {
//                        mCurrentScrollDirection = Direction.LEFT;
//                    }
//                    break;
//                }
//            }
//
//             //Calculate the new origin after scroll. good
//            switch (mCurrentScrollDirection) {
//                case LEFT:
//                case RIGHT:
//                    mCurrentOrigin.x -= distanceX * mXScrollingSpeed;
//                    ViewCompat.postInvalidateOnAnimation(WeekView.this);
//                    //mCurrentScrollDirection = Direction.NONE;
//                    break;
//                case VERTICAL:
//                    mCurrentOrigin.y -= distanceY;
//                    ViewCompat.postInvalidateOnAnimation(WeekView.this);
//                   // mCurrentScrollDirection = Direction.NONE;
//                    break;
//            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {




            if(abs(velocityX) > abs(velocityY)) {
//
//                final float totalDx = (0.4f * velocityX / 2);
//                final float position = e1.getX() - mCurrentOrigin.y;
//
//                Log.e("postion", " = " + position);
//
//                onAnimateMove(totalDx, (long) (1000 * 0.4f), position);

//                if (currentXVelocity == -1) {
//
//                    currentXVelocity = velocityX * mXScrollingSpeed;
//
//                }
//
//                boolean isBroke = false;
//
//
//                for (int i = 0; i < 2; i++) {
//
//                    for (block currentBlock : days.get(currentDayInt + i).getBlocks()) {
//
//                        if (position >= currentBlock.getTop() && position <= currentBlock.getBottom()) {
//
//                            int id = currentBlock.getId();
//
//                            for (eventBox event : days.get(currentDayInt + i).getHandledEvents()) {
//
//                                if (event.getId() == id) {
//
//                                    event.setOffset(- currentXVelocity);
//
//                                }
//
//
//                            }
//
//                            isBroke = true;
//                            break;
//                        }
//
//
//
//
//                    }
//
//                    if (isBroke) {
//                        break;
//                    }
//
//
//
//
//
//
//                }

//                //ViewCompat.postInvalidateOnAnimation(WeekView.this);
//                currentXVelocity -= velocityX * 0.4f;
//
//                if(currentXVelocity <= 0) {
//
//                    mScroller.forceFinished(true);
//
//                }
//
//                Log.e("i am being called", "i am being called");



            }

            else {

                    mScroller.fling(0, (int) mCurrentOrigin.y, 0, (int) velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, (int) -(mHourHeight * 1000 + mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom + mTimeTextHeight/2 - getHeight()), 0);

            }

            ViewCompat.postInvalidateOnAnimation(WeekView.this);
            return true;
        }


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // If the tap was on an event then trigger the callback.
            if (mEventRects != null && mEventClickListener != null) {
                List<EventRect> reversedEventRects = mEventRects;
                Collections.reverse(reversedEventRects);
                for (EventRect event : reversedEventRects) {
                    if (event.rectF != null && e.getX() > event.rectF.left && e.getX() < event.rectF.right && e.getY() > event.rectF.top && e.getY() < event.rectF.bottom) {
                        mEventClickListener.onEventClick(event.originalEvent, event.rectF);
                        playSoundEffect(SoundEffectConstants.CLICK);
                        return super.onSingleTapConfirmed(e);
                    }
                }
            }

            // If the tap was on in an empty space, then trigger the callback.
            if (mEmptyViewClickListener != null && e.getX() > mHeaderColumnWidth && e.getY() > (mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom)) {
                Calendar selectedTime = getTimeFromPoint(e.getX(), e.getY());
                if (selectedTime != null) {
                    playSoundEffect(SoundEffectConstants.CLICK);
                    mEmptyViewClickListener.onEmptyViewClicked(selectedTime);
                }
            }

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            if (mEventLongPressListener != null && mEventRects != null) {
                List<EventRect> reversedEventRects = mEventRects;
                Collections.reverse(reversedEventRects);
                for (EventRect event : reversedEventRects) {
                    if (event.rectF != null && e.getX() > event.rectF.left && e.getX() < event.rectF.right && e.getY() > event.rectF.top && e.getY() < event.rectF.bottom) {
                        mEventLongPressListener.onEventLongPress(event.originalEvent, event.rectF);
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        return;
                    }
                }
            }

            // If the tap was on in an empty space, then trigger the callback.
            if (mEmptyViewLongPressListener != null && e.getX() > mHeaderColumnWidth && e.getY() > (mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom)) {
                Calendar selectedTime = getTimeFromPoint(e.getX(), e.getY());
                if (selectedTime != null) {
                    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    mEmptyViewLongPressListener.onEmptyViewLongPress(selectedTime);
                }
            }
        }
    };

    public WeekView(Context context) {
        this(context, null);
    }

    public WeekView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Hold references.
        mContext = context;

        // Get the attribute values (if any).
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeekView, 0, 0);
        try {
            mFirstDayOfWeek = a.getInteger(R.styleable.WeekView_firstDayOfWeek, mFirstDayOfWeek);
            mHourHeight = a.getDimensionPixelSize(R.styleable.WeekView_hourHeight, mHourHeight);
            mMinHourHeight = a.getDimensionPixelSize(R.styleable.WeekView_minHourHeight, mMinHourHeight);
            mEffectiveMinHourHeight = mMinHourHeight;
            mMaxHourHeight = a.getDimensionPixelSize(R.styleable.WeekView_maxHourHeight, mMaxHourHeight);
            mTextSize = a.getDimensionPixelSize(R.styleable.WeekView_textSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, context.getResources().getDisplayMetrics()));
            mHeaderColumnPadding = a.getDimensionPixelSize(R.styleable.WeekView_headerColumnPadding, mHeaderColumnPadding);
            mColumnGap = a.getDimensionPixelSize(R.styleable.WeekView_columnGap, mColumnGap);
            mHeaderColumnTextColor = a.getColor(R.styleable.WeekView_headerColumnTextColor, mHeaderColumnTextColor);
            mNumberOfVisibleDays = a.getInteger(R.styleable.WeekView_noOfVisibleDays, mNumberOfVisibleDays);
            mShowFirstDayOfWeekFirst = a.getBoolean(R.styleable.WeekView_showFirstDayOfWeekFirst, mShowFirstDayOfWeekFirst);
            mHeaderRowPadding = a.getDimensionPixelSize(R.styleable.WeekView_headerRowPadding, mHeaderRowPadding);
            mHeaderRowBackgroundColor = a.getColor(R.styleable.WeekView_headerRowBackgroundColor, mHeaderRowBackgroundColor);
            mDayBackgroundColor = a.getColor(R.styleable.WeekView_dayBackgroundColor, mDayBackgroundColor);
            mFutureBackgroundColor = a.getColor(R.styleable.WeekView_futureBackgroundColor, mFutureBackgroundColor);
            mPastBackgroundColor = a.getColor(R.styleable.WeekView_pastBackgroundColor, mPastBackgroundColor);
            mFutureWeekendBackgroundColor = a.getColor(R.styleable.WeekView_futureWeekendBackgroundColor, mFutureBackgroundColor); // If not set, use the same color as in the week
            mPastWeekendBackgroundColor = a.getColor(R.styleable.WeekView_pastWeekendBackgroundColor, mPastBackgroundColor);
            mNowLineColor = a.getColor(R.styleable.WeekView_nowLineColor, mNowLineColor);
            mNowLineThickness = a.getDimensionPixelSize(R.styleable.WeekView_nowLineThickness, mNowLineThickness);
            mHourSeparatorColor = a.getColor(R.styleable.WeekView_hourSeparatorColor, mHourSeparatorColor);
            mTodayBackgroundColor = a.getColor(R.styleable.WeekView_todayBackgroundColor, mTodayBackgroundColor);
            mHourSeparatorHeight = a.getDimensionPixelSize(R.styleable.WeekView_hourSeparatorHeight, mHourSeparatorHeight);
            mTodayHeaderTextColor = a.getColor(R.styleable.WeekView_todayHeaderTextColor, mTodayHeaderTextColor);
            mEventTextSize = a.getDimensionPixelSize(R.styleable.WeekView_eventTextSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mEventTextSize, context.getResources().getDisplayMetrics()));
            mEventTextColor = a.getColor(R.styleable.WeekView_eventTextColor, mEventTextColor);
            mEventPadding = a.getDimensionPixelSize(R.styleable.WeekView_eventPadding, mEventPadding);
            mHeaderColumnBackgroundColor = a.getColor(R.styleable.WeekView_headerColumnBackground, mHeaderColumnBackgroundColor);
            mDayNameLength = a.getInteger(R.styleable.WeekView_dayNameLength, mDayNameLength);
            mOverlappingEventGap = a.getDimensionPixelSize(R.styleable.WeekView_overlappingEventGap, mOverlappingEventGap);
            mEventMarginVertical = a.getDimensionPixelSize(R.styleable.WeekView_eventMarginVertical, mEventMarginVertical);
            mXScrollingSpeed = a.getFloat(R.styleable.WeekView_xScrollingSpeed, mXScrollingSpeed);
            mEventCornerRadius = a.getDimensionPixelSize(R.styleable.WeekView_eventCornerRadius, mEventCornerRadius);
            mShowDistinctPastFutureColor = a.getBoolean(R.styleable.WeekView_showDistinctPastFutureColor, mShowDistinctPastFutureColor);
            mShowDistinctWeekendColor = a.getBoolean(R.styleable.WeekView_showDistinctWeekendColor, mShowDistinctWeekendColor);
            mShowNowLine = a.getBoolean(R.styleable.WeekView_showNowLine, mShowNowLine);
            mHorizontalFlingEnabled = a.getBoolean(R.styleable.WeekView_horizontalFlingEnabled, mHorizontalFlingEnabled);
            mVerticalFlingEnabled = a.getBoolean(R.styleable.WeekView_verticalFlingEnabled, mVerticalFlingEnabled);
            mAllDayEventHeight = a.getDimensionPixelSize(R.styleable.WeekView_allDayEventHeight, mAllDayEventHeight);
            mScrollDuration = a.getInt(R.styleable.WeekView_scrollDuration, mScrollDuration);
        } finally {
            a.recycle();
        }


        init();
    }

    private void init() {
        // Scrolling initialization.
        mGestureDetector = new GestureDetectorCompat(mContext, mGestureListener);
        mScroller = new OverScroller(mContext);

        mMinimumFlingVelocity = ViewConfiguration.get(mContext).getScaledMinimumFlingVelocity();
        mScaledTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();

        // Measure settings for time column.
        mTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeTextPaint.setTextAlign(Paint.Align.CENTER);
        mTimeTextPaint.setTextSize(65);
        mTimeTextPaint.setColor(mHeaderColumnTextColor);
        Rect rect = new Rect();
        mTimeTextPaint.getTextBounds("00", 0, "00".length(), rect);
        mTimeTextHeight = rect.height();
        mHeaderMarginBottom = mTimeTextHeight / 2;
        initTextTimeWidth();

        mTimeTextAMPMPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeTextAMPMPaint.setTextAlign(Paint.Align.CENTER);
        mTimeTextAMPMPaint.setTextSize(40);
        mTimeTextAMPMPaint.setColor(mHeaderColumnTextColor);
        Rect rectAMPM = new Rect();
        mTimeTextAMPMPaint.getTextBounds("PM", 0, "PM".length(), rectAMPM);


        // Measure settings for header row.
        mHeaderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeaderTextPaint.setColor(mHeaderColumnTextColor);
        mHeaderTextPaint.setTextAlign(Paint.Align.CENTER);
        mHeaderTextPaint.setTextSize(mTextSize);
        mHeaderTextPaint.getTextBounds("00 PM", 0, "00 PM".length(), rect);
        mHeaderTextHeight = rect.height();
        mHeaderTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        // Prepare header background paint.
        mHeaderBackgroundPaint = new Paint();
        mHeaderBackgroundPaint.setColor(mHeaderRowBackgroundColor);

        // Prepare day background color paint.
        mDayBackgroundPaint = new Paint();
        mDayBackgroundPaint.setColor(mDayBackgroundColor);
        mFutureBackgroundPaint = new Paint();
        mFutureBackgroundPaint.setColor(mFutureBackgroundColor);
        mPastBackgroundPaint = new Paint();
        mPastBackgroundPaint.setColor(mPastBackgroundColor);
        mFutureWeekendBackgroundPaint = new Paint();
        mFutureWeekendBackgroundPaint.setColor(mFutureWeekendBackgroundColor);
        mPastWeekendBackgroundPaint = new Paint();
        mPastWeekendBackgroundPaint.setColor(mPastWeekendBackgroundColor);

        // Prepare hour separator color paint.
        mHourSeparatorPaint = new Paint();
        mHourSeparatorPaint.setStyle(Paint.Style.STROKE);
        mHourSeparatorPaint.setStrokeWidth(mHourSeparatorHeight);
        mHourSeparatorPaint.setColor(mHourSeparatorColor);

        // Prepare the "now" line color paint
        mNowLinePaint = new Paint();
        mNowLinePaint.setStrokeWidth(mNowLineThickness);
        mNowLinePaint.setColor(mNowLineColor);

        // Prepare today background color paint.
        mTodayBackgroundPaint = new Paint();
        mTodayBackgroundPaint.setColor(mTodayBackgroundColor);

        // Prepare today header text color paint.
        mTodayHeaderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTodayHeaderTextPaint.setTextAlign(Paint.Align.CENTER);
        mTodayHeaderTextPaint.setTextSize(mTextSize);
        mTodayHeaderTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTodayHeaderTextPaint.setColor(mTodayHeaderTextColor);

        // Prepare event background color.
        mEventBackgroundPaint = new Paint();
        mEventBackgroundPaint.setColor(Color.rgb(174, 208, 238));

        // Prepare header column background color.
        mHeaderColumnBackgroundPaint = new Paint();
        mHeaderColumnBackgroundPaint.setColor(mHeaderColumnBackgroundColor);

        // Prepare event text size and color.
        mEventTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        mEventTextPaint.setStyle(Paint.Style.FILL);
        mEventTextPaint.setColor(mEventTextColor);
        mEventTextPaint.setTextSize(mEventTextSize);

        // Set default event color.

        mDefaultEventColor = Color.parseColor("#9fc6e7");

        //todo might not allow scaling
        mScaleDetector = new ScaleGestureDetector(mContext, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                mIsZooming = false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                mIsZooming = true;
                //goToNearestOrigin();
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mNewHourHeight = Math.round(mHourHeight * detector.getScaleFactor());
                invalidate();
                return true;
            }
        });


        //// TODO: 2017-03-15 set the calculations, and allow recalc

    }

    // fix rotation changes
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mAreDimensionsInvalid = true;
    }

    /**
     * Initialize time column width. Calculate value with all possible hours (supposed widest text).
     */
    private void initTextTimeWidth() {
        mTimeTextWidth = 0;
        for (int i = 0; i < 24; i++) {
            // Measure time string and get max width.
            String time = Integer.toString(i);
            mTimeTextWidth = Math.max(mTimeTextWidth, mTimeTextPaint.measureText(time));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.save();
//        canvas.translate(_xoffset, _yoffset);

        // Draw the header row.
        //drawHeaderRowAndEvents(canvas);

        // Draw the time column and all the axes/separators.
        //drawTimeColumnAndAxes(canvas);



        if (restart || days.isEmpty()) {

            mHourHeight = (int) ((getHeight() - mHeaderHeight)/4);

            getEvents();
            calculateTimeText();
            calculateTimeLines();

            currentDayInt = 0;

            nextDayStart = (float) days.get(currentDayInt).getLines().get(0);

            previousDayStart = 0;


            restart = false;
        }



        drawHeader(canvas, days);

        drawTimeForEachDay(canvas, days);

        drawTimeLines(canvas, days, currentDayInt);






        paintEvents(canvas);


        drawBlockLines(canvas);



//        canvas.restore();
    }

    private void calculateHeaderHeight(){
        //Make sure the header is the right size (depends on AllDay events)
        //todo eliminate all day events, will be described on event and will handle differently
//        boolean containsAllDayEvent = false;
//        if (mEventRects != null && mEventRects.size() > 0) {
//            for (int dayNumber = 0;
//                 dayNumber < mNumberOfVisibleDays;
//                 dayNumber++) {
//                Calendar day = (Calendar) getFirstVisibleDay().clone();
//                day.add(Calendar.DATE, dayNumber);
//                for (int i = 0; i < mEventRects.size(); i++) {
//
//                    if (isSameDay(mEventRects.get(i).event.getStartTime(), day) && mEventRects.get(i).event.isAllDay()) {
//                        containsAllDayEvent = true;
//                        break;
//                    }
//                }
//                if(containsAllDayEvent){
//                    break;
//                }
//            }
//        }
//        if(containsAllDayEvent) {
//            mHeaderHeight = mHeaderTextHeight + (mAllDayEventHeight + mHeaderMarginBottom);
//        }
//        else{
            //todo this is important but will be a constant independent of text width
            mHeaderHeight = mHeaderTextHeight;
//        }
    }

    //made a copy
    private void drawTimeColumnAndAxes(Canvas canvas) {
        // Draw the background color for the header column.
        canvas.drawRect(0, 75, mHeaderColumnWidth, getHeight(), mHeaderColumnBackgroundPaint);

        // Clip to paint in left column only.
        canvas.clipRect(0, 75, mHeaderColumnWidth, getHeight(), Region.Op.REPLACE);

        //todo would limit this to the time range of the current day
        for (int i = 0; i < 24; i++) {
            float top = mHeaderHeight + mHeaderRowPadding * 2 + mCurrentOrigin.y + mHourHeight * i + mHeaderMarginBottom;

            // Draw the text if its y position is not outside of the visible area. The pivot point of the text is the point at the bottom-right corner.
            String time = getDateTimeInterpreter().interpretTime(i);
            if (i == 12) {
                time = "12 PM";
            }
            if (time == null)
                throw new IllegalStateException("A DateTimeInterpreter must not return null time");
            //todo work on this, needs to
            if (top < getHeight()) canvas.drawText(time, mTimeTextWidth + mHeaderColumnPadding, top + mTimeTextHeight, mTimeTextPaint);
        }
    }
//// TODO: 2017-01-28 fix this
    private void drawHeaderRowAndEvents(Canvas canvas) {
        // Calculate the available width for each day.
        mHeaderColumnWidth = mTimeTextWidth + mHeaderColumnPadding *2;
        mWidthPerDay = dayWidth - mHeaderColumnWidth - mColumnGap * (mNumberOfVisibleDays - 1);
        mWidthPerDay = mWidthPerDay/mNumberOfVisibleDays;

        calculateHeaderHeight(); //Make sure the header is the right size (depends on AllDay events)

        Calendar today = today();

        if (mAreDimensionsInvalid) {
            mEffectiveMinHourHeight= Math.max(mMinHourHeight, (int) ((getHeight() - mHeaderHeight - mHeaderRowPadding * 2 - mHeaderMarginBottom) / 24));

            mAreDimensionsInvalid = false;
            if(mScrollToDay != null)
                goToDate(mScrollToDay);

            mAreDimensionsInvalid = false;
            if(mScrollToHour >= 0)
                goToHour(mScrollToHour);

            mScrollToDay = null;
            mScrollToHour = -1;
            mAreDimensionsInvalid = false;
        }
        if (mIsFirstDraw){
            mIsFirstDraw = false;

            // If the week view is being drawn for the first time, then consider the first day of the week.
            if(mNumberOfVisibleDays >= 7 && today.get(Calendar.DAY_OF_WEEK) != mFirstDayOfWeek && mShowFirstDayOfWeekFirst) {
                int difference = (today.get(Calendar.DAY_OF_WEEK) - mFirstDayOfWeek);
                mCurrentOrigin.x += (mWidthPerDay + mColumnGap) * difference;
            }
        }

        // Calculate the new height due to the zooming.
        if (mNewHourHeight > 0){
            if (mNewHourHeight < mEffectiveMinHourHeight)
                mNewHourHeight = mEffectiveMinHourHeight;
            else if (mNewHourHeight > mMaxHourHeight)
                mNewHourHeight = mMaxHourHeight;

            mCurrentOrigin.y = (mCurrentOrigin.y/mHourHeight)*mNewHourHeight;
            mHourHeight = mNewHourHeight;
            mNewHourHeight = -1;
        }

        // If the new mCurrentOrigin.y is invalid, make it valid.
        if (mCurrentOrigin.y < getHeight() - mHourHeight * 24 - mHeaderHeight - mHeaderRowPadding * 2 - mHeaderMarginBottom - mTimeTextHeight/2)
            mCurrentOrigin.y = getHeight() - mHourHeight * 24 - mHeaderHeight - mHeaderRowPadding * 2 - mHeaderMarginBottom - mTimeTextHeight/2;

        // Don't put an "else if" because it will trigger a glitch when completely zoomed out and
        // scrolling vertically.
        if (mCurrentOrigin.y > 0) {
            mCurrentOrigin.y = 0;
        }

        // Consider scroll offset.
        int leftDaysWithGaps = (int) -(Math.ceil(mCurrentOrigin.x / (mWidthPerDay + mColumnGap)));
        float startFromPixel = mCurrentOrigin.x + (mWidthPerDay + mColumnGap) * leftDaysWithGaps +
                mHeaderColumnWidth;
        float startPixel = startFromPixel;

        // Prepare to iterate for each day.
        Calendar day = (Calendar) today.clone();
        day.add(Calendar.HOUR, 6);

        // Prepare to iterate for each hour to draw the hour lines.
        int lineCount = (int) ((getHeight() - mHeaderHeight - mHeaderRowPadding * 2 -
                mHeaderMarginBottom) / mHourHeight) + 1;
        lineCount = (lineCount) * (mNumberOfVisibleDays+1);
        float[] hourLines = new float[lineCount * 4];

        // Clear the cache for event rectangles.
        //todo don't want to clear the cash of events
        if (mEventRects != null) {
            for (EventRect eventRect: mEventRects) {
                eventRect.rectF = null;
            }
        }

        // Clip to paint events only.
        canvas.clipRect(mHeaderColumnWidth, mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom + mTimeTextHeight/2, getWidth(), getHeight(), Region.Op.REPLACE);

        // Iterate through each day.
        Calendar oldFirstVisibleDay = mFirstVisibleDay;
        mFirstVisibleDay = (Calendar) today.clone();
        mFirstVisibleDay.add(Calendar.DATE, -(Math.round(mCurrentOrigin.x / (mWidthPerDay + mColumnGap))));

        //// TODO: 2017-02-02 might need
        if(!mFirstVisibleDay.equals(oldFirstVisibleDay) && mScrollListener != null){
            mScrollListener.onFirstVisibleDayChanged(mFirstVisibleDay, oldFirstVisibleDay);
        }
        for (int dayNumber = leftDaysWithGaps + 1;
             dayNumber <= leftDaysWithGaps + mNumberOfVisibleDays + 1;
             dayNumber++) {

            // Check if the day is today.
            day = (Calendar) today.clone();
            mLastVisibleDay = (Calendar) day.clone();
            day.add(Calendar.DATE, dayNumber - 1);
            mLastVisibleDay.add(Calendar.DATE, dayNumber - 2);
            boolean sameDay = isSameDay(day, today);

            // Get more events if necessary. We want to store the events 3 months beforehand. Get
            // events only when it is the first iteration of the loop.

            //// TODO: 2017-01-30 could keep this but may want to load all events always at least for now
            if (mEventRects == null || mRefreshEvents ||
                    (dayNumber == leftDaysWithGaps + 1 && mFetchedPeriod != (int) mWeekViewLoader.toWeekViewPeriodIndex(day) &&
                              abs(mFetchedPeriod - mWeekViewLoader.toWeekViewPeriodIndex(day)) > 0.5)) {
                getMoreEvents(day);
                mRefreshEvents = false;
            }

            // Draw background color for each day.
            float start =  (startPixel < mHeaderColumnWidth ? mHeaderColumnWidth : startPixel);
            if (mWidthPerDay + startPixel - start > 0){

                //for background
                canvas.drawRect(start, mHeaderHeight + mHeaderRowPadding * 2 + mTimeTextHeight / 2 + mHeaderMarginBottom, startPixel + mWidthPerDay, getHeight(), mTodayBackgroundPaint);

            }

            // Prepare the separator lines for hours.
            //// TODO: 2017-01-30 fix this to allow for many days, new loop
            int i = 0;
            for (int hourNumber = 0; hourNumber < 24; hourNumber++) {
                //todo add a modifier to this that adds a set padding to show that its a new day multiplied by the number of new days
                float top = mHeaderHeight + mHeaderRowPadding * 2 + mCurrentOrigin.y + mHourHeight * hourNumber + mTimeTextHeight/2 + mHeaderMarginBottom;
                if (top > mHeaderHeight + mHeaderRowPadding * 2 + mTimeTextHeight/2 + mHeaderMarginBottom - mHourSeparatorHeight && top < getHeight() && startPixel + mWidthPerDay - start > 0){
                    hourLines[i * 4] = start;
                    hourLines[i * 4 + 1] = top;
                    hourLines[i * 4 + 2] = startPixel + mWidthPerDay;
                    hourLines[i * 4 + 3] = top;
                    i++;
                }
            }

            // Draw the lines for hours.
            canvas.drawLines(hourLines, mHourSeparatorPaint);

            // Draw the events.
            drawEvents(day, startPixel, canvas);

            // Draw the line at the current time.
            if (mShowNowLine && sameDay){
                float startY = mHeaderHeight + mHeaderRowPadding * 2 + mTimeTextHeight/2 + mHeaderMarginBottom + mCurrentOrigin.y;
                Calendar now = Calendar.getInstance();
                float beforeNow = (now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE)/60.0f) * mHourHeight;
                canvas.drawLine(start, startY + beforeNow, startPixel + mWidthPerDay, startY + beforeNow, mNowLinePaint);
            }

            // In the next iteration, start from the next day.
            //// TODO: 2017-01-30 fix this, will want a running height change per day, not width
            startPixel += mWidthPerDay + mColumnGap;
        }

        // Hide everything in the first cell (top left corner).
        canvas.clipRect(0, 0, mTimeTextWidth + mHeaderColumnPadding * 2, mHeaderHeight + mHeaderRowPadding * 2, Region.Op.REPLACE);
        canvas.drawRect(0, 0, mTimeTextWidth + mHeaderColumnPadding * 2, mHeaderHeight + mHeaderRowPadding * 2, mHeaderBackgroundPaint);

        // Clip to paint header row only.
        canvas.clipRect(mHeaderColumnWidth, 0, getWidth(), mHeaderHeight + mHeaderRowPadding * 2, Region.Op.REPLACE);

        // Draw the header background.
        canvas.drawRect(0, 0, getWidth(), mHeaderHeight + mHeaderRowPadding * 2, mHeaderBackgroundPaint);

        // Draw the header row texts.
        startPixel = startFromPixel;
        for (int dayNumber=leftDaysWithGaps+1; dayNumber <= leftDaysWithGaps + mNumberOfVisibleDays + 1; dayNumber++) {
            // Check if the day is today.
            day = (Calendar) today.clone();
            day.add(Calendar.DATE, dayNumber - 1);
            boolean sameDay = isSameDay(day, today);

            // Draw the day labels.
            //todo may want to change this, dont know where day text will be currently
            String dayLabel = getDateTimeInterpreter().interpretDate(day);
            if (dayLabel == null)
                throw new IllegalStateException("A DateTimeInterpreter must not return null date");
            canvas.drawText(dayLabel, startPixel + mWidthPerDay / 2, mHeaderTextHeight + mHeaderRowPadding, sameDay ? mTodayHeaderTextPaint : mHeaderTextPaint);
            //drawAllDayEvents(day, startPixel, canvas);
            startPixel += mWidthPerDay + mColumnGap;
        }

    }

    public void paintHeader(Canvas canvas) {

        // Clip to paint header row only.
        canvas.clipRect(mHeaderColumnWidth, 0, getWidth(), mHeaderHeight + mHeaderRowPadding * 2, Region.Op.REPLACE);

        // Draw the header background.
        canvas.drawRect(0, 0, getWidth(), mHeaderHeight + mHeaderRowPadding * 2, mHeaderBackgroundPaint);



    }

    /**
     * Get the time and date where the user clicked on.
     * @param x The x position of the touch event.
     * @param y The y position of the touch event.
     * @return The time and date at the clicked position.
     */
    //todo may find this usefull when searching through events on click, but if we use would have to change it up
    private Calendar getTimeFromPoint(float x, float y){
        int leftDaysWithGaps = (int) -(Math.ceil(mCurrentOrigin.x / (mWidthPerDay + mColumnGap)));
        float startPixel = mCurrentOrigin.x + (mWidthPerDay + mColumnGap) * leftDaysWithGaps +
                mHeaderColumnWidth;
        for (int dayNumber = leftDaysWithGaps + 1;
             dayNumber <= leftDaysWithGaps + mNumberOfVisibleDays + 1;
             dayNumber++) {
            float start =  (startPixel < mHeaderColumnWidth ? mHeaderColumnWidth : startPixel);
            if (mWidthPerDay + startPixel - start > 0 && x > start && x < startPixel + mWidthPerDay){
                Calendar day = today();
                day.add(Calendar.DATE, dayNumber - 1);
                float pixelsFromZero = y - mCurrentOrigin.y - mHeaderHeight
                        - mHeaderRowPadding * 2 - mTimeTextHeight/2 - mHeaderMarginBottom;
                int hour = (int)(pixelsFromZero / mHourHeight);
                int minute = (int) (60 * (pixelsFromZero - hour * mHourHeight) / mHourHeight);
                day.add(Calendar.HOUR, hour);
                day.set(Calendar.MINUTE, minute);
                return day;
            }
            startPixel += mWidthPerDay + mColumnGap;
        }
        return null;
    }

    /**
     * Draw all the events of a particular day.
     * @param date The day.
     * @param startFromPixel The left position of the day area. The events will never go any left from this value.
     * @param canvas The canvas to draw upon.
     */
    //todo have to math this to draw events in the correct place, considering multiple events
    //todo going to be differnt calculations here, involving distance, title length, #emojis
    private void drawEvents(Calendar date, float startFromPixel, Canvas canvas) {
        if (mEventRects != null && mEventRects.size() > 0) {
            for (int i = 0; i < mEventRects.size(); i++) {
                if (isSameDay(mEventRects.get(i).event.getStartTime(), date) && !mEventRects.get(i).event.isAllDay()){

                    // Calculate top.
                    float top = mHourHeight * 24 * mEventRects.get(i).top / 1440 + mCurrentOrigin.y + mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom + mTimeTextHeight/2 + mEventMarginVertical;
                    // Calculate bottom.
                    float bottom = mEventRects.get(i).bottom;
                    bottom = mHourHeight * 24 * bottom / 1440 + mCurrentOrigin.y + mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom + mTimeTextHeight/2 - mEventMarginVertical;

                    // Calculate left and right.
                    float left = startFromPixel + mEventRects.get(i).left * mWidthPerDay;
                    if (left < startFromPixel)
                        left += mOverlappingEventGap;
                    float right = left + mEventRects.get(i).width * mWidthPerDay;
                    if (right < startFromPixel + mWidthPerDay)
                        right -= mOverlappingEventGap;

                    // Draw the event and the event name on top of it.
                    if (left < right &&
                            left < getWidth() &&
                            top < getHeight() &&
                            right > mHeaderColumnWidth &&
                            bottom > mHeaderHeight + mHeaderRowPadding * 2 + mTimeTextHeight / 2 + mHeaderMarginBottom
                            ) {
                        mEventRects.get(i).rectF = new RectF(left, top, right, bottom);
                        mEventBackgroundPaint.setColor(mEventRects.get(i).event.getColor() == 0 ? mDefaultEventColor : mEventRects.get(i).event.getColor());
                        canvas.drawRoundRect(mEventRects.get(i).rectF, mEventCornerRadius, mEventCornerRadius, mEventBackgroundPaint);
                        drawEventTitle(mEventRects.get(i).event, mEventRects.get(i).rectF, canvas, top, left);
                    }
                    else
                        mEventRects.get(i).rectF = null;
                }
            }
        }
    }

    /**
     * Draw all the Allday-events of a particular day.
     * @param date The day.
     * @param startFromPixel The left position of the day area. The events will never go any left from this value.
     * @param canvas The canvas to draw upon.
     */
    //todo may not need, or do it in another way
    private void drawAllDayEvents(Calendar date, float startFromPixel, Canvas canvas) {
        if (mEventRects != null && mEventRects.size() > 0) {
            for (int i = 0; i < mEventRects.size(); i++) {
                if (isSameDay(mEventRects.get(i).event.getStartTime(), date) && mEventRects.get(i).event.isAllDay()){

                    // Calculate top.
                    float top = mHeaderRowPadding * 2 + mHeaderMarginBottom +  + mTimeTextHeight/2 + mEventMarginVertical;

                    // Calculate bottom.
                    float bottom = top + mEventRects.get(i).bottom;

                    // Calculate left and right.
                    float left = startFromPixel + mEventRects.get(i).left * mWidthPerDay;
                    if (left < startFromPixel)
                        left += mOverlappingEventGap;
                    float right = left + mEventRects.get(i).width * mWidthPerDay;
                    if (right < startFromPixel + mWidthPerDay)
                        right -= mOverlappingEventGap;

                    // Draw the event and the event name on top of it.
                    if (left < right &&
                            left < getWidth() &&
                            top < getHeight() &&
                            right > mHeaderColumnWidth &&
                            bottom > 0
                            ) {
                        mEventRects.get(i).rectF = new RectF(left, top, right, bottom);
                        mEventBackgroundPaint.setColor(mEventRects.get(i).event.getColor() == 0 ? mDefaultEventColor : mEventRects.get(i).event.getColor());
                        canvas.drawRoundRect(mEventRects.get(i).rectF, mEventCornerRadius, mEventCornerRadius, mEventBackgroundPaint);
                        drawEventTitle(mEventRects.get(i).event, mEventRects.get(i).rectF, canvas, top, left);
                    }
                    else
                        mEventRects.get(i).rectF = null;
                }
            }
        }
    }


    /**
     * Draw the name of the event on top of the event rectangle.
     * @param event The event of which the title (and location) should be drawn.
     * @param rect The rectangle on which the text is to be drawn.
     * @param canvas The canvas to draw upon.
     * @param originalTop The original top position of the rectangle. The rectangle may have some of its portion outside of the visible area.
     * @param originalLeft The original left position of the rectangle. The rectangle may have some of its portion outside of the visible area.
     */
    //todo ill probobly not do the drawing here
    private void drawEventTitle(WeekViewEvent event, RectF rect, Canvas canvas, float originalTop, float originalLeft) {
        if (rect.right - rect.left - mEventPadding * 2 < 0) return;
        if (rect.bottom - rect.top - mEventPadding * 2 < 0) return;

        // Prepare the name of the event.
        SpannableStringBuilder bob = new SpannableStringBuilder();
        if (event.getName() != null) {
            bob.append(event.getName());
            bob.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, bob.length(), 0);
            bob.append(' ');
        }

        // Prepare the location of the event.
        if (event.getLocation() != null) {
            bob.append(event.getLocation());
        }

        int availableHeight = (int) (rect.bottom - originalTop - mEventPadding * 2);
        int availableWidth = (int) (rect.right - originalLeft - mEventPadding * 2);

        // Get text dimensions.

        StaticLayout textLayout = new StaticLayout(bob, mEventTextPaint, availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        int lineHeight = textLayout.getHeight() / textLayout.getLineCount();

        if (availableHeight >= lineHeight) {
            // Calculate available number of line counts.
            int availableLineCount = availableHeight / lineHeight;
            //todo definatly no shortening of text
            do {
                // Ellipsize text to fit into event rect.
                textLayout = new StaticLayout(TextUtils.ellipsize(bob, mEventTextPaint, availableLineCount * availableWidth, TextUtils.TruncateAt.END), mEventTextPaint, (int) (rect.right - originalLeft - mEventPadding * 2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

                // Reduce line count.
                availableLineCount--;

                // Repeat until text is short enough.
            } while (textLayout.getHeight() > availableHeight);

            // Draw text.
            canvas.save();
            canvas.translate(originalLeft + mEventPadding, originalTop + mEventPadding);
            textLayout.draw(canvas);
            canvas.restore();
        }
    }


    /**
     * A class to hold reference to the events and their visual representation. An EventRect is
     * actually the rectangle that is drawn on the calendar for a given event. There may be more
     * than one rectangle for a single event (an event that expands more than one day). In that
     * case two instances of the EventRect will be used for a single event. The given event will be
     * stored in "originalEvent". But the event that corresponds to rectangle the rectangle
     * instance will be stored in "event".
     */
    private class EventRect {
        public WeekViewEvent event;
        public WeekViewEvent originalEvent;
        public RectF rectF;
        public float left;
        public float width;
        public float top;
        public float bottom;

        /**
         * Create a new instance of event rect. An EventRect is actually the rectangle that is drawn
         * on the calendar for a given event. There may be more than one rectangle for a single
         * event (an event that expands more than one day). In that case two instances of the
         * EventRect will be used for a single event. The given event will be stored in
         * "originalEvent". But the event that corresponds to rectangle the rectangle instance will
         * be stored in "event".
         * @param event Represents the event which this instance of rectangle represents.
         * @param originalEvent The original event that was passed by the user.
         * @param rectF The rectangle.
         */
        public EventRect(WeekViewEvent event, WeekViewEvent originalEvent, RectF rectF) {
            this.event = event;
            this.rectF = rectF;
            this.originalEvent = originalEvent;
        }
    }


    /**
     * Gets more events of one/more month(s) if necessary. This method is called when the user is
     * scrolling the week view. The week view stores the events of three months: the visible month,
     * the previous month, the next month.
     * @param day The day where the user is currently is.
     */
    //todo will change this to load all event, no checks
    private void getMoreEvents(Calendar day) {

        // Get more events if the month is changed.
        if (mEventRects == null)
            mEventRects = new ArrayList<EventRect>();
        if (mWeekViewLoader == null && !isInEditMode())
            throw new IllegalStateException("You must provide a MonthChangeListener");

        // If a refresh was requested then reset some variables.
        if (mRefreshEvents) {
            mEventRects.clear();
            mPreviousPeriodEvents = null;
            mCurrentPeriodEvents = null;
            mNextPeriodEvents = null;
            mFetchedPeriod = -1;
        }

        if (mWeekViewLoader != null){
            int periodToFetch = (int) mWeekViewLoader.toWeekViewPeriodIndex(day);
            if (!isInEditMode() && (mFetchedPeriod < 0 || mFetchedPeriod != periodToFetch || mRefreshEvents)) {
                List<? extends WeekViewEvent> previousPeriodEvents = null;
                List<? extends WeekViewEvent> currentPeriodEvents = null;
                List<? extends WeekViewEvent> nextPeriodEvents = null;

                if (mPreviousPeriodEvents != null && mCurrentPeriodEvents != null && mNextPeriodEvents != null){
                    if (periodToFetch == mFetchedPeriod-1){
                        currentPeriodEvents = mPreviousPeriodEvents;
                        nextPeriodEvents = mCurrentPeriodEvents;
                    }
                    else if (periodToFetch == mFetchedPeriod){
                        previousPeriodEvents = mPreviousPeriodEvents;
                        currentPeriodEvents = mCurrentPeriodEvents;
                        nextPeriodEvents = mNextPeriodEvents;
                    }
                    else if (periodToFetch == mFetchedPeriod+1){
                        previousPeriodEvents = mCurrentPeriodEvents;
                        currentPeriodEvents = mNextPeriodEvents;
                    }
                }
                if (currentPeriodEvents == null)
                    currentPeriodEvents = mWeekViewLoader.onLoad(periodToFetch);
                if (previousPeriodEvents == null)
                    previousPeriodEvents = mWeekViewLoader.onLoad(periodToFetch-1);
                if (nextPeriodEvents == null)
                    nextPeriodEvents = mWeekViewLoader.onLoad(periodToFetch+1);


                // Clear events.
                mEventRects.clear();
                sortAndCacheEvents(previousPeriodEvents);
                sortAndCacheEvents(currentPeriodEvents);
                sortAndCacheEvents(nextPeriodEvents);
                calculateHeaderHeight();

                mPreviousPeriodEvents = previousPeriodEvents;
                mCurrentPeriodEvents = currentPeriodEvents;
                mNextPeriodEvents = nextPeriodEvents;
                mFetchedPeriod = periodToFetch;
            }
        }

        // Prepare to calculate positions of each events.
        List<EventRect> tempEvents = mEventRects;
        mEventRects = new ArrayList<EventRect>();

        // Iterate through each day with events to calculate the position of the events.
        while (tempEvents.size() > 0) {
            ArrayList<EventRect> eventRects = new ArrayList<>(tempEvents.size());

            // Get first event for a day.
            EventRect eventRect1 = tempEvents.remove(0);
            eventRects.add(eventRect1);

            int i = 0;
            while (i < tempEvents.size()) {
                // Collect all other events for same day.
                EventRect eventRect2 = tempEvents.get(i);
                if (isSameDay(eventRect1.event.getStartTime(), eventRect2.event.getStartTime())) {
                    tempEvents.remove(i);
                    eventRects.add(eventRect2);
                } else {
                    i++;
                }
            }
            computePositionOfEvents(eventRects);
        }
    }

    /**
     * Cache the event for smooth scrolling functionality.
     * @param event The event to cache.
     */
    private void cacheEvent(WeekViewEvent event) {
        if(event.getStartTime().compareTo(event.getEndTime()) >= 0)
            return;
        List<WeekViewEvent> splitedEvents = event.splitWeekViewEvents();
        for(WeekViewEvent splitedEvent: splitedEvents){
            mEventRects.add(new EventRect(splitedEvent, event, null));
        }
    }

    /**
     * Sort and cache events.
     * @param events The events to be sorted and cached.
     */
    private void sortAndCacheEvents(List<? extends WeekViewEvent> events) {
        sortEvents(events);
        for (WeekViewEvent event : events) {
            cacheEvent(event);
        }
    }

    /**
     * Sorts the events in ascending order.
     * @param events The events to be sorted.
     */
    //todo might be usefull early, then can prioitize
    private void sortEvents(List<? extends WeekViewEvent> events) {
        Collections.sort(events, new Comparator<WeekViewEvent>() {
            @Override
            public int compare(WeekViewEvent event1, WeekViewEvent event2) {
                long start1 = event1.getStartTime().getTimeInMillis();
                long start2 = event2.getStartTime().getTimeInMillis();
                int comparator = start1 > start2 ? 1 : (start1 < start2 ? -1 : 0);
                if (comparator == 0) {
                    long end1 = event1.getEndTime().getTimeInMillis();
                    long end2 = event2.getEndTime().getTimeInMillis();
                    comparator = end1 > end2 ? 1 : (end1 < end2 ? -1 : 0);
                }
                return comparator;
            }
        });
    }

    /**
     * Calculates the left and right positions of each events. This comes handy specially if events
     * are overlapping.
     * @param eventRects The events along with their wrapper class.
     */
    private void computePositionOfEvents(List<EventRect> eventRects) {
        // Make "collision groups" for all events that collide with others.
        List<List<EventRect>> collisionGroups = new ArrayList<List<EventRect>>();
        for (EventRect eventRect : eventRects) {
            boolean isPlaced = false;

            outerLoop:
            for (List<EventRect> collisionGroup : collisionGroups) {
                for (EventRect groupEvent : collisionGroup) {
                    if (isEventsCollide(groupEvent.event, eventRect.event) && groupEvent.event.isAllDay() == eventRect.event.isAllDay()) {
                        collisionGroup.add(eventRect);
                        isPlaced = true;
                        break outerLoop;
                    }
                }
            }

            if (!isPlaced) {
                List<EventRect> newGroup = new ArrayList<EventRect>();
                newGroup.add(eventRect);
                collisionGroups.add(newGroup);
            }
        }

        for (List<EventRect> collisionGroup : collisionGroups) {
            expandEventsToMaxWidth(collisionGroup);
        }
    }

    /**
     * Expands all the events to maximum possible width. The events will try to occupy maximum
     * space available horizontally.
     * @param collisionGroup The group of events which overlap with each other.
     */
    private void expandEventsToMaxWidth(List<EventRect> collisionGroup) {
        // Expand the events to maximum possible width.
        List<List<EventRect>> columns = new ArrayList<List<EventRect>>();
        columns.add(new ArrayList<EventRect>());
        for (EventRect eventRect : collisionGroup) {
            boolean isPlaced = false;
            for (List<EventRect> column : columns) {
                if (column.size() == 0) {
                    column.add(eventRect);
                    isPlaced = true;
                }
                else if (!isEventsCollide(eventRect.event, column.get(column.size()-1).event)) {
                    column.add(eventRect);
                    isPlaced = true;
                    break;
                }
            }
            if (!isPlaced) {
                List<EventRect> newColumn = new ArrayList<EventRect>();
                newColumn.add(eventRect);
                columns.add(newColumn);
            }
        }


        // Calculate left and right position for all the events.
        // Get the maxRowCount by looking in all columns.
        int maxRowCount = 0;
        for (List<EventRect> column : columns){
            maxRowCount = Math.max(maxRowCount, column.size());
        }
        for (int i = 0; i < maxRowCount; i++) {
            // Set the left and right values of the event.
            float j = 0;
            for (List<EventRect> column : columns) {
                if (column.size() >= i+1) {
                    EventRect eventRect = column.get(i);
                    eventRect.width = 1f / columns.size();
                    eventRect.left = j / columns.size();
                    if(!eventRect.event.isAllDay()) {
                        eventRect.top = eventRect.event.getStartTime().get(Calendar.HOUR_OF_DAY) * 60 + eventRect.event.getStartTime().get(Calendar.MINUTE);
                        eventRect.bottom = eventRect.event.getEndTime().get(Calendar.HOUR_OF_DAY) * 60 + eventRect.event.getEndTime().get(Calendar.MINUTE);
                    }
                    else{
                        eventRect.top = 0;
                        eventRect.bottom = mAllDayEventHeight;
                    }
                    mEventRects.add(eventRect);
                }
                j++;
            }
        }
    }


    /**
     * Checks if two events overlap.
     * @param event1 The first event.
     * @param event2 The second event.
     * @return true if the events overlap.
     */
    private boolean isEventsCollide(WeekViewEvent event1, WeekViewEvent event2) {
        long start1 = event1.getStartTime().getTimeInMillis();
        long end1 = event1.getEndTime().getTimeInMillis();
        long start2 = event2.getStartTime().getTimeInMillis();
        long end2 = event2.getEndTime().getTimeInMillis();
        return !((start1 >= end2) || (end1 <= start2));
    }


    /**
     * Checks if time1 occurs after (or at the same time) time2.
     * @param time1 The time to check.
     * @param time2 The time to check against.
     * @return true if time1 and time2 are equal or if time1 is after time2. Otherwise false.
     */
    private boolean isTimeAfterOrEquals(Calendar time1, Calendar time2) {
        return !(time1 == null || time2 == null) && time1.getTimeInMillis() >= time2.getTimeInMillis();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        mAreDimensionsInvalid = true;
    }

    /////////////////////////////////////////////////////////////////
    //
    //      Functions related to setting and getting the properties.
    //
    /////////////////////////////////////////////////////////////////

    public void setOnEventClickListener (EventClickListener listener) {
        this.mEventClickListener = listener;
    }

    public EventClickListener getEventClickListener() {
        return mEventClickListener;
    }

    public @Nullable
    MonthLoader.MonthChangeListener getMonthChangeListener() {
        if (mWeekViewLoader instanceof MonthLoader)
            return ((MonthLoader) mWeekViewLoader).getOnMonthChangeListener();
        return null;
    }

    public void setMonthChangeListener(MonthLoader.MonthChangeListener monthChangeListener) {
        this.mWeekViewLoader = new MonthLoader(monthChangeListener);
    }

    /**
     * Get event loader in the week view. Event loaders define the  interval after which the events
     * are loaded in week view. For a MonthLoader events are loaded for every month. You can define
     * your custom event loader by extending WeekViewLoader.
     * @return The event loader.
     */
    public WeekViewLoader getWeekViewLoader(){
        return mWeekViewLoader;
    }

    /**
     * Set event loader in the week view. For example, a MonthLoader. Event loaders define the
     * interval after which the events are loaded in week view. For a MonthLoader events are loaded
     * for every month. You can define your custom event loader by extending WeekViewLoader.
     * @param loader The event loader.
     */
    public void setWeekViewLoader(WeekViewLoader loader){
        this.mWeekViewLoader = loader;
    }

    public EventLongPressListener getEventLongPressListener() {
        return mEventLongPressListener;
    }

    public void setEventLongPressListener(EventLongPressListener eventLongPressListener) {
        this.mEventLongPressListener = eventLongPressListener;
    }

    public void setEmptyViewClickListener(EmptyViewClickListener emptyViewClickListener){
        this.mEmptyViewClickListener = emptyViewClickListener;
    }

    public EmptyViewClickListener getEmptyViewClickListener(){
        return mEmptyViewClickListener;
    }

    public void setEmptyViewLongPressListener(EmptyViewLongPressListener emptyViewLongPressListener){
        this.mEmptyViewLongPressListener = emptyViewLongPressListener;
    }

    public EmptyViewLongPressListener getEmptyViewLongPressListener(){
        return mEmptyViewLongPressListener;
    }

    public void setScrollListener(ScrollListener scrolledListener){
        this.mScrollListener = scrolledListener;
    }

    public ScrollListener getScrollListener(){
        return mScrollListener;
    }

    /**
     * Get the interpreter which provides the text to show in the header column and the header row.
     * @return The date, time interpreter.
     */
    public DateTimeInterpreter getDateTimeInterpreter() {
        if (mDateTimeInterpreter == null) {
            mDateTimeInterpreter = new DateTimeInterpreter() {
                @Override
                public String interpretDate(Calendar date) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM dd", Locale.getDefault());
                        return sdf.format(date.getTime()).toUpperCase();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "";
                    }
                }

                @Override
                public String interpretTime(int hour) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, 0);

                    try {
                        SimpleDateFormat sdf = DateFormat.is24HourFormat(getContext()) ? new SimpleDateFormat("HH:mm", Locale.getDefault()) : new SimpleDateFormat("hh a", Locale.getDefault());
                        return sdf.format(calendar.getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "";
                    }
                }
            };
        }
        return mDateTimeInterpreter;
    }

    /**
     * Set the interpreter which provides the text to show in the header column and the header row.
     * @param dateTimeInterpreter The date, time interpreter.
     */
    public void setDateTimeInterpreter(DateTimeInterpreter dateTimeInterpreter){
        this.mDateTimeInterpreter = dateTimeInterpreter;

        // Refresh time column width.
        initTextTimeWidth();
    }


    /**
     * Get the number of visible days in a week.
     * @return The number of visible days in a week.
     */
    public int getNumberOfVisibleDays() {
        return mNumberOfVisibleDays;
    }

    /**
     * Set the number of visible days in a week.
     * @param numberOfVisibleDays The number of visible days in a week.
     */
    public void setNumberOfVisibleDays(int numberOfVisibleDays) {
        this.mNumberOfVisibleDays = numberOfVisibleDays;
        mCurrentOrigin.x = 0;
        mCurrentOrigin.y = 0;
        invalidate();
    }

    public int getHourHeight() {
        return mHourHeight;
    }

    public void setHourHeight(int hourHeight) {
        mNewHourHeight = hourHeight;
        invalidate();
    }

    public int getColumnGap() {
        return mColumnGap;
    }

    public void setColumnGap(int columnGap) {
        mColumnGap = columnGap;
        invalidate();
    }

    public int getFirstDayOfWeek() {
        return mFirstDayOfWeek;
    }

    /**
     * Set the first day of the week. First day of the week is used only when the week view is first
     * drawn. It does not of any effect after user starts scrolling horizontally.
     * <p>
     *     <b>Note:</b> This method will only work if the week view is set to display more than 6 days at
     *     once.
     * </p>
     * @param firstDayOfWeek The supported values are {@link java.util.Calendar#SUNDAY},
     * {@link java.util.Calendar#MONDAY}, {@link java.util.Calendar#TUESDAY},
     * {@link java.util.Calendar#WEDNESDAY}, {@link java.util.Calendar#THURSDAY},
     * {@link java.util.Calendar#FRIDAY}.
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        mFirstDayOfWeek = firstDayOfWeek;
        invalidate();
    }

    public boolean isShowFirstDayOfWeekFirst() {
        return mShowFirstDayOfWeekFirst;
    }

    public void setShowFirstDayOfWeekFirst(boolean show) {
        mShowFirstDayOfWeekFirst = show;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
        mTodayHeaderTextPaint.setTextSize(mTextSize);
        mHeaderTextPaint.setTextSize(mTextSize);
        mTimeTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public int getHeaderColumnPadding() {
        return mHeaderColumnPadding;
    }

    public void setHeaderColumnPadding(int headerColumnPadding) {
        mHeaderColumnPadding = headerColumnPadding;
        invalidate();
    }

    public int getHeaderColumnTextColor() {
        return mHeaderColumnTextColor;
    }

    public void setHeaderColumnTextColor(int headerColumnTextColor) {
        mHeaderColumnTextColor = headerColumnTextColor;
        mHeaderTextPaint.setColor(mHeaderColumnTextColor);
        mTimeTextPaint.setColor(mHeaderColumnTextColor);
        invalidate();
    }

    public int getHeaderRowPadding() {
        return mHeaderRowPadding;
    }

    public void setHeaderRowPadding(int headerRowPadding) {
        mHeaderRowPadding = headerRowPadding;
        invalidate();
    }

    public int getHeaderRowBackgroundColor() {
        return mHeaderRowBackgroundColor;
    }

    public void setHeaderRowBackgroundColor(int headerRowBackgroundColor) {
        mHeaderRowBackgroundColor = headerRowBackgroundColor;
        mHeaderBackgroundPaint.setColor(mHeaderRowBackgroundColor);
        invalidate();
    }

    public int getDayBackgroundColor() {
        return mDayBackgroundColor;
    }

    public void setDayBackgroundColor(int dayBackgroundColor) {
        mDayBackgroundColor = dayBackgroundColor;
        mDayBackgroundPaint.setColor(mDayBackgroundColor);
        invalidate();
    }

    public int getHourSeparatorColor() {
        return mHourSeparatorColor;
    }

    public void setHourSeparatorColor(int hourSeparatorColor) {
        mHourSeparatorColor = hourSeparatorColor;
        mHourSeparatorPaint.setColor(mHourSeparatorColor);
        invalidate();
    }

    public int getTodayBackgroundColor() {
        return mTodayBackgroundColor;
    }

    public void setTodayBackgroundColor(int todayBackgroundColor) {
        mTodayBackgroundColor = todayBackgroundColor;
        mTodayBackgroundPaint.setColor(mTodayBackgroundColor);
        invalidate();
    }

    public int getHourSeparatorHeight() {
        return mHourSeparatorHeight;
    }

    public void setHourSeparatorHeight(int hourSeparatorHeight) {
        mHourSeparatorHeight = hourSeparatorHeight;
        mHourSeparatorPaint.setStrokeWidth(mHourSeparatorHeight);
        invalidate();
    }

    public int getTodayHeaderTextColor() {
        return mTodayHeaderTextColor;
    }

    public void setTodayHeaderTextColor(int todayHeaderTextColor) {
        mTodayHeaderTextColor = todayHeaderTextColor;
        mTodayHeaderTextPaint.setColor(mTodayHeaderTextColor);
        invalidate();
    }

    public int getEventTextSize() {
        return mEventTextSize;
    }

    public void setEventTextSize(int eventTextSize) {
        mEventTextSize = eventTextSize;
        mEventTextPaint.setTextSize(mEventTextSize);
        invalidate();
    }

    public int getEventTextColor() {
        return mEventTextColor;
    }

    public void setEventTextColor(int eventTextColor) {
        mEventTextColor = eventTextColor;
        mEventTextPaint.setColor(mEventTextColor);
        invalidate();
    }

    public int getEventPadding() {
        return mEventPadding;
    }

    public void setEventPadding(int eventPadding) {
        mEventPadding = eventPadding;
        invalidate();
    }

    public int getHeaderColumnBackgroundColor() {
        return mHeaderColumnBackgroundColor;
    }

    public void setHeaderColumnBackgroundColor(int headerColumnBackgroundColor) {
        mHeaderColumnBackgroundColor = headerColumnBackgroundColor;
        mHeaderColumnBackgroundPaint.setColor(mHeaderColumnBackgroundColor);
        invalidate();
    }

    public int getDefaultEventColor() {
        return mDefaultEventColor;
    }

    public void setDefaultEventColor(int defaultEventColor) {
        mDefaultEventColor = defaultEventColor;
        invalidate();
    }

    /**
     * <b>Note:</b> Use {@link #setDateTimeInterpreter(DateTimeInterpreter)} and
     * {@link #getDateTimeInterpreter()} instead.
     * @return Either long or short day name is being used.
     */
    @Deprecated
    public int getDayNameLength() {
        return mDayNameLength;
    }

    /**
     * Set the length of the day name displayed in the header row. Example of short day names is
     * 'M' for 'Monday' and example of long day names is 'Mon' for 'Monday'.
     * <p>
     *     <b>Note:</b> Use {@link #setDateTimeInterpreter(DateTimeInterpreter)} instead.
     * </p>
     * @param length Supported values are {@link WeekView#LENGTH_SHORT} and
     * {@link WeekView#LENGTH_LONG}.
     */
    @Deprecated
    public void setDayNameLength(int length) {
        if (length != LENGTH_LONG && length != LENGTH_SHORT) {
            throw new IllegalArgumentException("length parameter must be either LENGTH_LONG or LENGTH_SHORT");
        }
        this.mDayNameLength = length;
    }

    public int getOverlappingEventGap() {
        return mOverlappingEventGap;
    }

    /**
     * Set the gap between overlapping events.
     * @param overlappingEventGap The gap between overlapping events.
     */
    public void setOverlappingEventGap(int overlappingEventGap) {
        this.mOverlappingEventGap = overlappingEventGap;
        invalidate();
    }

    public int getEventCornerRadius() {
        return mEventCornerRadius;
    }

    /**
     * Set corner radius for event rect.
     *
     * @param eventCornerRadius the radius in px.
     */
    public void setEventCornerRadius(int eventCornerRadius) {
        mEventCornerRadius = eventCornerRadius;
    }

    public int getEventMarginVertical() {
        return mEventMarginVertical;
    }

    /**
     * Set the top and bottom margin of the event. The event will release this margin from the top
     * and bottom edge. This margin is useful for differentiation consecutive events.
     * @param eventMarginVertical The top and bottom margin.
     */
    public void setEventMarginVertical(int eventMarginVertical) {
        this.mEventMarginVertical = eventMarginVertical;
        invalidate();
    }

    /**
     * Returns the first visible day in the week view.
     * @return The first visible day in the week view.
     */
    public Calendar getFirstVisibleDay() {
        return mFirstVisibleDay;
    }

    /**
     * Returns the last visible day in the week view.
     * @return The last visible day in the week view.
     */
    public Calendar getLastVisibleDay() {
        return mLastVisibleDay;
    }

    /**
     * Get the scrolling speed factor in horizontal direction.
     * @return The speed factor in horizontal direction.
     */
    public float getXScrollingSpeed() {
        return mXScrollingSpeed;
    }

    /**
     * Sets the speed for horizontal scrolling.
     * @param xScrollingSpeed The new horizontal scrolling speed.
     */
    public void setXScrollingSpeed(float xScrollingSpeed) {
        this.mXScrollingSpeed = xScrollingSpeed;
    }

    /**
     * Whether weekends should have a background color different from the normal day background
     * color. The weekend background colors are defined by the attributes
     * `futureWeekendBackgroundColor` and `pastWeekendBackgroundColor`.
     * @return True if weekends should have different background colors.
     */
    public boolean isShowDistinctWeekendColor() {
        return mShowDistinctWeekendColor;
    }

    /**
     * Set whether weekends should have a background color different from the normal day background
     * color. The weekend background colors are defined by the attributes
     * `futureWeekendBackgroundColor` and `pastWeekendBackgroundColor`.
     * @param showDistinctWeekendColor True if weekends should have different background colors.
     */
    public void setShowDistinctWeekendColor(boolean showDistinctWeekendColor) {
        this.mShowDistinctWeekendColor = showDistinctWeekendColor;
        invalidate();
    }

    /**
     * Whether past and future days should have two different background colors. The past and
     * future day colors are defined by the attributes `futureBackgroundColor` and
     * `pastBackgroundColor`.
     * @return True if past and future days should have two different background colors.
     */
    public boolean isShowDistinctPastFutureColor() {
        return mShowDistinctPastFutureColor;
    }

    /**
     * Set whether weekends should have a background color different from the normal day background
     * color. The past and future day colors are defined by the attributes `futureBackgroundColor`
     * and `pastBackgroundColor`.
     * @param showDistinctPastFutureColor True if past and future should have two different
     *                                    background colors.
     */
    public void setShowDistinctPastFutureColor(boolean showDistinctPastFutureColor) {
        this.mShowDistinctPastFutureColor = showDistinctPastFutureColor;
        invalidate();
    }

    /**
     * Get whether "now" line should be displayed. "Now" line is defined by the attributes
     * `nowLineColor` and `nowLineThickness`.
     * @return True if "now" line should be displayed.
     */
    public boolean isShowNowLine() {
        return mShowNowLine;
    }

    /**
     * Set whether "now" line should be displayed. "Now" line is defined by the attributes
     * `nowLineColor` and `nowLineThickness`.
     * @param showNowLine True if "now" line should be displayed.
     */
    public void setShowNowLine(boolean showNowLine) {
        this.mShowNowLine = showNowLine;
        invalidate();
    }

    /**
     * Get the "now" line color.
     * @return The color of the "now" line.
     */
    public int getNowLineColor() {
        return mNowLineColor;
    }

    /**
     * Set the "now" line color.
     * @param nowLineColor The color of the "now" line.
     */
    public void setNowLineColor(int nowLineColor) {
        this.mNowLineColor = nowLineColor;
        invalidate();
    }

    /**
     * Get the "now" line thickness.
     * @return The thickness of the "now" line.
     */
    public int getNowLineThickness() {
        return mNowLineThickness;
    }

    /**
     * Set the "now" line thickness.
     * @param nowLineThickness The thickness of the "now" line.
     */
    public void setNowLineThickness(int nowLineThickness) {
        this.mNowLineThickness = nowLineThickness;
        invalidate();
    }

    /**
     * Get whether the week view should fling horizontally.
     * @return True if the week view has horizontal fling enabled.
     */
    public boolean isHorizontalFlingEnabled() {
        return mHorizontalFlingEnabled;
    }

    /**
     * Set whether the week view should fling horizontally.
     * @return True if it should have horizontal fling enabled.
     */
    public void setHorizontalFlingEnabled(boolean enabled) {
        mHorizontalFlingEnabled = enabled;
    }

    /**
     * Get whether the week view should fling vertically.
     * @return True if the week view has vertical fling enabled.
     */
    public boolean isVerticalFlingEnabled() {
        return mVerticalFlingEnabled;
    }

    /**
     * Set whether the week view should fling vertically.
     * @return True if it should have vertical fling enabled.
     */
    public void setVerticalFlingEnabled(boolean enabled) {
        mVerticalFlingEnabled = enabled;
    }

    /**
     * Get the height of AllDay-events.
     * @return Height of AllDay-events.
     */
    public int getAllDayEventHeight() {
        return mAllDayEventHeight;
    }

    /**
     * Set the height of AllDay-events.
     */
    public void setAllDayEventHeight(int height) {
        mAllDayEventHeight = height;
    }

    /**
     * Get scroll duration
     * @return scroll duration
     */
    public int getScrollDuration() {
        return mScrollDuration;
    }

    /**
     * Set the scroll duration
     */
    public void setScrollDuration(int scrollDuration) {
        mScrollDuration = scrollDuration;
    }

    /////////////////////////////////////////////////////////////////
    //
    //      Functions related to scrolling.
    //
    /////////////////////////////////////////////////////////////////

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        boolean val = mGestureDetector.onTouchEvent(event);

        // Check after call of mGestureDetector, so mCurrentFlingDirection and mCurrentScrollDirection are set.
        if (event.getAction() == MotionEvent.ACTION_UP && !mIsZooming && mCurrentFlingDirection == Direction.NONE) {
            if (mCurrentScrollDirection == Direction.RIGHT || mCurrentScrollDirection == Direction.LEFT) {
                //goToNearestOrigin();
            }
            mCurrentScrollDirection = Direction.NONE;
        }

        return val;
    }

    private void goToNearestOrigin(){
        double leftDays = mCurrentOrigin.x / (mWidthPerDay + mColumnGap);

        if (mCurrentFlingDirection != Direction.NONE) {
            // snap to nearest day
            leftDays = Math.round(leftDays);
        } else if (mCurrentScrollDirection == Direction.LEFT) {
            // snap to last day
            leftDays = Math.floor(leftDays);
        } else if (mCurrentScrollDirection == Direction.RIGHT) {
            // snap to next day
            leftDays = Math.ceil(leftDays);
        } else {
            // snap to nearest day
            leftDays = Math.round(leftDays);
        }

        int nearestOrigin = (int) (mCurrentOrigin.x - leftDays * (mWidthPerDay + mColumnGap));

        if (nearestOrigin != 0) {
            // Stop current animation.
            mScroller.forceFinished(true);
            // Snap to date.
            mScroller.startScroll((int) mCurrentOrigin.x, (int) mCurrentOrigin.y, -nearestOrigin, 0, (int) (abs(nearestOrigin) / mWidthPerDay * mScrollDuration));
            ViewCompat.postInvalidateOnAnimation(WeekView.this);
        }
        // Reset scrolling and fling direction.
        mCurrentScrollDirection = mCurrentFlingDirection = Direction.NONE;
    }


    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.isFinished()) {
            if (mCurrentFlingDirection != Direction.NONE) {
                // Snap to day after fling is finished.
                //goToNearestOrigin();
            }
        } else {
            if (mCurrentFlingDirection != Direction.NONE && forceFinishScroll()) {
                //goToNearestOrigin();
            } else if (mScroller.computeScrollOffset()) {
                mCurrentOrigin.y = mScroller.getCurrY();
                mCurrentOrigin.x = mScroller.getCurrX();
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }

    /**
     * Check if scrolling should be stopped.
     * @return true if scrolling should be stopped before reaching the end of animation.
     */
    private boolean forceFinishScroll() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // current velocity only available since api 14
            return mScroller.getCurrVelocity() <= mMinimumFlingVelocity;
        } else {
            return false;
        }
    }


    /////////////////////////////////////////////////////////////////
    //
    //      Public methods.
    //
    /////////////////////////////////////////////////////////////////

    /**
     * Show today on the week view.
     */
    public void goToToday() {
        Calendar today = Calendar.getInstance();
        goToDate(today);
    }

    /**
     * Show a specific day on the week view.
     * @param date The date to show.
     */
    public void goToDate(Calendar date) {
        mScroller.forceFinished(true);
        mCurrentScrollDirection = mCurrentFlingDirection = Direction.NONE;

        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        if(mAreDimensionsInvalid) {
            mScrollToDay = date;
            return;
        }

        mRefreshEvents = true;

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        long day = 1000L * 60L * 60L * 24L;
        long dateInMillis = date.getTimeInMillis() + date.getTimeZone().getOffset(date.getTimeInMillis());
        long todayInMillis = today.getTimeInMillis() + today.getTimeZone().getOffset(today.getTimeInMillis());
        long dateDifference = (dateInMillis/day) - (todayInMillis/day);
        mCurrentOrigin.x = - dateDifference * (mWidthPerDay + mColumnGap);
        invalidate();
    }

    /**
     * Refreshes the view and loads the events again.
     */
    public void notifyDatasetChanged(){
        mRefreshEvents = true;
        invalidate();
    }

    /**
     * Vertically scroll to a specific hour in the week view.
     * @param hour The hour to scroll to in 24-hour format. Supported values are 0-24.
     */
    public void goToHour(double hour){
        if (mAreDimensionsInvalid) {
            mScrollToHour = hour;
            return;
        }

        int verticalOffset = 0;
        if (hour > 24)
            verticalOffset = mHourHeight * 24;
        else if (hour > 0)
            verticalOffset = (int) (mHourHeight * hour);

        if (verticalOffset > mHourHeight * 24 - getHeight() + mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom)
            verticalOffset = (int)(mHourHeight * 24 - getHeight() + mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom);

        mCurrentOrigin.y = -verticalOffset;
        invalidate();
    }

    /**
     * Get the first hour that is visible on the screen.
     * @return The first hour that is visible.
     */
    public double getFirstVisibleHour(){
        return -mCurrentOrigin.y / mHourHeight;
    }



    /////////////////////////////////////////////////////////////////
    //
    //      Interfaces.
    //
    /////////////////////////////////////////////////////////////////

    public interface EventClickListener {
        /**
         * Triggered when clicked on one existing event
         * @param event: event clicked.
         * @param eventRect: view containing the clicked event.
         */
        void onEventClick(WeekViewEvent event, RectF eventRect);
    }

    public interface EventLongPressListener {
        /**
         * Similar to {@link WeekView.EventClickListener} but with a long press.
         * @param event: event clicked.
         * @param eventRect: view containing the clicked event.
         */
        void onEventLongPress(WeekViewEvent event, RectF eventRect);
    }

    public interface EmptyViewClickListener {
        /**
         * Triggered when the users clicks on a empty space of the calendar.
         * @param time: {@link Calendar} object set with the date and time of the clicked position on the view.
         */
        void onEmptyViewClicked(Calendar time);
    }

    public interface EmptyViewLongPressListener {
        /**
         * Similar to {@link WeekView.EmptyViewClickListener} but with long press.
         * @param time: {@link Calendar} object set with the date and time of the long pressed position on the view.
         */
        void onEmptyViewLongPress(Calendar time);
    }

    public interface ScrollListener {
        /**
         * Called when the first visible day has changed.
         *
         * (this will also be called during the first draw of the weekview)
         * @param newFirstVisibleDay The new first visible day
         * @param oldFirstVisibleDay The old first visible day (is null on the first call).
         */
        void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay);

    }


    //// FIXME: 2017-02-05 should consider making this an async task so it wont be called again while its calculating
    public void loadEvents(List<event> events) {

        if (events != null) {

            orderEvents(events);
            days = sortToDays(events);
            daysLength = days.size();
            calculateEventPositions(days);
            invalidate();
        }


    }

    public void orderEvents(List<event> events) {
        Collections.sort(events, new Comparator<event>() {
            @Override
            public int compare(event event1, event event2) {
                long start1 = event1.startTime().getMillis();
                long start2 = event2.startTime().getMillis();
                int comparator = start1 > start2 ? 1 : (start1 < start2 ? -1 : 0);
                if (comparator == 0) {
                    long end1 = event1.endTime().getMillis();
                    long end2 = event2.endTime().getMillis();
                    comparator = end1 > end2 ? 1 : (end1 < end2 ? -1 : 0);
                }
                return comparator;
            }
        });
    }

    public List<day> sortToDays(List<event> events) {

        int earliestTime = 100;

        int latestTime = -1;

        event firstEventInDay = null;

        List<day> days = new ArrayList<>();

        List<event> currentDayEvents = new ArrayList<>();

        DateTime dayTime = null;

        int eventsLength = events.size();

        DateTime today = new DateTime();
        DateTime eventTime = null;
        DateTime lastEventTime = null;

        for (event event : events) {


            if(eventTime != null) {
                lastEventTime = eventTime;
            }

            eventTime = event.startTime();

            if (firstEventInDay == null) {

                firstEventInDay = event;
                earliestTime = eventTime.getHourOfDay();
                dayTime = eventTime;

                //could be bugged here
                latestTime = earliestTime + 1;

                currentDayEvents.add(event);
                eventsLength -= 1;

                if (eventsLength == 0) {

                    days.add(createDay(eventTime, today, earliestTime, latestTime, currentDayEvents));

                    currentDayEvents.clear();
                }

            }

            else if (isSameDay(dayTime, eventTime)) {

                //// TODO: 2017-03-08 consider deleting this
                if (eventTime.getHourOfDay() < earliestTime) {
                    earliestTime = eventTime.getHourOfDay();
                }

                if (event.endTime().getHourOfDay() > latestTime) {
                    latestTime = event.endTime().getHourOfDay();
                }

                currentDayEvents.add(event);
                eventsLength -= 1;
                if (eventsLength == 0) {

                    days.add(createDay(eventTime, today, earliestTime, latestTime, currentDayEvents));

                    currentDayEvents.clear();

                }

            }

            //the event is in another day so we save events collected into a day class
            else if (!isSameDay(dayTime, eventTime)) {

               days.add(createDay(lastEventTime, today, earliestTime, latestTime, currentDayEvents));

                currentDayEvents.clear();

                currentDayEvents.add(event);
                eventsLength -= 1;

                earliestTime = eventTime.getHourOfDay();

                latestTime = eventTime.getHourOfDay();

                firstEventInDay = event;

                dayTime = eventTime;

                if(eventsLength == 0) {

                     days.add(createDay(eventTime, today, earliestTime, latestTime, currentDayEvents));
                    currentDayEvents.clear();

                }


            }


        }

        return days;
    }

    //todo add nd's and st's to day numbers
    public day createDay(DateTime eventTime, DateTime today, int earliestTime, int latestTime, List<event> currentDayEvents) {

        DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM");
        String timeString = fmt.print(eventTime).toUpperCase();
        timeString += " " + eventTime.getDayOfMonth() + "th";


        if(isSameDay(eventTime, today)) {
            timeString += " TODAY";
        }

        else if (isSameDay(eventTime.plusDays(- 1), today)) {
            timeString += " Tomorrow ";
        }


        if (earliestTime -1 < 0 ) {
            earliestTime = 1;
        }
        if (latestTime + 2 > 24) {
            latestTime = 22;
        }

        return new day(earliestTime - 1, latestTime + 2, currentDayEvents, eventTime, timeString);

    }

    private void drawTimeForEachDay(Canvas canvas, List<day> days) {

        // Draw the background color for the header column.
        canvas.drawRect(0, 75, mHeaderColumnWidth, getHeight(), mHeaderColumnBackgroundPaint);

        // Clip to paint in left column only.
        canvas.clipRect(0, 75, mHeaderColumnWidth, getHeight(), Region.Op.REPLACE);

        float dayAfterBot = 0;
        float dayAfterTop = 0;
        boolean isNextDay = false;

        if(currentDayInt < days.size() - 1) {

            dayAfterBot = days.get(currentDayInt + 1).getHeaderYbot() + mCurrentOrigin.y;
            dayAfterTop = days.get(currentDayInt + 1).getHeaderYtop() + mCurrentOrigin.y;
            isNextDay = true;

        }


        if ( (dayAfterBot > 75 && dayAfterTop < getHeight()) && isNextDay) {

            canvas.drawLine(0, dayAfterTop, mHeaderColumnWidth, dayAfterTop, mHourSeparatorPaint);
            canvas.drawLine(0, dayAfterBot, mHeaderColumnWidth, dayAfterBot, mHourSeparatorPaint);
        }

        int currentDay = 0;

        int daysSize = (days.size() >= 3 ? 2 : days.size());

        if (currentDayInt == days.size() - 1) {
            daysSize --;
        }


        if (currentDayInt != 0) {
            currentDay =  -1;
        }

        while (currentDay < daysSize) {

            day dayObj = days.get(currentDay + currentDayInt);

            List<hourText> dayHourText = dayObj.getHourText();

            if (dayHourText != null) {

                for (int hourNum = 0; hourNum < dayObj.getHourText().size(); hourNum++) {

                    hourText hour = dayObj.getHourText().get(hourNum);
                    float top = hour.getyStart() + mCurrentOrigin.y;
                    String time = hour.getNumberText();
                    String AMPM = hour.getAmPm();

                    if (top < getHeight()) {
                        canvas.drawText(time, mTimeTextWidth + mHeaderColumnPadding, top + mTimeTextHeight, mTimeTextPaint);
                        canvas.drawText(AMPM, mTimeTextWidth + 25, top + mTimeTextHeight * 2, mTimeTextAMPMPaint);
                        //linecount++;
                    }
                }
            }

                currentDay++;
        }
    }

    public void calculateTimeText() {

        int dayStart = 0;
        int lastDifference = 0;

        for (int j = 0; j < days.size(); j++) {
            int start = days.get(j).getStartHour();
            int difference = days.get(j).getEndHour() - start;

            if ( difference < 4) {

                difference = 4;

            }

            List<hourText> hours = new ArrayList<>();


            if (j > 0) {
                dayStart += lastDifference * mHourHeight;
            }

            lastDifference = difference;
            String AMPM = "AM";

            for (int i = 0; i < difference; i++) {
                float top = mHeaderHeight + mHeaderRowPadding * 2 + mHourHeight * i + mHeaderMarginBottom + dateSpace * j + dayStart;

                // Draw the text if its y position is not outside of the visible area. The pivot point of the text is the point at the bottom-right corner.
                //String time = getDateTimeInterpreter().interpretTime(start + i);
                int timeNumber = ((start + i) % 12);

                if (timeNumber == 0) {
                    timeNumber = 12;
                }
                if (timeNumber >= 12) {
                    AMPM = "PM";
                }

                String time = Integer.toString(timeNumber);

                if (time == null)
                    throw new IllegalStateException("A DateTimeInterpreter must not return null time");

                hourText hour = new hourText(top, time, AMPM);
                hours.add(hour);


            }

            days.get(j).setHourText(hours);

        }





    }

    //todo have to revise this as it is rough
    public void drawTimeLines(Canvas canvas, List<day> days, int currentDayInt) {

        mHeaderColumnWidth = mTimeTextWidth + mHeaderColumnPadding *2;

        float startFromPixel = mCurrentOrigin.x + mColumnGap + mHeaderColumnWidth;

        canvas.clipRect(mHeaderColumnWidth, 75, getWidth(), getHeight(), Region.Op.REPLACE);


        float currentDayYTop = days.get(currentDayInt).getHeaderYtop() + mCurrentOrigin.y;
        float currentDayYBot = days.get(currentDayInt).getHeaderYbot() + mCurrentOrigin.y;

        float dayAfterBot = 0;
        float dayAfterTop = 0;
        boolean isDayAfter = false;
        if (currentDayInt < days.size() - 1) {

            dayAfterBot = days.get(currentDayInt + 1).getHeaderYbot() + mCurrentOrigin.y;
            dayAfterTop = days.get(currentDayInt + 1).getHeaderYtop() + mCurrentOrigin.y;
            isDayAfter = true;

        }

        Log.e("currentDayBot", " = " + currentDayYBot);

        if (currentDayYBot > 75 && currentDayYTop < getHeight()) {

            Log.e("getting called", "getting called");

            canvas.drawText(days.get(currentDayInt).getDayString() + ", " + days.get(currentDayInt).getEvents().size() + " events", getWidth()/2, currentDayYBot - 10, mTimeTextPaint);
            canvas.drawLine(mHeaderColumnWidth, currentDayYTop, getWidth() , currentDayYTop, mHourSeparatorPaint );
            canvas.drawLine(mHeaderColumnWidth, currentDayYBot, getWidth(), currentDayYBot, mHourSeparatorPaint);

        }

        if ( (dayAfterBot > 75 && dayAfterTop < getHeight() && isDayAfter)) {

            canvas.drawText(days.get(currentDayInt + 1).getDayString() + ", " + days.get(currentDayInt + 1).getEvents().size() + " events", getWidth()/2, dayAfterBot - 10, mTimeTextPaint);

            canvas.drawLine(mHeaderColumnWidth, dayAfterTop, getWidth(), dayAfterTop, mHourSeparatorPaint);
            canvas.drawLine(mHeaderColumnWidth, dayAfterBot, getWidth(), dayAfterBot, mHourSeparatorPaint);

        }

        int workingDay = 0;

        int lastShowDay = (days.size() >= 3 ? 2 : days.size());

        if (currentDayInt == days.size() - 1) {
            lastShowDay --;
        }


        if (currentDayInt != 0) {
            workingDay =  -1;
        }

        //used to check if the current day is today to draw now line
        DateTime today = new DateTime();

        //iterate through day before and after and current
        while(workingDay < lastShowDay) {

            day currentDay = days.get(workingDay + currentDayInt);

            ArrayList lines = currentDay.getLines();


            if (isSameDay(currentDay.getDayInfo(), today)) {

                int now = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                if (now >= currentDay.getStartHour() && now <= currentDay.getEndHour()) {

                    int drawHour = now - currentDay.getStartHour();

                    float top = (float) lines.get(0) + mCurrentOrigin.y + mHourHeight * drawHour;

                    Calendar nowCal = Calendar.getInstance();
                    float beforeNow = nowCal.get(Calendar.MINUTE) / 60.0f * mHourHeight;
                    float start = top + beforeNow;
                    canvas.drawLine(startFromPixel, start, getWidth(), start, mNowLinePaint);

                }

            }

            //if day is before current day, go through hours backwords to draw, and break if having to draw outside screen
            if (workingDay < currentDayInt) {

                for (int w = lines.size() - 1; w >= 0; w--) {

                    float top = (float) lines.get(w) + mCurrentOrigin.y;

                    if (top > 75 && top < getHeight()) {

                        canvas.drawLine(startFromPixel, top, getWidth(), top, mHourSeparatorPaint);

                    }

                }

            }

            //current day

            else if (workingDay == currentDayInt) {

                for (int w = 0; w <= lines.size() - 1; w++) {

                    float top = (float) lines.get(w) + mCurrentOrigin.y;

                    if (top > 75 && top < getHeight()) {




                        canvas.drawLine(startFromPixel, top, getWidth(), top, mHourSeparatorPaint);
                    }


                }

            }

            else {


                for (int w = 0; w <= lines.size() - 1; w++) {

                    float top = (float) lines.get(w) + mCurrentOrigin.y;







                    //if (top > mHeaderHeight + mHeaderRowPadding * 2 && top < getHeight()) {


                        canvas.drawLine(startFromPixel, top, getWidth(), top, mHourSeparatorPaint);
                    //}

//                    else {
//                        break;
//                    }

                }

            }

            workingDay++;

        }
    }

    public void calculateTimeLines() {

        float top = mHeaderHeight + mHeaderRowPadding * 2  + mTimeTextHeight / 2 + mHeaderMarginBottom - mHourHeight - dateSpace;

        //iterate through each day to draw hour lines
        for (int i = 0; i < days.size(); i++) {

            boolean lastDay = false;

            if (i == days.size() - 1) {
                lastDay = true;
            }

            day currentDay = days.get(i);

            currentDay.setHeaderY(top + mHourHeight - 40);

            top += dateSpace;

            int difference = currentDay.getEndHour() - currentDay.getStartHour();

            if ( difference < 4) {

                difference = 4;
            }


            //each hour needed to be draw per day
            for (int j = 0; j < difference; j++) {

                top += mHourHeight;


                currentDay.setLines(top);
                if(lastDay && j == difference - 1) {

                    bottomScroll = top + 10;
                }

            }

        }

    }

    //todo only considers constant width events
    //// FIXME: 2017-02-03 only considers events with constant width
    public void calculateEventPositions(List<day> days) {

        List<eventBox> handledDayEvents = new ArrayList<>();
        List<eventBox> collisionEvents = new ArrayList<>();
        List<eventBox> workingEvents = new ArrayList<>();
        handledEvents = new ArrayList<>();
        HashMap<Integer, eventBox> columnMap = new HashMap<Integer, eventBox>();
        //SparseArray<eventBox> columnMap = new SparseArray<eventBox>();HashMap<Integer, eventBox>();

        float leftStart = 180;
        //float leftStart = mColumnGap + mHeaderColumnWidth;
        //time text height might be wrong to be here
        float baseTop = mHeaderHeight + mHeaderRowPadding * 2 + mTimeTextHeight / 2 + mHeaderMarginBottom;

        float dayOffset = 0;

        eventBox eventBox;
        int dayCount = currentDayInt;

        for (day currentDay : days) {

            baseTop += dayOffset;

            block currentBlock = null;

            List<block> dayBlocks = new ArrayList<block>();

            currentBlock = new block();


            for (event event : currentDay.getEvents()) {

                float eventWidth = calculateEventWidth(event);

                eventFlagHeight = calculateEventHeight(event);

                eventBox = CalculatePositionBeforeConflict(baseTop, leftStart, event, currentDay.getStartHour(), eventFlagHeight, eventWidth);

                calculateConflict(handledDayEvents, eventBox, columnMap, eventWidth, dayCount);

                //end of event loop
            }

            int difference = currentDay.getEndHour() - currentDay.getStartHour();

            if (difference < 4) {
                difference = 4;
            }
            dayOffset = (difference * mHourHeight) + dateSpace;
            // end of day loop

            dayCount ++;
        }

        //float top = mHeaderHeight + mHeaderRowPadding * 2 + mHourHeight * hourNumber + mTimeTextHeight / 2 + mHeaderMarginBottom + dateSpace * i;




    }

    //todo need to work on this more to draw the full event
    public void paintEvents(Canvas canvas) {

        int workingDay = 0;

        int lastShowDay = (days.size() >= 3 ? 2 : days.size());

        if (currentDayInt == days.size() - 1) {
            lastShowDay --;
        }


        if (currentDayInt != 0) {
            workingDay =  -1;
        }

        while (workingDay < lastShowDay) {

            List<eventBox> dayhandledEvents = days.get(workingDay + currentDayInt).getHandledEvents();

            if ( !dayhandledEvents.isEmpty()) {

                for (eventBox event : dayhandledEvents) {



                    Drawable d = getResources().getDrawable(R.drawable.blueevent);

                    d.setAlpha(80);

                    d.setBounds((int) event.getStartX(), (int) event.getTop() + (int) mCurrentOrigin.y, (int) event.getFlagRight(), (int) event.getBottom() + (int) mCurrentOrigin.y);


                    d.draw(canvas);

                    //d = null;


                    //RectF rect = new RectF(event.getStartX(), event.getTop() + mCurrentOrigin.y, event.getFlagRight(), event.getBottom() + mCurrentOrigin.y);

                    //canvas.drawRect(rect, mEventBackgroundPaint);

                }

            }

            workingDay ++;
        }

    }

    //this is assuming event width is the same
    public void calculateConflict(List<eventBox> handledEvents, eventBox eventBox, HashMap<Integer, eventBox> columnMap, float eventWidth, int currentDayIntTemp) {

        //todo set this value before
        mEventPadding = 20;
        boolean placed = false;
        int i = 0;
        int key = days.get(currentDayIntTemp).getHandledEvents().size();


        //loops through until a place if found for the event
        while (!placed) {

            //checks if event has been placed on this column
            if (!columnMap.containsKey(i)) {
                placed = true;
            }
            //checks if event collides with event in column, placed if not
            else if (!flagsCollide(eventBox, columnMap.get(i))) {
                placed = true;
            }

            //once a place is found calculations are done to place it correctly
            if (placed) {

                //handles x position
                eventBox.setOffset(((eventWidth + mEventPadding) * i));

                //replaces old event in the column
                columnMap.put(i, eventBox);

                days.get(currentDayIntTemp).getHandledEvents().add(eventBox);


                if (currentBlock != null) {

                    //checks if event collides with already colliding events of the block
                    if (currentBlock.collides(eventBox.getTop())) {

                        //add event to the block and updates bottom of the block
                        days.get(currentDayIntTemp).getBlocks().get(days.get(currentDayIntTemp).getBlocks().size() - 1).addEvent(eventBox.getBottom(), eventBox.getFlagRight());
                        eventBox.setId(currentBlock.getId());

                    }

                    else {

                        currentBlock = null;

                    }


                }

                //current block is null
                else {

                    //first column that starts to run offscreen, so create block, adding previous events
                    if (i == 2) {

                        int id = days.get(currentDayIntTemp).getBlocks().size();

                        days.get(currentDayIntTemp).addBlock(days.get(currentDayIntTemp).getHandledEvents().get(key - 2).getTop(), eventBox.getBottom(), id, days.get(currentDayIntTemp).getHandledEvents().get(key - 2).getStartX());

                        days.get(currentDayIntTemp).getHandledEvents().get(key - 2).setId(id);

                        days.get(currentDayIntTemp).getHandledEvents().get(key - 1).setId(id);

                        days.get(currentDayIntTemp).getHandledEvents().get(key).setId(id);

                        currentBlock = days.get(currentDayIntTemp).getBlocks().get(id);



                    }

                }

            }

            i++;


        }

    }



//    //if the event doesn't collide with the last event in the working list
//    // then its flag doesn't collide with any of the workingEvents
//    if(!flagsCollide(eventBox, workingEvents.get(-1))) {
//
//        //if event collides with flag, shift it to the right
//        if(eventBox.getTop() <= workingEvents.get(0).getPoleBottom()) {
//
//            //shift by flag padding
//            eventBox.setOffset(flagPadding);
//
//            //todo better way to do this
//            //check next event to see if it interacts with flag, if so move all events it collides with
//            if (eventBox.getTop() <= workingEvents.get(1).getPoleBottom() && eventBox.getFlagRight() > workingEvents.get(1).getStartX()) {
//                float shiftX = eventBox.getFlagRight() - workingEvents.get(1).getStartX();
//                for (int i = 1; i <= workingEvents.size(); i++) {
//                    workingEvents.get(i).setOffset(shiftX);
//                }
//            }
//        }
//
//
//        if(spaceSaving) {
//            //// TODO: 2017-02-06 define defineBlock
//            defineBlock();
//
//        }
//    }
//
//    for ( int i = handledEvents.size() - 1; i >= 0; i-- ) {
//
//        if (!flagsCollide(eventBox, handledEvents.get(i))) {
//
//        }
//
//    }

    public boolean flagsCollide(eventBox eventBox, eventBox box) {

        if (box.getTop() <= eventBox.getTop() && eventBox.getTop() <= box.getBottom()) {
            return true;
        }
        else {
            return false;
        }

    }

//// FIXME: 2017-02-05 need to add in use of emoji's
    public float calculateEventWidth(event event) {

        //todo determine actual width
        return 620;
       //return mEventTextPaint.measureText(event.getTitle());

    }

    //// FIXME: 2017-02-05 need to do check to see if all day event
    public float calculateEventHeight(event event) {

        return mHourHeight/2;

    }

    public eventBox CalculatePositionBeforeConflict(float baseTop, float leftStart, event event,
                                            int dayStartTime, float eventHeight, float eventWidth) {

        //calculates y position by adding hours and minutes to base y value

        float Length = (event.endTime().getHourOfDay() - event.startTime().getHourOfDay()) * mHourHeight
                - event.startTime().getMinuteOfDay()/60.0f * mHourHeight
                + event.endTime().getMinuteOfDay()/60.0f * mHourHeight;
        float eventStartTime = event.startTime().getHourOfDay();

        float minuiteofDay = event.startTime().getMinuteOfHour();

        float top = (event.startTime().getHourOfDay() - dayStartTime) * mHourHeight
                + (event.startTime().getMinuteOfHour()/60.0f * mHourHeight) + baseTop;

        //bottom of flag
        float bottom = top + eventHeight;

        float poleBottom = top + Length;

        float poleRight = leftStart + poleWidth;

        float flagRight = leftStart + eventWidth;


        eventBox eventBox = new eventBox(event, leftStart, top, Length, flagRight, bottom, poleBottom, poleRight);



        return eventBox;
    }













////header title
//    //clip top left corner
//    canvas.clipRect(0, 0, mTimeTextWidth + mHeaderColumnPadding * 2, mHeaderHeight + mHeaderRowPadding * 2, Region.Op.REPLACE);
//    canvas.drawRect(0, 0, mTimeTextWidth + mHeaderColumnPadding * 2, mHeaderHeight + mHeaderRowPadding * 2, mHeaderBackgroundPaint);
//
//    // Clip to paint header row only.
//    canvas.clipRect(mHeaderColumnWidth, 0, getFlagRight(), mHeaderHeight + mHeaderRowPadding * 2, Region.Op.REPLACE);
//
//    // Draw the header background.
//    canvas.drawRect(0, 0, getFlagRight(), mHeaderHeight + mHeaderRowPadding * 2, mHeaderBackgroundPaint);
//
//
//    //events
//    // Calculate top.
//    float top = mHourHeight * 24 * mEventRects.get(i).top / 1440 + mCurrentOrigin.y + mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom + mTimeTextHeight/2 + mEventMarginVertical;
//    // Calculate bottom.
//    float bottom = mEventRects.get(i).bottom;
//    bottom = mHourHeight * 24 * bottom / 1440 + mCurrentOrigin.y + mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom + mTimeTextHeight/2 - mEventMarginVertical;
//
//    // Calculate left and right.
//    float left = startFromPixel + mEventRects.get(i).left * mWidthPerDay;
//    if (left < startFromPixel)
//    left += mOverlappingEventGap;
//    float right = left + mEventRects.get(i).width * mWidthPerDay;
//    if (right < startFromPixel + mWidthPerDay)
//    right -= mOverlappingEventGap;


    //todo ill probobly not do the drawing here

    private void MydrawEventTitle(WeekViewEvent event, RectF rect, Canvas canvas, float originalTop, float originalLeft) {
        if (rect.right - rect.left - mEventPadding * 2 < 0) return;
        if (rect.bottom - rect.top - mEventPadding * 2 < 0) return;

        // Prepare the name of the event.
        SpannableStringBuilder bob = new SpannableStringBuilder();
        if (event.getName() != null) {
            bob.append(event.getName());
            bob.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, bob.length(), 0);
            bob.append(' ');
        }

        // Prepare the location of the event.
        if (event.getLocation() != null) {
            bob.append(event.getLocation());
        }

        int availableHeight = (int) (rect.bottom - originalTop - mEventPadding * 2);
        int availableWidth = (int) (rect.right - originalLeft - mEventPadding * 2);

        // Get text dimensions.

        StaticLayout textLayout = new StaticLayout(bob, mEventTextPaint, availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        int lineHeight = textLayout.getHeight() / textLayout.getLineCount();

        if (availableHeight >= lineHeight) {
            // Calculate available number of line counts.
            int availableLineCount = availableHeight / lineHeight;
            //todo definatly no shortening of text
            //todo make there be less muslims in the world
            do {
                // Ellipsize text to fit into event rect.
                textLayout = new StaticLayout(TextUtils.ellipsize(bob, mEventTextPaint, availableLineCount * availableWidth, TextUtils.TruncateAt.END), mEventTextPaint, (int) (rect.right - originalLeft - mEventPadding * 2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

                // Reduce line count.
                availableLineCount--;

                // Repeat until text is short enough.
            } while (textLayout.getHeight() > availableHeight);

            // Draw text.
            canvas.save();
            canvas.translate(originalLeft + mEventPadding, originalTop + mEventPadding);
            textLayout.draw(canvas);
            canvas.restore();
        }
    }

    public void getEvents() {

        if (events == null || restart) {

            settings = PreferenceManager.getDefaultSharedPreferences(getContext());

            events = savedPreferences.getListevents("interested events", settings);


            loadEvents(events);


        }

    }

    public void drawHeader(Canvas canvas, List<day> days) {

        // Clip to paint header row only.
        canvas.clipRect(0, 0, getWidth(), 75, Region.Op.REPLACE);

        // Draw the header background.
        //canvas.drawRect(0, 0, getWidth(), mHeaderHeight + mHeaderRowPadding * 2, mDayBackgroundPaint);


        boolean goBefore = false;

        float dayBeforeBot = 0;
        float dayBeforeTop = 0;

        boolean goAfter = false;

        String textAfter = "";

        float dayAfterBot = 0;
        float dayAfterTop = 0;



        if ( currentDayInt != 0) {

            if(days.get(currentDayInt-1).getHeaderYBotBot() != 0) {

                dayBeforeBot = days.get(currentDayInt - 1).getHeaderYBotBot() + mCurrentOrigin.y;
                dayBeforeTop = days.get(currentDayInt - 1).getHeaderYBotTop() + mCurrentOrigin.y;
                goBefore = true;
            }
        }

        if (currentDayInt < days.size() - 1) {

            textAfter = days.get(currentDayInt + 1).getDayString();
            goAfter = true;
            dayAfterBot = days.get(currentDayInt + 1).getHeaderYbot() + mCurrentOrigin.y;
            dayAfterTop = days.get(currentDayInt + 1).getHeaderYtop() + mCurrentOrigin.y;

        }

        float currentYTop = days.get(currentDayInt).getHeaderYtop() + mCurrentOrigin.y;
        float currentYBot = days.get(currentDayInt).getHeaderYbot() + mCurrentOrigin.y;



        if  (dayBeforeBot >= 0 && goBefore) {

            if (dayBeforeBot >= 75) {

                canvas.drawText(days.get(currentDayInt - 1).getDayString() + ", " + days.get(currentDayInt - 1).getEvents().size() + " events", getWidth() / 2, 65, mTimeTextPaint);
                canvas.drawLine(0, 75, getWidth(), 75, mHourSeparatorPaint);
                currentDayInt -= 1;


            }

            else {

                Log.e("i am running", "running = " + currentYBot);
                int numberOfEventscurrent = days.get(currentDayInt).getEvents().size();
                int numberofEventsBefore = days.get(currentDayInt - 1).getEvents().size();

                canvas.drawText(days.get(currentDayInt - 1).getDayString() + ", " + numberofEventsBefore + " events", getWidth() / 2, dayBeforeBot - 10, mTimeTextPaint);
                canvas.drawLine(0, dayBeforeBot, getWidth(), dayBeforeBot, mHourSeparatorPaint);

                canvas.drawText(days.get(currentDayInt).getDayString() + ", " + numberOfEventscurrent + " events", getWidth() / 2, currentYBot - 10, mTimeTextPaint);


            }
        }

        else if (dayAfterTop <= 75 && goAfter) {

            //if (dayAfter <= mHeaderHeight + mHeaderRowPadding + 10) {
            if (dayAfterTop <= 0) {

                days.get(currentDayInt).setHeaderYBotBot( - mCurrentOrigin.y);

                int numberOfEvents = days.get(currentDayInt + 1).getEvents().size();


                canvas.drawText(textAfter + ", " + numberOfEvents + " events", getWidth() / 2, dayAfterBot - 10, mTimeTextPaint);
                canvas.drawLine(0, dayAfterBot, getWidth(), dayAfterBot, mHourSeparatorPaint);
                currentDayInt += 1;

            }

            else {

                int numberOfEventscurrent = days.get(currentDayInt).getEvents().size();
                int numberofEventsAfter = days.get(currentDayInt + 1).getEvents().size();

                canvas.drawText(days.get(currentDayInt + 1).getDayString() + ", " + numberofEventsAfter + " events", getWidth() / 2, dayAfterBot - 10, mTimeTextPaint);
                canvas.drawLine(0, dayAfterTop, getWidth(), dayAfterTop, mHourSeparatorPaint);


                canvas.drawText(days.get(currentDayInt).getDayString() + ", " + numberOfEventscurrent + " events", getWidth() / 2, dayAfterTop - 10, mTimeTextPaint);

            }


        }

        else {

            canvas.drawText(days.get(currentDayInt).getDayString() + ", " + days.get(currentDayInt).getEvents().size() + " events", getWidth() / 2, 65, mTimeTextPaint);
            canvas.drawLine(0, 75, getWidth(), 75, mHourSeparatorPaint);

        }

    }

    public void onAnimateMove(final float dx, long duration, final float position) {

        animateInterpolator = new OvershootInterpolator();
        startTime = android.os.SystemClock.elapsedRealtime();
        endTime = startTime + duration;

        post(new Runnable() {
            @Override
            public void run() {
                onAnimateStep(dx, position);
            }
        });
    }

    public void onAnimateStep(float dx, float position) {
        long curTime = android.os.SystemClock.elapsedRealtime();
        float percentTime = (float) (curTime - startTime) / (float) (endTime - startTime);
        float percentDistance = animateInterpolator.getInterpolation(percentTime);
        float curDx = percentDistance * dx;

        moveBlock(curDx, position);


    }

    public void moveBlock(float dx, float position) {

        boolean isBroke = false;

        Log.e("position move block", " = " + position);

        for (int i = 0; i < 2; i++) {

            for (block currentBlock : days.get(currentDayInt + i).getBlocks()) {

                if (position >= currentBlock.getTop() && position <= currentBlock.getBottom()) {

                    int id = currentBlock.getId();

                    for (eventBox event : days.get(currentDayInt + i).getHandledEvents()) {

                        if (event.getId() == id) {

                            event.setOffset( - dx);

                        }


                    }

                    isBroke = true;
                    break;
                }




            }

            if (isBroke) {
                postInvalidate();
                break;
            }






        }

    }

    public void drawBlockLines(Canvas canvas) {

        int workingDay = currentDayInt;

        //int lastShowDay = days.size();

        int lastShowDay = (days.size() >= 3 ? 2 : days.size()) + currentDayInt;

        if (currentDayInt == days.size() - 1) {
            lastShowDay--;
        }



        while (workingDay < lastShowDay) {

            for (block blocks : days.get(workingDay).getBlocks()) {

                Paint newPaint = new Paint();
                Paint whitePaint = new Paint();
                whitePaint.setColor(Color.WHITE);
                whitePaint.setStrokeWidth(15);
                newPaint.setStrokeWidth(15);
                newPaint.setColor(mNowLineColor);

                canvas.drawLine(mHeaderColumnWidth, blocks.getTop() + mCurrentOrigin.y, mHeaderColumnWidth, blocks.getBottom(), whitePaint);

                canvas.drawLine(mHeaderColumnWidth + 15, blocks.getTop() + mCurrentOrigin.y, mHeaderColumnWidth + 15, blocks.getBottom() + mCurrentOrigin.y, newPaint);

                float top = blocks.getTop() -  ((blocks.getBottom() - blocks.getTop()) * ( blocks.getOffset() / (blocks.getEndX() - (blocks.getStartX() + (getWidth() - mHeaderColumnPadding)))));

                canvas.drawCircle(mHeaderColumnWidth + (float) 15.3, top + mCurrentOrigin.y, 15, newPaint );


            }

            workingDay++;


        }

    }



}
