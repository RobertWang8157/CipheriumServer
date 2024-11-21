package com.cipher.repostory;




import com.cipher.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

public User findOfficerByUserName(String userName);
}
