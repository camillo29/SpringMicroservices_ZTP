package pl.edu.pk.ztp.library.db.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pl.edu.pk.ztp.library.db.dto.BookDTO;
import pl.edu.pk.ztp.library.db.dto.BookRentalDTO;
import pl.edu.pk.ztp.library.db.dto.UserDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Repository
public class BookRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<BookDTO> findAll(boolean showOnlyAvailable){
        if(showOnlyAvailable){
            return jdbcTemplate.query("SELECT * FROM tbl_books WHERE id NOT IN (" +
                    "SELECT DISTINCT tbl_books.id FROM tbl_books JOIN tbl_rentals ON (tbl_books.id = tbl_rentals.bookid_fk)" +
                    " WHERE return_date IS NULL GROUP BY tbl_books.id HAVING tbl_books.quantity <= count(*))", BookRepository::mapResultsToBookDTO);
        }
        else return jdbcTemplate.query("SELECT * from tbl_books",
                BookRepository::mapResultsToBookDTO);
    }

    public BookDTO findBookById(final Integer id){
        List<BookRentalDTO> rentals = jdbcTemplate.query("SELECT tbl_rentals.*, tbl_users.id as userID, tbl_users.name" +
                        " FROM tbl_rentals JOIN tbl_users ON tbl_rentals.userid_fk = tbl_users.id" +
                        " WHERE bookid_fk = ?", BookRepository::mapResultsToBookRentalDTO, id);
        BookDTO book = jdbcTemplate.queryForObject("SELECT * FROM tbl_books WHERE id = ?", BookRepository::mapResultsToBookDTO, id);
        book.setRentals(rentals);
        return book;
    }

    public BookDTO rentBook(final Integer bookID, UserDTO user){
        BookDTO book;
        try {
            book = jdbcTemplate.queryForObject("SELECT * from tbl_books where id = ?", BookRepository::mapResultsToBookDTO, bookID);
        } catch(Exception e) {
            throw new UnsupportedOperationException("Podany identyfikator książki nie istnieje lub jest nieprawidłowy");
        }
        int rentedCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tbl_rentals WHERE bookid_fk = ? AND return_date IS NULL", Integer.class, bookID);
        if(book.getQuantity() - rentedCount <=0)
            throw new UnsupportedOperationException("Nie ma dostępnych wolnych egzemplarzy dla podanego identyfikatora książki");

        String sql = "INSERT INTO tbl_rentals (userid_fk, bookid_fk, rental_date) VALUES(?,?, CURRENT_DATE())";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setInt(1, user.getId());
            ps.setInt(2, bookID);
            return ps;
        }, keyHolder);
        Number id = keyHolder.getKey();
        BookRentalDTO rental = jdbcTemplate.queryForObject("SELECT tbl_rentals.*, tbl_users.id as userID, tbl_users.name" +
                " FROM tbl_rentals JOIN tbl_users ON tbl_rentals.userid_fk = tbl_users.id" +
                " WHERE tbl_rentals.id = ?", BookRepository::mapResultsToBookRentalDTO, id);
        List<BookRentalDTO> rentalList = new LinkedList<>(); rentalList.add(rental);
        book.setRentals(rentalList);
        return book;
    }

    public BookDTO returnBook(final Integer bookID, UserDTO user){
        BookDTO book;
        try {
            book = jdbcTemplate.queryForObject("SELECT * from tbl_books where id = ?", BookRepository::mapResultsToBookDTO, bookID);
        } catch(Exception e) {
            throw new UnsupportedOperationException("Podany identyfikator książki nie istnieje lub jest nieprawidłowy");
        }
        int rentedBooks = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tbl_rentals WHERE bookid_fk = ? AND userid_fk = ? AND return_date IS NULL", Integer.class, bookID, user.getId());
        if(rentedBooks<=0)
            throw new UnsupportedOperationException("Użytkownik nie ma aktualnie wypożyczonych egzemplarzy dla podanego identyfikatora książki");

        String sql = "UPDATE tbl_rentals SET return_date = CURRENT_DATE() WHERE bookid_fk = ? AND userid_fk = ? AND return_date IS NULL LIMIT 1";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setInt(1, bookID);
            ps.setInt(2, user.getId());
            return ps;
        }, keyHolder);
        Number id = keyHolder.getKey();
        BookRentalDTO rental = jdbcTemplate.queryForObject("SELECT tbl_rentals.*, tbl_users.id as userID, tbl_users.name" +
                " FROM tbl_rentals JOIN tbl_users ON tbl_rentals.userid_fk = tbl_users.id" +
                " WHERE tbl_rentals.id = ?", BookRepository::mapResultsToBookRentalDTO, id);
        List<BookRentalDTO> rentalList = new LinkedList<>(); rentalList.add(rental);
        book.setRentals(rentalList);
        return book;
    }

    public static BookDTO mapResultsToBookDTO(ResultSet rs, int rowNum) throws SQLException{
        return BookDTO.create(rs.getInt("id"), rs.getString("title"),
                rs.getString("author"), rs.getInt("quantity"));
    }

    public static BookRentalDTO mapResultsToBookRentalDTO(ResultSet rs, int rowNum) throws SQLException{
        UserDTO user = UserDTO.create(rs.getInt("userID"), rs.getString("name"));
        return BookRentalDTO.create(rs.getInt("id"), rs.getDate("rental_date"), rs.getDate("return_date"), user);
    }
}
