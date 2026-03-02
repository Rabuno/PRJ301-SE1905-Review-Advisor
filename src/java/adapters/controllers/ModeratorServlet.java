package adapters.controllers;

import domain.entities.Review;
import domain.enums.ReviewStatus;
import application.services.ReviewService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/moderator")
public class ModeratorServlet extends HttpServlet {

    private ReviewService reviewService = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<Review> reviews = reviewService.getFlaggedReviews();

        req.setAttribute("reviews", reviews);
        req.getRequestDispatcher("/views/moderation/dashboard.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String reviewId = req.getParameter("reviewId");
        String action = req.getParameter("action");

        ReviewStatus status;

        if ("publish".equals(action)) {
            status = ReviewStatus.PUBLISHED;
        } else {
            status = ReviewStatus.HIDDEN;
        }

        reviewService.moderateReview(reviewId, status);

        resp.sendRedirect(req.getContextPath() + "/moderator");
    }
}