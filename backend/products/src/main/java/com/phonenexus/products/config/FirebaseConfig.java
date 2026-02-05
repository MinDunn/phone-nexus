package com.phonenexus.products.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Check if Firebase is already initialized
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        // Try to load from classpath first, then fallback to environment variable or
        // specific path
        // For this example, we assume serviceAccountKey.json is in resources or
        // provided via Env
        InputStream serviceAccount;
        try {
            serviceAccount = new ClassPathResource("serviceAccountKey.json").getInputStream();
        } catch (IOException e) {
            // Fallback for development if file is missing, or handle gracefully
            // For production, this should likely throw an exception or read from ENV
            return null;
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("phone-nexus.appspot.com") // Replace with actual bucket
                .build();

        return FirebaseApp.initializeApp(options);
    }
}
