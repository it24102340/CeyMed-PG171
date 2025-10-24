package com.healthinsurancemanagementsystem.CeyMedInsurance.service;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Article;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

	private final ArticleRepository articleRepository;

	public ArticleService(ArticleRepository articleRepository) {
		this.articleRepository = articleRepository;
	}

	public List<Article> listAll() {
		return articleRepository.findAll();
	}

	public Article create(String title, String details, String imageUrl) {
		Article a = new Article();
		a.setTitle(title);
		a.setDetails(details);
		a.setImageUrl(imageUrl);
		return articleRepository.save(a);
	}

	public Article update(Long id, String title, String details, String imageUrl) {
		Article a = articleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Article not found"));
		a.setTitle(title);
		a.setDetails(details);
		a.setImageUrl(imageUrl);
		return articleRepository.save(a);
	}

	public void delete(Long id) {
		articleRepository.deleteById(id);
	}
}


