package com.sontung.eproject_springboot.config;

import com.sontung.eproject_springboot.entity.Account;
import com.sontung.eproject_springboot.service.AccountService;
import com.sontung.eproject_springboot.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    private final AccountService accountService;
    private final CartService cartService;

    @Value("${aws.s3.bucket.url}")
    String s3BucketUrl;

    /**
     * @Summary:
     * @Description:
     * @Param:
     * @Return:
     **/
    @ModelAttribute("s3BucketUrl")
    public String s3BucketUrl() {
        return s3BucketUrl;
    }

    /**
     * @Summary: Đếm số item có trong Cart.
     * @Description: Dùng để hiển thị số lượng item ở icon Cart.
     **/
    @ModelAttribute("cartItemCount")
    public int getCartItemCount(
            @ModelAttribute("loggedInUser") Account account
    ) {
        if (account != null) {
            return cartService.getTotalItem(account.getAccountId());
        }
        return 0;
    }

    @ModelAttribute("loggedInUser")
    public Account addUserToModel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            Account account = accountService.findByUserNameOrEmail(username);
            return account;
            //return username;
        }
        return null;
    }
}