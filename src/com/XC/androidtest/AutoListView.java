package com.XC.androidtest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author XC 自定义Listview　下拉刷新,上拉加载更多
 */

public class AutoListView extends ListView {

	private View footerView;
	private int height_footerView;

	public AutoListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public AutoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public AutoListView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		context = getContext();
		inflater = LayoutInflater.from(context);
		headerView = inflater.inflate(R.layout.headerview, null);
		arrow = (ImageView) headerView.findViewById(R.id.arrow);
		this.addHeaderView(headerView);
		paddingTop_headerView = headerView.getPaddingTop();
		measureView(headerView);
		height_headerView = headerView.getMeasuredHeight();
		topPadding(-height_headerView);

		/**************/
		footerView = inflater.inflate(R.layout.footerview, null);
		textView = (TextView) footerView.findViewById(R.id.tv_footertext);
		this.addFooterView(footerView);
		measureView(footerView);

		// paddingBottom_FooterView = footerView.getPaddingBottom();
		// measureView(footerView);
		// height_footerView = footerView.getMeasuredHeight();
		// topPadding(-height_footerView);
		/*****************/
		setOnScrollListener(onScrollListener);
		rotateAnim = new RotateAnimation(0f, 180f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotateAnim.setFillAfter(true);
		rotateAnim.setDuration(500);
		rotateAnim2 = new RotateAnimation(180f, 0f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotateAnim2.setFillAfter(true);
		rotateAnim2.setDuration(500);

	}

	private boolean isTop;
	OnScrollListener onScrollListener = new OnScrollListener() {

		boolean isLastRow = false;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (firstVisibleItem == 0) {
				isTop = true;
			} else {
				isTop = false;
			}
			// 滚动时一直回调，直到停止滚动时才停止回调。单击时回调一次。
			// firstVisibleItem：当前能看见的第一个列表项ID（从0开始）
			// visibleItemCount：当前能看见的列表项个数（小半个也算）
			// totalItemCount：列表项共数

			// 判断是否滚到最后一行
			if (firstVisibleItem + visibleItemCount == totalItemCount
					&& totalItemCount > 0) {
				isLastRow = true;
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// 正在滚动时回调，回调2-3次，手指没抛则回调2次。scrollState = 2的这次不回调
			// 回调顺序如下
			// 第1次：scrollState = SCROLL_STATE_TOUCH_SCROLL(1) 正在滚动
			// 第2次：scrollState = SCROLL_STATE_FLING(2) 手指做了抛的动作（手指离开屏幕前，用力滑了一下）
			// 第3次：scrollState = SCROLL_STATE_IDLE(0) 停止滚动
			// 当屏幕停止滚动时为0；当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1；
			// 由于用户的操作，屏幕产生惯性滑动时为2

			// 当滚到最后一行且停止滚动时，执行加载
			if (isLastRow
					&& scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
				// 加载元素
				isLastRow = false;
				// footerView.setVisibility(View.VISIBLE);
				loadmore.onLoadmore();

			}
		}
	};
	private int startY;
	private View headerView;
	private int paddingTop_headerView;
	private LayoutInflater inflater;
	private Context context;
	private int height_headerView;
	Refresh refresh;
	Loadmore loadmore;

	public Loadmore getLoadmore() {
		return loadmore;
	}

	public void setLoadmore(Loadmore loadmore) {
		this.loadmore = loadmore;
	}

	private boolean enough = false, enoughflag = false;
	private int paddingBottom_FooterView;
	private TextView textView;
	private ImageView arrow;
	private Animation rotateAnim;
	private Animation rotateAnim2;

	public Refresh getRefresh() {
		return refresh;
	}

	public void setRefresh(Refresh refresh) {
		this.refresh = refresh;
	}

	@Override
	public boolean onTouchEvent(android.view.MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (isTop && enough) {
				topPadding(paddingTop_headerView);
				refresh.onRefresh();
			} else {
				topPadding(-height_headerView);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int moveY = (int) ev.getY() - startY;
			if (isTop && moveY > 0) {
				int topPadding = moveY - height_headerView;
				if (topPadding <= 0) {
					if (enough) {
						arrow.clearAnimation();
						arrow.startAnimation(rotateAnim2);
					}
					enough = false;
					topPadding(topPadding);
				} else {
					if (!enough) {
						arrow.clearAnimation();
						arrow.startAnimation(rotateAnim);
					}
					enough = true;
					topPadding(topPadding / 3);
				}
			}

			if (moveY < 0) {
				textView.setVisibility(View.VISIBLE);
			}
			Log.i("tttt", "footerView"+footerView.getVisibility() + "");

			break;
		}
		return super.onTouchEvent(ev);
	}

	// 调整header的大小。其实调整的只是距离顶部的高度。
	private void topPadding(int topPadding) {
		headerView.setPadding(headerView.getPaddingLeft(), topPadding,
				headerView.getPaddingRight(), headerView.getPaddingBottom());
		headerView.invalidate();
	}

	/**
	 * 通知父布局，占用的宽，高；
	 * 
	 * @param view
	 */
	private void measureView(View view) {
		ViewGroup.LayoutParams p = view.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int height;
		int tempHeight = p.height;
		if (tempHeight > 0) {
			height = MeasureSpec.makeMeasureSpec(tempHeight,
					MeasureSpec.EXACTLY);
		} else {
			height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		view.measure(width, height);
	}

	public void onRefreshComplete() {
		topPadding(-height_headerView);
	}

	public void onLoadmoreCompelte() {
		textView.setVisibility(View.GONE);
	}

	interface Refresh {
		void onRefresh();
	}

	interface Loadmore {
		void onLoadmore();
	}
}
