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
        onChange();
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
        onChange();
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
        onChange();
    }

    public double getPrijs() {
        return prijs;
    }

    public void setPrijs(double prijs) {
        this.prijs = prijs;
        onChange();
    }

    public List<OVChipkaart> getKoppelingen() {
        return koppelingen;
    }

    void ovUpdate(OVChipkaart ov) {
        for (int i = 0; i < koppelingen.size(); i++) {
            if (koppelingen.get(i).getKaartnummer() == ov.getKaartnummer()) {
                koppelingen.set(i, ov);
                return;
            }
        }
    }

    private void onChange() {
        for (OVChipkaart ov : koppelingen) {
            ov.productUpdate(this);
        }
    }

    // tries to add ov. To update ov, do tryDelete first, then tryAdd it back
    public boolean tryAddOv(OVChipkaart ov) {
        if (ov == null) {
            return false;
        }

        if (koppelingen.contains(ov)) {
//            System.out.println("OV-chipkaart al gekoppeld");
            return false;
        }

        for (int i = 0; i < koppelingen.size(); i++) {
            // case: product al toegevoegd maar aangepast
            if (koppelingen.get(i).getKaartnummer() == ov.getKaartnummer()) {
                koppelingen.remove(i);
                koppelingen.add(ov);
//                System.out.println("ov succesvol gekoppeld aan product");
                ov.tryAddProduct(this);
                onChange();
                return true;
            }
        }

        koppelingen.add(ov);
//        System.out.println("ov succesvol gekoppeld aan product");
        ov.tryAddProduct(this);
        onChange();
        return true;
    }

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
            ov.tryDeleteProduct(this);
            onChange();
            return true;
        }

        for (int i = 0; i < koppelingen.size(); i++) {
            if (koppelingen.get(i).getKaartnummer() == ov.getKaartnummer()) {
                koppelingen.remove(i);
                ov.tryDeleteProduct(this);
                onChange();
                return true;
            }
        }

        return false;
    }


    @Override
    public String toString() {
        String result = "Product #%d %s : %s (%.2f)\nlinkedOV: [";
        for (OVChipkaart ov : koppelingen) {
            result += ov.getKaartnummer();
            result += ", ";
        }
        result += "]";
        return String.format(result, productnummer, naam, beschrijving, prijs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productnummer == product.productnummer &&
                Double.compare(product.prijs, prijs) == 0 &&
                naam.equals(product.naam) &&
                beschrijving.equals(product.beschrijving);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productnummer, naam, beschrijving, prijs);
    }
}
