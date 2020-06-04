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
public class BeanMarcacion implements Serializable {

  private String codPers = "";
  private String aPaterno = "";
  private String aMaterno = "";
  private String nombres = "";
  private String fecha = "";
  private String reloj = "";
  private int numMarcas = 0;
  private String sede = "";
  private String hora = "";
  private String descReloj = "";

  public BeanMarcacion() {
  }
  public String getCodPers() {
    return codPers;
  }
  public void setCodPers(String codPers) {
    this.codPers = codPers;
  }
  public void setAPaterno(String aPaterno) {
    this.aPaterno = aPaterno;
  }
  public String getAPaterno() {
    return aPaterno;
  }
  public void setAMaterno(String aMaterno) {
    this.aMaterno = aMaterno;
  }
  public String getAMaterno() {
    return aMaterno;
  }
  public void setNombres(String nombres) {
    this.nombres = nombres;
  }
  public String getNombres() {
    return nombres;
  }
  public void setFecha(String fecha) {
    this.fecha = fecha;
  }
  public String getFecha() {
    return fecha;
  }
  public void setReloj(String reloj) {
    this.reloj = reloj;
  }
  public String getReloj() {
    return reloj;
  }
  public void setNumMarcas(int numMarcas) {
    this.numMarcas = numMarcas;
  }
  public int getNumMarcas() {
    return numMarcas;
  }
  public void setSede(String sede) {
    this.sede = sede;
  }
  public String getSede() {
    return sede;
  }
  public void setHora(String hora) {
    this.hora = hora;
  }
  public String getHora() {
    return hora;
  }
  public void setDescReloj(String descReloj) {
    this.descReloj = descReloj;
  }
  public String getDescReloj() {
    return descReloj;
  }



}