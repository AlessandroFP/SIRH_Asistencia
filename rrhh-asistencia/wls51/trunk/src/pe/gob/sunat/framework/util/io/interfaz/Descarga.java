/*
 * Created on 13/12/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pe.gob.sunat.framework.util.io.interfaz;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public interface Descarga {

  public Map descargar(DataSource datasource, int id) throws IOException;
  public Map descargar(DataSource datasource, int id, HttpServletResponse response) throws IOException;
  public int cargar(File f, int ndias, boolean keepfile, int asociado) throws IOException;
  public int cargar(FileInputStream f, String filename, int ndias, boolean keepfile, int asociado) throws IOException;

  public void modificar(int id, File f, boolean keepfile) throws IOException;
  public void modificar(int id, FileInputStream f, String filename, boolean keepfile) throws IOException;

  public void eliminar(int id) throws IOException;
  
  public boolean isValidID(int id) throws IOException;
  public void setPrintFile(boolean printFile);
}
