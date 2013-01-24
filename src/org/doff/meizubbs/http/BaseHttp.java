package org.doff.meizubbs.http;

import java.util.HashMap;
import java.util.Map;

public class BaseHttp {
	/**
	 * «Î«Û≥¨ ±…Ë÷√
	 * @return
	 */
	protected int getTimeout() {
		return 15000;
	}
	
	Map<String ,String> header ;
	protected Map<String,String> getHeaders() {
		if (header==null) {
			header=new HashMap<String, String>();
		
			header.put("Accept-Encoding", "gzip, deflate");
		}
		return header;
	}
	
	
	
}
