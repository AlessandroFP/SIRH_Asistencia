//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
package pe.gob.sunat.sp.asistencia.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>Title: Bean Detalle de concepto de Solicitud de reintegro</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Sunat</p>
 * @author agonzalesf
 * @version 1.0
 */

public class BeanSolReinDetalle implements Serializable {

	 
	private static final long serialVersionUID = 1L;
	Integer indice; //Indice
	String fecLiIni;//Fecha de inicio de licencia
	String fecLiFin;//Fecha de fin de licencia 
	BigDecimal codLi;//Codigo de licencia
	String tipLic;//Tipo de Licencia  S,P,L, Asistencia(null)
	String fecO; //Fecha de asistencia
	String codMov; //Codigo de movimiento		 
	String codConc;//Codigo de Concepto
	Integer dscto=new Integer(0);// Cantidad descontada (descuento - enproceso- yareintegrado)
	Integer solic=new Integer(0);//Cantidad solicitada
	String tipDev ="";//Codigo de tipo de devolucion
	String tipDevDesc ="";//Descripcion de tipo de devolucion
	
	int dsctoOriginal = 0;//descuento original (planilla)
	int dsctoDevuelto = 0;// descuento ya devuelto en devoluciones de asistencia/licencia
	int dsctoEnProceso = 0;//descuento en transito en solicitudes de reintegro 
	boolean existeLic = false;
	boolean existeSustento = false;
	boolean existeDev = false;
	
	public Integer getIndice() {
		return indice;
	}
	public void setIndice(Integer indice) {
		this.indice = indice;
	}
	public String getFecLiIni() {
		return fecLiIni;
	}
	public void setFecLiIni(String fecLiIni) {
		this.fecLiIni = fecLiIni;
	}
	public String getFecLiFin() {
		return fecLiFin;
	}
	public void setFecLiFin(String fecLiFin) {
		this.fecLiFin = fecLiFin;
	}
	public BigDecimal getCodLi() {
		return codLi;
	}
	public void setCodLi(BigDecimal codLi) {
		this.codLi = codLi;
	}
	public String getTipLic() {
		return tipLic;
	}
	public void setTipLic(String tipLic) {
		this.tipLic = tipLic;
	}
	public String getFecO() {
		return fecO;
	}
	public void setFecO(String fecO) {
		this.fecO = fecO;
	}
	public String getCodMov() {
		return codMov;
	}
	public void setCodMov(String codMov) {
		this.codMov = codMov;
	}
	public String getCodConc() {
		return codConc;
	}
	public void setCodConc(String codConc) {
		this.codConc = codConc;
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
	public String getTipDev() {
		return tipDev;
	}
	public void setTipDev(String tipDev) {
		this.tipDev = tipDev;
	}
	public String getTipDevDesc() {
		return tipDevDesc;
	}
	public void setTipDevDesc(String tipDevDesc) {
		this.tipDevDesc = tipDevDesc;
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
	public boolean isExisteLic() {
		return existeLic;
	}
	public void setExisteLic(boolean existeLic) {
		this.existeLic = existeLic;
	}
	public boolean isExisteSustento() {
		return existeSustento;
	}
	public void setExisteSustento(boolean existeSustento) {
		this.existeSustento = existeSustento;
	}
	public boolean isExisteDev() {
		return existeDev;
	}
	public void setExisteDev(boolean existeDev) {
		this.existeDev = existeDev;
	}
	 
	
	 
	
}
