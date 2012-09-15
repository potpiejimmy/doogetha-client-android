package de.potpiejimmy.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.doogetha.client.android.R;

/**
 * List view extension with pull-to-refresh support
 */
public class PullRefreshableListView extends ListView {

	private static final float PULL_WEIGHT = 1.7f;
	private static final int BOUNCE_ANIM_DURATION = 700;
	private static final int ROTATE_ARROW_ANIM_DURATION = 250;

	private static enum RefreshState {
		PULL_TO_REFRESH, RELEASE_TO_REFRESH, REFRESHING
	}

	/**
	 * Callback interface
	 */
	public static interface OnRefreshListener {

		/**
		 * refresh
		 */
		public void onRefresh();
	}

	private static int measuredHeaderHeight;

	private float previousY;
	private int headerPadding;
	private boolean scrollbarEnabled;
	private boolean lockScrollWhileRefreshing = true;
	private boolean hasResetHeader;
	private boolean animating = false;

	private RefreshState state;
	private LinearLayout headerContainer;
	private RelativeLayout header;
	private RotateAnimation flipAnimation;
	private RotateAnimation reverseFlipAnimation;
	private TranslateAnimation bounceAnimation;
	private ImageView image;
	private ProgressBar spinner;
	private TextView text;
	private OnItemClickListener onItemClickListener;
	private OnRefreshListener onRefreshListener;

	public PullRefreshableListView(Context context) {
		super(context);
		init();
	}

	public PullRefreshableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PullRefreshableListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	/**
	 * Activate an OnRefreshListener to get notified on 'pull to refresh'
	 * events.
	 * 
	 * @param onRefreshListener
	 *            The OnRefreshListener to get notified
	 */
	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
		this.onRefreshListener = onRefreshListener;
	}

	/**
	 * @return If the list is in 'Refreshing' state
	 */
	public boolean isRefreshing() {
		return state == RefreshState.REFRESHING;
	}

	/**
	 * Default is true. When lockScrollWhileRefreshing is set to true, the list
	 * cannot scroll when in 'refreshing' mode. It's 'locked' on refreshing.
	 * 
	 * @param lockScrollWhileRefreshing
	 */
	public void setLockScrollWhileRefreshing(boolean lockScrollWhileRefreshing) {
		this.lockScrollWhileRefreshing = lockScrollWhileRefreshing;
	}

	/**
	 * Explicitly set the state to refreshing. This is useful when you want to
	 * show the spinner and 'Refreshing' text when the refresh was not triggered
	 * by 'pull to refresh', for example on start.
	 */
	public void setRefreshing() {
		state = RefreshState.REFRESHING;
		scrollTo(0, 0);
		setUiRefreshing();
		setHeaderPadding(0);
	}

	/**
	 * Set the state back to 'pull to refresh'. Call this method when refreshing
	 * the data is finished.
	 */
	public void onRefreshComplete() {
		state = RefreshState.PULL_TO_REFRESH;
		resetHeader();
	}

	private void init() {
		setVerticalFadingEdgeEnabled(false);

		headerContainer = (LinearLayout) LayoutInflater.from(getContext())
				.inflate(R.layout.pull_refreshable_listview_header, null);
		header = (RelativeLayout) headerContainer.findViewById(R.id.header);
		text = (TextView) header.findViewById(R.id.text);
		image = (ImageView) header.findViewById(R.id.image);
		spinner = (ProgressBar) header.findViewById(R.id.spinner);

		flipAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		flipAnimation.setInterpolator(new LinearInterpolator());
		flipAnimation.setDuration(ROTATE_ARROW_ANIM_DURATION);
		flipAnimation.setFillAfter(true);

		reverseFlipAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseFlipAnimation.setInterpolator(new LinearInterpolator());
		reverseFlipAnimation.setDuration(ROTATE_ARROW_ANIM_DURATION);
		reverseFlipAnimation.setFillAfter(true);

		addHeaderView(headerContainer);
		setState(RefreshState.PULL_TO_REFRESH);
		scrollbarEnabled = isVerticalScrollBarEnabled();

		ViewTreeObserver vto = header.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new PullRefreshableOnGlobalLayoutListener());

		super.setOnItemClickListener(new PullRefreshableOnItemClickListener());
	}

	private void setHeaderPadding(int padding) {
		headerPadding = padding;

		MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) header
				.getLayoutParams();
		mlp.setMargins(0, padding, 0, 0);
		header.setLayoutParams(mlp);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (animating) return true;
		
		if (lockScrollWhileRefreshing && state == RefreshState.REFRESHING) {
			return true;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (getFirstVisiblePosition() == 0)
				previousY = event.getY();
			else
				previousY = -1;
			break;

		case MotionEvent.ACTION_UP:
			if (previousY != -1
					&& (state == RefreshState.RELEASE_TO_REFRESH || getFirstVisiblePosition() == 0)) {
				switch (state) {
				case RELEASE_TO_REFRESH:
					setState(RefreshState.REFRESHING);
					bounceBackHeader();
					break;
				case PULL_TO_REFRESH:
					resetHeader();
					break;
				case REFRESHING:
					break;
				}
			}
			break;

		case MotionEvent.ACTION_MOVE:
			if (previousY != -1) {
				float y = event.getY();
				float diff = y - previousY;
				if (diff > 0)
					diff /= PULL_WEIGHT;
				previousY = y;

				int newHeaderPadding = Math.max(
						headerPadding + Math.round(diff), -header.getHeight());
				if (!lockScrollWhileRefreshing && state == RefreshState.REFRESHING
						&& newHeaderPadding > 0) {
					newHeaderPadding = 0;
				}

				setHeaderPadding(newHeaderPadding);

				if (state == RefreshState.PULL_TO_REFRESH && headerPadding > 0) {
					setState(RefreshState.RELEASE_TO_REFRESH);

					image.clearAnimation();
					image.startAnimation(flipAnimation);
				} else if (state == RefreshState.RELEASE_TO_REFRESH
						&& headerPadding < 0) {
					setState(RefreshState.PULL_TO_REFRESH);

					image.clearAnimation();
					image.startAnimation(reverseFlipAnimation);
				}
			}

			break;
		}

		return super.onTouchEvent(event);
	}

	private void bounceBackHeader() {
		int yTranslate = state == RefreshState.REFRESHING ? -(headerContainer
				.getHeight() - header.getHeight()) : -headerContainer
				.getHeight();

		bounceAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE,
				0, TranslateAnimation.ABSOLUTE, 0, TranslateAnimation.ABSOLUTE,
				0, TranslateAnimation.ABSOLUTE, yTranslate);

		bounceAnimation.setDuration(BOUNCE_ANIM_DURATION);
		bounceAnimation.setFillEnabled(true);
		bounceAnimation.setFillAfter(false);
		bounceAnimation.setFillBefore(true);
		bounceAnimation.setInterpolator(new DecelerateInterpolator());
		bounceAnimation.setAnimationListener(new HeaderAnimationListener());

		startAnimation(bounceAnimation);
	}

	private void resetHeader() {
		if (headerPadding == -header.getHeight()
				|| getFirstVisiblePosition() > 0) {
			setState(RefreshState.PULL_TO_REFRESH);
			return;
		}

		bounceBackHeader();
	}

	private void setUiRefreshing() {
		spinner.setVisibility(View.VISIBLE);
		image.clearAnimation();
		image.setVisibility(View.INVISIBLE);
		text.setText(R.string.refreshing);
	}

	private void setState(RefreshState state) {
		this.state = state;
		switch (state) {
		case PULL_TO_REFRESH:
			spinner.setVisibility(View.INVISIBLE);
			image.setVisibility(View.VISIBLE);
			text.setText(R.string.pull_to_refresh);
			break;

		case RELEASE_TO_REFRESH:
			spinner.setVisibility(View.INVISIBLE);
			image.setVisibility(View.VISIBLE);
			text.setText(R.string.release_to_refresh);
			break;

		case REFRESHING:
			setUiRefreshing();
			break;
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		if (!hasResetHeader) {
			if (measuredHeaderHeight > 0 && state != RefreshState.REFRESHING) {
				setHeaderPadding(-measuredHeaderHeight);
			}

			hasResetHeader = true;
		}
	}

	private class HeaderAnimationListener implements AnimationListener {

		private int height;
		private RefreshState stateAtAnimationStart;

		public void onAnimationStart(Animation animation) {
			animating = true;
			stateAtAnimationStart = state;

			android.view.ViewGroup.LayoutParams lp = getLayoutParams();
			height = lp.height;
			lp.height = getHeight() + headerContainer.getHeight();
			setLayoutParams(lp);

			if (scrollbarEnabled) {
				setVerticalScrollBarEnabled(false);
			}
		}

		public void onAnimationEnd(Animation animation) {
			animating = false;
			setHeaderPadding(stateAtAnimationStart == RefreshState.REFRESHING ? 0
					: -header.getHeight());

			android.view.ViewGroup.LayoutParams lp = getLayoutParams();
			lp.height = height;
			setLayoutParams(lp);

			if (scrollbarEnabled) {
				setVerticalScrollBarEnabled(true);
			}

			if (stateAtAnimationStart != RefreshState.REFRESHING) {
				setState(RefreshState.PULL_TO_REFRESH);
			} else {
				if (onRefreshListener == null) {
					setState(RefreshState.PULL_TO_REFRESH);
				} else {
					onRefreshListener.onRefresh();
				}
			}
		}

		public void onAnimationRepeat(Animation animation) {
		}
	}

	private class PullRefreshableOnGlobalLayoutListener implements OnGlobalLayoutListener {

		public void onGlobalLayout() {
			int initialHeaderHeight = header.getHeight();

			if (initialHeaderHeight > 0) {
				measuredHeaderHeight = initialHeaderHeight;

				if (measuredHeaderHeight > 0 && state != RefreshState.REFRESHING) {
					setHeaderPadding(-measuredHeaderHeight);
				}
			}

			getViewTreeObserver().removeGlobalOnLayoutListener(this);
		}
	}

	private class PullRefreshableOnItemClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> adapterView, View view,
				int position, long id) {
			hasResetHeader = false;

			if (onItemClickListener != null) {
				onItemClickListener
						.onItemClick(adapterView, view, position, id);
			}
		}
	}
}