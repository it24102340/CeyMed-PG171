package com.healthinsurancemanagementsystem.CeyMedInsurance.controller;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.User;
import com.healthinsurancemanagementsystem.CeyMedInsurance.service.ArticleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/article/admin")
public class ArticleAdminController {

	private final ArticleService articleService;

	public ArticleAdminController(ArticleService articleService) {
		this.articleService = articleService;
	}

	@GetMapping
	public String page(Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null || !Boolean.TRUE.equals(user.getIsAdmin())) {
			return "redirect:/user/login";
		}
		model.addAttribute("articles", articleService.listAll());
		return "admin/admin-articles";
	}

    @PostMapping("/create")
    public String create(@RequestParam String title,
                        @RequestParam String details,
                        @RequestParam(value = "image", required = false) MultipartFile image,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
		User user = (User) session.getAttribute("user");
		if (user == null || !Boolean.TRUE.equals(user.getIsAdmin())) {
			return "redirect:/user/login";
		}
		try {
            String imageUrl = saveImage(image);
            articleService.create(title, details, imageUrl);
			redirectAttributes.addFlashAttribute("successMessage", "Article added successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Failed to add article: " + e.getMessage());
		}
		return "redirect:/article/admin";
	}

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                        @RequestParam String title,
                        @RequestParam String details,
                        @RequestParam(value = "image", required = false) MultipartFile image,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
		User user = (User) session.getAttribute("user");
		if (user == null || !Boolean.TRUE.equals(user.getIsAdmin())) {
			return "redirect:/user/login";
		}
		try {
            String imageUrl = saveImage(image);
            articleService.update(id, title, details, imageUrl);
			redirectAttributes.addFlashAttribute("successMessage", "Article updated successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Failed to update article: " + e.getMessage());
		}
		return "redirect:/article/admin";
	}

	@PostMapping("/delete/{id}")
	public String delete(@PathVariable Long id,
	                    HttpSession session,
	                    RedirectAttributes redirectAttributes) {
		User user = (User) session.getAttribute("user");
		if (user == null || !Boolean.TRUE.equals(user.getIsAdmin())) {
			return "redirect:/user/login";
		}
		try {
			articleService.delete(id);
			redirectAttributes.addFlashAttribute("successMessage", "Article deleted successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete article: " + e.getMessage());
		}
		return "redirect:/article/admin";
	}

    private String saveImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) return null;
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        String ext = "";
        String original = image.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID() + ext;
        Path target = uploadDir.resolve(filename);
        Files.copy(image.getInputStream(), target);
        return "/uploads/" + filename;
    }
}


