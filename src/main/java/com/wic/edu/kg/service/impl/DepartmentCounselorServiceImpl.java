package com.wic.edu.kg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wic.edu.kg.entity.DepartmentCounselor;
import com.wic.edu.kg.mapper.DepartmentCounselorMapper;
import com.wic.edu.kg.service.DepartmentCounselorService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学部辅导员服务实现
 */
@Service
public class DepartmentCounselorServiceImpl extends ServiceImpl<DepartmentCounselorMapper, DepartmentCounselor> 
        implements DepartmentCounselorService {

    @Override
    public List<DepartmentCounselor> getCounselorsByDepartmentId(Long departmentId) {
        return this.list(new LambdaQueryWrapper<DepartmentCounselor>()
                .eq(DepartmentCounselor::getDepartmentId, departmentId)
                .eq(DepartmentCounselor::getStatus, 1)
                .orderByAsc(DepartmentCounselor::getSortOrder));
    }

    @Override
    public void addCounselor(DepartmentCounselor counselor) {
        counselor.setStatus(1);
        counselor.setCreatedAt(LocalDateTime.now());
        counselor.setUpdatedAt(LocalDateTime.now());
        this.save(counselor);
    }

    @Override
    public void deleteCounselor(Long id) {
        this.removeById(id);
    }

    @Override
    public void deleteByDepartmentId(Long departmentId) {
        this.remove(new LambdaQueryWrapper<DepartmentCounselor>()
                .eq(DepartmentCounselor::getDepartmentId, departmentId));
    }
}
