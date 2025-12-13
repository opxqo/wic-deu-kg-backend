package com.wic.edu.kg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wic.edu.kg.entity.DepartmentCounselor;

import java.util.List;

/**
 * 学部辅导员服务接口
 */
public interface DepartmentCounselorService extends IService<DepartmentCounselor> {
    
    /**
     * 获取学部的辅导员列表
     */
    List<DepartmentCounselor> getCounselorsByDepartmentId(Long departmentId);
    
    /**
     * 添加辅导员
     */
    void addCounselor(DepartmentCounselor counselor);
    
    /**
     * 删除辅导员
     */
    void deleteCounselor(Long id);
    
    /**
     * 删除学部下所有辅导员
     */
    void deleteByDepartmentId(Long departmentId);
}
