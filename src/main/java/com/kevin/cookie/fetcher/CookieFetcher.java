package com.kevin.cookie.fetcher;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import com.kevin.common.util.HttpUtil;
import com.kevin.common.util.JsonUtil;
import com.kevin.cookie.dto.SPDto;
import com.kevin.cookie.dto.TCWDto;
import lombok.extern.slf4j.Slf4j;

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

    private SPDto getCookies() {
        String time = System.currentTimeMillis() + "";
        time
    }

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
            String where = data.getBoolean("new_tid") ? "3" : "2";
            String confidence = data.getIntValue("confidence") != 0 ?
                    "000" + data.getIntValue("confidence") :
                    "100";
            confidence = confidence.substring(confidence.length() - 3);
            return new TCWDto(tid, where, confidence);
        }
        return null;
    }

    private SPDto getSubAndSubP(TCWDto tcw, String passportUrl) {
        if (tcw == null) {
            return null;
        }
        StringBuilder urlBuilder = new StringBuilder(VISITOR_URL);
        urlBuilder.append("&t=").append(tcw.getTid())
                .append("&w=").append(tcw.getWhere())
                .append("&c=").append(tcw.getConfidence())
                .append("&gc=&cb=cross_domain&from=weibo&_rand=").append(Math.random());
        String url = urlBuilder.toString();

        Map<String, String> headers = Maps.newHashMap();
        headers.put(HttpHeaders.ACCEPT, "*/*");
        headers.put(HttpHeaders.HOST, "passport.weibo.com");
        headers.put(HttpHeaders.COOKIE, "tid=" + tcw.getTid() + "__0" + tcw.getConfidence());
        headers.put(HttpHeaders.REFERER, passportUrl);

        String resp = HttpUtil.get(url, headers);
        resp = resp.substring(resp.indexOf("(") + 1, resp.lastIndexOf(")"));
        JSONObject respJson = JsonUtil.json2JsonObject(resp);
        if (respJson.getIntValue("retcode") == 20000000) {
            JSONObject data = respJson.getJSONObject("data");
            String sub = data.getString("sub");
            String subp = data.getString("subp");
            return new SPDto(sub, subp);
        }
        return null;
    }
}
