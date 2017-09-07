package com.strd.emailcrawler.util;

/**
 * Util class that formats URL pages
 * @author psilveira
 *
 */
public class UrlFormatter {
	
	/**
	 * Returns a formatted URL
	 * @param url
	 * @return
	 */
	public static String formatUrl(String url) {
		return url.toLowerCase()
				.replaceAll("http://", "")
				.replaceAll("www.", "")
				.replaceAll("/$", "");
	}

}
