package com.raf.nwpdomaci3.repository;

import com.raf.nwpdomaci3.domain.entities.User;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepositoryImplementation<User, Long> {

    User findByEmail(String email);

}
