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

public class BeanPeriodo implements Serializable {

  private String periodo = "";
  private String fechaIni = "";
  private String fechaFin = "";
  private String fechaCie = "";
  private String estId = "";
  private String fechaIniCAS = "";
  private String fechaFinCAS = "";
  private String fechaCieCAS = "";
  private String fechaIniModForm = "";
  private String fechaFinModForm = "";
  private String fechaCieModForm = "";


  public BeanPeriodo() {
  }
  public String getPeriodo() {
    return periodo;
  }
  public void setPeriodo(String periodo) {
    this.periodo = periodo;
  }
  public String getFechaIni() {
    return fechaIni;
  }
  public void setFechaIni(String fechaIni) {
    this.fechaIni = fechaIni;
  }
  public String getFechaFin() {
    return fechaFin;
  }
  public void setFechaFin(String fechaFin) {
    this.fechaFin = fechaFin;
  }
  public String getFechaCie() {
    return fechaCie;
  }
  public void setFechaCie(String fechaCie) {
    this.fechaCie = fechaCie;
  }
  public String getEstId() {
    return estId;
  }
  public void setEstId(String estId) {
    this.estId = estId;
  }
  
  public String getFechaIniCAS() {
	  return fechaIniCAS;
  }
  public void setFechaIniCAS(String fechaIniCAS) {
	  this.fechaIniCAS = fechaIniCAS;
  }
  public String getFechaFinCAS() {
	  return fechaFinCAS;
  }
  public void setFechaFinCAS(String fechaFinCAS) {
	  this.fechaFinCAS = fechaFinCAS;
  }
  public String getFechaCieCAS() {
	  return fechaCieCAS;
  }
  public void setFechaCieCAS(String fechaCieCAS) {
	  this.fechaCieCAS = fechaCieCAS;
  }
  
  
  public String getFechaIniModForm() {
	return fechaIniModForm;
  }
  public void setFechaIniModForm(String fechaIniModForm) {
	this.fechaIniModForm = fechaIniModForm;
  }
  public String getFechaFinModForm() {
	return fechaFinModForm;
  }
  public void setFechaFinModForm(String fechaFinModForm) {
	this.fechaFinModForm = fechaFinModForm;
  }
  public String getFechaCieModForm() {
	return fechaCieModForm;
  }
  public void setFechaCieModForm(String fechaCieModForm) {
	this.fechaCieModForm = fechaCieModForm;
  }
  
}