package org.doff.meizubbs.http;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * 异步线程加载图片工具类
 * 
 * @author liux
 */
@SuppressLint("HandlerLeak")
public class BitmapManager {

	private static HashMap<String, SoftReference<Bitmap>> cache;
	private static ExecutorService pool;
	private static Map<ImageView, String> imageViews;
	private Bitmap defaultBmp;

	static {
		cache = new HashMap<String, SoftReference<Bitmap>>();
		pool = Executors.newFixedThreadPool(5); // 固定线程池
		imageViews = Collections
				.synchronizedMap(new WeakHashMap<ImageView, String>());
	}

	public BitmapManager() {
	}

	public BitmapManager(Bitmap def) {
		this.defaultBmp = def;
	}

	/**
	 * 设置默认图片
	 * 
	 * @param bmp
	 */
	public void setDefaultBmp(Bitmap bmp) {
		defaultBmp = bmp;
	}

	/**
	 * 加载图片
	 * 
	 * @param url
	 * @param imageView
	 */
	public void loadBitmap(String url, ImageView imageView) {
		loadBitmap(url, imageView, this.defaultBmp, 0, 0);
	}

	/**
	 * 加载图片-可设置加载失败后显示的默认图片
	 * 
	 * @param url
	 * @param imageView
	 * @param defaultBmp
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp) {
		loadBitmap(url, imageView, defaultBmp, 0, 0);
	}

	/**
	 * 加载图片-可指定显示图片的高宽
	 * 
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp,
			int width, int height) {
		imageViews.put(imageView, url);
		imageView.setContentDescription(url);
		Bitmap bitmap = getBitmapFromCache(url);

		if (bitmap != null) {
			// 显示缓存图片
			// System.out.println("Bitmap cache");
			imageView.setImageBitmap(bitmap);
		} else {
			// 加载SD卡中的图片缓存
			String filename = getFileName(url);
			String filepath = getHeadImgPath(imageView.getContext()) + filename;
			File file = new File(filepath);
			if (file.exists()) {
				// 显示SD卡中的图片缓存
				Bitmap bmp = getBitmap(imageView.getContext(), filename);

				imageView.setImageBitmap(bmp);
			} else {
				// 线程加载网络图片

				imageView.setImageBitmap(defaultBmp);
				queueJob(url, imageView, width, height);
			}
		}
	}

	private Bitmap getBitmap(Context context, String filename) {
		// TODO Auto-generated method stub

		String myJpgPath = getHeadImgPath(context) + filename;
		Bitmap bm = BitmapFactory.decodeFile(myJpgPath);

		return bm;
	}

	private String getFileName(String url) {
		// TODO Auto-generated method stub
		return url.replace("http://", "").replace('/', '_');
	}

	private String getHeadImgPath(Context context) {

		String path = context.getFilesDir() + File.separator + "head"
				+ File.separator;
		File destDir = new File(path);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		return path;

	}

	private void saveImage(Context context, String fileName, Bitmap bmp) {
		// TODO Auto-generated method stub
		String filePath = getHeadImgPath(context) + fileName;
		BufferedOutputStream bos = null;
		try {
			FileOutputStream out = new FileOutputStream(filePath);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (bos != null) {
					bos.flush();
					bos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private Bitmap getNetBitmap(String url) {
		// TODO Auto-generated method stub
		Bitmap bitmap = null;
		HttpGet httpRequest = new HttpGet(url);
		// 取得HttpClient 对象
		HttpClient httpclient = new DefaultHttpClient();
		try {
			// 请求httpClient ，取得HttpRestponse
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 取得相关信息 取得HttpEntiy
				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream is = httpEntity.getContent();
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 从缓存中获取图片
	 * 
	 * @param url
	 */
	public Bitmap getBitmapFromCache(String url) {
		Bitmap bitmap = null;
		if (cache.containsKey(url)) {
			bitmap = cache.get(url).get();
		}
		return bitmap;
	}

	/**
	 * 从网络中加载图片
	 * 
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public void queueJob(final String url, final ImageView imageView,
			final int width, final int height) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				String tag = imageViews.get(imageView);
				if (tag != null && tag.equals(url)) {
					if (msg.obj != null) {
						imageView.setImageBitmap((Bitmap) msg.obj);
						// 向SD卡中写入图片缓存
						saveImage(imageView.getContext(), getFileName(url),
								(Bitmap) msg.obj);
					}
				}
			}

		};

		pool.execute(new Runnable() {
			public void run() {
				Message message = Message.obtain();
				message.obj = downloadBitmap(url, width, height);
				handler.sendMessage(message);
			}
		});
	}

	/**
	 * 下载图片-可指定显示图片的高宽
	 * 
	 * @param url
	 * @param width
	 * @param height
	 */
	private Bitmap downloadBitmap(String url, int width, int height) {
		Bitmap bitmap = null;
		try {
			// http加载图片
			bitmap = getNetBitmap(url);
			if (width > 0 && height > 0 && bitmap != null) {
				// 指定显示图片的高宽
				bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
			}
			// 放入缓存
			cache.put(url, new SoftReference<Bitmap>(bitmap));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

}