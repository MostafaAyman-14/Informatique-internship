package com.library.app;

import com.library.app.entity.Book;
import com.library.app.repository.BookRepos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AppApplicationTests {
@Autowired
	private BookRepos repose;
	@Test
	public void saveBook() {
		Book book=new Book();
		book.setId(1L);
		book.setAuthor("af");
		book.setIsbn("ra");
		book.setTitle("ba");
		book.setPublicationYear(1945);
		repose.save(book);
		Book savedBook=repose.findById(1L).get();
		assertNotNull(savedBook);
	}

}
