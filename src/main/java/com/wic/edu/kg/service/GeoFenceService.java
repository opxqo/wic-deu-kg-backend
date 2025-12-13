package com.wic.edu.kg.service;

/**
 * 地理围栏服务接口
 */
public interface GeoFenceService {
    
    /**
     * 检查坐标是否在允许范围内
     * @param latitude 纬度
     * @param longitude 经度
     * @return 是否在范围内
     */
    boolean isWithinCampus(double latitude, double longitude);
    
    /**
     * 检查 IP 是否在白名单中
     * @param ip IP 地址
     * @return 是否在白名单
     */
    boolean isIpWhitelisted(String ip);
    
    /**
     * 检查路径是否在白名单中
     * @param path 请求路径
     * @return 是否在白名单
     */
    boolean isPathWhitelisted(String path);
    
    /**
     * 计算两点之间的距离（米）
     * @param lat1 点1纬度
     * @param lon1 点1经度
     * @param lat2 点2纬度
     * @param lon2 点2经度
     * @return 距离（米）
     */
    double calculateDistance(double lat1, double lon1, double lat2, double lon2);
}
