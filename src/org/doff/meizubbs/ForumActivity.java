package org.doff.meizubbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.doff.meizubbs.adapters.ForumAdapter;
import org.doff.meizubbs.adapters.PagerAdapter;
import org.doff.meizubbs.http.ForumHttp;

import android.R.bool;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ForumActivity extends FragmentActivity implements
		OnPageChangeListener, View.OnClickListener, OnItemClickListener,
		ListView.OnScrollListener {
	ImageView img_topic_new, img_forum_left, img_forum_right;
	TextView tv_loginuser_name, tv_forum_name;
	String[] forumsName, forumsUrls;
	String currentName, currentUrl;
	int currentIndex = 0;
	ArrayList<SimpleAdapter> listAdapters;
	List<View> mListViews;
	LayoutInflater mInflater;
	PagerAdapter mSectionsPagerAdapter;
	ViewPager pager_forum;
	int[] pageIndex = { 1, 1, 1 };
	ForumAdapter[] adapters = new ForumAdapter[3];

	View header, footer;
	boolean downloading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.forum_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.forum_title); // titlebar为自己标题栏的布局

		forumsName = this.getResources().getStringArray(R.array.forumsName);
		forumsUrls = this.getResources().getStringArray(R.array.forumsURL);

		img_topic_new = (ImageView) findViewById(R.id.img_forum_newtopic);
		img_forum_left = (ImageView) findViewById(R.id.img_forum_left);
		img_forum_right = (ImageView) findViewById(R.id.img_forum_right);
		tv_loginuser_name = (TextView) findViewById(R.id.tv_loginuser_name);
		tv_forum_name = (TextView) findViewById(R.id.tv_forum_name);

		img_forum_left.setOnClickListener(this);
		img_forum_right.setOnClickListener(this);
		img_topic_new.setOnClickListener(this);

		mListViews = new ArrayList<View>();
		mInflater = getLayoutInflater();
		for (int i = 0; i < forumsUrls.length; i++) {

			View layout = mInflater.inflate(R.layout.forum_content, null);
			ListView lv_forum = (ListView) layout.findViewById(R.id.lv_forum);
			header = mInflater.inflate(R.layout.loading, null);
			footer = mInflater.inflate(R.layout.loading, null);
			lv_forum.addHeaderView(header);
			lv_forum.addFooterView(footer);
			lv_forum.setOnItemClickListener(this);
			lv_forum.setOnScrollListener(this);
			if ((i % 2) - 1 == 0) {
				layout.setBackgroundColor(getResources().getColor(R.color.gray));
			}
			mListViews.add(layout);
		}

		mSectionsPagerAdapter = new PagerAdapter(getSupportFragmentManager(),
				mListViews);
		pager_forum = (ViewPager) findViewById(R.id.pager_forum);
		pager_forum.setAdapter(mSectionsPagerAdapter);
		pager_forum.setOnPageChangeListener(this);
		pager_forum.setOffscreenPageLimit(forumsUrls.length);
		selectPage(0);

	}

	@Override
	public void onClick(View v) {
		// TODO 点击事件
		switch (v.getId()) {
		case R.id.img_forum_left:
			currentIndex--;
			selectPage(currentIndex);
			break;
		case R.id.img_forum_right:
			currentIndex++;
			selectPage(currentIndex);
			break;
		case R.id.img_forum_newtopic:
			Intent intent = new Intent();
			intent.setClass(this, NewActivity.class);
			intent.putExtra("forumIndex", currentIndex);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	private void selectPage(int index) {

		currentIndex = index;

		img_forum_left.setEnabled(true);
		img_forum_right.setEnabled(true);

		currentName = forumsName[index];

		tv_forum_name.setText(currentName);
		if (index == forumsUrls.length - 1) {
			img_forum_right.setEnabled(false);
		} else if (index == 0) {
			img_forum_left.setEnabled(false);
		}
		pager_forum.setCurrentItem(index);
		if (adapters[index] == null) {
			downloadData();
		}

	}

	protected void downloadData() {

		if (downloadThread == null || !downloadThread.isAlive()) {
			downloadThread = new Thread() {
				@Override
				public void run() {
					downloading = true;

					currentUrl = "http://bbs.meizu.cn/forum-"
							+ forumsUrls[currentIndex] + "-"
							+ pageIndex[currentIndex] + ".html";

					ForumHttp http = new ForumHttp(currentUrl);
					Message msg = new Message();
					msg.obj = http;
					msg.what = 2;
					handler.sendMessage(msg);
				}
			};

			downloadThread.start();

		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		System.out.println(arg0 + "" + currentIndex);
		if (arg0 != currentIndex) {
			selectPage(arg0);

		}
	}

	private Thread downloadThread;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {

			case 2:
				// 网络数据加载
				ForumHttp htt = (ForumHttp) msg.obj;
				downloading = false;
				ArrayList<HashMap<String, String>> arr = htt.getTitlesHashMap(
						forumsUrls[currentIndex],
						adapters[currentIndex] == null ? null
								: adapters[currentIndex].getData());

				if (arr == null) {
					return;
				}

				if (adapters[currentIndex] == null) {

					ForumAdapter sa = new ForumAdapter(getApplicationContext(),
							arr, R.layout.forum_item);
					adapters[currentIndex] = sa;

					ListView listView = (ListView) mListViews.get(currentIndex)
							.findViewById(R.id.lv_forum);
					listView.setVisibility(View.VISIBLE);
					listView.setAdapter(sa);
					listView.setSelection(1);
					LinearLayout ll = (LinearLayout) mListViews.get(
							currentIndex).findViewById(R.id.forum_loading);
					ll.setVisibility(View.GONE);
				} else {
					adapters[currentIndex].addData(arr);
				}

				pageIndex[currentIndex]++;

				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		TextView id = (TextView) arg1.findViewById(R.id.tv_forum_item_topidID);
		TextView title = (TextView) arg1.findViewById(R.id.tv_forum_item_title);
		ImageView im = (ImageView) arg1.findViewById(R.id.img_forum_item_head);
		TextView v2 = (TextView) arg1.findViewById(R.id.tv_forum_item_click);
		v2.setTextColor(-4276546);
		Intent i = new Intent();
		i.setClass(this, TopicActiity.class);
		i.putExtra("topicID", id.getText());
		i.putExtra("title", title.getText());
		
		
		System.out.println(im.getContentDescription());
		i.putExtra("avatar", im.getContentDescription());
		startActivity(i);

	}

	boolean needRefresh = false;
	/*
	 * @Override public void onScroll(AbsListView view, int firstVisibleItem,
	 * int visibleItemCount, int totalItemCount) { // TODO Auto-generated method
	 * stub if (firstVisibleItem == 0 && scrollState == 2 && !needRefresh) {
	 * ListView listView = (ListView) mListViews.get(currentIndex)
	 * .findViewById(R.id.lv_forum); listView.setSelection(1); } else if
	 * (firstVisibleItem == 0 && needRefresh) { adapters[currentIndex] = null;
	 * downloadData(); } else if (firstVisibleItem + visibleItemCount ==
	 * totalItemCount && !downloading) { downloadData(); } }
	 * 
	 * int scrollState = -1;
	 * 
	 * 
	 * public void onScrollStateChanged(AbsListView view, int scrollState) { //
	 * TODO Auto-generated method stub // System.out.println(scrollState);
	 * this.scrollState = scrollState; needRefresh = true; switch (scrollState)
	 * { case SCROLL_STATE_FLING: needRefresh = false; break; default: break; }
	 * }
	 */
	boolean clearPage = false, addPage = false;

	@Override
	public void onScroll(AbsListView paramAbsListView, int paramInt1,
			int paramInt2, int paramInt3) {
		this.clearPage = false;
		this.addPage = false;
		if (((paramInt1 == 0) && (paramInt2 == 9)) || (paramInt3 < 9))
			this.clearPage = true;
		if ((paramInt1 + paramInt2 != paramInt3) || (downloading))
			return;
		this.addPage = true;
	}

	@Override
	public void onScrollStateChanged(AbsListView paramAbsListView,
			int scrollState) {
		switch (scrollState) {
		case SCROLL_STATE_FLING:
		default:
		case SCROLL_STATE_IDLE:
			if (clearPage) {
				this.clearPage = false;
				this.pageIndex[this.currentIndex] = 1;
				this.adapters[this.currentIndex] = null;
				downloadData();
			}
			if (addPage) {
				this.addPage = false;
				downloadData();
			}
		case 1:
		}
		this.addPage = false;
		this.clearPage = false;
	}

}
