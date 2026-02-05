package com.phonenexus.products.models;

public enum ProductStatus {
    DRAFT, // Nháp, chưa hiển thị
    ACTIVE, // Đang hoạt động, hiển thị bình thường
    HIDDEN, // Tạm ẩn (do Admin)
    OUT_OF_STOCK // Hết hàng (có thể tự động hoặc thủ công)
}
