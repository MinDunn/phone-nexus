package com.phonenexus.products.controllers;

import com.phonenexus.products.services.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products/images")
@Tag(name = "Image Management", description = "APIs for uploading product images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload an image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = imageService.uploadImage(file);
        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        return ResponseEntity.ok(response);
    }
}
