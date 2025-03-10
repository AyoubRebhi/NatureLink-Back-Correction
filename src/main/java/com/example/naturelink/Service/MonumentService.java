package com.example.naturelink.Service;
import com.example.naturelink.Entity.Monument;
import com.example.naturelink.Repository.MonumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MonumentService implements IMonumentService {

    @Autowired
    private MonumentRepository monumentRepository;

    @Override
    public Monument createMonument(Monument monument) {
        return monumentRepository.save(monument);
    }

    @Override
    public Monument updateMonument(Monument monument) {
        return monumentRepository.save(monument);
    }

    @Override
    public void deleteMonument(Integer id) {
        monumentRepository.deleteById(id);
    }

    @Override
    public List<Monument> getAllMonuments() {
        return monumentRepository.findAll();
    }

    @Override
    public Optional<Monument> getMonumentById(Integer id) {
        return monumentRepository.findById(id);
    }
}