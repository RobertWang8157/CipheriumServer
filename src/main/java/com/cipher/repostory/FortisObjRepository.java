package com.cipher.repostory;

import com.cipher.entity.FortisObj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FortisObjRepository extends JpaRepository<FortisObj, Integer> {


    public Optional<FortisObj> findByPostId(int postId);

}
