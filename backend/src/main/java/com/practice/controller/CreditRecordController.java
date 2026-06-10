package com.practice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.practice.common.Result;
import com.practice.dto.CreditRecordDTO;
import com.practice.service.CreditRecordService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/credit")
@CrossOrigin
public class CreditRecordController {

    @Autowired
    private CreditRecordService creditRecordService;

    @GetMapping("/list")
    public Result<Page<CreditRecordDTO>> getCreditRecordList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) BigDecimal minCreditPoints,
            @RequestParam(required = false) BigDecimal maxCreditPoints) {
        Page<CreditRecordDTO> recordPage = creditRecordService.getCreditRecordList(page, size, studentId, userId, academicYear, semester, minCreditPoints, maxCreditPoints);
        return Result.success(recordPage);
    }

    @GetMapping("/total/{studentId}")
    public Result<Map<String, BigDecimal>> getTotalCredits(@PathVariable Long studentId) {
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("totalHours", creditRecordService.getTotalCreditHours(studentId));
        result.put("totalPoints", creditRecordService.getTotalCreditPoints(studentId));
        return Result.success(result);
    }

    @GetMapping("/export/student/{studentId}")
    public void exportStudentCreditRecord(@PathVariable Long studentId, HttpServletResponse response) throws IOException {
        List<CreditRecordDTO> records = creditRecordService.getAllCreditRecordsByStudentId(studentId);
        exportCreditRecordsToExcel(records, "个人学分证明", response);
    }

    @GetMapping("/export/contacts/{teacherId}")
    public void exportContactsCreditRecords(@PathVariable Long teacherId, HttpServletResponse response) throws IOException {
        List<CreditRecordDTO> records = creditRecordService.getAllCreditRecordsByTeacherId(teacherId);
        exportCreditRecordsToExcel(records, "联系人学分详情", response);
    }

    @GetMapping("/export/all")
    public void exportAllCreditRecords(HttpServletResponse response) throws IOException {
        List<CreditRecordDTO> records = creditRecordService.getAllCreditRecords();
        exportCreditRecordsToExcel(records, "所有学生学分详情", response);
    }

    @GetMapping("/test")
    public Result<Map<String, Object>> test() {
        Map<String, Object> result = new HashMap<>();
        
        // 查询学生刘海鸿（ID: 7）的学分记录
        List<CreditRecordDTO> records1 = creditRecordService.getAllCreditRecordsByStudentId(7L);
        result.put("student7Records", records1);
        
        // 查询学生陈亮新（ID: 10）的学分记录
        List<CreditRecordDTO> records2 = creditRecordService.getAllCreditRecordsByStudentId(10L);
        result.put("student10Records", records2);
        
        // 查询教师刘海鸿（ID: 8）的联系人学分记录
        List<CreditRecordDTO> records3 = creditRecordService.getAllCreditRecordsByTeacherId(8L);
        result.put("teacher8Records", records3);
        
        // 查询所有学分记录
        List<CreditRecordDTO> records4 = creditRecordService.getAllCreditRecords();
        result.put("allRecords", records4);
        
        return Result.success(result);
    }

    private void exportCreditRecordsToExcel(List<CreditRecordDTO> records, String sheetName, HttpServletResponse response) throws IOException {
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"学号", "姓名", "活动名称", "学时", "学分", "学年", "学期", "记录时间"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            
            // 设置标题样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            cell.setCellStyle(headerStyle);
        }

        // 填充数据
        int rowNum = 1;
        for (CreditRecordDTO record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getStudentNumber() != null ? record.getStudentNumber() : "");
            row.createCell(1).setCellValue(record.getStudentName() != null ? record.getStudentName() : "");
            row.createCell(2).setCellValue(record.getActivityTitle() != null ? record.getActivityTitle() : "");
            row.createCell(3).setCellValue(record.getCreditHours() != null ? record.getCreditHours().doubleValue() : 0);
            row.createCell(4).setCellValue(record.getCreditPoints() != null ? record.getCreditPoints().doubleValue() : 0);
            row.createCell(5).setCellValue(record.getAcademicYear() != null ? record.getAcademicYear() : "");
            row.createCell(6).setCellValue(record.getSemester() != null ? record.getSemester() : "");
            
            // 格式化时间
            String createTime = "";
            if (record.getCreateTime() != null) {
                createTime = record.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            row.createCell(7).setCellValue(createTime);
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = URLEncoder.encode(sheetName + ".xlsx", "UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        // 写入响应流
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}