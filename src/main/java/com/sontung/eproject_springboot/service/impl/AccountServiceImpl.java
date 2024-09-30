package com.sontung.eproject_springboot.service.impl;

import com.sontung.eproject_springboot.dto.RegisterDTO;
import com.sontung.eproject_springboot.dto.UpdatedAccountDTO;
import com.sontung.eproject_springboot.entity.Account;
import com.sontung.eproject_springboot.entity.Role;
import com.sontung.eproject_springboot.repository.IAccountRepository;
import com.sontung.eproject_springboot.repository.IRoleRepository;
import com.sontung.eproject_springboot.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final PasswordEncoder passwordEncoder;
    private final IAccountRepository accountRepository;
    private final IRoleRepository roleRepository;

    /**
     * @Summary: Tìm kiếm tài khoản trong cơ sở dữ liệu dựa trên tên người dùng hoặc email.
     * @Description: Nếu tìm thấy, trả về đối tượng UserDetails chứa tên đăng nhập, mật khẩu và quyền hạn (roles) của người dùng.
     * Nếu không tìm thấy, ném ra ngoại lệ UsernameNotFoundException.
     * @Exception: UsernameNotFoundException.
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Account account = accountRepository.findByUserNameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not exits by Username or Email"));
        Set<GrantedAuthority> authorities = account.getRoles().stream()
                .map((role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName())))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                usernameOrEmail,
                account.getPassword(),
                authorities
        );
    }

    /**
     * @Summary: Tìm kiếm tài khoản dựa trên tên người dùng hoặc email.
     * @Description: Nếu không tìm thấy, ném ngoại lệ UsernameNotFoundException.
     * @Exception: UsernameNotFoundException.
     */
    @Override
    public Account findByUserNameOrEmail(String username) throws UsernameNotFoundException {
        return accountRepository.findByUserNameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not exits"));
    }


    /**
     * @Summary: Tạo một tài khoản mới
     * @Description: Kiểm tra tính hợp lệ của mật khẩu và mật khẩu xác nhận.
     * Kiểm tra xem tên người dùng và email có tồn tại chưa.
     * Nếu hợp lệ, mã hóa mật khẩu, thiết lập thông tin tài khoản, và thêm quyền USER mặc định cho tài khoản mới.
     * Trả về mã lỗi tương ứng nếu có lỗi xảy ra trong quá trình tạo tài khoản (như tên người dùng hoặc email đã tồn tại).
     * Exception: RuntimeException('User not found').
     */
    @Override
    public int createAccount(RegisterDTO registerDTO) {
        if (!registerDTO.getPassword().equals(registerDTO.getPasswordConfirm())) {
            ;
            return 0;
        } else {
            if (accountRepository.existsByUserName(registerDTO.getUserName())) {
                return -1;
            } else {
                if (accountRepository.existsByEmail(registerDTO.getEmail())) {
                    return -2;
                } else {
                    if (registerDTO.getUserName().contains("admin")) {
                        return -3;
                    } else {
                        Account account = new Account();
                        account.setUserName(registerDTO.getUserName());
                        account.setPhone(registerDTO.getPhone());
                        account.setFullName(registerDTO.getFullName());
                        account.setEmail(registerDTO.getEmail());
                        account.setDob(registerDTO.getDob());
                        account.setAvatar(registerDTO.getAvatar());
                        account.setAddress(registerDTO.getAddress());
                        account.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
                        var rolesOptional = roleRepository.findByRoleName("USER");
                        if (rolesOptional.isPresent()) {
                            Set<Role> roles = new HashSet<>();
                            roles.add(rolesOptional.get());
                            account.setRoles(roles);
                        } else {
                            throw new RuntimeException("Role USER not found");
                        }
                        accountRepository.save(account);
                    }
                }
            }
        }
        return 1;
    }

    /**
     * @Summary: Kiểm tra xem tên người dùng có tồn tại trong cơ sở dữ liệu hay không.
     */
    @Override
    public boolean exitUsername(String username) {
        return accountRepository.existsByUserName(username);
    }


    /**
     * @Summary: Đăng nhập tự động bằng cách xác thực thông tin đăng nhập:
     * @Description: Tạo đối tượng UsernamePasswordAuthenticationToken từ tên người dùng và mật khẩu.
     * Sử dụng AuthenticationManager để xác thực thông tin.
     * Sau khi xác thực thành công, thiết lập trạng thái đăng nhập trong SecurityContextHolder.
     */
    @Override
    public void autoLogin(String username, String password, AuthenticationManager authenticationManager) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * @Summary: Hàm lấy thông báo lỗi dựa trên mã lỗi
     * @Description: Trả về thông báo lỗi tương ứng dựa trên mã lỗi nhận được từ quá trình tạo tài khoản.
     */
    @Override
    public String getErrorMessage(int result) {
        switch (result) {
            case 0:
                return "Mật khẩu và mật khẩu xác nhận không khớp.";
            case -1:
                return "Tên đăng nhập đã tồn tại.";
            case -2:
                return "Email đã tồn tại.";
            case -3:
                return "Tên đăng nhập không được chứa từ 'admin'.";
            default:
                return "Đã xảy ra lỗi không xác định.";
        }
    }

    /**
     * @Summary: Cập nhật thông tin tài khoản (email, số điện thoại, địa chỉ, ngày sinh, tên đầy đủ, ảnh đại diện) cho người dùng dựa trên dữ liệu mới.
     */
    @Override
    public Account updateAccount(UpdatedAccountDTO accountDTO) {
        Account updateAccount = accountRepository.findByUserNameOrEmail(accountDTO.getUserName(), accountDTO.getEmail()).orElseThrow();
        updateAccount.setEmail(accountDTO.getEmail());
        updateAccount.setPhone(accountDTO.getPhone());
        updateAccount.setAddress(accountDTO.getAddress());
        updateAccount.setDob(accountDTO.getDob());
        updateAccount.setFullName(accountDTO.getFullName());
        updateAccount.setAvatar(accountDTO.getAvatar());
        return accountRepository.save(updateAccount);
    }

    @Override
    public int changePassword(String password, String newPassword, String newPasswordConfirm){
        return 1;
    }
}
