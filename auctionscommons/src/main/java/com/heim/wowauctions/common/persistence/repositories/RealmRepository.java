package com.heim.wowauctions.common.persistence.repositories;


import com.heim.wowauctions.common.persistence.models.Realm;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RealmRepository extends CrudRepository<Realm, Long> {

    //   Realm findByName(String serverName);
//    Realm findBySlug(String slug);
//    List<Realm> findByConnection(ObjectId objectId);


}
