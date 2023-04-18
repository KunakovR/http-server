package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private String method;
    private String path;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String body;
    private boolean close;

    public Request(InputStream inputStream) throws IOException, URISyntaxException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        headers = new HashMap<>();
        queryParams = new HashMap<>();
        parseRequest(reader);
    }

    private void parseRequest(BufferedReader reader) throws IOException, URISyntaxException {
        // Чтение RequestLine
        String requestLine = reader.readLine();
        String[] requestLineParts = requestLine.split(" ");
        //Check RequestLine
        if (requestLineParts.length != 3) {
            close = true;
            reader.close();
            return;
        }
        //RequestLine method and path retrieve
        if (requestLineParts.length == 3) {
            method = requestLineParts[0];
            path = requestLineParts[1];
            //Разбираем path и query
            int index = path.indexOf('?');
            if (index >= 0) {
                String queryString = path.substring(index + 1);
                path = path.substring(0, index);
                parseQueryParams(queryString);
            }
        }

        // Чтение Headers
        var contentLength = 0;
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.substring("Content-Length:".length()).trim());
            }
            String[] headerParts = line.split(": ");
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }

        // Read body if present
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            reader.read(buffer);
            body = new String(buffer);
        }
    }

    // Геттеры для получения полей Request
    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public boolean getStatus() {
        return close;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getQueryParam(String name) {
        return queryParams.get(name);
    }

    //Парсим Query
    private Map<String, String> parseQueryParams(String queryString) {
        List<NameValuePair> parameters = URLEncodedUtils.parse(queryString, Charset.defaultCharset());
        for (NameValuePair parameter : parameters) {
            queryParams.put(parameter.getName(), parameter.getValue());
        }
        return queryParams;
    }
}


