package ra.nhom1_watchingfilmonline.repository.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.nhom1_watchingfilmonline.model.entity.Films;
import ra.nhom1_watchingfilmonline.model.entity.Roles;
import ra.nhom1_watchingfilmonline.model.entity.Users;
import ra.nhom1_watchingfilmonline.repository.IRoleRepository;
import ra.nhom1_watchingfilmonline.repository.IUserRepository;


import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
public class UserRepositoryImpl implements IUserRepository {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private IRoleRepository roleRepository;


    @Autowired
    private IUserRepository userRepository;

    @Override
    public Users findByOrEmailOrPhone(String email, String phone) {
        Session session = sessionFactory.openSession();
        Users user = null;
        try {
            session.beginTransaction();
            String hql = "from Users where email = :email or phone = :phone";
            user = (Users) session.createQuery(hql)
                    .setParameter("email", email)
                    .setParameter("phone", phone)
                    .uniqueResult();
            session.getTransaction().commit();
        }catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }finally {
            session.close();
        }
        return user;
    }


    @Override
    public Users findUsersByUsername(String email) {
        Session session = sessionFactory.openSession();
        Users user = null;
        try {
            session.beginTransaction();
            String hql = "from Users where email = :email";
            user = (Users) session.createQuery(hql)
                    .setParameter("email", email)
                    .uniqueResult();
            session.getTransaction().commit();

        }catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }finally {
            session.close();
        }
        return user;
    }

    @Override
    public Users save(Users user) {
        Session session = sessionFactory.openSession();
        // Kiểm tra tài khoản đã tồn tại
        Users existingUser = userRepository.findByOrEmailOrPhone(user.getEmail(), user.getPhone());
        if (existingUser != null) {
            throw new RuntimeException("User with email or phone already exists");
        }

        try {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return user;
    }

    @Override
    public Users registerUser(String userName, String fullName, String email, String phone, String password, Integer roleId) {
        // Tìm vai trò theo roleId
        // Kiểm tra tài khoản đã tồn tại
        Users existingUser = userRepository.findByOrEmailOrPhone(email, phone);
        if (existingUser != null) {
            throw new RuntimeException("User with email or phone already exists");
        }

        Roles role = roleRepository.findRolesByRoleName(roleId == 1 ? "ADMIN" : "USER");

        if (role == null) {
            throw new RuntimeException("Role not found");
        }
        // Tạo người dùng mới
        Users user = new Users();
        user.setUserName(userName);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(password);
        user.setAvatar(""); // Set default avatar if necessary
        user.setAddress(""); // Set default address if necessary
        user.setStatus(true); // Active by default
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setRoles(Arrays.asList(role)); // Sử dụng Arrays.asList để tạo danh sách
        return save(user);
    }

    @Override
    public List<Users> findAllUsers() {
        Session session = sessionFactory.openSession();
        List<Users> users = null;
        try {
            session.beginTransaction();
            String sql = "select * from users u join user_role ur on u.userId = ur.userId join roles r on r.roleId = ur.roleId where r.roleName not like 'ADMIN';"; // Truy vấn HQL để lấy tất cả người dùng
            users = session.createNativeQuery(sql, Users.class).list(); // Thực thi truy vấn và lấy danh sách người dùng
            session.getTransaction().commit();
        }catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }finally {
            session.close();
        }
        return users;
    }

    @Override
    public Boolean update(Users users) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(users);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public Users findById(Integer id) {
        Session session = sessionFactory.openSession();
        Users filmUser = null;
        try {
            session.beginTransaction();
            filmUser = session.get(Users.class, id);
            session.getTransaction().commit();
            return filmUser;
        }catch (Exception e){
            e.printStackTrace();
            session.getTransaction().rollback();
        }finally {
            session.close();
        }
        return null;
    }

//    @Override
//    public Long totalAllUser(String search) {
//        Session session = sessionFactory.openSession();
//        try {
//            if (search.isEmpty()) {
//                return session.createQuery("select count(u) from Users u", Long.class)
//                        .getSingleResult();
//            } else {
//                return session.createQuery("select count(u) from Users u where u.fullName like concat('%',:search,'%') ", Long.class)
//                        .setParameter("search", search)
//                        .getSingleResult();
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            session.close();
//        }
//    }
//
//    @Override
//    public List<Users> findAllByOrderByUserAsc(int page, int size) {
//        Session session = sessionFactory.openSession();
//        try {
//            // Tạo một truy vấn với phân trang
//            return session.createQuery("select u from Users u order by u.fullName asc", Users.class)
//                    .setFirstResult(page * size)
//                    .setMaxResults(size)
//                    .getResultList();
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        } finally {
//            session.close();
//        }
//    }
//
//    @Override
//    public List<Users> findAllByOrderByUserDesc(int page, int size) {
//        Session session = sessionFactory.openSession();
//        try {
//            // Tạo một truy vấn với phân trang
//            return session.createQuery("select u from Users u order by u.fullName desc", Users.class)
//                    .setFirstResult(page * size)
//                    .setMaxResults(size)
//                    .getResultList();
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        } finally {
//            session.close();
//        }
//    }
}
