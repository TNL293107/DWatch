package com.dwatch.cart;

import com.dwatch.common.EmailUtil;
import com.dwatch.order.Order;
import com.dwatch.order.OrderDAO;
import com.dwatch.order.OrderDetail;
import com.dwatch.product.Product;
import com.dwatch.product.ProductDAO;
import com.dwatch.user.User;
import com.dwatch.voucher.Voucher;
import com.dwatch.voucher.VoucherDAO;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * CartServlet — maps to /cart
 * Requirement 4: manage shopping cart (add / remove / change qty / checkout).
 *
 * Actions (via 'action' param):
 *   add           — add product to cart
 *   remove        — remove a product line from cart
 *   update        — update all quantities from cart form
 *   applyVoucher  — validate and apply a discount code to the cart
 *   removeVoucher — remove the applied discount code
 *   checkout      — save order to DB, clear cart, redirect to confirmation
 *   view          — show cart page (default GET)
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private static final String CART_KEY    = "cart";
    private static final String VOUCHER_KEY = "appliedVoucher";

    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO   orderDAO   = new OrderDAO();
    private final VoucherDAO voucherDAO = new VoucherDAO();

    // ----------------------------------------------------------------
    // GET  → view cart or remove item (action=remove)
    // ----------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            if ("remove".equals(req.getParameter("action"))) {
                doRemove(req, resp);
                return;
            }
            showCart(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    // ----------------------------------------------------------------
    // POST → handle all cart actions
    // ----------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) action = "view";

        try {
            switch (action) {
                case "add":           doAdd(req, resp);           break;
                case "remove":        doRemove(req, resp);        break;
                case "update":        doUpdate(req, resp);        break;
                case "applyVoucher":  doApplyVoucher(req, resp);  break;
                case "removeVoucher": doRemoveVoucher(req, resp); break;
                case "checkout":      doCheckout(req, resp);      break;
                default:              showCart(req, resp);
            }
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    // ----------------------------------------------------------------
    // ADD to cart
    // ----------------------------------------------------------------
    private void doAdd(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        int productID = Integer.parseInt(req.getParameter("productID"));
        int qty       = 1;
        try { qty = Integer.parseInt(req.getParameter("quantity")); }
        catch (Exception ignored) {}

        Map<Integer, CartItem> cart = getCart(req);
        if (cart.containsKey(productID)) {
            cart.get(productID).setQuantity(cart.get(productID).getQuantity() + qty);
        } else {
            Product p = productDAO.getProductByID(productID);
            if (p != null) cart.put(productID, new CartItem(p, qty));
        }
        saveCart(req, cart);
        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    // ----------------------------------------------------------------
    // REMOVE from cart
    // ----------------------------------------------------------------
    private void doRemove(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        int productID = Integer.parseInt(req.getParameter("productID"));
        Map<Integer, CartItem> cart = getCart(req);
        cart.remove(productID);
        saveCart(req, cart);
        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    // ----------------------------------------------------------------
    // UPDATE quantities (from cart form)
    // ----------------------------------------------------------------
    private void doUpdate(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Map<Integer, CartItem> cart = getCart(req);
        String[] productIDs = req.getParameterValues("productID");
        String[] quantities = req.getParameterValues("qty");

        if (productIDs != null && quantities != null && productIDs.length == quantities.length) {
            for (int i = 0; i < productIDs.length; i++) {
                String qtyStr = quantities[i];
                if (qtyStr == null || qtyStr.trim().isEmpty()) continue;
                int pid, q;
                try {
                    pid = Integer.parseInt(productIDs[i].trim());
                    q  = Integer.parseInt(qtyStr.trim());
                } catch (NumberFormatException ignored) { continue; }
                if (q <= 0) {
                    cart.remove(pid);
                } else if (cart.containsKey(pid)) {
                    cart.get(pid).setQuantity(q);
                }
            }
        }
        saveCart(req, cart);
        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    // ----------------------------------------------------------------
    // APPLY / REMOVE voucher
    // ----------------------------------------------------------------
    private void doApplyVoucher(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String code = req.getParameter("voucherCode");
        double subtotal = cartSubtotal(getCart(req));
        Voucher voucher = code != null ? voucherDAO.findValidByCode(code.trim()) : null;

        if (voucher == null) {
            req.setAttribute("error", "Mã giảm giá không hợp lệ hoặc đã hết hạn.");
        } else if (voucher.getMinOrderAmount() != null && subtotal < voucher.getMinOrderAmount()) {
            req.setAttribute("error", String.format(
                "Đơn hàng cần tối thiểu %,.0f₫ để áp dụng mã này.", voucher.getMinOrderAmount()));
        } else {
            req.getSession().setAttribute(VOUCHER_KEY, voucher);
            showCart(req, resp);
            return;
        }
        showCart(req, resp);
    }

    private void doRemoveVoucher(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.getSession().removeAttribute(VOUCHER_KEY);
        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private double cartSubtotal(Map<Integer, CartItem> cart) {
        double subtotal = 0;
        for (CartItem item : cart.values()) subtotal += item.getSubtotal();
        return subtotal;
    }

    // ----------------------------------------------------------------
    // CHECKOUT  — requires login; save to DB then clear cart
    // ----------------------------------------------------------------
    private void doCheckout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        User loggedUser = (User) req.getSession().getAttribute("loggedUser");
        if (loggedUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" +
                    java.net.URLEncoder.encode(req.getContextPath() + "/cart", "UTF-8"));
            return;
        }

        Map<Integer, CartItem> cart = getCart(req);
        if (cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        String paymentMethod = req.getParameter("paymentMethod");
        if (paymentMethod == null || (!Order.PAYMENT_COD.equals(paymentMethod) && !Order.PAYMENT_QR.equals(paymentMethod)))
            paymentMethod = Order.PAYMENT_COD;

        // Build order from logged user (form can override)
        Order order = new Order();
        order.setFullName(req.getParameter("fullName") != null ? req.getParameter("fullName").trim() : loggedUser.getFullName());
        order.setEmail(req.getParameter("email") != null ? req.getParameter("email").trim() : loggedUser.getEmail());
        order.setPhone(req.getParameter("phone") != null ? req.getParameter("phone").trim() : loggedUser.getPhone());
        order.setAddress(req.getParameter("address") != null ? req.getParameter("address").trim() : loggedUser.getAddress());
        order.setNote(req.getParameter("note"));
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus(Order.PAYMENT_STATUS_UNPAID); // Mới tạo đơn = Chưa thanh toán
        order.setUserID(loggedUser.getUserID());

        // Calculate subtotal & build details + HTML cho email
        double subtotal = 0;
        List<OrderDetail> details = new ArrayList<>();
        StringBuilder itemsHTML = new StringBuilder();
        for (CartItem item : cart.values()) {
            subtotal += item.getSubtotal();
            details.add(new OrderDetail(0,
                item.getProduct().getProductID(),
                item.getQuantity(),
                item.getProduct().getPrice()
            ));
            itemsHTML.append(String.format(
                "<tr><td style='padding:8px;border-bottom:1px solid #eee'>%s x%d</td>" +
                "<td style='padding:8px;border-bottom:1px solid #eee;text-align:right'>%,.0f&#8363;</td></tr>",
                item.getProduct().getProductName(),
                item.getQuantity(),
                item.getSubtotal()
            ));
        }

        // Apply voucher (nếu có) trước khi lưu đơn
        Voucher voucher = (Voucher) req.getSession().getAttribute(VOUCHER_KEY);
        double discount = voucher != null ? voucherDAO.computeDiscount(voucher, subtotal) : 0;
        double total = subtotal - discount;
        if (voucher != null && discount > 0) {
            order.setVoucherCode(voucher.getCode());
            order.setDiscountAmount(discount);
            itemsHTML.append(String.format(
                "<tr><td style='padding:8px;color:#c9a84c'>Giảm giá (%s)</td>" +
                "<td style='padding:8px;text-align:right;color:#c9a84c'>-%,.0f&#8363;</td></tr>",
                voucher.getCode(), discount
            ));
        }
        order.setTotalAmount(total);
        order.setDetails(details);

        int orderID = orderDAO.saveOrder(order);
        if (orderID > 0) {
            if (voucher != null && discount > 0) {
                voucherDAO.incrementUsage(voucher.getVoucherID());
                req.getSession().removeAttribute(VOUCHER_KEY);
            }
            // Gửi email xác nhận nếu khách có nhập email
            String customerEmail = order.getEmail();
            if (customerEmail != null && !customerEmail.trim().isEmpty()) {
                String paymentStatus = order.getPaymentStatus() != null ? order.getPaymentStatus() : Order.PAYMENT_STATUS_UNPAID;
                EmailUtil.sendOrderConfirmation(
                    customerEmail.trim(),
                    order.getFullName(),
                    orderID,
                    total,
                    order.getAddress(),
                    itemsHTML.toString(),
                    paymentStatus
                );
            }
            // Clear cart from session
            req.getSession().removeAttribute(CART_KEY);
            resp.sendRedirect(req.getContextPath() + "/orderConfirm?id=" + orderID);
        } else {
            req.setAttribute("error", "Đặt hàng thất bại. Vui lòng thử lại.");
            showCart(req, resp);
        }
    }

    // ----------------------------------------------------------------
    // Show cart page
    // ----------------------------------------------------------------
    private void showCart(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Map<Integer, CartItem> cart = getCart(req);
        req.setAttribute("cart", cart);
        req.setAttribute("loggedUser", req.getSession().getAttribute("loggedUser"));

        Voucher voucher = (Voucher) req.getSession().getAttribute(VOUCHER_KEY);
        if (voucher != null) {
            double discount = voucherDAO.computeDiscount(voucher, cartSubtotal(cart));
            req.setAttribute("appliedVoucher", voucher);
            req.setAttribute("discountAmount", discount);
        }
        req.getRequestDispatcher("/pages/cart.jsp").forward(req, resp);
    }

    // ----------------------------------------------------------------
    // Session helpers
    // ----------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private Map<Integer, CartItem> getCart(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        Map<Integer, CartItem> cart =
            (Map<Integer, CartItem>) session.getAttribute(CART_KEY);
        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute(CART_KEY, cart);
        }
        return cart;
    }

    private void saveCart(HttpServletRequest req, Map<Integer, CartItem> cart) {
        req.getSession(true).setAttribute(CART_KEY, cart);
    }
}
