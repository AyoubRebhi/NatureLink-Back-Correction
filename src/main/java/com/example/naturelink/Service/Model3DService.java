package com.example.naturelink.Service;

import com.example.naturelink.Entity.Monument;
import com.example.naturelink.Repository.MonumentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class Model3DService {

    private static final String MODEL_UPLOAD_DIR = "monument-Uploads/models/";

    @Autowired
    private MonumentRepository monumentRepository;

    /**
     * Déclenche la génération d'un modèle 3D pour un monument donné.
     * @param monumentId ID du monument
     * @throws IOException en cas d'erreur lors de l'exécution du script
     */
    public void generate3DModel(Integer monumentId) throws IOException {
        Monument monument = monumentRepository.findById(monumentId)
                .orElseThrow(() -> new EntityNotFoundException("Monument with id " + monumentId + " not found"));

        // Simuler l'appel à un script Python pour COLMAP
        String imagePath = "monument-Uploads/" + monument.getImage(); // Chemin de l'image
        String outputModelPath = MODEL_UPLOAD_DIR + "model_" + monumentId + ".gltf"; // Chemin du modèle 3D
        String scriptCommand = "python3 /path/to/colmap_script.py --input " + imagePath + " --output " + outputModelPath;

        try {
            // Exécuter le script Python
            Process process = Runtime.getRuntime().exec(scriptCommand);
            process.waitFor();

            // Mettre à jour le monument avec les informations du modèle 3D
            monument.setModel3DUrl(outputModelPath);
            monument.setModel3DFormat("GLTF");
            monument.setModel3DStatus("ready");
            monumentRepository.save(monument);
        } catch (InterruptedException e) {
            monument.setModel3DStatus("failed");
            monumentRepository.save(monument);
            throw new RuntimeException("Model generation failed", e);
        }
    }
}