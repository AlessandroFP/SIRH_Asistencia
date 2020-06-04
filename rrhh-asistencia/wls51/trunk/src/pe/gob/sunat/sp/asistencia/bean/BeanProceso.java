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

public class BeanProceso implements Serializable {

  private String codigo = "";
  private String fecha = "";
  private String horaIni = "";
  private String horaFin = "";
  private String movimiento = "";
  private String autorId = "";
  private String estado = "";
  private int trabIni;
  private int trabFin;
  private String periodo = "";
  private String fechaIni = "";
  private String fechaFin = "";
  private String criterio = "";
  private String valor = "";
  private String usuario = "";

  public BeanProceso() {
  }
  public String getCodigo() {
    return codigo;
  }
  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }
  public void setFecha(String fecha) {
    this.fecha = fecha;
  }
  public String getFecha() {
    return fecha;
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
  public void setMovimiento(String movimiento) {
    this.movimiento = movimiento;
  }
  public String getMovimiento() {
    return movimiento;
  }
  public void setAutorId(String autorId) {
    this.autorId = autorId;
  }
  public String getAutorId() {
    return autorId;
  }
  public void setEstado(String estado) {
    this.estado = estado;
  }
  public String getEstado() {
    return estado;
  }
  public void setTrabIni(int trabIni) {
    this.trabIni = trabIni;
  }
  public int getTrabIni() {
    return trabIni;
  }
  public void setTrabFin(int trabFin) {
    this.trabFin = trabFin;
  }
  public int getTrabFin() {
    return trabFin;
  }
  public void setPeriodo(String periodo) {
    this.periodo = periodo;
  }
  public String getPeriodo() {
    return periodo;
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
  public void setCriterio(String criterio) {
    this.criterio = criterio;
  }
  public String getCriterio() {
    return criterio;
  }
  public void setValor(String valor) {
    this.valor = valor;
  }
  public String getValor() {
    return valor;
  }
  public String getUsuario() {
    return usuario;
  }
  public void setUsuario(String usuario) {
    this.usuario = usuario;
  }
}