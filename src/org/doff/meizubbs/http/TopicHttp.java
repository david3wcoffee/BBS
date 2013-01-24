package org.doff.meizubbs.http;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TopicHttp  extends BaseHttp{
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

	public TopicHttp() {
	}

	public TopicHttp(String url) {
		System.out.println("requset:" + url);
		try {
			doc = Jsoup.connect(url).header("Accept-Encoding", "gzip, deflate")
					.timeout(getTimeout()).get();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<HashMap<String, String>> getTitlesHashMap() {
		if (doc == null) {
			return null;
		}

		ArrayList<HashMap<String, String>> arr = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> hash;
		Elements eles = doc.select(".mainbox.viewthread");
		// �����һ������
		for (int i = 0; i < eles.size(); i++) {
			Elements content = msgConvert(eles.get(i).select(".t_msgfont"));
			hash = new HashMap<String, String>();
			if (content.size() > 0) {

				hash.put("content", eles.get(i).select(".t_msgfont").html());
				hash.put("title", eles.get(i).select(".postcontent h2").text());
				hash.put("date", getDateString(eles.get(i).select(".info_head")
						.html().replace("������ ", "")));
				hash.put("floor", eles.get(i).select(".postinfo strong a")
						.html());
				hash.put("author", eles.get(i).select(".mzcite a").html());
			
				hash.put(
						"avatar",
						"http://user.meizu.com/avatar.php?size=avatar_middle&uid="
								+ eles.get(i).select(".mzcite a")
										.attr("href").replace(".html", "")
										.replace("space-uid-", ""));
				arr.add(hash);
			}
		}
		return arr;
	}

	@SuppressWarnings("deprecation")
	private String getDateString(String str) {
		if (str.equals("")) {
			return "";
		}
		SimpleDateFormat bartDateFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm");
		String formatString = "";
		// Create a string containing a text date to be parsed.
		Date date = null;
		try {

			date = bartDateFormat.parse(str);
			Date now = new Date();

			if (date.getDate() == now.getDate()) {// ����
				formatString = "HH:mm";
			} else if (date.getYear() == now.getYear()) {// ����
				formatString = "MM/dd HH:mm";
			} else {
				formatString = "yyyy/MM/dd HH:mm";
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		SimpleDateFormat outDateFormat = new SimpleDateFormat(formatString);

		return outDateFormat.format(date);
	}

	private Elements msgConvert(Elements content) {

		if (content.select(".smilies_image").size() > 0) {
			// TODO ���鴦��
			Elements smilies = content.select(".smilies_image");
			for (int j = 0; j < smilies.size(); j++) {
				String src = smilies.get(j).attr("src");// images/smilies/mx/07.gif
				src = src.replace("m8", "mx")

				.replace(".gif", "").replace("images/smilies/mx/", "");
				src = "mxem"
						+ (src.length() > 2 ? src.substring(src.length() - 2,
								src.length()) : src);
				smilies.get(j).attr("src", src);
			}
		}

		// TODO ���е�ͼƬ����
		Elements zoom = content.select("a[href=###zoom]");
		for (Element element : zoom) {
			element.attr("href", "");
			element.select("img").attr("ref", element.attr("src"));
			element.select("img").attr("src", "attach_image");// ����ͼƬ�����ĵ˼���ͼƬ
		}

		Elements attach_img = content
				.select("img[onload=attachimg(this, 'load')]");
		for (Element element : attach_img) {
			element.attr("ref", attach_img.attr("src"));
			element.attr("src", "attach_image");// ����ͼƬ�����ĵ˼���ͼƬ
		}

		// ��������
		if (content.select("div.t_attach").size() > 0) {
			Elements attach = content.select("div.t_attach");
			Elements tag_a = attach.select("a[href]");
			for (Element element : tag_a) {
				element.attr("href",
						"http://bbs.meizu.cn/" + element.attr("href"));
			}

			for (int i = 0; i < attach.size(); i++) {
				String aid = attach.get(i).attr("id").replace("_menu", "");// ��ȡ������������ID
				content.select("#" + aid).remove();// �Ƴ�������������
				attach.get(i).before(attach.get(i).html());
			}
			content.select("div.t_smallfont").remove();// �Ƴ����ϴ�����
			content.select(".absmiddle").remove();// �Ƴ����ָ��ȥ��Сͼ��
			attach.remove();
		}
		content.html().replace(
				"[img]http://bbs.meizu.com/images/common/back.gif[/img]", "");
		return content;
	}

	public HashMap<String, String> getTopicContent() {

		HashMap<String, String> hash;
		Elements eles = doc.select(".mainbox.viewthread");

		hash = new HashMap<String, String>();
		Elements content = msgConvert(eles.get(0).select(".t_msgfont"));

		hash.put("content", content.html());
		hash.put("date",
				eles.get(0).select(".info_head").html().replace("������ ", ""));
		hash.put("floor", eles.get(0).select(".postinfo strong a").html());
		hash.put("author", eles.get(0).select(".mzcite a").html());
		hash.put("title", eles.get(0).select(".postcontent h2").html());
		
		
		
		
		return hash;
	}
}
