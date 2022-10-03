package P5.patterns;

import P5.domein.*;

import java.sql.*;
import java.util.*;

public class ProductDAOPsql implements ProductDAO {
    private Connection conn;
    private OVChipkaartDAOPsql ovdao;
    private ReizigerDAOPsql rdao;

    public ProductDAOPsql(Connection conn) {
        this.conn = conn;
        this.ovdao = new OVChipkaartDAOPsql(conn);
        this.rdao = new ReizigerDAOPsql(conn);
    }

    public ProductDAOPsql(Connection conn, OVChipkaartDAOPsql ovdao) {
        this.conn = conn;
        this.ovdao = ovdao;
        this.rdao = new ReizigerDAOPsql(conn);
    }

    public ProductDAOPsql(Connection conn, ReizigerDAOPsql rdao) {
        this.conn = conn;
        this.ovdao = new OVChipkaartDAOPsql(conn);
        this.rdao = rdao;
    }

    public ProductDAOPsql(Connection conn, ReizigerDAOPsql rdao, OVChipkaartDAOPsql ovdao) {
        this.conn = conn;
        this.ovdao = ovdao;
        this.rdao = rdao;
    }

    public OVChipkaartDAOPsql getOvdao() {
        return ovdao;
    }

    public void setOvdao(OVChipkaartDAOPsql ovdao) {
        this.ovdao = ovdao;
    }

    public ReizigerDAOPsql getRdao() {
        return rdao;
    }

    public void setRdao(ReizigerDAOPsql rdao) {
        this.rdao = rdao;
    }

    //package private
    boolean save(Product p, boolean externalCall) {
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

                if (externalCall) {
                    // Check if any OV are already linked and save/update those
                    if (!(p.getKoppelingen().isEmpty())) {
                        List<OVChipkaart> all = ovdao.findAll();                // list of all existing ov in db
                        boolean exists = false;                                 // flag to see if ov already exists
                        for (OVChipkaart ov : p.getKoppelingen()) {
                            for (OVChipkaart dbOv : all) {
                                if (dbOv.getKaartnummer() == ov.getKaartnummer()) {
                                    exists = true;                              // if we find an existing ov
                                    ovdao.update(ov, false);          // update that ov to link product
                                    break;
                                }
                            }
                            if (!exists) {                           // if we didnt find existing ov
                                ovdao.save(ov, false);     // create new ov in database with link
                            }
                        }
                    }
                }
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
    public boolean save(Product p) {
        return save(p, true);
    }

    // package private, java way of default arguments
    boolean update(Product p, boolean externalCall) {
        String query = "UPDATE product " +
                "SET naam = ?, " +
                "beschrijving = ?, " +
                "prijs = ? " +
                "WHERE product_nummer = ?;";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, p.getNaam());
            pst.setString(2, p.getBeschrijving());
            pst.setDouble(3, p.getPrijs());
            pst.setInt(4, p.getProductnummer());

            if (pst.executeUpdate() != 0) {         // Should always return 1 on success, 0 on fail
                pst.close();                        // if you update without any changes still returns 1

                if (externalCall) {                 // if this was an external call, sync up all linked ov's
                    // check if linked ov's need updating/deleting
                    List<OVChipkaart> dbovList = ovdao.findByProduct(p);

                    // First check if there are connections that need to be deleted
                    if (p.getKoppelingen().isEmpty()) {         // if empty, all existing connections need to be deleted
                        for (OVChipkaart ov : dbovList) {
                            ov.tryDeleteProduct(p);             // remove the connection the db had
                            ovdao.update(ov);                   // and update db
                        }
                    } else {
                        for (OVChipkaart dbov : dbovList) {     // try to find dbov in local koppeling
                            if (!(p.getKoppelingen().contains(dbov))) {     // equals is overridden, so use contains
                                dbov.tryDeleteProduct(p);       // if we dont see dbov locally, delete the link dbov had
                                ovdao.update(dbov);             // and update the db so link also deleted there
                            }
                        }
                    }

                    // Everything that needed deleting was deleted
                    // now we check if there are any connections that need adding/inserting
                    // this part will also update any ov that have changed attributes besides product
                    dbovList = ovdao.findByProduct(p);                  // refresh dblist after the deletions
                    List<OVChipkaart> allDbovList = ovdao.findAll();

                    if (!(p.getKoppelingen().isEmpty())) {
                        for (OVChipkaart ov : p.getKoppelingen()) {
                            if (!(dbovList.contains(ov))) {             // if ov not in dbovList, then either update or save
                                boolean updated = false;

                                for (OVChipkaart dbov : allDbovList) {
                                    if (dbov.getKaartnummer() == ov.getKaartnummer()) {
                                        ovdao.update(ov);               // if ov exists in db, then update
                                        updated = true;
                                        break;
                                    }
                                }

                                if (!updated) {                         // if it wasnt updated, then
                                    ovdao.save(ov);                     // it didnt exist, so save it
                                }
                            }
                        }
                    }
                }
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

    //java way of default arguments
    @Override
    public boolean update(Product p) {
        return update(p, true);
    }

    @Override
    public boolean delete(Product p) {
        String query = "DELETE FROM product WHERE product_nummer = ?;";
        String query2 = "DELETE FROM ov_chipkaart_product WHERE product_nummer = ?;";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            PreparedStatement pst2 = conn.prepareStatement(query);
            pst.setInt(1, p.getProductnummer());
            pst2.setInt(1, p.getProductnummer());

            pst2.executeUpdate();                   // delete all links first, doesnt matter if success or not

            if (pst.executeUpdate() != 0) {         // Should always return 1 on success, 0 on fail
                pst.close();
                for (OVChipkaart ov : p.getKoppelingen()) {
                    ov.tryDeleteProduct(p);
                }
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

    public List<Product> findByOVChipkaart(OVChipkaart ov) {
        List<Product> allProduct = findAll();
        List<Product> result = new ArrayList<Product>();
        for (Product p : allProduct) {
            if (p.getKoppelingen().contains(ov)) {
                result.add(p);
            }
        }

        return result;
    }

    public List<Product> findAll() {
        String query = "SELECT t1.kaart_nummer, t2.geldig_tot, t2.klasse, t2.saldo, " +
                "t2.reiziger_id, t1.product_nummer, t3.naam, t3.beschrijving, t3.prijs " +
                "FROM ov_chipkaart_product t1, ov_chipkaart t2, product t3 " +
                "WHERE t1.kaart_nummer = t2.kaart_nummer " +
                "AND t1.product_nummer = t3.product_nummer;";

        try {
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(query);

            List<OVChipkaart> allOV = new ArrayList<OVChipkaart>();
            List<Product> allProduct = new ArrayList<Product>();
            boolean ovMatch = false;
            boolean productMatch = false;
            while (rs.next()) {
                if (!(allOV.isEmpty())) {
                    ovMatch = false;                      // reset flag
                    for (OVChipkaart ov : allOV) {
                        if (ov.getKaartnummer() == rs.getInt(1)) {      // check if ov already created
                            ovMatch = true;
                            break;
                        }
                    }

                    if (!ovMatch) {               // if not already created, create new ov
                        allOV.add(
                                new OVChipkaart(
                                        rs.getInt(1),
                                        rs.getDate(2),
                                        rs.getInt(3),
                                        rs.getDouble(4),
                                        rdao.findById(rs.getInt(5))
                                )
                        );
                    }

                } else {                                // if allOV is empty, just add OV
                    allOV.add(
                            new OVChipkaart(
                                    rs.getInt(1),
                                    rs.getDate(2),
                                    rs.getInt(3),
                                    rs.getDouble(4),
                                    rdao.findById(rs.getInt(5))
                            )
                    );
                }


                if (!(allProduct.isEmpty())) {
                    productMatch = false;                   // reset flag
                    for (Product p : allProduct) {
                        if (p.getProductnummer() == rs.getInt(6)) {      // check if product already created
                            productMatch = true;
                            break;
                        }
                    }

                    if (!productMatch) {                    // if product not created, create it
                        allProduct.add(
                                new Product(
                                        rs.getInt(6),
                                        rs.getString(7),
                                        rs.getString(8),
                                        rs.getDouble(9)
                                )
                        );
                    }

                } else {                // if allProduct is empty, just add product
                    allProduct.add(
                            new Product(
                                    rs.getInt(6),
                                    rs.getString(7),
                                    rs.getString(8),
                                    rs.getDouble(9)
                            )
                    );
                }

                // now add the links to the created objects
                for (OVChipkaart ov : allOV) {
                    if (ov.getKaartnummer() == rs.getInt(1)) {
                        for (Product p : allProduct) {
                            if (p.getProductnummer() == rs.getInt(6)) {
                                ov.tryAddProduct(p);        // will also add ov to p.koppelingen
                            }
                        }
                    }
                }
            }
            rs.close();
            st.close();
            return allProduct;
        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to findall product");
            e.printStackTrace();
            return null;
        }
    }
}
