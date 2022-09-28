package P5.patterns;

import P5.domein.*;

import java.sql.*;

public class ProductDAOPsql implements ProductDAO {
    private Connection conn;
    private OVChipkaartDAOPsql ovdao;

    public ProductDAOPsql(Connection conn) {
        this.conn = conn;
        this.ovdao = new OVChipkaartDAOPsql(conn);
    }

    public OVChipkaartDAOPsql getOvdao() {
        return ovdao;
    }

    public void setOvdao(OVChipkaartDAOPsql ovdao) {
        this.ovdao = ovdao;
    }

    //TODO CHANGE PRODUCT TO BE THE ONE THAT IS CALLED FOR KOPPELINGEN
    @Override
    public boolean save(Product p) {
        String query = "INSERT INTO product(product_nummer, naam, beschrijving, prijs) " +
                " VALUES(?, ?, ?, ?);";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, p.getProductnummer());
            pst.setString(2, p.getNaam());
            pst.setString(3, p.getBeschrijving());
            pst.setDouble(4, p.getPrijs());

            if (pst.executeUpdate() != 0) {         // Should always return 1 on success, 0 on fail
                pst.close();
                return true;
            }

            pst.close();
            return false;
        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to save Product.");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Product p) {
        String query = "UPDATE product " +
                "SET naam = ?, " +
                "beschrijving = ?, " +
                "prijs = ?, " +
                "WHERE product_nummer = ?;";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, p.getNaam());
            pst.setString(2, p.getBeschrijving());
            pst.setDouble(3, p.getPrijs());
            pst.setInt(4, p.getProductnummer());

            if (pst.executeUpdate() != 0) {         // Should always return 1 on success, 0 on fail
                pst.close();
                return true;
            }

            pst.close();
            return false;
        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to update Product.");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Product p) {
        String query = "DELETE FROM product WHERE product_nummer = ?;";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, p.getProductnummer());

            if (pst.executeUpdate() != 0) {         // Should always return 1 on success, 0 on fail
                pst.close();
                return true;
            }

            pst.close();
            return false;

        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to delete product.");
            e.printStackTrace();
            return false;
        }
    }
}
