package servlet;

import dao.WishlistDAO;
import model.Product;
import model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/wishlist")
public class WishlistServlet extends HttpServlet {

    private final WishlistDAO wishlistDAO = new WishlistDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" +
                req.getContextPath() + "/wishlist");
            return;
        }
        User user = getUser(req);
        List<Product> wishlist = wishlistDAO.getByUser(user.getUserID());
        req.setAttribute("wishlist", wishlist);
        req.getRequestDispatcher("/pages/wishlist.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setCharacterEncoding("UTF-8");
        User user      = getUser(req);
        Integer productID  = parseInt(req.getParameter("productID"));
        if (productID == null || productID <= 0) {
            resp.sendRedirect(req.getContextPath() + "/wishlist");
            return;
        }
        String action  = req.getParameter("action");
        String referer = req.getHeader("Referer");

        if ("remove".equals(action)) {
            wishlistDAO.remove(user.getUserID(), productID);
        } else {
            wishlistDAO.add(user.getUserID(), productID);
        }
        resp.sendRedirect(referer != null ? referer : req.getContextPath() + "/wishlist");
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        return req.getSession(false) != null &&
               req.getSession().getAttribute("loggedUser") != null;
    }
    private User getUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("loggedUser");
    }

    private Integer parseInt(String value) {
        if (value == null) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
