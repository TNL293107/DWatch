package servlet;

import dao.ProductDAO;
import model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * So sánh sản phẩm: lưu tối đa 3 sản phẩm trong session, hiển thị bảng so sánh.
 */
@WebServlet("/compare")
public class CompareServlet extends HttpServlet {

    private static final String COMPARE_KEY = "compareList";
    private static final int MAX_COMPARE = 3;
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) req.getSession(true).getAttribute(COMPARE_KEY);
        if (ids == null) ids = new ArrayList<>();
        String addParam = req.getParameter("add");
        String removeParam = req.getParameter("remove");
        if (addParam != null && !addParam.isEmpty()) {
            try {
                int id = Integer.parseInt(addParam.trim());
                Set<Integer> set = new LinkedHashSet<>(ids);
                if (set.size() >= MAX_COMPARE && !set.contains(id)) set.remove(set.iterator().next());
                set.add(id);
                ids = new ArrayList<>(set);
                if (ids.size() > MAX_COMPARE) ids = ids.subList(ids.size() - MAX_COMPARE, ids.size());
                req.getSession().setAttribute(COMPARE_KEY, ids);
            } catch (NumberFormatException ignored) {}
            resp.sendRedirect(req.getContextPath() + "/compare");
            return;
        }
        if (removeParam != null && !removeParam.isEmpty()) {
            try {
                int id = Integer.parseInt(removeParam.trim());
                ids.removeIf(i -> i == id);
                req.getSession().setAttribute(COMPARE_KEY, ids);
            } catch (NumberFormatException ignored) {}
            resp.sendRedirect(req.getContextPath() + "/compare");
            return;
        }
        List<Product> products = productDAO.getProductsByIDs(ids);
        req.setAttribute("compareProducts", products);
        req.setAttribute("compareIds", ids);
        req.getRequestDispatcher("/pages/compare.jsp").forward(req, resp);
    }
}
