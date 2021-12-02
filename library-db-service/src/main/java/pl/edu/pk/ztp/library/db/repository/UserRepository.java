package pl.edu.pk.ztp.library.db.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pl.edu.pk.ztp.library.db.dto.UserDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<UserDTO> findAll(){
        return jdbcTemplate.query("SELECT * from tbl_users",
                UserRepository::mapResultsToUserDTO);
    }

    public UserDTO findByUserId(final Integer userID){
        return jdbcTemplate.queryForObject("SELECT * from tbl_users where id = ?",
                UserRepository::mapResultsToUserDTO, userID);
    }

    public boolean deleteUserById(final Integer userID){
        if(jdbcTemplate.queryForObject("SELECT count(*) from tbl_rentals where userid_fk = ?", Integer.class, userID) != 0){
            throw new UnsupportedOperationException("Nie można usunąć użytkownika z powodu nie zwrócenia wszystkich książek");
        }
        else {
            int rowsAffected = jdbcTemplate.update("DELETE tbl_users where id = ?", userID);
            if(rowsAffected == 1) return true;
            else return false;
        }
    }

    public UserDTO createUser(final UserDTO user){
        String sql = "INSERT INTO tbl_users (name, roles) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getUsername());
            ps.setArray(2, connection.createArrayOf("VARCHAR", user.getRoles().toArray(new String[0])));
            return ps;
        }, keyHolder);
        Number id = keyHolder.getKey();
        return jdbcTemplate.queryForObject("SELECT * from tbl_users where id = ?", UserRepository::mapResultsToUserDTO, id);
    }

    public static UserDTO mapResultsToUserDTO(ResultSet rs, int rowNum) throws SQLException{
        return UserDTO.create(rs.getInt("id"), rs.getString("name"), rs.getString("roles"));
    }
}
