package com.wic.edu.kg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wic.edu.kg.config.CacheConfig;
import com.wic.edu.kg.entity.Department;
import com.wic.edu.kg.entity.DepartmentCounselor;
import com.wic.edu.kg.entity.SysUser;
import com.wic.edu.kg.mapper.DepartmentMapper;
import com.wic.edu.kg.mapper.SysUserMapper;
import com.wic.edu.kg.service.DepartmentCounselorService;
import com.wic.edu.kg.service.DepartmentService;
import com.wic.edu.kg.vo.DepartmentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 学部服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    private final DepartmentCounselorService counselorService;
    private final SysUserMapper sysUserMapper;

    @Override
    public List<DepartmentVO> getAllDepartments() {
        // 1. 查询所有启用的学部
        List<Department> departments = this.list(new LambdaQueryWrapper<Department>()
                .eq(Department::getStatus, 1)
                .orderByAsc(Department::getSortOrder));

        if (departments.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 获取所有学部名称列表
        List<String> deptNames = departments.stream()
                .map(Department::getNameZh)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Long> deptIds = departments.stream().map(Department::getId).collect(Collectors.toList());

        // 3. 批量查询用户数 (基于 department 名称统计)
        Map<String, Long> userCountByName = batchGetUserCountsByName(deptNames);

        // 4. 批量查询辅导员 (一次查询)
        Map<Long, List<DepartmentCounselor>> counselorMap = batchGetCounselors(deptIds);

        // 5. 组装VO
        return departments.stream()
                .map(dept -> convertToVO(dept, userCountByName, counselorMap))
                .collect(Collectors.toList());
    }

    /**
     * 批量查询各学部用户数（基于 department 名称统计）
     */
    private Map<String, Long> batchGetUserCountsByName(List<String> deptNames) {
        if (deptNames.isEmpty()) {
            return Collections.emptyMap();
        }
        List<SysUser> users = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .in(SysUser::getDepartment, deptNames)
                .select(SysUser::getDepartment));
        return users.stream()
                .filter(u -> u.getDepartment() != null)
                .collect(Collectors.groupingBy(SysUser::getDepartment, Collectors.counting()));
    }

    /**
     * 批量查询各学部辅导员
     */
    private Map<Long, List<DepartmentCounselor>> batchGetCounselors(List<Long> deptIds) {
        List<DepartmentCounselor> counselors = counselorService.list(new LambdaQueryWrapper<DepartmentCounselor>()
                .in(DepartmentCounselor::getDepartmentId, deptIds)
                .eq(DepartmentCounselor::getStatus, 1)
                .orderByAsc(DepartmentCounselor::getSortOrder));
        return counselors.stream()
                .collect(Collectors.groupingBy(DepartmentCounselor::getDepartmentId));
    }

    @Override
    public DepartmentVO getDepartmentById(Long id) {
        Department department = this.getById(id);
        if (department == null)
            return null;
        Map<String, Long> userCountByName = department.getNameZh() != null
                ? batchGetUserCountsByName(Collections.singletonList(department.getNameZh()))
                : Collections.emptyMap();
        return convertToVO(department, userCountByName,
                batchGetCounselors(Collections.singletonList(id)));
    }

    @Override
    public DepartmentVO getDepartmentByCode(String code) {
        Department department = this.getOne(new LambdaQueryWrapper<Department>()
                .eq(Department::getCode, code)
                .eq(Department::getStatus, 1));
        if (department == null)
            return null;
        Map<String, Long> userCountByName = department.getNameZh() != null
                ? batchGetUserCountsByName(Collections.singletonList(department.getNameZh()))
                : Collections.emptyMap();
        return convertToVO(department, userCountByName,
                batchGetCounselors(Collections.singletonList(department.getId())));
    }

    @Override
    public Integer getUserCountByDepartmentId(Long departmentId) {
        Department department = this.getById(departmentId);
        if (department == null || department.getNameZh() == null) {
            return 0;
        }
        return Math.toIntExact(sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDepartment, department.getNameZh())));
    }

    @Override
    @CacheEvict(value = { CacheConfig.DEPARTMENT_BY_ID, CacheConfig.DEPARTMENT_BY_CODE }, allEntries = true)
    public void evictDepartmentCache() {
        // 清除学部缓存
        log.info("已清除学部缓存");
    }

    /**
     * 转换为VO对象 (使用批量查询结果)
     */
    private DepartmentVO convertToVO(Department department,
            Map<String, Long> userCountByName,
            Map<Long, List<DepartmentCounselor>> counselorMap) {
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
        // 从批量查询结果获取用户数（基于学部名称）
        vo.setOnlineCount(userCountByName.getOrDefault(department.getNameZh(), 0L).intValue());
        vo.setSortOrder(department.getSortOrder());

        // 从批量查询结果获取辅导员列表
        List<DepartmentCounselor> counselors = counselorMap.getOrDefault(department.getId(), Collections.emptyList());
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
