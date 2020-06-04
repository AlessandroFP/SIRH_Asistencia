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

public class BeanHoraExtra implements Serializable {

  private String codPers = "";
  private String codJefe = "";
  private String horaIni = "";
  private String horaFin = "";
  private String observacion = "";
  private String codUO = "";
  private String horaSalida = "";
  private String estado = "";
  private String trabajador = "";
  private String fechaAutorizacion = "";
  private String fechaEfectiva = "";
  private String jefe = "";
  private float acumulado = 0;
  private String codCate = "";
  private String desCate = "";
  private String descAcumulado = "";
  private String fechaCompensacion = "";
  private String descUO = "";

  public BeanHoraExtra() {
  }
  public String getCodPers() {
    return codPers;
  }
  public void setCodPers(String codPers) {
    this.codPers = codPers;
  }
  public void setCodJefe(String codJefe) {
    this.codJefe = codJefe;
  }
  public String getCodJefe() {
    return codJefe;
  }
  public void setFechaAutorizacion(String fechaAutorizacion) {
    this.fechaAutorizacion = fechaAutorizacion;
  }
  public String getFechaAutorizacion() {
    return fechaAutorizacion;
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
  public void setObservacion(String observacion) {
    this.observacion = observacion;
  }
  public String getObservacion() {
    return observacion;
  }
  public void setCodUO(String codUO) {
    this.codUO = codUO;
  }
  public String getCodUO() {
    return codUO;
  }
  public void setFechaEfectiva(String fechaEfectiva) {
    this.fechaEfectiva = fechaEfectiva;
  }
  public String getFechaEfectiva() {
    return fechaEfectiva;
  }
  public void setHoraSalida(String horaSalida) {
    this.horaSalida = horaSalida;
  }
  public String getHoraSalida() {
    return horaSalida;
  }
  public void setEstado(String estado) {
    this.estado = estado;
  }
  public String getEstado() {
    return estado;
  }
  public void setTrabajador(String trabajador) {
    this.trabajador = trabajador;
  }
  public String getTrabajador() {
    return trabajador;
  }
  public void setJefe(String jefe) {
    this.jefe = jefe;
  }
  public String getJefe() {
    return jefe;
  }
  public void setAcumulado(float acumulado) {
    this.acumulado = acumulado;
  }
  public float getAcumulado() {
    return acumulado;
  }
  public void setCodCate(String codCate) {
    this.codCate = codCate;
  }
  public String getCodCate() {
    return codCate;
  }
  public void setDesCate(String desCate) {
    this.desCate = desCate;
  }
  public String getDesCate() {
    return desCate;
  }
  public void setDescAcumulado(String descAcumulado) {
    this.descAcumulado = descAcumulado;
  }
  public String getDescAcumulado() {
    return descAcumulado;
  }
  public void setFechaCompensacion(String fechaCompensacion) {
    this.fechaCompensacion = fechaCompensacion;
  }
  public String getFechaCompensacion() {
    return fechaCompensacion;
  }
  public void setDescUO(String descUO) {
    this.descUO = descUO;
  }
  public String getDescUO() {
    return descUO;
  }
}