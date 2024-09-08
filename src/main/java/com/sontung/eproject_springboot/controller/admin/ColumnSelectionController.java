package com.sontung.eproject_springboot.controller.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ColumnSelectionController {
    private final HttpSession session;

    @PostMapping("/saveColumnSelection")
    public ResponseEntity<?> saveColumnSelection(@RequestParam List<String> columns) {
        session.setAttribute("selectedColumns", columns);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getColumnSelection")
    public ResponseEntity<List<String>> getColumnSelection() {
        List<String> selectedColumns = (List<String>) session.getAttribute("selectedColumns");
        if (selectedColumns == null || selectedColumns.isEmpty()) {
            // Nếu không có lựa chọn nào được lưu, trả về các cột mặc định
            selectedColumns = List.of("image", "productName", "price", "category", "status", "createdDate", "updatedDate", "actions");
        }
        return ResponseEntity.ok(selectedColumns);
    }
}
