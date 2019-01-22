package com.heim.wowauctions.common.persistence.repositories;


import com.heim.wowauctions.common.persistence.models.Feedback;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/15/14
 * Time: 3:00 AM
 */
@Repository
public interface FeedbackRepository extends SolrCrudRepository<Feedback, String> {


}
