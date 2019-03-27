package org.vault.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.vault.model.Author;
import org.vault.model.Book;
import org.vault.repo.BookRepo;

@Controller
public class BookController {

	private final BookRepo bookRepo;
	
	public BookController(BookRepo bookRepo) {
		this.bookRepo = bookRepo;
	}
	
	@GetMapping("/showBooks")
	public String showBooks(Model model) {
		System.out.println("inside showBooks");
		List<Book> books = this.bookRepo.findAll();
		System.out.println("call to db completed");
		System.out.println("list size:"+books.size());
		model.addAttribute("books", books);
		return "showBooks";
	}
	
	
	@GetMapping("/editBookDetails/{bookId}")
	public String showBookDetails(@PathVariable("bookId") int id, Model model) {
		System.out.println("inside showBookDetails");
		System.out.println("book id is:"+id);
		Book bookdetails = this.bookRepo.findById(id);
		System.out.println("db call complete");
		System.out.println("bookdetails:"+ bookdetails.getName());
		System.out.println("bookdetails:"+ bookdetails.getAuthors().size());
		model.addAttribute("bookdetails", bookdetails);
		return "bookDetails";
	}
 	
	@PostMapping("/editBookDetails/{bookId}")
	public String saveDetails(@PathVariable("bookId") int id, @ModelAttribute("bookdetails") Book bookdetails) {
		
		System.out.println("in save details");
		System.out.println("id is:"+id);
		System.out.println("book details:"+bookdetails.getName());
		List<Author> athList = bookdetails.getAuthors();
		for(Author ath : athList) {
			System.out.println("Auth details:"+ ath.getAuthname());
			System.out.println("Auth details:"+ ath.getId());
			System.out.println("Auth details:"+ ath.getBook().getId());
		}
		System.out.println("id to be set");
		bookdetails.setId(id);
		System.out.println("id set done");
		this.bookRepo.save(bookdetails);
		System.out.println("db save complete");
		return "redirect:/showBooks";
	}
}
