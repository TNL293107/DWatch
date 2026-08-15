package servlet;

import util.CsrfUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * CsrfFilter — chặn Cross-Site Request Forgery bằng synchronizer token.
 *
 * <p>Request an toàn (GET/HEAD/OPTIONS/TRACE) chỉ đảm bảo session đã có token để JSP
 * render vào form. Request làm thay đổi dữ liệu (POST/PUT/PATCH/DELETE) bắt buộc phải
 * kèm token khớp với session, nếu không sẽ bị trả về 403.
 */
@WebFilter(urlPatterns = "/*")
public class CsrfFilter implements Filter {

    private static final Set<String> SAFE_METHODS =
            Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    /** Tài nguyên tĩnh không cần token và cũng không nên làm phát sinh session. */
    private static final Set<String> STATIC_PREFIXES =
            Set.of("/css/", "/js/", "/images/");

    private static final String ENCODING = "UTF-8";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Phải set encoding trước lần đọc parameter đầu tiên. Thứ tự giữa các @WebFilter
        // không được đảm bảo nên không thể dựa vào CharsetFilter chạy trước.
        req.setCharacterEncoding(ENCODING);

        if (isStaticResource(req)) {
            chain.doFilter(request, response);
            return;
        }

        if (SAFE_METHODS.contains(req.getMethod())) {
            CsrfUtil.getOrCreateToken(req.getSession(true));
            chain.doFilter(request, response);
            return;
        }

        if (!CsrfUtil.isValid(req)) {
            rejectRequest(resp);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isStaticResource(HttpServletRequest req) {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        return STATIC_PREFIXES.stream().anyMatch(path::startsWith);
    }

    private void rejectRequest(HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding(ENCODING);
        resp.setContentType("text/html; charset=UTF-8");
        resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                "Phiên làm việc đã hết hạn hoặc yêu cầu không hợp lệ. Vui lòng tải lại trang và thử lại.");
    }

    @Override
    public void init(FilterConfig cfg) {
    }

    @Override
    public void destroy() {
    }
}
