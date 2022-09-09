package P2;

import java.sql.Date;

public class Reiziger {
    private int id;
    private String voorletters;
    private String tussenvoegsel;
    private String achternaam;
    private Date geboortedatum;

    // gbdt format: yyyy-mm-dd
    public Reiziger(int id, String voor, String tussen, String achter, Date gbdt) {
        this.id = id;
        this.voorletters = voor;
        this.tussenvoegsel = tussen;
        this.achternaam = achter;
        this.geboortedatum = gbdt;
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
        return String.format(s, getNaam(), geboortedatum.toString());
    }
}
