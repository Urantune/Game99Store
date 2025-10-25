package WebBackEnd.repository;

import WebBackEnd.Entity.Detail_Specical;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DetailRepository extends CrudRepository<Detail_Specical, Integer> {


    List<Detail_Specical> findByDetailType(String detailType);


}

