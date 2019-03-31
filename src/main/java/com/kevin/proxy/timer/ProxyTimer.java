package com.kevin.proxy.timer;

import com.alibaba.fastjson.JSONObject;
import com.kevin.common.util.HttpUtil;
import com.kevin.common.util.JsonUtil;
import com.kevin.proxy.dto.ProxyDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 类名: ProxyTask<br/>
 * 包名：com.kevin.crawler.timer<br/>
 * 作者：kevin<br/>
 * 时间：2019/3/31 19:56<br/>
 * 版本：1.0<br/>
 * 描述：
 */
@Component
@Slf4j
public class ProxyTimer {

    public static BlockingQueue<ProxyDto> proxyQueue = new ArrayBlockingQueue<>(200);

    @Scheduled(cron = "0 0 */10 * * ?")
    public static void proxy() {
        if (proxyQueue.size() < 100) {
            final String url = "https://raw.githubusercontent.com/fate0/proxylist/master/proxy.list";
            String resp = HttpUtil.get(url);
            String[] proxyInfo = resp.split("\\n");
            CloseableHttpClient httpClient = HttpClients.createDefault();
            for (String proxy : proxyInfo) {
                JSONObject proxyJson = JsonUtil.json2JsonObject(proxy);
                String host = proxyJson.getString("host");
                String port = proxyJson.getString("port");
                String type = proxyJson.getString("type");

                HttpHost proxyHost = new HttpHost(host, Integer.parseInt(port), type);
                RequestConfig requestConfig = RequestConfig.custom().setProxy(proxyHost)
                        .setConnectTimeout(2000).setSocketTimeout(2000).build();
                HttpGet httpGet = new HttpGet("https://www.baidu.com");
                httpGet.setConfig(requestConfig);
                try {
                    httpClient.execute(httpGet);
                    log.info("valid ip:[" + host + "," + port + "," + type + "]");
                    proxyQueue.add(new ProxyDto(host, port, type));
                } catch (IOException e) {
                    log.info("invalid ip:[" + host + "," + port + "," + type + "]");
                }
            }
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
