package P3;

import java.sql.*;

import P3.domein.*;
import P3.patterns.*;

import java.util.List;

public class Main {
    private static Connection connection;

    public static void main(String[] args) {
        try {
            getConnection();
            ReizigerDAOPsql rd = new ReizigerDAOPsql(connection);
            AdresDAOPsql adao = rd.getAdao();
            testReizigerDAO(rd);
            testAdresDAO(adao, rd);
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
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", java.sql.Date.valueOf(gbdatum));
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
}
