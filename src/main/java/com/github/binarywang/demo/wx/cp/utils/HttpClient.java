package com.github.binarywang.demo.wx.cp.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.binarywang.demo.wx.cp.component.WxPermanentCode;
import java.io.CharArrayWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wut
 * @since 2022-05-05
 */
public class HttpClient {
    protected final static Logger logger = LoggerFactory.getLogger(WxPermanentCode.class);
    final static ObjectMapper JSONMAPPER = new ObjectMapper();

    /**
     * 三方接口调用工具
     *
     * @param urlStr
     * @param file
     * @return
     */
    public static HttpResult doPostFile(String urlStr, File file) throws IOException{
        HttpURLConnection conn = null;
        try {
            final String newLine = "\r\n";
            final String boundaryPrefix = "--";
            String BOUNDARY = "========7d4a6d158c9";
            URL url = new URL(urlStr);
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);

            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            OutputStream out = new DataOutputStream(conn.getOutputStream());

            StringBuilder sb = new StringBuilder();
            sb.append(boundaryPrefix)
                .append(BOUNDARY)
                .append(newLine);
//            sb.append("Content-Disposition: form-data; name=\"media\";filename=\"wework.txt\"; filelength=6");
            sb.append("Content-Disposition: form-data; name=\"media\";filename=\""+ file.getName() + "\""+ newLine);
            sb.append("Content-Type: application/octet-stream")
                .append(newLine).append(newLine);

            out.write(sb.toString().getBytes());

            DataInputStream in = new DataInputStream(new FileInputStream(file));
            byte[] bufferOut = new byte[1024];
            int bytes = 0;
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }

            out.write(newLine.getBytes());
            in.close();

            byte[] end_data = (newLine + boundaryPrefix + BOUNDARY + boundaryPrefix + newLine).getBytes();
            out.write(end_data);
            out.flush();

            int respCode = conn.getResponseCode();
            String resp = null;
            if (HttpURLConnection.HTTP_OK == respCode) {
                resp = toString(conn.getInputStream(), "UTF-8");
            } else {
                resp = toString(conn.getErrorStream(), "UTF-8");
            }
            return new HttpResult(respCode, resp);

        } finally {
            if (null != conn) {
                conn.disconnect();
            }
        }
    }


    /**
     * get 方式
     *
     * @param url
     * @param paramValues
     * @return
     *
     * @throws IOException
     */
    public static HttpResult httpGet(String url, Map<String, String> paramValues) throws IOException {
        return httpGet(url, null, paramValues, "UTF-8", 5000);
    }

    /**
     * get 方式
     *
     * @param url
     * @param headers
     * @param paramValues
     * @param encoding
     * @param readTimeoutMs
     * @return
     *
     * @throws IOException
     */
    public static HttpResult httpGet(String url, Map<String, String> headers, Map<String, String> paramValues,
        String encoding, long readTimeoutMs) throws IOException {
        String encodedContent = encodingParams(paramValues, encoding);
        url += (null == encodedContent) ? "" : ("?" + encodedContent);

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout((int) readTimeoutMs);
            conn.setReadTimeout((int) readTimeoutMs);
            setHeaders(conn, headers, encoding);

            conn.connect();
            int respCode = conn.getResponseCode();
            String resp = null;

            if (HttpURLConnection.HTTP_OK == respCode) {
                resp = toString(conn.getInputStream(), encoding);
            } else {
                resp = toString(conn.getErrorStream(), encoding);
            }
            return new HttpResult(respCode, resp);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * post 方式
     *
     * @param url
     * @param headers
     * @param paramValues
     * @return
     *
     * @throws IOException
     */
    public static HttpResult httpPost(String url, Map<String, String> headers, Map<String, String> paramValues)
        throws IOException {
        return httpPost(url, headers, paramValues, "UTF-8", 5000);
    }

    /**
     * post 方式
     *
     * @param url
     * @param paramValues
     * @return
     *
     * @throws IOException
     */
    public static HttpResult httpPost(String url, Map<String, String> paramValues) throws IOException {
        return httpPost(url, null, paramValues, "UTF-8", 5000);
    }

    /**
     * post 方式(参数使用json方式)
     *
     * @param url
     * @param paramValues
     * @return
     *
     * @throws IOException
     */
    public static HttpResult httpPostJson(String url, Map<String, ?> paramValues) throws IOException {
        String encodedContent = "";
        if (null != paramValues) {
            encodedContent = JSONMAPPER.writeValueAsString(paramValues);
        }
        Map<String, String> header = Collections.singletonMap("Content-Type", "application/json");
        return httpPost(url, header, "UTF-8", 5000, encodedContent);
    }

    /**
     * post 方式
     *
     * @param url
     * @param headers
     * @param paramValues
     * @param encoding
     * @param readTimeoutMs
     * @return
     *
     * @throws IOException
     */
    public static HttpResult httpPost(String url, Map<String, String> headers, Map<String, String> paramValues,
        String encoding, long readTimeoutMs) throws IOException {
        String encodedContent = encodingParams(paramValues, encoding);
        return httpPost(url, headers, encoding, (int) readTimeoutMs, encodedContent);
    }

    public static HttpResult httpPost(String url, Map<String, String> headers, String encoding, int readTimeoutMs,
        String encodedContent) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(readTimeoutMs);
            conn.setReadTimeout(readTimeoutMs);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            setHeaders(conn, headers, encoding);

            conn.getOutputStream()
                .write(encodedContent.getBytes(encoding));

            int respCode = conn.getResponseCode();
            String resp = null;
            if (HttpURLConnection.HTTP_OK == respCode) {
                resp = toString(conn.getInputStream(), encoding);
            } else {
                resp = toString(conn.getErrorStream(), encoding);
            }
            return new HttpResult(respCode, resp);
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
        }
    }

    private static String encodingParams(Map<String, String> paramValues, String encoding)
        throws UnsupportedEncodingException {
        if (null == paramValues) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : paramValues.entrySet()) {
            if (0 < sb.length()) {
                sb.append("&");
            }
            sb.append(entry.getKey())
                .append("=");
            sb.append(URLEncoder.encode(entry.getValue(), encoding));
        }
        return sb.toString();
    }

    private static void setHeaders(HttpURLConnection conn, Map<String, String> headers, String encoding) {
        if (null != headers) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + encoding);
    }

    private static String toString(InputStream input, String encoding) throws IOException {
        return (null == encoding) ? toString(new InputStreamReader(input, "UTF-8"))
            : toString(new InputStreamReader(input, encoding));
    }

    private static String toString(Reader reader) throws IOException {
        CharArrayWriter sw = new CharArrayWriter();
        copy(reader, sw);
        return sw.toString();
    }

    private static long copy(Reader input, Writer output) throws IOException {
        char[] buffer = new char[1 << 12];
        long count = 0;
        for (int n = 0; (n = input.read(buffer)) >= 0; ) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * http 结果集合的简单封装
     *
     * @author chenzhaoju
     */
    public static class HttpResult {

        /**
         * http响应码
         */
        public final int code;
        /**
         * http响应内容
         */
        public final String content;

        public HttpResult(int code, String content) {
            this.code = code;
            this.content = content;
        }

        @Override
        public String toString() {
            return "HttpResult{" + "code=" + code + ", content='" + content + '\'' + '}';
        }
    }
}
