package com.wic.edu.kg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wic.edu.kg.entity.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}
