package com.fangcheng.plugin.newBusiness.newDianping;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HtmlParser{
	public static String getType(String html) {
		Document doc = Jsoup.parse(html);
		String type = null;
		if (doc.select("div.breadcrumb > a").isEmpty()) {
			type = "pink";
		} else if (doc.select(".bread-name").isEmpty()) {
			HashSet<String> cat_list = new HashSet<String>();
			Elements es1 = doc.select("div.breadcrumb > a");
			Iterator<Element> it1 = es1.iterator();
			while (it1.hasNext()) {
				cat_list.add(it1.next().ownText());
			}
			if (cat_list.contains("综合商场")) {
				type = "mall";
			} else {
				type = "yellow";
			}
		}else {
			return null;
		}
		return type;
	}
	

//	public ParseResult getParse(Content c) {
//		
//		if (type == "yellow") {
//			return parse_yellow(doc, html,c);
//		} else if (type == "mall"){
//			return parse_mall(doc, html,c);
//		}else if (type == "pink") {
//			return parse_pink(doc, html,c);
//		}else{
//			return null;
//		}
//	}
	public static void main(String[] args) throws Exception {
		for (int i = 0; i < args.length; i++) {
			String name = args[i];
			System.out.println(name);
			String url = "file:" + name;
			File file = new File(name);
			byte[] bytes = new byte[(int) file.length()];
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			in.readFully(bytes);
			String html = new String(bytes);
			HtmlParser parser = new HtmlParser();
//			String result = parser.parseYellow(html);
			String result = parser.parseMall(html);
			System.out.println(result);

		}
	}

	public static String parseYellow(String html) {
		String type = "yellow",cat_text = "", closed_text = "", all_comments_text = "-", views_text = "", stars_text = "", shops_inside = "-";
		Document doc = Jsoup.parse(html);
		Elements cats = doc.select("div.breadcrumb > a");
		Iterator<Element> it = cats.iterator();
		while (it.hasNext()) {
			Element next = it.next();
			cat_text += (next.ownText() + ",");
		}

		if (doc.select("p.shop-closed").isEmpty()) {
			closed_text = "FALSE";
		} else {
			closed_text = "TRUE";
		}

		if (doc.select("#comment > h2 > a:nth-child(2) > span").isEmpty()) {
			all_comments_text = "-";
		} else {
			all_comments_text = doc
					.select("#comment > h2 > a:nth-child(2) > span").first()
					.ownText().replaceAll("\\(|\\)", "");
		}

		if (doc.select("#aside > div.mod.aside-mod.view-about > ul > li > a")
				.isEmpty()) {
			views_text = "-";
		} else {
			Iterator<Element> i = doc.select(
					"#aside > div.mod.aside-mod.view-about > ul > li > a")
					.iterator();
			while (i.hasNext()) {
				Element e = i.next();
				views_text += (e.attr("href").replace(
						"http://www.dianping.com/shop/", "").replace("/shop/", "") + ",");
			}
		}

		try {
			Element star_5 = doc.select(
					"#comment > h2 > span > a:nth-child(2) > span").first();
			Element star_4 = doc.select(
					"#comment > h2 > span > a:nth-child(3) > span").first();
			Element star_3 = doc.select(
					"#comment > h2 > span > a:nth-child(4) > span").first();
			Element star_2 = doc.select(
					"#comment > h2 > span > a:nth-child(5) > span").first();
			Element star_1 = doc.select(
					"#comment > h2 > span > a:nth-child(6) > span").first();
			stars_text = star_1.ownText().replaceAll("\\(|\\)", "") + ","
					+ star_2.ownText().replaceAll("\\(|\\)", "") + ","
					+ star_3.ownText().replaceAll("\\(|\\)", "") + ","
					+ star_4.ownText().replaceAll("\\(|\\)", "") + ","
					+ star_5.ownText().replaceAll("\\(|\\)", "");
		} catch (Exception e) {
			stars_text = "-";
		}
		return type + "|" + cat_text + "|" + closed_text
				+ "|" + all_comments_text + "|" + views_text + "|" + stars_text
				+ "|" + shops_inside + "|";

	}
	public static String parsePink(String html){
		String type = "pink",cat_text = "", closed_text = "-", all_comments_text = "-", views_text = "", stars_text = "", shops_inside = "-";
		Document doc = Jsoup.parse(html);
		Elements es = doc.select(".bread-name");
		Iterator<Element> it = es.iterator();
		while (it.hasNext()) {
			Element next = it.next();
			cat_text += (next.ownText() + ",");
		}
		
		 if (html.contains("商户已关闭")) {
		 closed_text = "TRUE";
		 } else {
		 closed_text = "FALSE";
		 }
		
		 if
		 (doc.select("#ur > div.block-inner > ul > li.first > span > a > em").isEmpty()){
		 all_comments_text = "-";
		 }else {
		 all_comments_text =
		 doc.select("#ur > div.block-inner > ul > li.first > span > a > em").first().ownText().replaceAll(
		 "\\(|\\)|[\\u4e00-\\u9fa5]", "");
		 }
		
		if (doc.select("ul.rank-list > li > h4 > a").isEmpty()) {
			views_text = "-";
		} else {
			Iterator<Element> i = doc.select("ul.rank-list > li > h4 > a")
					.iterator();
			while (i.hasNext()) {
				Element e = i.next();
				views_text += (e.attr("href").replace(
						"http://www.dianping.com/shop/", "").replace("/shop/", "") + ",");
			}
		}
		
		try {
			Element star_5 = doc.select(
					"#ur > div.block-inner > div > ul > li:nth-child(1) > a")
					.first();
			Element star_4 = doc.select(
					"#ur > div.block-inner > div > ul > li:nth-child(2) > a")
					.first();
			Element star_3 = doc.select(
					"#ur > div.block-inner > div > ul > li:nth-child(3) > a")
					.first();
			Element star_2 = doc.select(
					"#ur > div.block-inner > div > ul > li:nth-child(4) > a")
					.first();
			Element star_1 = doc.select(
					"#ur > div.block-inner > div > ul > li:nth-child(5) > a")
					.first();
			stars_text = star_1.ownText().replaceAll(
					"\\(|\\)|[\\u4e00-\\u9fa5]", "")
					+ ","
					+ star_2.ownText().replaceAll("\\(|\\)|[\\u4e00-\\u9fa5]",
							"")
					+ ","
					+ star_3.ownText().replaceAll("\\(|\\)|[\\u4e00-\\u9fa5]",
							"")
					+ ","
					+ star_4.ownText().replaceAll("\\(|\\)|[\\u4e00-\\u9fa5]",
							"")
					+ ","
					+ star_5.ownText().replaceAll("\\(|\\)|[\\u4e00-\\u9fa5]",
							"");
		} catch (Exception e) {
			stars_text = "-";
		}
		
		return type + "|" + cat_text + "|" + closed_text
				+ "|" + all_comments_text + "|" + views_text + "|" + stars_text
				+ "|" + shops_inside + "|";	
	}
	public static String parseMall(String html){
		String type = "mall",cat_text = "", closed_text = "-", all_comments_text = "-", views_text = "", stars_text = "", shops_inside = "-";
		Document doc = Jsoup.parse(html);
		Elements cats = doc.select("div.breadcrumb > a");
		Iterator<Element> it = cats.iterator();
		while (it.hasNext()) {
			Element next = it.next();
			cat_text += (next.ownText() + ",");
		}

		if (doc.select("p.shop-closed").isEmpty()) {
			closed_text = "FALSE";
		} else {
			closed_text = "TRUE";
		}

		if (doc.select("#comment > h2 > a:nth-child(2) > span").isEmpty()) {
			all_comments_text = "-";
		} else {
			all_comments_text = doc
					.select("#comment > h2 > a:nth-child(2) > span").first()
					.ownText().replaceAll("\\(|\\)", "");
		}

		if (doc.select("#aside > div.mod.aside-mod.view-about > ul > li > a")
				.isEmpty()) {
			views_text = "-";
		} else {
			Iterator<Element> i = doc.select(
					"#aside > div.mod.aside-mod.view-about > ul > li > a")
					.iterator();
			while (i.hasNext()) {
				Element e = i.next();
				views_text += (e.attr("href").replace(
						"http://www.dianping.com/shop/", "") + ",");
			}
		}

		try {
			Element star_5 = doc.select(
					"#comment > h2 > span > a:nth-child(2) > span").first();
			Element star_4 = doc.select(
					"#comment > h2 > span > a:nth-child(3) > span").first();
			Element star_3 = doc.select(
					"#comment > h2 > span > a:nth-child(4) > span").first();
			Element star_2 = doc.select(
					"#comment > h2 > span > a:nth-child(5) > span").first();
			Element star_1 = doc.select(
					"#comment > h2 > span > a:nth-child(6) > span").first();
			stars_text = star_1.ownText().replaceAll("\\(|\\)", "") + ","
					+ star_2.ownText().replaceAll("\\(|\\)", "") + ","
					+ star_3.ownText().replaceAll("\\(|\\)", "") + ","
					+ star_4.ownText().replaceAll("\\(|\\)", "") + ","
					+ star_5.ownText().replaceAll("\\(|\\)", "");
		} catch (Exception e) {
			stars_text = "-";
		}
		
		Pattern p = Pattern.compile("brandList:\\[.+\\]");
		Matcher matcher = p.matcher(html);
		if (matcher.find()) {
			shops_inside = matcher.group();
		}
		
		return type + "|" + cat_text + "|" + closed_text
				+ "|" + all_comments_text + "|" + views_text + "|" + stars_text
				+ "|" + shops_inside + "|";
	}
}

