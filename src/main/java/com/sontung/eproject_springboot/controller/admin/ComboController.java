package com.sontung.eproject_springboot.controller.admin;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sontung.eproject_springboot.dto.ComboDetailDTO;
import com.sontung.eproject_springboot.entity.Combo;
import com.sontung.eproject_springboot.service.ComboDetailService;
import com.sontung.eproject_springboot.service.ComboService;
import com.sontung.eproject_springboot.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class ComboController {
    private final S3Service s3Service;
    @Autowired
    ComboService comboService;
    @Autowired
    ComboDetailService comboDetailService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ComboController(ComboDetailService comboDetailService, S3Service s3Service) {
        this.comboDetailService = comboDetailService;
        this.s3Service = s3Service;
    }

    @Value("${aws.s3.bucket.url}")
    String s3BucketUrl;

    @ModelAttribute("s3BucketUrl")
    public String s3BucketUrl() {
        return s3BucketUrl;
    }

    // TODO: 29/07/2024  
    @GetMapping("")
    public String getCombos(Model model){
        model.addAttribute("combos", comboService.getCombos());
        return "/admin/combo/index";
    }
    @GetMapping("/create")
    public String createCombo( Model model){
        model.addAttribute("products", comboService.getProducts());
        return "/admin/combo/create";
    }
    @Transactional
    @PostMapping("/createConfirm")
    public String createCombo(@RequestParam String comboName,
                              @RequestParam LocalDate startDate,
                              @RequestParam LocalDate endDate,
                              @RequestParam String productsJson,
                              @RequestParam BigDecimal finalAmount,
                              @RequestParam("file") MultipartFile image,
                              RedirectAttributes redirectAttributes) {
        try {
            Combo combo =  new Combo();
            combo.setComboName(comboName);
            combo.setStartDate(startDate);
            combo.setEndDate(endDate);

            if (!image.isEmpty()) {
                try {
                    // TODO: 30/7/24 fix function upload File
                    String uniqueFilename = s3Service.generateUniqueFilename(image);

                    s3Service.uploadFile(image, uniqueFilename);

                    combo.setImage(uniqueFilename);

                    comboService.createCombo(combo);
                    redirectAttributes.addFlashAttribute("message", "File Successfully Upload");
                    Combo createdCombo = comboService.createCombo(combo);
                    // Chuyển đổi JSON thành danh sách các đối tượng ComboDetailDTO
                    List<ComboDetailDTO> comboDetailDTOS = objectMapper.readValue(productsJson, new TypeReference<List<ComboDetailDTO>>() {});
                    // Tạo DTO mới và xử lý dữ liệu bằng service
                    BigDecimal totalAmount = BigDecimal.ZERO;
                    for (ComboDetailDTO comboDetailDTO : comboDetailDTOS) {
                        comboDetailDTO.setComboId(createdCombo.getComboId());
                        comboDetailService.createComboDetail(comboDetailDTO);
                        totalAmount = totalAmount.add(comboDetailDTO.getUniquePrice().multiply(BigDecimal.valueOf(comboDetailDTO.getQuantity())));
                    }
                    comboService.updateCombo(combo, totalAmount, finalAmount);
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
    public String getCombo(@RequestParam String comboId, Model model){
        model.addAttribute("combo", comboService.getCombo(comboId));
        model.addAttribute("comboDetails", comboDetailService.getComboDetails(comboId));
        return "/admin/combo/detail";
    }

    @PostMapping("/delete")
    public String deleteCombo(@RequestParam String comboId){
        comboService.removeCombo(comboId);
        return "redirect:/admin/combo";
    }
}
