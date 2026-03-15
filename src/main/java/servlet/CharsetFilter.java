package servlet;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * CharsetFilter — ensures all requests and responses use UTF-8.
 * Essential for Vietnamese text support.
 */
@WebFilter("/*")
public class CharsetFilter implements Filter {

    private String encoding = "UTF-8";

    @Override
    public void init(FilterConfig cfg) {
        String enc = cfg.getInitParameter("encoding");
        if (enc != null && !enc.isEmpty()) encoding = enc;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        req.setCharacterEncoding(encoding);
        resp.setCharacterEncoding(encoding);
        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {}
}
