package P4.domein;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Reiziger {
    private int id;
    private String voorletters;
    private String tussenvoegsel;
    private String achternaam;
    private Date geboortedatum;
    private Adres adres;
    private List<OVChipkaart> ovList;

    // gbdt format: yyyy-mm-dd
    public Reiziger(int id, String voor, String tussen, String achter, Date gbdt) {
        this.id = id;
        this.voorletters = voor;
        this.tussenvoegsel = tussen;
        this.achternaam = achter;
        this.geboortedatum = gbdt;
        this.ovList = new ArrayList<OVChipkaart>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVoorletters() {
        return voorletters;
    }

    public String getTussenvoegsel() {
        return tussenvoegsel;
    }

    public void setTussenvoegsel(String tussenvoegsel) {
        this.tussenvoegsel = tussenvoegsel;
    }

    public String getAchternaam() {
        return achternaam;
    }

    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }

    public Date getGeboortedatum() {
        return geboortedatum;
    }

    public void setGeboortedatum(Date geboortedatum) {
        this.geboortedatum = geboortedatum;
    }

    public void setVoorletters(String voorletters) {
        this.voorletters = voorletters;
    }

    public Adres getAdres() {
        return adres;
    }

    public void setAdres(Adres adres) {
        this.adres = adres;
    }

    public List<OVChipkaart> getOvList() {
        return ovList;
    }

    public void setOvList(List<OVChipkaart> ovList) {
        this.ovList = ovList;
    }

    public boolean hasOV() {
        return (ovList != null) && !(ovList.isEmpty());
    }

    public boolean tryAddOVChipkaart(OVChipkaart ov) {
        // check if already in the list
        if (!(ovList.isEmpty())) {
            for (int i = 0; i < ovList.size(); i++) {
                if (ovList.get(i).getKaartnummer() == ov.getKaartnummer()) {
                    System.out.println("OV already linked to this reiziger");
                    return false;
                }
            }
        }

        //check if ov linked to someone else
        if (ov.getReiziger() != null) {
            if (ov.getReiziger().getId() != this.id) {
                System.out.println("OV is already linked to another reiziger");
                return false;
            }
        }

        ov.setReiziger(this);
        this.ovList.add(ov);
        return true;
    }

    public void clearOVList() {
        this.ovList.clear();
    }

    public boolean tryDeleteOVChipkaart(int kaartnummer) {
        if (!(ovList.isEmpty())) {
            for (int i = 0; i < ovList.size(); i++) {
                if (ovList.get(i).getKaartnummer() == kaartnummer) {
                    ovList.remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    public String getNaam() {
        String naam = "%S. %s%s";
        if (tussenvoegsel != null && !tussenvoegsel.isBlank()) {
            return String.format(naam,
                    voorletters.trim().toUpperCase(),
                    tussenvoegsel.trim(),
                    " " + achternaam.trim());
        } else {
            return String.format(naam,
                    voorletters.trim().toUpperCase(),
                    "",
                    achternaam.trim());
        }
    }

    @Override
    public String toString() {
        String s = "%s (%s)";
        if (adres != null) {
            s = "%s (%s) met adres %s";
            return String.format(s, getNaam(), geboortedatum.toString(),
                    adres.toString().substring(0, adres.toString().length() - 13 - String.valueOf(id).length()));
        }
        return String.format(s, getNaam(), geboortedatum.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reiziger reiziger = (Reiziger) o;
        return id == reiziger.id &&
                voorletters.equals(reiziger.voorletters) &&
                Objects.equals(tussenvoegsel, reiziger.tussenvoegsel) &&
                achternaam.equals(reiziger.achternaam) &&
                geboortedatum.equals(reiziger.geboortedatum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, voorletters, tussenvoegsel, achternaam, geboortedatum);
    }
}
