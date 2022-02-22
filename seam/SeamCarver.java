import edu.princeton.cs.algs4.Picture;

import java.util.Arrays;
import java.util.Comparator;

public class SeamCarver {
    private Picture picture;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();
        this.picture = new Picture(picture);
    }

    // current picture
    public Picture picture() {
        return new Picture(this.picture);
    }

    // width of current picture
    public int width() {
        return this.picture.width();
    }

    // height of current picture
    public int height() {
        return this.picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {

        int w = this.width();
        int h = this.height();
        if ((x < 0) || (x > w - 1)) throw new IllegalArgumentException();
        if ((y < 0) || (y > h - 1)) throw new IllegalArgumentException();

        if ((x == 0) || (x == w - 1)) return 1000.0;
        if ((y == 0) || (y == h - 1)) return 1000.0;

        int deltaX = deltaCal(this.picture.getRGB(x - 1, y), this.picture.getRGB(x + 1, y));
        int deltaY = deltaCal(this.picture.getRGB(x, y - 1), this.picture.getRGB(x, y + 1));
        return Math.sqrt(deltaX + deltaY);

    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        AcyclicSP acyclicSP = new AcyclicSP(Boolean.FALSE);
        return acyclicSP.minPath();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        AcyclicSP acyclicSP = new AcyclicSP(Boolean.TRUE);
        return acyclicSP.minPath();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (this.height() <= 1) throw new IllegalArgumentException();
        if (!(seam.length == this.width())) throw new IllegalArgumentException();
        int prev = seam[0];
        for (int x = 0; x < this.width(); x++) {
            if ((seam[x] < 0) || (seam[x] > this.height() - 1))
                throw new IllegalArgumentException();
            if (x > 0) {
                if ((seam[x] > (prev + 1)) || (seam[x] < (prev - 1)))
                    throw new IllegalArgumentException();
                prev = seam[x];
            }

        }
        Picture newPic = new Picture(this.width(), this.height() - 1);
        for (int x = 0; x < this.width(); x++) {

            int row = 0;
            for (int y = 0; y < this.height(); y++) {

                if (!(y == seam[x])) {
                    newPic.setRGB(x, row, this.picture.getRGB(x, y));
                    row++;
                }

            }
        }
        this.picture = newPic;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (this.width() <= 1) throw new IllegalArgumentException();
        if (!(seam.length == this.height())) throw new IllegalArgumentException();
        int prev = seam[0];
        for (int y = 0; y < this.height(); y++) {
            if ((seam[y] < 0) || (seam[y] > this.width() - 1)) throw new IllegalArgumentException();
            if (y > 0) {
                if ((seam[y] > (prev + 1)) || (seam[y] < (prev - 1)))
                    throw new IllegalArgumentException();
                prev = seam[y];
            }

        }
        Picture newPic = new Picture(this.width() - 1, this.height());

        for (int y = 0; y < this.height(); y++) {


            int col = 0;
            for (int x = 0; x < this.width(); x++) {
                if (!(x == seam[y])) {
                    newPic.setRGB(col, y, this.picture.getRGB(x, y));
                    col++;
                }

            }
        }
        this.picture = newPic;
    }

    private int deltaCal(int rgbMinus, int rgbPlus) {

        int rMinus = (rgbMinus >> 16) & 0xFF;
        int gMinus = (rgbMinus >> 8) & 0xFF;
        int bMinus = (rgbMinus >> 0) & 0xFF;

        int rPlus = (rgbPlus >> 16) & 0xFF;
        int gPlus = (rgbPlus >> 8) & 0xFF;
        int bPlus = (rgbPlus >> 0) & 0xFF;
        return (rPlus - rMinus) * (rPlus - rMinus) + (gPlus - gMinus) * (gPlus - gMinus)
                + (bPlus - bMinus) * (bPlus - bMinus);

    }


    private class AcyclicSP {
        private final boolean vertical;
        private int[][] root;
        private double[][] distTo;
        private int w = SeamCarver.this.width();
        private int h = SeamCarver.this.height();
        private double[][] energies;

        private AcyclicSP(Boolean vertical) {
            this.vertical = vertical;
            root = new int[w][h];
            distTo = new double[w][h];
            energies = new double[w][h];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    this.distTo[x][y] = Double.POSITIVE_INFINITY;
                    energies[x][y] = SeamCarver.this.energy(x, y);
                }
            }
            if (vertical) {

                for (int x = 0; x < w; x++) {
                    this.distTo[x][0] = energies[x][0];
                    this.root[x][0] = -1;
                }

                for (int y = 1; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        this.relax(x, y, true);
                    }

                }
            }
            else {

                for (int y = 0; y < h; y++) {
                    this.distTo[0][y] = energies[0][y];
                    this.root[0][y] = -1;
                }

                for (int x = 1; x < w; x++) {
                    for (int y = 0; y < h; y++) {
                        this.relax(x, y, false);
                    }

                }
            }

        }

        private void relax(int x, int y, boolean vert) {
            if (vert) {
                if (this.validatePixel(x - 1, y - 1)) {
                    if (this.distTo[x][y] > this.distTo[x - 1][y - 1] + energies[x][y]) {
                        this.distTo[x][y] = this.distTo[x - 1][y - 1] + energies[x][y];
                        this.root[x][y] = x - 1;
                    }
                }
                if (this.validatePixel(x + 1, y - 1)) {
                    if (this.distTo[x][y] > this.distTo[x + 1][y - 1] + energies[x][y]) {
                        this.distTo[x][y] = this.distTo[x + 1][y - 1] + energies[x][y];
                        this.root[x][y] = x + 1;
                    }
                }
                if (this.validatePixel(x, y - 1)) {
                    if (this.distTo[x][y] > this.distTo[x][y - 1] + energies[x][y]) {
                        this.distTo[x][y] = this.distTo[x][y - 1] + energies[x][y];
                        this.root[x][y] = x;
                    }
                }
            }

            else {
                if (this.validatePixel(x - 1, y - 1)) {
                    if (this.distTo[x][y] > this.distTo[x - 1][y - 1] + energies[x][y]) {
                        this.distTo[x][y] = this.distTo[x - 1][y - 1] + energies[x][y];
                        this.root[x][y] = y - 1;
                    }
                }
                if (this.validatePixel(x - 1, y + 1)) {
                    if (this.distTo[x][y] > this.distTo[x - 1][y + 1] + energies[x][y]) {
                        this.distTo[x][y] = this.distTo[x - 1][y + 1] + energies[x][y];
                        this.root[x][y] = y + 1;
                    }
                }
                if (this.validatePixel(x - 1, y)) {
                    if (this.distTo[x][y] > this.distTo[x - 1][y] + energies[x][y]) {
                        this.distTo[x][y] = this.distTo[x - 1][y] + energies[x][y];
                        this.root[x][y] = y;
                    }
                }
            }

        }

        private boolean validatePixel(int x, int y) {
            if ((0 <= x) && (x < w) && (0 <= y) && (y < h))
                return true;
            return false;
        }

        private int[] minPath() {
            if (this.vertical) {
                double[][] pathEnergy = new double[w][2];
                for (int x = 0; x < w; x++) {
                    pathEnergy[x][0] = x;
                    pathEnergy[x][1] = this.distTo[x][h - 1];
                }
                Arrays.sort(pathEnergy, Comparator.comparingDouble(o -> o[1]));
                int end = (int) (pathEnergy[0][0]);
                int[] path = new int[h];
                int x = end;
                int y = h - 1;
                while (!(x == -1)) {
                    path[y] = x;
                    x = this.root[x][y];
                    y--;
                }
                return path;
            }
            else {
                double[][] pathEnergy = new double[h][2];
                for (int y = 0; y < h; y++) {
                    pathEnergy[y][0] = y;
                    pathEnergy[y][1] = this.distTo[w - 1][y];
                }
                Arrays.sort(pathEnergy, Comparator.comparingDouble(o -> o[1]));
                int end = (int) (pathEnergy[0][0]);
                int[] path = new int[w];
                int y = end;
                int x = w - 1;
                while (!(y == -1)) {
                    path[x] = y;
                    y = this.root[x][y];
                    x--;
                }
                return path;
            }


        }
    }


    //  unit testing (optional)
    public static void main(String[] args) {

        Picture picture = new Picture(args[0]);
        SeamCarver seamCarver = new SeamCarver(picture);
        int[] seam = seamCarver.findHorizontalSeam();
        System.out.println(Arrays.toString(seam));
        seamCarver.removeHorizontalSeam(seam);

        System.out.println(Arrays.toString(seamCarver.findHorizontalSeam()));
    }


}


