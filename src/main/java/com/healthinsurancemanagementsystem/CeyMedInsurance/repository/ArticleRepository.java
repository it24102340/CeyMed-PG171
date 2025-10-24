package com.healthinsurancemanagementsystem.CeyMedInsurance.repository;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    java.util.List<Article> findTop5ByOrderByCreatedAtDesc();
}


