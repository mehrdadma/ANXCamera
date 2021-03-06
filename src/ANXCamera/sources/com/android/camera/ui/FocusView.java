package com.android.camera.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MiuiSettings.ScreenEffect;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.Range;
import android.util.Rational;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.camera.Camera;
import com.android.camera.CameraSettings;
import com.android.camera.R;
import com.android.camera.Util;
import com.android.camera.data.DataRepository;
import com.android.camera.data.data.global.DataItemGlobal;
import com.android.camera.log.Log;
import com.android.camera.module.loader.camera2.Camera2DataContainer;
import com.android.camera.protocol.ModeCoordinatorImpl;
import com.android.camera.protocol.ModeProtocol.BottomPopupTips;
import com.android.camera.protocol.ModeProtocol.CameraAction;
import com.android.camera.protocol.ModeProtocol.EvChangedProtocol;
import com.android.camera.protocol.ModeProtocol.MainContentProtocol;
import com.android.camera.statistic.CameraStatUtil;
import com.android.camera.ui.drawable.focus.CameraFocusAnimateDrawable;
import com.android.camera.ui.drawable.focus.CameraIndicatorState;
import com.mi.config.b;
import com.mi.config.d;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import miui.view.animation.CubicEaseOutInterpolator;

@TargetApi(21)
public class FocusView extends View implements FocusIndicator, V6FunctionUI, Rotatable {
    public static final int CENTER_CAPTURE = 2;
    public static final int CENTER_CIRCLE = 1;
    public static final int CENTER_LOCK = 5;
    public static final int CENTER_MOON = 4;
    public static final int CENTER_NONE = 0;
    public static final int CENTER_SUN = 3;
    public static final int CURSOR_GATHER = 1;
    public static final int CURSOR_NORMAL = 0;
    public static final int CURSOR_SLIDE_BACK = 2;
    private static final int DISAPPEAR_TIME = 1500;
    private static final int END_DISAPPEAR_TIMEOUT = 800;
    private static final float GAP_NUM = d.getFloat(d.To, 0.0f).floatValue();
    public static final int MAX_SLIDE_DISTANCE = Util.dpToPixel(110.3f);
    private static final int MSG_ANIMATE_EV_CENTER = 7;
    private static final int MSG_FINISH_DISAPPEAR = 5;
    private static final int MSG_RESET_CENTER = 6;
    private static final int MSG_START_DISAPPEAR = 4;
    public static final int NORMAL_CAPTURE = 0;
    public static final int NORMAL_EV = 1;
    public static final int SET_BY_AUTO_DISAPPEAR = 6;
    public static final int SET_BY_AUTO_FOCUS_MOVING = 3;
    public static final int SET_BY_CANCEL_PREVIOUS_FOCUS = 2;
    public static final int SET_BY_CLEAR_VIEW = 7;
    public static final int SET_BY_DEVICE_KEEP_MOVING = 4;
    public static final int SET_BY_INITIALIZE = 8;
    public static final int SET_BY_RESET_CENTER = 5;
    public static final int SET_BY_RESET_TO_FACE_AREA = 9;
    public static final int SET_BY_TOUCH_DOWN = 1;
    private static final int START_DISAPPEAR_TIMEOUT = 5000;
    private static final int STATE_CHANGE_TIME = 500;
    private static final int STATE_FAIL = 3;
    private static final int STATE_IDLE = 0;
    private static final int STATE_START = 1;
    private static final int STATE_SUCCESS = 2;
    /* access modifiers changed from: private */
    public static final String TAG = "FocusView";
    /* access modifiers changed from: private */
    public static final int TRIANGLE_BASE_DIS = Util.dpToPixel(3.0f);
    public static final int ZERO_MINUS = 3;
    public static final int ZERO_PLUS = 4;
    /* access modifiers changed from: private */
    public Camera mActivity;
    /* access modifiers changed from: private */
    public RollAdapter mAdapter;
    private long mAdjustedDoneTime;
    /* access modifiers changed from: private */
    public boolean mBeingEvAdjusted;
    private int mBottomRelative;
    /* access modifiers changed from: private */
    public CameraFocusAnimateDrawable mCameraFocusAnimateDrawable;
    private Bitmap mCaptureBitmap;
    private int mCenterFlag = 0;
    private int mCenterX;
    private int mCenterY;
    /* access modifiers changed from: private */
    public int mCurrentDistanceY;
    /* access modifiers changed from: private */
    public int mCurrentItem;
    private int mCurrentMinusCircleCenter;
    private float mCurrentMinusCircleRadius;
    /* access modifiers changed from: private */
    public int mCurrentMode;
    private int mCurrentRadius;
    private int mCurrentRayBottom;
    private int mCurrentRayHeight;
    private int mCurrentRayWidth;
    /* access modifiers changed from: private */
    public int mCurrentViewState = 0;
    /* access modifiers changed from: private */
    public int mCursorState = 0;
    /* access modifiers changed from: private */
    public Rect mDisplayRect;
    /* access modifiers changed from: private */
    public float mEVAnimationRatio;
    /* access modifiers changed from: private */
    public long mEVAnimationStartTime;
    private float mEVCaptureRatio = -1.0f;
    /* access modifiers changed from: private */
    public float mEvValue;
    /* access modifiers changed from: private */
    public ExposureViewListener mExposureViewListener;
    private long mFailTime;
    private GestureDetector mGestureDetector;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            String access$000 = FocusView.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("msg=");
            sb.append(message.what);
            sb.append(" ");
            sb.append(FocusView.this.mAdapter != null);
            Log.d(access$000, sb.toString());
            if (FocusView.this.mAdapter != null) {
                int i = message.what;
                if (i == 4 || i == 5) {
                    if (!FocusView.this.mIsDraw || !FocusView.this.mIsDown) {
                        FocusView.this.reset(6);
                    } else {
                        FocusView.this.clearMessages();
                        sendEmptyMessageDelayed(5, 50);
                    }
                } else if (i == 6) {
                    FocusView.this.resetCenter();
                } else if (i == 7) {
                    long uptimeMillis = SystemClock.uptimeMillis() - FocusView.this.mEVAnimationStartTime;
                    if (uptimeMillis >= 520) {
                        FocusView.this.mCurrentViewState = 1;
                        FocusView.this.mCursorState = 0;
                        return;
                    }
                    FocusView.this.mEVAnimationRatio = ((float) uptimeMillis) / 500.0f;
                    FocusView.this.calculateAttribute();
                    FocusView.this.invalidate();
                    sendEmptyMessageDelayed(7, 20);
                }
            }
        }
    };
    private int mHeight;
    private CameraIndicatorState mIndicatorState;
    private Interpolator mInterpolator;
    /* access modifiers changed from: private */
    public boolean mIsDown;
    /* access modifiers changed from: private */
    public volatile boolean mIsDraw;
    /* access modifiers changed from: private */
    public boolean mIsEvAdjustable;
    private boolean mIsTouchFocus;
    /* access modifiers changed from: private */
    public int mLastItem;
    private Bitmap mLockBodyBitmap;
    private Bitmap mLockHeadBitmap;
    /* access modifiers changed from: private */
    public int mRotation;
    /* access modifiers changed from: private */
    public int mScrollDistanceY;
    private SimpleOnGestureListener mSimpleOnGestureListener = new SimpleOnGestureListener() {
        public boolean onDown(MotionEvent motionEvent) {
            boolean z = false;
            if (!FocusView.this.mIsDraw) {
                return false;
            }
            int access$1200 = FocusView.this.mCurrentMode;
            if ((access$1200 == 163 || access$1200 == 165 || access$1200 == 167 || access$1200 == 171 || access$1200 == 173 || access$1200 == 175 || access$1200 == 177) && FocusView.this.mExposureViewListener != null && FocusView.this.mExposureViewListener.isSupportFocusShoot() && !FocusView.this.mExposureViewListener.isShowAeAfLockIndicator()) {
                z = true;
            }
            if (FocusView.this.mCurrentViewState != 0 || !z || !FocusView.this.isInCircle(motionEvent.getX() - ((float) FocusView.this.mDisplayRect.left), motionEvent.getY() - ((float) FocusView.this.mDisplayRect.top), ((float) CameraFocusAnimateDrawable.BIG_RADIUS) * 0.5f)) {
                FocusView.this.mIsDown = true;
                FocusView.this.removeMessages();
                FocusView.this.setTouchDown();
            } else if (FocusView.this.mAdapter != null) {
                ((MainContentProtocol) ModeCoordinatorImpl.getInstance().getAttachProtocol(166)).removeTiltShiftMask();
                CameraAction cameraAction = (CameraAction) ModeCoordinatorImpl.getInstance().getAttachProtocol(161);
                if (cameraAction != null && !cameraAction.isDoingAction()) {
                    cameraAction.onShutterButtonClick(90);
                }
            }
            return true;
        }

        /* JADX WARNING: Removed duplicated region for block: B:30:0x0098  */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x00b1  */
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f2, float f3) {
            int i;
            int access$2500;
            float f4;
            if (!FocusView.this.mIsDown || motionEvent2 == null || !FocusView.this.mIsEvAdjustable) {
                return false;
            }
            int gestureOrientation = V6GestureRecognizer.getInstance(FocusView.this.mActivity).getGestureOrientation();
            if ((gestureOrientation != 200 || (FocusView.this.mRotation / 90) % 2 != 0) && (gestureOrientation != 100 || (FocusView.this.mRotation / 90) % 2 == 0)) {
                return false;
            }
            int access$2100 = FocusView.this.mScrollDistanceY;
            int access$2000 = FocusView.this.mRotation;
            if (access$2000 == 0) {
                f4 = ((float) access$2100) - f3;
            } else if (access$2000 == 90) {
                f4 = ((float) access$2100) + f2;
            } else if (access$2000 != 180) {
                if (access$2000 == 270) {
                    f4 = ((float) access$2100) - f2;
                }
                float f5 = ((float) Util.sWindowHeight) / 2.0f;
                int i2 = FocusView.MAX_SLIDE_DISTANCE;
                i = (int) (((float) access$2100) / (f5 / (((float) i2) / 4.0f)));
                FocusView.this.mCurrentDistanceY = Util.clamp(i, ((-i2) / 2) - FocusView.TRIANGLE_BASE_DIS, FocusView.MAX_SLIDE_DISTANCE / 2);
                if (i == FocusView.this.mCurrentDistanceY) {
                    FocusView.this.mScrollDistanceY = access$2100;
                }
                FocusView.this.mBeingEvAdjusted = true;
                access$2500 = FocusView.this.getItemByCoordinate();
                if (access$2500 != FocusView.this.mCurrentItem) {
                    if (FocusView.this.mCurrentViewState != 3 && access$2500 < FocusView.this.mCurrentItem && FocusView.this.mCurrentItem >= FocusView.this.mAdapter.getCenterIndex() && access$2500 < FocusView.this.mAdapter.getCenterIndex()) {
                        FocusView.this.startAnimation();
                        FocusView focusView = FocusView.this;
                        focusView.mLastItem = focusView.mCurrentItem;
                        FocusView.this.mCurrentViewState = 3;
                    } else if (FocusView.this.mCurrentViewState != 4 && access$2500 > FocusView.this.mCurrentItem && FocusView.this.mCurrentItem < FocusView.this.mAdapter.getCenterIndex() && access$2500 >= FocusView.this.mAdapter.getCenterIndex()) {
                        FocusView.this.startAnimation();
                        FocusView focusView2 = FocusView.this;
                        focusView2.mLastItem = focusView2.mCurrentItem;
                        FocusView.this.mCurrentViewState = 4;
                    }
                    FocusView.this.setCurrentItem(access$2500, false);
                }
                FocusView.this.mCameraFocusAnimateDrawable.setEvChanged((float) FocusView.this.mCurrentDistanceY, FocusView.this.mEvValue);
                if (FocusView.this.mCurrentViewState == 0 || FocusView.this.mCurrentViewState == 1) {
                    FocusView.this.mCurrentViewState = 1;
                    FocusView.this.calculateAttribute();
                    FocusView.this.invalidate();
                }
                return true;
            } else {
                f4 = ((float) access$2100) + f3;
            }
            access$2100 = (int) f4;
            float f52 = ((float) Util.sWindowHeight) / 2.0f;
            int i22 = FocusView.MAX_SLIDE_DISTANCE;
            i = (int) (((float) access$2100) / (f52 / (((float) i22) / 4.0f)));
            FocusView.this.mCurrentDistanceY = Util.clamp(i, ((-i22) / 2) - FocusView.TRIANGLE_BASE_DIS, FocusView.MAX_SLIDE_DISTANCE / 2);
            if (i == FocusView.this.mCurrentDistanceY) {
            }
            FocusView.this.mBeingEvAdjusted = true;
            access$2500 = FocusView.this.getItemByCoordinate();
            if (access$2500 != FocusView.this.mCurrentItem) {
            }
            FocusView.this.mCameraFocusAnimateDrawable.setEvChanged((float) FocusView.this.mCurrentDistanceY, FocusView.this.mEvValue);
            FocusView.this.mCurrentViewState = 1;
            FocusView.this.calculateAttribute();
            FocusView.this.invalidate();
            return true;
        }
    };
    private long mStartTime;
    private int mState;
    private long mSuccessTime;
    private int mWidth;

    @Retention(RetentionPolicy.SOURCE)
    public @interface CenterFlag {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface CursorState {
    }

    public interface ExposureViewListener {
        boolean isMeteringAreaOnly();

        boolean isNeedHapticFeedback();

        boolean isShowAeAfLockIndicator();

        boolean isShowCaptureButton();

        boolean isSupportAELockOnly();

        boolean isSupportFocusShoot();
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface SetPositionType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewState {
    }

    public FocusView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mActivity = (Camera) context;
        this.mInterpolator = new CubicEaseOutInterpolator();
        this.mGestureDetector = new GestureDetector(context, this.mSimpleOnGestureListener);
        this.mGestureDetector.setIsLongpressEnabled(false);
        this.mCaptureBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_capture);
        this.mIndicatorState = new CameraIndicatorState();
        this.mCameraFocusAnimateDrawable = new CameraFocusAnimateDrawable(getContext());
        this.mCameraFocusAnimateDrawable.setIndicatorData(this.mIndicatorState, this.mCaptureBitmap);
        this.mLockHeadBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_aeaf_lock_head);
        this.mLockBodyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_aeaf_lock_body);
        this.mCameraFocusAnimateDrawable.setLockIndicatorData(this.mIndicatorState, this.mLockHeadBitmap, this.mLockBodyBitmap);
        this.mCameraFocusAnimateDrawable.setCallback(this);
    }

    /* access modifiers changed from: private */
    public void calculateAttribute() {
        float itemRatio = getItemRatio(this.mCurrentItem);
        float itemRatio2 = getItemRatio(this.mLastItem);
        int i = this.mCurrentViewState;
        int i2 = 1;
        if (i == 0) {
            if (this.mIsTouchFocus) {
                ExposureViewListener exposureViewListener = this.mExposureViewListener;
                if (exposureViewListener != null && exposureViewListener.isShowCaptureButton() && this.mExposureViewListener.isSupportFocusShoot()) {
                    i2 = 2;
                }
            }
            this.mCenterFlag = i2;
        } else if (i != 1) {
            if (i == 3) {
                float f2 = this.mEVAnimationRatio;
                if (f2 <= 0.5f) {
                    float f3 = f2 * 2.0f;
                    this.mCurrentRayWidth = Util.dpToPixel(1.5f);
                    float f4 = 1.0f - f3;
                    float f5 = (itemRatio2 * f4) - f3;
                    this.mCurrentRayHeight = Util.dpToPixel((f5 * 2.0f) + 5.0f);
                    this.mCurrentRayBottom = Util.dpToPixel((f5 * 3.0f) + 7.5f);
                    this.mCurrentRadius = Util.dpToPixel((f3 * 3.0f) + 5.0f + (itemRatio2 * 2.0f * f4));
                    this.mCenterFlag = 3;
                } else {
                    float f6 = (f2 - 0.5f) * 2.0f;
                    this.mCurrentRadius = Util.dpToPixel(8.0f - (((1.0f - itemRatio) * f6) * 2.0f));
                    int i3 = this.mCurrentRadius;
                    float f7 = 1.0f - f6;
                    this.mCurrentMinusCircleCenter = (int) (((float) i3) * ((0.914f * f7) + 0.5f));
                    this.mCurrentMinusCircleRadius = ((float) i3) * ((f7 * 0.2f) + 0.8f);
                    this.mCenterFlag = 4;
                }
            } else if (i != 4) {
                this.mCenterFlag = 1;
            } else {
                float f8 = this.mEVAnimationRatio;
                if (f8 < 0.5f) {
                    float f9 = f8 * 2.0f;
                    this.mCurrentRadius = Util.dpToPixel(((itemRatio2 + ((1.0f - itemRatio2) * f9)) * 2.0f) + 6.0f);
                    int i4 = this.mCurrentRadius;
                    this.mCurrentMinusCircleCenter = (int) (((float) i4) * ((0.914f * f9) + 0.5f));
                    this.mCurrentMinusCircleRadius = ((float) i4) * ((f9 * 0.2f) + 0.8f);
                    this.mCenterFlag = 4;
                } else {
                    float f10 = (f8 - 0.5f) * 2.0f;
                    this.mCurrentRayWidth = Util.dpToPixel(1.5f);
                    float f11 = 1.0f - f10;
                    float f12 = (itemRatio * f10) - f11;
                    this.mCurrentRayHeight = Util.dpToPixel((f12 * 2.0f) + 5.0f);
                    this.mCurrentRayBottom = Util.dpToPixel((f12 * 3.0f) + 7.5f);
                    this.mCurrentRadius = Util.dpToPixel((f11 * 3.0f) + 5.0f + (itemRatio * 2.0f * f10));
                    this.mCenterFlag = 3;
                }
            }
        } else if (this.mCurrentItem < this.mAdapter.getCenterIndex()) {
            this.mCurrentRadius = Util.dpToPixel((itemRatio * 2.0f) + 6.0f);
            int i5 = this.mCurrentRadius;
            this.mCurrentMinusCircleCenter = (int) (((float) i5) * 0.5f);
            this.mCurrentMinusCircleRadius = ((float) i5) * 0.8f;
            this.mCenterFlag = 4;
        } else {
            this.mCurrentRayWidth = Util.dpToPixel(1.5f);
            float f13 = (2.0f * itemRatio) + 5.0f;
            this.mCurrentRayHeight = Util.dpToPixel(f13);
            this.mCurrentRayBottom = Util.dpToPixel((itemRatio * 3.0f) + 7.5f);
            this.mCurrentRadius = Util.dpToPixel(f13);
            this.mCenterFlag = 3;
        }
        this.mCameraFocusAnimateDrawable.cancelResetCenter();
        CameraIndicatorState cameraIndicatorState = this.mIndicatorState;
        cameraIndicatorState.mCenterFlag = this.mCenterFlag;
        cameraIndicatorState.mCurrentRadius = this.mCurrentRadius;
        cameraIndicatorState.mCurrentAngle = (float) getCurrentAngle();
        CameraIndicatorState cameraIndicatorState2 = this.mIndicatorState;
        cameraIndicatorState2.mCurrentRayWidth = this.mCurrentRayWidth;
        cameraIndicatorState2.mCurrentRayHeight = this.mCurrentRayHeight;
        cameraIndicatorState2.mCurrentRayBottom = this.mCurrentRayBottom;
        cameraIndicatorState2.mCurrentMinusCircleCenter = this.mCurrentMinusCircleCenter;
        cameraIndicatorState2.mCurrentMinusCircleRadius = this.mCurrentMinusCircleRadius;
    }

    /* access modifiers changed from: private */
    public void clearMessages() {
        this.mHandler.removeMessages(4);
        this.mHandler.removeMessages(5);
        this.mHandler.removeMessages(6);
        this.mHandler.removeMessages(7);
    }

    private int getCurrentAngle() {
        int i;
        if (this.mCursorState == 2) {
            int i2 = this.mCurrentViewState;
            if (!(i2 == 3 || i2 == 4)) {
                i = Util.clamp(this.mCurrentItem >= this.mAdapter.getCenterIndex() ? ((this.mCurrentItem - this.mAdapter.getCenterIndex()) * ScreenEffect.SCREEN_PAPER_MODE_TWILIGHT_START_DEAULT) / this.mAdapter.getCenterIndex() : 0, 0, (int) ScreenEffect.SCREEN_PAPER_MODE_TWILIGHT_START_DEAULT);
                return 360 - i;
            }
        }
        int i3 = this.mCurrentViewState;
        if (i3 == 1) {
            int clamp = Util.clamp(this.mBottomRelative - this.mCurrentDistanceY, 0, MAX_SLIDE_DISTANCE);
            int i4 = MAX_SLIDE_DISTANCE;
            i = Util.clamp(clamp >= i4 / 2 ? ((clamp - (i4 / 2)) * ScreenEffect.SCREEN_PAPER_MODE_TWILIGHT_START_DEAULT) / (i4 / 2) : 0, 0, (int) ScreenEffect.SCREEN_PAPER_MODE_TWILIGHT_START_DEAULT);
            return 360 - i;
        } else if (i3 == 3) {
            return Util.clamp((int) (this.mEVAnimationRatio * 2.0f * 135.0f), 0, 135);
        } else {
            if (i3 == 4) {
                return Util.clamp((int) ((1.0f - ((this.mEVAnimationRatio - 0.5f) * 2.0f)) * 135.0f), 0, 135);
            }
            return 0;
        }
    }

    private float getInterpolation(float f2) {
        float interpolation = this.mInterpolator.getInterpolation(f2);
        if (((double) interpolation) > 1.0d) {
            return 1.0f;
        }
        return interpolation;
    }

    /* access modifiers changed from: private */
    public int getItemByCoordinate() {
        return Util.clamp((this.mAdapter.getMaxItem() * (this.mBottomRelative - this.mCurrentDistanceY)) / MAX_SLIDE_DISTANCE, 0, this.mAdapter.getMaxItem());
    }

    private float getItemRatio(int i) {
        float maxItem = ((float) i) / ((float) this.mAdapter.getMaxItem());
        if (maxItem >= 0.5f) {
            maxItem -= 0.5f;
        }
        return maxItem * 2.0f;
    }

    private void initRect() {
        this.mDisplayRect = Util.getPreviewRect(getContext());
        this.mWidth = this.mDisplayRect.width();
        this.mHeight = this.mDisplayRect.height();
        this.mCenterX = this.mWidth / 2;
        this.mCenterY = this.mHeight / 2;
        setPivotX((float) this.mCenterX);
        setPivotY((float) this.mCenterY);
        CameraFocusAnimateDrawable cameraFocusAnimateDrawable = this.mCameraFocusAnimateDrawable;
        boolean z = true;
        if (1 != getLayoutDirection()) {
            z = false;
        }
        cameraFocusAnimateDrawable.setRtlAndDisplayRect(z, this.mDisplayRect);
        this.mCameraFocusAnimateDrawable.setCenter(this.mCenterX, this.mCenterY);
    }

    /* access modifiers changed from: private */
    public boolean isInCircle(float f2, float f3, float f4) {
        float f5 = f2 - ((float) this.mCenterX);
        float f6 = f3 - ((float) this.mCenterY);
        return Math.sqrt((double) ((f5 * f5) + (f6 * f6))) <= ((double) f4);
    }

    private boolean isStableStart() {
        if (!this.mIsTouchFocus) {
            return false;
        }
        ExposureViewListener exposureViewListener = this.mExposureViewListener;
        if (exposureViewListener == null) {
            return false;
        }
        return exposureViewListener.isMeteringAreaOnly();
    }

    private void processParameterIfNeeded(float f2) {
        if (this.mIsTouchFocus && this.mEVCaptureRatio != -1.0f && this.mCenterFlag == 0) {
            this.mCenterFlag = 2;
        }
    }

    private void reload() {
        RollAdapter rollAdapter = this.mAdapter;
        if (rollAdapter != null) {
            this.mCurrentItem = rollAdapter.getItemIndexByValue(Integer.valueOf(CameraSettings.readExposure()));
            updateEV();
        }
    }

    /* access modifiers changed from: private */
    public void removeMessages() {
    }

    /* access modifiers changed from: private */
    public void reset(int i) {
        if (this.mIsDraw || i == 8 || i == 2) {
            clearMessages();
            this.mState = 0;
            setPosition(i, this.mWidth / 2, this.mHeight / 2);
            this.mCurrentViewState = 0;
            this.mCenterFlag = 0;
            this.mIsDown = false;
            stopEvAdjust();
            setDraw(false);
            invalidate();
        }
    }

    /* access modifiers changed from: private */
    public void resetCenter() {
        this.mCurrentViewState = 0;
        this.mAdjustedDoneTime = System.currentTimeMillis();
        ExposureViewListener exposureViewListener = this.mExposureViewListener;
        if (exposureViewListener != null) {
            if (exposureViewListener.isShowAeAfLockIndicator() && CameraSettings.isAEAFLockSupport()) {
                this.mCenterFlag = 5;
                this.mIndicatorState.mCenterFlag = this.mCenterFlag;
                invalidate();
            } else if (this.mExposureViewListener.isShowCaptureButton() && this.mExposureViewListener.isSupportFocusShoot()) {
                this.mCenterFlag = 2;
                this.mIndicatorState.mCenterFlag = this.mCenterFlag;
                invalidate();
            }
        }
    }

    private void resetEvValue() {
        this.mBeingEvAdjusted = false;
        this.mEvValue = 0.0f;
        this.mCurrentItem = 0;
        this.mScrollDistanceY = 0;
        this.mCurrentDistanceY = 0;
        this.mCameraFocusAnimateDrawable.reset();
    }

    /* access modifiers changed from: private */
    public void setCurrentItem(int i, boolean z) {
        if (i != this.mCurrentItem) {
            this.mCurrentItem = i;
            if (this.mAdapter != null) {
                EvChangedProtocol evChangedProtocol = (EvChangedProtocol) ModeCoordinatorImpl.getInstance().getAttachProtocol(169);
                if (evChangedProtocol == null) {
                    Log.d(TAG, "needEvPresenter");
                } else {
                    evChangedProtocol.onEvChanged(this.mAdapter.getItemValue(i), 1);
                }
            }
            updateEV();
        }
    }

    private void setDraw(boolean z) {
        if (z && this.mIsTouchFocus && this.mIsDraw != z) {
            reload();
        }
        this.mIsDraw = z;
    }

    /* access modifiers changed from: private */
    public void setTouchDown() {
        this.mBottomRelative = (int) (((float) MAX_SLIDE_DISTANCE) * 0.5f);
    }

    private void showTipMessage(int i, @StringRes int i2) {
        BottomPopupTips bottomPopupTips = (BottomPopupTips) ModeCoordinatorImpl.getInstance().getAttachProtocol(175);
        if (bottomPopupTips != null) {
            if (bottomPopupTips.isPortraitHintVisible()) {
                bottomPopupTips.setPortraitHintVisible(8);
            }
            bottomPopupTips.showTips(i, i2, 2);
        }
    }

    /* access modifiers changed from: private */
    public void startAnimation() {
        this.mEVAnimationStartTime = SystemClock.uptimeMillis();
        this.mHandler.removeMessages(7);
        this.mHandler.sendEmptyMessage(7);
    }

    private void stopEvAdjust() {
        if (this.mBeingEvAdjusted) {
            this.mBeingEvAdjusted = false;
            EvChangedProtocol evChangedProtocol = (EvChangedProtocol) ModeCoordinatorImpl.getInstance().getAttachProtocol(169);
            if (evChangedProtocol != null) {
                evChangedProtocol.onEvChanged(0, 2);
            }
        }
    }

    private void updateEV() {
        Rational exposureCompensationRational = Camera2DataContainer.getInstance().getCurrentCameraCapabilities().getExposureCompensationRational();
        RollAdapter rollAdapter = this.mAdapter;
        if (rollAdapter != null) {
            this.mEvValue = ((float) rollAdapter.getItemValue(this.mCurrentItem)) * exposureCompensationRational.floatValue();
        } else {
            this.mEvValue = 0.0f;
        }
    }

    public void clear() {
        reset(7);
    }

    public void clear(int i) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("clear: ");
        sb.append(i);
        Log.d(str, sb.toString());
        CameraFocusAnimateDrawable cameraFocusAnimateDrawable = this.mCameraFocusAnimateDrawable;
        if (cameraFocusAnimateDrawable != null) {
            cameraFocusAnimateDrawable.cancelFocusingAnimation();
        }
        reset(i);
    }

    public void enableControls(boolean z) {
    }

    public void initialize(ExposureViewListener exposureViewListener) {
        this.mExposureViewListener = exposureViewListener;
        reset(8);
    }

    public void invalidateDrawable(Drawable drawable) {
        invalidate();
    }

    public boolean isEvAdjusted() {
        return this.mBeingEvAdjusted;
    }

    public boolean isEvAdjustedTime() {
        return isShown() && this.mIsTouchFocus && (this.mBeingEvAdjusted || !Util.isTimeout(System.currentTimeMillis(), this.mAdjustedDoneTime, 1500));
    }

    public boolean isVisible() {
        return this.mIsDraw;
    }

    public void onCameraOpen() {
    }

    public void onCreate() {
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        CameraFocusAnimateDrawable cameraFocusAnimateDrawable = this.mCameraFocusAnimateDrawable;
        if (cameraFocusAnimateDrawable != null) {
            cameraFocusAnimateDrawable.cancelFocusingAnimation();
            this.mCameraFocusAnimateDrawable.setCallback(null);
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mIsDraw) {
            CameraFocusAnimateDrawable cameraFocusAnimateDrawable = this.mCameraFocusAnimateDrawable;
            if (cameraFocusAnimateDrawable != null) {
                cameraFocusAnimateDrawable.draw(canvas);
            }
        }
    }

    public void onPause() {
        setDraw(false);
    }

    public void onResume() {
        setDraw(false);
    }

    public boolean onViewTouchEvent(MotionEvent motionEvent) {
        if (this.mAdapter == null || !this.mIsTouchFocus) {
            return false;
        }
        if (this.mState != 2 && !isStableStart()) {
            return false;
        }
        this.mGestureDetector.onTouchEvent(motionEvent);
        boolean z = this.mIsDown;
        if (motionEvent.getActionMasked() == 5 && this.mIsDown) {
            this.mIsDown = false;
        }
        if (1 == motionEvent.getAction() || 3 == motionEvent.getAction()) {
            if (this.mBeingEvAdjusted) {
                CameraStatUtil.trackEvAdjusted();
                stopEvAdjust();
                this.mAdjustedDoneTime = System.currentTimeMillis();
                this.mHandler.removeMessages(6);
                this.mHandler.sendEmptyMessageDelayed(6, 1000);
            }
            if (this.mIsDraw) {
                this.mIsDown = false;
            }
        }
        return z || this.mIsDown;
    }

    public void reInit() {
        Log.d(TAG, "onCameraOpen>>");
        initRect();
        Range exposureCompensationRange = Camera2DataContainer.getInstance().getCurrentCameraCapabilities().getExposureCompensationRange();
        int intValue = ((Integer) exposureCompensationRange.getLower()).intValue();
        int intValue2 = ((Integer) exposureCompensationRange.getUpper()).intValue();
        if (b.Ln) {
            intValue = -3;
            intValue2 = 4;
        }
        if (!(intValue2 == 0 || intValue2 == intValue)) {
            float f2 = GAP_NUM;
            this.mAdapter = new FloatSlideAdapter(intValue, intValue2, f2 == 0.0f ? 1.0f : ((float) (intValue2 - intValue)) / f2);
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onCameraOpen: adapter=");
            sb.append(this.mAdapter);
            Log.w(str, sb.toString());
            if (this.mAdapter != null) {
                resetEvValue();
                this.mCurrentMode = DataRepository.dataItemGlobal().getCurrentMode();
                if (this.mCurrentMode != 167) {
                    this.mCameraFocusAnimateDrawable.setEvTextVisible(8);
                } else {
                    this.mCameraFocusAnimateDrawable.setEvTextVisible(0);
                }
                setEvAdjustable(CameraSettings.isEvAdjustable());
                int itemIndexByValue = this.mAdapter.getItemIndexByValue(Integer.valueOf(CameraSettings.readExposure()));
                if (itemIndexByValue < 0) {
                    this.mCurrentItem = this.mAdapter.getMaxItem() / 2;
                } else {
                    this.mCurrentItem = itemIndexByValue;
                }
                updateEV();
            }
        }
    }

    public void releaseListener() {
        this.mExposureViewListener = null;
    }

    public void setEvAdjustable(boolean z) {
        if (this.mAdapter != null) {
            this.mIsEvAdjustable = z;
            resetEvValue();
            calculateAttribute();
            this.mCameraFocusAnimateDrawable.setEvAdjustVisible(z ? 0 : 8);
            if (!z) {
                this.mCameraFocusAnimateDrawable.setEvTextVisible(8);
            }
            invalidate();
        }
    }

    public void setFocusType(boolean z) {
        this.mIsTouchFocus = z;
    }

    public void setOrientation(int i, boolean z) {
        if (this.mRotation != i) {
            this.mRotation = i;
            this.mCameraFocusAnimateDrawable.setOrientation(i);
            if (this.mIsDraw) {
                invalidate();
            }
        }
    }

    public void setPosition(int i, int i2, int i3) {
        this.mCenterX = i2;
        this.mCenterY = i3;
        setPivotX((float) i2);
        setPivotY((float) i3);
        this.mCameraFocusAnimateDrawable.setCenter(i2, i3);
        removeMessages();
        if (this.mEvValue != 0.0f && (i == 1 || i == 2 || i == 5)) {
            EvChangedProtocol evChangedProtocol = (EvChangedProtocol) ModeCoordinatorImpl.getInstance().getAttachProtocol(169);
            if (evChangedProtocol != null) {
                evChangedProtocol.resetEvValue();
            }
        }
        if (this.mCurrentDistanceY != 0 && i != 7) {
            resetEvValue();
        }
    }

    public void showFail() {
        this.mCameraFocusAnimateDrawable.startFocusFailAnimation();
        Log.d(TAG, "showFail");
        if (this.mState == 1) {
            clearMessages();
            setDraw(true);
            this.mState = 3;
            this.mFailTime = SystemClock.uptimeMillis();
            this.mHandler.sendEmptyMessageDelayed(5, 800);
            invalidate();
        }
    }

    public void showStart() {
        Log.d(TAG, "showStart");
        clearMessages();
        this.mState = 1;
        this.mCursorState = 1;
        this.mStartTime = SystemClock.uptimeMillis();
        setDraw(true);
        this.mCameraFocusAnimateDrawable.startTouchDownAnimation();
        if (isStableStart()) {
            this.mEVCaptureRatio = 1.0f;
            showSuccess();
        } else {
            this.mEVCaptureRatio = -1.0f;
            this.mCenterFlag = 0;
            processParameterIfNeeded(0.0f);
            this.mHandler.sendEmptyMessageDelayed(4, 5000);
        }
        invalidate();
    }

    public void showSuccess() {
        Log.d(TAG, "showSuccess");
        if (this.mState == 1) {
            clearMessages();
            setDraw(true);
            this.mState = 2;
            this.mSuccessTime = SystemClock.uptimeMillis();
            if (!this.mIsTouchFocus) {
                this.mHandler.sendEmptyMessageDelayed(5, 800);
            }
        }
        ExposureViewListener exposureViewListener = this.mExposureViewListener;
        if (exposureViewListener == null) {
            Log.d(TAG, "needExposurePresenter");
            return;
        }
        if (exposureViewListener.isShowAeAfLockIndicator() && CameraSettings.isAEAFLockSupport()) {
            int i = R.string.hint_ae_af_lock;
            if (this.mExposureViewListener.isSupportAELockOnly()) {
                i = R.string.hint_ae_lock;
            }
            if (b.Tc()) {
                DataItemGlobal dataItemGlobal = DataRepository.dataItemGlobal();
                String str = CameraSettings.KEY_EN_FIRST_CHOICE_LOCK_AE_AF_TOAST;
                if (dataItemGlobal.getBoolean(str, false) && getResources().getConfiguration().getLocales().get(0).getLanguage().equalsIgnoreCase("en")) {
                    DataRepository.dataItemGlobal().editor().putBoolean(str, false).apply();
                    i = R.string.hint_ae_af_lock_extra;
                }
            }
            showTipMessage(8, i);
            this.mCenterFlag = 5;
        } else if (!this.mExposureViewListener.isShowCaptureButton() || !this.mExposureViewListener.isSupportFocusShoot()) {
            this.mCenterFlag = 1;
        } else {
            this.mCenterFlag = 2;
        }
        this.mCameraFocusAnimateDrawable.startFocusSuccessAnimation(this.mCenterFlag, this.mIsTouchFocus);
    }
}
