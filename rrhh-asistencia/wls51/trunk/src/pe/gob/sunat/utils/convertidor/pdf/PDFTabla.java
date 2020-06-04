package pe.gob.sunat.utils.convertidor.pdf;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sunat</p> 
 * @author cgarratt
 * @version 1.0
 * @deprecated by pe.gob.sunat.framework.util.io.pdf.PDFTabla
 */

public class PDFTabla extends Table {

    public Cell celda;
        
    public PDFTabla(int columnas, int filas) throws BadElementException{
        super(columnas, filas);
    }
    public PDFTabla(int filas) throws BadElementException{
        super(filas);
    }
    
    public void setCelda(){
        this.celda = new Cell();
    }    
    public void setCelda(String s){
        this.celda = new Cell(s);
    }
    public void setCelda(String s, Font f) throws BadElementException{
        this.celda = new Cell(new Phrase(s,f));
    }
    public void agregarCelda(String s, Font f) throws BadElementException{
        this.addCell(new Phrase(s,f));
    }
    public void setCelda(Image i)
    	throws BadElementException{
        this.celda = new Cell(i);
    }
    public void setCelda(Paragraph p)
    	throws BadElementException{
        this.celda = new Cell(p);
    }
    public void setCelda(Table t)
		throws BadElementException{
        this.celda = new Cell(t);
    }
    public Cell getCelda(){
        return this.celda;
    }
}