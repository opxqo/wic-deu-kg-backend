package com.wic.edu.kg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 学部视图对象
 */
@Data
@Schema(description = "学部视图对象")
public class DepartmentVO {
    
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "学部代码")
    private String code;
    
    @Schema(description = "中文名称")
    private String nameZh;
    
    @Schema(description = "英文名称")
    private String nameEn;
    
    @Schema(description = "图标名称")
    private String icon;
    
    @Schema(description = "中文描述")
    private String descriptionZh;
    
    @Schema(description = "英文描述")
    private String descriptionEn;
    
    @Schema(description = "位置")
    private String location;
    
    @Schema(description = "热门专业(中文)")
    private String hotMajorZh;
    
    @Schema(description = "热门专业(英文)")
    private String hotMajorEn;
    
    @Schema(description = "在线人数")
    private Integer onlineCount;
    
    @Schema(description = "排序顺序")
    private Integer sortOrder;
    
    @Schema(description = "辅导员列表")
    private List<CounselorInfo> counselors;
    
    /**
     * 辅导员信息
     */
    @Data
    @Schema(description = "辅导员信息")
    public static class CounselorInfo {
        @Schema(description = "ID")
        private Long id;
        
        @Schema(description = "姓名")
        private String name;
        
        @Schema(description = "头像")
        private String avatar;
        
        @Schema(description = "职称")
        private String title;
    }
}
