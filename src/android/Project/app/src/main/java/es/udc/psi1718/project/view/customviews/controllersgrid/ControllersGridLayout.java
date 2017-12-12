package es.udc.psi1718.project.view.customviews.controllersgrid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.concurrent.atomic.AtomicBoolean;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.view.customviews.controllersgrid.controllers.ControllerView;


/**
 * Custom GridLayout with {@link ControllerView} objects in each cell
 * Based on https://github.com/patrick-iv/DragNDropApp
 */
public class ControllersGridLayout extends ScrollView {
	private static final String TAG = "CustomView";
	private Context context;

	// Layout variables
	private GridLayout mGrid;
	private ScrollView mScrollView;
	private ValueAnimator mAnimator;

	// Indexes for the movement
	private int index;
	private int initialIndex = -1;

	// Listener
	private ControllersGridListener controllersGridListener;

	// Using AtomicBoolean for thread-safe operation
	private AtomicBoolean mIsScrolling = new AtomicBoolean(false);


	/* BUILDERS */
	public ControllersGridLayout(Context context) {
		super(context);
		controllersGridListener = (ControllersGridListener) context;
		init(context);
	}

	public ControllersGridLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		controllersGridListener = (ControllersGridListener) context;
		init(context);
	}

	public ControllersGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		controllersGridListener = (ControllersGridListener) context;
		init(context);
	}

	/**
	 * Initializes the layout of our custom view
	 *
	 * @param context context
	 */
	private void init(Context context) {
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.customgrid_layout, this);

		// Initialize scrollView
		mScrollView = (ScrollView) findViewById(R.id.scroll_view);
		mScrollView.setSmoothScrollingEnabled(true);

		// Initialize gridLayout
		mGrid = (GridLayout) findViewById(R.id.grid_layout);
		mGrid.setOnDragListener(new DragListener());
	}


	/**
	 * Long press listener to start the Drag&Drop animation
	 */
	static class LongPressListener implements View.OnLongClickListener {
		@Override
		public boolean onLongClick(View view) {
			final ClipData data = ClipData.newPlainText("", "");
			View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
			view.startDrag(data, shadowBuilder, view, 0);
			view.setVisibility(View.INVISIBLE);
			return true;
		}
	}


	/**
	 * Drag listener to manage the Drag&Drop animation
	 */
	class DragListener implements View.OnDragListener {
		@Override
		public boolean onDrag(View v, DragEvent event) {
			final View view = (View) event.getLocalState();
			switch (event.getAction()) {
				case DragEvent.ACTION_DRAG_LOCATION:
					// do nothing if hovering above own position
					if (view == v) return true;
					// get the new list index
					index = calculateNewIndex(event.getX(), event.getY());
					if (initialIndex < 0) {
						initialIndex = index;
					}

					// Log.e(TAG, "Index : " + index);

					final Rect rect = new Rect();
					mScrollView.getHitRect(rect);
					final int scrollY = mScrollView.getScrollY();

					if (event.getY() - scrollY > mScrollView.getBottom() - 250) {
						startScrolling(scrollY, mGrid.getHeight());
					} else if (event.getY() - scrollY < mScrollView.getTop() + 250) {
						startScrolling(scrollY, 0);
					} else {
						stopScrolling();
					}

					// remove the view from the old position
					mGrid.removeView(view);
					// and push to the new
					mGrid.addView(view, index);
					break;
				case DragEvent.ACTION_DROP:
					view.setVisibility(View.VISIBLE);
					break;
				case DragEvent.ACTION_DRAG_ENDED:
					Log.e(TAG, "ACTION_DRAG_ENDED : " + index);

					if (initialIndex == -1) break;

					// Update index of every modified view
					int firstModifiedIndex = (initialIndex < index) ? initialIndex : index;
					updateIndexes(firstModifiedIndex);

					// Notify listener
					controllersGridListener.controllersPositionChanged(initialIndex, index);

					// Reinitialize variables
					initialIndex = -1;

					// Stop the animation
					stopScrolling();
					if (!event.getResult()) {
						view.setVisibility(View.VISIBLE);
					}
					break;
			}
			return true;
		}
	}

	/**
	 * Start scrolling animation
	 *
	 * @param from from position
	 * @param to   to position
	 */
	private void startScrolling(int from, int to) {
		if (from != to && mAnimator == null) {
			mIsScrolling.set(true);
			mAnimator = new ValueAnimator();
			mAnimator.setInterpolator(new OvershootInterpolator());
			mAnimator.setDuration(Math.abs(to - from));
			mAnimator.setIntValues(from, to);
			mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					mScrollView.smoothScrollTo(0, (int) valueAnimator.getAnimatedValue());
				}
			});
			mAnimator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mIsScrolling.set(false);
					mAnimator = null;
				}
			});
			mAnimator.start();
		}
	}


	/**
	 * Stop scrolling animation
	 */
	private void stopScrolling() {
		if (mAnimator != null) {
			mAnimator.cancel();
		}
	}


	/**
	 * Calculate new index of the element dragged
	 *
	 * @param x x position of the item
	 * @param y y position of the item
	 *
	 * @return int new index inside the gridlayout
	 */
	private int calculateNewIndex(float x, float y) {
		// Calculate which column to move to
		final float cellWidth = mGrid.getWidth() / mGrid.getColumnCount();
		final int column = (int) (x / cellWidth);

		// Calculate which row to move to
		final float cellHeight = mGrid.getHeight() / mGrid.getRowCount();
		final int row = (int) Math.floor(y / cellHeight);

		// the items in the GridLayout are organized as a wrapping list
		// and not as an actual grid, so this is how to get the new index
		int index = row * mGrid.getColumnCount() + column;
		if (index >= mGrid.getChildCount()) {
			index = mGrid.getChildCount() - 1;
		}

		// Return the new index
		return index;
	}


	/**
	 * Update indexes of the views whenever one of them changes position
	 *
	 * @param position first value modified
	 */
	private void updateIndexes(int position) {
		if (position < 0) return;
		Log.d(TAG, "Updating indexes above " + position + "...");
		for (int i = position; i < mGrid.getChildCount(); i++) {
			View view = mGrid.getChildAt(i);
			LinearLayout linearLayout = (LinearLayout) view;
			ControllersGridItem childView = (ControllersGridItem) linearLayout.getChildAt(0);
			// childView.setPosition(i);
		}
	}


	/**
	 * Add a new controller to the layout
	 *
	 * @param controllerView any implementation of the abstract class {@link ControllerView}
	 */
	public void addController(ControllerView controllerView) {
		// We are using a container for every item to be able to inflate the layout with mGrid as parent
		// Doing so, allows us to have the property 'columnWeight' in our xml
		final LayoutInflater inflater = LayoutInflater.from(context);
		final LinearLayout itemView = (LinearLayout) inflater.inflate(R.layout.customgrid_items_container, mGrid, false);

		// Create new ControllerGridItem
		ControllersGridItem item = new ControllersGridItem(context, controllerView);

		// Add ControllerGridItem to our item container
		itemView.addView(item);

		// Set long click listener to be able to drag and drop later
		itemView.setOnLongClickListener(new ControllersGridLayout.LongPressListener());

		// Add our newly create item to the main grid layout
		mGrid.addView(itemView);

		// Finally, update the index of this view
		updateIndexes(mGrid.getChildCount() - 1);
	}


	public void removeController(ControllerView controllerView) {
		// TODO remove item from the layout
	}


	/**
	 * Return number of controllers in the mGrid layout
	 *
	 * @return int, number of controllers
	 */
	public int getControllersCount() {
		return mGrid.getChildCount();
	}


	/**
	 * Reset layout
	 */
	public void reset() {
		mGrid.removeAllViews();
	}
}