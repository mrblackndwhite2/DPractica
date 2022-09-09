package P2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO {
    private Connection conn;

    public ReizigerDAOPsql(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean save(Reiziger reiziger) {
        String query = "INSERT INTO reiziger (reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum)\n" +
                "VALUES (?, ?, ?, ?, TO_DATE(?, 'yyyy-mm-dd'))";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, reiziger.getId());
            pst.setString(2, reiziger.getVoorletters());
            if (reiziger.getTussenvoegsel() == null || reiziger.getTussenvoegsel().isBlank()) {
                pst.setString(3, null);
            } else {
                pst.setString(3, reiziger.getTussenvoegsel());
            }
            pst.setString(4, reiziger.getAchternaam());
            pst.setString(5, reiziger.getGeboortedatum().toString());

            if (pst.executeUpdate() != 0) { //aantal rows changed should always be 1 or 0
                pst.close();
                return true;
            }
            pst.close();
            return false;

        } catch (SQLTimeoutException e1) {
            System.out.println("Connection timed out.");
            e1.printStackTrace();
            return false;
        } catch (SQLException e2) {
            System.out.println("Something went wrong while trying to save reiziger.");
            e2.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Reiziger reiziger) {
        String query = "UPDATE reiziger\n" +
                "SET voorletters = ?,\n" +
                "tussenvoegsel = ?\n," +
                "achternaam = ?,\n" +
                "geboortedatum = TO_DATE(?, 'yyyy-mm-dd')\n" +
                "WHERE reiziger_id = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(query);

            pst.setString(1, reiziger.getVoorletters());
            if (reiziger.getTussenvoegsel() == null || reiziger.getTussenvoegsel().isBlank()) {
                pst.setString(2, null);
            } else {
                pst.setString(2, reiziger.getTussenvoegsel());
            }
            pst.setString(3, reiziger.getAchternaam());
            pst.setString(4, reiziger.getGeboortedatum().toString());
            pst.setInt(5, reiziger.getId());

            if (pst.executeUpdate() != 0) {
                pst.close();
                return true;
            }
            pst.close();
            return false;
        } catch (SQLTimeoutException e1) {
            System.out.println("Connection timed out.");
            e1.printStackTrace();
            return false;
        } catch (SQLException e2) {
            System.out.println("Something went wrong while trying to update reiziger.");
            e2.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Reiziger reiziger) {
        String query = "DELETE FROM reiziger WHERE reiziger_id = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, reiziger.getId());
            if (pst.executeUpdate() != 0) {
                pst.close();
                return true;
            }
            pst.close();
            return false;
        } catch (SQLTimeoutException e1) {
            System.out.println("Connection timed out.");
            e1.printStackTrace();
            return false;
        } catch (SQLException e2) {
            System.out.println("Something went wrong while trying to delete reiziger.");
            e2.printStackTrace();
            return false;
        }
    }

    @Override
    public Reiziger findById(int id) {
        String query = "SELECT * FROM reiziger WHERE reiziger_id = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            rs.next();
            Reiziger result = new Reiziger(rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    java.sql.Date.valueOf(rs.getString(5)));
            pst.close();
            rs.close();
            return result;
        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to find reiziger.");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Reiziger> findByGbdatum(String datum) {
        String query = "SELECT * FROM reiziger WHERE geboortedatum = CAST(? AS DATE)";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            List<Reiziger> result = new ArrayList<Reiziger>();
            pst.setString(1, datum);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                result.add(new Reiziger(rs.getInt(1),
                                rs.getString(2),
                                rs.getString(3),
                                rs.getString(4),
                                java.sql.Date.valueOf(rs.getString(5)))
                );
            }
            pst.close();
            rs.close();
            return result;
        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to find reiziger.");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Reiziger> findAll() {
        String query = "SELECT * FROM reiziger";
        try {
            Statement pst = conn.createStatement();
            ResultSet rs = pst.executeQuery(query);
            List<Reiziger> result = new ArrayList<Reiziger>();
            while (rs.next()) {
                result.add(new Reiziger(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        java.sql.Date.valueOf(rs.getString(5))));
            }
            pst.close();
            rs.close();
            return result;
        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to find reiziger.");
            e.printStackTrace();
            return null;
        }
    }
}
