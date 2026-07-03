package com.dwatch.admin;

import com.dwatch.voucher.Voucher;
import com.dwatch.voucher.VoucherDAO;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Admin voucher management: create/edit/deactivate discount codes.
 */
@WebServlet("/admin/vouchers")
public class AdminVoucherServlet extends HttpServlet {

    private final VoucherDAO voucherDAO = new VoucherDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }

        String action = req.getParameter("action");
        if ("edit".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            req.setAttribute("editVoucher", voucherDAO.getVoucherByID(id));
        }
        if ("deactivate".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            voucherDAO.deactivateVoucher(id);
            resp.sendRedirect(req.getContextPath() + "/admin/vouchers?msg=deactivated");
            return;
        }

        req.setAttribute("vouchers", voucherDAO.getAllVouchers());
        req.getRequestDispatcher("/pages/adminVouchers.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }
        req.setCharacterEncoding("UTF-8");

        Voucher v = new Voucher();
        String idParam = req.getParameter("voucherID");
        if (idParam != null && !idParam.isEmpty()) {
            v.setVoucherID(Integer.parseInt(idParam));
        }
        v.setCode(req.getParameter("code").trim().toUpperCase());
        v.setDiscountType(req.getParameter("discountType"));
        v.setDiscountValue(Double.parseDouble(req.getParameter("discountValue")));
        v.setMinOrderAmount(parseNullableDouble(req.getParameter("minOrderAmount")));
        v.setMaxUses(parseNullableInt(req.getParameter("maxUses")));
        v.setExpiryDate(parseNullableDate(req.getParameter("expiryDate")));
        v.setActive(true);

        boolean success = v.getVoucherID() > 0
            ? voucherDAO.updateVoucher(v)
            : voucherDAO.insertVoucher(v);

        resp.sendRedirect(req.getContextPath() + "/admin/vouchers?msg=" + (success ? "saved" : "error"));
    }

    private Double parseNullableDouble(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return null; }
    }

    private Integer parseNullableInt(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return null; }
    }

    private java.util.Date parseNullableDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return new SimpleDateFormat("yyyy-MM-dd").parse(s); } catch (ParseException e) { return null; }
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && Boolean.TRUE.equals(session.getAttribute("adminLoggedIn"));
    }
}
