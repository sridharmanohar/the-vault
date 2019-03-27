package org.vault.controller;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
	
	
	@GetMapping("/showBookDetails/{bookId}")
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
 	
	@PostMapping(value="/editBookDetails/{bookId}")
	public String saveBookDetails(@PathVariable("bookId") int id, @ModelAttribute("bookdetails") Book bookdetails, HttpServletRequest req) {
		System.out.println("in save details");
		String addRowIndicator = req.getParameter("addRow");
		System.out.println("fetched rowid");
		System.out.println("id is:"+id);
		System.out.println("book details:"+bookdetails.getName());
		List<Author> athList = bookdetails.getAuthors();
		for(Author ath : athList) {
			System.out.println("Auth details name:"+ ath.getAuthname());
			System.out.println("Auth details id:"+ ath.getId());
			System.out.println("Auth details book id:"+ ath.getBook().getId());
		}
		System.out.println("size of authors:"+bookdetails.getAuthors().size());
		if(addRowIndicator != null) {
			System.out.println("abut to add new row");
			Author a1 = new Author();
			bookdetails.setId(id);
			a1.setBook(bookdetails);
			bookdetails.getAuthors().add(a1);
			System.out.println("new row added");
			return "bookDetails";
		}
		System.out.println("id to be set");
		bookdetails.setId(id);
		System.out.println("id set done");
		this.bookRepo.save(bookdetails);
		System.out.println("db save complete");
		return "redirect:/showBooks";
	}
	
}
