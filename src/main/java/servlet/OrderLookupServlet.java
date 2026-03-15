package servlet;

import dao.OrderDAO;
import model.Order;
import model.OrderDetail;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Tra cứu đơn hàng không cần đăng nhập: nhập Mã đơn + SĐT hoặc Email.
 */
@WebServlet("/orderLookup")
public class OrderLookupServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/pages/orderLookup.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String idStr = req.getParameter("orderId");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        if (idStr == null || idStr.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập mã đơn hàng.");
            req.getRequestDispatcher("/pages/orderLookup.jsp").forward(req, resp);
            return;
        }
        int orderID;
        try {
            orderID = Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Mã đơn hàng không hợp lệ.");
            req.getRequestDispatcher("/pages/orderLookup.jsp").forward(req, resp);
            return;
        }
        if ((phone == null || phone.trim().isEmpty()) && (email == null || email.trim().isEmpty())) {
            req.setAttribute("error", "Vui lòng nhập số điện thoại hoặc email đặt hàng.");
            req.getRequestDispatcher("/pages/orderLookup.jsp").forward(req, resp);
            return;
        }
        Order order = orderDAO.getOrderByID(orderID);
        if (order == null) {
            req.setAttribute("error", "Không tìm thấy đơn hàng với mã #" + orderID + ".");
            req.getRequestDispatcher("/pages/orderLookup.jsp").forward(req, resp);
            return;
        }
        String phoneTrim = phone != null ? phone.trim() : "";
        String emailTrim = email != null ? email.trim() : "";
        boolean match = false;
        if (!phoneTrim.isEmpty() && order.getPhone() != null && order.getPhone().trim().equals(phoneTrim)) match = true;
        if (!emailTrim.isEmpty() && order.getEmail() != null && order.getEmail().trim().equalsIgnoreCase(emailTrim)) match = true;
        if (!match) {
            req.setAttribute("error", "Số điện thoại hoặc email không khớp với đơn hàng. Vui lòng kiểm tra lại.");
            req.getRequestDispatcher("/pages/orderLookup.jsp").forward(req, resp);
            return;
        }
        List<OrderDetail> details = orderDAO.getOrderDetails(orderID);
        req.setAttribute("order", order);
        req.setAttribute("details", details);
        req.getRequestDispatcher("/pages/orderLookupResult.jsp").forward(req, resp);
    }
}
