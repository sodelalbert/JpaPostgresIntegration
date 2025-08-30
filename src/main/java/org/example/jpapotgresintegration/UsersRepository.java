package org.example.jpapotgresintegration;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UsersRepository extends JpaRepository<Users, Long> {

    // custom query methods if needed
}