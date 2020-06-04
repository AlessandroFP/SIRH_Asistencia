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

public class BeanDevolucion implements Serializable{

  private String codPers = "";
  private String codUO = "";
  private String periodo = "";
  private String mov = "";
  private String descMovimiento = "";
  private String periodoReg = "";
  private String observ = "";
  private int total = 0;
  public BeanDevolucion() {
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
  public String getDescMovimiento() {
    return descMovimiento;
  }
  public void setDescMovimiento(String descMovimiento) {
    this.descMovimiento = descMovimiento;
  }
  public String getPeriodoReg() {
    return periodoReg;
  }
  public void setPeriodoReg(String periodoReg) {
    this.periodoReg = periodoReg;
  }
  public String getObserv() {
    return observ;
  }
  public void setObserv(String observ) {
    this.observ = observ;
  }
  public int getTotal() {
    return total;
  }
  public void setTotal(int total) {
    this.total = total;
  }
public String getCodUO() {
	return codUO;
}
public void setCodUO(String codUO) {
	this.codUO = codUO;
}
}