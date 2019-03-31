package com.kevin.common.util;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类名：HttpUtil<br/>
 * 包名：com.kevin.util<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2017/7/10 19:14<br/>
 * 版本：1.0<br/>
 * 描述：Http工具类<br/>
 */
public class HttpUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    private static final String GET_START = "-------HttpClient GET开始---------";
    private static final String GET_END = "-------HttpClient GET结束----------";
    private static final String GET_URL_ILLEGAL = "GET请求不合法，请检查uri参数!";
    private static final String GET = "GET: {}";
    private static final String GET_ERROR = "GET请求失败";
    private static final String POST_START = "-------HttpClient POST开始----------";
    private static final String POST_END = "-------HttpClient POST结束----------";
    private static final String POST_URL_ILLEGAL = "POST请求不合法，请检查uri参数!";
    private static final String POST = "POST: {}";
    private static final String POST_ERROR = "POST请求失败";
    private static final String REQ = "req: {}";
    private static final String UTF8 = "UTF-8";

    /**
     * 连接池最大连接数
     **/
    private static final int HTTPCLIENT_MAX_TOTAL = 200;
    /**
     * 单个路由最大连接数
     **/
    private static final int HTTPCLIENT_DEFAULT_MAX_PER_ROUTE = 20;
    /**
     * 从连接管理器获取连接的超时时间
     **/
    private static final int HTTPCLIENT_CONNECTION_REQUEST_TIMEOUT = 1000;
    /**
     * 建立连接的超时时间
     **/
    private static final int HTTPCLIENT_CONNECT_TIMEOUT = 5000;
    /**
     * 请求获取数据的超时时间
     **/
    private static final int HTTPCLIENT_SOCKET_TIMEOUT = 300000;
    /**
     * 请求重试次数
     **/
    private static final int HTTPCLIENT_RETRY_COUNT = 3;

    // 连接管理器
    private static PoolingHttpClientConnectionManager connectionManager;
    // 请求配置
    private static RequestConfig requestConfig;
    // http客户端
    private static CloseableHttpClient httpClient;

    // 请求重试机制
    private static HttpRequestRetryHandler requestRetryHandler = (exception, executionCount, context) -> {
        if (executionCount > HTTPCLIENT_RETRY_COUNT) {
            // Do not retry if over 3 retry count
            return false;
        }
        if (exception instanceof InterruptedIOException) {
            // Timeout
            return false;
        }
        if (exception instanceof UnknownHostException) {
            // Unknown host
            return false;
        }
        if (exception instanceof ConnectTimeoutException) {
            // Connection refused
            return false;
        }
        if (exception instanceof SSLException) {
            // SSL handshake exception
            return false;
        }
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        HttpRequest request = clientContext.getRequest();
        return !(request instanceof HttpEntityEnclosingRequest);
    };

    static {
        // 创建http连接管理器，使用连接池管理http连接
        connectionManager = new PoolingHttpClientConnectionManager();
        // 设置最大连接数
        connectionManager.setMaxTotal(HTTPCLIENT_MAX_TOTAL);
        // 设置每个路由的最大连接数
        connectionManager.setDefaultMaxPerRoute(HTTPCLIENT_DEFAULT_MAX_PER_ROUTE);
        // 创建请求配置
        requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(HTTPCLIENT_CONNECTION_REQUEST_TIMEOUT)
                .setConnectTimeout(HTTPCLIENT_CONNECT_TIMEOUT)
                .setSocketTimeout(HTTPCLIENT_SOCKET_TIMEOUT)
                .build();
        // 创建http客户端构建器
        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(requestRetryHandler)   // 默认重试3次
                .build();
    }

    private HttpUtil() {
        // no constructor function
    }

    /**
     * @return
     * @方法名：getHttpClient
     * @作者：kevin[wangqi2017@xinhua.org]
     * @时间：2017/7/10 19:49
     * @描述：获取HttpClient
     */
    private static CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * GET请求，针对文本
     * @param url
     * @return
     */
    public static String get(String url) {
        log.info(GET_START);
        log.info(GET, url);
        if (StringUtil.isEmpty(url)) {
            log.error(GET_URL_ILLEGAL);
            return null;
        }

        CloseableHttpClient httpClient = getHttpClient();
        HttpGet httpGet =new HttpGet(url);
        CloseableHttpResponse response = null;
        String respContent = null;
        try {
            // 执行GET请求
            response = httpClient.execute(httpGet);
            // 获取响应内容
            respContent = getRespContent(response, "GET");
        } catch (IOException e) {
            log.error(GET_ERROR, e);
        } finally {
           httpGet.releaseConnection();
        }

        log.info(GET_END);
        return respContent;
    }

    /**
     * 获取响应，针对文本
     * @param response
     * @param method
     * @return
     * @throws IOException
     */
    private static String getRespContent(HttpResponse response, String method) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        String reasonPhrase = statusLine.getReasonPhrase();
        String respContent = null;

        if (statusCode == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            respContent = EntityUtils.toString(entity);
        } else {
            log.error("{}: statusCode[{}], desc[{}]", method, statusCode, reasonPhrase);
        }

        return respContent;
    }

    /**
     * GET请求，针对多媒体，请求成功后，会直接从对方服务器下载多媒体文件到本地磁盘
     * @param url
     * @param des
     * @return
     */
    public static void get(String url, String des) {
        log.info(GET_START);
        log.info(GET, url);
        if (StringUtil.isEmpty(url)) {
            log.error(GET_URL_ILLEGAL);
            return;
        }

        CloseableHttpClient httpClient = getHttpClient();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            // 执行GET请求
            response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            String reasonPhrase = statusLine.getReasonPhrase();

            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // 写文件到本地磁盘
                    InputStream in = entity.getContent();
                    FileUtil.write(in, des, true);
                } else {
                    log.error("GET: statusCode[{}], desc[{}]", statusCode, reasonPhrase);
                }
            }
        } catch (IOException e) {
            log.error(GET_ERROR, e);
        } finally {
            httpGet.releaseConnection();
        }

        log.info(GET_END);
    }

    /**
     * POST请求，请求参数为json格式
     * @param url
     * @param jsonStr
     * @return
     */
    public static String post(String url, String jsonStr) {
        log.info(POST_START);
        log.info(POST, url);
        if (StringUtil.isEmpty(url)) {
            log.error(POST_URL_ILLEGAL);
            return null;
        }
        log.info(REQ, jsonStr);


        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        String respContent = null;
        try {
            // 执行POST请求
            StringEntity entity = new StringEntity(jsonStr, Charset.forName(UTF8));
            entity.setContentType("application/json; charset=UTF-8");
            entity.setContentEncoding(UTF8);
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            // 获取响应内容
            respContent = getRespContent(response, "POST");
        } catch (IOException e) {
            log.error(POST_ERROR, e);
        } finally {
            httpPost.releaseConnection();
        }

        log.info(POST_END);
        return respContent;
    }

    /**
     * POST请求，请求参数为表单
     * @param url
     * @param params
     * @return
     */
    public static String post(String url, Map<String, String> params) {
        log.info(POST_START);
        log.info(POST, url);
        if (StringUtil.isEmpty(url)) {
            log.error(POST_URL_ILLEGAL);
            return null;
        }
        log.info(REQ, params);


        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        String respContent = null;
        try {
            // 设置POST请求
            List<NameValuePair> pairs = new ArrayList<>();
            for (Map.Entry<String, String> param : params.entrySet()) {
                pairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, Charset.forName(UTF8));
            entity.setContentType("application/x-www-form-urlencoded");
            entity.setContentEncoding(UTF8);
            httpPost.setEntity(entity);
            // 执行POST请求
            response = httpClient.execute(httpPost);
            // 获取响应内容
            respContent = getRespContent(response, "POST");
        } catch (IOException e) {
            log.error(POST_ERROR, e);
        } finally {
            httpPost.releaseConnection();
        }

        log.info(POST_END);
        return respContent;
    }

    /**
     * POST请求，请求参数为表单
     * @param url
     * @param params
     * @return
     */
    public static String post(String url, Map<String, String> headers, Map<String, String> params) {
        log.info(POST_START);
        log.info(POST, url);
        if (StringUtil.isEmpty(url)) {
            log.error(POST_URL_ILLEGAL);
            return null;
        }
        log.info(REQ, params);


        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        String respContent = null;
        try {
            // 设置POST请求头
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpPost.addHeader(header.getKey(), header.getValue());
            }
            // 设置POST请求体
            List<NameValuePair> pairs = new ArrayList<>();
            for (Map.Entry<String, String> param : params.entrySet()) {
                pairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, Charset.forName(UTF8));
            entity.setContentType("application/x-www-form-urlencoded");
            entity.setContentEncoding(UTF8);
            httpPost.setEntity(entity);
            // 执行POST请求
            response = httpClient.execute(httpPost);
            // 获取响应内容
            respContent = getRespContent(response, "POST");
        } catch (IOException e) {
            log.error(POST_ERROR, e);
        } finally {
            httpPost.releaseConnection();
        }

        log.info(POST_END);
        return respContent;
    }

    /**
     * POST请求，请求参数为Json格式
     * @param url
     * @param jsonStr
     * @return 响应状态码
     */
    public static int postAndGetStatusCode(String url, String jsonStr) {
        log.info(POST_START);
        log.info(POST, url);
        if (StringUtil.isEmpty(url)) {
            log.error(POST_URL_ILLEGAL);
            return 400;
        }
        log.info(REQ, jsonStr);


        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        int respStatusCode = 200;
        try {
            // 执行POST请求
            StringEntity entity = new StringEntity(jsonStr, Charset.forName(UTF8));
            entity.setContentType("application/json; charset=UTF-8");
            entity.setContentEncoding(UTF8);
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            // 获取响应内容
            respStatusCode = getRespStatusCode(response);
        } catch (IOException e) {
            log.error(POST_ERROR, e);
        } finally {
            httpPost.releaseConnection();
        }

        log.info(POST_END);
        return respStatusCode;
    }

    /**
     * 获取响应状态码
     * @param response
     * @return
     * @throws IOException
     */
    private static int getRespStatusCode(HttpResponse response) {
        StatusLine statusLine = response.getStatusLine();
        return statusLine.getStatusCode();
    }
}
