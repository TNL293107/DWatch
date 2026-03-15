package servlet;

import dao.OrderDAO;
import model.Order;
import model.OrderDetail;
import model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/orders")
public class OrderHistoryServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = getUser(req);
        String detailParam = req.getParameter("id");

        if (detailParam != null && !detailParam.trim().isEmpty()) {
            Integer orderID = parseInt(detailParam);
            if (orderID == null || orderID <= 0) {
                req.setAttribute("error", "Mã đơn hàng không hợp lệ.");
                req.setAttribute("orders", orderDAO.getOrdersByEmail(user.getEmail()));
                req.getRequestDispatcher("/pages/orderHistory.jsp").forward(req, resp);
                return;
            }

            Order order = orderDAO.getOrderByID(orderID);
            if (order == null || !sameEmail(order.getEmail(), user.getEmail())) {
                req.setAttribute("error", "Không tìm thấy đơn hàng.");
                req.setAttribute("orders", orderDAO.getOrdersByEmail(user.getEmail()));
                req.getRequestDispatcher("/pages/orderHistory.jsp").forward(req, resp);
                return;
            }

            List<OrderDetail> details = orderDAO.getOrderDetails(orderID);
            req.setAttribute("order", order);
            req.setAttribute("details", details);
            req.getRequestDispatcher("/pages/orderDetail.jsp").forward(req, resp);
        } else {
            List<Order> orders = orderDAO.getOrdersByEmail(user.getEmail());
            req.setAttribute("orders", orders);
            req.getRequestDispatcher("/pages/orderHistory.jsp").forward(req, resp);
        }
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("loggedUser") != null;
    }

    private User getUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("loggedUser");
    }

    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private boolean sameEmail(String a, String b) {
        if (a == null || b == null) return false;
        return a.trim().equalsIgnoreCase(b.trim());
    }
}
