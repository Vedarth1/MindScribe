package com.Spring.MindScribe.services;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file) {
        try {
            Map upload = cloudinary.uploader().upload(file.getBytes(), 
                Map.of("resource_type", "auto"));
            return upload.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Cloudinary upload failed: " + e.getMessage());
        }
    }
    public String deleteFile(String url) {
        String publicId = extractPublicId(url);

        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return result.get("result").toString();
        } catch (Exception e) {
            throw new RuntimeException("Cloudinary delete failed: " + e.getMessage());
        }
    }

    private String extractPublicId(String url) {
        String filename = url.substring(url.lastIndexOf("/") + 1);
        return filename.substring(0, filename.lastIndexOf(".")); 
    }
}