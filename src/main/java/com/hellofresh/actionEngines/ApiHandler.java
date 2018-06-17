package com.hellofresh.actionEngines;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;

import com.hellofresh.utilities.Getconfig;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiHandler {

	private static HttpHost PROXY_STR=null;  // As we are not restricted with any https/ http proxy , I have set it to NULL.
	private static boolean IGNORE_SSL_ERRORS = false;

	@SuppressWarnings("deprecation")
	private static OkHttpClient getClient() throws MalformedURLException {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();

		if (IGNORE_SSL_ERRORS) {
		    try {
				    // Create a trust manager that does not validate certificate chains
				    final TrustManager[] trustAllCerts = new TrustManager[] {
				        new X509TrustManager() {
					          @Override
					          public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
					          }
					          @Override
					          public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
					          }
					          @Override
					          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					            return new java.security.cert.X509Certificate[]{};
					          }
						}
				    };

				    // Install the all-trusting trust manager
				    final SSLContext sslContext = SSLContext.getInstance("SSL");
						sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
				    // Create an ssl socket factory with our all-trusting manager
				    final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

				    builder.sslSocketFactory(sslSocketFactory);
				    builder.hostnameVerifier(new HostnameVerifier() {
					      @Override
						public boolean verify(String hostname, SSLSession session) {
					        return true;
					      }
					});
			} catch (KeyManagementException | NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}


		OkHttpClient client ;

		/*When need to run from the local machine*/
		if (PROXY_STR != null) {
			URL proxyUrl = new URL(PROXY_STR.toString());

			Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxyUrl.getHost(), proxyUrl.getPort()));
			client = builder.proxy(proxy).build();
		} else {
			client = builder.build();
		}
		return client;
	}

	// GET API CALL Builder

	public static Response getApi(String url) throws IOException, Exception {

		OkHttpClient client = getClient();

		Builder builder = new Request.Builder().url(url).get();
		Request request = builder.build();
		Response response = client.newCall(request).execute();

		System.out.println("Response:::"+response);
		return response;

	}

	// POST API CALL Builder
	public static Response postApi(String url, String strBody) throws IOException, Exception {
		OkHttpClient client = getClient();
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(MediaType.parse(strBody), strBody);
		Builder builder = new Request.Builder().url(url).post(body);
		Request request = builder.build();
		Response response = client.newCall(request).execute();
		return response;
	}

	// To get the POST API response in the String format by String Buffer.
	public static String apiResponseContent(Response postrsp) throws IOException {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		br = new BufferedReader(new InputStreamReader(postrsp.body().byteStream()));
		String api_response = "";
		while ((api_response = br.readLine()) != null) {
			sb.append(api_response);
		}
		return sb.toString();
	}

	// To get the status code of the response
	public static int responseCode(Response response) {
		int status_code = response.code();
		return status_code;
	}

	private static List<String> fileStrings = new ArrayList<>();

	@SuppressWarnings("unused")
	private static void saveBody(Response response) throws IOException {
		File file = File.createTempFile("test-file", "txt");
		IOUtils.write(response.body().string(), new FileOutputStream(file));
		fileStrings.add(file.getAbsolutePath());
	}

	@AfterMethod
	public void saveBody(ITestResult testResult) throws IOException {
		if (fileStrings.size() > 0) {
			File scrFile = new File(fileStrings.get(fileStrings.size() - 1));
			FileUtils.copyFile(scrFile, new File(getFilePath(testResult)));

			fileStrings = new ArrayList<>();
		}
	}

	private String getFilePath(ITestResult testResult) {
		String path = System.getProperty("user.dir") + File.separator + "test-output" + File.separator + "images";
		File filePath = new File(path);
		filePath.mkdirs();

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date(testResult.getEndMillis()));
		String filePathName = filePath.getAbsolutePath() + File.separator + testResult.getName() + timeStamp + ".txt";
		return filePathName;
	}

	public String path(String collectionPath) {
		return System.getProperty("user.dir") + Getconfig.get_properties(collectionPath);
	}

}
