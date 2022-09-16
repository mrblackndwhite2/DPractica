package P4.patterns;

import P4.domein.Reiziger;

import java.util.List;

public interface ReizigerDAO {
    boolean save(Reiziger reiziger);

    boolean update(Reiziger reiziger);

    boolean delete(Reiziger reiziger);

    Reiziger findById(int id);

    List<Reiziger> findByGbdatum(String datum);

    List<Reiziger> findAll();
}
