package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.request.OrderDtoRequest;
import com.sontung.eproject_springboot.entity.Cart;
import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.entity.OrderDetail;
import com.sontung.eproject_springboot.entity.Product;
import com.sontung.eproject_springboot.repository.*;
import com.sontung.eproject_springboot.util.VnPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderService {
    private final IOrderRepository orderRepository;
    private final IProductRepository productRepository;
    private final IComboRepository comboRepository;
    private final ICartRepository cartRepository;
    private final IAccountRepository accountRepository;
    private final CartService cartService;


    public OrderService(IOrderRepository orderRepository, IProductRepository productRepository, IComboRepository comboRepository, ICartRepository cartRepository, IAccountRepository accountRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.comboRepository = comboRepository;
        this.cartRepository = cartRepository;
        this.accountRepository = accountRepository;
        this.cartService = cartService;
    }


    /**
     * Xử lý tạo Order vs Orderdetail đồng thời.
     */

    public Order createOrder(OrderDtoRequest orderDtoRequest, String userId) {
        List<Cart> carts = cartRepository.getCartsByAccount_AccountId(userId);
        // 1 Tính tổng tiền dơn hàng, giá lấy trong bảng product.
        BigDecimal totalAmount = BigDecimal.ZERO;
        // 2 Create Order.
        Order order = new Order();
        order.setAccount(accountRepository.findById(userId).get());
        order.setOrderDate(new Date());
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
        Order savedOrder = orderRepository.save(order);
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