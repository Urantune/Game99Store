package WebBackEnd.repository;

import WebBackEnd.model.Entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface FeedbackRepository extends CrudRepository<Feedback, UUID> {

        List<Feedback> findByGameId(UUID gameId);
}
