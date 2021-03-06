package jNovel.kernel;

import jNovel.kernel.utils.FileUtils;
import jNovel.kernel.utils.Logger;
import jNovel.option.Option;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class Downloader {

	public static final String MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; U; Android 4.0.3; zh-tw; HTC_Sensation_Z710e Build/IML74K)AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";

	private String[] urlStrings;
	private int toPage;
	private UrlData urlData;
	private static String sessionId;

	public Downloader() {

	}

	public void setup(int page, String urlString) {

		Logger.print("分析網址");

		urlData = Analysis.analysisUrl(urlString); // 分析網址
		this.toPage = page;
	}

	public UrlData getUrlData() {

		return urlData;
	}

	public void generateUrlList() {

		Logger.print("產生下載清單...");
		// http://ck101.com/thread-1753100-55-1.html
		urlStrings = new String[toPage - urlData.page + 1];

		if (urlData.domain.indexOf("eyny") >= 0) {
			// http://www02.eyny.com/archiver/?tid-8910527.html&mobile=yes
			String temp = String.format(
					"http://%s/archiver/?tid-%s&mobile=yes&page=",
					urlData.domain, String.valueOf(urlData.Tid));
			int m = 0;
			for (int n = urlData.page; n <= toPage; n++) {
				urlStrings[m++] = temp + n;
			}
		} else {
			String temp = String.format("http://%s/thread-%s-", urlData.domain,
					String.valueOf(urlData.Tid));
			int m = 0;
			for (int n = urlData.page; n <= toPage; n++) {
				urlStrings[m++] = temp + n + "-1.html";
			}
		}
	}

	/**
	 * 下載檔案，透過 Multi-Thread 加速～
	 * 
	 * @param option
	 * @param book
	 * @return
	 * @throws IOException
	 */
	public boolean downloading(Option option, ReadHtml book) throws IOException {

		// 需要重點加速的地方

		if (urlData.wrongUrl) {
			Logger.print("網址有問題 無法分析");
			return false;
		} else {
			/*
			 * if (urlData.domain.indexOf("eyny") >= 0) {
			 * Downloader.sendLoginRequest(); }
			 */
			generateUrlList();
			// http://ck101.com/thread-1753100-55-1.html
			int m = 0;
			int threadNumber = option.threadNumber;
			int morethread = urlStrings.length % threadNumber; // 有多少執行續會多一個檔案
			int tempNumber = urlStrings.length / threadNumber; // 每個執行續最少多少個檔案

			DownloadThread[] downloadThread = new DownloadThread[threadNumber];
			String[] from;
			String[] to;
			String[] totalTo = new String[urlStrings.length];
			int number;
			/*************** 建立存放清單 ******************************/
			for (int n = urlData.page; n <= toPage; n++) {
				// from[m]=urlStrings[m];
				totalTo[m] = option.tempPath + "thread-" + urlData.Tid + "-"
						+ n + "-1.html";
				;
				// book.addFileName(totalTo[m]);
				m++;
			}
			m = 0;
			/*************** 分派任務 ******************************/
			for (int x = 0; x < threadNumber; x++) {
				if (morethread > 0) {
					number = tempNumber + 1;
					morethread--;
				} else {
					number = tempNumber;
				}
				from = new String[number];
				to = new String[number];
				for (int y = 0; y < number; y++) {
					from[y] = urlStrings[m];
					to[y] = totalTo[m++];
				}
				downloadThread[x] = new DownloadThread(from, to, x);
				// 放入任務
				// downloadThread[x] = new DownloadThread(from, to, x); // 放入任務
				book.addFileName(to);//
				downloadThread[x].start();// 執行任務
			}
			try {
				/*************** 確保任務執行完再繼續 *****************************/
				for (int x = 0; x < threadNumber; x++) {
					downloadThread[x].join();
				}
			} catch (InterruptedException e) {
			}
			for (int x = 0; x < threadNumber; x++) {
				if (!downloadThread[x].downloadstate)
					return false;
			}
			return true;
		}
	}

	public static void sendLoginRequest() throws IOException {

		String temp, cookie_sid = null, cookie_auth = null;
		String login_formhash = null, post_formhash;
		InputStream is;
		URL loginUrl = new URL(
				"http://www.eyny.com/member.php?mod=logging&action=login&mobile=yes");
		HttpURLConnection con = (HttpURLConnection) loginUrl.openConnection();
		if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
			// 获取服务器发给客户端的 Cookie
			temp = con.getHeaderField("Set-Cookie");
			System.out.println("Set-Cookie:" + temp);
			// 取 Cookie 前面的部分就可以了，后面是过期时间、路径等，不用管它
			cookie_sid = temp.substring(0, 9);
			System.out.println(cookie_sid);

			is = con.getInputStream();
			byte[] b = new byte[is.available()];
			is.read(b);
			// 服务器会返回一个页面，此页面中包含 formhash
			temp = new String(b);
			// 找出这个 formhash 的位置
			// System.out.println(temp);
			int pos = temp.indexOf("name=\"formhash\" id=\"formhash\" value=");
			// System.out.println(temp);
			// 找出这个 formhash 的内容，这是登录用的 formhash
			login_formhash = temp.substring(pos + 37, pos + 37 + 8);
			System.out.println("login_formhash:" + login_formhash);
			System.out
					.println("------------------------------------------------------------");
			is.close();
		}

		// 获取cookie_auth -----------------------------------------------
		// loginUrl = new
		// URL("http://www.eyny.com/member.php?mod=logging&action=login&loginsubmit=yes&loginhash=Lv83q&mobile=yes&formhash="
		// + login_formhash
		// + "&username="
		// + "pupuliao"
		// + "&password=" + "781230");
		loginUrl = new URL("http://www.eyny.com/member.php?mod=logging&");
		con = (HttpURLConnection) loginUrl.openConnection();

		// 设定以 POST 发送
		con.setRequestMethod("POST");
		// 加入 Cookie 内容
		con.setRequestProperty("Cookie", cookie_sid);
		con.setConnectTimeout(5000);
		con.setRequestProperty("Keep-Alive", "300");
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		// 添加 POST 的内容
		con.setDoOutput(true);
		OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream());
		osw.write("action=login&loginsubmit=yes&loginhash=Lv83q&mobile=yes&formhash="
				+ login_formhash
				+ "&username="
				+ "pupuliao"
				+ "&password="
				+ "781230"
				+ "&submit=%E7%99%BB%E9%8C%84&questionid=0&answer=&cookietime=2592000");
		osw.flush();
		osw.close();
		// System.out.println(con.getResponseCode());
		if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
			// System.err.println("OK");
			Map<String, List<String>> map = con.getHeaderFields();
			System.out.println(con.getHeaderFields());
			List<String> list = map.get("Set-Cookie");
			for (int i = 0; i < list.size(); i++) {
				temp = list.get(i);
				System.out.println(temp);
				if (temp.contains("_auth") && !temp.contains("_invite_auth")) {
					System.out.println(temp);
					// 取 Cookie 前面的部分就可以了，后面是过期时间、路径等，不用管它
					cookie_auth = temp.split(";")[0];
					System.out.println("cookie_auth:" + cookie_auth);
				}
				if (temp.contains("_lastvisit")) {
					System.out.println(temp);
					// 取 Cookie 前面的部分就可以了，后面是过期时间、路径等，不用管它
					cookie_auth = cookie_auth + "; " + temp.split(";")[0];
					System.out.println("cookie_auth:" + cookie_auth);
				}
				if (temp.contains("_lastact")) {
					System.out.println(temp);
					// 取 Cookie 前面的部分就可以了，后面是过期时间、路径等，不用管它
					cookie_auth = cookie_auth + "; " + temp.split(";")[0];
					System.out.println("cookie_auth:" + cookie_auth);
				}
			}
			cookie_auth += "; username=pupuliao; djAX_e8d7_sid=kZ84lN; djAX_e8d7_visitedfid=1775; djAX_e8d7_ulastactivity=003aqxA93kX5ejkAKI3rShiPwFbKtyuFz4bx9WLU6nVXBVSEgBEj";
			is = con.getInputStream();
			byte[] b = new byte[is.available()];
			is.read(b);
			// System.out.println(new String(b));
			System.out
					.println("------------------------------------------------------------");
			is.close();
		}
		sessionId = cookie_auth;
		System.out.println(sessionId);
		//
		URL url = new URL(
				"http://www.eyny.com/forum.php?mod=viewthread&tid=7617222&mobile=yes");
		con = (HttpURLConnection) url.openConnection();
		con.setConnectTimeout(5000);
		con.setRequestProperty("Keep-Alive", "300");
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Accept-Language",
				"zh-tw,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		con.setRequestProperty("Accept-Encoding", "gzip, deflate");
		con.setRequestProperty("Cookie", sessionId);
		con.setRequestProperty("User-Agent", MOBILE_USER_AGENT);

		con.connect();

		// BufferedReader reader = new BufferedReader(new
		// InputStreamReader(inStream, "utf8"));
		BufferedReader reader = FileUtils.readFileFromStream(con
				.getInputStream());

		String line = "";
		StringBuffer total = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			total.append(line + "\n");
		}

		String tempFilePath = File.createTempFile("loginRequest", ".tmp")
				.getAbsolutePath();

		Logger.printf("寫入 temp : %s", tempFilePath);
		FileUtils.writeData(tempFilePath, total.toString());
		//
		// OutputStreamWriter writer = new OutputStreamWriter(new
		// FileOutputStream("K:\123.html"),
		// "UTF-8");
		// writer.write(total.toString());
		// writer.flush();
		// writer.close();

		System.out.println("下載完成");

	}

}
