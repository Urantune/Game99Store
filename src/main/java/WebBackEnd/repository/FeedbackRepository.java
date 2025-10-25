package WebBackEnd.repository;

import WebBackEnd.Entity.Feedback;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface FeedbackRepository extends CrudRepository<Feedback, UUID> {

        List<Feedback> findByGameId(UUID gameId);
}
