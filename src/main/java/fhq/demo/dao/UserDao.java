package fhq.demo.dao;

import fhq.demo.bean.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @author fhq
 * @date 2019/12/22 16:31
 */
@Repository
@Mapper
public interface UserDao {
    @Select("SELECT * FROM user WHERE email=#{email}")
    User findUserByEmail(@Param("email") String email);

    @Update("UPDATE user SET isEmailActive=#{isEmailActive}, password=#{password} WHERE email=#{email}")
    void updateUser(User user);

    @Insert("INSERT user(email, password, isEmailActive) VALUES(#{email}, #{password}, 0)")
    void addUser(User user);
}
