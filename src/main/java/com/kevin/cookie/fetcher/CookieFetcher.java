package com.kevin.cookie.fetcher;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import com.kevin.common.util.HttpUtil;
import com.kevin.common.util.JsonUtil;
import com.kevin.cookie.dto.TCWDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 类名: WeiboCookieFetcher<br/>
 * 包名：com.kevin.cookie<br/>
 * 作者：kevin<br/>
 * 时间：2019/3/31 20:59<br/>
 * 版本：1.0<br/>
 * 描述：
 */
@Slf4j
public class WeiboCookieFetcher {

    private static final String PASSPORT_URL = "https://passport.weibo.com/visitor/visitor?entry=miniblog&a=enter&url=https%3A%2F%2Fweibo.com%2F&domain=.weibo.com&ua=php-sso_sdk_client-0.6.28&_rand=";

    private static final String GEN_VISITOR_URL = "https://passport.weibo.com/visitor/genvisitor";

    private static final String VISITOR_URL = "https://passport.weibo.com/visitor/visitor?a=incarnate";

    public static String getCookie() {
            Map<String, String> map;
//            while (true) {
//                map = getCookieParam();
//                if (map.containsKey("SUB") && map.containsKey("SUBP") &&
//                        StringUtils.isNoneEmpty(map.get("SUB"), map.get("SUBP")))
//                    break;
//                HttpClientInstance.instance().changeProxy();
//            }
//            return " YF-Page-G0=" + "; _s_tentry=-; SUB=" + map.get("SUB") + "; SUBP=" + map.get("SUBP");
            return "";
        }

//        private Map<String, String> getCookieParam() {
//            String time = System.currentTimeMillis() + "";
//            time = time.substring(0, 9) + "." + time.substring(9, 13);
//            String passporturl = PASSPORT_URL + "&_rand=" + time;
//
//            String tid = "";
//            String c = "";
//            String w = "";
//            {
//                String str = postGenvisitor(passporturl);
//                if (str.contains("\"retcode\":20000000")) {
//                    JSONObject jsonObject = new JSONObject(str).getJSONObject("data");
//                    tid = jsonObject.optString("tid");
//                    try {
//                        tid = URLEncoder.encode(tid, "utf-8");
//                    } catch (UnsupportedEncodingException e) {
//                    }
//                    c = jsonObject.has("confidence") ? "000" + jsonObject.getInt("confidence") : "100";
//                    w = jsonObject.optBoolean("new_tid") ? "3" : "2";
//                }
//            }
//            String s = "";
//            String sp = "";
//            {
//                if (StringUtils.isNoneEmpty(tid, w, c)) {
//                    String str = getVisitor(tid, w, c, passporturl);
//                    str = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
//                    if (str.contains("\"retcode\":20000000")) {
//                        System.out.println(new JSONObject(str).toString(2));
//                        JSONObject jsonObject = new JSONObject(str).getJSONObject("data");
//                        s = jsonObject.getString("sub");
//                        sp = jsonObject.getString("subp");
//                    }
//
//                }
//            }
//            Map<String, String> map = Maps.newHashMap();
//            map.put("SUB", s);
//            map.put("SUBP", sp);
//            return map;
//        }

        private TCWDto getTCW(String passportUrl) {
            Map<String, String> headers = new HashMap<>();
            headers.put(HttpHeaders.ACCEPT, "*/*");
            headers.put(HttpHeaders.ORIGIN, "https://passport.weibo.com");
            headers.put(HttpHeaders.REFERER, passportUrl);

            Map<String, String> params = Maps.newHashMap();
            params.put("cb", "gen_callback");
            params.put("fp", "{\"os\":\"1\",\"browser\":\"Chrome73,0,3683,86\",\"fonts\":\"undefined\",\"screenInfo\":\"1280*1024*24\",\"plugins\":\"Portable Document Format::internal-pdf-viewer::Chrome PDF Plugin|::mhjfbmdgcfjbbpaeojofohoefgiehjai::Chrome PDF Viewer|::internal-nacl-plugin::Native Client\"}");

            String resp = HttpUtil.post(GEN_VISITOR_URL, headers, params);
            resp = resp.substring(resp.indexOf("(") + 1, resp.lastIndexOf(")"));
            JSONObject respJson = JsonUtil.json2JsonObject(resp);
            if (respJson.getIntValue("retcode") == 20000000) {
                JSONObject data = respJson.getJSONObject("data");
                String tid = data.getString("tid");
                try {
                    tid = URLEncoder.encode(tid, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // do nothing
                }
                String confidence = data.getIntValue("confidence") != 0 ?
                        "000" + data.getIntValue("confidence") :
                        "100";
                String where = data.getBoolean("new_tid") ? "3" : "2";
                return new TCWDto(tid, confidence, where);
            }
            return null;
        }

        private String getVisitor(String tid, String w, String c, String passporturl) {
            String url = VISITOR_URL + "&t=" + tid + "&w=" + "&c=" + c.substring(c.length() - 3)
                    + "&gc=&cb=cross_domain&from=weibo&_rand=0." + rand();

            Map<String, String> headers = Maps.newHashMap();
            headers.put(HttpHeaders.ACCEPT, "*/*");
            headers.put(HttpHeaders.HOST, "passport.weibo.com");
            headers.put(HttpHeaders.COOKIE, "tid=" + tid + "__0" + c);
            headers.put(HttpHeaders.REFERER, passporturl);

            HttpGet httpGet = HttpRequestUtils.createHttpGet(url, headers);
            httpGet.setConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build());
            return HttpClientInstance.instance().execute(httpGet, null);
        }

        private static String rand() {
            return new BigDecimal(Math.floor(Math.random() * 10000000000000000L)).toString();
        }
    }
}
