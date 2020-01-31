package com.fydp.backend.extraction;

import com.fydp.backend.controllers.AppController;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.util.Matrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Extracts images from a pdf previously saved in upload_files directory
 */
public class FigureExtractor extends PDFStreamEngine {

    public FigureExtractor() throws IOException{

    }

    public int imageNumber = 1;

    public static String extract(String fileName) throws IOException{
        PDDocument doc = null;

        //TODO: replace
        fileName = "Weiten - Psychology Themes and Variations 3rd Edition.pdf";

        try
        {
            doc = PDDocument.load(new File(AppController.UPLOAD_PATH + fileName));
            FigureExtractor printer = new FigureExtractor();
            int pageNum = 0;
            for ( PDPage page : doc.getPages() )
            {
                pageNum++;
                if (pageNum != 20) continue;
                System.out.println("Processing page: " + pageNum);
                printer.processPage(page);
            }
        }
        finally
        {
            if (doc != null)
                {
                    doc.close();
                }
        }

        return "todo";
    }

    @Override
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException
    {
        String operation = operator.getName();
        if( "Do".equals(operation) )
        {
            COSName objectName = (COSName) operands.get( 0 );
            PDXObject xobject = getResources().getXObject( objectName );
            if( xobject instanceof PDImageXObject)
            {
                PDImageXObject image = (PDImageXObject)xobject;
                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();

                Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();
                float imageXScale = ctmNew.getScalingFactorX();
                float imageYScale = ctmNew.getScalingFactorY();

                // same image to local
                BufferedImage bImage = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_ARGB);
                bImage = image.getImage();
                ImageIO.write(bImage,"PNG",new File("image_"+imageNumber+".png"));
                System.out.println("Image saved.");
                imageNumber++;

            }
            else if(xobject instanceof PDFormXObject)
            {
                PDFormXObject form = (PDFormXObject)xobject;
                showForm(form);
            }
        }
        else
        {
            super.processOperator( operator, operands);
        }
    }

}
