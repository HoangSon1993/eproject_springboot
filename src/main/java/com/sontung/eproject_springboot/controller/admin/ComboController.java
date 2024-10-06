package com.sontung.eproject_springboot.controller.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sontung.eproject_springboot.dto.ComboDTO;
import com.sontung.eproject_springboot.dto.ComboDetailDTO;
import com.sontung.eproject_springboot.entity.Combo;
import com.sontung.eproject_springboot.entity.OrderDetail;
import com.sontung.eproject_springboot.service.ComboService;
import com.sontung.eproject_springboot.service.S3Service;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/combo")
@RequiredArgsConstructor
public class ComboController {
    private final S3Service s3Service;
    private final ComboService comboService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public String getCombos(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate filterDate,
                            @RequestParam(defaultValue = "1") int page,
                            @Min(5) @RequestParam(defaultValue = "9") int size,
                            Model model) {
        model.addAttribute("currentPage", page);
        if (page < 1) {
            page = 1;
        }
        model.addAttribute("size", size);
        if (filterDate == null) {
            Page<Combo> comboList = comboService.getCombos(page, size);
            model.addAttribute("combos", comboList);
            long totalItems = comboService.countAdminComBos();
            int totalPages = (int) (Math.ceil((double) totalItems / size));
            model.addAttribute("totalPages", totalPages);
        } else {
            Page<OrderDetail> orderDetails = comboService.getOrdersByDate(filterDate, page, size);
            model.addAttribute("filterDate", filterDate);
            model.addAttribute("orders", orderDetails);
            //long totalItems = comboService.countOrderDetailInComboMgr(filterDate);
            long totalItems = orderDetails.getTotalElements();
            int totalPages = (int) (Math.ceil((double) totalItems / size));
            model.addAttribute("totalPages", totalPages);
            System.out.println(filterDate);
        }
        return "/admin/combo/index";
    }

    @GetMapping("/expired")
    public String getExpiringCombo(Model model) {
        model.addAttribute("expiringCombos", comboService.getExpiringCombos());
        return "/admin/combo/expired";
    }

    @GetMapping("/create")
    public String createCombo(Model model) {
        model.addAttribute("products", comboService.getProducts());
        model.addAttribute("combo", new ComboDTO());
        return "/admin/combo/create";
    }

    @Transactional
    @PostMapping("/createConfirm")
    public String createCombo(@ModelAttribute ComboDTO comboDTO,
                              @RequestParam("file") MultipartFile image,
                              RedirectAttributes redirectAttributes) {
        try {
            Combo combo = new Combo();
            combo.setComboName(comboDTO.getComboName());
            combo.setDescription(comboDTO.getDescription());
            combo.setStartDate(comboDTO.getStartDate());
            combo.setEndDate(comboDTO.getEndDate());

            if (!image.isEmpty()) {
                try {
                    // TODO: 30/7/24 fix function upload File
                    String uniqueFilename = s3Service.generateUniqueFilename(image);
                    s3Service.uploadFile(image, uniqueFilename);
                    combo.setImage(uniqueFilename);
                    comboService.createCombo(combo);
                    redirectAttributes.addFlashAttribute("message", "File Successfully Upload");
                    // Chuyển đổi JSON thành danh sách các đối tượng ComboDetailDTO
                    List<ComboDetailDTO> comboDetailDTOS = objectMapper.readValue(comboDTO.getProductsJson(), new TypeReference<List<ComboDetailDTO>>() {
                    });
                    // Tạo DTO mới và xử lý dữ liệu bằng service
                    BigDecimal totalAmount = BigDecimal.ZERO;
                    for (ComboDetailDTO comboDetailDTO : comboDetailDTOS) {
                        comboDetailDTO.setComboId(combo.getComboId());
                        comboService.createComboDetail(comboDetailDTO);
                        totalAmount = totalAmount.add(comboDetailDTO.getUniquePrice().multiply(BigDecimal.valueOf(comboDetailDTO.getQuantity())));
                    }
                    comboService.updateCombo(combo, totalAmount, comboDTO.getFinalAmount());
                    return "redirect:/admin/combo";
                } catch (AwsServiceException e) {
                    redirectAttributes.addFlashAttribute("error", "AWS Service Exception: " + e.getMessage());
                } catch (SdkClientException e) {
                    redirectAttributes.addFlashAttribute("error", "SDK Client Exception: " + e.getMessage());
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("error", "IOException: " + e.getMessage());
                }
                return "error";
            }
            // Case image is Null
            return "redirect:/admin/combo";

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/detail")
    public String getCombo(@RequestParam String comboId, Model model) {
        model.addAttribute("combo", comboService.getCombo(comboId));
        model.addAttribute("comboDetails", comboService.getComboDetails(comboId));
        return "/admin/combo/detail";
    }

    @PostMapping("/delete")
    public String deleteCombo(@RequestParam String comboId) {
        comboService.removeCombo(comboId);
        return "redirect:/admin/combo";
    }
}
