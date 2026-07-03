package com.dwatch.common;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Rejects POST requests without a valid per-session CSRF token.
 * Registered explicitly in web.xml (after CharsetFilter) so filter order is
 * deterministic — CharsetFilter must set UTF-8 before any getParameter() call
 * reads the POST body, otherwise Vietnamese form input gets mis-decoded.
 */
public class CsrfFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(true);
        CsrfUtil.getToken(session); // ensure a token exists so JSPs can render it via ${sessionScope.csrfToken}

        if ("POST".equalsIgnoreCase(req.getMethod()) && !CsrfUtil.isValid(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
