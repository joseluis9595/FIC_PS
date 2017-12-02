package es.udc.psi1718.project.view.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.udc.psi1718.project.R;
import es.udc.psi1718.project.view.customviews.viewpager.ViewPagerCustomDuration;

public class TutorialActivity extends AppCompatActivity {
	private static String TAG = "TutorialActivity";

	// Layout variables
	private ViewPagerCustomDuration mViewPager;
	private int[] screens;
	private Button btnSkip, btnNext;
	private LinearLayout dotsContainer;

	// Animations
	// Animation fadeOutAnim;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);

		// Change screen to fullScreen
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

		// Initialize the screens of the tutorial
		screens = new int[]{
				R.layout.tutorial_screen1_layout,
				R.layout.tutorial_screen2_layout,
				R.layout.tutorial_screen3_layout,
				R.layout.tutorial_screen4_layout,
				R.layout.tutorial_screen5_layout
		};

		// Load animation
		// fadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.anim_fade_out_08);

		// Initialize variables
		mViewPager = (ViewPagerCustomDuration) findViewById(R.id.view_pager);
		dotsContainer = (LinearLayout) findViewById(R.id.layout_activtutorial_dotscontainer);
		btnSkip = (Button) findViewById(R.id.btn_tutorialctiv_skip);
		btnNext = (Button) findViewById(R.id.btn_tutorialctiv_next);

		// Modify layout
		setNavigationDot(0);
		changeStatusBarColor();

		// Add viewPager Adapter
		MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
		mViewPager.setAdapter(myViewPagerAdapter);
		mViewPager.addOnPageChangeListener(mOnPageChangeListener);
		mViewPager.setPageTransformer(true, myPageTransformer);

		// Add listeners
		btnSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent mainActivIntent = new Intent(TutorialActivity.this, MainActivity.class);
				startActivity(mainActivIntent);
				finish();
			}
		});

		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int newPosition = mViewPager.getCurrentItem() + 1;
				if (newPosition < screens.length) {
					mViewPager.setCurrentItem(newPosition, true);
				} else {
					Intent mainActivIntent = new Intent(TutorialActivity.this, MainActivity.class);
					startActivity(mainActivIntent);
					finish();
				}
			}
		});
	}

	/**
	 * Make the status bar transparent
	 */
	private void changeStatusBarColor() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
		}
	}


	/**
	 * Adds navigation dots to the bottom of the viewPager
	 *
	 * @param position which position is selected
	 */
	private void setNavigationDot(int position) {
		TextView[] dots = new TextView[screens.length];
		dotsContainer.removeAllViews();
		for (int i = 0; i < dots.length; i++) {
			dots[i] = new TextView(this);
			dots[i].setText(Html.fromHtml("&#8226;"));
			dots[i].setTextSize(35);
			dots[i].setAlpha(.2f);
			dots[i].setTextColor(getResources().getColor(R.color.colorDarkGrey));
			dotsContainer.addView(dots[i]);
		}
		dots[position].setAlpha(1f);
		dots[position].setTextColor(getResources().getColor(R.color.colorLightBlue));
		Log.e(TAG, "Executed setNavigationDots");
	}

	/**
	 * PageChange listener for the viewPager
	 */
	ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			setNavigationDot(position);
			if (position == screens.length - 1) {
				btnNext.setText(R.string.tutorial_btnnext_finish);
			} else {
				btnNext.setText(R.string.tutorial_btnnext_normal);
			}
			// Hide or show 'skip' button
			if (position == 0) {
				btnSkip.setVisibility(View.VISIBLE);
				AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 0.8f);
				fadeIn.setDuration(200);
				btnSkip.startAnimation(fadeIn);
			} else {
				btnSkip.setVisibility(View.GONE);
				AlphaAnimation fadeOut = new AlphaAnimation(0.8f, 0.0f);
				fadeOut.setDuration(200);
				btnSkip.startAnimation(fadeOut);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	};

	/**
	 * PageTransformer to add animation to the viewPager
	 */
	ViewPager.PageTransformer myPageTransformer = new ViewPager.PageTransformer() {
		@Override
		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);

			} else if (position <= 1) { // [-1,1]

				// Change animation of the whole view
				view.setAlpha(1 - Math.abs(position));
				view.setTranslationX(position * (pageWidth / 3));

				// Change animation of the title
				TextView tvTitle = (TextView) view.findViewById(R.id.tv_tutorial_title);
				// tvTitle.setAlpha(1 - Math.abs(position));
				tvTitle.setTranslationX(position * (pageWidth / 2));

				// Change animation of the imageView
				ImageView img = (ImageView) view.findViewById(R.id.img_tutorial);
				img.setTranslationX(position * (pageWidth / 6));

			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	};


	/**
	 * Adapter for the ViewPager
	 */
	public class MyViewPagerAdapter extends PagerAdapter {
		private LayoutInflater inflater;

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View inflatedView = inflater.inflate(screens[position], container, false);
			container.addView(inflatedView);
			return inflatedView;
		}

		@Override
		public int getCount() {
			return screens.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;
			container.removeView(view);
		}
	}
}
