package com.example.naturelink.Service;

import com.example.naturelink.Entity.Transport;
import com.example.naturelink.Entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ITransportService {

    public List<Transport> getAllTransports();
    public Optional<Transport> getTransportById(Integer id);
    public Transport addTransport(Transport transport);

    public Transport updateTransport(Integer id, Transport transportDetails);
    Transport updateTransportWithImage(Integer id, Transport transportDetails, MultipartFile imageFile);
    public void deleteTransport(Integer id);
    Transport addTransportWithImage(Transport transport, MultipartFile imageFile);

}
