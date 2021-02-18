package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Attack;

public class JsonTranslator {
    private Attack[] attacks;
    private long R2D2;
    private long Lando;
    private int Ewoks;

    public Attack[] getAttacks() {
        return attacks;
    }

    public void setAttacks(Attack[] attacks) {
        this.attacks = attacks;
    }

    public long getR2D2() {
        return R2D2;
    }

    public void setR2D2(long r2D2) {
        R2D2 = r2D2;
    }

    public long getLando() {
        return Lando;
    }

    public void setLando(long lando) {
        Lando = lando;
    }

    public int getEwoks() {
        return Ewoks;
    }

    public void setEwoks(int ewoks) {
        Ewoks = ewoks;
    }
}
