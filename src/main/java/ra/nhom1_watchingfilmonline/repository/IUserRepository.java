package ra.nhom1_watchingfilmonline.repository;


import ra.nhom1_watchingfilmonline.model.entity.Categories;
import ra.nhom1_watchingfilmonline.model.entity.Users;

import java.util.List;

public interface IUserRepository {
    Users findByOrEmailOrPhone(String email, String phone);
    Users findUsersByUsername(String mail);
    Users save(Users user);
    Users registerUser(String userName, String fullName, String email, String phone, String password, Integer roleId);
    List<Users> findAllUsers();
    Boolean update(Users users);
    Users findById(Integer id);
    String getCurrentUserName();
}
