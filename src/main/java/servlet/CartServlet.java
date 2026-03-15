package servlet;

import dao.OrderDAO;
import dao.ProductDAO;
import model.CartItem;
import model.Order;
import model.OrderDetail;
import model.Product;

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
 *   add      — add product to cart
 *   remove   — remove a product line from cart
 *   update   — update all quantities from cart form
 *   checkout — save order to DB, clear cart, redirect to confirmation
 *   view     — show cart page (default GET)
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private static final String CART_KEY = "cart";

    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO   orderDAO   = new OrderDAO();

    // ----------------------------------------------------------------
    // GET  → view cart
    // ----------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        showCart(req, resp);
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

        switch (action) {
            case "add":      doAdd(req, resp);      break;
            case "remove":   doRemove(req, resp);   break;
            case "update":   doUpdate(req, resp);   break;
            case "checkout": doCheckout(req, resp); break;
            default:         showCart(req, resp);
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

        if (productIDs != null && quantities != null) {
            for (int i = 0; i < productIDs.length; i++) {
                int pid = Integer.parseInt(productIDs[i]);
                int q   = 0;
                try { q = Integer.parseInt(quantities[i]); } catch (Exception ignored) {}
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
    // CHECKOUT  — save to DB then clear cart
    // ----------------------------------------------------------------
    private void doCheckout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        Map<Integer, CartItem> cart = getCart(req);
        if (cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        // Build order object
        Order order = new Order();
        order.setFullName(req.getParameter("fullName"));
        order.setEmail(req.getParameter("email"));
        order.setPhone(req.getParameter("phone"));
        order.setAddress(req.getParameter("address"));
        order.setNote(req.getParameter("note"));

        // Calculate total & build details + HTML cho email
        double total = 0;
        List<OrderDetail> details = new ArrayList<>();
        StringBuilder itemsHTML = new StringBuilder();
        for (CartItem item : cart.values()) {
            total += item.getSubtotal();
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
        order.setTotalAmount(total);
        order.setDetails(details);

        int orderID = orderDAO.saveOrder(order);
        if (orderID > 0) {
            // Gửi email xác nhận nếu khách có nhập email
            String customerEmail = order.getEmail();
            if (customerEmail != null && !customerEmail.trim().isEmpty()) {
                util.EmailUtil.sendOrderConfirmation(
                    customerEmail.trim(),
                    order.getFullName(),
                    orderID,
                    total,
                    order.getAddress(),
                    itemsHTML.toString()
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
        req.setAttribute("cart", getCart(req));
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
