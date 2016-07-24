package org.giiwa.forum.bean;

import java.net.URLEncoder;

import org.giiwa.core.base.GString;
import org.giiwa.core.base.Http;
import org.giiwa.core.bean.X;

public class IP {

	static final String ip126 = "http://ip.ws.126.net/ipquery?ip=$ip";

	public static Address get(String ip) {
		String req = ip126.replaceAll("\\$ip", URLEncoder.encode(ip));
		Http.Response r = Http.get(req);
		GString st = GString.create(r.body);
		String lo = st.substring("lo=\"", "\"");
		String lc = st.substring("lc=\"", "\"");
		return X.isEmpty(lo) ? null : new Address(ip, lo, lc);
	}

	public static final class Address {
		/**
		 * 
		 */
		public String ip;
		public String lo;
		public String lc;

		public Address(String ip, String lo, String lc) {
			this.ip = ip;
			this.lo = lo;
			this.lc = lc;
		}
	}
}
