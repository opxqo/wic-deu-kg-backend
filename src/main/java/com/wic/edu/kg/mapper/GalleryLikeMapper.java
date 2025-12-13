package com.wic.edu.kg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wic.edu.kg.entity.GalleryLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图片点赞 Mapper
 */
@Mapper
public interface GalleryLikeMapper extends BaseMapper<GalleryLike> {
}
