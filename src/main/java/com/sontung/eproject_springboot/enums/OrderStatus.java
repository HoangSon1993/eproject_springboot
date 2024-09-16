package com.sontung.eproject_springboot.enums;

import java.util.HashMap;
import java.util.Map;

public enum OrderStatus {

    ORDERED("Đã đặt hàng"),
    PAID("Đã thanh toán"),    // Đã thanh toán
    CANCELED("Hủy bỏ"), // Hủy bỏ
    PENDING("Chờ thanh toán"), // Chờ thanh toán
    COD("Thanh toán khi nhân hàng.");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (OrderStatus orderStatus : OrderStatus.values()) {
            map.put(orderStatus.name(), orderStatus.getDescription());
        }
        return map;
    }
}