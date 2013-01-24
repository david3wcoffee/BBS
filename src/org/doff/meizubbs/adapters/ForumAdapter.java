package org.doff.meizubbs.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import org.doff.meizubbs.R;
import org.doff.meizubbs.http.BitmapManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ForumAdapter extends BaseAdapter {
	Context context;
	int layoutID;

	ArrayList<HashMap<String, String>> data;

	public ForumAdapter(Context context,
			ArrayList<HashMap<String, String>> data, int layoutID
	) {
		this.context = context;
		this.layoutID = layoutID;
	
		this.data = data;
	}
	public ArrayList<HashMap<String, String>> getData() {
		// TODO Auto-generated method stub
		return this.data;
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

		TextView v = (TextView) convertView
				.findViewById(R.id.tv_forum_item_title);
		if (v != null) {
			v.setText(data.get(position).get("title"));
		}
		TextView v1 = (TextView) convertView
				.findViewById(R.id.tv_forum_item_date);
		if (v1 != null) {
			v1.setText(data.get(position).get("date"));
		}
		TextView v2 = (TextView) convertView
				.findViewById(R.id.tv_forum_item_click);
		if (v2 != null) {
			v2.setText(data.get(position).get("nums"));
			
			if (data.get(position).get("folder").equals("folder_digest")) {
				v2.setTextColor(convertView.getContext().getResources().getColor( R.color.folder_digest));
			}else if (data.get(position).get("folder").equals("folder_hot")) {
				v2.setTextColor(convertView.getContext().getResources().getColor( R.color.folder_hot));
			}else {
				v2.setTextColor(convertView.getContext().getResources().getColor( R.color.folder_new));
			}
			 
		}
		TextView v3 = (TextView) convertView
				.findViewById(R.id.tv_forum_item_author);
		if (v3 != null) {
			v3.setText(data.get(position).get("author"));
		}
		TextView v4 = (TextView) convertView
				.findViewById(R.id.tv_forum_item_topidID);
		if (v4 != null) {
			v4.setText(data.get(position).get("topicid"));
		}

		ImageView im_avatar=(ImageView) convertView.findViewById(R.id.img_forum_item_head);
		
		Bitmap bmp = BitmapFactory.decodeResource(convertView.getContext()
				.getResources(), R.drawable.ic_attachment_contact_vcf_picture);

		bm.loadBitmap(data.get(position).get("avatar"), im_avatar,
				bmp, 86, 86);
		return convertView;
	}

	

}
