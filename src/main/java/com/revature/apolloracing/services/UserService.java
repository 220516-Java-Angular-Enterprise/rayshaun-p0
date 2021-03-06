package com.revature.apolloracing.services;

import com.revature.apolloracing.daos.UserDAO;
import com.revature.apolloracing.models.User;
import com.revature.apolloracing.util.annotations.Inject;
import com.revature.apolloracing.util.custom_exceptions.InvalidUserException;
import com.revature.apolloracing.util.custom_exceptions.ObjectDoesNotExist;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    public final String NAMEREQ =
            "Username must be alphanumeric and 8-20 characters long.";
    private final String REPREQ = "\tNo repetitive characters\n";
    private final String ALNREQ = "\tUse letters and numbers\n";
    private final String SPECREQ = "\tUse at least one of the following ~ ` ! @ # $ % ^ & * ? ; :\n";
    private final String LENREQ = "\tBe 8-256 characters long\n";
    public final String PASSREQ = "Password requirements:\n"+
            REPREQ+ALNREQ+SPECREQ+LENREQ;

    @Inject
    private final UserDAO mUserDAO;
    @Inject
    public UserService(UserDAO uDAO) {
        mUserDAO = uDAO;
    }

    public void createUser(User u) throws SQLException{
        mUserDAO.save(u);
    }

    public List<User> searchUser(String s) throws SQLException, ObjectDoesNotExist {
        return mUserDAO.getAllLike(s);
    }

    public boolean removeUser(User u) throws ObjectDoesNotExist{
        try { mUserDAO.delete(u); }
        catch(SQLException e) {
            throw new ObjectDoesNotExist(u.getUserName());
        }
        return true;
    }

    public void updateUser(User u) throws InvalidUserException {
        try { mUserDAO.update(u); }
        catch(SQLException e) {
            throw new InvalidUserException("Update Failed.\n"+
                    e.getMessage()+"\nSQLState: "+e.getSQLState());
        }
    }

    public boolean isValidUsername(String uName) throws InvalidUserException {
        if(uName.matches("^(?!.*_{2})(?=\\w{8,20}$)[^_].*[^_]$"))
            return true;
        throw new InvalidUserException("Invalid Username. "+NAMEREQ);
    }

    public boolean isNotDuplicateUsername(String uName) throws InvalidUserException {
        if (mUserDAO.findUsername(uName))
            throw new InvalidUserException("Username Unavailable. "+NAMEREQ);
        return true;
    }

    public boolean isValidPassword(String pWord) throws InvalidUserException {
        if(pWord.matches("^(?=.*[~`!@#$%^&*?;:]+)(?=.*\\d+)(?=.*[a-zA-Z]+)(?!.*(.)\\1\\1).{8,256}"))
            return true;

        StringBuilder error = new StringBuilder("Invalid Password.\n");
        if(pWord.matches("^(?!.*[~`!@#$%^&*?;:]+).*")) error.append(SPECREQ);
        if(pWord.matches("^(?!.*\\w+).*")) error.append(ALNREQ);
        if(pWord.matches("(?=.*(.)\\1\\1).*")) error.append(REPREQ);
        if(pWord.matches("^.{0,7}$|^.{257,}$")) error.append(LENREQ);
        throw new InvalidUserException(new String(error));
    }

    public User getValidCredentials(String uName, String pWord)
            throws SQLException, InvalidUserException {
        User out;
        out = mUserDAO.getByCredentials(uName, pWord);
        return out;
    }

    public boolean isValidEmail(String mail) throws InvalidUserException {
        if(mail.toLowerCase().matches("^(?!.*[-.]{2})[\\w.-]+(?!@\\.)@[a-z-.]+\\.[a-z]{2,3}"))
            return true;
        throw new InvalidUserException("Please enter a valid email");
    }

    public boolean isNotDuplicateEmail(String email) throws InvalidUserException, SQLException {
        if(mUserDAO.findEmail(email))
            throw new InvalidUserException("Email already exists in system.");
        else return true;
    }

    public boolean isValidPhone(String pNum) throws InvalidUserException {
        if(pNum.matches("^(|(\\d{1,3}|(1|44)-\\d{3}) ?)(?!\\(\\d{3}\\)[.-])(\\(\\d{3}\\) ?|\\d{3})[ .-]?\\d{3}[ .-]?\\d{4}"))
            return true;
        throw new InvalidUserException("Please enter a valid phone number");
    }

}
