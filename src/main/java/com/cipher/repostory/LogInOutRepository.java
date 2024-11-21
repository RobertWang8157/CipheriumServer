package com.cipher.repostory;

import com.cipher.entity.LogInOut;
import com.cipher.entity.LogLogInOutPK;
import com.cipher.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogInOutRepository extends JpaRepository<LogInOut, LogLogInOutPK> {


}
