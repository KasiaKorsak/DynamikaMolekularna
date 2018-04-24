package md;

//istota animacji: ma robic 1 krok verleta; jesli uplynal okreslony czas to wyswietl, jak nie to nast krok

import java.util.Random;

public class MD {

    //masy sa jednostkowe (=1)

    public int nAtoms;
    private int stepCounter; //liczy ilosc krokow Verleta
    private double boxWidth;
    private final double rCut2 = 16.0; //jesli czastki beda w wiekszej odl niz 4 to nie liczymy dla nich sily

    private double ePot;
    private double eKin;
    private final double wallStifness = 50;
    private double elasticEnergy;

    private double x[],y[],vx[],vy[],ax[],ay[];


    public MD(int quantity, double boxWidth) { //poczatkowe polozenia i predkosci np 2 czastek

        nAtoms = quantity;
        this.boxWidth = boxWidth;

        x = new double[nAtoms];
        y = new double[nAtoms];
        vx = new double[nAtoms];
        vy = new double[nAtoms];
        initialValues();

        ax = new double[nAtoms];
        ay = new double[nAtoms];

        calculateAcceleration();
        calculateKineticEnergy();

        stepCounter = 0;

    }

    private void calculateAcceleration() { //w metodzie Verleta trzeba raz, na poczatku, obliczyc przyspieszenie

        //kasowanie starych wartosci przyspieszen
        for (int i = 0; i < nAtoms; i++) {
            ax[i] = 0;
            ay[i] = 0;
        }
        ePot = 0;
        for (int i = 0; i < nAtoms; i++)  //3 zasada dynamiki Newtona
            for (int j = i + 1; j < nAtoms; j++) {
                double dx = x[i] - x[j];
                double dy = y[i] - y[j];

                double rij2 = dx * dx + dy * dy; //odleglosc miedzy x i y do kwadratu
                if (rij2 < rCut2) {
                    double fr2 = 1. / rij2;
                    double fr6 = fr2 * fr2 * fr2;
                    double fr = 48. * fr6 * (fr6 - 0.5) / rij2; //sprawdzic czy to poprawne
                    double fxi = fr * dx;
                    double fyi = fr * dy;
                    ax[i] += fxi;
                    ay[i] += fyi;
                    ax[j] -= fxi;
                    ay[j] -= fyi;
                    ePot += 4 * fr6 * (fr6 - 1.0);
                }
            }

        elasticEnergy = 0;
        for (int i = 0; i < nAtoms; i++) {
            double d = 0;
            if (x[i] < 0.5) {
                d = 0.5 - x[i];
                ax[i] += wallStifness * d;
                elasticEnergy += 0.5 * wallStifness * d * d;
            }
            if (x[i] > (boxWidth - 0.5)) {
                d = (boxWidth - 0.5 - x[i]);
                ax[i] += wallStifness * d;
                elasticEnergy += 0.5 * wallStifness * d * d;
            }
        }

        for (int i = 0; i < nAtoms; i++) {
            double d = 0;
            if (y[i] < 0.5) {
                d = 0.5 - y[i];
                ay[i] += wallStifness * d;
                elasticEnergy += 0.5 * wallStifness * d * d;
            }
            if (y[i] > (boxWidth - 0.5)) {
                d = (boxWidth - 0.5 - y[i]);
                ay[i] += wallStifness * d;
                elasticEnergy += 0.5 * wallStifness * d * d;
            }
        }

    }

    public void Verlet(double h) {

        for (int i = 0; i < nAtoms; i++) {

            vx[i] = vx[i] + h * ax[i] * 0.5;
            vy[i] = vy[i] + h * ay[i] * 0.5;
            if (h * vx[i] < boxWidth && h * vy[i] < boxWidth) { // czy ma byc ten if ?
                x[i] += h * vx[i];
                y[i] += h * vy[i];
            } else
                continue;
        }

        calculateAcceleration();
        for (int i = 0; i < nAtoms; i++) {

            vx[i] = vx[i] + h * 0.5 * ax[i];
            vy[i] = vy[i] + h * 0.5 * ay[i];
        }

        stepCounter++;
        calculateKineticEnergy();

    }

    private void calculateKineticEnergy() {

        eKin = 0;
        for (int i = 0; i < nAtoms; i++)
            eKin += vx[i] * vx[i] + vy[i] * vy[i];
        eKin *= 0.5;
    }

    private void initialValues() {

        Random random = new Random();

        for (int i = 0; i < nAtoms; i++) {
            x[i] = random.nextDouble() * boxWidth;
            y[i] = random.nextDouble() * boxWidth;

            if (i > 0) {
                for (int j = 0; j < i; j++) {
                    double dx = x[i] - x[j];
                    double dy = y[i] - y[j];
                    double rij = Math.sqrt(dx * dx + dy * dy);
                    if (rij < 2*Animacja.getR()) {
                        i--;
                        break;
                    }
                }
            }
            vx[i] = random.nextGaussian() * 3;
            vy[i] = random.nextGaussian() * 3;

        }
    }

    public double getTotalEnergy() {
        return eKin + ePot + elasticEnergy;
    }

    public double[] getX() {
        return x;
    }

    public double[] getY() {
        return y;
    }

    public int getStepCounter() {
        return stepCounter;
    }

    public double getePot() {
        return ePot;
    }

    public double geteKin() {
        return eKin;
    }

    public double getElasticEnergy() {
        return elasticEnergy;
    }
}
