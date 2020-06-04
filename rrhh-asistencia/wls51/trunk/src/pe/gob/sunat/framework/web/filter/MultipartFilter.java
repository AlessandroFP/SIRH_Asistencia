/*
 * Created on Jan 24, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package pe.gob.sunat.framework.web.filter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.util.date.FechaBean;

/**
 * 
 * @author Carlos Enrique Quispe Salazar modifyBy JVALDEZ
 *
 * @web.filter name="FilterTimer"
 * @web.filter-mapping servlet-name="ServletTest" filter-name="Filtro de medida de tiempo"
 * @since web-1.2 (se agrego el logger)
 */
public final class MultipartFilter extends FilterAbstract
{
  private static final int SIZE_THRESHOLD = 0x10000;
  private FilterConfig filterConfiguration = null;
  private DiskFileUpload fileUpload = new DiskFileUpload();
  /**
   * 
   */
  private boolean diskSave = false;
  /**
   * 
   */
  private String posfijo = "";
  /**
   * 
   */
  private String prefijo = "";
  /**
   * 
   */
  private File dirUpload = null;
  private long maxSize = 1024*1024L;

  /**
   * Este metodo permite cargar los objetos que vienen en un request tipo Multipart, y 
   * crear un objeto Map con los archivos en memoria o disco, mas los campos adicionales
   * del request.
   * El objeto creado se llama Multipart, y coexiste con otro objeto llamado IsMultipart
   * de tipo Boolean. 
   * Los archivos en el Multipart se cargan con los siguientes nombres:
   * [campo file], contiene un objeto tipo File o un InputStream, depende de si el archivo
   * esta en disco o memoria, respectivamente.
   * [campo file-filter-filename], contiene el nombre del archivo 
   * [campo file-size], contiene un objeto Long con el tamaño en bytes del archivo cargado.
   * De los otros campos que puedan venir en el request, se debe tener en cuenta la 
   * siguiente particularidad para los campos con el mismo nombre, tales como un checkbox.
   * Si se da el caso de existir multiples campos con el mismo nombre en el request,
   * el programa creará un arreglo tipo String[] luego de encontrar el segundo nombre igual,
   * en caso de nombres unicos siempre se devolvera un String.
   * 
   */
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
  throws IOException, ServletException
  {
    if(FileUploadBase.isMultipartContent((HttpServletRequest)request)) {
      if (log.isDebugEnabled())log.debug("[Filtro]Es multipart");
      MensajeBean mBean = new MensajeBean();
      try {
        
        if(diskSave) {
          if (log.isDebugEnabled()) log.debug("disksave");
          if(!this.dirUpload.exists()) {
            throw new FilterException(this, "La ruta " + this.dirUpload + " no existe");
          }
          
          if (!this.dirUpload.isDirectory()) {
            throw new FilterException(this, "La ruta " + this.dirUpload + " no es un directorio");
          }
          
          File tmpDir = new File(this.dirUpload, "tmp");
          
          try {
            tmpDir.mkdir();
          }
          catch(RuntimeException ex) {
            throw new FilterException(this, "Error al crear el directorio " + tmpDir.getAbsolutePath());
          }
          fileUpload.setRepositoryPath(tmpDir.getAbsolutePath());
        }
        fileUpload.setSizeMax(maxSize);
        fileUpload.setSizeThreshold(SIZE_THRESHOLD);
        
        Map map = new HashMap();
        List fileItems = null;
        
        try {
          fileItems = fileUpload.parseRequest((HttpServletRequest)request);
        }
        catch(FileUploadBase.SizeLimitExceededException ex) {
          throw new FilterException(this, "Se ha excedido en el tamaño maximo permitido para cargar un archivo");
        }
        catch(FileUploadException ex) {
          throw new FilterException(this, "Error en la carga del archivo: " + ex.toString());
        }
        Iterator i = fileItems.iterator();
        while(i.hasNext()) {
          FileItem fi = (FileItem) i.next();
          
          if(fi.isFormField()) {
            if (map.containsKey( fi.getFieldName() )){
              
              if ( map.get( fi.getFieldName() ) instanceof String[]){
                
                String t[] = new String[ ((String[])map.get( fi.getFieldName() )).length + 1 ];
                //
                System.arraycopy( ((String[])map.get( fi.getFieldName() )), 0, t, 0, ((String[])map.get( fi.getFieldName() )).length );
                t[ t.length - 1] = fi.getString(); 
                map.put( fi.getFieldName(), t);
                
              } else {
                map.put( fi.getFieldName(), new String[]{ (String) map.get( fi.getFieldName() ), fi.getString() });
              }
            } else {
              map.put(fi.getFieldName(), fi.getString());
            }
          }
          else {
            String fileName = getFileName(fi);
            Object obj = null;
            
            if(fileName != null) {
              try {
                if(diskSave) {
                  
                  fileName = this.prefijo + fileName + getDataPosfijo();
                  
                  File file = new File(this.dirUpload, fileName);
                  fi.write(file);
                  obj = file;
                }
                else {
                  obj =  fi.getInputStream();
                }
              }
              catch (Exception ex) {
                throw new FilterException(this, "Error al leer el archivo '" + fileName + ": " + ex.toString());
              }
              map.put(fi.getFieldName() + "-filter-filename", fileName);
            }
            
            map.put(fi.getFieldName(), obj);
            map.put(fi.getFieldName()+"-size", new Long(fi.getSize()).toString());
            
          }
        }
        request.setAttribute("isMultipart", new Boolean(true));
        request.setAttribute("multipartObject", map);
        request.setAttribute("isOK", new Boolean(true));
      } catch (FilterException e) {
        if (log.isDebugEnabled())
          log.debug("FilterException..." + mBean.getMensajeerror());
        mBean.setError(true);
        mBean.setMensajeerror(e.getMessage());
        request.setAttribute("isMultipart", new Boolean(true));
        request.setAttribute("isOK", new Boolean(false));
        request.setAttribute("beanErr", mBean);
      }
    }
    else {
      if (log.isDebugEnabled())log.debug("[Filtro]No es multipart");
    }

    if (log.isDebugEnabled()) log.debug("saliendo del filtro multipart");
    chain.doFilter(request, response);
  }
  /**
   * 
   * @param fi FileItem
   * @return String
   */
  private String getFileName(FileItem fi) {
    String clientFileName = fi.getName();
    
    if(clientFileName == null || clientFileName.equals("")) {
      return null;
    }
    
    String fileName = null;
    StringTokenizer st = new StringTokenizer(clientFileName, "\\");
    
    while(st.hasMoreTokens()) {
      fileName = st.nextToken();
    }
    
    st = new StringTokenizer(fileName, "/");
    while(st.hasMoreTokens()) {
      fileName = st.nextToken();
    }
    
    return fileName;
  }
  /**
   * 
   */
  public void init(FilterConfig filterConfig)    
  {
    if(log.isDebugEnabled())log.debug("Inicializo una vez");
    String temp = filterConfig.getInitParameter("dir_upload");
    
    if(temp != null && temp.length()>0) {
      diskSave = true;
      dirUpload = new File(temp);
    }
    
    temp = filterConfig.getInitParameter("prefijo");
    
    if(temp != null && temp.length()>0) {
      this.prefijo = temp;
    }
    
    temp = filterConfig.getInitParameter("max_size");
    
    if(temp != null && temp.length()>0) {
      maxSize = Long.parseLong(filterConfig.getInitParameter("max_size"));
    }
    
    temp = filterConfig.getInitParameter("posfijo");
    
    if(temp != null && temp.length()>0) {
      this.posfijo = temp;
    }
    
    filterConfiguration = filterConfig;
  }
  /**
   * 
   */
  public void destroy()
  {
    filterConfiguration = null;
  }
  
  public void preProcesa(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    // TODO Auto-generated method stub
    
  }
  
  public void postProcesa(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    // TODO Auto-generated method stub
    
  }
  
  public void procesa(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    // TODO Auto-generated method stub
    
  }
  /**
   * 
   * @return String
   */
  private String getDataPosfijo(){
    if ("timestamp".equalsIgnoreCase(posfijo)){
      Random rnd = new Random( Long.parseLong(new FechaBean().getFormatDate("HHmmssSSS")) );
      return "~~" + new FechaBean().getFormatDate("yyyyMMddHHmmssSSS") + rnd.nextLong();
    }
    return posfijo != null && posfijo.length()>0? "-" + posfijo : "";
  }
}


