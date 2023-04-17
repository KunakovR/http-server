package ru.netology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private String method;
    private String path;
    private Map<String, String> headers;
    private String body;
    private boolean close;

    public Request(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        headers = new HashMap<>();
        parseRequest(reader);
    }

    private void parseRequest(BufferedReader reader) throws IOException {
        // Чтение Request Line
        String requestLine = reader.readLine();
        String[] requestLineParts = requestLine.split(" ");
        if (requestLineParts.length != 3) {
            close = true;
            reader.close();
            return;
        }
        if (requestLineParts.length == 3) {
            method = requestLineParts[0];
            path = requestLineParts[1];
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
}


