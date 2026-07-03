package com.dwatch.common;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory sliding-window rate limiter for login/register endpoints, keyed
 * by client IP + path. Single-node only (matches this app's deployment) —
 * not meant to survive a restart or scale across instances.
 */
public class RateLimitFilter implements Filter {

    private static final int MAX_ATTEMPTS = 10;
    private static final long WINDOW_MILLIS = 5 * 60 * 1000L;

    private final Map<String, Window> attemptsByKey = new ConcurrentHashMap<>();

    private static class Window {
        long windowStart = System.currentTimeMillis();
        int count = 0;
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String key = clientIp(req) + ":" + req.getServletPath();
            if (isRateLimited(key)) {
                resp.sendError(429, "Quá nhiều yêu cầu, vui lòng thử lại sau ít phút.");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isRateLimited(String key) {
        long now = System.currentTimeMillis();
        Window w = attemptsByKey.computeIfAbsent(key, k -> new Window());
        synchronized (w) {
            if (now - w.windowStart > WINDOW_MILLIS) {
                w.windowStart = now;
                w.count = 0;
            }
            w.count++;
            return w.count > MAX_ATTEMPTS;
        }
    }

    private String clientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        return req.getRemoteAddr();
    }

    @Override
    public void destroy() {}
}
