package com.wic.edu.kg.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wic.edu.kg.dto.ArticleRequest;
import com.wic.edu.kg.entity.Article;
import com.wic.edu.kg.vo.ArticleVO;

/**
 * 学院动态服务接口
 */
public interface ArticleService extends IService<Article> {

    /**
     * 分页获取文章列表
     * 
     * @param page    页码
     * @param size    每页数量
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    Page<ArticleVO> getArticleList(int page, int size, String keyword);

    /**
     * 根据ID获取文章详情
     * 
     * @param id 文章ID
     * @return 文章详情
     */
    ArticleVO getArticleById(Long id);

    /**
     * 创建文章
     * 
     * @param request 文章请求
     * @return 创建的文章
     */
    Article createArticle(ArticleRequest request);

    /**
     * 更新文章
     * 
     * @param id      文章ID
     * @param request 文章请求
     * @return 更新后的文章
     */
    Article updateArticle(Long id, ArticleRequest request);

    /**
     * 删除文章
     * 
     * @param id 文章ID
     */
    void deleteArticle(Long id);

    /**
     * 增加浏览次数
     * 
     * @param id 文章ID
     */
    void incrementViewCount(Long id);
}
