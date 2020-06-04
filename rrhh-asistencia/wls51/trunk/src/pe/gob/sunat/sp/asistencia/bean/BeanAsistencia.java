package pe.gob.sunat.sp.asistencia.bean;

import java.io.Serializable;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class BeanAsistencia implements Serializable {

	private String codPers;

	private String periodo;

	private String UOrgan;

	private String mov;

	private String fIng;

	private String hIng;

	private String fSal;

	private String hSal;

	private String autorId;

	private String jefeAutor;

	private java.sql.Timestamp fechaAutor;

	private String estadoId;

	private java.sql.Timestamp fCreacion;

	private String cUserCrea;

	private java.sql.Timestamp fMod;

	private String cUserMod;

	private String trabajador;
	
	private String descMov;
	
	private String descObserv;
	

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

	public String getUOrgan() {
		return UOrgan;
	}

	public void setUOrgan(String UOrgan) {
		this.UOrgan = UOrgan;
	}

	public String getMov() {
		return mov;
	}

	public void setMov(String mov) {
		this.mov = mov;
	}

	public String getFIng() {
		return fIng;
	}

	public void setFIng(String fIng) {
		this.fIng = fIng;
	}

	public String getHIng() {
		return hIng;
	}

	public void setHIng(String hIng) {
		this.hIng = hIng;
	}

	public String getFSal() {
		return fSal;
	}

	public void setFSal(String fSal) {
		this.fSal = fSal;
	}

	public String getHSal() {
		return hSal;
	}

	public void setHSal(String hSal) {
		this.hSal = hSal;
	}

	public String getAutorId() {
		return autorId;
	}

	public void setAutorId(String autorId) {
		this.autorId = autorId;
	}

	public String getJefeAutor() {
		return jefeAutor;
	}

	public void setJefeAutor(String jefeAutor) {
		this.jefeAutor = jefeAutor;
	}

	public java.sql.Timestamp getFechaAutor() {
		return fechaAutor;
	}

	public void setFechaAutor(java.sql.Timestamp fechaAutor) {
		this.fechaAutor = fechaAutor;
	}

	public String getEstadoId() {
		return estadoId;
	}

	public void setEstadoId(String estadoId) {
		this.estadoId = estadoId;
	}

	public java.sql.Timestamp getFCreacion() {
		return fCreacion;
	}

	public void setFCreacion(java.sql.Timestamp fCreacion) {
		this.fCreacion = fCreacion;
	}

	public String getCUserCrea() {
		return cUserCrea;
	}

	public void setCUserCrea(String cUserCrea) {
		this.cUserCrea = cUserCrea;
	}

	public java.sql.Timestamp getFMod() {
		return fMod;
	}

	public void setFMod(java.sql.Timestamp fMod) {
		this.fMod = fMod;
	}

	public String getCUserMod() {
		return cUserMod;
	}

	public void setCUserMod(String cUserMod) {
		this.cUserMod = cUserMod;
	}

	public String getTrabajador() {
		return trabajador;
	}

	public void setTrabajador(String trabajador) {
		this.trabajador = trabajador;
	}
	
	public String getDescMov() {
		return descMov;
	}

	public void setDescMov(String desc) {
		this.descMov = desc;
	}	

	public String getDescObserv() {
		return descObserv;
	}

	public void setDescObserv(String descObserv) {
		this.descObserv = descObserv;
	}
}