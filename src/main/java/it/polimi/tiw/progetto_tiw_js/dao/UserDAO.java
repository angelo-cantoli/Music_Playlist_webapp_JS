package it.polimi.tiw.progetto_tiw_js.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.progetto_tiw_js.beans.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {
    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Method that search in the DB is the user exist or if the userName is already used by an other user
     * @param userName is the userName I have to verify if it's already in the DB
     * @return true if it exists, false otherwise
     * @throws SQLException
     */
    public boolean findUser(String userName) throws SQLException{
        boolean result = false;
        String query = "SELECT userName FROM user WHERE userName = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1 , userName);

            resultSet = pStatement.executeQuery();

            if(resultSet.next()) result = true;
        }catch(SQLException e) {
            throw new SQLException(e);
        }finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            }catch(Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if(pStatement != null) {
                    pStatement.close();
                }
            }catch(Exception e2) {
                throw new SQLException(e2);
            }
        }
        return result;
    }

    /**
     * Method that verify if userName and Password, inserted during the login, are correct
     * @param userName
     * @param password
     * @return true is password and userName are right, false if userName doesn't exist or password is wrong
     * @throws SQLException
     */
    public User checkAuthentication(String userName, String password) throws SQLException{
        String query ="SELECT * FROM user WHERE userName = ?";
        ResultSet resultSet = null;
        PreparedStatement pStatement = null;

        try{
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1 , userName);

            resultSet = pStatement.executeQuery();

            if(resultSet.next()) {
                if(BCrypt.checkpw(password, resultSet.getString("password"))) {
                    return new User(resultSet.getString("userName") , resultSet.getString("password") , resultSet.getInt("id"), resultSet.getString("firstName"), resultSet.getString("lastName"));
                }

            }
        }catch(SQLException e) {
            throw new SQLException(e);
        }finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
            }catch(Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if(pStatement != null) {
                    pStatement.close();
                }
            }catch(Exception e2) {
                throw new SQLException(e2);
            }
        }
        return null;
    }


    public boolean addUser(String userName , String password, String firstName, String lastName) throws SQLException{
        int code = 0;

        if(findUser(userName) == true)
            return false;

        String query = "INSERT into user (userName, password, firstName, lastName) VALUES(?, ?, ?, ?)";
        PreparedStatement pStatement = null;

        try {
            pStatement = connection.prepareStatement(query);
            pStatement.setString(1 , userName);
            pStatement.setString(2 , password);
            pStatement.setString(3, firstName);
            pStatement.setString(4, lastName);

            code = pStatement.executeUpdate();//code is the number of updated row in the DB
        }catch(SQLException e) {
            throw new SQLException(e);
        }finally {
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
        }
        return (code > 0);
    }

}
