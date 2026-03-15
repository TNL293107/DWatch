package servlet;

import dao.UserDAO;
import model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = {"/login", "/register", "/logout", "/profile"})
public class UserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

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
                req.getSession().removeAttribute("loggedUser");
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
            case "/profile":  doProfile(req, resp);  break;
        }
    }

    private void doLogin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String email    = req.getParameter("email");
        String password = req.getParameter("password");
        User user = userDAO.login(email, password);
        if (user != null) {
            req.getSession().setAttribute("loggedUser", user);
            String redirect = req.getParameter("redirect");
            resp.sendRedirect(redirect != null && !redirect.isEmpty()
                ? redirect : req.getContextPath() + "/home");
        } else {
            req.setAttribute("error", "Email hoặc mật khẩu không đúng.");
            req.getRequestDispatcher("/pages/login.jsp").forward(req, resp);
        }
    }

    private void doRegister(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String fullName  = req.getParameter("fullName");
        String email     = req.getParameter("email");
        String password  = req.getParameter("password");
        String password2 = req.getParameter("password2");
        String phone     = req.getParameter("phone");

        if (!password.equals(password2)) {
            req.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            req.getRequestDispatcher("/pages/register.jsp").forward(req, resp);
            return;
        }
        if (userDAO.emailExists(email)) {
            req.setAttribute("error", "Email này đã được đăng ký.");
            req.getRequestDispatcher("/pages/register.jsp").forward(req, resp);
            return;
        }
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);

        int id = userDAO.register(user);
        if (id > 0) {
            user.setUserID(id);
            req.getSession().setAttribute("loggedUser", user);
            String redirect = req.getParameter("redirect");
            resp.sendRedirect(redirect != null && !redirect.isEmpty()
                    ? redirect : req.getContextPath() + "/home");
        } else {
            req.setAttribute("error", "Đăng ký thất bại. Vui lòng thử lại.");
            req.getRequestDispatcher("/pages/register.jsp").forward(req, resp);
        }
    }

    private void doProfile(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        User loggedUser = (User) req.getSession().getAttribute("loggedUser");
        String action = req.getParameter("action");

        if ("changePassword".equals(action)) {
            String oldPass = req.getParameter("oldPassword");
            String newPass = req.getParameter("newPassword");
            String newPass2 = req.getParameter("newPassword2");
            if (!newPass.equals(newPass2)) {
                req.setAttribute("error", "Mật khẩu mới không khớp.");
            } else if (userDAO.changePassword(loggedUser.getUserID(), oldPass, newPass)) {
                req.setAttribute("success", "Đổi mật khẩu thành công.");
            } else {
                req.setAttribute("error", "Mật khẩu cũ không đúng.");
            }
        } else {
            loggedUser.setFullName(req.getParameter("fullName"));
            loggedUser.setPhone(req.getParameter("phone"));
            loggedUser.setAddress(req.getParameter("address"));
            if (userDAO.updateProfile(loggedUser)) {
                req.getSession().setAttribute("loggedUser", loggedUser);
                req.setAttribute("success", "Cập nhật thông tin thành công.");
            } else {
                req.setAttribute("error", "Cập nhật thất bại.");
            }
        }
        req.getRequestDispatcher("/pages/profile.jsp").forward(req, resp);
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        return req.getSession(false) != null &&
               req.getSession().getAttribute("loggedUser") != null;
    }
}
