package com.wic.edu.kg.service;

import com.wic.edu.kg.vo.DashboardVO;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {

    /**
     * 获取仪表盘数据
     *
     * @return 仪表盘数据
     */
    DashboardVO getDashboardData();
}
