import java.io.*;


class CoefTask implements Runnable {
    CoefTask(int i, int j, double[][] m, double[] coefs) {
        this.i = i;
        this.j = j;
        this.m = m;
        this.coefs = coefs;
    }

    private int i;
    private int j;
    private double[][] m;
    private double[] coefs;

    public void run() {
        coefs[i] = m[i][j] / m[j][j];
    }
}


class PostMultiplicationTask implements Runnable {
    PostMultiplicationTask(int i, double[][] m, double[] r) {
        this.i = i;
        this.m = m;
        this.r = r;
    }

    private int i;
    private double[][] m;
    private double[] r;

    public void run() {
        r[i] /= m[i][i];
        m[i][i] = 1;
    }
}

class DivideLeftTask implements Runnable {
    DivideLeftTask(int i, int j, int k, double[][] m, double c) {
        this.i = i;
        this.j = j;
        this.m = m;
        this.k = k;
        this.c = c;
    }

    private int i;
    private int j;
    private int k;
    private double[][] m;
    private double c;

    public void run() {
        m[j][k] -= c * m[i][k];
    }
}

class DivideRightTask implements Runnable {
    DivideRightTask(int i, int j, double c, double[] r) {
        this.i = i;
        this.j = j;
        this.c = c;
        this.r = r;
    }

    private int i;
    private int j;
    private double[] r;
    private double c;

    public void run() {
        r[j] -= c * r[i];
    }
}

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        File fil = new File("input.txt");
        FileReader inputFil;
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
                        new CoefTask(i, j, lhs, coefs)
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

            Thread[][] muThrs = new Thread[size][size + 1];
            for (int i = 0; i < size; i++) {
                if (i == j) {
                    continue;
                }
                for (int k = 0 ; k < size; k++) {
                    muThrs[i][k] = new Thread(
                            new DivideLeftTask(j, i, k, lhs, coefs[i])
                    );
                }
                muThrs[i][size] = new Thread(
                        new DivideRightTask(j, i, coefs[i], rhs)
                );
            }

            for (int i = 0; i < size; i++) {
                if (i == j) {
                    continue;
                }
                for (int k = 0 ; k < size + 1; k++) {
                    muThrs[i][k].start();
                }
            }
            for (int i = 0; i < size; i++) {
                if (i == j) {
                    continue;
                }
                for (int k = 0 ; k < size + 1; k++) {
                    muThrs[i][k].join();
                }
            }
        }

        Thread[] divT = new Thread[size];
        for (int j = 0; j < size; j++) {
            divT[j] = new Thread(
                    new PostMultiplicationTask(j, lhs, rhs)
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
