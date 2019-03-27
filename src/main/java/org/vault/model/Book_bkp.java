package org.vault.model;

import java.io.Serializable;
import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;


/**
 * The persistent class for the books database table.
 * 
 */
@Entity
@Table(name="books")
//@NamedQuery(name="Book.findAll", query="SELECT b FROM Book b")
public class Book implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String isbn;

	private String name;

	//bi-directional many-to-one association to Author
	@OneToMany(mappedBy="book", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	private List<Author> authors = new ArrayList<>();

	public Book() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIsbn() {
		return this.isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Author> getAuthors() {
		return this.authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public Author addAuthor(Author author) {
		getAuthors().add(author);
		author.setBook(this);

		return author;
	}

	public Author removeAuthor(Author author) {
		getAuthors().remove(author);
		author.setBook(null);

		return author;
	}

}