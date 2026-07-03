package com.dwatch.product;

import com.dwatch.common.Pagination;
import com.dwatch.review.Review;
import com.dwatch.review.ReviewDAO;
import com.dwatch.user.User;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/product")
public class ProductDetailServlet extends HttpServlet {

    private static final int REVIEWS_PAGE_SIZE = 10;

    private final ProductDAO productDAO = new ProductDAO();
    private final ReviewDAO  reviewDAO  = new ReviewDAO();

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

            int reviewPage = Pagination.parsePage(req.getParameter("reviewPage"));
            int totalReviews = reviewDAO.countReviewsByProduct(id);
            Pagination reviewPagination = new Pagination(reviewPage, REVIEWS_PAGE_SIZE, totalReviews);
            List<Review> reviews = reviewDAO.getReviewsByProduct(id, reviewPagination.getCurrentPage(), REVIEWS_PAGE_SIZE);

            req.setAttribute("product", product);
            req.setAttribute("relatedProducts", relatedProducts);
            req.setAttribute("reviews", reviews);
            req.setAttribute("reviewCount", totalReviews);
            req.setAttribute("averageRating", reviewDAO.getAverageRating(id));
            req.setAttribute("reviewCurrentPage", reviewPagination.getCurrentPage());
            req.setAttribute("reviewTotalPages", reviewPagination.getTotalPages());
            req.getRequestDispatcher("/pages/productDetail.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/home");
        }
    }

    /** Chỉ hỗ trợ action=addReview: gửi đánh giá sản phẩm (cần đăng nhập). */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");

        int productID;
        try {
            productID = Integer.parseInt(req.getParameter("productID"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        User loggedUser = (User) req.getSession().getAttribute("loggedUser");
        if (loggedUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" +
                java.net.URLEncoder.encode(req.getContextPath() + "/product?id=" + productID, "UTF-8"));
            return;
        }

        int rating;
        try {
            rating = Integer.parseInt(req.getParameter("rating"));
        } catch (NumberFormatException e) {
            rating = 0;
        }
        if (rating >= 1 && rating <= 5) {
            Review review = new Review();
            review.setProductID(productID);
            review.setUserID(loggedUser.getUserID());
            review.setAuthorName(loggedUser.getFullName());
            review.setRating(rating);
            review.setComment(req.getParameter("comment"));
            reviewDAO.addReview(review);
        }

        resp.sendRedirect(req.getContextPath() + "/product?id=" + productID + "#reviews");
    }
}
