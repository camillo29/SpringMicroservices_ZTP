package pl.edu.pk.ztp.library.db.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

@JsonPropertyOrder({"id", "rentalDate", "returnDate", "user"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookRentalDTO {
    private Integer id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date rentalDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date returnDate;

    private UserDTO user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(Date rentalDate) {
        this.rentalDate = rentalDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public static BookRentalDTO create(final int id, final Date rentalDate, final Date returnDate, final UserDTO user){
        BookRentalDTO bookRental = new BookRentalDTO();
        bookRental.setId(id);
        bookRental.setRentalDate(rentalDate);
        bookRental.setReturnDate(returnDate);
        bookRental.setUser(user);
        return bookRental;
    }
}
