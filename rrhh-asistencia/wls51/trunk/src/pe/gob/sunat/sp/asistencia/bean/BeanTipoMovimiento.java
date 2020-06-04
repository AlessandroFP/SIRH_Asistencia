package pe.gob.sunat.sp.asistencia.bean;

import java.io.Serializable;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class BeanTipoMovimiento implements Serializable {

  private String mov = "";
  private String descrip = "";
  private String califica = "";
  private String medida = "";
  private String estId = "";
  private String tipoId = "";
  private java.sql.Timestamp fechaIni;
  private java.sql.Timestamp fechaFin;
  private String descMedida = "";
  private String abrevTipo;
  private float total = 0;
  private String autRH = "";
  private String solicitud = "";
  private String procAsistencia = "";
  private String asisEntrada = "";
  private String asisRefrigerio = "";
  private String asisSalida = "";
  private int diasAntes = 0;
  private int diasDespues = 0;
  private int diasAcum = 0;
  private int qvalida = 0;
  private String obligId = "";
  private String indDias = "0";
  private String indProc = "0";

  public BeanTipoMovimiento() {
  }

  public String getMov() {
    return mov;
  }
  public void setMov(String mov) {
    this.mov = mov;
  }
  public String getDescrip() {
    return descrip;
  }
  public void setDescrip(String descrip) {
    this.descrip = descrip;
  }
  public String getCalifica() {
    return califica;
  }
  public void setCalifica(String califica) {
    this.califica = califica;
  }
  public String getMedida() {
    return medida;
  }
  public void setMedida(String medida) {
    this.medida = medida;
  }
  public String getEstId() {
    return estId;
  }
  public void setEstId(String estId) {
    this.estId = estId;
  }
  public String getTipoId() {
    return tipoId;
  }
  public void setTipoId(String tipoId) {
    this.tipoId = tipoId;
  }
  public java.sql.Timestamp getFechaIni() {
    return fechaIni;
  }
  public void setFechaIni(java.sql.Timestamp fechaIni) {
    this.fechaIni = fechaIni;
  }
  public java.sql.Timestamp getFechaFin() {
    return fechaFin;
  }
  public void setFechaFin(java.sql.Timestamp fechaFin) {
    this.fechaFin = fechaFin;
  }
  public String getDescMedida() {
    return descMedida;
  }
  public void setDescMedida(String descMedida) {
    this.descMedida = descMedida;
  }
  public String getAbrevTipo() {
    return abrevTipo;
  }
  public void setAbrevTipo(String abrevTipo) {
    this.abrevTipo = abrevTipo;
  }
  public void setTotal(float total) {
    this.total = total;
  }
  public float getTotal() {
    return total;
  }
  public String getAutRH() {
    return autRH;
  }
  public void setAutRH(String autRH) {
    this.autRH = autRH;
  }
  public String getSolicitud() {
    return solicitud;
  }
  public void setSolicitud(String solicitud) {
    this.solicitud = solicitud;
  }
  public String getProcAsistencia() {
    return procAsistencia;
  }
  public void setProcAsistencia(String procAsistencia) {
    this.procAsistencia = procAsistencia;
  }
  public String getAsisEntrada() {
    return asisEntrada;
  }
  public void setAsisEntrada(String asisEntrada) {
    this.asisEntrada = asisEntrada;
  }
  public String getAsisRefrigerio() {
    return asisRefrigerio;
  }
  public void setAsisRefrigerio(String asisRefrigerio) {
    this.asisRefrigerio = asisRefrigerio;
  }
  public String getAsisSalida() {
    return asisSalida;
  }
  public void setAsisSalida(String asisSalida) {
    this.asisSalida = asisSalida;
  }
  public int getDiasAntes() {
    return diasAntes;
  }
  public void setDiasAntes(int diasAntes) {
    this.diasAntes = diasAntes;
  }
  public int getDiasDespues() {
    return diasDespues;
  }
  public void setDiasDespues(int diasDespues) {
    this.diasDespues = diasDespues;
  }
  public int getDiasAcum() {
    return diasAcum;
  }
  public void setDiasAcum(int diasAcum) {
    this.diasAcum = diasAcum;
  }
  public int getQValida() {
    return qvalida;
  }
  public void setQValida(int qvalida) {
    this.qvalida = qvalida;
  }  
  public String getObligId() {
    return obligId;
  }
  public void setObligId(String obligId) {
    this.obligId = obligId;
  }
  public String getIndDias() {
    return indDias;
  }
  public void setIndDias(String indDias) {
    this.indDias = indDias;
  }  
  public String getIndProc() {
    return indProc;
  }
  public void setIndProc(String indProc) {
    this.indProc = indProc;
  }  
}