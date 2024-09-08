package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.OrderDetailDTO;
import com.sontung.eproject_springboot.dto.request.OrderDtoRequest;
import com.sontung.eproject_springboot.entity.Order;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

public interface OrderService {
    List<Order> getOrders();

    Order getOrder(String orderId);

    List<OrderDetailDTO> getOrderDetails(String orderId);

    List<Order> getOrdersByPrice(int priceValue);

    List<Order> getOrdersByFilterDate(@DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate);

    List<Order> getOrdersByPriceAndDate(int priceValue,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate);

    Order createOrder(OrderDtoRequest orderDtoRequest, String userId);

    String getVnpay(Order order, HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException;

    String handleResult(HttpServletRequest request, String userId) throws UnsupportedEncodingException;

    Order findByCodeAndAccountId(String accountId, String code);
}
