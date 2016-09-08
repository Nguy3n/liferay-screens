package com.liferay.mobile.screens.westerosemployees.Views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import com.liferay.mobile.screens.westerosemployees.R;
import com.liferay.mobile.screens.westerosemployees.utils.CardState;
import com.liferay.mobile.screens.westerosemployees.utils.PixelUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Víctor Galán Grande
 */

public class Card extends FrameLayout {

	public static final int MARGIN = 5;
	public static final int DURATION_MILLIS = 300;
	public static final int CARD_SIZE = 60;

	public static int NORMAL_Y = MARGIN * 5;
	public static int BACKGROUND_Y = MARGIN;

	public Card(Context context) {
		super(context);
	}

	public Card(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public Card(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	public Card(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs);
	}

	protected void init(Context context, AttributeSet attributeSet) {

		TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.Card);

		normalY = array.getDimensionPixelSize(R.styleable.Card_normalMarginTop,
			PixelUtil.pixelFromDp(getContext(), NORMAL_Y));
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		arrows = getViewsByTag(this, "arrow");
	}

	}

	private void takeSubviewsOffScreen() {
		for (int i = 1; i < getChildCount(); i++) {
			getChildAt(i).setX(maxWidth);
		}
	}

	public void initPosition(int minimizedPosition, int maxHeight, int maxWidth) {
		this.minimizedPosition = minimizedPosition;
		this.backgroundY = PixelUtil.pixelFromDp(getContext(), BACKGROUND_Y);
		this.maxHeight = maxHeight;
		this.maxWidth = maxWidth;

		setY(maxHeight);

		takeSubviewsOffScreen();
	}

	public void setState(CardState state) {
		switch (state) {
			case BACKGROUND:
				animate().setDuration(DURATION_MILLIS).scaleX(0.95f);
				animate().setDuration(DURATION_MILLIS).translationY(backgroundY);
				break;
			case NORMAL:
				showArrows(true);
				setNormalModeHeight();
				animate().setDuration(DURATION_MILLIS).scaleX(1f);
				animate().setDuration(DURATION_MILLIS).translationY(normalY);
				break;
			case MINIMIZED:
				showArrows(false);
				animate().setDuration(DURATION_MILLIS).translationY(minimizedPosition);
				break;
			case MAXIMIZED:
				setMaximizedModeHeight();
				animate().setDuration(DURATION_MILLIS).translationY(0);
				break;

			case HIDDEN:
				animate().setDuration(DURATION_MILLIS).translationY(maxHeight);
				break;
		}

		cardState = state;
	}

	public void goRight() {
		goRight(null);
	}

	public void goRight(Runnable endAction) {

		View current = getChildAt(index);
		View next = getChildAt(index + 1);

		if (next != null) {
			index++;
			current.animate().translationX(-maxWidth);
			next.animate().translationX(0).withEndAction(endAction);
		}
	}

	public void goLeft() {
		goLeft(null);
	}

	public void goLeft(Runnable endAction) {

		View current = getChildAt(index);
		View next = getChildAt(index - 1);

		if (next != null) {
			index--;
			next.animate().translationX(0).withEndAction(endAction);
			current.animate().translationX(maxWidth);
		}
	}

	protected void setMaximizedModeHeight() {
		TransitionManager.beginDelayedTransition(this);
		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = maxHeight;
		setLayoutParams(params);
	}

	protected void setNormalModeHeight() {
		TransitionManager.beginDelayedTransition(this);
		if (statusBarHeight == 0) {
			Rect rectangle = new Rect();
			Window window = ((Activity) getContext()).getWindow();
			window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
			statusBarHeight = rectangle.top;
		}

		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = maxHeight - (maxHeight - minimizedPosition - PixelUtil.pixelFromDp(getContext(), CARD_SIZE)
			+ statusBarHeight);
		setLayoutParams(params);
	}

	public CardState getCardState() {
		return cardState;
	}

	public int getCardSubviewCurrentIndex() {
		return index;
	}

	protected void showArrows(boolean show) {
		int visibility = show ? VISIBLE : INVISIBLE;

		for (View arrow : arrows) {
			if (isChildArrow(arrow)) {
				arrow.setVisibility(visibility);
			}
		}
	}

	protected boolean isChildArrow(View arrow) {
		return arrow.getParent().getParent().equals(this);
	}

	protected static List<View> getViewsByTag(ViewGroup root, String tag){
		List<View> views = new ArrayList<>();
		final int childCount = root.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = root.getChildAt(i);
			if (child instanceof ViewGroup) {
				views.addAll(getViewsByTag((ViewGroup) child, tag));
			}

			final Object tagObj = child.getTag();
			if (tagObj != null && tagObj.equals(tag)) {
				views.add(child);
			}
		}

		return views;
	}

	protected static int statusBarHeight;

	protected List<View> arrows;
	protected int index;

	protected int maxWidth;
	protected int maxHeight;

	protected int minimizedPosition;
	protected int normalY;
	protected int backgroundY;

	protected CardState cardState;
}
