package com.gent00;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class HTTPSessionBean {

    @Resource(lookup = "java:/MariadbDS")
    public DataSource ds;

    @Transactional(Transactional.TxType.REQUIRED)
    public boolean createSession(String jsesh, String previousJsesh, Map<String, Object> dic) throws SQLException {
        boolean previousSessionFound = false;
        System.out.println("CREATE session for " + jsesh + " with previous " + previousJsesh);
        Connection con = ds.getConnection();
        PreparedStatement ps = null;
//        PreparedStatement ps = con.prepareStatement("insert into data(id,k,v) values(?,?,?)");
//        ps.setString(1, jsesh);
//        ps.setString(2, "DTTM");
//        ps.setString(3, String.valueOf(System.currentTimeMillis()));
//        int row = ps.executeUpdate();
//        ps.close();

        //Copy old session contents
        if (previousJsesh != null && !previousJsesh.isEmpty()) {
            boolean isLocked = lockSessionByID(previousJsesh, con); // Lock previous session
            if (isLocked) { //If rows were locked, copy k/v into new session.
                ps = con.prepareStatement("select k,v from data where id=? AND k is not null for update");
                ps.setString(1, previousJsesh);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    previousSessionFound = true;
                    String k = rs.getString(1);
                    String v = rs.getString(2);
                    System.out.println("XFER " + k + " -> " + v);
                    addAttribute(jsesh, k, v);//This copies to new database session. Actual HTTP session somewhere else.
                    if (dic != null) {
                        dic.put(k, v);
                    }
                }
                rs.close();
                ps.close();

            } else {
                System.out.println("Previous session " + previousJsesh + " not found in DB. Empty session created.");
            }
//            deleteSession(previousJsesh);
        }
        System.out.println("CREATE-DONE session for " + jsesh + " with previous " + previousJsesh);


        con.close();
        return previousSessionFound;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public boolean deleteSession(String jsesh) throws SQLException {
        Connection con = ds.getConnection();
        PreparedStatement ps = con.prepareStatement("delete from data where id=?");
        ps.setString(1, jsesh);
        int row = ps.executeUpdate();
        ps.close();
        con.close();
        return row > 0;
    }


    @Transactional(Transactional.TxType.REQUIRED)
    public int countSessions() throws SQLException {
        Connection con = ds.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT count(*) from data;");
        ResultSet rs = ps.executeQuery();
        rs.next();
        int sessions = rs.getInt(1);
        rs.close();
        con.close();
        System.out.println(sessions);
        return sessions;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void addAttribute(String jsesh, String attributeName, Object attributeValue) throws SQLException {
        Connection con = ds.getConnection();
        boolean foundRowsToUpdate = lockSessionByIDForK(jsesh, attributeName, con);
        if (foundRowsToUpdate) { // If you didn't find rows to update, you cannot "add" new ones.
            System.out.println("Attempting to update for " + jsesh + ":" + attributeName + ":" + attributeValue);
            PreparedStatement ps = con.prepareStatement("update data set v=? where id=? and k=?");
            ps.setString(1, attributeValue.toString());
            ps.setString(2, jsesh);
            ps.setString(3, attributeName);
            ps.executeUpdate();
            ps.close();
        }else{
            System.out.println("Attempting to insert for " + jsesh + ":" + attributeName + ":" + attributeValue);
            PreparedStatement ps = con.prepareStatement("insert into data(id,k,v) values(?,?,?)");
            ps.setString(1, jsesh);
            ps.setString(2, attributeName);
            ps.setString(3, attributeValue.toString());
            ps.executeUpdate();
            ps.close();
        }
        con.close();
        System.out.println("Added " + jsesh + " " + attributeName + ":" + attributeValue);

    }

    public void removeAttribute(String jsesh, String attributeName, Object attributeValue) throws SQLException {
        System.out.println("Removing " + jsesh + " " + attributeName + ":" + attributeValue);
        Connection con = ds.getConnection();
        PreparedStatement ps = con.prepareStatement("select id,k,v from data where id=? for update");
        ps.setString(1, jsesh);
        int row = ps.executeUpdate();
        //Locked now

        ps = con.prepareStatement("delete from data where id=? and k=?");
        ps.setString(1, jsesh);
        ps.setString(2, attributeName);
        ps.executeUpdate();
        ps.close();
        con.close();
        System.out.println("Removed attribute " + jsesh + " " + attributeName + ":" + attributeValue);
    }

    private boolean lockSessionByID(String jsesh, Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement("select id,k,v from data where id=? for update");
        ps.setString(1, jsesh);
        boolean truth = ps.execute();
//        System.out.println("Found " +truth + " for " + jsesh);
        return truth;
    }

    private boolean lockSessionByIDForK(String jsesh, String k, Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement("select id,k,v from data where id=? and k=? for update");
        ps.setString(1, jsesh);
        ps.setString(2, k);
        ResultSet rs =  ps.executeQuery();
        boolean truth = rs.next();
        System.out.println("TRUTH IS" + truth);

        return truth;
    }


    public void replaceAttribute(String jsesh, String attributeName, Object attributeValue) throws SQLException {
        System.out.println("Replacing " + jsesh + " " + attributeName + ":" + attributeValue);
        Connection con = ds.getConnection();
        lockSessionByID(jsesh, con);
        //Locked now
        PreparedStatement ps = con.prepareStatement("update data set v=? where id=? and k=?");
        ps.setString(1, attributeValue.toString());
        ps.setString(2, jsesh);
        ps.setString(3, attributeName);
        ps.executeUpdate();
        ps.close();
        con.close();
        System.out.println("Replaced attribute " + jsesh + " " + attributeName + ":" + attributeValue);
    }
}
