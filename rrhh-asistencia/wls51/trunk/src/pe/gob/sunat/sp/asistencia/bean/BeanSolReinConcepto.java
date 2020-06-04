//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
package pe.gob.sunat.sp.asistencia.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: Bean Concepto de Solicitud de reintegro</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Sunat</p>
 * @author agonzalesf
 * @version 1.0
 */

public class BeanSolReinConcepto implements Serializable {
 
	private static final long serialVersionUID = 1L;
	Integer numSeq;//Numero de Secuencia
	String codTipLi;//Tipo de Licencia  S,P,L, Asistencia(null)
	String codConc;//Codigo de concepto
	String conDesc;//Descripcion de concepto
	String codMov;//Codigo de Movimiento
	String desMov;//Descripcion de movimiento
	String desMot;//Motivo de reintegro 
	Integer dscto=new Integer(0);// Cantidad descontada (descuento - enproceso- yareintegrado)
	Integer solic=new Integer(0); //Cantidad solicitada
	int dsctoOriginal =0;// descuento original (planilla)
	int dsctoDevuelto =0;// descuento ya devuelto en devoluciones de asistencia/licencia
	int dsctoEnProceso =0;//descuento en transito en solicitudes de reintegro 
	List detalles = new ArrayList();//lista de detalles
	
	
	
	public Integer getNumSeq() {
		return numSeq;
	}
	public void setNumSeq(Integer numSeq) {
		this.numSeq = numSeq;
	}
	public String getCodTipLi() {
		return codTipLi;
	}
	public void setCodTipLi(String codTipLi) {
		this.codTipLi = codTipLi;
	}
	public String getCodConc() {
		return codConc;
	}
	public void setCodConc(String codConc) {
		this.codConc = codConc;
	}
	public String getConDesc() {
		return conDesc;
	}
	public void setConDesc(String conDesc) {
		this.conDesc = conDesc;
	}
	public String getCodMov() {
		return codMov;
	}
	public void setCodMov(String codMov) {
		this.codMov = codMov;
	}
	public String getDesMov() {
		return desMov;
	}
	public void setDesMov(String desMov) {
		this.desMov = desMov;
	}
	public String getDesMot() {
		return desMot;
	}
	public void setDesMot(String desMot) {
		this.desMot = desMot;
	}
	public Integer getDscto() {
		return dscto;
	}
	public void setDscto(Integer dscto) {
		this.dscto = dscto;
	}
	public Integer getSolic() {
		return solic;
	}
	public void setSolic(Integer solic) {
		this.solic = solic;
	}
	public List getDetalles() {
		return detalles;
	}
	public void setDetalles(List detalles) {
		this.detalles = detalles;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getDsctoOriginal() {
		return dsctoOriginal;
	}
	public void setDsctoOriginal(int dsctoOriginal) {
		this.dsctoOriginal = dsctoOriginal;
	}
	public int getDsctoDevuelto() {
		return dsctoDevuelto;
	}
	public void setDsctoDevuelto(int dsctoDevuelto) {
		this.dsctoDevuelto = dsctoDevuelto;
	}
	public int getDsctoEnProceso() {
		return dsctoEnProceso;
	}
	public void setDsctoEnProceso(int dsctoEnProceso) {
		this.dsctoEnProceso = dsctoEnProceso;
	}
	 
	
}
