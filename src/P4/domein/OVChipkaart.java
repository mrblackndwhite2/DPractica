package P4.domein;

import java.sql.*;
import java.util.Objects;

public class OVChipkaart {
    private int kaartnummer;
    private Date vervaldatum;
    private int klasse;
    private double saldo;
    private Reiziger reiziger;

    public OVChipkaart(int kaartnummer, Date vervaldatum, int klasse, double saldo, Reiziger reiziger) {
        this.kaartnummer = kaartnummer;
        this.vervaldatum = vervaldatum;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger = reiziger;
    }
    public OVChipkaart(int kaartnummer, Date vervaldatum, int klasse, double saldo) {
        this.kaartnummer = kaartnummer;
        this.vervaldatum = vervaldatum;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger = null;
    }

    public OVChipkaart(int kaartnummer, Date vervaldatum, int klasse, Reiziger reiziger) {
        this.kaartnummer = kaartnummer;
        this.vervaldatum = vervaldatum;
        this.klasse = klasse;
        this.saldo = 0;
        this.reiziger = reiziger;
    }

    public OVChipkaart(int kaartnummer, Date vervaldatum, int klasse) {
        this.kaartnummer = kaartnummer;
        this.vervaldatum = vervaldatum;
        this.klasse = klasse;
        this.saldo = 0;
        this.reiziger = null;
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
