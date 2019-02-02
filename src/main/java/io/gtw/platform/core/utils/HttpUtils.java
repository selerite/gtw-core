package io.gtw.platform.core.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);
    private static final int CONNECTION_TIMEOUT = 35000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 35000;
    private static final int SOCKET_TIMEOUT = 60000;
    private static final int MAX_TOTAL = 200;
    private static final int DEFAULT_MAX_PER_ROUTE = 20;
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/201001";

    public static CloseableHttpClient createHttpClient() {
        PoolingHttpClientConnectionManager phccm = null;
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
            // 配置同时支持 HTTP 和 HTTPS
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register(
                    "http", PlainConnectionSocketFactory.getSocketFactory()).register(
                    "https", sslsf).build();
            phccm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            // 将最大连接数增加到200，实际项目最好从配置文件中读取这个值
            phccm.setMaxTotal(MAX_TOTAL);
            // 设置最大路由
            phccm.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
            return HttpClients.custom().setConnectionManager(phccm).build();
        } catch (Exception e) {
            LOGGER.error("创建HttpClient时发生异常,返回createDefault(). ex={}", e);
        }
        return HttpClients.createDefault();
    }

    public static String doGet(CloseableHttpClient httpClient, RequestDataItem requestDataItem, String encoding) {
        String responseResult = "";
        HttpGet httpGet = new HttpGet(requestDataItem.getUrl());
        CloseableHttpResponse httpResponse = null;
        try {
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(CONNECTION_TIMEOUT)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)// 请求超时时间
                    .setSocketTimeout(SOCKET_TIMEOUT)// 数据读取超时时间,指的是连接上一个url，获取response的返回等待时间
                    .build();
            httpGet.setConfig(requestConfig);
            if (requestDataItem.isXmlHttpRequest()) {
                httpGet.addHeader("X-Requested-With", "XMLHttpRequest");
            }
            if (StringUtils.isNotBlank(requestDataItem.getReferer())) {
                httpGet.addHeader("Referer", requestDataItem.getReferer());
            }
            for (Entry<String, String> header : requestDataItem.getHttpHeaderItem().entrySet()) {
                httpGet.addHeader(header.getKey(), header.getValue());
            }
            httpGet.addHeader("User-Agent", USER_AGENT);
            httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            responseResult = EntityUtils.toString(entity, encoding);
            LOGGER.info("doGet执行完成. url={}, status={}", requestDataItem.getUrl(), httpResponse.getStatusLine().getStatusCode());
        } catch (Exception ex) {
            LOGGER.error("doGet执行时发生异常. ex={}", ex);
        } finally {
            httpGet.releaseConnection();
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    LOGGER.error("doGet在关闭CloseableHttpResponse时发生异常. ex={}", e);
                }
            }
        }
        return responseResult;
    }

    public static String doPost(CloseableHttpClient httpClient, RequestDataItem requestDataItem, Map<String, String> paramMap, String rawData, String encoding) {
        CloseableHttpResponse httpResponse = null;
        String resposeResult = null;
        HttpPost httpPost = new HttpPost(requestDataItem.getUrl());
        try {
            // 创建httpPost远程连接实例
            // 配置请求参数实例
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT)// 设置连接主机服务超时时间
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)// 设置连接请求超时时间
                    .setSocketTimeout(SOCKET_TIMEOUT)// 设置读取数据连接超时时间
                    .build();
            // 为httpPost实例设置配置
            httpPost.setConfig(requestConfig);
            // 设置请求头
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            if (requestDataItem.isXmlHttpRequest()) {
                httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
            }
            if (StringUtils.isNotBlank(requestDataItem.getReferer())) {
                httpPost.addHeader("Referer", requestDataItem.getReferer());
            }
            for (Entry<String, String> header : requestDataItem.getHttpHeaderItem().entrySet()) {
                httpPost.addHeader(header.getKey(), header.getValue());
            }
            httpPost.addHeader("User-Agent", USER_AGENT);
            // 封装post请求参数
            if (null != paramMap && paramMap.size() > 0) {
                List<NameValuePair> nvps = new ArrayList<>();
                // 通过map集成entrySet方法获取entity
                Set<Entry<String, String>> entrySet = paramMap.entrySet();
                // 循环遍历，获取迭代器
                for (Entry<String, String> mapEntry : entrySet) {
                    nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue()));
                }
                // 为httpPost设置封装好的请求参数
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));
            }

            if (null != rawData) {
                httpPost.setEntity(new StringEntity(rawData));
            }
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            HttpEntity httpEntity = httpResponse.getEntity();
            LOGGER.info("doPostForm执行完成. url={}, status={}", requestDataItem.getUrl(), httpResponse.getStatusLine().getStatusCode());
            return EntityUtils.toString(httpEntity);
        } catch (IOException ex) {
            LOGGER.error("doPost执行时发生异常. ex={}", ex);
        } finally {
            httpPost.releaseConnection();
            // 关闭资源
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    LOGGER.error("doPost在关闭CloseableHttpResponse时发生异常. ex={}", e);
                }
            }
        }
        return resposeResult;
    }

}


