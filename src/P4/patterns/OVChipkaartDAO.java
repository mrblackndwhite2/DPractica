package P4.patterns;

import P4.domein.OVChipkaart;
import P4.domein.Reiziger;

import java.util.List;

public interface OVChipkaartDAO {
    boolean save(OVChipkaart ovchipkaart);

    boolean update(OVChipkaart ovchipkaart);

    boolean delete(OVChipkaart ovchipkaart);

    OVChipkaart findByKaartnummer(int kaartnummer);

    List<OVChipkaart> findByReiziger(Reiziger reiziger);

    List<OVChipkaart> findAll();
}
