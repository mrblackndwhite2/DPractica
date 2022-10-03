package P5.patterns;

import P5.domein.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {
    private Connection conn;
    private ReizigerDAOPsql rdao;
    private ProductDAOPsql pdao;

    public OVChipkaartDAOPsql(Connection conn) {
        this.conn = conn;
        this.rdao = new ReizigerDAOPsql(this.conn);
        this.pdao = new ProductDAOPsql(this.conn);
    }

    public OVChipkaartDAOPsql(Connection conn, ReizigerDAOPsql rdao) {
        this.conn = conn;
        this.rdao = rdao;
        this.pdao = new ProductDAOPsql(this.conn, rdao, this);
    }

    public OVChipkaartDAOPsql(Connection conn, ReizigerDAOPsql rdao, ProductDAOPsql pdao) {
        this.conn = conn;
        this.rdao = rdao;
        this.pdao = pdao;
    }

    public OVChipkaartDAOPsql(Connection conn, ProductDAOPsql pdao) {
        this.conn = conn;
        this.rdao = new ReizigerDAOPsql(this.conn);
        this.pdao = pdao;
    }

    public ReizigerDAOPsql getRdao() {
        return rdao;
    }

    public void setRdao(ReizigerDAOPsql rdao) {
        this.rdao = rdao;
    }

    public ProductDAOPsql getPdao() {
        return pdao;
    }

    public void setPdao(ProductDAOPsql pdao) {
        this.pdao = pdao;
    }

    boolean save(OVChipkaart ovchipkaart, boolean externalCall) {
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

                if (externalCall) {             // if its an external call, sync up linked producten
                    // If product doesnt exist in database, create it
                    // if it does, check if it needs updating
                    if (!(ovchipkaart.getProducten().isEmpty())) {
                        boolean found;

                        List<Product> dbProducten = pdao.findAll();
                        for (Product localProduct : ovchipkaart.getProducten()) {
                            found = false;                               //reset flag

                            for (Product dbProduct : dbProducten) {
                                if (localProduct.getProductnummer() == dbProduct.getProductnummer()) {
                                    found = true;

                                    // now check if it needs updating
                                    if (!(localProduct.equals(dbProduct))) {
                                        pdao.update(localProduct, false);
                                    }

                                    break;
                                }
                            }

                            if (!found) {                           // if we didnt find product, create it
                                pdao.save(localProduct, false);
                            }
                        }
                    }
                }

                // for any type of call, update link table
                updateLinkTable(ovchipkaart);
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
    public boolean save(OVChipkaart ovchipkaart) {
        return save(ovchipkaart, true);
    }

    boolean update(OVChipkaart ovchipkaart, boolean externalCall) {
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

                if (externalCall) {
                    // If product doesnt exist in database, create it
                    // if it does, check if it needs updating
                    // if a productlink was deleted, updateLinkTable takes care of that
                    if (!(ovchipkaart.getProducten().isEmpty())) {
                        boolean found;

                        List<Product> dbProducten = pdao.findAll();
                        for (Product localProduct : ovchipkaart.getProducten()) {
                            found = false;                               //reset flag

                            for (Product dbProduct : dbProducten) {
                                if (localProduct.getProductnummer() == dbProduct.getProductnummer()) {
                                    found = true;

                                    // now check if it needs updating
                                    if (!(localProduct.equals(dbProduct))) {
                                        pdao.update(localProduct, false);
                                    }

                                    break;
                                }
                            }

                            if (!found) {                           // if we didnt find product, create it
                                pdao.save(localProduct, false);
                            }
                        }
                    }
                }

                // for any type of call, update link table
                updateLinkTable(ovchipkaart);
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
    public boolean update(OVChipkaart ovchipkaart) {
        return update(ovchipkaart, true);
    }

    @Override
    public boolean delete(OVChipkaart ovchipkaart) {
        String deleteOV = "DELETE FROM ov_chipkaart WHERE kaart_nummer = ?;";
        String deleteLink = "DELETE FROM ov_chipkaart_product WHERE kaart_nummer = ?;";

        try {
            PreparedStatement pstOv = conn.prepareStatement(deleteOV);
            PreparedStatement pstLink = conn.prepareStatement(deleteLink);
            pstOv.setInt(1, ovchipkaart.getKaartnummer());
            pstLink.setInt(1, ovchipkaart.getKaartnummer());

            pstLink.executeUpdate();           // delete any links before deleting OV itself

            if (pstOv.executeUpdate() != 0) {     //should always return 1 if successful
                pstOv.close();
                return true;
            }
            pstOv.close();                        //else if 0
            return false;
        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to delete ovchip.");
            e.printStackTrace();
            return false;
        }
    }

    // because assignment states to ignore status and last_update
    // any new entries will have NULL values in those fields
    private void updateLinkTable(OVChipkaart ov) {
        String deleteQuery = "DELETE FROM ov_chipkaart_product " +
                "WHERE kaart_nummer = ? AND product_nummer = ?;";
        String insertQuery = "INSERT INTO ov_chipkaart_product(kaart_nummer, product_nummer, status, last_update) " +
                "VALUES(?, ?, NULL, NULL)";

        // first check if delete is necessary
        List<Product> dbProducten = pdao.findByOVChipkaart(ov);
        boolean found;
        for (Product dbProduct : dbProducten) {
            found = false;                          // reset flag

            for (Product localProduct : ov.getProducten()) {
                if (dbProduct.getProductnummer() == localProduct.getProductnummer()) {
                    found = true;
                    break;
                }
            }

            if (!found) {           // if we didnt find product, that means the link need to be deleted
                try {
                    PreparedStatement pst = conn.prepareStatement(deleteQuery);
                    pst.setInt(1, ov.getKaartnummer());
                    pst.setInt(2, dbProduct.getProductnummer());
                    pst.executeUpdate();
                    pst.close();
                } catch (SQLException e) {
                    System.out.println("Something went wrong while trying to delete ov-product link");
                    e.printStackTrace();
                }
            }
        }

        // now check if we need to add links
        // use the same db, it is outdated, but only with deleted links
        // probably more efficient than getting the entire db info again
        // basically exact same loop as last one, but db and local switch places
        for (Product localProduct : ov.getProducten()) {
            found = false;                              // assume we need to add link

            for (Product dbProduct : dbProducten) {
                if (localProduct.getProductnummer() == dbProduct.getProductnummer()) {
                    found = true;
                    break;
                }
            }

            if (!found) {           // if we didnt find product, that means the link need to be inserted
                try {
                    PreparedStatement pst = conn.prepareStatement(insertQuery);
                    pst.setInt(1, ov.getKaartnummer());
                    pst.setInt(2, localProduct.getProductnummer());
                    pst.executeUpdate();
                    pst.close();
                } catch (SQLException e) {
                    System.out.println("Something went wrong while trying to insert ov-product link");
                    e.printStackTrace();
                }
            }
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
            System.out.println("Something went wrong while trying to findAll.");
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

    public List<OVChipkaart> findByProduct(Product p) {
        String query = "SELECT t2.kaart_nummer, geldig_tot, klasse, saldo, reiziger_id " +
                "FROM ov_chipkaart_product t1, ov_chipkaart t2 " +
                "WHERE t1.kaart_nummer = t2.kaart_nummer " +
                "AND t1.product_nummer = ?;";

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, p.getProductnummer());

            ResultSet rs = pst.executeQuery();
            List<OVChipkaart> result = new ArrayList<OVChipkaart>();

            while (rs.next()) {
                result.add(
                        new OVChipkaart(
                                rs.getInt(1),
                                rs.getDate(2),
                                rs.getInt(3),
                                rs.getDouble(4),
                                rdao.findById(rs.getInt(5)))
                );
            }

            return result;
        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to findByProduct");
            e.printStackTrace();
            return null;
        }
    }
}
