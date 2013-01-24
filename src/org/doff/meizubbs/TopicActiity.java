package org.doff.meizubbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.doff.meizubbs.adapters.PagerAdapter;
import org.doff.meizubbs.adapters.TopicReplyAdapter;
import org.doff.meizubbs.http.BitmapManager;
import org.doff.meizubbs.http.TopicHttp;
import org.doff.meizubbs.views.RichTextView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView.BufferType;

public class TopicActiity extends FragmentActivity implements
		OnPageChangeListener, OnScrollListener, View.OnClickListener {
	PagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	ImageView cursor, img_topic_authorhead;
	TextView tv1, tv2, tv_topic_author, tv_topic_date, tv_topic_title_title,
			tv_topic_title;

	RichTextView tv_topic_content;
	ListView listView;
	List<View> mListViews;
	LayoutInflater mInflater;
	View layout1 = null, layout2 = null;
	int scrolledX = 0, scrolledY = 0;
	int pageIndex = 1;
	String url;
	ArrayList<HashMap<String, String>> arr;
	boolean downloading = false;
	String topicID;
	TopicReplyAdapter replayAdapter;

	Button loadButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.topic_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.topic_title); // titlebar为自己标题栏的布局

		tv_topic_title_title = (TextView) findViewById(R.id.tv_topic_title_title);
		img_topic_authorhead = (ImageView) findViewById(R.id.img_topic_authorhead);
		// topicID = "2839280";
		topicID = getIntent().getStringExtra("topicID");// ;
		String title = getIntent().getStringExtra("title");
		tv_topic_title_title.setText(title);

		Bitmap bmp = BitmapFactory.decodeResource(getApplicationContext()
				.getResources(), R.drawable.ic_attachment_contact_vcf_picture);
		System.out.println(getIntent().getStringExtra("avatar"));
		new BitmapManager().loadBitmap(getIntent().getStringExtra("avatar"),
				img_topic_authorhead, bmp, 86, 86);

		mInflater = getLayoutInflater();
		mListViews = new ArrayList<View>();
		layout1 = mInflater.inflate(R.layout.topic_content, null);
		layout2 = mInflater.inflate(R.layout.topic_reply, null);
		listView = (ListView) layout2.findViewById(R.id.lv_Reply);

		loadButton = new Button(this);
		loadButton.setText("点击加载");
		loadButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadButton.setText("正在加载……");
				downloadData(pageIndex);

			}
		});

		listView.addFooterView(loadButton);
		listView.setOnScrollListener(this);

		mListViews.add(layout1);
		mListViews.add(layout2);

		mSectionsPagerAdapter = new PagerAdapter(getSupportFragmentManager(),
				mListViews);

		cursor = (ImageView) findViewById(R.id.imageView1);
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager2);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setOnPageChangeListener(this);
		mViewPager.setOffscreenPageLimit(mListViews.size());

		tv1 = (TextView) findViewById(R.id.textView1);

		tv2 = (TextView) findViewById(R.id.textView2);
		tv1.setOnClickListener(this);
		tv2.setOnClickListener(this);

		downloadData(1);

	}

	@Override
	public void onClick(View v) {
		// TODO 标题栏页面切换
		switch (v.getId()) {
		case R.id.textView1:
			mViewPager.setCurrentItem(0);
			break;
		case R.id.textView2:
			mViewPager.setCurrentItem(1);

			break;
		default:
			break;
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
		// TODO 帖子和回复内容标签动画
		Animation animation = null;
		switch (arg0) {
		case 0:
			animation = new TranslateAnimation(80, 0, 0, 0);

			break;
		case 1:
			animation = new TranslateAnimation(0, 80, 0, 0);
			break;

		default:
			break;
		}
		animation.setFillAfter(true);// True:图片停在动画结束位置
		animation.setDuration(100);
		cursor.startAnimation(animation);
	}

	int firstVisibleItem = 0;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		this.firstVisibleItem = firstVisibleItem;
		if (firstVisibleItem + visibleItemCount == totalItemCount
				&& !downloading) {
			// downloadData(pageIndex);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	private Thread downloadThread;

	private void downloadData(int index) {
		url = "http://bbs.meizu.cn/thread-" + topicID + "-" + index + "-1.html";

		if (downloadThread == null || !downloadThread.isAlive()) {
			downloadThread = new Thread() {
				@Override
				public void run() {

					TopicHttp http = new TopicHttp(url);
					Message msg = new Message();
					msg.what = 2;
					// Bundle b = new Bundle();
					// b.putString("html", .getHtml());
					msg.obj = http;

					handler.sendMessage(msg);

				}
			};
			if (!downloading) {
				downloadThread.start();
				downloading = true;
			}
		}
	}

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {

			case 2:
				// 网络数据加载
				if (msg.obj == null) {
					return;

				}
				TopicHttp htt = (TopicHttp) msg.obj;

				ArrayList<HashMap<String, String>> arr = htt.getTitlesHashMap();

				if (arr == null || arr.size() == 0) {
					return;
				}

				HashMap<String, String> topic = arr.get(0);

				if (tv_topic_author == null) {
					tv_topic_author = (TextView) findViewById(R.id.tv_topic_author);
					tv_topic_content = (RichTextView) findViewById(R.id.tv_topic_content);
					tv_topic_date = (TextView) findViewById(R.id.tv_topic_date);

					tv_topic_title = (TextView) findViewById(R.id.tv_topic_title);

					if (tv_topic_author == null) {
						return;

					}

					tv_topic_title.setText(topic.get("title"));

					tv_topic_author.setText(topic.get("author"));
					tv_topic_content.setText(topic.get("content"),
							BufferType.SPANNABLE);
					tv_topic_date.setText(topic.get("date"));
					setTitle(topic.get("title"));
				}

				if (arr.size() == 1 && pageIndex == 1) {// 第一页没有回复

				} else if (arr.size() < 20 && pageIndex == 1) {// 第一页记录不到20条
					arr.remove(0);
					replayAdapter = new TopicReplyAdapter(
							getApplicationContext(), arr,
							R.layout.topic_reply_item);
					listView.setAdapter(replayAdapter);
					listView.setSelection(firstVisibleItem + 1);

				} else if (arr.size() == 20 && pageIndex == 1) {// 第一页回复记录20条

					if (replayAdapter == null) {
						arr.remove(0);
						replayAdapter = new TopicReplyAdapter(
								getApplicationContext(), arr,
								R.layout.topic_reply_item);
						listView.setAdapter(replayAdapter);
					} else {
						replayAdapter.addData(arr);
					}
					pageIndex++;

				} else if (arr.size() == 20 && pageIndex > 1) {// 第N页，并有20条回复记录

					replayAdapter.addData(arr);
					pageIndex++;

				} else if (arr.size() < 20 && pageIndex > 1) {// 第N页，不到20条回复记录

					replayAdapter.clearLastPage();
					replayAdapter.addData(arr);
				}
				if (mListViews == null) {
					return;
				}
				mListViews.get(0).findViewById(R.id.sv_topic_content)
						.setVisibility(View.VISIBLE);
				mListViews.get(0).findViewById(R.id.ll_topic_loading)
						.setVisibility(View.GONE);
				mListViews.get(1).findViewById(R.id.ic_topic_reply_loading)
						.setVisibility(View.GONE);
				mListViews.get(1).findViewById(R.id.lv_Reply)
						.setVisibility(View.VISIBLE);

				loadButton.setText("点击加载");

				downloading = false;
				break;
			default:
				break;
			}
		}
	};

}
