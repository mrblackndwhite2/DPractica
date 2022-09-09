package P2;

import java.sql.*;
import java.util.List;

public class Main {
    private static Connection connection;

    public static void main(String[] args) {
        try {
            getConnection();
            ReizigerDAOPsql rd = new ReizigerDAOPsql(connection);
            testReizigerDAO(rd);
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
        oldr.setAchternaam("Bakker");
        oldr.setTussenvoegsel("de");
        oldr.setGeboortedatum(Date.valueOf("1998-08-11"));
        rdao.update(oldr);
        List<Reiziger> rlist = rdao.findByGbdatum("1998-08-11");
        System.out.println("[Test} na update o.a. geboortedatum, findByGbdatum: ");
        for (Reiziger r : rlist) {
            System.out.println(r);
        }

        // Delete
        System.out.println("[Test] voor delete, reizigers.size = " + rdao.findAll().size());
        rdao.delete(rdao.findById(77));
        System.out.println("[Test] na delete, reizigers.size = " + rdao.findAll().size());
    }
}
