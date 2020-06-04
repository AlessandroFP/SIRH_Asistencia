package pe.gob.sunat.utils.convertidor.pdf;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sunat</p> 
 * @author cgarratt
 * @version 1.0
 * @deprecated by pe.gob.sunat.framework.util.io.pdf.PDFDocumento
 */

public class PDFDocumento extends Document {

    public Font fuente;
    public Paragraph parrafo;
    public Image imagen;
    public Chunk chunk;
    public Phrase frase;
    public HeaderFooter cabecera;
    public HeaderFooter pie;
    
    private PdfWriter writer;
    private ArrayList tablas;    
    
    public static Rectangle A4 = PageSize.A4;
    public static Rectangle CARTA = PageSize.LETTER;    
    
    public static int HELVETICA = Font.HELVETICA;
    public static int TIMES = Font.TIMES_ROMAN;
    public static int COURIER = Font.COURIER;
    
    public static int BOLD = Font.BOLD;   
    public static int NORMAL = Font.NORMAL;
    public static int ITALIC = Font.ITALIC;
    public static int UNDERLINE = Font.UNDERLINE;
    
    public static Color WHITE = new Color(255,255,255);
    public static Color BLACK = new Color(0,0,0);
    public static Color GRAY = new Color(192,192,192);
    public static Color LIGHT_GRAY = new Color(165,165,165);
    
    public static int IMPRIMR = PdfWriter.AllowPrinting;
    public static int COPIAR = PdfWriter.AllowCopy;
    public static int MODIFICAR = PdfWriter.AllowModifyContents;
    public static int LEER = PdfWriter.AllowScreenReaders;       
    
    public PDFDocumento() {
        super();
        this.tablas = new ArrayList();
    }    
    public PDFDocumento(Rectangle r) {
        super(r);
        this.tablas = new ArrayList();
    }
    public void abrir() {
        this.open();
    }
    public void cerrar() {
        this.close();
    }
    public void agregar(Element e) throws DocumentException{
        this.add(e);        
    }
    public void setMetadata(HashMap meta) throws DocumentException{
        this.addTitle((String)meta.get("title"));
        this.addSubject((String) meta.get("subject"));
        this.addKeywords((String) meta.get("keywords"));
        this.addCreator((String) meta.get("creator"));
        this.addAuthor((String) meta.get("author"));
        this.addHeader("Expires", (String) meta.get("expires"));
    }
    
    public void setFuente(int tipo, int tamano, int estilo){
        this.fuente = new Font(tipo, tamano, estilo);
    }    
    public void setFuente(int tipo, int tamano, int estilo, Color color){
        this.fuente = new Font(tipo, tamano, estilo, color);
    }
    public Font getFuente(){
        return this.fuente;
    }
    public void setParrafo(String texto){
        this.parrafo = new Paragraph(texto);
    }
    public void setParrafo(String texto, Font fuente){
        this.parrafo = new Paragraph(texto, fuente);
    }    
    public Paragraph getParrafo(){
        return this.parrafo;
    }
    public void setFrase(String texto, Font fuente){
        this.frase = new Phrase(texto, fuente);
    }
    public void setFrase(String texto){
        this.frase = new Phrase(texto);
    }    
    public Phrase getFrase(){
        return this.frase;
    }
    public void setCabecera(Phrase frase, boolean mostrarNum){
        this.cabecera = new HeaderFooter(frase, mostrarNum);
    }    
    public void setCabecera(PDFTabla tabla){
        Paragraph p = new Paragraph("");
        p.add(tabla);
        this.cabecera = new HeaderFooter(p, false);
    }
    public HeaderFooter getCabecera(){
        return this.cabecera;
    }
    public void setChunk(Image img, int x, int y){
        this.chunk = new Chunk(img, x, y);
    }    
    public Chunk getChunk(){
        return this.chunk;
    }
    public void setImagen(String ruta)
    	throws BadElementException, IOException{
        this.imagen = Image.getInstance(ruta);
    }    
    public void setImagen(URL url)
		throws BadElementException, IOException{
        this.imagen = Image.getInstance(url);
    }
    public Image getImagen(){
        return this.imagen;
    }
    public void setWriter(ByteArrayOutputStream baos) throws DocumentException{
        this.writer = PdfWriter.getInstance(this, baos);
    }
    public PdfWriter getWriter(){
        return this.writer;
    }
    public void setTabla(int indice, int columnas)
		throws BadElementException{
        PDFTabla tabla = new PDFTabla(columnas);
        this.tablas.add(indice,tabla);
    }
    public void setTabla(int columnas)
    	throws BadElementException{
        PDFTabla tabla = new PDFTabla(columnas);
        this.tablas.add(0,tabla);
    }
    public PDFTabla getTabla(int indice){
        return (PDFTabla)this.tablas.get(indice);
    }
    public PDFTabla getTabla(){
        return (PDFTabla)this.tablas.get(0);
    }
}