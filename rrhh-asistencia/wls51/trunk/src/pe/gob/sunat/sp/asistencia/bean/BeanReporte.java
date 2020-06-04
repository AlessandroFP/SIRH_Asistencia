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

public class BeanReporte implements Serializable {

  //estos son los datos necesarios para el quiebre
  private String codigo = "";
  private String codigoJefe = ""; // ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
  private String nombre = "";
  private String categoria = "";
  private String unidad = "";
  private String unidadCorta = ""; // ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
  private String codigoUnidad = ""; // ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
  private String fecha = "";
  private String anno = "";
  private String descripcion = "";
  private String hora = "";
  private String hora1 = "";
  private String hora2 = "";
  private int cantidad = 0;
  private int cantidad2 = 0; // ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
  private java.sql.Timestamp fechaInicio;
  private java.sql.Timestamp fechaFin;

  //este es el detalle del quiebre
  private java.util.ArrayList detalle;
  private java.util.HashMap map;

  public BeanReporte() {
  }
  public String getCodigo() {
    return codigo;
  }
  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
  public String getNombre() {
    return nombre;
  }
  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }
  public String getCategoria() {
    return categoria;
  }
  //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
  public void setUnidadCorta(String unidadCorta) {
	    this.unidadCorta = unidadCorta;
	  }
	  public String getUnidadCorta() {
	    return unidadCorta;
	  }
  public void setCodigoUnidad(String codigoUnidad) {
    this.codigoUnidad = codigoUnidad;
  }
  public String getCodigoUnidad() {
    return codigoUnidad;
  }
  public void setCodigoJefe(String codigoJefe) {
	    this.codigoJefe = codigoJefe;
	  }
	  public String getCodigoJefe() {
	    return codigoJefe;
	  }
  //FIN  
  public void setUnidad(String unidad) {
	this.unidad = unidad;
  }
  public String getUnidad() {
	return unidad;
  } 
	  
  public void setFecha(String fecha) {
    this.fecha = fecha;
  }
  public String getFecha() {
    return fecha;
  }
  public void setDetalle(java.util.ArrayList detalle) {
    this.detalle = detalle;
  }
  public java.util.ArrayList getDetalle() {
    return detalle;
  }
  public void setCantidad(int cantidad) {
    this.cantidad = cantidad;
  }
  public int getCantidad() {
    return cantidad;
  }
  //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
  public void setCantidad2(int cantidad2) {
	this.cantidad2 = cantidad2;
  }
  public int getCantidad2() {
	return cantidad2;
  }
  //FIN
  public void setAnno(String anno) {
    this.anno = anno;
  }
  public String getAnno() {
    return anno;
  }
  public void setFechaInicio(java.sql.Timestamp fechaInicio) {
    this.fechaInicio = fechaInicio;
  }
  public java.sql.Timestamp getFechaInicio() {
    return fechaInicio;
  }
  public void setFechaFin(java.sql.Timestamp fechaFin) {
    this.fechaFin = fechaFin;
  }
  public java.sql.Timestamp getFechaFin() {
    return fechaFin;
  }
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }
  public String getDescripcion() {
    return descripcion;
  }
  public void setHora(String hora) {
    this.hora = hora;
  }
  public String getHora() {
    return hora;
  }
  public void setHora1(String hora1) {
    this.hora1 = hora1;
  }
  public String getHora1() {
    return hora1;
  }
  public void setHora2(String hora2) {
    this.hora2 = hora2;
  }
  public String getHora2() {
    return hora2;
  }
  public java.util.HashMap getMap() {
    return map;
  }
  public void setMap(java.util.HashMap map) {
    this.map = map;
  }
}