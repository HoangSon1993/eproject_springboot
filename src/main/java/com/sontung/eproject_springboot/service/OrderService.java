package com.sontung.eproject_springboot.service;


import com.sontung.eproject_springboot.dto.OrderDetailDTO;
import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.entity.OrderDetail;
import com.sontung.eproject_springboot.repository.IOrderRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final IOrderRepository iOrderRepository;
    private final OrderDetailService orderDetailService;
    private final ComboService comboService;
    public OrderService(IOrderRepository iOrderRepository, OrderDetailService orderDetailService, ComboService comboService){
        this.comboService = comboService;
        this.iOrderRepository = iOrderRepository;
        this.orderDetailService = orderDetailService;
    }
    public List<Order> getOrders(){
        return iOrderRepository.findAll();
    }
    public Order getOrder(String orderId){
        return iOrderRepository.findById(orderId).orElseThrow(()-> new RuntimeException("Không tìm thấy đơn hàng"));
    }
    public List<OrderDetailDTO> getOrderDetails(String orderId){
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetails(orderId);
        List<OrderDetailDTO> orderDetailDTOList = new ArrayList<>();
        for (OrderDetail item: orderDetails) {
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
            orderDetailDTO.setOrder(item.getOrder());
            orderDetailDTO.setProduct(item.getProduct());
            orderDetailDTO.setQuantity(item.getQuantity());
            orderDetailDTO.setUniquePrice(item.getPrice());
            if((item.getCombo())==null){
                orderDetailDTOList.add(orderDetailDTO);
            }
            else{
                orderDetailDTO.setCombo(comboService.getComboById((item.getCombo()).getComboId()));
                orderDetailDTOList.add(orderDetailDTO);
            }
        }
        return orderDetailDTOList;
    }
//    public List<ComboOrderDTO> getCombosOrder(String orderId){
//        return orderDetailService.getCombos(orderId);
//    }

    public List<Order> getOrdersByPrice(int priceValue) {
        List<Order> orders = new ArrayList<>();

        switch (priceValue) {
            case 1: // Dưới 100k
                orders = iOrderRepository.findAll().stream()
                        .filter(i -> i.getTotalAmount().compareTo(new BigDecimal("100000")) < 0)
                        .collect(Collectors.toList());
                break;
            case 2: // 100k -> 200k
                orders = iOrderRepository.findAll().stream()
                        .filter(i -> i.getTotalAmount().compareTo(new BigDecimal("100000")) >= 0 &&
                                i.getTotalAmount().compareTo(new BigDecimal("200000")) <= 0)
                        .collect(Collectors.toList());
                break;
            case 3: // 200k -> 300k
                orders = iOrderRepository.findAll().stream()
                        .filter(i -> i.getTotalAmount().compareTo(new BigDecimal("200000")) > 0 &&
                                i.getTotalAmount().compareTo(new BigDecimal("300000")) <= 0)
                        .collect(Collectors.toList());
                break;
            case 4: // 300k -> 500k
                orders = iOrderRepository.findAll().stream()
                        .filter(i -> i.getTotalAmount().compareTo(new BigDecimal("300000")) > 0 &&
                                i.getTotalAmount().compareTo(new BigDecimal("500000")) <= 0)
                        .collect(Collectors.toList());
                break;
            case 5: // 500k Trở Lên
                orders = iOrderRepository.findAll().stream()
                        .filter(i -> i.getTotalAmount().compareTo(new BigDecimal("500000")) > 0)
                        .collect(Collectors.toList());
                break;
            default:
                throw new IllegalArgumentException("Invalid price range");
        }
        return orders;
    }
    public List<Order> getOrdersByFilterDate(@DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate){
        LocalDate filterLocalDate = filterDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return iOrderRepository.findAll().stream().filter(i->i.getOrderDate().equals(filterLocalDate)).collect(Collectors.toList());
    }

    public List<Order> getOrdersByPriceAndDate(int priceValue,
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate){
        LocalDate filterLocalDate = filterDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return iOrderRepository.findAll().stream().filter(i->{
            BigDecimal totalAmount = i.getTotalAmount();
            boolean priceMatches = false;
            switch (priceValue) {
                case 1: // Dưới 100k
                    priceMatches = totalAmount.compareTo(new BigDecimal("100000")) < 0;
                    break;
                case 2: // 100k -> 200k
                    priceMatches = totalAmount.compareTo(new BigDecimal("100000")) >= 0 &&
                            totalAmount.compareTo(new BigDecimal("200000")) <= 0;
                    break;
                case 3: // 200k -> 300k
                    priceMatches = totalAmount.compareTo(new BigDecimal("200000")) > 0 &&
                            totalAmount.compareTo(new BigDecimal("300000")) <= 0;
                    break;
                case 4: // 300k -> 500k
                    priceMatches = totalAmount.compareTo(new BigDecimal("300000")) > 0 &&
                            totalAmount.compareTo(new BigDecimal("500000")) <= 0;
                    break;
                case 5: // 500k Trở Lên
                    priceMatches = totalAmount.compareTo(new BigDecimal("500000")) > 0;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid price range");
            }
            return priceMatches && i.getOrderDate().equals(filterLocalDate);
        }).collect(Collectors.toList());
    }
}
