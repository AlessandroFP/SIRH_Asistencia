package pe.gob.sunat.personalizer.mail.bean;
import java.util.StringTokenizer;
/**
 * Title:        Boletas de Pago
 * Description:  Mantenimiento de Boletas de Pago pendientes de Recojo.
 * Copyright:    Copyright (c) 2001
 * Company:      SUNAT
 * @author Johnny A. Valdez A.
 * @version 1.0
 */

public class BeanCuentaCorreo {

  public BeanCuentaCorreo() {
  }
  private String cuenta;
  private String servidor;
  public void setDireccion(String direccion){
    StringTokenizer st = new StringTokenizer(direccion, "@");
    cuenta = (String)st.nextElement();
    servidor = (String)st.nextElement();
  }
  public String getCuenta() {
    return cuenta;
  }
  public void setCuenta(String newCuenta) {
    cuenta = newCuenta;
  }
  public void setServidor(String newServidor) {
    servidor = newServidor;
  }
  public String getServidor() {
    return servidor;
  }
  public String getDireccion() {
    return cuenta + "@" + servidor;
  }
}