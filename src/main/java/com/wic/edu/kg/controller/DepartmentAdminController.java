package com.wic.edu.kg.controller;

import com.wic.edu.kg.annotation.RequireRole;
import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.entity.Department;
import com.wic.edu.kg.entity.DepartmentCounselor;
import com.wic.edu.kg.enums.UserRole;
import com.wic.edu.kg.service.DepartmentCounselorService;
import com.wic.edu.kg.service.DepartmentService;
import com.wic.edu.kg.vo.DepartmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学部后台管理 API
 */
@RestController
@RequestMapping("/api/admin/departments")
@Tag(name = "学部后台管理", description = "学部管理接口（需要管理员权限）")
@RequireRole(UserRole.ADMIN)
public class DepartmentAdminController {

    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private DepartmentCounselorService counselorService;

    // ==================== 学部管理 ====================

    @GetMapping
    @Operation(summary = "获取所有学部（含禁用）", description = "获取所有学部列表，包括禁用的")
    public Result<List<Department>> getAllDepartmentsAdmin() {
        List<Department> departments = departmentService.list();
        return Result.success(departments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取学部详情", description = "获取单个学部完整信息")
    public Result<DepartmentVO> getDepartment(
            @Parameter(description = "学部ID") @PathVariable Long id) {
        DepartmentVO department = departmentService.getDepartmentById(id);
        if (department == null) {
            return Result.error("学部不存在");
        }
        return Result.success(department);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新学部信息", description = "更新学部基本信息")
    public Result<Void> updateDepartment(
            @Parameter(description = "学部ID") @PathVariable Long id,
            @RequestBody Department department) {
        Department existing = departmentService.getById(id);
        if (existing == null) {
            return Result.error("学部不存在");
        }
        
        // 更新可修改的字段
        existing.setNameZh(department.getNameZh());
        existing.setNameEn(department.getNameEn());
        existing.setDescriptionZh(department.getDescriptionZh());
        existing.setDescriptionEn(department.getDescriptionEn());
        existing.setLocation(department.getLocation());
        existing.setHotMajorZh(department.getHotMajorZh());
        existing.setHotMajorEn(department.getHotMajorEn());
        existing.setOnlineCount(department.getOnlineCount());
        existing.setSortOrder(department.getSortOrder());
        existing.setStatus(department.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        
        departmentService.updateById(existing);
        return Result.success();
    }

    @PutMapping("/{id}/online-count")
    @Operation(summary = "更新在线人数", description = "更新学部在线人数")
    public Result<Void> updateOnlineCount(
            @Parameter(description = "学部ID") @PathVariable Long id,
            @Parameter(description = "在线人数") @RequestParam Integer count) {
        departmentService.updateOnlineCount(id, count);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新学部状态", description = "启用或禁用学部")
    public Result<Void> updateStatus(
            @Parameter(description = "学部ID") @PathVariable Long id,
            @Parameter(description = "状态: 1-启用, 0-禁用") @RequestParam Integer status) {
        Department department = departmentService.getById(id);
        if (department == null) {
            return Result.error("学部不存在");
        }
        department.setStatus(status);
        department.setUpdatedAt(LocalDateTime.now());
        departmentService.updateById(department);
        return Result.success();
    }

    // ==================== 辅导员管理 ====================

    @GetMapping("/{departmentId}/counselors")
    @Operation(summary = "获取学部辅导员列表", description = "获取指定学部的所有辅导员")
    public Result<List<DepartmentCounselor>> getCounselors(
            @Parameter(description = "学部ID") @PathVariable Long departmentId) {
        List<DepartmentCounselor> counselors = counselorService.getCounselorsByDepartmentId(departmentId);
        return Result.success(counselors);
    }

    @PostMapping("/{departmentId}/counselors")
    @Operation(summary = "添加辅导员", description = "为学部添加新辅导员")
    public Result<Void> addCounselor(
            @Parameter(description = "学部ID") @PathVariable Long departmentId,
            @RequestBody DepartmentCounselor counselor) {
        // 验证学部存在
        Department department = departmentService.getById(departmentId);
        if (department == null) {
            return Result.error("学部不存在");
        }
        
        counselor.setDepartmentId(departmentId);
        counselorService.addCounselor(counselor);
        return Result.success();
    }

    @PutMapping("/counselors/{id}")
    @Operation(summary = "更新辅导员信息", description = "更新辅导员详细信息")
    public Result<Void> updateCounselor(
            @Parameter(description = "辅导员ID") @PathVariable Long id,
            @RequestBody DepartmentCounselor counselor) {
        DepartmentCounselor existing = counselorService.getById(id);
        if (existing == null) {
            return Result.error("辅导员不存在");
        }
        
        existing.setName(counselor.getName());
        existing.setAvatar(counselor.getAvatar());
        existing.setTitle(counselor.getTitle());
        existing.setPhone(counselor.getPhone());
        existing.setEmail(counselor.getEmail());
        existing.setSortOrder(counselor.getSortOrder());
        existing.setStatus(counselor.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        
        counselorService.updateById(existing);
        return Result.success();
    }

    @DeleteMapping("/counselors/{id}")
    @Operation(summary = "删除辅导员", description = "删除指定辅导员")
    public Result<Void> deleteCounselor(
            @Parameter(description = "辅导员ID") @PathVariable Long id) {
        counselorService.deleteCounselor(id);
        return Result.success();
    }
}
