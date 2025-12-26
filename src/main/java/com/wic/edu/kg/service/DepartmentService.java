package com.wic.edu.kg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wic.edu.kg.entity.Department;
import com.wic.edu.kg.vo.DepartmentVO;

import java.util.List;

/**
 * 学部服务接口
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 获取所有启用的学部列表（包含辅导员信息）
     */
    List<DepartmentVO> getAllDepartments();

    /**
     * 根据ID获取学部详情
     */
    DepartmentVO getDepartmentById(Long id);

    /**
     * 根据代码获取学部详情
     */
    DepartmentVO getDepartmentByCode(String code);

    /**
     * 获取学部用户数（动态计算）
     */
    Integer getUserCountByDepartmentId(Long departmentId);
}
