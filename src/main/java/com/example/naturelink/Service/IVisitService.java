package com.example.naturelink.Service;

import com.example.naturelink.Entity.Visit;
import java.util.List;
import java.util.Optional;

public interface IVisitService {
    Visit createVisit(Visit visit);
    Visit updateVisit(Visit visit);
    void deleteVisit(Integer id);
    List<Visit> getAllVisits();
    Optional<Visit> getVisitById(Integer id);
}
