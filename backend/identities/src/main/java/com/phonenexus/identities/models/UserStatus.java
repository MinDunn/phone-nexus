package com.phonenexus.identities.models;

public enum UserStatus {
    ACTIVE, // Hoạt động bình thường
    LOCKED, // Tạm khóa (nhập sai pass nhiều lần)
    BANNED, // Cấm vĩnh viễn
    PENDING_VERIFICATION // Chờ xác thực email/sdt
}
