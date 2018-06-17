package com.hellofresh.Apitestscripts;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.hellofresh.actionEngines.ApiHandler;
import com.hellofresh.actionEngines.PostmanCollection;
import com.hellofresh.utilities.ApiPOJO;

import okhttp3.Response;

public class GroupktServices extends ApiHandler {
	public static String responseBody;
	public static JSONObject js;
	List<String> nameOfCountry = new ArrayList<String>();
	List<String> alpha2CountryCode = new ArrayList<String>();
	List<String> alpha3CountryCode = new ArrayList<String>();

	@Test(dataProvider = "getDataForAllCountries", description = "Validating GroupKtServices APIs for all countries", enabled = true)
	public void getAPIForAllCountries(String name, String urlStr, String method, String body) throws Exception {

		try {
			URL url = new URL(urlStr);
			String path = url.getPath();

			if (url.getQuery() != null && url.getQuery().length() > 0) {
				path = path + "?" + url.getQuery();
			}
			URL newUri = new URL(url.getProtocol() + "://" + url.getHost() + path);

			Response rsp = null;

			// GET API CALL for all countries
			if (method.equals("GET") && name.equals("GET all countries data")) {
				String actualUrl = newUri.toString();
				rsp = getApi(actualUrl);
				int rspStatusCode = responseCode(rsp);
				responseBody = apiResponseContent(rsp);
				js = new JSONObject(responseBody);
				JSONArray resultArray = js.getJSONObject("RestResponse").getJSONArray("result");

				for (int i = 0; i < resultArray.length(); i++) {
					nameOfCountry.add(resultArray.getJSONObject(i).getString("name"));
					alpha2CountryCode.add(resultArray.getJSONObject(i).getString("alpha2_code"));
					alpha3CountryCode.add(resultArray.getJSONObject(i).getString("alpha3_code"));
				}

				try {
					// Assert the number of countries showing in message is same as that of number
					// of countries showing in the result object.
					Assert.assertTrue(js.getJSONObject("RestResponse").getJSONArray("messages").toString()
							.contains(String.valueOf(resultArray.length())));

					Assert.assertEquals(rspStatusCode, 200, "For URL: " + url + " method: " + method
							+ " actual status was: " + rspStatusCode + ", expected status: 200");

					Assert.assertTrue(alpha2CountryCode.contains("US"),
							"The result contains the alpha2Country code: US ");

					Assert.assertTrue(alpha2CountryCode.contains("DE"),
							"The result contains the alpha2Country code: DE ");

					Assert.assertTrue(alpha2CountryCode.contains("GB"),
							"The result contains the alpha2Country code: GB ");

				} catch (AssertionError ae) {
					System.out.println(ae.getMessage());
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@Test(dataProvider = "getDataForSpecificCountries", description = "Validating GroupKtServices APIs for US DE and GB", enabled = true)
	public void getAPIForUS_DE_GB(String name, String urlStr, String method, String body) throws Exception {

		try {
			URL url = new URL(urlStr);
			String path = url.getPath();
			String[] pathvar = path.split("/");
			String country_code = pathvar[pathvar.length - 1];

			if (url.getQuery() != null && url.getQuery().length() > 0) {
				path = path + "?" + url.getQuery();
			}
			URL newUri = new URL(url.getProtocol() + "://" + url.getHost() + path);

			Response rsp = null;

			if (method.equals("GET") && name.equals("GET call to view country as " + country_code)) {
				String actualUrl = newUri.toString();
				rsp = getApi(actualUrl);
				int rspStatusCode = responseCode(rsp);
				responseBody = apiResponseContent(rsp);
				js = new JSONObject(responseBody);
				JSONObject resultObj = js.getJSONObject("RestResponse").getJSONObject("result");

				try {
					Assert.assertEquals(rspStatusCode, 200, "For URL: " + url + " method: " + method
							+ " actual status was: " + rspStatusCode + ", expected status: 200");

					Assert.assertEquals(resultObj.get("alpha2_code"), country_code);

				} catch (AssertionError ae) {
					System.out.println(ae.getMessage());
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@Test(dataProvider = "getDataForNonExistantCountries", description = "Validating GroupKtServices APIs for invalid country code", enabled = true)
	public void getAPIForInexistantCountr(String name, String urlStr, String method, String body) throws Exception {

		try {
			URL url = new URL(urlStr);
			String path = url.getPath();
			String[] pathvar = path.split("/");
			String country_code = pathvar[pathvar.length - 1];

			if (url.getQuery() != null && url.getQuery().length() > 0) {
				path = path + "?" + url.getQuery();
			}
			URL newUri = new URL(url.getProtocol() + "://" + url.getHost() + path);

			Response rsp = null;

			if (method.equals("GET") && name.equals("GET call to view country as " + country_code)) {
				String actualUrl = newUri.toString();
				rsp = getApi(actualUrl);
				int rspStatusCode = responseCode(rsp);
				responseBody = apiResponseContent(rsp);
				js = new JSONObject(responseBody);
				JSONObject resultObj = js.getJSONObject("RestResponse").getJSONObject("messages");

				try {
					Assert.assertEquals(rspStatusCode, 200, "For URL: " + url + " method: " + method
							+ " actual status was: " + rspStatusCode + ", expected status: 200");

					Assert.assertTrue(resultObj.toString()
							.equals("No matching country found for requested code [" + country_code + "]"));

				} catch (AssertionError ae) {
					System.out.println(ae.getMessage());
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@Test(dataProvider = "getDataForNewCountryAddition", description = "Post api call to add a new country in the GroupKtServices", enabled = true)
	public void getAPIForUS(String name, String urlStr, String method, String body) throws Exception {

		try {
			URL url = new URL(urlStr);
			String path = url.getPath();
			String[] pathvar = path.split("/");
			String country_code = pathvar[pathvar.length - 1];

			if (url.getQuery() != null && url.getQuery().length() > 0) {
				path = path + "?" + url.getQuery();
			}
			URL newUri = new URL(url.getProtocol() + "://" + url.getHost() + path);

			Response rsp = null;

			if (method.equals("POST") && name.equals("POST call to add new Country")) {
				String actualUrl = newUri.toString();
				rsp = postApi(actualUrl, body);
				int rspStatusCode = responseCode(rsp);
				responseBody = apiResponseContent(rsp);
				js = new JSONObject(responseBody);
				JSONObject resultObj = js.getJSONObject("RestResponse").getJSONObject("result");

				try {

					/*
					 * Assertion for new Country addition, status code and result object validation
					 * 
					 * Assert.assertEquals(rspStatusCode, 200, "For URL: " + url + " method: " +
					 * method
					 * 
					 * + " actual status was: " + rspStatusCode + ", expected status: 200");
					 * 
					 * Assert.assertEquals(resultObj.get("alpha2_code"), country_code);
					 * 
					 * //Assertion between response body alpha2Code and request body alpha2Code
					 * 
					 * Assert.assertEquals(resultObj.get("alpha2_code"),JSONObject(body).get(
					 * alpha2_code));
					 */

				} catch (AssertionError ae) {
					System.out.println(ae.getMessage());
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@DataProvider
	public Object[][] getDataForAllCountries() throws Exception {
		List<ApiPOJO> apiValues = PostmanCollection.apiUrlHeaders(path("HelloFreshAPIForAllCountries"));
		Object[][] ret = new Object[apiValues.size()][1];
		int idx = 0;
		for (ApiPOJO apiCall : apiValues) {
			ret[idx] = new Object[] { apiCall.getName(), apiCall.getUrl(), apiCall.getMethod(), apiCall.getBody() };
			idx = idx + 1;
		}
		return ret;

	}

	@DataProvider
	public Object[][] getDataForSpecificCountries() throws Exception {
		List<ApiPOJO> apiValues = PostmanCollection.apiUrlHeaders(path("HelloFreshAPIForSpecificCountries"));
		Object[][] ret = new Object[apiValues.size()][1];
		int idx = 0;
		for (ApiPOJO apiCall : apiValues) {
			ret[idx] = new Object[] { apiCall.getName(), apiCall.getUrl(), apiCall.getMethod(), apiCall.getBody() };
			idx = idx + 1;
		}
		return ret;

	}

	@DataProvider
	public Object[][] getDataForNonExistantCountries() throws Exception {
		List<ApiPOJO> apiValues = PostmanCollection.apiUrlHeaders(path("HelloFreshAPIForNonExistantCountries"));
		Object[][] ret = new Object[apiValues.size()][1];
		int idx = 0;
		for (ApiPOJO apiCall : apiValues) {
			ret[idx] = new Object[] { apiCall.getName(), apiCall.getUrl(), apiCall.getMethod(), apiCall.getBody() };
			idx = idx + 1;
		}
		return ret;

	}

	@DataProvider
	public Object[][] getDataForNewCountryAddition() throws Exception {
		List<ApiPOJO> apiValues = PostmanCollection.apiUrlHeaders(path("HelloFreshAPIForNewCountryAddition"));
		Object[][] ret = new Object[apiValues.size()][1];
		int idx = 0;
		for (ApiPOJO apiCall : apiValues) {
			ret[idx] = new Object[] { apiCall.getName(), apiCall.getUrl(), apiCall.getMethod(), apiCall.getBody() };
			idx = idx + 1;
		}
		return ret;

	}
}
