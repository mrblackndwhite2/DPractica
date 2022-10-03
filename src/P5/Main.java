package P5;

import P5.domein.*;
import P5.patterns.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {
    private static Connection connection;

    public static void main(String[] args) {
        try {
            getConnection();
            ReizigerDAOPsql rdao = new ReizigerDAOPsql(connection);
            AdresDAOPsql adao = rdao.getAdao();
            OVChipkaartDAOPsql ovdao = rdao.getOvdao();
            ProductDAOPsql pdao = ovdao.getPdao();
            pdao.setOvdao(ovdao);

//            testReizigerDAO(rdao);
//            testAdresDAO(adao, rdao);
//            testOVDAO(rdao, ovdao);
            testProductDAO(rdao, ovdao, pdao);
            closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost/ovchip";
        String username = "postgres";
        String password = "postgres";
        connection = DriverManager.getConnection(url, username, password);
    }

    private static void closeConnection() throws SQLException {
        connection.close();
    }

    /**
     * P2. Reiziger DAO: persistentie van een klasse
     * <p>
     * Deze methode test de CRUD-functionaliteit van de Reiziger DAO
     *
     * @throws SQLException
     */
    private static void testReizigerDAO(ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        // Haal alle reizigers op uit de database
        List<Reiziger> reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();

        // Maak een nieuwe reiziger aan en persisteer deze in de database
        String gbdatum = "1981-03-14";
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", Date.valueOf(gbdatum));
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        // Vind reiziger met id en update deze
        Reiziger oldr = rdao.findById(77);
        System.out.println("[Test] voor de update findbyid: " + oldr);
        System.out.println();

        oldr.setAchternaam("Bakker");
        oldr.setTussenvoegsel("de");
        oldr.setGeboortedatum(Date.valueOf("1998-08-11"));
        rdao.update(oldr);
        List<Reiziger> rlist = rdao.findByGbdatum("1998-08-11");
        System.out.println();

        System.out.println("[Test} na update o.a. geboortedatum, findByGbdatum: ");
        for (Reiziger r : rlist) {
            System.out.println(r);
        }
        System.out.println();

        // Delete
        System.out.println("[Test] voor delete, reizigers.size = " + rdao.findAll().size());
        rdao.delete(rdao.findById(77));
        System.out.println("[Test] na delete, reizigers.size = " + rdao.findAll().size());
        System.out.println();
    }

    private static void testAdresDAO(AdresDAO adao, ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test AdresDAO -------------");

        Reiziger testp = new Reiziger(100,
                "voor",
                "tussen",
                "achter",
                Date.valueOf("2000-01-01"));
        Reiziger testp2 = new Reiziger(101,
                "voor",
                "tussen",
                "achter",
                Date.valueOf("2000-01-01"));
        Adres testad = new Adres(99, "postcode", "straat", "hsnr", "plaats", 100);

        // save test persons and address
        rdao.save(testp);
        rdao.save(testp2);
        adao.save(testad);
        List<Adres> all = adao.findAll();

        // findAll
        System.out.println("[TEST] find all addresses:");
        for (Adres a : all) {
            System.out.println(a);
        }
        System.out.println();

        // update
        testad.setReiziger_id(101);
        testad.setPostcode("postcode2");
        adao.update(testad);
        System.out.println("[TEST]na update van testadres, findbyID:");
        System.out.println(adao.findById(99));
        System.out.println();

        // findByReiziger
        System.out.println("[TEST]nu findbyreiziger:");
        System.out.println(adao.findByReiziger(rdao.findById(101)));
        System.out.println();

        adao.delete(testad);
        rdao.delete(testp);
        rdao.delete(testp2);
        System.out.println("[TEST] na deleten address and test persons:");
        all = adao.findAll();
        for (Adres a : all) {
            System.out.println(a);
        }
    }

    private static void testOVDAO(ReizigerDAO rdao, OVChipkaartDAO ovdao){
        System.out.println("-------------OVDAO TEST---------------");
        Reiziger r1 = new Reiziger(100, "voor1", null,"achter1", Date.valueOf("2000-01-01"));
        OVChipkaart ov1 = new OVChipkaart(1, Date.valueOf("2022-09-16"), 2, 5.00);
        OVChipkaart ov2 = new OVChipkaart(2, Date.valueOf("2022-11-25"), 1, 254.00);
        System.out.println("try add ov1 to r1 outcome: " + r1.tryAddOVChipkaart(ov1));
        System.out.println("try add ov2 to r1 outcome: " + r1.tryAddOVChipkaart(ov2));
        System.out.println("printing r1.getOVList:\n" + r1.getOvList());
        System.out.println();
        System.out.println("trying to save: " + rdao.save(r1));
        System.out.println("trying ovdao.FindByReiziger:");
        List<OVChipkaart> linkedOV = ovdao.findByReiziger(r1);
        System.out.println(linkedOV);
        System.out.println();
        System.out.println("attempting update both saldo to 50.00");
        for (OVChipkaart linked : linkedOV) {
            linked.setSaldo(50.00);
            System.out.println("update outcome: " + ovdao.update(linked));
        }
        System.out.println();
        System.out.println("now retrieving reiziger from db");
        Reiziger r2 = rdao.findById(100);
        System.out.println("retrieved reiziger ovlist:\n" + r2.getOvList());
        System.out.println();
        System.out.println("try to remove ov#2 from reiziger: " + r2.tryDeleteOVChipkaart(2));
        System.out.println("updating db: " + rdao.update(r2));
        System.out.println("OV.findbyreiziger again: ");
        System.out.println(ovdao.findByReiziger(r2));
        System.out.println();
        System.out.println("Final test retrieving reiziger again");
        Reiziger r3 = rdao.findById(100);
        System.out.println(r3);
        System.out.println(r3.getOvList());
        System.out.println("deleting reiziger: " + rdao.delete(r3));
    }

    private static void testProductDAO(ReizigerDAOPsql rdao, OVChipkaartDAOPsql ovdao, ProductDAOPsql pdao) {
        Reiziger r1 = new Reiziger(100, "voor1", null,"achter1", Date.valueOf("2000-01-01"));
        OVChipkaart ov1 = new OVChipkaart(1, Date.valueOf("2022-09-16"), 2, 5.00);
        OVChipkaart ov2 = new OVChipkaart(2, Date.valueOf("2022-11-25"), 1, 254.00);
        Product p1 = new Product(50, "product1", "beschrijving1", 20.00);
        Product p2 = new Product(51, "product2", "beschrijving2", 25.00);
        ov1.tryAddProduct(p1);
        ov2.tryAddProduct(p2);
        r1.tryAddOVChipkaart(ov1);
        r1.tryAddOVChipkaart(ov2);

        System.out.println("We have 2 ov's and 2 producten:");
        System.out.println(p1);
        System.out.println(p2);
        System.out.println(ov1);
        System.out.println(ov2);

        System.out.println();
        System.out.println("We will save reiziger, which should save everything else: " + rdao.save(r1));
        System.out.println("findbyOV voor ov1 geeft:");
        List<Product> productList = pdao.findByOVChipkaart(ov1);
        System.out.println(productList);

        System.out.println();
        productList.get(0).setNaam("Updated product");
        System.out.println("Try to update naam van p1: " + pdao.update(productList.get(0)));

        System.out.println("Opnieuw ophalen van p1 door findbyOV:");
        System.out.println(pdao.findByOVChipkaart(ov1));

        System.out.println();
        System.out.println("Lastly deleting reiziger should cascade delete all associated ov");
        System.out.println("handmatig verwijderen van producten p1 en p2 erna");
        System.out.println(pdao.findAll());

        System.out.println(rdao.delete(r1));

        System.out.println(pdao.findAll());
        System.out.println(pdao.delete(p1));
        System.out.println(pdao.delete(p2));

        System.out.println();
        System.out.println("UNFIXED BUG:");
        System.out.println("Om de een of andere reden, als ik een reiziger verwijder, verwijdert hij ook");
        System.out.println("de gelinkte producten van de gelinkte ov's. ");
        System.out.println("Na debuggen en analyse vind ik in rdao.delete() echter geen (verborgen) call naar pdao.delete()");
        System.out.println("En volgens Intellij usages zijn de enige calls naar pdao.delete() de twee laatste testlines hierboven");
        System.out.println("Ik heb geen idee wat hier gebeurt.");
    }
}

