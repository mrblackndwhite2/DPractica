package P5.patterns;

import P5.domein.OVChipkaart;
import P5.domein.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO {
    private Connection conn;
    private AdresDAOPsql adao;
    private OVChipkaartDAOPsql ovdao;

    public ReizigerDAOPsql(Connection conn) {
        this.conn = conn;
        this.adao = new AdresDAOPsql(this.conn, this);
        this.ovdao = new OVChipkaartDAOPsql(this.conn, this);
    }

    public AdresDAOPsql getAdao() {
        return adao;
    }

    public void setAdao(AdresDAOPsql adao) {
        this.adao = adao;
    }

    public OVChipkaartDAOPsql getOvdao() {
        return ovdao;
    }

    public void setOvdao(OVChipkaartDAOPsql ovdao) {
        this.ovdao = ovdao;
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

                // Check if address exists, else add address
                if (reiziger.getAdres() != null) {  //if reiziger has adres
                    if (adao.findById(reiziger.getAdres().getId()) == null) { //if adres doesnt exist in db
                        adao.save(reiziger.getAdres());
                    }
                }

                // Check OV
                if (!(reiziger.getOvList().isEmpty())) {
                    // Get all linked OV from db
                    List<OVChipkaart> linkedOVList = ovdao.findByReiziger(reiziger);

                    // check if local OV and db OV are the same, if not, update
                    // if local OV not saved at all, save it
                    if (linkedOVList != null) {
                        for (OVChipkaart local : reiziger.getOvList()) {    // for each local ov
                            boolean exists = false;                         // assume theres no linked ov

                            for (OVChipkaart linked : linkedOVList) {       // try to find linked ov
                                if (linked.getKaartnummer() == local.getKaartnummer()) {
                                    exists = true;                          // set flag when you find it
                                    if (!(linked.equals(local))) {
                                        ovdao.update(local);                // see if it needs updating
                                    }
                                    break;                                  // look at next LOCAL ov
                                }
                            }
                            if (!exists) {                  // if you didnt find linked ov
                                ovdao.save(local);          // save ov
                            }
                        }
                    } else {
                        for (OVChipkaart local: reiziger.getOvList()) {
                            ovdao.save(local);
                        }
                    }
                }
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

                if (reiziger.getAdres() != null) {  //if reiziger has adres
                    if (adao.findById(reiziger.getAdres().getId()) == null) {       // if adres doesnt exist in db
                        adao.save(reiziger.getAdres());
                    } else {                                                        // else if it does exist in db
                        adao.update(reiziger.getAdres());
                    }
                }

                if (!(reiziger.getOvList().isEmpty())) {
                    // Get all linked OV from db
                    List<OVChipkaart> linkedOVList = ovdao.findByReiziger(reiziger);

                    // check if local OV and db OV are the same, if not, update
                    // first check if db OV need to be deleted
                    // then check if any db OV need to be updated
                    // if local OV not saved at all, save it in db
                    if ((linkedOVList != null) && !(linkedOVList.isEmpty())) {
                        for (OVChipkaart linked : linkedOVList) {       // for each db ov
                            boolean found = false;                      // assume it needs deleting

                            for (OVChipkaart local : reiziger.getOvList()) {    // compare wth every local ov
                                if (linked.getKaartnummer() == local.getKaartnummer()) {
                                    found = true;                           // if you find local match
                                    break;                                  // set flag
                                }
                            }

                            if (!found) {                               // if you didnt find match
                                ovdao.delete(linked);                   // delete db ov
                            }
                        }

                        // part that updates ov attributes
                        for (OVChipkaart local : reiziger.getOvList()) {    // for each local ov
                            boolean exists = false;                         // assume theres no linked ov

                            for (OVChipkaart linked : linkedOVList) {       // try to find linked ov
                                if (linked.getKaartnummer() == local.getKaartnummer()) {
                                    exists = true;                          // set flag when you find it
                                    if (!(linked.equals(local))) {
                                        ovdao.update(local);                // see if it needs updating
                                    }
                                    break;                                  // look at next LOCAL ov
                                }
                            }
                            if (!exists) {                  // if you didnt find linked ov
                                ovdao.save(local);          // save ov
                            }
                        }
                    } else {
                        for (OVChipkaart local: reiziger.getOvList()) {
                            ovdao.save(local);
                        }
                    }
                }
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

            // Delete ov before reiziger, foreign key constraint
            List<OVChipkaart> linkedOVList = ovdao.findByReiziger(reiziger);
            if (linkedOVList != null) {
                for (OVChipkaart linked : linkedOVList) {
                    ovdao.delete(linked);
                }
            }

            // now delete reiziger and adres if needed
            if (pst.executeUpdate() != 0) {
                pst.close();

                // delete all possible connections to reiziger to keep db valid
                if (reiziger.getAdres() != null) {                              //if reiziger object had address linked
                    if (adao.findById(reiziger.getAdres().getId()) != null) {    //if this address exists in db
                        adao.delete(reiziger.getAdres());
                    }
                }

                if (adao.findByReiziger(reiziger) != null) {        //if there was address linked in db
                    adao.delete(adao.findByReiziger(reiziger));
                }

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
                    Date.valueOf(rs.getString(5)));
            pst.close();
            rs.close();

            // check if there is address linked in db
            if (adao.findByReiziger(result) != null) {
                result.setAdres(adao.findByReiziger(result));
            }

            List<OVChipkaart> linkedOVList = ovdao.findByReiziger(result);
            if (linkedOVList != null) {
                result.setOvList(linkedOVList);
            }

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
            AdresDAOPsql adao = new AdresDAOPsql(this.conn, this);

            while (rs.next()) {
                Reiziger match = new Reiziger(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        Date.valueOf(rs.getString(5)));

                if (adao.findByReiziger(match) != null) {
                    match.setAdres(adao.findByReiziger(match));
                }

                List<OVChipkaart> linkedOVList = ovdao.findByReiziger(match);
                if (linkedOVList != null) {
                    match.setOvList(linkedOVList);
                }
                result.add(match);
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
                Reiziger match = new Reiziger(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        Date.valueOf(rs.getString(5)));

                if (adao.findByReiziger(match) != null) {
                    match.setAdres(adao.findByReiziger(match));
                }

                List<OVChipkaart> linkedOVList = ovdao.findByReiziger(match);
                if (linkedOVList != null) {
                    match.setOvList(linkedOVList);
                }
                result.add(match);
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
