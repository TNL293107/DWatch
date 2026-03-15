package servlet;

import dao.OrderDAO;
import model.Order;
import model.OrderDetail;
import util.VietQRUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * OrderConfirmServlet — shows order confirmation after successful checkout.
 */
@WebServlet("/orderConfirm")
public class OrderConfirmServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        try {
            int   orderID = Integer.parseInt(idParam);
            Order order   = orderDAO.getOrderByID(orderID);
            if (order == null) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
            List<OrderDetail> details = orderDAO.getOrderDetails(orderID);
            req.setAttribute("order",   order);
            req.setAttribute("details", details);
            if (Order.PAYMENT_QR.equals(order.getPaymentMethod())) {
                long amountVND = Math.round(order.getTotalAmount());
                String addInfo = "DWatch DH" + orderID;
                String qrUrl = VietQRUtil.getPaymentURL(getServletContext(), amountVND, addInfo);
                req.setAttribute("vietqrUrl", qrUrl);
            }
            req.getRequestDispatcher("/pages/orderConfirm.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/home");
        }
    }
}
