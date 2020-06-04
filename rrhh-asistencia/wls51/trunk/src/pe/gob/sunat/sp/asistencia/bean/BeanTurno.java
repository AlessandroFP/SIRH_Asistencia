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

public class BeanTurno implements Serializable {

  private String codTurno = "";
  private String desTurno = "";
  private String fechaIni = "";
  private String fechaFin = "";
  private String horaIni = "";
  private String horaFin = "";
  private String estId = "";
  private String diasInt = "";
  private String tolera = "";
  private String horaLimite = "";
  private String operId = "";
  private String controlId = "";
  private String refrIni = "";
  private String refrFin = "";
  private String refrMin = "";

  public BeanTurno() {
  }
  public String getCodTurno() {
    return codTurno;
  }
  public void setCodTurno(String codTurno) {
    this.codTurno = codTurno;
  }
  public void setDesTurno(String desTurno) {
    this.desTurno = desTurno;
  }
  public String getDesTurno() {
    return desTurno;
  }
  public void setFechaIni(String fechaIni) {
    this.fechaIni = fechaIni;
  }
  public String getFechaIni() {
    return fechaIni;
  }
  public void setFechaFin(String fechaFin) {
    this.fechaFin = fechaFin;
  }
  public String getFechaFin() {
    return fechaFin;
  }
  public void setHoraIni(String horaIni) {
    this.horaIni = horaIni;
  }
  public String getHoraIni() {
    return horaIni;
  }
  public void setHoraFin(String horaFin) {
    this.horaFin = horaFin;
  }
  public String getHoraFin() {
    return horaFin;
  }
  public void setEstId(String estId) {
    this.estId = estId;
  }
  public String getEstId() {
    return estId;
  }
  public void setDiasInt(String diasInt) {
    this.diasInt = diasInt;
  }
  public String getDiasInt() {
    return diasInt;
  }
  public void setTolera(String tolera) {
    this.tolera = tolera;
  }
  public String getTolera() {
    return tolera;
  }
  public void setHoraLimite(String horaLimite) {
    this.horaLimite = horaLimite;
  }
  public String getHoraLimite() {
    return horaLimite;
  }
  public String getOperId() {
    return operId;
  }
  public void setOperId(String operId) {
    this.operId = operId;
  }
  public String getRefrIni() {
    return refrIni;
  }
  public void setRefrIni(String refrIni) {
    this.refrIni = refrIni;
  }
  public String getRefrFin() {
    return refrFin;
  }
  public void setRefrFin(String refrFin) {
    this.refrFin = refrFin;
  }
  public String getRefrMin() {
    return refrMin;
  }
  public void setRefrMin(String refrMin) {
    this.refrMin = refrMin;
  }
  public String getControlId() {
	return controlId;
  }
  public void setControlId(String controlId) {
	this.controlId = controlId;
  }
}