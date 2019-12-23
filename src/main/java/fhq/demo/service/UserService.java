package fhq.demo.service;

import fhq.demo.bean.User;
import fhq.demo.dao.UserDao;
import fhq.demo.exception.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * @author fhq
 * @date 2019/12/22 16:30
 */
@Service
public class UserService {
    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public User findUserByEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return null;
        }

        return userDao.findUserByEmail(email);
    }

    @Transactional
    public void updateUser(User user) throws UserException {
        if (user == null) {
            throw new UserException("user can not be null");
        }

        User userInDb = findUserByEmail(user.getEmail());
        if (userInDb == null) {
            throw new UserException("don't find user");
        }

        userDao.updateUser(user);
    }

    /**
     * 激活用户
     *
     * @param user 待激活用户
     * @throws UserException
     */
    @Transactional
    public void activateEmail(User user) throws UserException {
        if (user == null) {
            throw new UserException("user can not be null");
        }
        user.setEmailActive(true);
        updateUser(user);
    }

    @Transactional
    public void addUser(User user) throws UserException {
        if (user == null) {
            throw new UserException("user can not be null");
        }
        User userInDb = userDao.findUserByEmail(user.getEmail());
        if (userInDb != null) {
            throw new UserException("this email is activated");
        }
        userDao.addUser(user);
    }
}
