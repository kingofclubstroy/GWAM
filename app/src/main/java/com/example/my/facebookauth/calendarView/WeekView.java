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
import static hani.momanii.supernova_emoji_library.Helper.EmojiconHandler.getEmojiResource;
import static java.lang.Math.abs;
import static java.lang.Math.floor;
import hani.momanii.supernova_emoji_library.Helper.EmojiconHandler;

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
    //private List<EventRect> mEventRects;
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
    private static int mHourHeight = 1500;
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
    private int mHourSeparatorHeight = 3;
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
    private static float bottomScroll;
    private static float mEventWidth;

    //variables for flinging blocks
    private Interpolator animateInterpolator;
    private long startTime;
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

                if (mCurrentOrigin.y - distanceY >= 0) {
                    mCurrentOrigin.y = 0;
                }

                else if ( mCurrentOrigin.y - distanceY <=  -bottomScroll) {

                    mCurrentOrigin.y =  - bottomScroll;

                }

                else {

                    mCurrentOrigin.y -= distanceY;

                }

                ViewCompat.postInvalidateOnAnimation(WeekView.this);
            }
//
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

                    mScroller.fling(0, (int) mCurrentOrigin.y, 0, (int) velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, (int) - bottomScroll, 0);

            }

            ViewCompat.postInvalidateOnAnimation(WeekView.this);
            return true;
        }


        //todo haven't finished initializing this with new code
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // If the tap was on an event then trigger the callback.
            //if (mEventRects != null && mEventClickListener != null) {
                //List<EventRect> reversedEventRects = mEventRects;
                //Collections.reverse(reversedEventRects);
//                for (EventRect event : reversedEventRects) {
//                    if (event.rectF != null && e.getX() > event.rectF.left && e.getX() < event.rectF.right && e.getY() > event.rectF.top && e.getY() < event.rectF.bottom) {
//                        mEventClickListener.onEventClick(event.originalEvent, event.rectF);
//                        playSoundEffect(SoundEffectConstants.CLICK);
//                        return super.onSingleTapConfirmed(e);
//                    }
//                }
            //}

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

        //todo adapt to new code
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            //if (mEventLongPressListener != null && mEventRects != null) {
                //List<EventRect> reversedEventRects = mEventRects;
                //Collections.reverse(reversedEventRects);
//                for (EventRect event : reversedEventRects) {
//                    if (event.rectF != null && e.getX() > event.rectF.left && e.getX() < event.rectF.right && e.getY() > event.rectF.top && e.getY() < event.rectF.bottom) {
//                        mEventLongPressListener.onEventLongPress(event.originalEvent, event.rectF);
//                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
//                        return;
//                    }
//                }
            //}

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

    // these are the weekviews constructors
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

        //todo may need to remove this
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

    //initializes everything on create

    private void init() {
        // Scrolling initialization.
        mGestureDetector = new GestureDetectorCompat(mContext, mGestureListener);
        mScroller = new OverScroller(mContext);

        mMinimumFlingVelocity = ViewConfiguration.get(mContext).getScaledMinimumFlingVelocity();
        mScaledTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();

        // Measure settings for time column.
        mTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeTextPaint.setTextAlign(Paint.Align.CENTER);
        mTimeTextPaint.setTextSize(45);
        mTimeTextPaint.setColor(mHeaderColumnTextColor);

        Rect rect = new Rect();
        mTimeTextPaint.getTextBounds("00", 0, "00".length(), rect);
        mTimeTextHeight = rect.height();
        mHeaderMarginBottom = mTimeTextHeight / 2;
        initTextTimeWidth();

        mTimeTextAMPMPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeTextAMPMPaint.setTextAlign(Paint.Align.CENTER);
        mTimeTextAMPMPaint.setTextSize(29);
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
        calculateEventWidth();

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


    /**
     * called everytime invalidate is called, handles all drawing on the screen
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        //allows the data to be recalculated when data chamges

        if (restart || days.isEmpty()) {

            mHourHeight = (int) ((getHeight() - mHeaderHeight)/3.5);
            calculateEventWidth();

            //grabs events and does all calculations
            getEvents();
            calculateTimeText();
            calculateTimeLines();

            currentDayInt = 0;

            nextDayStart = (float) days.get(currentDayInt).getLines().get(0);

            previousDayStart = 0;


            restart = false;
        }



        //handles sticky headers
        drawHeader(canvas, days);

        //draws time strings
        drawTimeForEachDay(canvas, days);

        //draws lines
        drawTimeLines(canvas, days, currentDayInt);






        paintEvents(canvas);


        //draws green block indicators
        drawBlockLines(canvas);

    }

    /**
     * tells onDraw to run
     */
    @Override
    public void invalidate() {
        super.invalidate();
        mAreDimensionsInvalid = true;
    }

    //disregard functions in this block, going to take them out

    /////////////////////////////////////////////////////////////////
    //
    //      Functions related to setting and getting the properties.
    //
    /////////////////////////////////////////////////////////////////

    public void setOnEventClickListener (EventClickListener listener) {
        this.mEventClickListener = listener;
    }


    public void setMonthChangeListener(MonthLoader.MonthChangeListener monthChangeListener) {
        this.mWeekViewLoader = new MonthLoader(monthChangeListener);
    }



    public void setEventLongPressListener(EventLongPressListener eventLongPressListener) {
        this.mEventLongPressListener = eventLongPressListener;
    }


    public void setEmptyViewLongPressListener(EmptyViewLongPressListener emptyViewLongPressListener){
        this.mEmptyViewLongPressListener = emptyViewLongPressListener;
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

//disregard these functions as well
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


    /**
     * initializes the grabbing and calculating events into days and positions
     * @param events
     */
    public void loadEvents(List<event> events) {

        if (events != null) {

            orderEvents(events);
            days = sortToDays(events);
            daysLength = days.size();
            calculateEventPositions(days);
            invalidate();
        }


    }

    /**
     * orders events in eventlist by start time
     * @param events
     */
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

    /**
     * sorts events into days
     * @param events
     * @return
     */
    public List<day> sortToDays(List<event> events) {

        //large start time that will be replaced by first event
        int earliestTime = 100;

        //same on opposite end
        int latestTime = -1;

        event firstEventInDay = null;

        //will hold event days
        List<day> days = new ArrayList<>();

        //holds events in the day, will be added to day object
        List<event> currentDayEvents = new ArrayList<>();

        //holds current day time
        DateTime dayTime = null;

        //triggers end of loop when empty
        int eventsLength = events.size();

        //holds current day
        DateTime today = new DateTime();
        DateTime eventTime = null;
        DateTime lastEventTime = null;

        for (event event : events) {


            //if eventTime exists then next event in list is at the same time or later
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

                //if empty then has to be end of day, creates day out of currentDayEvents
                if (eventsLength == 0) {

                    days.add(createDay(eventTime, today, earliestTime, latestTime, currentDayEvents));

                    currentDayEvents.clear();
                }

            }

            //if event is same day, add to current day events
            else if (isSameDay(dayTime, eventTime)) {

                latestTime = event.endTime().getHourOfDay();

                currentDayEvents.add(event);
                eventsLength -= 1;
                if (eventsLength == 0) {

                    days.add(createDay(eventTime, today, earliestTime, latestTime, currentDayEvents));

                    currentDayEvents.clear();

                }

            }

            //the event is in another day so we save events collected into a day class
            //and add current event into cleared currentDayEvents
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

    /**
     * creates day
     * @param eventTime DateTime used to set dayTime
     * @param today DateTime used to check if its is currently today
     * @param earliestTime sets the start hour for drawing events
     * @param latestTime sets end for drawing events
     * @param currentDayEvents list of events in day
     * @return
     */
    public day createDay(DateTime eventTime, DateTime today, int earliestTime, int latestTime, List<event> currentDayEvents) {

        //formats eventTime into String for day
        DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM");
        String timeString = fmt.print(eventTime).toUpperCase();
        timeString += " " + eventTime.getDayOfMonth() + "th";


        if(isSameDay(eventTime, today)) {
            timeString += " TODAY";
        }

        else if (isSameDay(eventTime.plusDays(- 1), today)) {
            timeString += " Tomorrow ";
        }

        //currently start drawing times and hour before start time
        if (earliestTime -1 < 0 ) {
            earliestTime = 1;
        }
        //and 2 hours after
        if (latestTime + 2 > 24) {
            latestTime = 22;
        }

        //creates and returns day
        return new day(earliestTime, latestTime + 2, currentDayEvents, eventTime, timeString);

    }


    private void drawTimeForEachDay(Canvas canvas, List<day> days) {

        mHeaderColumnWidth = 80;

        // Draw the background color for the header column.
        canvas.drawRect(0, 75, mHeaderColumnWidth, getHeight(), mHeaderColumnBackgroundPaint);

        // Clip to paint in left column only.
        canvas.clipRect(0, 75, mHeaderColumnWidth, getHeight(), Region.Op.REPLACE);

        float dayAfterBot = 0;
        float dayAfterTop = 0;
        boolean isNextDay = false;

        //checks if last day so wont try to draw day after
        if(currentDayInt < days.size() - 1) {

            dayAfterBot = days.get(currentDayInt + 1).getHeaderYbot() + mCurrentOrigin.y;
            dayAfterTop = days.get(currentDayInt + 1).getHeaderYtop() + mCurrentOrigin.y;
            isNextDay = true;

        }


        //draws header lines
        if ( (dayAfterBot > 75 && dayAfterTop < getHeight()) && isNextDay) {

            canvas.drawLine(0, dayAfterTop, mHeaderColumnWidth, dayAfterTop, mHourSeparatorPaint);
            canvas.drawLine(0, dayAfterBot, mHeaderColumnWidth, dayAfterBot, mHourSeparatorPaint);
        }

        int currentDay = 0;

        //limits days to loop through to 3 maximum
        int daysSize = (days.size() >= 3 ? 2 : days.size());

        //again if last day prevent trying to load day after
        if (currentDayInt == days.size() - 1) {
            daysSize --;
        }


        //if not zero there is a day before
        if (currentDayInt != 0) {
            currentDay =  -1;
        }

        //loops through day before current and after if able to try todraw time strings
        while (currentDay < daysSize) {

            //working day
            day dayObj = days.get(currentDay + currentDayInt);

            //grabs list of all text, function from day class
            List<hourText> dayHourText = dayObj.getHourText();

            if (dayHourText != null) {


                for (int hourNum = 0; hourNum < dayObj.getHourText().size(); hourNum++) {

                    //creates a string for hour number and if its am or pm
                    hourText hour = dayObj.getHourText().get(hourNum);
                    float top = hour.getyStart() + mCurrentOrigin.y;
                    String time = hour.getNumberText();
                    String AMPM = hour.getAmPm();

                    //checks if it should draw
                    if (top < getHeight()) {
                        canvas.drawText(time, 40, top + mTimeTextHeight, mTimeTextPaint);
                        canvas.drawText(AMPM, 40, top + mTimeTextHeight * 2, mTimeTextAMPMPaint);
                        //linecount++;
                    }
                }
            }

                currentDay++;
        }
    }

    //makes list of time text for each day
    public void calculateTimeText() {

        int dayStart = 0;
        int lastDifference = 0;

        for (int j = 0; j < days.size(); j++) {
            int start = days.get(j).getStartHour();
            int difference = days.get(j).getEndHour() - start;

            //makes sure each day take up the whole screen
            if ( difference < 4) {

                difference = 4;

            }

            List<hourText> hours = new ArrayList<>();

            //adds up spacing for each day
            if (j > 0) {
                dayStart += lastDifference * mHourHeight;
            }

            lastDifference = difference;
            String AMPM = "AM";

            for (int i = 0; i < difference; i++) {
                float top = mHeaderHeight + mHeaderRowPadding * 2 + mHourHeight * i + mHeaderMarginBottom + dateSpace * j + dayStart;

                //
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

                //create new hourText andd add to hours
                hourText hour = new hourText(top, time, AMPM);
                hours.add(hour);


            }

            //sets hours to current day
            days.get(j).setHourText(hours);

        }





    }

    /**
     * draws hour separator lines and header
     * @param canvas
     * @param days
     * @param currentDayInt
     */
    public void drawTimeLines(Canvas canvas, List<day> days, int currentDayInt) {

        //start point
        float startFromPixel = mCurrentOrigin.x + mColumnGap + mHeaderColumnWidth;

        canvas.clipRect(mHeaderColumnWidth, 75, getWidth(), getHeight(), Region.Op.REPLACE);


        float currentDayYTop = days.get(currentDayInt).getHeaderYtop() + mCurrentOrigin.y;
        float currentDayYBot = days.get(currentDayInt).getHeaderYbot() + mCurrentOrigin.y;

        float dayAfterBot = 0;
        float dayAfterTop = 0;
        boolean isDayAfter = false;

        //checks if its not last day
        if (currentDayInt < days.size() - 1) {

            dayAfterBot = days.get(currentDayInt + 1).getHeaderYbot() + mCurrentOrigin.y;
            dayAfterTop = days.get(currentDayInt + 1).getHeaderYtop() + mCurrentOrigin.y;
            isDayAfter = true;

        }

        //draws day before header, might not be required
        if (currentDayYBot > 75 && currentDayYTop < getHeight()) {


            canvas.drawText(days.get(currentDayInt).getDayString() + ", " + days.get(currentDayInt).getEvents().size() + " events", getWidth()/2, currentDayYBot - 10, mHeaderTextPaint);
            canvas.drawLine(mHeaderColumnWidth, currentDayYTop, getWidth() , currentDayYTop, mHourSeparatorPaint );
            canvas.drawLine(mHeaderColumnWidth, currentDayYBot, getWidth(), currentDayYBot, mHourSeparatorPaint);

        }

        //draws day after header
        if ( (dayAfterBot > 75 && dayAfterTop < getHeight() && isDayAfter)) {

            canvas.drawText(days.get(currentDayInt + 1).getDayString() + ", " + days.get(currentDayInt + 1).getEvents().size() + " events", getWidth()/2, dayAfterBot - 10, mHeaderTextPaint);

            canvas.drawLine(mHeaderColumnWidth, dayAfterTop, getWidth(), dayAfterTop, mHourSeparatorPaint);
            canvas.drawLine(mHeaderColumnWidth, dayAfterBot, getWidth(), dayAfterBot, mHourSeparatorPaint);

        }

        //sets up to make sure doesnt try to draw day that doesn't exist

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


            //draws now line
            //// TODO: 2017-04-11 something wrong with now line, shifts up when goes back to activity
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

                    if (top > mHeaderHeight + mHeaderRowPadding * 2 && top < getHeight()) {

                        canvas.drawLine(startFromPixel, top, getWidth(), top, mHourSeparatorPaint);
                    }

                }

            }

            workingDay++;

        }
    }

    /**
     * calculates positions and number of time lines
     */
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

                    bottomScroll = top - getHeight() + mHourHeight;
                }

            }

        }

    }

    /**
     * gets position of events
     * @param days
     */
    public void calculateEventPositions(List<day> days) {

        List<eventBox> handledDayEvents = new ArrayList<>();
        handledEvents = new ArrayList<>();
        HashMap<Integer, eventBox> columnMap = new HashMap<Integer, eventBox>();

        float leftStart = mHeaderColumnWidth + 110;

        float baseTop = mHeaderHeight + mHeaderRowPadding * 2 + mTimeTextHeight / 2 + mHeaderMarginBottom;

        float dayOffset = 0;

        eventBox eventBox;
        int dayCount = currentDayInt;

        //loops through each day
        for (day currentDay : days) {

            baseTop += dayOffset;

            for (event event : currentDay.getEvents()) {

                eventFlagHeight = calculateEventHeight(event);

                //gets their y position
                eventBox = CalculatePositionBeforeConflict(baseTop, leftStart, event, currentDay.getStartHour(), eventFlagHeight, mEventWidth);

                //gets x position and adds to block if conflicting
                calculateConflict(handledDayEvents, eventBox, columnMap, mEventWidth, dayCount);

                //end of event loop
            }

            int difference = currentDay.getEndHour() - currentDay.getStartHour();

            if (difference < 4) {
                difference = 4;
            }
            //sets offset for each day so spacing works
            dayOffset = (difference * mHourHeight) + dateSpace;
            // end of day loop

            dayCount ++;
        }

    }

    //paints events on screen
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

                    if (event.getStartX() <= getWidth() && event.getFlagRight() >= mHeaderColumnWidth && (event.getTop() + mCurrentOrigin.y) <= getHeight()) {

                        //draws a rectangle
                        RectF rec = new RectF((int) event.getStartX(), (int) event.getTop() + (int) mCurrentOrigin.y, (int) event.getFlagRight(), (int) event.getBottom() + (int) mCurrentOrigin.y);

                        Paint rectPaint = new Paint();

                        event currentEvent = event.getEvent();
                        rectPaint.setARGB( 205, currentEvent.getRed(), currentEvent.getGreen(), currentEvent.getBlue());

                        //int colorId = event.getEvent().getColorId();

                        //Drawable d = getResources().getDrawable(colorId);
                        canvas.drawRoundRect(rec, 25, 25, rectPaint);

                        //d.setAlpha(210);

                        //d.setBounds((int) event.getStartX(), (int) event.getTop() + (int) mCurrentOrigin.y, (int) event.getFlagRight(), (int) event.getBottom() + (int) mCurrentOrigin.y);
                        //d.draw(canvas);

                        //sets emojis
                        ArrayList emojiList = new ArrayList();
                        emojiList.add(0x1f37a);
                        emojiList.add(0x1f61c);
                        emojiList.add(0x1f3c0);

                        String startTimeString = event.getEvent().getStartTimeString();

                        String AMPM = event.getEvent().getAMPM();

                        Paint textPaint = new Paint();
                        textPaint.setColor(Color.WHITE);
                        textPaint.setTextAlign(Paint.Align.LEFT);
                        textPaint.setTextSize(45);
                        textPaint.setTypeface(Typeface.create("Helvetica", Typeface.NORMAL));
                        float width = textPaint.measureText(startTimeString);

                        canvas.drawText(startTimeString, event.getStartX() + 10, event.getTop() + mCurrentOrigin.y + 40, textPaint);

                        String title = event.getEvent().getTitle();

                        Typeface mediumHelvetica = Typeface.createFromAsset(getContext().getAssets(), "fonts/HelveticaNeue-Medium (1).ttf");

                        textPaint.setTypeface(mediumHelvetica);



                        //// TODO: 2017-04-11 properly set up ellipses

                        if (textPaint.measureText(title) > mEventWidth - 10) {

                            String[] splitTitle = title.split("\\s+");


                            for (int e = 0; e < splitTitle.length; e++) {
                                Log.e("split Title", "" + splitTitle[e]);
                            }

                            String topString = "";
                            float topStringWidth = 0;
                            int i = 0;
                            while (i < splitTitle.length) {
                                if (topStringWidth + textPaint.measureText(splitTitle[i]) <= mEventWidth - 10) {

                                    topString += splitTitle[i] + " ";
                                    topStringWidth = textPaint.measureText(topString);
                                    i++;

                                }

                                else {

                                    break;
                                }
                            }
                            String botString = "";
                            float botStringWidth = 0;

                            for (int j = i; j < splitTitle.length; j++) {

                                //todo issue here, would need to elipse already added word at times

                                if (botStringWidth + textPaint.measureText(splitTitle[j]) <= mEventWidth - 10) {

                                    botString += splitTitle[j] + " ";
                                    botStringWidth = textPaint.measureText(botString);

                                } else {

                                    break;

//                                for (int u = 0; i < splitTitle[j].length(); u++) {
//
//                                    if (botStringWidth + textPaint.measureText((splitTitle[j].substring(u, u)), 0, 0) <= 380) {

                                    // botString += splitTitle[j].substring(u, u);
                                    //botStringWidth = textPaint.measureText(botString);

                                    //}

//                                    else if (splitTitle[j].length() - 1 == u) {
//
//
//                                    }

                                    //}


                                }

                            }

                            canvas.drawText(topString, event.getStartX() + 10, event.getTop() + 80 + mCurrentOrigin.y, textPaint);
                            canvas.drawText(botString, event.getStartX() + 10, event.getTop() + 130 + mCurrentOrigin.y, textPaint);


                        } else {
                            canvas.drawText(title, event.getStartX() + 10, event.getTop() + 80 + mCurrentOrigin.y, textPaint);
                        }

                        textPaint.setTextSize(25);
                        textPaint.setFakeBoldText(true);
                        canvas.drawText(AMPM, event.getStartX() + 10 + width, event.getTop() + mCurrentOrigin.y + 40, textPaint);


                        for (int i = 0; i < 3; i++) {

                            Drawable emoji = getResources().getDrawable(EmojiconHandler.getEmojiResource((int) emojiList.get(i)));

                            emoji.setBounds((int) event.getStartX() + 10 + (70 * i), (int) event.getBottom() + (int) mCurrentOrigin.y - 80, (int) event.getStartX() + 80 + (70 * i), (int) event.getBottom() + (int) mCurrentOrigin.y - 10);

                            emoji.draw(canvas);
                        }


                    }

                }

            }

            workingDay ++;
        }

    }

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


    public boolean flagsCollide(eventBox eventBox, eventBox box) {

        if (box.getTop() <= eventBox.getTop() && eventBox.getTop() <= box.getBottom()) {
            return true;
        }
        else {
            return false;
        }

    }

    public void calculateEventWidth() {

        mEventWidth = mHourHeight - 10;

    }

    //pretty redundent
    public float calculateEventHeight(event event) {

        return mHourHeight/2;

    }

    public eventBox CalculatePositionBeforeConflict(float baseTop, float leftStart, event event,
                                            int dayStartTime, float eventHeight, float eventWidth) {

        //calculates y position by adding hours and minutes to base y value

        float Length = (event.endTime().getHourOfDay() - event.startTime().getHourOfDay()) * mHourHeight
                - event.startTime().getMinuteOfDay()/60.0f * mHourHeight
                + event.endTime().getMinuteOfDay()/60.0f * mHourHeight;


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

                canvas.drawText(days.get(currentDayInt - 1).getDayString() + ", " + days.get(currentDayInt - 1).getEvents().size() + " events", getWidth() / 2, 65, mHeaderTextPaint);
                canvas.drawLine(0, 75, getWidth(), 75, mHourSeparatorPaint);
                currentDayInt -= 1;


            }

            else {

                int numberOfEventscurrent = days.get(currentDayInt).getEvents().size();
                int numberofEventsBefore = days.get(currentDayInt - 1).getEvents().size();

                canvas.drawText(days.get(currentDayInt - 1).getDayString() + ", " + numberofEventsBefore + " events", getWidth() / 2, dayBeforeBot - 10, mHeaderTextPaint);
                canvas.drawLine(0, dayBeforeBot, getWidth(), dayBeforeBot, mHourSeparatorPaint);

                canvas.drawText(days.get(currentDayInt).getDayString() + ", " + numberOfEventscurrent + " events", getWidth() / 2, currentYBot - 10, mHeaderTextPaint);


            }
        }

        else if (dayAfterTop <= 75 && goAfter) {

            //if (dayAfter <= mHeaderHeight + mHeaderRowPadding + 10) {
            if (dayAfterTop <= 0) {

                days.get(currentDayInt).setHeaderYBotBot( - mCurrentOrigin.y);

                int numberOfEvents = days.get(currentDayInt + 1).getEvents().size();


                canvas.drawText(textAfter + ", " + numberOfEvents + " events", getWidth() / 2, dayAfterBot - 10, mHeaderTextPaint);
                canvas.drawLine(0, dayAfterBot, getWidth(), dayAfterBot, mHourSeparatorPaint);
                currentDayInt += 1;

            }

            else {

                int numberOfEventscurrent = days.get(currentDayInt).getEvents().size();
                int numberofEventsAfter = days.get(currentDayInt + 1).getEvents().size();

                canvas.drawText(days.get(currentDayInt + 1).getDayString() + ", " + numberofEventsAfter + " events", getWidth() / 2, dayAfterBot - 10, mHeaderTextPaint);
                canvas.drawLine(0, dayAfterTop, getWidth(), dayAfterTop, mHourSeparatorPaint);


                canvas.drawText(days.get(currentDayInt).getDayString() + ", " + numberOfEventscurrent + " events", getWidth() / 2, dayAfterTop - 10, mHeaderTextPaint);

            }


        }

        else {

            canvas.drawText(days.get(currentDayInt).getDayString() + ", " + days.get(currentDayInt).getEvents().size() + " events", getWidth() / 2, 65, mHeaderTextPaint);
            canvas.drawLine(0, 75, getWidth(), 75, mHourSeparatorPaint);

        }

    }


    public void moveBlock(float dx, float position) {

        boolean isBroke = false;


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

    //everything below here is useless for now, might find use in the code


    /**
     * Draw the name of the event on top of the event rectangle.
     * @param event The event of which the title (and location) should be drawn.
     * @param rect The rectangle on which the text is to be drawn.
     * @param canvas The canvas to draw upon.
     * @param originalTop The original top position of the rectangle. The rectangle may have some of its portion outside of the visible area.
     * @param originalLeft The original left position of the rectangle. The rectangle may have some of its portion outside of the visible area.
     */
    //todo ill probobly not do the drawing here keep to handel title drawing better
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


    /////////////////////////////////////////////////////////////////
    //
    //      Public methods.
    //
    /////////////////////////////////////////////////////////////////

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


}
