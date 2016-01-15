package com.heim.wowauctions.repositories;

import com.heim.wowauctions.models.Realm;
import org.springframework.stereotype.Repository;



@Repository
public interface RealmRepository extends org.springframework.data.repository.CrudRepository<Realm,Long>
{

 //   Realm findByName(String serverName);
//    Realm findBySlug(String slug);
//    List<Realm> findByConnection(ObjectId objectId);


}
