package pe.gob.sunat.sp.asistencia.bean;

import java.io.Serializable;

/**
 * <p>
 * Title: Control de Asistencia
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: Sunat
 * </p>
 * 
 * @author cgarratt
 * @version 1.0
 */
public class BeanLicencia implements Serializable {

	private String codPers = "";

	private String trabajador = "";

	private String tipo = "";

	private String descripcion = "";

	private String numero = "";

	private String observacion = "";

	private String licencia = "";

	private String anoRef = "";

	private String numeroRef = "";

	private String areaRef = "";

	private String periodo = "";

	private java.sql.Timestamp fechaFin;

	private java.sql.Timestamp fechaIni;

	private String anno = "";

	private float diasAcumulados = 0;

	private int maxDiasAcum = 0;

	private boolean masiva = true;

	private String usuario = "";

	private String fgraba = "";

	// Datos de la licencia medica
	private String certificado = "";

	private String nroCMP = "";

	private java.sql.Timestamp fechaCita;

	private String tipoEnfermedad = "";
	
	// Dias de Licencia
	
	private String diasLicencia = "";

	public BeanLicencia() {
	}

	public String getCodPers() {
		return codPers;
	}

	public void setCodPers(String codPers) {
		this.codPers = codPers;
	}

	public void setTrabajador(String trabajador) {
		this.trabajador = trabajador;
	}

	public String getTrabajador() {
		return trabajador;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setFechaIni(java.sql.Timestamp fechaIni) {
		this.fechaIni = fechaIni;
	}

	public java.sql.Timestamp getFechaIni() {
		return fechaIni;
	}

	public void setFechaFin(java.sql.Timestamp fechaFin) {
		this.fechaFin = fechaFin;
	}

	public java.sql.Timestamp getFechaFin() {
		return fechaFin;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getNumero() {
		return numero;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setLicencia(String licencia) {
		this.licencia = licencia;
	}

	public String getLicencia() {
		return licencia;
	}

	public void setAnoRef(String anoRef) {
		this.anoRef = anoRef;
	}

	public String getAnoRef() {
		return anoRef;
	}

	public void setNumeroRef(String numeroRef) {
		this.numeroRef = numeroRef;
	}

	public String getNumeroRef() {
		return numeroRef;
	}

	public void setAreaRef(String areaRef) {
		this.areaRef = areaRef;
	}

	public String getAreaRef() {
		return areaRef;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setCertificado(String certificado) {
		this.certificado = certificado;
	}

	public String getCertificado() {
		return certificado;
	}

	public void setNroCMP(String nroCMP) {
		this.nroCMP = nroCMP;
	}

	public String getNroCMP() {
		return nroCMP;
	}

	public void setFechaCita(java.sql.Timestamp fechaCita) {
		this.fechaCita = fechaCita;
	}

	public java.sql.Timestamp getFechaCita() {
		return fechaCita;
	}

	public void setDiasAcumulados(float diasAcumulados) {
		this.diasAcumulados = diasAcumulados;
	}

	public float getDiasAcumulados() {
		return diasAcumulados;
	}

	public void setAnno(String anno) {
		this.anno = anno;
	}

	public String getAnno() {
		return anno;
	}

	public void setTipoEnfermedad(String tipoEnfermedad) {
		this.tipoEnfermedad = tipoEnfermedad;
	}

	public String getTipoEnfermedad() {
		return tipoEnfermedad;
	}

	public int getMaxDiasAcum() {
		return maxDiasAcum;
	}

	public void setMaxDiasAcum(int maxDiasAcum) {
		this.maxDiasAcum = maxDiasAcum;
	}

	public boolean isMasiva() {
		return masiva;
	}

	public void setMasiva(boolean masiva) {
		this.masiva = masiva;
	}

	public String getFgraba() {
		return fgraba;
	}

	public void setFgraba(String fgraba) {
		this.fgraba = fgraba;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
	public void setDiasLicencia(String diasLicencia) {
		this.diasLicencia = diasLicencia;
	}

	public String getDiasLicencia() {
		return diasLicencia;
	}

}