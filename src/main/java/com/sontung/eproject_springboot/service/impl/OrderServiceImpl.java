package com.sontung.eproject_springboot.service.impl;

import com.sontung.eproject_springboot.dto.OrderDetailDTO;
import com.sontung.eproject_springboot.dto.request.OrderDtoRequest;
import com.sontung.eproject_springboot.entity.*;
import com.sontung.eproject_springboot.enums.InvoiceStatus;
import com.sontung.eproject_springboot.enums.OrderStatus;
import com.sontung.eproject_springboot.repository.*;
import com.sontung.eproject_springboot.service.OrderDetailService;
import com.sontung.eproject_springboot.service.OrderService;
import com.sontung.eproject_springboot.util.VnPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.out;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderDetailService orderDetailService;

    private final IOrderRepository orderRepository;
    private final IOrderDetailRepository orderDetailRepository;
    private final IInvoiceRepository invoiceRepository;
    private final IInvoiceDetailRepository invoiceDetailRepository;
    private final IProductRepository productRepository;
    private final ICartRepository cartRepository;
    private final IAccountRepository accountRepository;
    private final IComboRepository comboRepository;

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
    }

    @Override
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
                orderDetailDTO.setCombo(comboRepository.findById((item.getCombo()).getComboId()).orElse(null));
                orderDetailDTOList.add(orderDetailDTO);
            }
        }
        return orderDetailDTOList;
    }
//    public List<ComboOrderDTO> getCombosOrder(String orderId){
//        return orderDetailService.getCombos(orderId);
//    }

    @Override
    public List<Order> getOrdersByPrice(int priceValue) {
        List<Order> orders = new ArrayList<>();

        switch (priceValue) {
            case 1: // Dưới 100k
                orders = orderRepository.findAll().stream()
                        .filter(i -> i.getTotalAmount().compareTo(new BigDecimal("100000")) < 0)
                        .collect(Collectors.toList());
                break;
            case 2: // 100k -> 200k
                orders = orderRepository.findAll().stream()
                        .filter(i -> i.getTotalAmount().compareTo(new BigDecimal("100000")) >= 0 &&
                                i.getTotalAmount().compareTo(new BigDecimal("200000")) <= 0)
                        .collect(Collectors.toList());
                break;
            case 3: // 200k -> 300k
                orders = orderRepository.findAll().stream()
                        .filter(i -> i.getTotalAmount().compareTo(new BigDecimal("200000")) > 0 &&
                                i.getTotalAmount().compareTo(new BigDecimal("300000")) <= 0)
                        .collect(Collectors.toList());
                break;
            case 4: // 300k -> 500k
                orders = orderRepository.findAll().stream()
                        .filter(i -> i.getTotalAmount().compareTo(new BigDecimal("300000")) > 0 &&
                                i.getTotalAmount().compareTo(new BigDecimal("500000")) <= 0)
                        .collect(Collectors.toList());
                break;
            case 5: // 500k Trở Lên
                orders = orderRepository.findAll().stream()
                        .filter(i -> i.getTotalAmount().compareTo(new BigDecimal("500000")) > 0)
                        .collect(Collectors.toList());
                break;
            default:
                throw new IllegalArgumentException("Invalid price range");
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByFilterDate(@DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate) {
        LocalDate filterLocalDate = filterDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return orderRepository.findAll().stream().filter(i -> i.getOrderDate().equals(filterLocalDate)).collect(Collectors.toList());
    }

    @Override
    public List<Order> getOrdersByPriceAndDate(int priceValue,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd") Date filterDate) {
        LocalDate filterLocalDate = filterDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return orderRepository.findAll().stream().filter(i -> {
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

    /**
     * @Summary: Creat Order.
     * @Description: Tạo Order và OrderDetails với những CartItems, quantity mà người dùng chọn.
     * @Param: OrderDtoRequest orderDtoRequest, String userId.
     * @Return: Order order.
     * @Exception:
     **/
    @Override
    public Order createOrder(OrderDtoRequest orderDtoRequest, String userId) {
        // Todo: Bằng cách nào đó carts == empty thì k cho tạo Order
        List<Cart> carts = cartRepository.getCartsByAccount_AccountId(userId);
        // 1 Tính tổng tiền dơn hàng, giá lấy trong bảng product.
        BigDecimal totalAmount = BigDecimal.ZERO;
        // 2 Create Order.
        Order order = Order.builder()
                .account(accountRepository.findById(userId).get())
                .orderDate(LocalDate.now())
                .status(OrderStatus.PENDING)
                .shippingAddress(orderDtoRequest.getShippAddress())
                .shippingPhone(orderDtoRequest.getShippingPhone())
                //.orderDetails(new ArrayList<>()) trong Order đã có @Buider.Default
                .build();

        for (Cart cart : carts) {
            if (orderDtoRequest.getCartItems().contains(cart.getCartId())) {
                BigDecimal price = BigDecimal.ZERO;
                OrderDetail.OrderDetailBuilder orderDetail = OrderDetail.builder()
                        .order(order)
                        .quantity(cart.getQuantity());
                if (cart.getProductId() != null && cart.getComboId() == null) {
                    // Case Product
                    //Todo: hanle product == null
                    Optional<Product> product = productRepository.findById(cart.getProductId());
                    BigDecimal productPrice = product.get().getPrice();
                    price = productPrice.multiply(BigDecimal.valueOf(cart.getQuantity()));
                    orderDetail.product(product.get());
                } else {
                    // Case Combo
                    //Todo: hanle combo == null
                    Optional<Combo> combo = comboRepository.findById(cart.getComboId());
                    BigDecimal comboPrice = combo.get().getFinalAmount();
                    price = comboPrice.multiply(BigDecimal.valueOf(cart.getQuantity()));
                    orderDetail.combo(combo.get());
                }
                totalAmount = totalAmount.add(price);

                // Create Order Detail
                orderDetail
                        .price(price)
                        .totalPrice(price.multiply(new BigDecimal(cart.getQuantity())));
                // add OrderDetail to Order.
                order.addOrderDetail(orderDetail.build());
            }
        }

        order.setTotalAmount(totalAmount);
        order.setCode(VnPayUtil.getCodeNumber(8));

        // 3 Save Order.
        orderRepository.save(order);
        return order;
    }

    /**
     * @Summary Create URI to call VNPay for payment processing
     * @Description Creates a URI to interact with the VNPay API for processing payments. This method generates a URI based on the provided order details and request/response context.
     * @Param order The order object containing payment details.
     * @Param req The HTTP request object providing context for the operation.
     * @Param resp The HTTP response object used for sending responses.
     * @Return A URI string used to initiate a payment request with VNPay.
     * @Exception UnsupportedEncodingException If an error occurs while encoding the URI.
     **/
    @Override
    public String getVnpay(Order order, HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = order.getTotalAmount().longValue();
        amount *= 100;
        //String bankCode = req.getParameter("bankCode");
        String bankCode = "NCB"; // Test với ngân hàng NCB vì thẻ cuả vnpay cung cấp là NCB
        String vnp_TxnRef = order.getCode(); // VnPayUtil.getRandomNumber da duoc goi ra luc tao order
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
//            Các mã loại hình thức thanh toán lựa chọn tại website-ứng dụng của merchant
//            vnp_BankCode=VNPAYQRThanh toán quét mã QR
//            vnp_BankCode=VNBANKThẻ ATM - Tài khoản ngân hàng nội địa
//            vnp_BankCode=INTCARDThẻ thanh toán quốc tế
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef); // co the su dung vnp_TxnRef de tao code cho Order
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

    /**
     * @Summary Xử lý kết quả của Vnpay trả về.
     * @Description: 1st: Xử lý checkSum xem giữ liệu trả về có đảm bảo hợp lệ hay không.
     * 2nd: check 'vnp_ResponseCode' == '00' là thanh toán thành công.
     * Sau đó kiểm tra 'code', 'total_amount' trong Order.
     * 3nd: Tạo Invoice and Invoice_detai.
     * @Param
     * @Return: void.
     * @Exception UnsupportedEncodingException
     **/
    @Override
    public String handleResult(HttpServletRequest request, String userId) throws UnsupportedEncodingException {
        // Get order from DB
        String vnpOrderInfo = request.getParameter("vnp_OrderInfo");
        String code = vnpOrderInfo.substring(vnpOrderInfo.length() - 8);
        Order order = orderRepository.findByCodeAndAccount_AccountId(code,userId);


        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
            String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }

        // Check checksum
        String signValue = VnPayUtil.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {

            boolean checkOrderId = (request.getParameter("vnp_TxnRef").equals(order.getCode()));
            // vnp_TxnRef exists in your database
            double amoutParam = Double.parseDouble(request.getParameter("vnp_Amount"));
            double totalAmount = order.getTotalAmount().doubleValue() * 100;
            final double EPSILON = 0.01;
            boolean checkAmount = Math.abs(amoutParam - totalAmount) < EPSILON;
            // vnp_Amount is valid (Check vnp_Amount VNPAY returns compared to the amount of the code (vnp_TxnRef) in the Your database).

            boolean checkOrderStatus = (order.getStatus() == OrderStatus.PENDING);
            // PaymnentStatus = 0 (pending)


            if (checkOrderId) {
                if (checkAmount) {
                    if (checkOrderStatus) {
                        if ("00".equals(request.getParameter("vnp_ResponseCode"))) {

                            //Here Code update PaymnentStatus = 1 into your Database
                            order.setStatus(OrderStatus.PAID);
                            orderRepository.save(order);

                            // Thanh toan thanh cong
                            // Create Invoice
                            Invoice invoice = Invoice.builder()
                                    .order(order)
                                    .totalAmount(order.getTotalAmount())
                                    .paymentDate(LocalDate.parse(request.getParameter("vnp_PayDate"), DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                                    .invoiceDate(LocalDate.now())
                                    .paymentStatus(InvoiceStatus.PAID)
                                    .paymentMethod("VNPay")
                                    .bankCode(request.getParameter("vnp_BankCode"))
                                    .transactionNo(request.getParameter("vnp_BankTranNo"))
                                    .cardType(request.getParameter("vnp_CardType"))
                                    .transactionStatus(request.getParameter("vnp_TransactionStatus"))
                                    .bankTransactionNo(request.getParameter("vnp_TransactionNo"))
                                  //  .invoiceDetails(new ArrayList<>()) // không cần phải có dòng này vì trong Invoice đã có @Buider.Default
                                    .build();

                            // Create OrderDetail
                            List<OrderDetail> orderDetails = order.getOrderDetails();
                            for (OrderDetail orderDetail : orderDetails) {
                                InvoiceDetail.InvoiceDetailBuilder invoiceDetail = InvoiceDetail.builder()
                                        .invoice(invoice)
                                        .quantity(orderDetail.getQuantity())
                                        .price(orderDetail.getPrice())
                                        .totalPrice(orderDetail.getTotalPrice());

                                if (orderDetail.getProduct() != null && orderDetail.getCombo() == null) {
                                    invoiceDetail.productId(orderDetail.getProduct().getProductId());
                                } else {
                                    invoiceDetail.comboId(orderDetail.getCombo().getComboId());
                                }
                                invoice.addInvoiceDetail(invoiceDetail.build());
                            }

                            // Save Invoice and InvoiceDetai to DB.
                            Invoice invoiceCreated = invoiceRepository.save(invoice);

                            // Tra ket qua ve cho nguoi dung
                        } else if ("24".equals(request.getParameter("vnp_ResponseCode"))) {
                            // vnp_ResponseCode == 24; Người dùng hủy thanh toán,
                            order.setStatus(OrderStatus.CANCELED);
                            orderRepository.save(order);
                            return "Giao dịch đã bị hủy, bạn có thể thử lại.";
                        } else {
                            // Here Code update PaymnentStatus = 2 into your Database
                        }
                        // Thanh cong thi show thong bao nay.
                        out.print("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
                        return "Xác nhận thành công.";
                    } else {
                        out.print("{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}");
                        return "Đơn hàng đã được xác nhận.";
                    }
                } else {
                    out.print("{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}");
                    return "Số tiền không hợp lệ.";
                }
            } else {
                out.print("{\"RspCode\":\"01\",\"Message\":\"Order not Found\"}");
                return "Đơn hàng không được tìm thấy.";
            }
        } else {
            out.print("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
            return "Tổng kiểm tra không hợp lệ.";
        }
    }

    /**
     * @Summary: Lấy Order với code
     **/
    @Override
    public Order findByCodeAndAccountId(String accountId, String code) {
        return orderRepository.findByCodeAndAccount_AccountId(code, accountId);
    }
}