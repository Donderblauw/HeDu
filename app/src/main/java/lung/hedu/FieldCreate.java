package lung.hedu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Luuk on 11-8-2015.
 */
public class FieldCreate
{
    private int DimX, DimY, PagesX, PagesY, rectBorderWidth, numPages;
    private double pageHeight, pageWidth, pageBorderHeight, pageBorderWidth, DimRect;
    private PdfDocument.Page[] pages;
    private PrintedPdfDocument document;
    private Paint paintBlack, paintWhite, paintRed;
    private Rect rectInner, rectOuter;

    public FieldCreate()
    {
        this.DimX = 6;
        this.DimY = 10;
        this.pageHeight = 27.5;
        this.pageWidth = 19;
        this.DimRect = 5;
        this.rectBorderWidth = 2;
        this.paintBlack = new Paint();
        this.paintWhite = new Paint();
        this.paintWhite.setARGB(255, 255, 255, 255);
        this.paintRed = new Paint();
        this.paintRed.setARGB(255, 255, 0, 0);

        int r = this.toInchesRounded72th(this.DimRect);
        Log.i("Create Field", "r=" + r);
        Rect rect = new Rect();
        rect.top = 0;
        rect.left = 0;
        rect.bottom = r;
        rect.right = r;
        this.rectOuter = rect;
        Rect rectIn = new Rect();
        rectIn.top = 0;
        rectIn.left = 0;
        rectIn.bottom = (r - (2*this.rectBorderWidth));
        rectIn.right = (r - (2*this.rectBorderWidth));
        this.rectInner = rectIn;
    }

    public void createPDF()
    {
        //Context context = new Context();
        PrintAttributes.Builder build = new PrintAttributes.Builder();
        build.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
        build.setMinMargins(PrintAttributes.Margins.NO_MARGINS);
        PrintAttributes.Resolution res = new PrintAttributes.Resolution("1", "Resolution used for HerpDerp", 600, 600);
        build.setResolution(res);
        build.setColorMode(1);
        PrintedPdfDocument doc = new PrintedPdfDocument(ApplicationContextProvider.getContext(), build.build());
        this.document = doc;
        //PrintedPdfDocument doc = new PrintedPdfDocument(Context.MODE_WORLD_READABLE, build.build());

        /*
        Page docPage1 = doc.startPage(0);

        //View content = getContentView();
        //content.draw(docPage1.getCanvas());
        Canvas docPage1Canvas = docPage1.getCanvas();
        Rect rect = new Rect();
        rect.top = 0;
        rect.left = 0;
        rect.bottom = 50;
        rect.right = 50;
        rect.offsetTo(50, 50);
        Paint paint = new Paint();
        docPage1Canvas.drawRect(rect, paint);

        doc.finishPage(docPage1);
        */
        this.calculatePages();
        this.paintFieldOnPages();

        FileIO.savePDFPrivate("Naam", doc);

        doc.close();
    }

    public void calculatePages()
    {
        int n = 0, nAlt = 0, pagesHorizontal, pagesVertical, pagesHorizontalAlt, pagesVerticalAlt;
        Log.i("Create Field", "ch="+pageWidth+"; chk="+pageWidth % this.DimRect+"; ck="+this.DimRect);
        pagesHorizontal = (int) Math.ceil( (this.DimX * this.DimRect)/(this.pageWidth) );
        Log.i("Create Field", "uitk="+pagesHorizontal);
        pagesVertical = (int) Math.ceil( (this.DimY * this.DimRect)/(this.pageHeight) );
        n = pagesHorizontal * pagesVertical;
        Log.i("Create Field", "pagesH="+pagesHorizontal+"; pagesV="+pagesVertical);
        pagesHorizontalAlt = (int) Math.ceil( (this.DimY * this.DimRect)/(this.pageWidth) );
        pagesVerticalAlt = (int) Math.ceil( (this.DimX * this.DimRect)/(this.pageHeight) );
        nAlt = pagesHorizontalAlt  * pagesVerticalAlt;

        Log.i("Create Field", "n="+n+"; nAlt="+nAlt);
        if(nAlt < n)
        {
            Log.e("Create Field", "In this case it shouldnt get here");
            pagesHorizontal = pagesHorizontalAlt;
            pagesVertical = pagesVerticalAlt;

            n = nAlt;

            nAlt = this.DimX;
            this.DimX = this.DimY;
            this.DimY = nAlt;
        }

        /*
        this.pages = new Page[n];
        for(int i = 0; i < n; i++)
        {
            Log.i("Create Field", "i = " + i);
            this.pages[i] = this.document.startPage(i);
        }
        */
        this.numPages = n;
        this.PagesX = pagesHorizontal;
        this.PagesY = pagesVertical;
        Log.i("Create Field", "pagesX="+this.PagesX+"; pagesY="+this.PagesY);
    }

    public void fieldRect(Canvas canvas, int posX, int posY)
    {
        Rect rectOut = this.rectOuter;
        rectOut.offsetTo(posX, posY);
        canvas.drawRect(rectOut, this.paintBlack);
        Rect rectIn = this.rectInner;
        rectIn.offsetTo(posX + this.rectBorderWidth, posY + this.rectBorderWidth);
        canvas.drawRect(rectIn, this.paintWhite);
        //Log.i("Create Field", "PutRect at["+posX+";"+posY+"]");
    }

    public void drawLineHorz(Canvas canvas, double startX, double startY, double length)
    {
        int width = 2;
        Rect rect = new Rect();
        rect.top = 0;
        rect.left = 0;
        rect.bottom = width;
        rect.right = this.toInchesRounded72th(length);
        rect.offsetTo(this.toInchesRounded72th(startX) - (width/2), this.toInchesRounded72th(startY));
        canvas.drawRect(rect, this.paintRed);
    }

    public void drawLineVert(Canvas canvas, double startX, double startY, double length)
    {
        int width = 2;
        Rect rect = new Rect();
        rect.top = 0;
        rect.left = 0;
        rect.bottom = this.toInchesRounded72th(length);
        rect.right = width;
        rect.offsetTo(this.toInchesRounded72th(startX), this.toInchesRounded72th(startY) - (width/2));
        canvas.drawRect(rect, this.paintRed);
    }

    public void paintRightConnect(Canvas canvas)
    {
        canvas.drawLine(0,50,50,50,this.paintRed);
        Log.e("Create Field", "Creating Right connection");
        int heightConnection = 8;
        double interval = (this.toCm(this.document.getPageHeight()/72) - (3 * 8))/4;
        //double border = 0.5 * (this.toCm(this.document.getPageWidth()/72) - this.pageWidth);
        double x = this.pageWidth + this.pageBorderWidth;
        double y = interval;
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.toInchesRounded72th(x), this.toInchesRounded72th(y + heightConnection), this.paintRed);
        //this.drawLineVert(canvas, x, y, heightConnection);
        y += interval + heightConnection;
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.toInchesRounded72th(x), this.toInchesRounded72th(y + (heightConnection/4)), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.toInchesRounded72th(x + this.pageBorderWidth), this.toInchesRounded72th(y), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y + (3*heightConnection/4)), this.toInchesRounded72th(x), this.toInchesRounded72th(y + heightConnection), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y + heightConnection), this.toInchesRounded72th(x + this.pageBorderWidth), this.toInchesRounded72th(y + heightConnection), this.paintRed);
        //this.drawLineVert(canvas, x, y, (heightConnection/4));
        //this.drawLineHorz(canvas, x, y, border);
        //this.drawLineVert(canvas, x, y + (3*heightConnection/4), heightConnection);
        //this.drawLineHorz(canvas, x, y + heightConnection, border);
        y += interval + heightConnection;
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.toInchesRounded72th(x), this.toInchesRounded72th(y + heightConnection), this.paintRed);
        //this.drawLineVert(canvas, x, y, heightConnection);
        //canvas.drawLine(50, 50, 50, 100, this.paintRed);
    }

    public void paintLeftConnect(Canvas canvas)
    {
        Log.e("Create Field", "Creating Left connection");
        //canvas.drawLine(50,0,50,0,this.paintRed);
        int heightConnection = 8;
        double interval = (this.toCm(this.document.getPageHeight()/72) - (3 * 8))/4;
        //double border = 0.5 * (this.toCm(this.document.getPageWidth()/72) - this.pageWidth);
        double x = this.pageBorderWidth;
        double y = interval;
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.toInchesRounded72th(x), this.toInchesRounded72th(y + (heightConnection/4)), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.toInchesRounded72th(0), this.toInchesRounded72th(y), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y + (3*heightConnection/4)), this.toInchesRounded72th(x), this.toInchesRounded72th(y + heightConnection), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y + heightConnection), this.toInchesRounded72th(0), this.toInchesRounded72th(y + heightConnection), this.paintRed);
        y += interval + heightConnection;
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.toInchesRounded72th(x), this.toInchesRounded72th(y + heightConnection), this.paintRed);
        y += interval + heightConnection;
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.toInchesRounded72th(x), this.toInchesRounded72th(y + (heightConnection/4)), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.toInchesRounded72th(0), this.toInchesRounded72th(y), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y + (3*heightConnection/4)), this.toInchesRounded72th(x), this.toInchesRounded72th(y + heightConnection), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y + heightConnection), this.toInchesRounded72th(0), this.toInchesRounded72th(y + heightConnection), this.paintRed);
    }

    public void paintBottomConnect(Canvas canvas)
    {
        //canvas.drawLine(0,0,50,50,this.paintRed);
        Log.e("Create Field", "Creating Bottom connection");
        int numConnections = 3;
        numConnections = 2*numConnections +1;

        double widthConnection = 8;
        double interval = (this.toCm(this.document.getPageWidth()/72) - (3 * 8))/4;
        //double border = 0.5 * (this.toCm(this.document.getPageHeight()/72) - this.pageHeight);
        interval = this.toCm(this.document.getPageWidth()/72) / numConnections;
        widthConnection = interval;
        double x = interval;
        double y = this.pageHeight + this.pageBorderHeight;
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.toInchesRounded72th(x + widthConnection), this.toInchesRounded72th(y), this.paintRed);
        x += interval + widthConnection;
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.toInchesRounded72th(x + (widthConnection/4)), this.toInchesRounded72th(y), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.toInchesRounded72th(x), this.toInchesRounded72th(y + this.pageBorderHeight), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x + (3*widthConnection/4)), this.toInchesRounded72th(y), this.toInchesRounded72th(x + widthConnection), this.toInchesRounded72th(y), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x + widthConnection), this.toInchesRounded72th(y), this.toInchesRounded72th(x + widthConnection), this.toInchesRounded72th(y + this.pageBorderHeight), this.paintRed);
        x += interval + widthConnection;
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.toInchesRounded72th(x + widthConnection), this.toInchesRounded72th(y), this.paintRed);
    }

    public void paintTopConnect(Canvas canvas)
    {
        Log.e("Create Field", "Creating Top connection");
        //canvas.drawLine(0,0,50,0,this.paintRed);
        int numConnections = 3;
        numConnections = 2*numConnections +1;

        double widthConnection = 8;
        double interval = (this.toCm(this.document.getPageWidth()/72) - (3 * 8))/4;
        //double border = 0.5 * (this.toCm(this.document.getPageHeight()/72) - this.pageHeight);
        interval = this.toCm(this.document.getPageWidth()/72) / numConnections;
        widthConnection = interval;
        double x = interval;
        double y = this.pageBorderHeight;
        //Log.i("Create Field", "x="+x+"; y="+y+"; num="+numConnections+"; intv= "+interval);
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.toInchesRounded72th(x + (widthConnection/4)), this.toInchesRounded72th(y), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(0), this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x + (3*widthConnection/4)), this.toInchesRounded72th(y), this.toInchesRounded72th(x + widthConnection), this.toInchesRounded72th(y), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x + widthConnection), this.toInchesRounded72th(0), this.toInchesRounded72th(x + widthConnection), this.toInchesRounded72th(y), this.paintRed);
        x += interval + widthConnection;
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.toInchesRounded72th(x + widthConnection), this.toInchesRounded72th(y), this.paintRed);
        x += interval + widthConnection;
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.toInchesRounded72th(x + (widthConnection/4)), this.toInchesRounded72th(y), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x), this.toInchesRounded72th(0), this.toInchesRounded72th(x), this.toInchesRounded72th(y), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x + (3*widthConnection/4)), this.toInchesRounded72th(y), this.toInchesRounded72th(x + widthConnection), this.toInchesRounded72th(y), this.paintRed);
        canvas.drawLine(this.toInchesRounded72th(x + widthConnection), this.toInchesRounded72th(0), this.toInchesRounded72th(x + widthConnection), this.toInchesRounded72th(y), this.paintRed);
    }

    public void paintFieldOnPages()
    {
        Log.i("Create Field", "ch="+this.PagesX * this.pageWidth+"; chk="+this.DimX * this.DimRect);
        this.pageBorderWidth = this.toCm(this.document.getPageWidth()/72) - this.pageWidth;
        this.pageBorderHeight = this.toCm(this.document.getPageHeight()/72) - this.pageHeight;
        double xBorder = ((this.PagesX * this.pageWidth) - (this.DimX * this.DimRect)) / 2;
        double yBorder = ((this.PagesY * this.pageHeight) - (this.DimY * this.DimRect)) / 2;
        Log.i("Create Field", "xBorder="+xBorder+"; yBorder="+yBorder);
        double x, y = yBorder, yBegin;
        for(int i = 0; i < this.PagesY; i++)
        {
            double yMax = this.pageHeight;
            if(i == this.PagesY - 1)
            {
                yMax = yMax - yBorder;
            }
            x = xBorder;
            yBegin = y;
            for(int j = 0; j < this.PagesX; j++)
            {
//                Page page = this.pages[i*this.PagesX + j + 1];

                int pageNum = i * this.PagesX + j;
                Log.e("Create Field", "PageNummer="+pageNum);
                PdfDocument.Page page = this.document.startPage(pageNum);
                Canvas canvas = page.getCanvas();
                double xMax = this.pageWidth;
                if(j == this.PagesX - 1)
                {
                    xMax = xMax - xBorder;
                }
                while(x < xMax)
                {
                    y = yBegin;
                    //Log.e("Create Field", "x="+x+"; y="+y);
                    while(y < yMax)
                    {
                        fieldRect(canvas, this.toInchesRounded72th(x + this.pageBorderWidth), this.toInchesRounded72th(y + this.pageBorderHeight));
                        y += this.DimRect;
                        //Log.i("Create Field", "y="+y);
                    }
                    x += this.DimRect;
                }
                x -= this.pageWidth;

                if(i != 0)
                {
                    this.paintTopConnect(canvas);
                }
                if(i != (this.PagesY - 1))
                {
                    this.paintBottomConnect(canvas);
                }
                if(j != 0)
                {
                    this.paintLeftConnect(canvas);
                }
                if(j != (this.PagesX - 1))
                {
                    this.paintRightConnect(canvas);
                }

                this.document.finishPage(page);
            }
            y -= this.pageHeight;
        }
    }

    public double toInches(double cm)
    {
        return (cm/2.54);
    }

    public double toCm(double inches)
    {
        return (inches*2.54);
    }

    public int toInchesRounded(double cm)
    {
        return (int) (this.toInches(cm));
    }

    public int toInchesRounded72th(double cm)
    {
        return (int) (this.toInches(cm)*72);
    }

    public int toCmRounded(double inches)
    {
        return (int) (this.toCm(inches));
    }
}
