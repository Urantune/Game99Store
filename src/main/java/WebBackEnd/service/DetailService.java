package WebBackEnd.service;

import WebBackEnd.model.Entity.Detail_Specical;
import WebBackEnd.repository.DetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DetailService {

    private final DetailRepository detailRepository;

    public DetailService(DetailRepository detailRepository) {
        this.detailRepository = detailRepository;
    }

    public List<Detail_Specical> findByDetailType(String detailType) {
        return detailRepository.findByDetailType(detailType);
    }

}

