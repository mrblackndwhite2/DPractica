package P1;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        //default port is 5432
        String url = "jdbc:postgresql://localhost/ovchip";
        String username = "postgres";
        String password = "postgres";
        Connection db = DriverManager.getConnection(url, username, password);

        String query = "SELECT * FROM reiziger";
        Statement st = db.createStatement();
        ResultSet rs = st.executeQuery(query);

        String line = " #%d: %S. %s%s (%s)";
        System.out.println("Alle reizigers:");
        int counter = 1;
        while (rs.next()) {
            if (rs.getString(3) == null) {   // check tussenvoegsel = null
                System.out.println(String.format(line,
                        counter,
                        rs.getString(2),
                        "",
                        rs.getString(4),
                        rs.getString(5)));
            } else {
                System.out.println(String.format(line,
                        counter,
                        rs.getString(2),
                        rs.getString(3) + " ",
                        rs.getString(4),
                        rs.getString(5)));
            }
        }
        rs.close();
        st.close();
        db.close();
    }
}
