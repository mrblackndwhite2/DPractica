package P5.patterns;

import P5.domein.OVChipkaart;
import P5.domein.Reiziger;

import java.util.List;

public interface OVChipkaartDAO {
    boolean save(OVChipkaart ovchipkaart);

    boolean update(OVChipkaart ovchipkaart);

    boolean delete(OVChipkaart ovchipkaart);

    OVChipkaart findByKaartnummer(int kaartnummer);

    List<OVChipkaart> findByReiziger(Reiziger reiziger);

    List<OVChipkaart> findAll();
}
