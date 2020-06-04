package pe.gob.sunat.sp.asistencia.ejb;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

import pe.gob.sunat.ejb.EJBEstandarLocalCMP;

/**
 * @ejb.bean cmp-version="2.x" description="Entidad de la tabla t1279tipo_mov "
 *           local-jndi-name="ejb/rrhh/cmp/sp/asistencia/t1279cmp" name="T1279CMP"
 *           type="CMP" view-type="local" schema="T1279Schema"
 * 
 * @ejb.home local-class="pe.gob.sunat.sp.asistencia.ejb.T1279CMPHome"
 *           local-extends="javax.ejb.EJBLocalHome"
 * 
 * @ejb.interface local-class="pe.gob.sunat.sp.asistencia.ejb.T1279CMPLocal"
 *                local-extends="javax.ejb.EJBLocalObject"
 * 
 * @ejb.transaction-type type="Container"
 * 
 * @ejb.persistence table-name="t1279tipo_mov"
 * @ejb.pk class="pe.gob.sunat.sp.asistencia.ejb.T1279CMPPK"
 *         extends="java.lang.Object" implements="java.io.Serializable"
 * 
 * @jboss.container-configuration name="Standard CMP 2.x EntityBean"
 * @jboss.persistence create-table="false" remove-table="false"
 *                    table-name="t1279tipo_mov"
 * 
 * @jboss.version="3.2" 
 * 
 * @weblogic.persistence create-table="false" remove-table="false" table-name="t1279tipo_mov"
 * @weblogic.cache max-beans-in-cache="100"
 * @weblogic.pool max-beans-in-free-pool="100" initial-beans-in-free-pool="0"
 * @weblogic.delay-database-insert-until ejbCreate
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * 
 * @author CGARRATT
 *  
 */
public abstract class T1279CMP extends EJBEstandarLocalCMP 
	implements EntityBean {

	private EntityContext entityContext;

	/**
	 * @ejb.create-method view-type="local"
	 */
	public T1279CMPPK ejbCreate(java.lang.String codMovimiento,
			java.lang.String descripcion, java.lang.String califica,
			java.lang.String medida, java.lang.String estId,
			java.lang.String tipo, java.sql.Timestamp fechaIni,
			java.sql.Timestamp fechaFin, java.lang.String soliId,
			java.lang.String vbRrHh, java.lang.String procId,
			java.lang.String entrId, java.lang.String refrId,
			java.lang.String saliId, java.lang.Integer diasAntes,
			java.lang.Integer diasDespues, java.lang.Integer diasAcum,
			java.lang.String obligId, java.sql.Timestamp fgraba,
			java.lang.String cuser, java.lang.String ind_dias) throws CreateException {

		setMov(codMovimiento);
		setDescrip(descripcion);
		setCalifica(califica);
		setMedida(medida);
		setEstId(estId);
		setTipoId(tipo);
		setFini(fechaIni);
		setFfin(fechaFin);
		setSoliId(soliId);
		setRrhhId(vbRrHh);
		setProcId(procId);
		setEntrId(entrId);
		setRefrId(refrId);
		setSaliId(saliId);
		setDiasAntes(diasAntes);
		setDiasDespues(diasDespues);
		setDiasAcum(diasAcum);
		setObligId(obligId);
		setFgraba(fgraba);
		setCuser(cuser);
		setQValida(0);
		setIndDias(ind_dias);

		return null;
	}

	public void ejbPostCreate(java.lang.String codMovimiento,
			java.lang.String descripcion, java.lang.String califica,
			java.lang.String medida, java.lang.String estId,
			java.lang.String tipo, java.sql.Timestamp fechaIni,
			java.sql.Timestamp fechaFin, java.lang.String soliId,
			java.lang.String vbRrHh, java.lang.String procId,
			java.lang.String entrId, java.lang.String refrId,
			java.lang.String saliId, java.lang.Integer diasAntes,
			java.lang.Integer diasDespues, java.lang.Integer diasAcum,
			java.lang.String obligId, java.sql.Timestamp fgraba,
			java.lang.String cuser, java.lang.String ind_dias) throws CreateException {
		/** @todo Complete this method */
	}

	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="mov" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setMov(java.lang.String mov);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="descrip" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setDescrip(java.lang.String descrip);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="califica" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setCalifica(java.lang.String califica);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="medida" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setMedida(java.lang.String medida);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="est_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setEstId(java.lang.String estId);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="tipo_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setTipoId(java.lang.String tipoId);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="ffini" jdbc-type="TIMESTAMP"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setFini(java.sql.Timestamp fini);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="ffin" jdbc-type="TIMESTAMP"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setFfin(java.sql.Timestamp ffin);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="ssoli_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setSoliId(java.lang.String soliId);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="srrhh_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setRrhhId(java.lang.String rrhhId);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="sproc_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setProcId(java.lang.String procId);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="sentr_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setEntrId(java.lang.String entrId);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="srefr_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setRefrId(java.lang.String refrId);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="sali_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setSaliId(java.lang.String saliId);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="dias_antes" jdbc-type="INTEGER"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setDiasAntes(java.lang.Integer diasAntes);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="dias_despues" jdbc-type="INTEGER"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setDiasDespues(java.lang.Integer diasDespues);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="dias_acum" jdbc-type="INTEGER"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setDiasAcum(java.lang.Integer diasAcum);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="oblig_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setObligId(java.lang.String obligId);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fgraba" jdbc-type="TIMESTAMP"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setFgraba(java.sql.Timestamp fgraba);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cuser" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setCuser(java.lang.String cuser);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="qvalida" jdbc-type="SMALLINT"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setQValida(int qvalida);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="ind_dias" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setIndDias(java.lang.String ind_dias);	
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="ind_proc" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract void setIndProc(java.lang.String ind_proc);	
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="mov" jdbc-type="VARCHAR"
	 * @ejb.pk-field
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getMov();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="descrip" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getDescrip();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="califica" jdbc-type="VARCHAR" 
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getCalifica();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="medida" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getMedida();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="est_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getEstId();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="tipo_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getTipoId();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="ffini" jdbc-type="TIMESTAMP"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.sql.Timestamp getFini();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="ffin" jdbc-type="TIMESTAMP"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.sql.Timestamp getFfin();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="ssoli_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getSoliId();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="srrhh_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getRrhhId();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="sproc_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getProcId();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="sentr_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getEntrId();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="srefr_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getRefrId();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="sali_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getSaliId();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="dias_antes" jdbc-type="INTEGER"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.Integer getDiasAntes();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="dias_despues" jdbc-type="INTEGER"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.Integer getDiasDespues();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="dias_acum" jdbc-type="INTEGER"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.Integer getDiasAcum();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="oblig_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getObligId();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="fgraba" jdbc-type="TIMESTAMP"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.sql.Timestamp getFgraba();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="cuser" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getCuser();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="qvalida" jdbc-type="SMALLINT"
	 * @ejb.transaction type="Required"
	 */
	public abstract int getQValida();	
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="ind_dias" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getIndDias();	
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="ind_proc" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getIndProc();	

}