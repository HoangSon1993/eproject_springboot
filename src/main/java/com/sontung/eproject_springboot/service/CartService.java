package com.sontung.eproject_springboot.service;

import com.sontung.eproject_springboot.dto.CartDetailDTO;
import com.sontung.eproject_springboot.entity.*;
import com.sontung.eproject_springboot.exception.ProductNotFoundException;
import com.sontung.eproject_springboot.exception.UserNotFoundException;
import com.sontung.eproject_springboot.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final ICartRepository cartRepository;
    private final IComboRepository comboRepository;
    private final IAccountRepository accountRepository;
    private final IComboDetailRepository iComboDetailRepository;
    private final IProductRepository productRepository;

    public CartService(
            ICartRepository cartRepository,
            IComboRepository comboRepository,
            IAccountRepository accountRepository,
            IComboDetailRepository iComboDetailRepository,
            IProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.comboRepository = comboRepository;
        this.accountRepository = accountRepository;
        this.iComboDetailRepository = iComboDetailRepository;
        this.productRepository = productRepository;
    }


    // 07/08/2024 Tạo giỏ hàng cho product(do anh Sơn làm)

    /**
     * @Summary: Add product to Cart
     * @Description:
     **/
    public Cart addProductToCart(String userId, String productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Sản phẩm không tồn tại"));
        Account account = accountRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Người dùng không tồn tại"));
        Cart cartItem = cartRepository.findByProductIdAndAccount_AccountId(productId, userId);
        if (cartItem != null) {
            // Update quantity and amount
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // The product does not exist in the cart
            cartItem = new Cart();
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getPrice());
            cartItem.setAccount(account);
        }
        // Save change to database
        cartRepository.save(cartItem);
        return cartItem;
    }

    // 07/08/2024 Tạo giỏ hàng cho combo(do Tùng làm)
    public Cart addComboToCart(String userId, String comboId, int quantity) {

        Combo combo = comboRepository.findById(comboId)
                .orElseThrow(() -> new RuntimeException("Combo không tồn tại"));
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        Cart cart = cartRepository.findByAccountAndComboId(account, comboId);
        if (cart == null) {
            cart = new Cart();
            cart.setComboId(comboId);
            cart.setQuantity(quantity);
            cart.setAccount(account);
            cart.setPrice(combo.getFinalAmount()); // Tung xem lai vi da change amount -> price
            cartRepository.save(cart);
        } else {
            int newQuantity = cart.getQuantity() + quantity;
            cart.setQuantity(newQuantity);
            cartRepository.save(cart);
        }
        return cart;

    }

    /**
     * @Summary: Lấy ra danh sách item trong cart
     * @Description: Sử dụng cho 2 trường hợp
     * @Case1: getAll cartItem in Cart
     * required: userId, optional: cartItems
     * @Case2: getAll cartItem in Cart where cartId =
     * required: userId, cartItems
     **/
    public List<CartDetailDTO> getCarts(String userId, List<String> cartItems) {
        List<Cart> carts;

        // Kiểm tra xem có cartItems hay không
        if (cartItems == null || cartItems.isEmpty()) {
            // Trường hợp lấy tất cả cartItems của User
            carts = cartRepository.getCartsByAccount_AccountId(userId);
        } else {
            // Trường hợp có cartItems, lấy các cart của User ứng với cartItems
            carts = cartItems.stream()
                    .map(cartId -> cartRepository.findByCartIdAndAccount_AccountId(cartId, userId))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        List<CartDetailDTO> cartDetailDTOList = new ArrayList<>();
        for (Cart cart : carts) {
            CartDetailDTO cartDetailDTO = new CartDetailDTO();
            cartDetailDTO.setId(cart.getCartId());
            cartDetailDTO.setQuantity(cart.getQuantity());
            cartDetailDTO.setPrice(cart.getPrice());

            if (cart.getComboId() != null && cart.getProductId() == null) {
                // Case CartDetail is Combo
                Combo combo = comboRepository.findById(cart.getComboId()).orElseThrow(() -> new RuntimeException("Combo không tồn tại"));
                List<ComboDetail> comboDetailList = iComboDetailRepository.findAll().stream().filter(cd -> cd.getCombo() == combo).toList();

                cartDetailDTO.setName(combo.getComboName());
                cartDetailDTO.setImage(combo.getImage());
                cartDetailDTO.setComboDetails(comboDetailList);
            } else {
                // Case CartDetail is Product
                Product product = productRepository.findById(cart.getProductId()).orElseThrow(() -> new RuntimeException("Product không tồn tại"));
                cartDetailDTO.setName(product.getProductName());
                cartDetailDTO.setImage(product.getImage());
            }
            cartDetailDTOList.add(cartDetailDTO);
        }
        return cartDetailDTOList;
    }

    /**
     * @Summary: Tính tổng giá trị của giỏ hàng
     * @Description Giá của product được lấy từ bảng product
     * Được sử dụng khi người dùng checkout -> page order
     * Được dùng chung ở OrderService
     **/
    public BigDecimal getTotalAmount(String userId, List<String> checkedItems) {
        List<Cart> carts = cartRepository.getCartsByAccount_AccountId(userId);
        BigDecimal total = BigDecimal.ZERO;
        for (Cart cart : carts) {
            // Kiểm tra nếu cartItem được check
            if (checkedItems.contains(cart.getCartId())) {
                BigDecimal price = BigDecimal.ZERO;
                // Case Product
                if (cart.getProductId() != null && cart.getComboId() == null) {
                    BigDecimal productPrice = productRepository.getPriceByProductId(cart.getProductId());
                    price = productPrice.multiply(new BigDecimal(cart.getQuantity()));
                } else {
                    // Case Combo
                    BigDecimal comboPrice = comboRepository.getFinalAmountByComboId(cart.getComboId());
                    price = comboPrice.multiply(new BigDecimal(cart.getQuantity()));
                }
                total = total.add(price);
            }
        }
        return total;
    }

    /**
     * @Summary: Tính tổng số lượng sản phẩm trong giỏ hàng
     **/
    public int getTotalItem(String userId) {
        List<Cart> carts = cartRepository.getCartsByAccount_AccountId(userId);
        return carts.size();
    }

    /**
     * @Summary: Xóa sản phẩm ra khỏi giỏ hàng
     * @Description: Trong Cart, khi người dùng bấm vào icon remove item thì thực hiện việc xoá item ra khỏi Cart
     **/
    public void removeItemFromCart(String cartId) {
        try {
            cartRepository.deleteById(cartId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xoá sản phẩm ra khỏi giỏ hàng");
        }
    }

    /**
     * @Summary: Xóa CartItems trong Cart
     * @Description: Sau khi tạo Order vaf OrderDetals thi Xoá CartItems khỏi Cart.
     * @Param: List <String> cartItems
     * @Return: void.
     * @Exception:
     * **/
    public void removeCartItems(List<String> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return;
        }
        for (String cartItem : cartItems) {
            cartRepository.deleteById(cartItem);
        }
    }

    /**
     * @Summary: Câp nhật số lượng sản phẩm.
     **/
    public boolean updateQuantity(String cartId, Integer newQuantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại."));
        if (cart.getComboId() == null) {
            cart.setQuantity(newQuantity);
            Product product = productRepository.findById(cart.getProductId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
            cart.setPrice(product.getPrice());
        } else {
            // Case Combo
            cart.setQuantity(newQuantity);
            Combo combo = comboRepository.findById(cart.getComboId())
                    .orElseThrow(() -> new RuntimeException("Combo không tồn tại"));
            cart.setPrice(combo.getFinalAmount());
        }
        cartRepository.save(cart);
        return true;
    }

    /**
     * @Summary: Get All Cart Item with Account_ID and Cart_Id
     * @Description: The price of the product or combo is obtained from the Product or Combo table.
     **/
    public List<CartDetailDTO> getCartByIds(String userId, List<String> cartItems)
    {
        List<Cart> carts = new ArrayList<>();
        for (String cartId : cartItems) {
            Cart cart = cartRepository.findByCartIdAndAccount_AccountId(cartId, userId);
            if (cart != null) {
                carts.add(cart);
            }
        }
        List<CartDetailDTO> cartDetailDTOList = new ArrayList<>();
        for (Cart cart : carts) {
            CartDetailDTO cartDetailDTO = new CartDetailDTO();

            cartDetailDTO.setId(cart.getCartId());
            cartDetailDTO.setQuantity(cart.getQuantity());

            if (cart.getComboId() != null && cart.getProductId() == null) {
                // Case CartDetail is Combo
                Combo combo = comboRepository.findById(cart.getComboId()).orElseThrow(() -> new RuntimeException("Combo không tồn tại"));
                List<ComboDetail> comboDetailList = iComboDetailRepository.findAll().stream().filter(cd -> cd.getCombo() == combo).toList();

                cartDetailDTO.setName(combo.getComboName());
                cartDetailDTO.setPrice(combo.getFinalAmount());
                cartDetailDTO.setImage(combo.getImage());
                cartDetailDTO.setComboDetails(comboDetailList);
            } else {
                // Case CartDetail is Product
                Product product = productRepository.findById(cart.getProductId()).orElseThrow(() -> new RuntimeException("Product không tồn tại"));
                cartDetailDTO.setName(product.getProductName());
                cartDetailDTO.setPrice(product.getPrice());
                cartDetailDTO.setImage(product.getImage());
            }

            cartDetailDTOList.add(cartDetailDTO);
        }
        return cartDetailDTOList;
    }
}
