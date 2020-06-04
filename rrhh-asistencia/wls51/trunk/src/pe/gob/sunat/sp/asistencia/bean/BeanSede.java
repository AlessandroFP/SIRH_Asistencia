package pe.gob.sunat.sp.asistencia.bean;

import java.io.Serializable;

/**
 * <p>Title: Control de Asistencia</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sunat</p>
 * @author cgarratt
 * @version 1.0
 */

public class BeanSede implements Serializable{

  private String codLocal;
  private String nomLocal;
  public BeanSede() {
  }
  public String getCodLocal() {
    return codLocal;
  }
  public void setCodLocal(String codLocal) {
    this.codLocal = codLocal;
  }
  public String getNomLocal() {
    return nomLocal;
  }
  public void setNomLocal(String nomLocal) {
    this.nomLocal = nomLocal;
  }
}