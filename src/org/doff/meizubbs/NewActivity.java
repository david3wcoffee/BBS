package org.doff.meizubbs;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.Window;
import android.widget.TextView;

public class NewActivity extends Activity {
	String[] forumsName, forumsUrls;
	int currentIndex;
	TextView tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.topic_new);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.topic_new_title); // titlebar为自己标题栏的布局

		currentIndex = getIntent().getIntExtra("forumIndex", 0);
		System.out.println(currentIndex);

		forumsName = this.getResources().getStringArray(R.array.forumsName);
		forumsUrls = this.getResources().getStringArray(R.array.forumsURL);
		setTitle(forumsName[currentIndex]);
		tv_title = (TextView) findViewById(R.id.tv_topic_new_title);
		tv_title.setText(forumsName[currentIndex]+" - 新帖子");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actions_topic_new, menu);

		return true;
	}
}
