import java.io.*;


class CoefConter implements Runnable {
    CoefConter(int i, int j, double[][] m, double[] coefs) {
        this.i = i;
        this.j = j;
        this.m = m;
        this.coefs = coefs;
    }

    int i;
    int j;
    double[][] m;
    double[] coefs;

    public void run() {
        coefs[i] = m[i][j] / m[j][j];
    }
}


class OneableDividor implements Runnable {
    OneableDividor(int i, double[][] m, double[] r) {
        this.i = i;
        this.m = m;
        this.r = r;
    }

    int i;
    double[][] m;
    double[] r;

    public void run() {
        r[i] /= m[i][i];
        m[i][i] = 1;
    }
}

class RowMult implements Runnable {
    RowMult(int i, int j, double[][] m, double c, double r[]) {
        this.i = i;
        this.j = j;
        this.m = m;
        this.c = c;
        this.r = r;
    }

    int i;
    int j;
    double[][] m;
    double[] r;
    double c;

    public void run() {
        for (int g = 0; g < m.length; g++) {
            m[j][g] -= c * m[i][g];
        }
        r[j] -= c * r[i];
    }
}

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        File fil = new File("input.txt");
        FileReader inputFil = null;
        inputFil = new FileReader(fil);

        BufferedReader in = new BufferedReader(inputFil);

        String s = in.readLine();

        int size = Integer.parseInt(s);
        double[][] lhs = new double[size][size];
        double[] rhs = new double[size];

        for (int i = 0; i < size; i++) {
            s = in.readLine();
            String[] sp = s.split(" ");
            for (int j = 0; j < size; j++) {
                lhs[i][j] = Double.parseDouble(sp[j]);
            }
        }
        s = in.readLine();
        String[] sp = s.split(" ");
        for (int j = 0; j < size; j++) {
            rhs[j] = Double.parseDouble(sp[j]);
        }

        for (int j = 0; j < size; j++) {
            double[] coefs = new double[size];
            Thread[] coThrs = new Thread[size];
            for (int i = 0; i < size; i++) {
                if (i == j) {
                    continue;
                }
                coThrs[i] = new Thread(
                        new CoefConter(i, j, lhs, coefs)
                );
            }
            for (int i = 0; i < size; i++) {
                if (i == j) {
                    continue;
                }
                coThrs[i].start();
            }
            for (int i = 0; i < size; i++) {
                if (i == j) {
                    continue;
                }
                coThrs[i].join();
            }

            Thread[] muThrs = new Thread[size];
            for (int i = 0; i < size; i++) {
                if (i == j) {
                    continue;
                }
                muThrs[i] = new Thread(
                        new RowMult(j, i, lhs, coefs[i], rhs)
                );
            }
            for (int i = 0; i < size; i++) {
                if (i == j) {
                    continue;
                }
                muThrs[i].start();
            }
            for (int i = 0; i < size; i++) {
                if (i == j) {
                    continue;
                }
                muThrs[i].join();
            }
        }

        Thread[] divT = new Thread[size];
        for (int j = 0; j < size; j++) {
            divT[j] = new Thread(
                    new OneableDividor(j, lhs, rhs)
            );
        }
        for (int j = 0; j < size; j++) {
            divT[j].start();
        }
        for (int j = 0; j < size; j++) {
            divT[j].join();
        }
        System.out.println(size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(lhs[i][j] + " ");
            }
            System.out.println();
        }
        for (int j = 0; j < size; j++) {
            System.out.print(rhs[j] + " ");
        }
    }
}
