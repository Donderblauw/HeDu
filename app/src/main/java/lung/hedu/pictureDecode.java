package lung.hedu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luuk on 11-8-2015.
 */
public class pictureDecode
{
    private Bitmap picture2;
    private int[][] arrayPic = null;
    private int[][] arrayField = null;
    private int[][] arrayFieldGray = null;
    private int[] startingPoint = new int[2];

    /*
    line:
    0 - left / links
    1 - right / rechts
    2 - top / boven
    3 - bottom / onder
    */
    private double[][] Lines = new double[4][4];
    private boolean[] lockLines = new boolean[4];
    private int[][] FieldCorners;

    private int[][] dotsRight;
    private int[][] dotsLeft;

    private int[][] dotsTop;
    private int[][] dotsBottom;

    private int bufLength;
    private double perctNeeded;
    private int numNeeded;

    public pictureDecode(File picFile)
    {
        picture2 = BitmapFactory.decodeFile(picFile.getAbsolutePath());
        this.lockLines[0] = false;
        this.lockLines[1] = false;
        this.lockLines[2] = false;
        this.lockLines[3] = false;
        this.bufLength = this.picture2.getHeight() / 100;
        this.numNeeded = (int) (bufLength * perctNeeded);
        this.perctNeeded = 0.8;
    }

    public pictureDecode(Bitmap picFile)
    {
        picture2 = picFile;
        this.lockLines[0] = false;
        this.lockLines[1] = false;
        this.lockLines[2] = false;
        this.lockLines[3] = false;
        this.bufLength = this.picture2.getHeight() / 100;
        this.numNeeded = (int) (bufLength * perctNeeded);
        this.perctNeeded = 0.8;
    }

    public void pictureCheckForColorGrayish(double tolerance)
    {
        this.arrayPic = new int[this.getWidth()][this.getHeight()];
        int high = pictureGetHighestGrayish();
        Log.i("DPCC", "Boven tolerantie; " + high);
        int colorRefBottom = (int)(tolerance * high);
        Log.i("DPCC", "Onder tolerantie; " + colorRefBottom);
        int[] number = new int[2];

        int colorLowTop = 150;
        int colorLowBottom = 0;

        for (int xPixel = 0; xPixel < this.getWidth(); xPixel++)
        {
            for (int yPixel = 0; yPixel < this.getHeight(); yPixel++)
            {
                int color = this.getPixel(xPixel, yPixel);
                int graytint = Color.blue(color) + Color.green(color) + Color.red(color);
                if (graytint <= high && graytint >= colorRefBottom)
                {
                    this.arrayPic[xPixel][yPixel] = 1;
                    number[0]++;
                }
                else if (graytint <= colorLowTop && graytint >= colorLowBottom)
                {
                    this.arrayPic[xPixel][yPixel] = 2;
                    number[1]++;
                }
                else
                {
                    this.arrayPic[xPixel][yPixel] = 0;
                }
            }
        }
        Log.i("DPCC", number[0] + " pixels out of " + this.getHeight() * this.getWidth() + "pixels are right.");
        Log.i("DPCC", number[1] + " pixels out of " + this.getHeight() * this.getWidth() + "pixels are right.");
    }

    public int pictureGetHighestGrayish()
    {
        int highest = 0;
        for (int xPixel = 0; xPixel < this.getWidth(); xPixel++)
        {
            for (int yPixel = 0; yPixel < this.getHeight(); yPixel++)
            {
                int color = this.getPixel(xPixel, yPixel);
                int graytint = Color.blue(color) + Color.green(color) + Color.red(color);
                if (graytint > highest)
                {
                    highest = graytint;
                }
            }
        }
        return highest;
    }

    public int pictureGetLowestGrayish()
    {
        int lowest = 765;
        for (int xPixel = 0; xPixel < this.getWidth(); xPixel++)
        {
            for (int yPixel = 0; yPixel < this.getHeight(); yPixel++)
            {
                int color = this.getPixel(xPixel, yPixel);
                int graytint = Color.blue(color) + Color.green(color) + Color.red(color);
                if (graytint < lowest)
                {
                    lowest = graytint;
                }
            }
        }
        return lowest;
    }

    public void pictureCheckForBoard()
    {
        double tolerance = 0.55;


        this.pictureCheckForColorGrayish(tolerance);

        this.calculateBottomLine();
        this.calculateLeftLine();
        this.calculateRightLine();
        this.calculateTopLine();

        double angleVert = this.calculateLinesAngle(this.Lines[0][0], this.Lines[1][0]);
        double angleHor = this.calculateLinesAngle(this.Lines[2][0], this.Lines[3][0]);

        boolean check = true;

        while((angleVert > 3 || angleHor > 3) && check)
        {
            tolerance = tolerance + 0.05;
            if(tolerance >= 1)
            {
                Log.e("Decode Picture", "Wasn't able to find (good) rectangle");
                break;
            }
            Log.i("PictureDecode", "Tolerance changed to:"+tolerance);
            this.pictureCheckForColorGrayish(tolerance);

            if(!this.lockLines[0])
            {
                this.calculateLeftLine();
            }
            if(!this.lockLines[1])
            {
                this.calculateRightLine();
            }
            if(!this.lockLines[2])
            {
                this.calculateTopLine();
            }
            if(!this.lockLines[3])
            {
                this.calculateBottomLine();
            }

            angleVert = this.calculateLinesAngle(this.Lines[0][0], this.Lines[1][0]);
            angleHor = this.calculateLinesAngle(this.Lines[2][0], this.Lines[3][0]);
        }

        this.setFieldCorners();
    }

    public Bitmap ArrayToBitmap()
    {
        if(this.arrayPic != null)
        {
            Bitmap bmp = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
            for(int x = 0; x < this.getWidth(); x++)
            {
                for(int y = 0; y < this.getHeight(); y++)
                {
                    if(this.arrayPic[x][y] == 1)
                    {
                        bmp.setPixel(x, this.getHeight() - 1 - y, Color.BLUE);
                    }
                    else if(this.arrayPic[x][y] == 0)
                    {
                        bmp.setPixel(x, this.getHeight() - 1 - y, Color.GREEN);
                    }
                    else if(this.arrayPic[x][y] == 2)
                    {
                        bmp.setPixel(x, this.getHeight() - 1 - y, Color.RED);
                    }
                    else if(this.arrayPic[x][y] == 3)
                    {
                        bmp.setPixel(x, this.getHeight() - 1 - y, Color.MAGENTA);
                    }
                    else if(this.arrayPic[x][y] == 4)
                    {
                        bmp.setPixel(x, this.getHeight() - 1 - y, Color.BLACK);
                    }
                    else
                    {
                        bmp.setPixel(x, this.getHeight() - 1 - y, Color.BLACK);
                    }
                }
            }
            return bmp;
        }
        return picture2;
    }

    public int getHeight()
    {
        return picture2.getHeight();
    }

    public int getWidth()
    {
        return picture2.getWidth();
    }

    public int getPixel(int xPixel, int yPixel)
    {
        return picture2.getPixel(xPixel, yPixel);
    }

    public void calculateLeftLine()
    {
        double[] line = findLeftLine();
        this.Lines[0] = line;
        if(line[3] <= 3)
        {
            this.lockLines[0] = true;
        }
    }

    public double[] findLeftLine()
    {
        Log.i("Decoded picture lines", "Starting with Left line.");
        int n = this.picture2.getHeight();
        boolean allow;

        int start1 = (int) (0.325*n);
        int end1 = (int) (0.375*n);
        int start2 = (int) (0.45*n);
        int end2 = (int) (0.55*n);
        int start3 = (int) (0.625*n);
        int end3 = (int) (0.675*n);
        int numDots = end3 + end2 + end1 + 3 - start3 - start2 - start1;
        int[][] LeftDots = new int[numDots][2];

        int j = 0;
        int value, value2;

        for(int y= start1; y <= end3; y++, j++)
        {
            LeftDots[j][1] = y;
            int x = 0;
            allow = true;
            for(int xc = 0; xc < this.picture2.getWidth() - 1; xc++)
            {
                value = this.arrayPic[xc][y];
                if(value == 1 && allow)
                {
                    if(xc == 0)
                    {
                        int num = 0;
                        for(int l = xc; l < (xc + this.bufLength); l++)
                        {
                            value2 = this.arrayPic[l][y];
                            if(value2 == 1)
                            {
                                num++;
                            }
                        }

                        if(num >= this.numNeeded)
                        {
                            allow = false;
                        }
                    }
                    else
                    {
                        x = xc;
                        break;
                    }
                }
                else if(value != 1 && allow)
                {
                    int num = 0;
                    for(int l = xc; l < (xc + this.bufLength); l++)
                    {
                        value2 = this.arrayPic[l][y];
                        if(value2 != 1)
                        {
                            num++;
                        }
                    }

                    if(num >= this.numNeeded)
                    {
                        allow = true;
                    }
                }
            }
            LeftDots[j][0] = x;

            if(y == end1)
            {
                y = start2 -1;
            }
            else if(y == end2)
            {
                y = start3 -1;
            }
        }

        this.dotsLeft = LeftDots;

        return calculateSpecLine(LeftDots, false, false);
    }

    public void calculateRightLine()
    {
        double[] line = findRightLine();
        this.Lines[1] = line;
        if(line[3] <= 3)
        {
            this.lockLines[1] = true;
        }
    }

    public double[] findRightLine()
    {
        Log.i("Decoded picture lines", "Starting with Right line.");
        int n = this.picture2.getHeight();
        boolean allow;

        int start1 = (int) (0.325*n);
        int end1 = (int) (0.375*n);
        int start2 = (int) (0.45*n);
        int end2 = (int) (0.55*n);
        int start3 = (int) (0.625*n);
        int end3 = (int) (0.675*n);
        int numDots = end3 + end2 + end1 + 3 - start3 - start2 - start1;
        int[][] RightDots = new int[numDots][2];

        int j = 0;
        int value, value2;

        for(int y = start1; y <= end3; y++, j++)
        {
            RightDots[j][1] = y;
            int x = 0;
            allow = true;
            for(int xc = this.picture2.getWidth() - 1; xc > 0; xc--)
            {
                value = this.arrayPic[xc][y];
                // TODO: check the integrety and meaning of this function
                if(value == 1 && allow)
                {
                    if(xc == 0)
                    {
                        int num = 0;
                        for(int l = xc; l > (xc - this.bufLength); l--)
                        {
                            value2 = this.arrayPic[l][y];
                            if(value2 == 1)
                            {
                                num++;
                            }
                        }

                        if(num >= this.numNeeded)
                        {
                            allow = false;
                        }
                    }
                    else
                    {
                        x = xc;
                        break;
                    }
                }
                else if(value != 1 && !allow)
                {
                    int num = 0;
                    for(int l = xc; l > (xc - this.bufLength); l--)
                    {
                        value2 = this.arrayPic[l][y];
                        if(value2 != 1)
                        {
                            num++;
                        }
                    }

                    if(num >= this.numNeeded)
                    {
                        allow = true;
                    }
                }
            }
            RightDots[j][0] = x;

            if(y == end1)
            {
                y = start2 -1;
            }
            else if(y == end2)
            {
                y = start3 -1;
            }
        }

        this.dotsRight = RightDots;

        return calculateSpecLine(RightDots, false, false);
    }

    //TODO: make it Bottom insteadof Top
    public void calculateTopLine()
    {
        double[] line = findTopLine();
        this.Lines[2] = line;
        if(line[3] <= 3)
        {
            this.lockLines[2] = true;
        }
    }

    public double[] findTopLine()
    {
        Log.i("Decoded picture lines", "Starting with Top line.");
        int n = this.picture2.getWidth();
        boolean allow;

        int start1 = (int) (0.325*n);
        int end1 = (int) (0.375*n);
        int start2 = (int) (0.45*n);
        int end2 = (int) (0.55*n);
        int start3 = (int) (0.625*n);
        int end3 = (int) (0.675*n);
        int numDots = end3 + end2 + end1 + 3 - start3 - start2 - start1;
        int[][] TopDots = new int[numDots][2];

        int j = 0;
        int value, value2;

        for(int x = start1; x <= end3; x++, j++)
        {
            TopDots[j][0] = x;
            int y = 0;
            allow = true;
            //for(int yc = 0; yc < this.picture2.getHeight() - 1; yc++)
            for(int yc = this.picture2.getHeight() - 1; yc > 0; yc--)
            {
                value = this.arrayPic[x][yc];
                if(value == 1 && allow)
                {
                    //if(yc == 0)
                    if(yc == this.picture2.getHeight() - 1)
                    {
                        int num = 0;
                        //for(int l = yc; l < (yc + this.bufLength); l++)
                        for(int l = yc; l > (yc - this.bufLength); l--)
                        {
                            value2 = this.arrayPic[x][l];
                            if(value2 == 1)
                            {
                                num++;
                            }
                        }

                        if(num >= this.numNeeded)
                        {
                            allow = false;
                        }
                    }
                    else
                    {
                        y = yc;
                        break;
                    }
                }
                else if(value != 1 && !allow)
                {
                    int num = 0;
                    //for(int l = yc; l < (yc + this.bufLength); l++)
                    for(int l = yc; l < (yc - this.bufLength); l--)
                    {
                        value2 = this.arrayPic[x][l];
                        if(value2 != 1)
                        {
                            num++;
                        }
                    }

                    if(num >= this.numNeeded)
                    {
                        allow = true;
                    }
                }
            }
            TopDots[j][1] = y;

            if(x == end1)
            {
                x = start2 -1;
            }
            else if(x == end2)
            {
                x = start3 -1;
            }
        }

        this.dotsTop = TopDots;

        return calculateSpecLine(TopDots, true, false);
    }

    //TODO: make it Top insteadof Bottom
    public void calculateBottomLine()
    {
        double[] line = findBottomLine();
        this.Lines[3] = line;
        if(line[3] <= 3)
        {
            this.lockLines[3] = true;
        }
    }

    public double[] findBottomLine()
    {
        Log.i("Decoded picture lines", "Starting with Bottom line.");
        int n = this.picture2.getWidth();
        boolean allow;

        int start1 = (int) (0.325*n);
        int end1 = (int) (0.375*n);
        int start2 = (int) (0.45*n);
        int end2 = (int) (0.55*n);
        int start3 = (int) (0.625*n);
        int end3 = (int) (0.675*n);
        int numDots = end3 + end2 + end1 + 3 - start3 - start2 - start1;
        int[][] BottomDots = new int[numDots][2];

        int j = 0;
        int value, value2;

        for(int x = start1; x <= end3; x++, j++)
        {
            BottomDots[j][0] = x;
            int y = 0;
            allow = true;
            //for(int yc = this.picture2.getHeight() - 1; yc > 0; yc--)
            for(int yc = 0; yc < this.picture2.getHeight() - 1; yc++)
            {
                value = this.arrayPic[x][yc];
                if(value == 1 && allow)
                {
                    //if(yc == this.picture2.getHeight() - 1)
                    if(yc == 0)
                    {
                        int num = 0;
                        //for(int l = yc; l > (yc - this.bufLength); l--)
                        for(int l = yc; l < (yc + this.bufLength); l++)
                        {
                            value2 = this.arrayPic[x][l];
                            if(value2 == 1)
                            {
                                num++;
                            }
                        }

                        if(num >= this.numNeeded)
                        {
                            allow = false;
                        }
                    }
                    else
                    {
                        y = yc;
                        break;
                    }
                }
                else if(value != 1 && !allow)
                {
                    int num = 0;
                    //for(int l = yc; l > (yc - this.bufLength); l--)
                    for(int l = yc; l < (yc + this.bufLength); l++)
                    {
                        value2 = this.arrayPic[x][l];
                        if(value2 != 1)
                        {
                            num++;
                        }
                    }

                    if(num >= this.numNeeded)
                    {
                        allow = true;
                    }
                }
            }
            BottomDots[j][1] = y;

            if(x == end1)
            {
                x = start2 -1;
            }
            else if(x == end2)
            {
                x = start3 -1;
            }
        }

        this.dotsBottom = BottomDots;

        return calculateSpecLine(BottomDots, true, false);
    }

    public void giveRow(int numRow)
    {
        for(int i = 0; i < this.arrayPic[0].length; i++)
        {
            Log.e("GetRow"+numRow, "x=" + i +"; value="+this.arrayPic[numRow][i]);
        }
    }

    public void paintCorner()
    {
        for(int i = 0; i < 100; i++)
        {
            for(int j = 0; j < 50; j++)
            {
                this.arrayPic[j][i] = 3;
            }
        }
    }

    public double calculateLinesAngle(double xFactorLine1, double xFactorLine2)
    {
        double pLine1 = Math.toDegrees(Math.atan(xFactorLine1));
        double pLine2 = Math.toDegrees(Math.atan(xFactorLine2));
        return Math.abs(pLine1 - pLine2);
    }

    public double[] calculateSpecLine(int[][] Dots, boolean toX, boolean extraCheck)
    {
        int times = 6;
        int numDots = Dots.length;
        Log.i("Calc. Line", "numDots="+numDots);
        if(!toX)
        {
            Dots = switchDotsXY(Dots);
        }
        double[] line = calculateLine(Dots, true);
        for(int i = 0; i < 5; i++)
        {
            if(line[2] <= 0.8 && line[2] >= -0.8)
            {
                int[][] DotsR2 = new int[numDots][3];
                for(int j = 0; j < numDots; j++)
                {
                    int DotX = Dots[j][0];
                    int DotY = Dots[j][1];
                    int R2 = calculateR2dotToLine(DotX, DotY, line[0], line[1], true);
                    DotsR2[j][0] = DotX;
                    DotsR2[j][1] = DotY;
                    DotsR2[j][2] = R2;
                }
                Dots = reduceDotsByR2(DotsR2, 0.9);
                numDots = Dots.length;
                line = calculateLine(Dots, true);
            }
            else
            {
                times = i + 1;
                break;
            }
        }

        if(!toX)
        {
            double a = -(line[1]/line[0]);
            double b = (1/line[0]);
            line[0] = b;
            line[1] = a;
        }
        Log.i("Calc. Line" , "Calculated line; y = "+line[1]+" + "+line[0]+"x with correlation r2 = "+line[2]+". Within "+times+" times and with " +numDots+ " dots remaining");
        double[] retLine = new double[5];
        retLine[0] = line[0];
        retLine[1] = line[1];
        retLine[2] = line[2];
        retLine[3] = times;
        retLine[4] = numDots;

        if(extraCheck)
        {
            Dots = switchDotsXY(Dots);
            showDotsOnField(Dots);
        }

        return retLine;
    }

    public int[][] reduceDotsByR2(int[][] Dots, double factor)
    {
        int numDots = Dots.length;
        int highestR2 = Dots[0][2];
        int lowestR2 = Dots[0][2];
        for(int i = 1; i < numDots; i++)
        {
            if(Dots[i][2] > highestR2)
            {
                highestR2 = Dots[i][2];
            }
            else if(Dots[i][2] < lowestR2)
            {
                lowestR2 = Dots[i][2];
            }
        }
        List<int[]> dotsList = new ArrayList<>();
        int limit = (int) (lowestR2 + (factor *(highestR2 - lowestR2)));
        for(int i = 1; i < numDots; i++)
        {
            if(Dots[i][2] < limit)
            {
                int dot[] = new int[3];
                int DotX = Dots[i][0];
                int DotY = Dots[i][1];
                dot[0] = DotX;
                dot[1] = DotY;
                dotsList.add(dot);
            }
        }
        int[][] newDots = new int[dotsList.size()][3];
        for(int i = 0; i < dotsList.size(); i++)
        {
            newDots[i] = dotsList.get(i);
        }

        //Log.i("DPCC", dotsList.size() + " out of " + numDots + " dots passed.");
        return newDots;
    }

    public Integer calculateR2dotToLine(int dotx, int doty, double b, double a, boolean factorIsToX)
    {
        double R = (((b*doty)+dotx)/((a*b*b)+a)) - dotx;

        return (int) (R*R);
    }

    public double[] calculateLine(int[][] Dots, boolean toX)
    {
        int numDots = Dots.length;
        if(!toX)
        {
            Dots = switchDotsXY(Dots);
        }

        double sx = 0;
        double sy = 0;
        double sx2 = 0;
        double sy2 = 0;
        double sxy = 0;
        //for(int i = 0; i < numDots; i++)
        for(int[] Dotvalues : Dots)
        {
            //int[] Dotvalues = Dots[i];
            sx = sx + (double)Dotvalues[0];
            sy = sy + (double)Dotvalues[1];
            sx2 = sx2 + ((double)Dotvalues[0] * (double)Dotvalues[0]);
            sy2 = sy2 + ((double)Dotvalues[1] * (double)Dotvalues[1]);
            sxy = sxy + ((double)Dotvalues[0] * (double)Dotvalues[1]);
        }

        //Je krijgt een lijn in vorm van y= 'a' + 'b' * x, ookwel y= '[1]' + '[0]' * x
        double gx = sx / numDots;
        double gy = sy / numDots;

        double ssxx = sx2 - (numDots * gx * gx);
        double ssyy = sy2 - (numDots * gy * gy);
        double ssxy = sxy - (numDots * gx * gy);

        double b = ssxy / ssxx;
        double a = gy - (b * gx);
        double r2 = (ssxy * ssxy) / (ssxx * ssyy);
        if(!toX)
        {
            a = -(a/b);
            b = (1/b);
        }

        double[] ans = new double[3];
        ans[0] = b;
        ans[1] = a;
        ans[2] = r2;
        return ans;
    }

    public int[][] switchDotsXY (int[][] Dots)
    {
        int numDots = Dots.length;
        int[][] newDots = new int[numDots][2];
        Log.i("Calculate Line", "Switching dots X and Y components");
        for(int i = 0; i < numDots; i++)
        {
            int DotNewX = Dots[i][1];
            int DotNewY = Dots[i][0];
            newDots[i][0] = DotNewX;
            newDots[i][1] = DotNewY;
        }
        return newDots;
    }

    public void drawLine(double c, double xfactor, int colorNumber, String lineName)
    {
        int numx = this.arrayPic.length;
        int numy = this.arrayPic[0].length;
        int n = 0;
        if(xfactor < 1 && xfactor > -1)
        {
            for(int i = 0; i < numx; i++)
            {
                int yvalue = (int) (c + (xfactor * i));
                if(numy > yvalue && 0 < yvalue)
                {
                    this.arrayPic[i][yvalue] = colorNumber;
                    n++;
                }
            }
        }
        else
        {
            for(int i = 0; i < numy; i++)
            {
                int xvalue = (int) ((i - c) / xfactor);
                if(numx > xvalue && 0 < xvalue)
                {
                    this.arrayPic[xvalue][i] = colorNumber;
                    n++;
                }
            }
        }
        Log.i("Decoded picture lines", lineName +"; drawn. "+ n +" pixels set.");
    }

    public void showDots(int[][] dots, int colorNumber, String error)
    {
        int numDots = dots.length;
        Log.i("Decode Picture", "Show Dots of " + error);
        //for(int i = 0; i < numDots; i++)
        for(int[] dot : dots)
        {
            //this.arrayPic[ (dots[i][0]) ][ (dots[i][1]) ] = colorNumber;
            this.arrayPic[ (dot[0]) ][ (dot[1]) ] = colorNumber;
        }
    }

    public void drawLines()
    {
        /*double[] test = findLeftLine();
        double[] test2 = findRightLine();
        double[] test3 = findTopLine();
        double[] test4 = findBottomLine();

        this.Lines = new double[4][3];
        this.Lines[0] = test;
        this.Lines[1] = test2;
        this.Lines[2] = test3;
        this.Lines[3] = test4;*/

        int numx = this.arrayPic.length;
        int numy = this.arrayPic[0].length;
        Log.i("Decoded picture lines", "Calculating lines: y=" + numy + "; x=" + numx);
        drawLine(this.Lines[0][1], this.Lines[0][0], 3, "Left line");
        drawLine(this.Lines[1][1], this.Lines[1][0], 3, "Right line");
        drawLine(this.Lines[2][1], this.Lines[2][0], 3, "Top line");
        drawLine(this.Lines[3][1], this.Lines[3][0], 3, "Bottom line");

        showDots(this.dotsLeft, 4, "Left");
        showDots(this.dotsRight, 4, "Right");
        showDots(this.dotsTop, 4, "Top");
        showDots(this.dotsBottom, 4, "Bottom");
    }

    public Bitmap FieldToBitmap()
    {
        int width = this.arrayField.length;
        int height = this.arrayField[0].length;
        int n = 0;
        if(this.arrayField != null)
        {
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for(int x = 0; x < width; x++)
            {
                for(int y = 0; y < height; y++)
                {
                    if(this.arrayField[x][y] == 1)
                    {
                        bmp.setPixel(x, height - 1 - y, Color.BLUE);
                        n++;
                    }
                    else if(this.arrayField[x][y] == 0)
                    {
                        bmp.setPixel(x, height - 1 - y, Color.GREEN);
                        n++;
                    }
                    else if(this.arrayField[x][y] == 2)
                    {
                        bmp.setPixel(x, height - 1 - y, Color.RED);
                        n++;
                    }
                    else if(this.arrayField[x][y] == 3)
                    {
                        bmp.setPixel(x, height - 1 - y, Color.MAGENTA);
                        n++;
                    }
                    else if(this.arrayField[x][y] == 4)
                    {
                        bmp.setPixel(x, height - 1 - y, Color.BLACK);
                        n++;
                    }
                    else if(this.arrayField[x][y] == 5)
                    {
                        bmp.setPixel(x, height - 1 - y, Color.YELLOW);
                        n++;
                    }
                    else
                    {
                        bmp.setPixel(x, height - 1 - y, Color.BLACK);
                        n++;
                    }
                }
            }

            return bmp;
        }
        return picture2;
    }

    public int[] getTopLeftCorner(){return this.FieldCorners[0];}

    public int[] getBottomLeftCorner(){return this.FieldCorners[1];}

    public int[] getTopRightCorner(){return this.FieldCorners[2];}

    public int[] getBottomRightCorner(){return this.FieldCorners[3];}

    public void setFieldCorners()
    {
        double[][] Lines = this.Lines;
        int[][] Corners = new int[4][2];

        Corners[0][0] = (int) ((Lines[0][1] - Lines[2][1])/(Lines[2][0] - Lines[0][0]));
        Corners[0][1] = (int) ((Lines[0][1]*Lines[2][0] - Lines[2][1]*Lines[0][0])/(Lines[2][0] - Lines[0][0]));
        Log.i("SetCorners","TL corner[0]=["+Corners[0][0]+","+Corners[0][1]+"]");

        Corners[1][0] = (int) ((Lines[0][1] - Lines[3][1])/(Lines[3][0] - Lines[0][0]));
        Corners[1][1] = (int) ((Lines[0][1]*Lines[3][0] - Lines[3][1]*Lines[0][0])/(Lines[3][0] - Lines[0][0]));
        Log.i("SetCorners","BL corner[1]=["+Corners[1][0]+","+Corners[1][1]+"]");

        Corners[2][0] = (int) ((Lines[1][1] - Lines[2][1])/(Lines[2][0] - Lines[1][0]));
        Corners[2][1] = (int) ((Lines[1][1]*Lines[2][0] - Lines[2][1]*Lines[1][0])/(Lines[2][0] - Lines[1][0]));
        Log.i("SetCorners","TR corner[2]=["+Corners[2][0]+","+Corners[2][1]+"]");

        Corners[3][0] = (int) ((Lines[1][1] - Lines[3][1])/(Lines[3][0] - Lines[1][0]));
        Corners[3][1] = (int) ((Lines[1][1]*Lines[3][0] - Lines[3][1]*Lines[1][0])/(Lines[3][0] - Lines[1][0]));
        Log.i("SetCorners","BR corner[3]=["+Corners[3][0]+","+Corners[3][1]+"]");

        this.FieldCorners = Corners;
    }

    public void setField()
    {
        int heightMax = 0;
        int heightMin = this.getHeight();

        int lengths[] = new int[4];
        for (int i = 0; i < 4; i++)
        {
            int[] corner = this.FieldCorners[i];
            lengths[i] = corner[0];

            if(heightMin > corner[1])
            {
                heightMin = corner[1];
            }
            else if(heightMax < corner[1])
            {
                heightMax = corner[1];
            }
        }

        for(int i = 0; i < 4; i++)
        {
            for(int j = 3; j > i; j--)
            {
                if(lengths[j] < lengths[i])
                {
                    int buf = lengths[i];
                    lengths[i] =lengths[j];
                    lengths[j] = buf;
                }
            }
        }

        int length = lengths[3] - lengths[0];
        int height = heightMax - heightMin;
        this.startingPoint[0] = lengths[0];
        this.startingPoint[1] = heightMin;

        this.arrayFieldGray = new int[length][height];
        int highestGray = 0;
        int lowestGray = 768;
        this.arrayField = new int[length][height];
        int valueTop, valueBottom;
        int x, y, n=0, check;

        Log.i("SetField", "length=" + length + ",height=" + height);
        Log.i("SetField", "startpoint ["+lengths[0]+";"+heightMin+"]");

        for(int xPixel = 0; xPixel < length; xPixel++)
        {
            x = xPixel + lengths[0];
            valueTop = (int) (this.Lines[2][1] + this.Lines[2][0]*x);
            valueBottom = (int) (this.Lines[3][1] + this.Lines[3][0]*x);

            if(valueBottom < heightMin)
            {
                valueBottom = heightMin;
            }
            if(valueTop > heightMax)
            {
                valueTop = heightMax;
            }

            int yBot;
            int yTop;

            if(x < lengths[1])
            {
                yBot = this.getBottomLeftCorner()[0];
                yTop = this.getTopLeftCorner()[0];
                check = (int) (this.Lines[0][1] + this.Lines[0][0]*x);

                if(yBot < yTop)
                {
                    valueBottom = check;
                }
                else if(yBot > yTop)
                {
                    valueTop = check;
                }
            }
            else if(x > lengths[2])
            {
                yBot = this.getBottomRightCorner()[0];
                yTop = this.getTopRightCorner()[0];
                check = (int) (this.Lines[1][1] + this.Lines[1][0]*x);

                if(yBot < yTop)
                {
                    valueTop = check;
                }
                else if(yBot > yTop)
                {
                    valueBottom = check;
                }
            }

            for(int yPixel = 0; yPixel < height; yPixel++)
            {
                y = yPixel + heightMin;
                if(y < valueBottom || y > valueTop)
                {
                    this.arrayField[xPixel][yPixel] = 0;
                    n++;
                }
                else
                {
                    int color = this.getPixel(x, y);
                    int colorGray = Color.blue(color) + Color.green(color) + Color.red(color);
                    this.arrayFieldGray[xPixel][yPixel] = colorGray;
                    if (colorGray < lowestGray)
                    {
                        lowestGray = colorGray;
                    }
                    else if (colorGray > highestGray)
                    {
                        highestGray = colorGray;
                    }
                }
            }
        }
        Log.i("SetField", "n="+n+"/"+(length*height));

        double tolerance = 0.45;
        n=0;
        for(int xPixel = 0; xPixel < length; xPixel++)
        {
            boolean firstBlue = false;
            for (int yPixel = 0; yPixel < height; yPixel++)
            {
                int gray = this.arrayFieldGray[xPixel][yPixel];
                if(gray == 0)
                {
                    n++;
                }
                else if( gray >= (int)(highestGray * (1-tolerance)) )
                {
                    this.arrayField[xPixel][yPixel] = 1;
                    firstBlue = true;
                }
                else if(firstBlue)
                {
                    this.arrayField[xPixel][yPixel] = 2;
                }
                else
                {
                    this.arrayField[xPixel][yPixel] = 1;
                }
            }
        }
        Log.i("SetField", "n="+n);

        for(int xPixel = 0; xPixel < length; xPixel++)
        {
            for (int yPixel = height - 1; yPixel > 0; yPixel--)
            {
                int colorCode = this.arrayField[xPixel][yPixel];
                if(colorCode == 1)
                {
                    break;
                }
                else if(colorCode == 2)
                {
                    this.arrayField[xPixel][yPixel] = 1;
                }
            }
        }
        for(int yPixel = 0; yPixel < height; yPixel++)
        {
            for (int xPixel = 0; xPixel < length; xPixel++)
            {
                int colorCode = this.arrayField[xPixel][yPixel];
                if(colorCode == 1)
                {
                    break;
                }
                else if(colorCode == 2)
                {
                    this.arrayField[xPixel][yPixel] = 1;
                }
            }
        }
        for(int yPixel = 0; yPixel < height; yPixel++)
        {
            for (int xPixel = length - 1; xPixel > 0; xPixel--)
            {
                int colorCode = this.arrayField[xPixel][yPixel];
                if(colorCode == 1)
                {
                    break;
                }
                else if(colorCode == 2)
                {
                    this.arrayField[xPixel][yPixel] = 1;
                }
            }
        }
    }

    public void decodeField()
    {
        int[] middelpoint = new int[2];
        int length = this.arrayField.length;
        int height = this.arrayField[0].length;
        boolean paperHorizontal = true, fieldHorizontal = true;

        middelpoint[0] = (int) (0.25 * (this.getTopLeftCorner()[0] + this.getTopRightCorner()[0] + this.getBottomLeftCorner()[0] + this.getBottomRightCorner()[0]) - this.startingPoint[0]);
        middelpoint[1] = (int) (0.25 * (this.getTopLeftCorner()[1] + this.getTopRightCorner()[1] + this.getBottomLeftCorner()[1] + this.getBottomRightCorner()[1]) - this.startingPoint[1]);

        if(paperHorizontal && fieldHorizontal)
        {
            List<double[]> listLinesV = new ArrayList<>();
            double b = 0.5 * (this.Lines[0][0] + this.Lines[1][0]);
            double a = middelpoint[1] - (b * middelpoint[0]);
            int range = 10;
            int n = 0;
            double aDiv = (-4 * range * b * Math.sqrt((1/(b*b))+1));
            double xDivM = 0;
            boolean toRight = true;

            do
            {
                double[] line = getLineFromField(a, b, range, 2);
                if(line[4] > (0.5 * height) )
                {
                    n++;
                    listLinesV.add(line);
                    showLineOnField(line, "Vertical line " + n, 5);
                    b = line[0];
                    a = line[1];
                    aDiv = (-4 * range * b * Math.sqrt((1/(b*b))+1));
                }
                Log.i("decField", "numDotsFound = " + line[4]);

                if(toRight)
                {
                    a += aDiv;
                }
                else
                {
                    a -= aDiv;
                }

                if((((0.25 * height)-a)/b) > length)
                {
                    a = middelpoint[1] - (b * middelpoint[0]) - aDiv;
                    toRight = false;
                }
                else if((((0.5 * height)-a)/b) < 0)
                {
                    Log.e("Field decode", "Fatal: Could not find 2 vertical lines in given field!!!");
                    break;
                }
            }
            while( !(n >= 2) );

            if(n >= 2)
            {
                if(!toRight)
                {
                    a += aDiv;
                }
                else
                {
                    a -= aDiv;
                }

                xDivM = Math.abs( (((height * 0.5) - listLinesV.get(0)[1]) / listLinesV.get(0)[0]) - (((height * 0.5) - listLinesV.get(1)[1]) / listLinesV.get(1)[0]) );
                aDiv = Math.abs(xDivM * b);
                if((toRight == true && b < 0) || (toRight == false && b > 0))
                {
                    a += aDiv;
                }
                else
                {
                    a -= aDiv;
                }

                while((((0.25 * height)-a)/b) > 0)
                {
                    double[] line2 = new double[2];
                    line2[0] = b;
                    line2[1] = a;

                    double[] line = getLineFromField(a, b, range, 2);

                    if(line[4] > (0.5 * height) )
                    {
                        n++;
                        listLinesV.add(line);
                        showLineOnField(line, "Vertical line " + n, 5);
                        b = line[0];
                        a = line[1];
                        aDiv = Math.abs(xDivM * b);
                    }
                    Log.i("decField", "numDotsFound = " + line[4]);

                    if((toRight == true && b < 0) || (toRight == false && b > 0))
                    {
                        a += aDiv;
                    }
                    else
                    {
                        a -= aDiv;
                    }

                    if((((0.5 * height)-a)/b) > length)
                    {
                        a = listLinesV.get(0)[1];
                        b = listLinesV.get(0)[0];
                        aDiv = Math.abs(xDivM * b);

                        toRight = false;
                        if(b > 0)
                        {
                            a += aDiv;
                        }
                        else
                        {
                            a -= aDiv;
                        }
                    }
                }

                //
                // Check for horizontal lines
                //
                List<double[]> listLinesH = new ArrayList<>();
                b = 0.5 * (this.Lines[2][0] + this.Lines[3][0]);
                a = middelpoint[1] - (b * middelpoint[0]);
                boolean toBottom = true;
                n = 0;

                do
                {
                    double[] line = getLineFromField(a, b, range, 2);
                    if(line[4] > (0.25 * length) )
                    {
                        n++;
                        listLinesH.add(line);
                        showLineOnField(line, "Horizontal line " + n, 5);
                        b = line[0];
                        a = line[1];
                        aDiv = (-4 * range * b * Math.sqrt((1/(b*b))+1));
                    }
                    Log.i("decField", "numDotsFound = " + line[4]);

                    if(toBottom)
                    {
                        a -= aDiv;
                    }
                    else
                    {
                        a += aDiv;
                    }

                    if((a+ (b * 0.5 * length)) < 0)
                    {
                        a = middelpoint[1] - (b * middelpoint[0]) - aDiv;
                        toBottom = false;
                    }
                    else if((a+ (b * 0.5 * length)) > height)
                    {
                        Log.e("Field decode", "Fatal: Could not find 1 horizontal line in given field!!!");
                        break;
                    }
                }
                while( !(n >= 1) );

                if(n >= 1)
                {
                    if(toBottom)
                    {
                        a += aDiv;
                    }
                    else
                    {
                        a -= aDiv;
                    }

                    //TODO: find better soluution which uses the last found vertical lines
                    //xDivM = Math.abs( (listLinesV.get(0)[1] + (0.5 * length * listLinesV.get(0)[0])) - (listLinesV.get(1)[1] + (0.5 * length * listLinesV.get(1)[0])) );
                    aDiv = xDivM;
                    if(toBottom)
                    {
                        a -= aDiv;
                    }
                    else
                    {
                        a += aDiv;
                    }

                    while((a+ (b * 0.5 * length)) < height && n <= 20)
                    {
                        double[] line2 = new double[2];
                        line2[0] = b;
                        line2[1] = a;

                        double[] line = getLineFromField(a, b, range, 2);

                        if(line[4] > (0.25 * length) )
                        {
                            n++;
                            listLinesH.add(line);
                            showLineOnField(line, "Horizontal Line " + n, 5);
                            b = line[0];
                            a = line[1];
                        }
                        Log.i("decField", "numDotsFound = " + line[4]);

                        if(toBottom)
                        {
                            a -= aDiv;
                        }
                        else
                        {
                            a += aDiv;
                        }

                        if((a+ (b * 0.5 * length)) < 0)
                        {
                            a = listLinesH.get(0)[1];
                            b = listLinesH.get(0)[0];

                            toBottom = false;
                            a += aDiv;
                        }
                    }
                }
            }
        }
    }

    public double[] getLineFromField(double expectedA, double expectedB, int R, int ColorCode)
    {
        int length = this.arrayField.length;
        int height = this.arrayField[0].length;
        Log.e("GLFF", "length = " + length + "; heigth  = " + height);
        int left, right, x, num = 0;
        boolean toX = false;
        if(expectedB > -1 && expectedB < 1)
        {
            Log.i("FML", "switching X and Y!");
            toX = true;
            int buf = length;
            length = height;
            height = buf;
            expectedA = -expectedA/expectedB;
            expectedB = 1/expectedB;
        }
        int range = (int) Math.sqrt((R * R) + ((R * R) / (expectedB * expectedB)));
        List<int[]> listDots = new ArrayList<>();
        for(int yPixel = 0; yPixel < height; yPixel++)
        {
            x = (int) ((yPixel - expectedA) / expectedB);

            left = x - range;
            if(left < 0)
            {
                left = 0;
            }
            right = x + range;
            if(right > length)
            {
                right = length - 1;
            }

            for(int xPixel = left; xPixel <= right; xPixel++, num++)
            {
                int[] Dot = new int[2];

                if(!toX && this.arrayField[xPixel][yPixel] == 2)
                {
                    Dot[0] = xPixel;
                    Dot[1] = yPixel;
                    listDots.add(Dot);
                }
                else if(toX && this.arrayField[yPixel][xPixel] == 2)
                {
                    Dot[1] = xPixel;
                    Dot[0] = yPixel;
                    listDots.add(Dot);
                }
            }
        }

        int[][] Dots = new int[listDots.size()][2];
        for(int n = 0; n < listDots.size(); n++)
        {
            Dots[n] = listDots.get(n);
        }

        double line[];

        line = calculateSpecLine(Dots, toX, false);

        //showDotsOnField(Dots);

        return line;
    }

    public void showDotsOnField(int dots[][])
    {
        //for(int i = 0; i < dots.length; i++)
        for(int[] dot : dots)
        {
            //this.arrayField[dots[i][0]][dots[i][1]] = 4;
            this.arrayField[dot[0]][dot[1]] = 4;
        }
    }

    public void showLineOnField(double line[], String lineName, int colorCode)
    {
        int numx = this.arrayField.length;
        int numy = this.arrayField[0].length;
        int n = 0;
        if(line[0] < 1 && line[0] > -1)
        {
            for(int i = 0; i < numx; i++)
            {
                int yvalue = (int) (line[1] + (line[0] * i));
                if(numy > yvalue && 0 < yvalue)
                {
                    this.arrayField[i][yvalue] = colorCode;
                    n++;
                }
            }
        }
        else
        {
            for(int i = 0; i < numy; i++)
            {
                int xvalue = (int) ((i - line[1]) / line[0]);
                if(numx > xvalue && 0 < xvalue)
                {
                    this.arrayField[xvalue][i] = colorCode;
                    n++;
                }
            }
        }
        Log.i("Decoded picture lines", lineName +", y = " + line[1] + " + " + line[0] + " * x ; drawn. "+ n +" pixels set.");
    }
}
