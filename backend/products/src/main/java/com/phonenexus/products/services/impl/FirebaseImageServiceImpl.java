package com.phonenexus.products.services.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.phonenexus.products.services.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class FirebaseImageServiceImpl implements ImageService {

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Bucket bucket = StorageClient.getInstance().bucket();

            Blob blob = bucket.create(fileName, file.getInputStream(), file.getContentType());

            // Generate a signed URL valid for 365 days (or make it public)
            // For simplicity in this demo, we generate a signed URL.
            // In a real app, you might want a public URL if the bucket is public.
            return blob.signUrl(365, TimeUnit.DAYS).toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }
}
