package pl.edu.pk.ztp.library.db.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"id", "title", "author", "quantity", "rentals"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDTO {

    private Integer id;
    private String title;
    private String author;
    private int quantity;
    private List<BookRentalDTO> rentals;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<BookRentalDTO> getRentals() {
        return rentals;
    }

    public void setRentals(List<BookRentalDTO> rentals) {
        this.rentals = rentals;
    }

    public static BookDTO create(final int id, final String title,
                                 final String author, final int quantity){
        BookDTO book = new BookDTO();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setQuantity(quantity);
        return book;
    }
}
