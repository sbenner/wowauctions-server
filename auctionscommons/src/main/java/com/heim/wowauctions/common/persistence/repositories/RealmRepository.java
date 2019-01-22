package com.heim.wowauctions.common.persistence.repositories;


import com.heim.wowauctions.common.persistence.models.Realm;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RealmRepository extends SolrCrudRepository<Realm, String> {

    //   Realm findByName(String serverName);
//    Realm findBySlug(String slug);
//    List<Realm> findByConnection(ObjectId objectId);


}
