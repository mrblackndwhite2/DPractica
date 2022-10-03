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
        onChange();
    }

    public Date getVervaldatum() {
        return vervaldatum;
    }

    public void setVervaldatum(Date vervaldatum) {
        this.vervaldatum = vervaldatum;
        onChange();
    }

    public int getKlasse() {
        return klasse;
    }

    public void setKlasse(int klasse) {
        this.klasse = klasse;
        onChange();
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
        onChange();
    }

    public Reiziger getReiziger() {
        return reiziger;
    }

    public void setReiziger(Reiziger reiziger) {
        this.reiziger = reiziger;
        onChange();
    }

    public List<Product> getProducten() {
        return producten;
    }

    public void setProducten(List<Product> producten) {
        this.producten = producten;
    }

    // overwrites old versions of OV in every linked product
    void productUpdate(Product p) {
        for (int i = 0; i < producten.size(); i++) {
            if (producten.get(i).getProductnummer() == p.getProductnummer()) {
                producten.set(i, p);
                return;
            }
        }
    }

    private void onChange() {
        for (Product p : producten) {
            p.ovUpdate(this);
        }
    }

    public boolean tryAddProduct(Product p) {
        if (p == null) {
            return false;
        }

        if (producten.isEmpty()) {
            producten.add(p);
//            System.out.println("product succesvol gekoppeld aan ov");
            p.tryAddOv(this);
            onChange();
            return true;
        }

        for (int i = 0; i < producten.size(); i++) {
            // case: product al toegevoegd maar aangepast
            if (producten.get(i).getProductnummer() == p.getProductnummer()) {
                if (producten.get(i).equals(p)) {
//                    System.out.println("Product al gekoppeld aan ov");
                    return false;
                } else {
                    producten.remove(i);
                    producten.add(p);
//                    System.out.println("product succesvol gekoppeld aan ov");
                    p.tryAddOv(this);
                    onChange();
                    return true;
                }
            }
        }

        // if we get here, that means no product with same id is added, so safe to add
        producten.add(p);
//        System.out.println("product succesvol gekoppeld aan ov");
        p.tryAddOv(this);
        onChange();
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
                producten.remove(i);
                p.tryDeleteOv(this);
                onChange();
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        String result = "OV#%d vervalt op %s, %de klasse, €%.2f saldo van reiziger#%d\n" +
                "producten: [";
        for (Product p : producten) {
            result+=p.getNaam();
            result+=", ";
        }
        result+="]";
        return String.format(result, kaartnummer, vervaldatum.toString(), klasse, saldo, reiziger.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OVChipkaart that = (OVChipkaart) o;
        return kaartnummer == that.kaartnummer &&
                klasse == that.klasse &&
                vervaldatum.equals(that.vervaldatum) &&
                Objects.equals(reiziger, that.reiziger);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kaartnummer, vervaldatum, klasse, reiziger);
    }
}
