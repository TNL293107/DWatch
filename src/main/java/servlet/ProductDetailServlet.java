package servlet;

import dao.ProductDAO;
import model.Product;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/product")
public class ProductDetailServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        try {
            int     id      = Integer.parseInt(idParam);
            Product product = productDAO.getProductByID(id);
            if (product == null) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }

            // Sản phẩm liên quan: cùng danh mục, lấy 4 sản phẩm ngẫu nhiên
            List<Product> relatedProducts = productDAO.getRelatedProducts(
                product.getCategoryID(), product.getProductID(), 4
            );

            req.setAttribute("product", product);
            req.setAttribute("relatedProducts", relatedProducts);
            req.getRequestDispatcher("/pages/productDetail.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/home");
        }
    }
}
