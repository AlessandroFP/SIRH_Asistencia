/*
 * Created on 17-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pe.gob.sunat.framework.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.framework.util.io.dao.T3290DAO;
import pe.gob.sunat.framework.util.lang.Cadena;
import pe.gob.sunat.framework.util.dao.SequenceDAO;

/**
 * Clase que realizar operaciones sobre archivos
 * 
 * @author JVALDEZ
 * @since framework 1.1
 *
 */
public class Archivo {
  private Log log = LogFactory.getLog(getClass());
  public static char DELIMITADOR_PIPE = '|';
  /**
   * Nombre de la secuencia a utilizar para la carga de archivos a base de datos.
   */
  public static String SEQNAME = "SEBLOBDATA0";
  private static final int BUFFER_SIZE = 4096;
  /**
   * 
   */
  public Archivo() {
    super();
  }
  /**
   * 
   * Lee un InputStream que sigue el patron de lineas de texto con delimitadores,
   * y lo convierte a una lista, donde cada elemento de la lista contiene un
   * arreglo de cadenas, este arreglo de cadenas es generado 
   * separando la linea por el delimitador.
   * 
   * @param is InputStream
   * @param delimiter String
   * @return List
   * @throws IOException
   */
  public List parseDelimitedFile(InputStream is, char delimiter) throws IOException {
    List res = new ArrayList();
    
    BufferedReader br = new BufferedReader( new InputStreamReader(is) );
    
    String linea = null;
    while ( (linea=br.readLine())!=null){
      res.add(Cadena.split(linea, delimiter) );
    }
    
    return res;
  }

  /**
   * Lee un archivo que sigue el patron de lineas de texto con delimitadores,
   * y lo convierte a una lista, donde cada elemento de la lista contiene un
   * arreglo de cadenas, este arreglo de cadenas es generado 
   * separando la linea por el delimitador.
   * 
   * @param filename String
   * @param delimiter String
   * @return List
   * @throws FileNotFoundException
   * @throws IOException
   */
  public List parseDelimitedFile(String filename, char delimiter) throws FileNotFoundException, IOException{
    File fi = new File(filename);
    
    FileInputStream fis = new FileInputStream(fi);
    try {
      return parseDelimitedFile( fis, delimiter);
    } finally {
      if (fis!=null)
        fis.close();
    }
  }
  
  /**
   * Permite descargar un archivo cualquiera, enviando los datos al response.
   * 
   * @param datos Object, puede ser un InputStream, byte[], File o ByteArrayOutputStream 
   * @param filename String, nombre del archivo con que se vera al descargar
   * @param response HttpServletResponse
   * @throws IOException
   * @since util-1.3
   * @author JVALDEZ
   */
  public void descargar(Object datos, String filename, HttpServletResponse response) throws IOException {
    descargar(datos, filename, response, true);
  }
  /**
   * Permite descargar un archivo cualquiera, enviando los datos al response.
   * 
   * @param datos Object, puede ser un InputStream, byte[], File o ByteArrayOutputStream 
   * @param filename String, nombre del archivo con que se vera al descargar
   * @param response HttpServletResponse
   * @param attachment boolean si es true permite al navegador mostrar una pantalla de que desea hacer con el
   *  archivo que se esta descargando, si es false muestra el archivo directamente dentro del navegador.
   * @throws IOException
   * @since util-1.6
   * @author JVALDEZ
   */
  public void descargar(Object datos, String filename, HttpServletResponse response, boolean attachment) throws IOException{
    OutputStream out = null;
    try {
      out = new BufferedOutputStream ( response.getOutputStream() );
      FileNameMap fnm = URLConnection.getFileNameMap();
      response.setContentType( fnm.getContentTypeFor(filename));
      
      if (attachment)
        response.setHeader("Content-disposition", "attachment; filename=\"" + filename + '\"');
      
      log.debug(datos.getClass().getName());
      
      if (datos instanceof InputStream){
        log.debug("instanceof InputStream");

        int b = 0;
        while ((b = ((InputStream)datos).read()) != -1) {
          out.write(b);
        }
        ((InputStream)datos).reset();
      } else if (datos instanceof byte[]){
        log.debug("instanceof byte[]");
        out.write((byte[])datos);
      } else if (datos instanceof ByteArrayOutputStream){
        log.debug("instanceof ByteArrayOutputStream");
        ((ByteArrayOutputStream)datos).writeTo(out);
        ((ByteArrayOutputStream)datos).reset();
      } else if (datos instanceof File){
        log.debug("instanceof File");
        // FileInputStream stream = null;
        byte[] data = new byte[ BUFFER_SIZE ];
        BufferedInputStream stream = null;
        try {
          stream = new BufferedInputStream( new FileInputStream( ((File)datos) ) );
          int count = 0;
          int read = 0;
          do {
            read = stream.read(data, 0, BUFFER_SIZE );
            if (read != -1) {
              out.write(data, 0, read);
              count += read;
            } else if (((File)datos).length()==0)
              out.write(data, 0, 0);
          } while (count < ((File)datos).length());
          
        } finally {
          stream.close();
          log.debug("finally stream.close");
        }
      }
    } finally {
      out.flush();
      out.close();
    }
    
  }
  /**
   * 
   * Para utilizar este metodo, el datasource tiene que apuntar al pool de la base de 
   * secuencias y donde se encuentre la tabla t3290data0.
   * 
   * @param datasource_seq DataSource base de datos de secuencias.
   * @param datasource_file DataSource base de datos de la tabla donde se guarda el archivo.
   * @param f File Referencia al archivo a cargar.
   * @param ndias int numero de dias que se aumentaran al dia actual para generar la fecha de vencimiento
   * @param keepfile boolean permite conservar el archivo que se ha cargado a la base de datos
   * 
   * @return int Numero de secuencia generado 
   * @throws Exception
   * @since util 1.6 build12 
   */
  public static int cargar(DataSource datasource_seq, DataSource datasource_file, File f, int ndias, boolean keepfile) throws IOException{
    int id = 0;
    
    if ( f != null){
      
      if (!f.exists())
        throw new IOException ("El archivo indicado no existe");
      if (!f.isFile())
        throw new IOException ("El archivo indicado es un directorio");
      if (!f.canRead())
        throw new IOException ("No se tiene permiso de lectura sobre el archivo cargado");
      
      FechaBean hoy = new FechaBean();
      FechaBean venc = new FechaBean();
      
      venc.getCalendar().add(Calendar.DATE, ndias);
      
      SequenceDAO secuencia = new SequenceDAO();
      
      id = secuencia.getSequence(datasource_seq, SEQNAME);
      
      T3290DAO data0 = new T3290DAO(datasource_file);
      
      Map datos = new HashMap();
      InputStream stream = null;
      try {
        
        stream = new FileInputStream( f );
        
        datos.put("num_id", new Integer(id));
        datos.put("fec_creacion", hoy.getSQLDate());
        datos.put("fec_vencimiento", venc.getTimestamp());
        datos.put("arc_datos", stream);
        datos.put("cnt_tamanho", new Integer(stream.available()));
        datos.put("des_nombre", f.getName());
        
        data0.insert(datos);
        
        datos = null;
        
      } catch (FileNotFoundException e) {
        throw new IOException (e.getMessage());
      } finally {
        stream.close();
        if (!keepfile && f.canWrite())
          f.delete();
      }
    } else {
      throw new IOException ("Error de parametros, no se puede enviar una referencia nula");
    }
    
    return id;
  }

  /**
   * Para utilizar este metodo, el datasource tiene que apuntar al pool de la base de  
   * datos donde se encuentre la tabla t3290data0, ademas se tiene que tener configurado
   * un directorio /data0/tempo ( o el indicado en el archivo download.properties del core), ya
   * que este sera utilizado como directorio temporal.
   * 
   * @param datasource DataSource base de datos de secuencias y de la tabla donde se guarda el archivo.
   * @param id int  Numero de secuencia generado
   * @param response HttpServletResponse
   * 
   * @return Map Mapa con la info del archivo o null si no existe o ya no es vigente.
   * @throws IOException
   * @since util 1.6 build12
   * @deprecated se movio esta funcionalidad a la clase ArchivoTemporalBean, siguiendo un nuevo modelo de factoria.
   */
  public Map descargar(DataSource datasource, int id, HttpServletResponse response) throws IOException{
    
    T3290DAO data0 = new T3290DAO(datasource);
    Map datos = data0.findByPK(id);
    
    if (datos != null) {
      try {
        log.debug("descargando archivo... " + datos.get("des_nombre"));
        descargar(datos.get("arc_datos"), (String) datos.get("des_nombre"), response);
      } finally {
        ((File)datos.get("arc_datos")).delete();
      }
    }
    
    return datos;
  }
}
