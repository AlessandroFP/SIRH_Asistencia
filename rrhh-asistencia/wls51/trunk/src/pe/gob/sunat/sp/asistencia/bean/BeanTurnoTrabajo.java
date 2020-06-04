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

public class BeanTurnoTrabajo implements Serializable {

	private String codPers = "";

	private String codUOrg = "";

	private String descUOrg = "";

	private String turno = "";

	private String estId = "";

	private String horaIni = "";

	private String horaFin = "";

	private String trabajador = "";

	private String codCategoria = "";

	private String descCategoria = "";

	private java.sql.Timestamp fechaFin;

	private java.sql.Timestamp fechaIni;

	private String descTurno = "";

	private int duracion = 0;

	private int tolera = 0;

	private String horaLimite = "";

	//private int horasCompensa = 0;
	private boolean operativo = false;

	private boolean controla = true;

	private String horaIniRefrigerio = "";

	private String horaFinRefrigerio = "";

	private int minutosRefrigerio = 0;
	
	//ICR -06/12/2010-agregacion de "Fecha y Usuario de Creación" y "Fecha y Usuario de Modificación"
	
	private java.sql.Timestamp fechaCreacion;
	
	private String codUsuCreacion = "";	

	private java.sql.Timestamp fechaModificacion;
	
	private String codUsuModificacion = "";
	
	private String sustento="";
	
	//ICR

	public BeanTurnoTrabajo() {
	}

	public String getCodPers() {
		return codPers;
	}

	public void setCodPers(String codPers) {
		this.codPers = codPers;
	}

	public void setCodUOrg(String codUOrg) {
		this.codUOrg = codUOrg;
	}

	public String getCodUOrg() {
		return codUOrg;
	}

	public void setDescUOrg(String descUOrg) {
		this.descUOrg = descUOrg;
	}

	public String getDescUOrg() {
		return descUOrg;
	}

	public void setTurno(String turno) {
		this.turno = turno;
	}

	public String getTurno() {
		return turno;
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
	
	
	public String getSustento() {
		return sustento;
	}

	public void setSustento(String sustento) {
		this.sustento = sustento;
	}

	public java.sql.Timestamp getFechaFin() {
		return fechaFin;
	}

	public void setEstId(String estId) {
		this.estId = estId;
	}

	public String getEstId() {
		return estId;
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

	public void setTrabajador(String trabajador) {
		this.trabajador = trabajador;
	}

	public String getTrabajador() {
		return trabajador;
	}

	public void setCodCategoria(String codCategoria) {
		this.codCategoria = codCategoria;
	}

	public String getCodCategoria() {
		return codCategoria;
	}

	public void setDescCategoria(String descCategoria) {
		this.descCategoria = descCategoria;
	}

	public String getDescCategoria() {
		return descCategoria;
	}

	public void setDescTurno(String descTurno) {
		this.descTurno = descTurno;
	}

	public String getDescTurno() {
		return descTurno;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	public int getDuracion() {
		return duracion;
	}

	public int getTolera() {
		return tolera;
	}

	public void setTolera(int tolera) {
		this.tolera = tolera;
	}

	public String getHoraLimite() {
		return horaLimite;
	}

	public void setHoraLimite(String horaLimite) {
		this.horaLimite = horaLimite;
	}

	/*
	 * public int getHorasCompensa() { return horasCompensa; } public void
	 * setHorasCompensa(int horasCompensa) { this.horasCompensa = horasCompensa; }
	 */
	public boolean isOperativo() {
		return operativo;
	}

	public void setOperativo(boolean operativo) {
		this.operativo = operativo;
	}

	public String getHoraIniRefrigerio() {
		return horaIniRefrigerio;
	}

	public void setHoraIniRefrigerio(String horaIniRefrigerio) {
		this.horaIniRefrigerio = horaIniRefrigerio;
	}

	public String getHoraFinRefrigerio() {
		return horaFinRefrigerio;
	}

	public void setHoraFinRefrigerio(String horaFinRefrigerio) {
		this.horaFinRefrigerio = horaFinRefrigerio;
	}

	public int getMinutosRefrigerio() {
		return minutosRefrigerio;
	}

	public void setMinutosRefrigerio(int minutosRefrigerio) {
		this.minutosRefrigerio = minutosRefrigerio;
	}

	/**
	 * @return Returns the controla.
	 */
	public boolean isControla() {
		return controla;
	}

	/**
	 * @param controla
	 *            The controla to set.
	 */
	public void setControla(boolean controla) {
		this.controla = controla;
	}
	
	//ICR 06/12/2010
	
	public void setFechaCreacion(java.sql.Timestamp fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public java.sql.Timestamp getFechaCreacion() {
		return fechaCreacion;
	}
		
	public void setCodUsuCreacion(String codUsuCreacion) {
		this.codUsuCreacion = codUsuCreacion;
	}

	public String getCodUsuCreacion() {
		return codUsuCreacion;
	}
	
	public void setFechaModificacion(java.sql.Timestamp fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	public java.sql.Timestamp getFechaModificacion() {
		return fechaModificacion;
	}

	public void setCodUsuModificacion(String codUsuModificacion) {
		this.codUsuModificacion = codUsuModificacion;
	}

	public String getCodUsuModificacion() {
		return codUsuModificacion;
	}	
	
	//ICR 06/12/2010
	
	public String toString(){
		return turno+" - ("+fechaIni+"-"+fechaFin+") / ("+horaIni+"-"+horaFin+")";
	}
	
}