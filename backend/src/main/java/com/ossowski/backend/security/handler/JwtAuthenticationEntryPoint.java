package com.ossowski.backend.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.ossowski.backend.security.handler.SecurityAttributes.JWT_ERROR_MSG;
import static com.ossowski.backend.security.handler.SecurityAttributes.JWT_STATUS_CODE;

//for someone who touched secured endopint without beeing logged
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper om;
    private final boolean devMode;

    public JwtAuthenticationEntryPoint(ObjectMapper om,
                                       @Value("${app.debug-auth-errors:false}")  boolean devMode) {
        this.om = om;
        this.devMode = devMode;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        //internal error message set up in JwtAuthenticationFilter
        String internal_error_msg = (String) request.getAttribute(JWT_ERROR_MSG);
        if(internal_error_msg == null) internal_error_msg = "unauthorized";


        Integer suggestedStatus = (Integer) request.getAttribute(JWT_STATUS_CODE);
        if (suggestedStatus == null) suggestedStatus = HttpServletResponse.SC_UNAUTHORIZED;

        response.setStatus(suggestedStatus);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");


        if(devMode){
            response.setHeader(
                    "WWW-Authenticate",
                    "Bearer error=\"" + internal_error_msg + "\", error_description=\"" + humanize(internal_error_msg) + "\""
            );
        }else {
            response.setHeader("WWW-Authenticate", "Bearer");
        }

        // log details internal. Only for dev

        Map<String, Object> body = devMode ? Map.of(
                "status", 401,
                "error", "unauthorized",
                "detail", internal_error_msg,
                "path", request.getRequestURI(),
                "timestamp", java.time.Instant.now().toString()
                )

                : Map.of(
                        "status", 401,
                "error", "unauthorized",
                "path", request.getRequestURI(),
                "timestamp", java.time.Instant.now().toString()
        );

        om.writeValue(response.getWriter(), body);
    }
    private String humanize(String code) {
        return switch (code) {
            case "malformed_jwt" -> "Token structure is malformed";
            case "token_expired" -> "Token has expired";
            case "invalid_signature" -> "Token signature is invalid";
            case "unsupported_jwt" -> "Token algorithm/type is unsupported";
            case "empty_or_null_token" -> "Token is empty or missing";
            case "jwt_invalid" -> "Token failed validation";
            case "unauthorized" -> "Unauthorized";
            default -> "Unauthorized";
        };

    }
}
