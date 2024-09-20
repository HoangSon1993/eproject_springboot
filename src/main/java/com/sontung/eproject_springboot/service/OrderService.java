package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.OrderDetailDTO;
import com.sontung.eproject_springboot.dto.request.OrderDtoRequest;
import com.sontung.eproject_springboot.entity.Order;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface OrderService {
    List<Order> getOrders();

    Order getOrder(String orderId);

    List<OrderDetailDTO> getOrderDetails(String orderId);

    Page<Order> getOrders(int page, int size);

    // ========================== Count order: Admin site==============//
    // Count all order
    long countOrder();

    // Filter Order by date in OrderManagement
    Page<Order> getOrdersByFilterDateOrder(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate filterDate,
                                           int page,
                                           int size);

    // Count order by filterDate
    long countOrderByFilterDate(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate filterDate);

    Page<Order> getOrdersByPrice(int priceValue, int page, int size);

    long countOrderByPrice(int priceValue);

    List<Order> getOrdersByPrice(int priceValue);

    List<Order> getOrdersByFilterDate(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate filterDate);

    // Filter Order by date in ComboManagement
    List<Order> getOrdersByFilterDateCombo(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate filterDate);

    List<Order> getOrdersByPriceAndDate(int priceValue,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate);

    long countOrderByPriceAndFilterDate(int priceValue, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate filterDate);

    Page<Order> getOrdersByPriceAndDate(int priceValue,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate filterDate,
                                        int page,
                                        int size);

    Order createOrder(OrderDtoRequest orderDtoRequest, String userId);

    String getVnpay(Order order, HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException;

    String handleResult(HttpServletRequest request, String userId) throws UnsupportedEncodingException;

    Order findByCodeAndAccountId(String accountId, String code);
}
