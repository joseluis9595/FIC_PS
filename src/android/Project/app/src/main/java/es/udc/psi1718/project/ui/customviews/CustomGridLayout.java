package es.udc.psi1718.project.ui.customviews;

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
import android.widget.ScrollView;

import java.util.concurrent.atomic.AtomicBoolean;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.ui.customviews.controllers.ControllerView;


public class CustomGridLayout extends ScrollView {

	private static final String TAG = "CustomView";
	private static final int NBR_ITEMS = 4;
	private GridLayout mGrid;
	private ScrollView mScrollView;
	private ValueAnimator mAnimator;
	private AtomicBoolean mIsScrolling = new AtomicBoolean(false);
	private int index;
	private Context context;

	public CustomGridLayout(Context context) {
		super(context);
		init(context);
	}

	public CustomGridLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CustomGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.customgrid_layout, this);


		mScrollView = (ScrollView) findViewById(R.id.scroll_view);
		mScrollView.setSmoothScrollingEnabled(true);

		mGrid = (GridLayout) findViewById(R.id.grid_layout);
		mGrid.setOnDragListener(new DragListener());

		// final LayoutInflater inflater = LayoutInflater.from(context);
		// for (int i = 0; i < NBR_ITEMS; i++) {
		// 	final View itemView = inflater.inflate(R.layout.grid_item, mGrid, false);
		// 	final TextView text = (TextView) itemView.findViewById(R.id.text);
		// 	text.setText(String.valueOf(i + 1));
		//
		// 	itemView.setOnLongClickListener(new LongPressListener());
		// 	mGrid.addView(itemView);
		// }
	}


	static class LongPressListener implements View.OnLongClickListener {
		@Override
		public boolean onLongClick(View view) {
			final ClipData data = ClipData.newPlainText("", "");
			View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
			view.startDrag(data, shadowBuilder, view, 0);
			view.setVisibility(View.INVISIBLE);
			// Log.e(TAG, "Id es : " + view.getId());
			return true;
		}
	}

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

					Log.e(TAG, "Index : " + index);

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
					saveGridItemsPosition();
					Log.e(TAG, "ACTION_DRAG_ENDED : " + index);
					stopScrolling();
					if (!event.getResult()) {
						view.setVisibility(View.VISIBLE);
					}
					break;
			}
			return true;
		}
	}

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

	private void stopScrolling() {
		if (mAnimator != null) {
			mAnimator.cancel();
		}
	}

	private int calculateNewIndex(float x, float y) {
		// calculate which column to move to
		final float cellWidth = mGrid.getWidth() / mGrid.getColumnCount();
		final int column = (int) (x / cellWidth);

		// calculate which row to move to
		final float cellHeight = mGrid.getHeight() / mGrid.getRowCount();
		final int row = (int) Math.floor(y / cellHeight);

		// the items in the GridLayout is organized as a wrapping list
		// and not as an actual grid, so this is how to get the new index
		int index = row * mGrid.getColumnCount() + column;
		if (index >= mGrid.getChildCount()) {
			index = mGrid.getChildCount() - 1;
		}

		return index;
	}

	private void saveGridItemsPosition() {
		// TODO implementar esta función
		// Recorrer todos los CustomGridItems uno a uno, actualizando su posición con la posición actual
	}

	// public void addCard(View view) {
	// LayoutInflater inflater = LayoutInflater.from(context);
	// final View itemView = inflater.inflate(R.layout.customgrid_items, mGrid, false);
	// LinearLayout containerLayout = (LinearLayout) itemView.findViewById(R.id.customgrid_container_layout);
	// containerLayout.addView(view);
	//
	// // final TextView text = (TextView) itemView.findViewById(R.id.text);
	// // text.setText(String.valueOf(99));
	// //
	// itemView.setOnLongClickListener(new LongPressListener());
	// mGrid.addView(itemView);
	// }

	public void addController(ControllerView controllerView) {
		CustomGridItem item = new CustomGridItem(context, controllerView);
		item.createView(mGrid);
	}


	/* TODO drag and drop
	 * Posibles soluciones al problema del drag and drop:
	 * ... Crear otro layout personalizado, que tenga como campo el controllerView
	 */
}