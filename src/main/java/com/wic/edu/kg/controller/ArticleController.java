package com.wic.edu.kg.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wic.edu.kg.common.ApiResponse;
import com.wic.edu.kg.dto.ArticleRequest;
import com.wic.edu.kg.entity.Article;
import com.wic.edu.kg.service.ArticleService;
import com.wic.edu.kg.vo.ArticleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 学院动态控制器
 * 
 * RESTful API 设计：
 * - GET /api/article - 获取文章列表
 * - GET /api/article/{articleId} - 获取文章详情
 * - POST /api/article - 创建文章 (管理员)
 * - PUT /api/article/{articleId} - 更新文章 (管理员)
 * - DELETE /api/article/{articleId} - 删除文章 (管理员)
 */
@RestController
@RequestMapping("/api/article")
@Tag(name = "学院动态", description = "学院动态/新闻公告接口")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    @Operation(summary = "获取文章列表", description = "分页获取已发布的文章列表")
    public ResponseEntity<ApiResponse<Page<ArticleVO>>> getArticleList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {

        Page<ArticleVO> result = articleService.getArticleList(page, size, keyword);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{articleId}")
    @Operation(summary = "获取文章详情", description = "根据ID获取文章详情，同时增加浏览次数")
    public ResponseEntity<ApiResponse<ArticleVO>> getArticleById(
            @Parameter(description = "文章ID", required = true) @PathVariable Long articleId) {

        ArticleVO article = articleService.getArticleById(articleId);
        if (article == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "文章不存在", "/api/article/" + articleId));
        }

        // 增加浏览次数
        articleService.incrementViewCount(articleId);

        return ResponseEntity.ok(ApiResponse.ok(article));
    }

    @PostMapping
    @Operation(summary = "创建文章", description = "创建新的学院动态文章（需要管理员权限）")
    public ResponseEntity<ApiResponse<ArticleVO>> createArticle(
            @Valid @RequestBody ArticleRequest request) {

        Article article = articleService.createArticle(request);
        ArticleVO vo = articleService.getArticleById(article.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(vo));
    }

    @PutMapping("/{articleId}")
    @Operation(summary = "更新文章", description = "更新指定ID的文章（需要管理员权限）")
    public ResponseEntity<ApiResponse<ArticleVO>> updateArticle(
            @Parameter(description = "文章ID", required = true) @PathVariable Long articleId,
            @Valid @RequestBody ArticleRequest request) {

        try {
            articleService.updateArticle(articleId, request);
            ArticleVO vo = articleService.getArticleById(articleId);
            return ResponseEntity.ok(ApiResponse.ok(vo));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", e.getMessage(), "/api/article/" + articleId));
        }
    }

    @DeleteMapping("/{articleId}")
    @Operation(summary = "删除文章", description = "删除指定ID的文章（需要管理员权限）")
    public ResponseEntity<ApiResponse<Void>> deleteArticle(
            @Parameter(description = "文章ID", required = true) @PathVariable Long articleId) {

        articleService.deleteArticle(articleId);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
