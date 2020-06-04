package pe.gob.sunat.sp.bean;

import java.io.Serializable;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class BeanT01 implements Serializable {
  
  private String numero;
  private String tipo;
  private String argumento;
  private String descLarga;
  private String descCorta;
  
  public String getNumero() {
    return numero;
  }
  public void setNumero(String numero) {
    this.numero = numero;
  }
  public String getTipo() {
    return tipo;
  }
  public void setTipo(String tipo) {
    this.tipo = tipo;
  }
  public String getArgumento() {
    return argumento;
  }
  public void setArgumento(String argumento) {
    this.argumento = argumento;
  }
  public String getDescLarga() {
    return descLarga;
  }
  public void setDescLarga(String descLarga) {
    this.descLarga = descLarga;
  }
  public String getDescCorta() {
    return descCorta;
  }
  public void setDescCorta(String descCorta) {
    this.descCorta = descCorta;
  }

}