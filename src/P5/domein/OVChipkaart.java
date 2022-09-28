package P5.domein;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OVChipkaart {
    private int kaartnummer;
    private Date vervaldatum;
    private int klasse;
    private double saldo;
    private Reiziger reiziger;
    private List<Product> producten;

    public OVChipkaart(int kaartnummer, Date vervaldatum, int klasse, double saldo, Reiziger reiziger) {
        this.kaartnummer = kaartnummer;
        this.vervaldatum = vervaldatum;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger = reiziger;
        this.producten = new ArrayList<Product>();
    }
    public OVChipkaart(int kaartnummer, Date vervaldatum, int klasse, double saldo) {
        this(kaartnummer, vervaldatum, klasse, saldo, null);
    }

    public OVChipkaart(int kaartnummer, Date vervaldatum, int klasse, Reiziger reiziger) {
        this(kaartnummer, vervaldatum, klasse, 0, reiziger);
    }

    public OVChipkaart(int kaartnummer, Date vervaldatum, int klasse) {
        this(kaartnummer, vervaldatum, klasse, 0, null);
    }

    public int getKaartnummer() {
        return kaartnummer;
    }

    public void setKaartnummer(int kaartnummer) {
        this.kaartnummer = kaartnummer;
    }

    public Date getVervaldatum() {
        return vervaldatum;
    }

    public void setVervaldatum(Date vervaldatum) {
        this.vervaldatum = vervaldatum;
    }

    public int getKlasse() {
        return klasse;
    }

    public void setKlasse(int klasse) {
        this.klasse = klasse;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public Reiziger getReiziger() {
        return reiziger;
    }

    public void setReiziger(Reiziger reiziger) {
        this.reiziger = reiziger;
    }

    public boolean tryAddProduct(Product p) {
        if (p == null) {
            return false;
        }

        if (producten.isEmpty()) {
            producten.add(p);
            p.tryAddOv(this);
            return true;
        }

        for (int i = 0; i < producten.size(); i++) {
            // case: product al toegevoegd maar aangepast
            if (producten.get(i).getProductnummer() == p.getProductnummer()) {
                if (producten.get(i).equals(p)) {
                    System.out.println("Product al gekoppeld aan ov");
                    return false;
                } else {
                    producten.remove(i);
                    producten.add(p);
                    p.tryAddOv(this);
                    return true;
                }
            }
        }

        // if we get here, that means no product with same id is added, so safe to add
        producten.add(p);
        p.tryAddOv(this);
        return true;
    }

    public boolean tryDeleteProduct(Product p){
        if (p == null) {
            return false;
        }

        if (producten.isEmpty()){
            return false;
        }

        for (int i = 0; i < producten.size(); i++) {
            if (producten.get(i).getProductnummer() == p.getProductnummer()) {
                producten.get(i).tryDeleteOv(this);
                producten.remove(i);
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("OV#%d varvalt op %s. %de klasse en saldo van %.2f is van reiziger#%d",
                kaartnummer, vervaldatum.toString(), klasse, saldo, reiziger.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OVChipkaart that = (OVChipkaart) o;
        return kaartnummer == that.kaartnummer &&
                klasse == that.klasse &&
                Double.compare(that.saldo, saldo) == 0 &&
                vervaldatum.equals(that.vervaldatum) &&
                Objects.equals(reiziger, that.reiziger);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kaartnummer, vervaldatum, klasse, saldo, reiziger);
    }
}
