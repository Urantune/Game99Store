package WebBackEnd.repository;

import WebBackEnd.model.Entity.Detail_Specical;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DetailRepository extends CrudRepository<Detail_Specical, Integer> {


    List<Detail_Specical> findByDetailType(String detailType);


}

