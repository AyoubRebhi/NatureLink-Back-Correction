package com.example.naturelink.Repository;

import com.example.naturelink.Entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD

public interface IActivityRepository extends JpaRepository<Activity, Long> {
    // Define custom query methods if needed
=======
import org.springframework.stereotype.Repository;

@Repository
public interface IActivityRepository extends JpaRepository<Activity, Long> {
>>>>>>> origin/ayoub
}
