package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        final var server = new Server();

        server.addHandler("GET", "/index.html", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                final var filePath = Path.of(".", "public", request.getPath());
                final var mimeType = Files.probeContentType(filePath);
                final var length = Files.size(filePath);
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length2: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, responseStream);
                responseStream.flush();
            }
        });
        server.addHandler("POST", "/index.html", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                responseStream.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                responseStream.flush();
            }
        });
        server.listen(9999);
    }
}


