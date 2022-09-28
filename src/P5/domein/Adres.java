package P5.domein;

import java.util.Objects;

public class Adres {
    private int id;
    private String postcode;
    private String huisnummer;
    private String straat;
    private String woonplaats;
    private int reiziger_id;

    public Adres(int id, String postcode, String straat, String huisnummer, String woonplaats) {
        this.id = id;
        this.postcode = postcode;
        this.huisnummer = huisnummer;
        this.straat = straat;
        this.woonplaats = woonplaats;
    }

    public Adres(int id, String postcode, String straat, String huisnummer, String woonplaats, int rid) {
        this(id, postcode, straat, huisnummer, woonplaats);
        this.reiziger_id=rid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getHuisnummer() {
        return huisnummer;
    }

    public void setHuisnummer(String huisnummer) {
        this.huisnummer = huisnummer;
    }

    public String getStraat() {
        return straat;
    }

    public void setStraat(String straat) {
        this.straat = straat;
    }

    public String getWoonplaats() {
        return woonplaats;
    }

    public void setWoonplaats(String woonplaats) {
        this.woonplaats = woonplaats;
    }

    public int getReiziger_id() {
        return reiziger_id;
    }

    public void setReiziger_id(int reiziger_id) {
        this.reiziger_id = reiziger_id;
    }

    @Override
    public String toString() {
        return String.format("(#%d) %s %s, %S %S van reiziger %d",
                id, straat, huisnummer, postcode, woonplaats, reiziger_id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Adres adres = (Adres) o;
        return id == adres.id &&
                postcode.equals(adres.postcode) &&
                huisnummer.equals(adres.huisnummer) &&
                straat.equals(adres.straat) &&
                woonplaats.equals(adres.woonplaats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, postcode, huisnummer, straat, woonplaats);
    }
}
