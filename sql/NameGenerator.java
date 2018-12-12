package sql;

import java.sql.*;

public class NameGenerator {

    final String driver = "com.mysql.cj.jdbc.Driver";
    final String mydb_jurl = "jdbc:mysql://localhost:3306/names?useSSL=false&serverTimezone=Europe/Moscow&noAccessToProcedureBodies=true";
    final String mydb_name = "name";
    final String mydb_password = "name";

    private boolean active = false;
    private PreparedStatement stmt = null;
    private Connection connection = null;

    private String first;   // имя
    private String second;  // фамилия

    private int idLaunch;

    public NameGenerator(int ecount, int fcount, int pcount, int capacity) {
        open();
        getLaunchId(ecount, fcount, pcount, capacity);
    }

    public void generate() {
        if(active) {
            try {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    first = rs.getString(1);
                    second = rs.getString(2);
                }

                rs.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void open() {
        if(!active) {
            String sql;

            try {
                Class.forName(driver);
                connection = DriverManager.getConnection(mydb_jurl, mydb_name, mydb_password);
                sql = "CALL get_random_name()";
                stmt = connection.prepareStatement(sql);
                active = true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void insertInfo(String firstname, String lastname, int source, int destination,
                           Double generateTime, Double inTime, Double outTime,
                           String elevStrategy) {
        if(active) {
            String sql;
            PreparedStatement ctmt;
            try {
                sql = "CALL insertInfo(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                ctmt = connection.prepareStatement(sql);
                ctmt.setInt(1, idLaunch);
                ctmt.setString(2, firstname);
                ctmt.setString(3, lastname);
                ctmt.setInt(4, source);
                ctmt.setInt(5, destination);
                ctmt.setDouble(6, generateTime);
                ctmt.setDouble(7, inTime);
                ctmt.setDouble(8, outTime);
                ctmt.setString(9, elevStrategy);
                ctmt.execute();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void getLaunchId(int ecount, int fcount, int pcount, int capacity) {
        if(active) {
            String sql;
            CallableStatement ctmt;
            try {
                sql = "CALL new_launch(?, ?, ?, ?, ?)";
                ctmt = connection.prepareCall(sql);
                ctmt.setInt(1, ecount);
                ctmt.setInt(2, fcount);
                ctmt.setInt(3, pcount);
                ctmt.setInt(4, capacity);
                ctmt.registerOutParameter(5, Types.INTEGER);
                ctmt.execute();
                idLaunch = ctmt.getInt(5);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void close() {
        if(active) {
            String sql = "CALL close_launch(?)";
            try {
                stmt = connection.prepareStatement(sql);
                stmt.setInt(1, idLaunch);
                stmt.execute();

                stmt.close();
                connection.close();
                active = false;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

}
