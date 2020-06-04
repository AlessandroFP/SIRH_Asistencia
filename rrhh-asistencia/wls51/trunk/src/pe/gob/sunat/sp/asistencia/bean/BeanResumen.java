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

public class BeanResumen  implements Serializable{

  private String codPers = "";
  private String periodo = "";
  private String mov = "";
  private String trabajador = "";
  private String undOrg = "";
  private String descMovimiento = "";
  private int total = 0;
  private String medida = "";
  public BeanResumen() {
  }
  public String getCodPers() {
    return codPers;
  }
  public void setCodPers(String codPers) {
    this.codPers = codPers;
  }
  public String getPeriodo() {
    return periodo;
  }
  public void setPeriodo(String periodo) {
    this.periodo = periodo;
  }
  public String getMov() {
    return mov;
  }
  public void setMov(String mov) {
    this.mov = mov;
  }
  public String getTrabajador() {
    return trabajador;
  }
  public void setTrabajador(String trabajador) {
    this.trabajador = trabajador;
  }
  public String getUndOrg() {
    return undOrg;
  }
  public void setUndOrg(String undOrg) {
    this.undOrg = undOrg;
  }
  public String getDescMovimiento() {
    return descMovimiento;
  }
  public void setDescMovimiento(String descMovimiento) {
    this.descMovimiento = descMovimiento;
  }
  public int getTotal() {
    return total;
  }
  public void setTotal(int total) {
    this.total = total;
  }
  public String getMedida() {
    return medida;
  }
  public void setMedida(String medida) {
    this.medida = medida;
  }
}