package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.OrderDetailDTO;
import com.sontung.eproject_springboot.dto.request.OrderDtoRequest;
import com.sontung.eproject_springboot.entity.Cart;
import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.entity.OrderDetail;
import com.sontung.eproject_springboot.repository.*;
import com.sontung.eproject_springboot.util.VnPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final ComboService comboService;
    private final OrderDetailService orderDetailService;

    private final IOrderRepository iOrderRepository;
    private final IProductRepository productRepository;
    private final ICartRepository cartRepository;
    private final IAccountRepository accountRepository;
    private final IComboRepository comboRepository;

    public OrderService(IOrderRepository iOrderRepository, OrderDetailService orderDetailService, IProductRepository productRepository, ComboService comboService, ICartRepository cartRepository, IAccountRepository accountRepository, IComboRepository comboRepository) {
        this.comboService = comboService;
        this.orderDetailService = orderDetailService;
        this.iOrderRepository = iOrderRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.accountRepository = accountRepository;
        this.comboRepository = comboRepository;
    }

    public Page<Order> getOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return iOrderRepository.findAll(pageable);
    }

    // ====== Count order: Admin site======//
    // Count all order
    public long countOrder(){
        return iOrderRepository.countOrder();
    }
    // Count order by filterDate
    public long countOrderByFilterDate(@DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate){
        LocalDate filterLocalDate = filterDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return iOrderRepository.countOrderByFilterDate(filterLocalDate);
    }

    // ===============================//
    public Order getOrder(String orderId) {
        return iOrderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
    }

    public List<OrderDetailDTO> getOrderDetails(String orderId) {
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetails(orderId);
        List<OrderDetailDTO> orderDetailDTOList = new ArrayList<>();
        for (OrderDetail item : orderDetails) {
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
            orderDetailDTO.setOrder(item.getOrder());
            orderDetailDTO.setProduct(item.getProduct());
            orderDetailDTO.setQuantity(item.getQuantity());
            orderDetailDTO.setUniquePrice(item.getPrice());
            if ((item.getCombo()) == null) {
                orderDetailDTOList.add(orderDetailDTO);
            } else {
                orderDetailDTO.setCombo(comboService.getComboById((item.getCombo()).getComboId()));
                orderDetailDTOList.add(orderDetailDTO);
            }
        }
        return orderDetailDTOList;
    }
//    public List<ComboOrderDTO> getCombosOrder(String orderId){
//        return orderDetailService.getCombos(orderId);
//    }

    public Page<Order> getOrdersByPrice(int priceValue, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return switch (priceValue) {
            case 1 -> iOrderRepository.findOrdersUnder100K(pageable);
            case 2 -> iOrderRepository.findOrdersBetween100KAnd200K(pageable);
            case 3 -> iOrderRepository.findOrdersBetween200KAnd300K(pageable);
            case 4 -> iOrderRepository.findOrdersBetween300KAnd500K(pageable);
            case 5 -> iOrderRepository.findOrdersOver500K(pageable);
            default -> throw new IllegalArgumentException("Invalid price range");
        };
    }

    public long countOrderByPrice(int priceValue) {
        return switch (priceValue) {
            case 1 -> iOrderRepository.countByTotalAmountLessThan(BigDecimal.valueOf(100000));
            case 2 -> iOrderRepository.countByTotalAmountBetween(BigDecimal.valueOf(100000), BigDecimal.valueOf(200000));
            case 3 -> iOrderRepository.countByTotalAmountBetween(BigDecimal.valueOf(200000), BigDecimal.valueOf(300000));
            case 4 -> iOrderRepository.countByTotalAmountBetween(BigDecimal.valueOf(300000), BigDecimal.valueOf(400000));
            case 5 -> iOrderRepository.countByTotalAmountGreaterThanEqual(BigDecimal.valueOf(400000));
            default -> throw new IllegalArgumentException("Invalid price range");
        };
    }

    // Filter Order by date in OrderManagement
    public Page<Order> getOrdersByFilterDateOrder(@DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate,
                                             int page,
                                             int size) {
        LocalDate filterLocalDate = filterDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Pageable pageable = PageRequest.of(page - 1, size);
        return iOrderRepository.findByOrderDateOrder(filterLocalDate, pageable);
    }
    // Filter Order by date in ComboManagement
    public List<Order> getOrdersByFilterDateCombo(@DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate) {
        LocalDate filterLocalDate = filterDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return iOrderRepository.findByOrderDateCombo(filterLocalDate);
    }
    public List<Order> getOrdersByPriceAndDate(int priceValue,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate) {
        LocalDate filterLocalDate = filterDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return iOrderRepository.findAll().stream().filter(i -> {
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


    public Order createOrder(OrderDtoRequest orderDtoRequest, String userId) {
        List<Cart> carts = cartRepository.getCartsByAccount_AccountId(userId);
        // 1 Tính tổng tiền dơn hàng, giá lấy trong bảng product.
        BigDecimal totalAmount = BigDecimal.ZERO;
        // 2 Create Order.
        Order order = new Order();
        order.setAccount(accountRepository.findById(userId).get());
        order.setOrderDate(LocalDate.now());
        order.setStatus("Chờ thanh toán");
        order.setShippingAddress(orderDtoRequest.getShippAddress());
        order.setShippingPhone(orderDtoRequest.getShippingPhone());

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (Cart cart : carts) {
            if (orderDtoRequest.getCartItems().contains(cart.getCartId())) {
                BigDecimal price = BigDecimal.ZERO;
                // Create Order Detail
                OrderDetail orderDetail = new OrderDetail();
                // Case Product
                if (cart.getProductId() != null && cart.getComboId() == null) {
                    price = productRepository.getPriceByProductId(cart.getProductId());
                    orderDetail.setProduct(productRepository.findById(cart.getProductId()).get());
                } else {
                    // Case Combo
                    price = comboRepository.getFinalAmountByComboId(cart.getComboId());
                    orderDetail.setCombo(comboRepository.findById(cart.getComboId()).get());
                }
                totalAmount = totalAmount.add(price);


                orderDetail.setOrder(order);
                orderDetail.setPrice(price);
                orderDetail.setQuantity(cart.getQuantity());
                orderDetail.setTotalPrice(price.multiply(new BigDecimal(cart.getQuantity())));

                orderDetails.add(orderDetail);
            }
        }

        order.setTotalAmount(totalAmount);
        order.setOrderDetails(orderDetails);


        // 3 Save Order.
        Order savedOrder = iOrderRepository.save(order);
//        // 4. Create and save 'OrderDetails'.
//        for(String cartItemId : orderDtoRequest.getCartItems()) {
//            Product product = productRepository.findById(cartItemId)
//                    .orElseThrow(()-> new RuntimeException("Sản phẩm không tìm thấy."));
//        }

        // 5. Call VnPay in layer Controller.
        return savedOrder;
    }

    public String getVnpay(Order order, HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = order.getTotalAmount().longValue();
        amount *= 100;
        //String bankCode = req.getParameter("bankCode");
        String bankCode = "NCB"; // Test với ngân hàng NCB vì thẻ cuả vnpay cung cấp là NCB
        String vnp_TxnRef = VnPayUtil.getRandomNumber(8);
        String vnp_IpAddr = VnPayUtil.getIpAddress(req);

        String vnp_TmnCode = VnPayUtil.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        //        String locate = req.getParameter("language");
//        if (locate != null && !locate.isEmpty()) {
//            vnp_Params.put("vnp_Locale", locate);
//        } else {
        vnp_Params.put("vnp_Locale", "vn");
//        }

        vnp_Params.put("vnp_ReturnUrl", VnPayUtil.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VnPayUtil.hmacSHA512(VnPayUtil.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VnPayUtil.vnp_PayUrl + "?" + queryUrl;

        return paymentUrl;
    }
}