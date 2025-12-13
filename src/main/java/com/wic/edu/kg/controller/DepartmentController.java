package com.wic.edu.kg.controller;

import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.service.DepartmentService;
import com.wic.edu.kg.vo.DepartmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学部公开 API
 */
@RestController
@RequestMapping("/api/departments")
@Tag(name = "学部管理", description = "学部公开接口")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "获取所有学部列表", description = "获取所有启用的学部及其辅导员信息")
    public Result<List<DepartmentVO>> getAllDepartments() {
        List<DepartmentVO> departments = departmentService.getAllDepartments();
        return Result.success(departments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取学部详情", description = "根据ID获取学部详细信息")
    public Result<DepartmentVO> getDepartmentById(
            @Parameter(description = "学部ID") @PathVariable Long id) {
        DepartmentVO department = departmentService.getDepartmentById(id);
        if (department == null) {
            return Result.error("学部不存在");
        }
        return Result.success(department);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "根据代码获取学部", description = "根据学部代码获取详细信息")
    public Result<DepartmentVO> getDepartmentByCode(
            @Parameter(description = "学部代码") @PathVariable String code) {
        DepartmentVO department = departmentService.getDepartmentByCode(code);
        if (department == null) {
            return Result.error("学部不存在");
        }
        return Result.success(department);
    }
}
