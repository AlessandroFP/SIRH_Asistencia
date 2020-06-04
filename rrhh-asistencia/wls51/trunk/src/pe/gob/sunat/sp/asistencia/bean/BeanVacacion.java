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

public class BeanVacacion implements Serializable {

  private String codPers = "";
  private String anno = "";
  private int dias = 0;
  private int saldo = 0;
  private int saldoTemp = 0;
  //private String numero = "";
  private String periodo = "";
  private String fechaIng = "";
  private String licencia = "";
  private String annoVac = "";
  private String codUO = "";
  private String observacion = "";
  private String estado = "";
  private java.sql.Timestamp fechaInicio;
  private java.sql.Timestamp fechaFin;
  private String usrCrea = "";
  private String usrMod = "";
  private java.sql.Timestamp fCrea;
  private String annoRef;
  private String areaRef;
  private String numeroRef;
  private String tipo;

  public BeanVacacion() {
  }
  public String getCodPers() {
    return codPers;
  }
  public void setCodPers(String codPers) {
    this.codPers = codPers;
  }
  public void setAnno(String anno) {
    this.anno = anno;
  }
  public String getAnno() {
    return anno;
  }
  public void setDias(int dias) {
    this.dias = dias;
  }
  public int getDias() {
    return dias;
  }
  public void setSaldo(int saldo) {
    this.saldo = saldo;
  }
  public int getSaldo() {
    return saldo;
  }
  public void setSaldoTemp(int saldoTemp) {
    this.saldoTemp = saldoTemp;
  }
  public int getSaldoTemp() {
    return saldoTemp;
  }
  /*public void setNumero(String numero) {
    this.numero = numero;
  }
  public String getNumero() {
    return numero;
  }*/
  public void setPeriodo(String periodo) {
    this.periodo = periodo;
  }
  public String getPeriodo() {
    return periodo;
  }
  public void setFechaIng(String fechaIng) {
    this.fechaIng = fechaIng;
  }
  public String getFechaIng() {
    return fechaIng;
  }
  public void setLicencia(String licencia) {
    this.licencia = licencia;
  }
  public String getLicencia() {
    return licencia;
  }
  public void setFechaInicio(java.sql.Timestamp fechaInicio) {
    this.fechaInicio = fechaInicio;
  }
  public java.sql.Timestamp getFechaInicio() {
    return fechaInicio;
  }
  public void setAnnoVac(String annoVac) {
    this.annoVac = annoVac;
  }
  public String getAnnoVac() {
    return annoVac;
  }
  public void setFechaFin(java.sql.Timestamp fechaFin) {
    this.fechaFin = fechaFin;
  }
  public java.sql.Timestamp getFechaFin() {
    return fechaFin;
  }
  public void setCodUO(String codUO) {
    this.codUO = codUO;
  }
  public String getCodUO() {
    return codUO;
  }
  public void setObservacion(String observacion) {
    this.observacion = observacion;
  }
  public String getObservacion() {
    return observacion;
  }
  public void setEstado(String estado) {
    this.estado = estado;
  }
  public String getEstado() {
    return estado;
  }
  public java.sql.Timestamp getFCrea() {
    return fCrea;
  }
  public void setFCrea(java.sql.Timestamp fCrea) {
    this.fCrea = fCrea;
  }
  public String getUsrCrea() {
    return usrCrea;
  }
  public void setUsrCrea(String usrCrea) {
    this.usrCrea = usrCrea;
  }
  public String getAnnoRef() {
    return annoRef;
  }
  public void setAnnoRef(String annoRef) {
    this.annoRef = annoRef;
  }
  public String getAreaRef() {
    return areaRef;
  }
  public void setAreaRef(String areaRef) {
    this.areaRef = areaRef;
  }
  public String getNumeroRef() {
    return numeroRef;
  }
  public void setNumeroRef(String numeroRef) {
    this.numeroRef = numeroRef;
  }
  public String getTipo() {
    return tipo;
  }
  public void setTipo(String tipo) {
    this.tipo = tipo;
  }
public String getUsrMod() {
	return usrMod;
}
public void setUsrMod(String usrMod) {
	this.usrMod = usrMod;
}
}