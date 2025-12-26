package com.wic.edu.kg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wic.edu.kg.entity.Department;
import com.wic.edu.kg.entity.DepartmentCounselor;
import com.wic.edu.kg.entity.SysUser;
import com.wic.edu.kg.mapper.DepartmentMapper;
import com.wic.edu.kg.mapper.SysUserMapper;
import com.wic.edu.kg.service.DepartmentCounselorService;
import com.wic.edu.kg.service.DepartmentService;
import com.wic.edu.kg.vo.DepartmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 学部服务实现
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Autowired
    private DepartmentCounselorService counselorService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public List<DepartmentVO> getAllDepartments() {
        // 查询所有启用的学部
        List<Department> departments = this.list(new LambdaQueryWrapper<Department>()
                .eq(Department::getStatus, 1)
                .orderByAsc(Department::getSortOrder));

        // 转换为VO并填充辅导员信息
        return departments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentVO getDepartmentById(Long id) {
        Department department = this.getById(id);
        return department != null ? convertToVO(department) : null;
    }

    @Override
    public DepartmentVO getDepartmentByCode(String code) {
        Department department = this.getOne(new LambdaQueryWrapper<Department>()
                .eq(Department::getCode, code)
                .eq(Department::getStatus, 1));
        return department != null ? convertToVO(department) : null;
    }

    @Override
    public Integer getUserCountByDepartmentId(Long departmentId) {
        return Math.toIntExact(sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDepartmentId, departmentId)));
    }

    /**
     * 转换为VO对象
     */
    private DepartmentVO convertToVO(Department department) {
        DepartmentVO vo = new DepartmentVO();
        vo.setId(department.getId());
        vo.setCode(department.getCode());
        vo.setNameZh(department.getNameZh());
        vo.setNameEn(department.getNameEn());
        vo.setIcon(department.getIcon());
        vo.setDescriptionZh(department.getDescriptionZh());
        vo.setDescriptionEn(department.getDescriptionEn());
        vo.setLocation(department.getLocation());
        vo.setHotMajorZh(department.getHotMajorZh());
        vo.setHotMajorEn(department.getHotMajorEn());
        // 动态计算用户数
        vo.setOnlineCount(getUserCountByDepartmentId(department.getId()));
        vo.setSortOrder(department.getSortOrder());

        // 获取辅导员列表
        List<DepartmentCounselor> counselors = counselorService.getCounselorsByDepartmentId(department.getId());
        vo.setCounselors(counselors.stream().map(c -> {
            DepartmentVO.CounselorInfo info = new DepartmentVO.CounselorInfo();
            info.setId(c.getId());
            info.setName(c.getName());
            info.setAvatar(c.getAvatar());
            info.setTitle(c.getTitle());
            return info;
        }).collect(Collectors.toList()));

        return vo;
    }
}
