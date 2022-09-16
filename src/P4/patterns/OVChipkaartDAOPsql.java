package P4.patterns;

import P4.domein.OVChipkaart;
import P4.domein.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {
    private Connection conn;

    private ReizigerDAOPsql rdao;

    public OVChipkaartDAOPsql(Connection conn) {
        this.conn = conn;
        this.rdao = new ReizigerDAOPsql(this.conn);
    }

    public OVChipkaartDAOPsql(Connection conn, ReizigerDAOPsql rdao) {
        this.conn = conn;
        this.rdao = rdao;
    }

    public ReizigerDAOPsql getRdao() {
        return rdao;
    }

    public void setRdao(ReizigerDAOPsql rdao) {
        this.rdao = rdao;
    }

    @Override
    public boolean save(OVChipkaart ovchipkaart) {
        String query = "INSERT INTO ov_chipkaart(kaart_nummer, geldig_tot, klasse, saldo, reiziger_id)\n" +
                "VALUES(?, TO_DATE(?, 'yyyy-mm-dd'), ?, ?, ?)";
        try {
            if (ovchipkaart.getReiziger() == null) {
                System.out.println("OVChipkaart has no linked reiziger. Could not save.");
                return false;
            }
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, ovchipkaart.getKaartnummer());
            pst.setString(2, ovchipkaart.getVervaldatum().toString());
            pst.setInt(3, ovchipkaart.getKlasse());
            pst.setDouble(4, ovchipkaart.getSaldo());
            pst.setInt(5, ovchipkaart.getReiziger().getId());

            if (pst.executeUpdate() != 0) {     //should always return 1 if successful
                pst.close();
                return true;
            }

            pst.close();                        //else if 0
            return false;

        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to save ovchip.");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(OVChipkaart ovchipkaart) {
        String query = "UPDATE ov_chipkaart \n " +
                "SET geldig_tot = TO_DATE(?, 'yyyy-mm-dd'), " +
                "klasse = ?, " +
                "saldo = ?, " +
                "reiziger_id = ? " +
                "WHERE kaart_nummer = ?;";
        try {
            if (ovchipkaart.getReiziger() == null) {
                System.out.println("OVChipkaart has no linked reiziger. Could not save.");
                return false;
            }
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, ovchipkaart.getVervaldatum().toString());
            pst.setInt(2, ovchipkaart.getKlasse());
            pst.setDouble(3, ovchipkaart.getSaldo());
            pst.setInt(4, ovchipkaart.getReiziger().getId());
            pst.setInt(5, ovchipkaart.getKaartnummer());

            if (pst.executeUpdate() != 0) {     //should always return 1 if successful
                pst.close();
                return true;
            }
            pst.close();                        //else if 0
            return false;

        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to update ovchip.");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(OVChipkaart ovchipkaart) {
        String query = "DELETE FROM ov_chipkaart WHERE kaart_nummer = ?;";

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, ovchipkaart.getKaartnummer());

            if (pst.executeUpdate() != 0) {     //should always return 1 if successful
                pst.close();
                return true;
            }
            pst.close();                        //else if 0
            return false;
        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to delete ovchip.");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) {
        String query = "SELECT * FROM ov_chipkaart WHERE reiziger_id = ?;";

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, reiziger.getId());

            ResultSet rs = pst.executeQuery();
            List<OVChipkaart> result = new ArrayList<OVChipkaart>();

            while (rs.next()) {
                result.add(new OVChipkaart(
                        rs.getInt(1),
                        rs.getDate(2),
                        rs.getInt(3),
                        rs.getDouble(4),
                        reiziger));
            }
            pst.close();
            rs.close();
            return result;

        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to findbyreiziger.");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<OVChipkaart> findAll() {
        String query = "SELECT * FROM ov_chipkaart;";

        try {
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(query);
            List<OVChipkaart> result = new ArrayList<OVChipkaart>();

            while (rs.next()) {
                result.add(new OVChipkaart(
                        rs.getInt(1),
                        rs.getDate(2),
                        rs.getInt(3),
                        rs.getDouble(4),
                        rdao.findById(rs.getInt(5))));
            }
            st.close();
            rs.close();
            return result;

        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to findbyreiziger.");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public OVChipkaart findByKaartnummer(int kaartnummer) {
        String query = "SELECT * FROM ov_chipkaart WHERE kaart_nummer = ?;";

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, kaartnummer);

            ResultSet rs = pst.executeQuery();
            rs.next();
            OVChipkaart result = new OVChipkaart(
                    rs.getInt(1),
                    rs.getDate(2),
                    rs.getInt(3),
                    rs.getDouble(4),
                    rdao.findById(rs.getInt(5)));
            pst.close();
            rs.close();
            return result;
        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to findbykaartnummer.");
            e.printStackTrace();
            return null;
        } catch (Exception e2) {
            System.out.println("Something went wrong IN CODE findbykaartnummer.");
            e2.printStackTrace();
            return null;
        }
    }
}
