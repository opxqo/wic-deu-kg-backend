package com.wic.edu.kg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wic.edu.kg.dto.ArticleRequest;
import com.wic.edu.kg.entity.Article;
import com.wic.edu.kg.mapper.ArticleMapper;
import com.wic.edu.kg.service.ArticleService;
import com.wic.edu.kg.vo.ArticleVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 学院动态服务实现类
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Override
    public Page<ArticleVO> getArticleList(int page, int size, String keyword) {
        Page<Article> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        // 只查询已发布的文章
        wrapper.eq(Article::getStatus, 1);

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(Article::getTitle, keyword)
                    .or()
                    .like(Article::getSubtitle, keyword)
                    .or()
                    .like(Article::getContent, keyword));
        }

        // 按发布日期倒序
        wrapper.orderByDesc(Article::getPublishDate);

        Page<Article> result = this.page(pageParam, wrapper);

        // 转换为VO
        Page<ArticleVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public ArticleVO getArticleById(Long id) {
        Article article = this.getById(id);
        if (article == null) {
            return null;
        }
        return convertToVO(article);
    }

    @Override
    public Article createArticle(ArticleRequest request) {
        Article article = new Article();
        copyFromRequest(article, request);

        // 设置默认值
        if (article.getPublishDate() == null) {
            article.setPublishDate(LocalDate.now());
        }
        if (article.getStatus() == null) {
            article.setStatus(1); // 默认已发布
        }
        if (article.getViewCount() == null) {
            article.setViewCount(0);
        }

        this.save(article);
        return article;
    }

    @Override
    public Article updateArticle(Long id, ArticleRequest request) {
        Article article = this.getById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }

        copyFromRequest(article, request);
        this.updateById(article);
        return article;
    }

    @Override
    public void deleteArticle(Long id) {
        this.removeById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        LambdaUpdateWrapper<Article> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Article::getId, id)
                .setSql("view_count = view_count + 1");
        this.update(wrapper);
    }

    /**
     * 将实体转换为VO
     */
    private ArticleVO convertToVO(Article article) {
        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSubtitle(article.getSubtitle());
        vo.setAuthor(article.getAuthor());
        vo.setPublishDate(article.getPublishDate());
        vo.setReadTime(article.getReadTime());
        vo.setCoverImage(article.getCoverImage());
        vo.setContent(article.getContent());
        vo.setViewCount(article.getViewCount());
        vo.setStatus(article.getStatus());
        vo.setCreatedAt(article.getCreatedAt());
        vo.setUpdatedAt(article.getUpdatedAt());

        // 将逗号分隔的标签字符串转换为List
        if (StringUtils.hasText(article.getTags())) {
            vo.setTags(Arrays.asList(article.getTags().split(",")));
        } else {
            vo.setTags(Collections.emptyList());
        }

        return vo;
    }

    /**
     * 从请求复制属性到实体
     */
    private void copyFromRequest(Article article, ArticleRequest request) {
        article.setTitle(request.getTitle());
        article.setSubtitle(request.getSubtitle());
        article.setAuthor(request.getAuthor());
        article.setPublishDate(request.getPublishDate());
        article.setReadTime(request.getReadTime());
        article.setCoverImage(request.getCoverImage());
        article.setContent(request.getContent());
        article.setStatus(request.getStatus());

        // 将标签List转换为逗号分隔的字符串
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            article.setTags(String.join(",", request.getTags()));
        } else {
            article.setTags(null);
        }
    }
}
