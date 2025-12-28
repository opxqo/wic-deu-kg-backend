package com.wic.edu.kg.controller;

import com.wic.edu.kg.common.ApiResponse;
import com.wic.edu.kg.service.DepartmentService;
import com.wic.edu.kg.vo.DepartmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学部资源控制器（RESTful）
 * 
 * RESTful 设计：
 * - GET /api/departments - 获取学部列表
 * - GET /api/departments/{id} - 获取学部详情
 */
@RestController
@RequestMapping("/api/departments")
@Tag(name = "学部资源", description = "学部公开接口")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "获取所有学部列表", description = "获取所有启用的学部及其辅导员信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功")
    })
    public ResponseEntity<ApiResponse<List<DepartmentVO>>> getAllDepartments() {
        List<DepartmentVO> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(ApiResponse.ok(departments));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取学部详情", description = "根据ID获取学部详细信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "学部不存在")
    })
    public ResponseEntity<ApiResponse<DepartmentVO>> getDepartmentById(
            @Parameter(description = "学部ID", required = true) @PathVariable Long id) {
        DepartmentVO department = departmentService.getDepartmentById(id);
        if (department == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "学部不存在", "/api/departments/" + id));
        }
        return ResponseEntity.ok(ApiResponse.ok(department));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "根据代码获取学部", description = "根据学部代码获取详细信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "学部不存在")
    })
    public ResponseEntity<ApiResponse<DepartmentVO>> getDepartmentByCode(
            @Parameter(description = "学部代码", required = true) @PathVariable String code) {
        DepartmentVO department = departmentService.getDepartmentByCode(code);
        if (department == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "学部不存在", "/api/departments/code/" + code));
        }
        return ResponseEntity.ok(ApiResponse.ok(department));
    }
}
