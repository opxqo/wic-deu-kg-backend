package com.wic.edu.kg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wic.edu.kg.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 系统配置Mapper
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 根据配置键获取配置值
     */
    @Select("SELECT config_value FROM sys_config WHERE config_key = #{key}")
    String getValueByKey(@Param("key") String key);

    /**
     * 更新配置值
     */
    @Update("UPDATE sys_config SET config_value = #{value}, updated_at = NOW() WHERE config_key = #{key}")
    int updateValueByKey(@Param("key") String key, @Param("value") String value);
}
