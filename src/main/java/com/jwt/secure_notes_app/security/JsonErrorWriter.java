package com.jwt.secure_notes_app.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

final class JsonErrorWriter {
    private JsonErrorWriter() {}

    static void write(HttpServletResponse response, int status, String error,
                      String message, String path) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                Instant.now(), status, error, message, path.replace("\\", "\\\\").replace("\"", "\\\"")));
    }
}
