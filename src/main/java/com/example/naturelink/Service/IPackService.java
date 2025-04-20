package com.example.naturelink.Service;

import com.example.naturelink.dto.PackDTO;
import com.example.naturelink.Entity.Pack;

import java.util.List;

public interface IPackService {

    List<PackDTO> getAllPacks();

    PackDTO getPackById(Long id);

    Pack addPack(PackDTO packDTO);

    Pack updatePack(Long id, PackDTO packDTO);

    void deletePack(Long id);
}
