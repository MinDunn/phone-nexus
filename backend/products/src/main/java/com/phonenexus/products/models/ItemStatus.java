package com.phonenexus.products.models;

public enum ItemStatus {
    AVAILABLE, // Sẵn sàng để bán
    RESERVED, // Đã được đặt (đang trong giỏ hàng hoặc đơn hàng chờ thanh toán)
    SOLD, // Đã bán thành công
    DEFECTIVE, // Hàng lỗi/hỏng
    RETURNED // Hàng trả lại
}
