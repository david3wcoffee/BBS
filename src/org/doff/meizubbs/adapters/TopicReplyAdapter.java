package org.doff.meizubbs.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import org.doff.meizubbs.R;
import org.doff.meizubbs.http.BitmapManager;
import org.doff.meizubbs.views.RichTextView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class TopicReplyAdapter extends BaseAdapter {
	Context context;
	int layoutID;

	ArrayList<HashMap<String, String>> data;

	public TopicReplyAdapter(Context context,
			ArrayList<HashMap<String, String>> data, int layoutID) {
		this.context = context;
		this.layoutID = layoutID;
		this.data = data;
	}

	/**
	 * 
	 * @param pageIndex
	 *            要清除的页
	 */
	public void clearLastPage() {
		while ((this.data.size() + 1) % 20 > 0) {// 加上主题，取余页20条
			this.data.remove(this.data.size() - 1);
		}

	}

	public void addData(ArrayList<HashMap<String, String>> data) {

		this.data.addAll(data);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		return data==null?0:data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	BitmapManager bm = new BitmapManager();

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(layoutID, null);
		// 动态数组与ListItem对应的子项

		TextView v1 = (TextView) convertView.findViewById(R.id.tv_sendtime);
		if (v1 != null) {
			v1.setText(data.get(position).get("date"));
		}
		RichTextView v2 = (RichTextView) convertView
				.findViewById(R.id.tv_content);

		if (v2 != null) {
			v2.setText(data.get(position).get("content"), BufferType.SPANNABLE);
		}
		TextView v3 = (TextView) convertView.findViewById(R.id.tv_floornumber);
		if (v3 != null) {
			v3.setText(data.get(position).get("author"));
		}
		
	 
		
		
		
		ImageView iv_reply_item_headimg = (ImageView) convertView
				.findViewById(R.id.iv_reply_item_headimg);

		Bitmap bmp = BitmapFactory.decodeResource(convertView.getContext()
				.getResources(), R.drawable.ic_attachment_contact_vcf_picture);

		bm.loadBitmap(data.get(position).get("avatar"), iv_reply_item_headimg,
				bmp, 86, 86);
		return convertView;
	}

}
