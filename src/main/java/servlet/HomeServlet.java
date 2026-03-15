package servlet;

import dao.CategoryDAO;
import dao.ProductDAO;
import model.Category;
import model.Product;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/home", "/index", ""})
public class HomeServlet extends HttpServlet {

    private final ProductDAO  productDAO  = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private static final int PAGE_SIZE = 12;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // --- Đọc tham số ---
        String keyword    = req.getParameter("keyword");
        String catParam   = req.getParameter("cat");
        String brandParam = req.getParameter("brand");
        String sortBy     = req.getParameter("sort");     // price_asc | price_desc | newest | bestseller
        String minPriceP  = req.getParameter("minPrice");
        String maxPriceP  = req.getParameter("maxPrice");
        String movementP  = req.getParameter("movement");
        String waterP     = req.getParameter("water");
        String pageParam  = req.getParameter("page");

        int page      = 1;
        int catID     = 0;
        double minPrice = 0, maxPrice = 0;

        try { page    = Math.max(1, Integer.parseInt(pageParam)); }  catch (Exception e) {}
        try { catID   = Integer.parseInt(catParam); }                catch (Exception e) {}
        try { minPrice = Double.parseDouble(minPriceP); }            catch (Exception e) {}
        try { maxPrice = Double.parseDouble(maxPriceP); }            catch (Exception e) {}

        if (sortBy == null || sortBy.isEmpty()) sortBy = "price_asc";

        List<Product> products;
        int totalProducts;

        // --- Nếu có keyword thì tìm kiếm (không phân trang) ---
        if (keyword != null && !keyword.trim().isEmpty()) {
            products      = productDAO.searchProducts(keyword.trim());
            totalProducts = products.size();
            req.setAttribute("keyword", keyword.trim());
        } else {
            // --- Lọc + sắp xếp + phân trang ---
            Integer catFilter = catID > 0 ? catID : null;
            products = productDAO.getFilteredProductsSorted(
                catFilter, brandParam, minPrice, maxPrice,
                movementP, waterP, sortBy, page, PAGE_SIZE
            );
            totalProducts = productDAO.countFilteredProducts(
                catFilter, brandParam, minPrice, maxPrice, movementP, waterP
            );
            if (catID > 0) req.setAttribute("selectedCat", catID);
        }

        int totalPages = (int) Math.ceil((double) totalProducts / PAGE_SIZE);

        // --- Dữ liệu cho filter sidebar ---
        List<Category> categories   = categoryDAO.getAllCategories();
        List<String>   brands       = productDAO.getAllBrands();
        List<String>   movements    = productDAO.getAllMovements();
        List<String>   waterResists = productDAO.getAllWaterResists();

        // --- Set attributes ---
        req.setAttribute("products",     products);
        req.setAttribute("categories",   categories);
        req.setAttribute("brands",       brands);
        req.setAttribute("movements",    movements);
        req.setAttribute("waterResists", waterResists);
        req.setAttribute("currentPage",  page);
        req.setAttribute("totalPages",   totalPages);
        req.setAttribute("totalProducts",totalProducts);
        req.setAttribute("sortBy",       sortBy);
        req.setAttribute("selectedBrand",   brandParam);
        req.setAttribute("selectedMovement",movementP);
        req.setAttribute("selectedWater",   waterP);
        req.setAttribute("minPrice",     minPriceP);
        req.setAttribute("maxPrice",     maxPriceP);

        req.getRequestDispatcher("/pages/home.jsp").forward(req, resp);
    }
}
