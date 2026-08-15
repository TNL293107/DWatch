package servlet;

import service.AuthService;
import service.ServiceResult;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ResetPasswordServlet — đặt lại mật khẩu từ link gửi qua email.
 */
@WebServlet("/resetPassword")
public class ResetPasswordServlet extends HttpServlet {

    private static final String VIEW = "/pages/resetPassword.jsp";

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String token = req.getParameter("token");
        if (authService.emailForValidToken(token) == null) {
            req.setAttribute("error",
                    "Link đã hết hạn hoặc không hợp lệ. Vui lòng yêu cầu gửi lại link.");
            req.getRequestDispatcher(VIEW).forward(req, resp);
            return;
        }
        req.setAttribute("token", token);
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String token = req.getParameter("token");

        ServiceResult result = authService.resetPassword(
                token,
                req.getParameter("newPassword"),
                req.getParameter("newPassword2"));

        if (result.success()) {
            resp.sendRedirect(req.getContextPath() + "/login?msg=reset");
            return;
        }
        req.setAttribute("error", result.errorMessage());
        req.setAttribute("token", token);
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }
}
