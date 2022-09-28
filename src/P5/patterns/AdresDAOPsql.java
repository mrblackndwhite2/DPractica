package P5.patterns;

import P5.domein.Adres;
import P5.domein.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdresDAOPsql implements AdresDAO {
    private Connection conn;
    private ReizigerDAO rdao;

    public AdresDAOPsql(Connection conn, ReizigerDAO rdao) {
        this.conn = conn;
        this.rdao = rdao;
    }

    public AdresDAOPsql(Connection conn) {
        this.conn = conn;
    }

    public ReizigerDAO getRdao() {
        return rdao;
    }

    public void setRdao(ReizigerDAO rdao) {
        this.rdao = rdao;
    }

    @Override
    public boolean save(Adres adres) {
        String query = "INSERT INTO adres (adres_id, postcode, straat, huisnummer, woonplaats, reiziger_id)\n" +
                " VALUES(?, ?, ?, ?, ?, ?);";

        // check of reiziger_id has been set, 0 means not set correctly
        if (adres.getReiziger_id() == 0) {
            System.out.println("Adres heeft geen reiziger_id.");
            return false;
        }

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, adres.getId());
            pst.setString(2, adres.getPostcode());
            pst.setString(3, adres.getStraat());
            pst.setString(4, adres.getHuisnummer());
            pst.setString(5, adres.getWoonplaats());
            pst.setInt(6, adres.getReiziger_id());
            if (pst.executeUpdate() == 1) {
                pst.close();
                return true;
            }
            pst.close();
            return false;
        } catch (SQLException e) {
            System.out.println("Something went wrong when trying to save adres");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Adres adres) {
        String query = "UPDATE adres \n" +
                "SET postcode = ?, \n" +
                "straat = ?, \n" +
                "huisnummer = ?, \n" +
                "woonplaats = ?, \n" +
                "reiziger_id = ? \n" +
                "WHERE adres_id = ?;";

        // check of reiziger_id has been set, 0 means not set correctly
        if (adres.getReiziger_id() == 0) {
            System.out.println("Adres heeft geen reiziger_id.");
            return false;
        }

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, adres.getPostcode());
            pst.setString(2, adres.getStraat());
            pst.setString(3, adres.getHuisnummer());
            pst.setString(4, adres.getWoonplaats());
            pst.setInt(5, adres.getReiziger_id());
            pst.setInt(6, adres.getId());
            if (pst.executeUpdate() == 1) {
                pst.close();
                return true;
            }
            pst.close();
            return false;
        } catch (SQLException e) {
            System.out.println("Something went wrong when trying to update adres");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Adres adres) {
        String query = "DELETE FROM adres WHERE adres_id = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, adres.getId());
            if (pst.executeUpdate() == 1) {
                pst.close();
                return true;
            }
            pst.close();
            return false;
        } catch (SQLException e) {
            System.out.println("Something went wrong when trying to delete adres");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Adres findById(int id) {
        String query = "SELECT * FROM adres WHERE adres_id = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            rs.next();
            Adres result = new Adres(rs.getInt(1),
                    rs.getString(2),
                    rs.getString(4),
                    rs.getString(3),
                    rs.getString(5),
                    rs.getInt(6)
            );
            rs.close();
            pst.close();
            return result;
        } catch (SQLException e) {
            // returned null value is used often, only print stacktrace for debugging specific bugs
//            System.out.println("Something went wrong when trying to find adres by id");
//            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Adres findByReiziger(Reiziger reiziger) {
        String query = "SELECT * FROM adres WHERE reiziger_id = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, reiziger.getId());
            ResultSet rs = pst.executeQuery();
            rs.next();
            Adres result = new Adres(rs.getInt(1),
                    rs.getString(2),
                    rs.getString(4),
                    rs.getString(3),
                    rs.getString(5),
                    rs.getInt(6)
            );
            rs.close();
            pst.close();
            return result;
        } catch (SQLException e) {
            // returned null value is used often, only print stacktrace for debugging specific bugs
//            System.out.println("Something went wrong when trying to find adres by reiziger_id");
//            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Adres> findAll() {
        String query = "SELECT * FROM adres;";
        List<Adres> result = new ArrayList<Adres>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                result.add(new Adres(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(4),
                        rs.getString(3),
                        rs.getString(5),
                        rs.getInt(6)));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Something went wrong when trying to find all addresses");
            e.printStackTrace();
            return null;
        }
    }
}
