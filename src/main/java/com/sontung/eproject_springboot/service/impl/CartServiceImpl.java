package com.sontung.eproject_springboot.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sontung.eproject_springboot.dto.CartDetailDTO;
import com.sontung.eproject_springboot.entity.*;
import com.sontung.eproject_springboot.exception.PriceChangedException;
import com.sontung.eproject_springboot.exception.ProductNotFoundException;
import com.sontung.eproject_springboot.exception.UserNotFoundException;
import com.sontung.eproject_springboot.repository.*;
import com.sontung.eproject_springboot.service.CartService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ICartRepository cartRepository;
    private final IComboRepository comboRepository;
    private final IAccountRepository accountRepository;
    private final IComboDetailRepository iComboDetailRepository;
    private final IProductRepository productRepository;

    // 07/08/2024 Tạo giỏ hàng cho product(do anh Sơn làm)

    /**
     * @Summary: Add product to Cart
     * @Description:
     **/
    @Override
    public Cart addProductToCart(String userId, String productId, int quantity) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Sản phẩm không tồn tại"));
        Account account = accountRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Người dùng không tồn tại"));
        Cart cartItem = cartRepository.findByAccountIdAndProductIdOrComboId(userId, productId, null);
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
    @Override
    public Cart addComboToCart(String userId, String comboId, int quantity) {

        Combo combo = comboRepository.findById(comboId).orElseThrow(() -> new RuntimeException("Combo không tồn tại"));
        Account account =
                accountRepository.findById(userId).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        Cart cart = cartRepository.findByAccountIdAndProductIdOrComboId(userId, null, comboId);
        if (cart == null) {
            cart = new Cart();
            cart.setComboId(comboId);
            cart.setQuantity(quantity);
            cart.setAccount(account);
            cart.setPrice(combo.getFinalAmount());
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
    @Override
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
            CartDetailDTO.CartDetailDTOBuilder cartDetailDTOBuilder = CartDetailDTO.builder();

            cartDetailDTOBuilder.id(cart.getCartId());
            cartDetailDTOBuilder.quantity(cart.getQuantity());
            cartDetailDTOBuilder.price(cart.getPrice()); // Lưu giá cũ từ Cart.

            BigDecimal currentPrice;

            if (cart.getComboId() != null && cart.getProductId() == null) {
                // Case CartDetail is Combo
                Combo combo = comboRepository
                        .findById(cart.getComboId())
                        .orElseThrow(() -> new RuntimeException("Combo không tồn tại"));
                currentPrice = combo.getFinalAmount(); // Lấy giá hiện tại từ bảng combo.
                List<ComboDetail> comboDetailList = iComboDetailRepository.findAll().stream()
                        .filter(cd -> cd.getCombo() == combo)
                        .toList();

                cartDetailDTOBuilder.name(combo.getComboName());
                cartDetailDTOBuilder.image(combo.getImage());
                cartDetailDTOBuilder.comboDetails(comboDetailList);
            } else {
                // Case CartDetail is Product
                Product product = productRepository
                        .findById(cart.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product không tồn tại"));
                currentPrice = product.getPrice();
                cartDetailDTOBuilder.name(product.getProductName());
                cartDetailDTOBuilder.image(product.getImage());
            }

            // Cập nhật giá hiện tại vào DTO
            cartDetailDTOBuilder.currentPrice(currentPrice);
            cartDetailDTOList.add(cartDetailDTOBuilder.build());
        }
        return cartDetailDTOList;
    }

    /**
     * @Summary: Tính tổng giá trị của giỏ hàng
     * @Description Giá của product được lấy từ bảng product
     * Được sử dụng khi người dùng checkout -> page order
     * Được dùng chung ở OrderService
     **/
    @Override
    public BigDecimal getTotalAmount(List<CartDetailDTO> cartDetailDTOList) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        boolean priceChanged = false;

        for (CartDetailDTO cartDetailDTO : cartDetailDTOList) {
            // Kiểm tra nếu giá thay đổi.
            if (cartDetailDTO.getPrice().compareTo(cartDetailDTO.getCurrentPrice()) != 0) {
                priceChanged = true;
                updateCartPrices(cartDetailDTO);
            }
            BigDecimal amount = cartDetailDTO.getCurrentPrice().multiply(new BigDecimal(cartDetailDTO.getQuantity()));
            totalAmount = totalAmount.add(amount);
        }
        if (priceChanged) {
            throw new PriceChangedException("Giá sản phẩm đã thay đổi giá bán.");
        }
        return totalAmount;
    }

    private void updateCartPrices(CartDetailDTO cartDetailDTO) {
        Cart cart = cartRepository.findById(cartDetailDTO.getId()).orElse(null);
        if (cart != null) {
            if (cart.getProductId() != null) {
                Product product =
                        productRepository.findById(cart.getProductId()).orElse(null);
                cart.setPrice(product.getPrice());
            } else {
                Combo combo = comboRepository.findById(cart.getComboId()).orElse(null);
                cart.setPrice(combo.getFinalAmount());
            }
            cartRepository.save(cart);
        }
    }

    /**
     * @Summary: Tính tổng số lượng sản phẩm trong giỏ hàng
     **/
    @Override
    public int getTotalItem(String userId) {
        return cartRepository.countCartsByAccount_AccountId(userId);
    }

    /**
     * @Summary: Xóa sản phẩm ra khỏi giỏ hàng
     * @Description: Trong Cart, khi người dùng bấm vào icon remove item thì thực hiện việc xoá item ra khỏi Cart
     **/
    @Override
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
     **/
    @Override
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
    @Override
    public boolean updateQuantity(String cartId, Integer newQuantity) {
        if (newQuantity == null) {
            throw new RuntimeException("Số lượng không hợp lệ.");
        }
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại."));
        if (cart.getComboId() == null) {
            cart.setQuantity(newQuantity);
            Product product = productRepository
                    .findById(cart.getProductId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
            cart.setPrice(product.getPrice());
        } else {
            // Case Combo
            cart.setQuantity(newQuantity);
            Combo combo = comboRepository
                    .findById(cart.getComboId())
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
    @Override
    public List<CartDetailDTO> getCartByIds(String userId, List<String> cartItems) {
        List<Cart> carts = new ArrayList<>();
        for (String cartId : cartItems) {
            Cart cart = cartRepository.findByCartIdAndAccount_AccountId(cartId, userId);
            if (cart != null) {
                carts.add(cart);
            }
        }
        List<CartDetailDTO> cartDetailDTOList = new ArrayList<>();
        for (Cart cart : carts) {
            CartDetailDTO.CartDetailDTOBuilder cartDetailDTOBuilder = CartDetailDTO.builder();

            cartDetailDTOBuilder.id(cart.getCartId());
            cartDetailDTOBuilder.quantity(cart.getQuantity());
            cartDetailDTOBuilder.price(cart.getPrice()); // Lưu giá cũ từ Cart.

            BigDecimal currentPrice;

            if (cart.getComboId() != null && cart.getProductId() == null) {
                // Case CartDetail is Combo
                Combo combo = comboRepository
                        .findById(cart.getComboId())
                        .orElseThrow(() -> new RuntimeException("Combo không tồn tại"));
                currentPrice = combo.getFinalAmount();
                List<ComboDetail> comboDetailList = iComboDetailRepository.findAll().stream()
                        .filter(cd -> cd.getCombo() == combo)
                        .toList();

                cartDetailDTOBuilder.name(combo.getComboName());
                //                cartDetailDTOBuilder.price(combo.getFinalAmount());
                cartDetailDTOBuilder.image(combo.getImage());
                cartDetailDTOBuilder.comboDetails(comboDetailList);
            } else {
                // Case CartDetail is Product
                Product product = productRepository
                        .findById(cart.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product không tồn tại"));
                currentPrice = product.getPrice();
                cartDetailDTOBuilder.name(product.getProductName());
                //                cartDetailDTOBuilder.price(product.getPrice());
                cartDetailDTOBuilder.image(product.getImage());
            }
            // Cập nhật giá hiện tại vào DTO.
            cartDetailDTOBuilder.currentPrice(currentPrice);
            cartDetailDTOList.add(cartDetailDTOBuilder.build());
        }
        return cartDetailDTOList;
    }

    /**
     * @Summary: Add item to cart
     * @Description: Xử lý cho cả 2 th combo và product
     * @Param String userId
     * @Param String comboId
     * @Param String productId
     * @Param int quantity
     * @Return Cart
     * @Exceiption ProductNotFoundException
     * @Exceiption RuntimeException
     **/
    @Override
    public Cart addItemToCart(String accountId, String comboId, String productId, int quantity) {
        Account currentAccount = accountRepository
                .findById(accountId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        Product currentProduct = null;
        Cart cart = null;
        if (productId != null) {
            currentProduct = productRepository
                    .findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Sản phẩm không tồn tại"));
            cart = cartRepository.findByProductIdAndAccount_AccountId(productId, accountId);
        }
        Combo currentCombo = null;
        if (comboId != null) {
            currentCombo = comboRepository
                    .findById(comboId)
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
            cart = cartRepository.findByAccountAndComboId(currentAccount, comboId);
        }

        if (cart == null) {
            cart = new Cart();
            cart.setAccount(currentAccount);
            cart.setComboId(comboId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setPrice(comboId != null ? currentCombo.getFinalAmount() : currentProduct.getPrice());

            cartRepository.save(cart);
        } else {
            int newQuantity = quantity + cart.getQuantity();
            cart.setQuantity(newQuantity);
            cartRepository.save(cart);
        }
        return cart;
    }
}
