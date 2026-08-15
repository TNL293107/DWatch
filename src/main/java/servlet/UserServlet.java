package servlet;

import model.User;
import service.AuthService;
import service.ServiceResult;
import service.UserService;
import util.CsrfUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * UserServlet — điều hướng cho đăng nhập / đăng ký / đăng xuất / trang cá nhân.
 *
 * <p>Servlet chỉ đọc tham số request và chọn view; toàn bộ nghiệp vụ xác thực
 * nằm ở {@link AuthService}.
 */
@WebServlet(urlPatterns = {"/login", "/register", "/logout", "/profile"})
public class UserServlet extends HttpServlet {

    private final AuthService authService = new AuthService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        switch (path) {
            case "/login":
                req.getRequestDispatcher("/pages/login.jsp").forward(req, resp);
                break;
            case "/register":
                req.getRequestDispatcher("/pages/register.jsp").forward(req, resp);
                break;
            case "/logout":
                doLogout(req);
                resp.sendRedirect(req.getContextPath() + "/home");
                break;
            case "/profile":
                if (!isLoggedIn(req)) {
                    resp.sendRedirect(req.getContextPath() + "/login");
                    return;
                }
                req.getRequestDispatcher("/pages/profile.jsp").forward(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getServletPath();
        switch (path) {
            case "/login":    doLogin(req, resp);    break;
            case "/register": doRegister(req, resp); break;
            case "/profile":
                if (!isLoggedIn(req)) {
                    resp.sendRedirect(req.getContextPath() + "/login");
                    return;
                }
                doProfile(req, resp);
                break;
        }
    }

    private void doLogin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        ServiceResult result = authService.login(
                req.getParameter("email"),
                req.getParameter("password"));

        if (!result.success()) {
            req.setAttribute("error", result.errorMessage());
            req.getRequestDispatcher("/pages/login.jsp").forward(req, resp);
            return;
        }
        startAuthenticatedSession(req, result.user());
        redirectAfterAuth(req, resp);
    }

    private void doRegister(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        ServiceResult result = authService.register(
                req.getParameter("fullName"),
                req.getParameter("email"),
                req.getParameter("password"),
                req.getParameter("password2"),
                req.getParameter("phone"));

        if (!result.success()) {
            req.setAttribute("error", result.errorMessage());
            req.getRequestDispatcher("/pages/register.jsp").forward(req, resp);
            return;
        }
        startAuthenticatedSession(req, result.user());
        redirectAfterAuth(req, resp);
    }

    private void doProfile(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        User loggedUser = (User) req.getSession().getAttribute("loggedUser");
        if (loggedUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if ("changePassword".equals(req.getParameter("action"))) {
            ServiceResult result = authService.changePassword(
                    loggedUser.getUserID(),
                    req.getParameter("oldPassword"),
                    req.getParameter("newPassword"),
                    req.getParameter("newPassword2"));
            if (result.success()) {
                req.setAttribute("success", "Đổi mật khẩu thành công.");
            } else {
                req.setAttribute("error", result.errorMessage());
            }
        } else {
            updateProfile(req, loggedUser);
        }
        req.getRequestDispatcher("/pages/profile.jsp").forward(req, resp);
    }

    private void updateProfile(HttpServletRequest req, User loggedUser) {
        ServiceResult result = userService.updateProfile(
                loggedUser,
                req.getParameter("fullName"),
                req.getParameter("phone"),
                req.getParameter("address"));
        if (result.success()) {
            req.getSession().setAttribute("loggedUser", result.user());
            req.setAttribute("success", "Cập nhật thông tin thành công.");
        } else {
            req.setAttribute("error", result.errorMessage());
        }
    }

    /** Cấp session mới sau khi xác thực để chống session fixation. */
    private void startAuthenticatedSession(HttpServletRequest req, User user) {
        req.getSession(true);
        req.changeSessionId();
        HttpSession session = req.getSession();
        session.setAttribute("loggedUser", user);
        CsrfUtil.rotateToken(session);
    }

    private void doLogout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute("loggedUser");
        req.changeSessionId();
        CsrfUtil.rotateToken(session);
    }

    private void redirectAfterAuth(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.sendRedirect(resolveSafeRedirect(req, req.getParameter("redirect"),
                req.getContextPath() + "/home"));
    }

    /**
     * Chỉ nhận đường dẫn nội bộ nằm trong context path — chặn open redirect sang site khác
     * và chặn CRLF injection vào header Location.
     */
    private String resolveSafeRedirect(HttpServletRequest req, String redirect, String fallback) {
        if (redirect == null) return fallback;
        String target = redirect.trim();
        if (target.isEmpty()) return fallback;
        if (target.contains("\r") || target.contains("\n")) return fallback;
        if (target.startsWith("http://") || target.startsWith("https://") || target.startsWith("//")) {
            return fallback;
        }
        if (!target.startsWith("/")) return fallback;

        String ctx = req.getContextPath();
        if (ctx == null || ctx.isEmpty()) return target;
        if (target.equals(ctx) || target.startsWith(ctx + "/")) return target;
        return fallback;
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("loggedUser") != null;
    }
}
