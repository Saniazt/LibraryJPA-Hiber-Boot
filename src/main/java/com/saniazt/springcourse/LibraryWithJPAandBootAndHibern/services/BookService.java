package com.saniazt.springcourse.LibraryWithJPAandBootAndHibern.services;
import com.saniazt.springcourse.LibraryWithJPAandBootAndHibern.dao.BookDAO;
import com.saniazt.springcourse.LibraryWithJPAandBootAndHibern.models.Book;
import com.saniazt.springcourse.LibraryWithJPAandBootAndHibern.models.Person;
import com.saniazt.springcourse.LibraryWithJPAandBootAndHibern.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;
    private final BookDAO bookDAO;


    @Autowired
    public BookService(BookRepository bookRepository, BookDAO bookDAO) {
        this.bookRepository = bookRepository;
        this.bookDAO = bookDAO;
    }

    public List<Book> findAll(boolean sortByYear){
        if (sortByYear){
            return bookRepository.findAll(Sort.by("year"));
        }
        else
            return bookRepository.findAll();
    }

    public  Book findOne(int id){
        Optional<Book> foundedBook = bookRepository.findById(id);
        return foundedBook.orElse(null);
    }

    public List<Book> findWithPagination(Integer page, Integer bookPerPage, boolean sortByYear){
        if (sortByYear)
            return bookRepository.findAll(PageRequest.of(page, bookPerPage, Sort.by("year"))).getContent();
        else
            return bookRepository.findAll(PageRequest.of(page, bookPerPage)).getContent();
    }
    public List<Book> searchByTitleNam(String query){
       // return bookRepository.searchByTitle(query);
         return bookDAO.findByTitleOfBooks(query);
    }

    @Transactional
    public void save(Book book){
        bookRepository.save(book);
    }
    @Transactional
    public void update(int id, Book updatedBook){
        updatedBook.setId(id);
        bookRepository.save(updatedBook);
    }
    @Transactional
    public void delete(int id){
        bookRepository.deleteById(id);
    }

    public Optional<Book> findByOwner(int id){
        return bookRepository.findById(id);
    }
    @Transactional
    public void release(int id) {
        bookRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(null);
                    book.setTakenAt(null);
                });
    }
    @Transactional
    public void assign(int id, Person selectedPerson) {
        bookRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(selectedPerson);
                    book.setTakenAt(new Date()); // current time
                }
        );
    }

    public Person getBookOwner(int id) {
        return bookRepository.findById(id).map(Book::getOwner).orElse(null);
    }

    public List<Book> findExpired(){
       List<Book> allBooks = bookRepository.findAll().stream().toList();
       List<Book> expiredBooks = new ArrayList<>();
       long currentTime =  new Date().getTime();
      loop1: for(int i=0;i<allBooks.size();i++){
           try{
               long takenTime = allBooks.get(i).getTakenAt().getTime();
               long so = currentTime-takenTime;
               if (so>82800000) {
                   expiredBooks.add(allBooks.get(i));
                   allBooks.get(i).setExpired(true);
               }
           } catch (NullPointerException nullPointerException) {
               continue loop1;
           }
            // allBooks.get(i).setExpired(true);
       } return expiredBooks;
    }
}