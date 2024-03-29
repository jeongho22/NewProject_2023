package com.example.dy.Repository;

import com.example.dy.Domain.Article;
import com.example.dy.Domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article,Long> {   // 관리대상 Aricle 저장, 대표값 저장 Long

    @Modifying // 변경쿼리에 사용
    @Query("UPDATE Article a SET a.view = a.view + 1 WHERE a.id = :id")
    void incrementViewCount(@Param("id") Long id);
    // 이 메소드를 사용할 때 Article 엔티티의 인스턴스는 변경되지 않습니다.
    // 즉, JPA의 엔티티 상태 관리(context) 밖에서 작업이 수행되기 때문에,
    // Article 엔티티의 @LastModifiedDate 어노테이션이 적용되는 modifiedAt 필드가 자동으로 업데이트되는 것을 방지할 수 있습니다.
    // 이는 특히 Article 엔티티의 다른 중요한 정보가 변경되지 않았음에도 불구하고
    // 단순히 조회수가 증가하는 경우에 modifiedAt 필드가 업데이트되는 것을 원치 않을 때 유용합니다.


    // 1. 게시물 페이지 조회
    Page<Article> findAll(Pageable pageable);

    // 2. 전체 조회
    Page<Article> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    // 3. 제목 조회
    Page<Article> findByTitleContaining(String title, Pageable pageable);

    // 4. 내용 조회
    Page<Article> findByContentContaining(String content, Pageable pageable);

    // 5. 게시물을 작성한 유저 조회
    List<Article> findByUserId(@Param("UserId") Long UserId);


    // 6.인기 게시물 조회

    @Query("SELECT a FROM Article a WHERE a.id IN " +
            "(SELECT b.article.id FROM LikeBoard b GROUP BY b.article.id HAVING COUNT(b) >= :likeCount) " +
            "AND a.view >= :viewCount " +
            "AND (LOWER(a.title) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :searchKeyword, '%')))")
    Page<Article> findPopularArticlesWithSearch(
            @Param("likeCount") Long likeCount,
            @Param("viewCount") Long viewCount,
            @Param("searchKeyword") String searchKeyword,
            Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.id IN " +
            "(SELECT b.article.id FROM LikeBoard b GROUP BY b.article.id HAVING COUNT(b) >= :likeCount) " +
            "AND a.view >= :viewCount " +
            "AND LOWER(a.title) LIKE LOWER(:searchKeyword)")
    Page<Article> findPopularArticlesWithTitleSearch(
            @Param("likeCount") Long likeCount,
            @Param("viewCount") Long viewCount,
            @Param("searchKeyword") String searchKeyword,
            Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.id IN " +
            "(SELECT b.article.id FROM LikeBoard b GROUP BY b.article.id HAVING COUNT(b) >= :likeCount) " +
            "AND a.view >= :viewCount " +
            "AND LOWER(a.content) LIKE LOWER(:searchKeyword)")
    Page<Article> findPopularArticlesWithContentSearch(
            @Param("likeCount") Long likeCount,
            @Param("viewCount") Long viewCount,
            @Param("searchKeyword") String searchKeyword,
            Pageable pageable);


}