package org.doff.meizubbs.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ForumHttp extends BaseHttp {

	Document doc;
	String html;

	public void setHtml(String html) {
		this.html = html;
		this.doc = Jsoup.parse(html);

	}

	public String getHtml() {
		if (doc == null) {
			return "";
		}
		if (html == null || html.equals("")) {
			html = this.doc.html();
		}
		return this.doc.html();
	}

	public Document getDoc() {
		return doc;
	}

	public ForumHttp() {
	}

	public ForumHttp(String url) {
		System.out.println("requset:" + url);
		try {
			doc = Jsoup.connect(url).header("Accept-Encoding", "gzip, deflate")
					.timeout(getTimeout()).get();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<HashMap<String, String>> getTitlesHashMap(String id,
			ArrayList<HashMap<String, String>> data) {
		if (doc == null) {
			return null;
		}
		ArrayList<HashMap<String, String>> arr = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> hash;
		Elements eles = doc.select("#forum_" + id + " tbody");
		boolean remove = false;
		for (int i = 0; i < eles.size(); i++) {
			String topicID = eles.get(i).select(".thread_font").parents()
					.first().attr("id").replace("thread_", "");
			remove = false;
			if (data != null) {
				for (int j = 0; j < data.size(); j++) {
					if (data.get(j).get("topicid").equals(topicID)) {
						remove = true;
						// System.out.println("remove");
						break;
					}
				}
			}
			if (!remove) {
				hash = new HashMap<String, String>();
				// 1304289&
				hash.put("title",
						eles.get(i).select(".thread_font").attr("title"));
				hash.put("date", eles.get(i).select(".author span").html());
				hash.put("nums", eles.get(i).select(".nums .replies").html()
						+ "/" + eles.get(i).select(".nums .views").html());
				hash.put("author", eles.get(i).select(".author div a").html());
				hash.put("topicid", topicID);

				hash.put(
						"avatar",
						"http://user.meizu.com/avatar.php?size=avatar_middle&uid="
								+ eles.get(i).select(".author div a")
										.attr("href").replace(".html", "")
										.replace("space-uid-", ""));
				hash.put(
						"folder",
						eles.get(i).select(".folder img").attr("src")
								.replace("images/meizu/", "")
								.replace(".gif", "").replace("_1.gif", "")
								.replace("_2.gif", "").replace("_3.gif", ""));
				arr.add(hash);
			}
		}
		return arr;
	}
}
