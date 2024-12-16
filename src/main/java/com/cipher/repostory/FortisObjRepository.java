package com.cipher.repostory;

import com.cipher.dto.FortisObjDTO;
import com.cipher.entity.FortisObj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FortisObjRepository extends JpaRepository<FortisObj, Integer> {

    @Query("SELECT new com.cipher.dto.FortisObjDTO(f.id, f.postId, f.uploadTime) FROM FortisObj f WHERE f.postId = :postId")
    public List<FortisObjDTO> findByPostId(int postId);

}
