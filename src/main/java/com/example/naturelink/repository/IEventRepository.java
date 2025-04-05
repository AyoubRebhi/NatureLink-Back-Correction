package com.example.naturelink.repository;
import com.example.naturelink.entity.Event;

import org.springframework.data.jpa.repository.JpaRepository;
public interface IEventRepository extends JpaRepository<Event, Integer> {
}
