package servlet;

import dao.CategoryDAO;
import dao.ProductDAO;
import model.Category;
import model.Product;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.util.List;
import java.io.IOException;
/**
 * ProductSetupServlet — admin page to add/edit/delete products.
 * Requirement 1: Setup products page.
 *
 * Simple password protection: redirects to /admin/login if not authenticated.
 * GET  → show product list + add form
 * POST → insert / update / delete
 */
@WebServlet("/admin/products")
public class ProductSetupServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "images";

    private final ProductDAO  productDAO  = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    // ----------------------------------------------------------------
    // GET — list all products and show the add form
    // ----------------------------------------------------------------
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
            req.setAttribute("editProduct", productDAO.getProductByID(id));
        }
        if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            productDAO.deleteProduct(id);
            resp.sendRedirect(req.getContextPath() + "/admin/products?msg=deleted");
            return;
        }

        req.setAttribute("products",   productDAO.getAllProducts());
        req.setAttribute("categories", categoryDAO.getAllCategories());
        req.getRequestDispatcher("/pages/productSetup.jsp").forward(req, resp);
    }

    // ----------------------------------------------------------------
    // POST — insert or update a product
    // ----------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }
        req.setCharacterEncoding("UTF-8");

        Product p = new Product();
        String idParam = req.getParameter("productID");
        if (idParam != null && !idParam.isEmpty()) {
            p.setProductID(Integer.parseInt(idParam));
        }

        p.setProductName(req.getParameter("productName"));
        p.setDescription(req.getParameter("description"));
        p.setPrice(Double.parseDouble(req.getParameter("price")));
        p.setStock(Integer.parseInt(req.getParameter("stock")));
        p.setBrand(req.getParameter("brand"));
        p.setOrigin(req.getParameter("origin"));
        p.setWaterResist(req.getParameter("waterResist"));
        p.setCaseSize(req.getParameter("caseSize"));
        p.setMovement(req.getParameter("movement"));
        p.setCategoryID(Integer.parseInt(req.getParameter("categoryID")));

        // Handle image URL
        String imageURL = req.getParameter("imageURL");
        if (imageURL != null && !imageURL.trim().isEmpty()) {
            p.setImageURL(imageURL.trim());
        } else {
            p.setImageURL("");
        }

        boolean success;
        if (p.getProductID() > 0) {
            success = productDAO.updateProduct(p);
        } else {
            success = productDAO.insertProduct(p);
        }

        String msg = success ? "saved" : "error";
        resp.sendRedirect(req.getContextPath() + "/admin/products?msg=" + msg);
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private boolean isLoggedIn(HttpServletRequest req) {
    HttpSession session = req.getSession(false);
    return session != null && Boolean.TRUE.equals(session.getAttribute("adminLoggedIn"));
    }

}
