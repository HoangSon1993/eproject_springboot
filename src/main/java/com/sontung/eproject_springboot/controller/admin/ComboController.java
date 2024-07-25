package com.sontung.eproject_springboot.controller.admin;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sontung.eproject_springboot.dto.ComboDetailDTO;
import com.sontung.eproject_springboot.entity.Combo;
import com.sontung.eproject_springboot.service.ComboDetailService;
import com.sontung.eproject_springboot.service.ComboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/combo")
public class ComboController {
    @Autowired
    ComboService comboService;
    @Autowired
    ComboDetailService comboDetailService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public ComboController(ComboDetailService comboDetailService) {
        this.comboDetailService = comboDetailService;
    }
    @GetMapping("")
    public String getCombos(Model model){
        model.addAttribute("combos", comboService.getCombos());
        return "/admin/combo/index";
    }
    @GetMapping("/create")
    public String createCombo( Model model){
        model.addAttribute("products",  comboService.getProducts());
        return "/admin/combo/create";
    }
    @PostMapping("/createConfirm")
    public String createCombo(@RequestParam String comboName,
                              @RequestParam LocalDate startDate,
                              @RequestParam LocalDate endDate,
                              @RequestParam String productsJson,
                              @RequestParam BigDecimal finalAmount) {
        try {

            Combo combo =  new Combo();
            combo.setComboName(comboName);
            combo.setStartDate(startDate);
            combo.setEndDate(endDate);
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
            comboService.updateCombo(combo,totalAmount ,finalAmount);
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
