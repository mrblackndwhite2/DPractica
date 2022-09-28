package P5.domein;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Product {
    private int productnummer;
    private String naam;
    private String beschrijving;
    private double prijs;
    private List<OVChipkaart> koppelingen;

    public Product(int productnummer, String naam, String beschrijving, double prijs) {
        this.productnummer = productnummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
        this.koppelingen = new ArrayList<OVChipkaart>();
    }

    public int getProductnummer() {
        return productnummer;
    }

    public void setProductnummer(int productnummer) {
        this.productnummer = productnummer;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public double getPrijs() {
        return prijs;
    }

    public void setPrijs(double prijs) {
        this.prijs = prijs;
    }

    // DO NOT CALL YOURSELF, USE OVCHIPKAART ADD INSTEAD
    // tries to add ov. To update ov, do tryDelete first, then tryAdd it back
    public boolean tryAddOv(OVChipkaart ov) {
        if (ov == null) {
            return false;
        }

        if (koppelingen.contains(ov)) {
            System.out.println("OV-chipkaart al gekoppeld");
            return false;
        }

        koppelingen.add(ov);
        return true;
    }

    // DO NOT CALL YOURSELF, USE OVCHIPKAART DELETE INSTEAD
    // Deletes ov with same kaartnummer as arg
    public boolean tryDeleteOv(OVChipkaart ov) {
        if (koppelingen.isEmpty()) {
            return false;
        }

        if (ov == null) {
            return false;
        }

        if (koppelingen.contains(ov)) {
            koppelingen.remove(ov);
            return true;
        }

        for (int i = 0; i < koppelingen.size(); i++) {
            if (koppelingen.get(i).getKaartnummer() == ov.getKaartnummer()) {
                koppelingen.remove(i);
                return true;
            }
        }

        return false;
    }


    @Override
    public String toString() {
        return "Product{" +
                "productnummer=" + productnummer +
                ", naam='" + naam + '\'' +
                ", beschrijving='" + beschrijving + '\'' +
                ", prijs=" + prijs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(product.prijs, prijs) == 0 && naam.equals(product.naam) && Objects.equals(beschrijving, product.beschrijving);
    }

    @Override
    public int hashCode() {
        return Objects.hash(naam, beschrijving, prijs);
    }
}
