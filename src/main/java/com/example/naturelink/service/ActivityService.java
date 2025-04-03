package com.example.naturelink.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.naturelink.entity.Activity;
import com.example.naturelink.repository.IActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ActivityService implements IActivityService {

    private final IActivityRepository activityRepository;
    @Autowired
    private Cloudinary cloudinary;

    public ActivityService(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    @Override
    public Optional<Activity> getActivityById(Integer id) {
        return activityRepository.findById(id);
    }

    @Override
    public Activity addActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public Activity updateActivity(Integer id, Activity activityDetails) {
        return activityRepository.findById(id).map(activity -> {
            activity.setName(activityDetails.getName());
            activity.setDescription(activityDetails.getDescription());
            activity.setProviderId(activityDetails.getProviderId());
            activity.setLocation(activityDetails.getLocation());
            activity.setDuration(activityDetails.getDuration());
            activity.setPrice(activityDetails.getPrice());
            activity.setMaxParticipants(activityDetails.getMaxParticipants());
            activity.setDifficultyLevel(activityDetails.getDifficultyLevel());
            activity.setRequiredEquipment(activityDetails.getRequiredEquipment());
            return activityRepository.save(activity);
        }).orElseThrow(() -> new RuntimeException("Activity not found"));
    }

    @Override
    public void deleteActivity(Integer id) {
        activityRepository.deleteById(id);
    }
    public Activity addActivityWithImages(Activity activity, List<MultipartFile> imageFiles) {
        List<String> uploadedImageUrls = new ArrayList<>();
        for (MultipartFile file : imageFiles) {
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                uploadedImageUrls.add((String) uploadResult.get("secure_url"));
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        activity.setImageUrls(uploadedImageUrls);
        return activityRepository.save(activity);
    }

}
