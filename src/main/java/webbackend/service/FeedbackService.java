package webbackend.service;

import webbackend.entity.Feedback;
import webbackend.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    public List<Feedback> findFeedbackByGameId(UUID gameId) {
        return feedbackRepository.findByGameId(gameId);
    }

    public void saveFeedback(Feedback feedback) {
        feedbackRepository.save(feedback);
    }

}
