package application.ports;

import domain.entities.Alert;

public interface IAlertRepository {
    boolean saveAlert(Alert alert);

    Alert findByReviewId(String reviewId);
}