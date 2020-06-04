//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
package pe.gob.sunat.sp.asistencia.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * <p>Title: Bean  Solicitud de reintegro</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Sunat</p>
 * @author agonzalesf
 * @version 1.0
 */
public class BeanSolReintegro implements Serializable{
	
	
	String annSolicitud;//año de solicitud
	Integer numSolicitud;//numero  de solicitud
	String numSeqrein;//numero de secuencia 
	String codUUOO;//codigo uuoo
	String codPers;//codigo de personal
	String codTipSol;//tipo de solicitud de reintegro : 301 ->asistencia/licencia 302->subsidio
	Date fecSolicitud;//fecha de solicitud
	String codPlanorig;//tipo de planilla origen
	String codSplanorig;//subtipo de planilla origen
	String annSolplan;//año de planilla origen
	String mesSolplan;//mes de planilla origen
	Integer numArchivo;//numero de archivo		
	int cantConc;//cantidad de conceptos	
	List conceptos = new ArrayList();//lista de conceptos de reintegro

	public String getAnnSolicitud() {
		return annSolicitud; 
	}

	public void setAnnSolicitud(String annSolicitud) {
		this.annSolicitud = annSolicitud;
	}

	public Integer getNumSolicitud() {
		return numSolicitud;
	}

	public void setNumSolicitud(Integer numSolicitud) {
		this.numSolicitud = numSolicitud;
	}

	public String getNumSeqrein() {
		return numSeqrein;
	}

	public void setNumSeqrein(String numSeqrein) {
		this.numSeqrein = numSeqrein;
	}

	public String getCodUUOO() {
		return codUUOO;
	}

	public void setCodUUOO(String codUUOO) {
		this.codUUOO = codUUOO;
	}

	public String getCodPers() {
		return codPers;
	}

	public void setCodPers(String codPers) {
		this.codPers = codPers;
	}

	public String getCodTipSol() {
		return codTipSol;
	}

	public void setCodTipSol(String codTipSol) {
		this.codTipSol = codTipSol;
	}

	public Date getFecSolicitud() {
		return fecSolicitud;
	}

	public void setFecSolicitud(Date fecSolicitud) {
		this.fecSolicitud = fecSolicitud;
	}

	public String getCodPlanorig() {
		return codPlanorig;
	}

	public void setCodPlanorig(String codPlanorig) {
		this.codPlanorig = codPlanorig;
	}

	public String getCodSplanorig() {
		return codSplanorig;
	}

	public void setCodSplanorig(String codSplanorig) {
		this.codSplanorig = codSplanorig;
	}

	public String getAnnSolplan() {
		return annSolplan;
	}

	public void setAnnSolplan(String annSolplan) {
		this.annSolplan = annSolplan;
	}

	public String getMesSolplan() {
		return mesSolplan;
	}

	public void setMesSolplan(String mesSolplan) {
		this.mesSolplan = mesSolplan;
	}

	public int getCantConc() {
		return cantConc;
	}

	public void setCantConc(int cantConc) {
		this.cantConc = cantConc;
	}

	public List getConceptos() {
		return conceptos;
	}

	public void setConceptos(List conceptos) {
		this.conceptos = conceptos;
	}

	public Integer getNumArchivo() {
		return numArchivo;
	}

	public void setNumArchivo(Integer numArchivo) {
		this.numArchivo = numArchivo;
	}
	 
	
	

}
