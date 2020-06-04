package pe.gob.sunat.sp.asistencia.ejb;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

import pe.gob.sunat.ejb.EJBEstandarLocalCMP;

/**
 * @ejb.bean name="T45CMP" cmp-version="2.x" description="Entidad de la tabla
 *           t45turno"
 *           local-jndi-name="ejb/rrhh/cmp/sp/asistencia/t45cmp" type="CMP"
 *           view-type="local" schema="T45Schema"
 * 
 * @ejb.home local-class="pe.gob.sunat.sp.asistencia.ejb.T45CMPHome"
 *           local-extends="javax.ejb.EJBLocalHome"
 * 
 * @ejb.interface local-class="pe.gob.sunat.sp.asistencia.ejb.T45CMPLocal"
 *                local-extends="javax.ejb.EJBLocalObject"
 * 
 * @ejb.transaction type="Required"
 * @ejb.transaction-type type="Container"
 * 
 * @ejb.persistence table-name="t45turno"
 * @ejb.pk class="pe.gob.sunat.sp.asistencia.ejb.T45CMPPK"
 *         extends="java.lang.Object" implements="java.io.Serializable"
 * 
 * @jboss.container-configuration name="Standard CMP 2.x EntityBean"
 * @jboss.persistence create-table="false" remove-table="false"
 *                    table-name="t45turno"
 * @jboss.version="3.2"
 * 
 * @weblogic.persistence create-table="false" remove-table="false"
 *                       table-name="t45turno"
 * @weblogic.cache max-beans-in-cache="300"
 * @weblogic.pool max-beans-in-free-pool="300" initial-beans-in-free-pool="0"
 * @weblogic.delay-database-insert-until ejbCreate
 * 
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * @author CGARRATT
 */
abstract public class T45CMP extends EJBEstandarLocalCMP implements EntityBean {

	EntityContext entityContext;

	/**
	 * @ejb.create-method view-type="local"
	 * @param codTurno
	 * @return
	 * @throws CreateException
	 */
	public T45CMPPK ejbCreate(java.lang.String codTurno) throws CreateException {
		setCodTurno(codTurno);
		return null;
	}

	public void ejbPostCreate(java.lang.String codTurno) throws CreateException {
		/** @todo Complete this method */
	}

	/**
	 * @ejb.create-method view-type="local"
	 * @param codTurno
	 * @param descripcion
	 * @param fechaIni
	 * @param horaIni
	 * @param fechaFin
	 * @param horaFin
	 * @param estId
	 * @param dias
	 * @param tolerancia
	 * @param horaLimite
	 * @param operId
	 * @param fgraba
	 * @param refrIni
	 * @param refrFin
	 * @param refrMin
	 * @param cuser
	 * @param controlID
	 * @return
	 * @throws CreateException
	 */
	public T45CMPPK ejbCreate(java.lang.String codTurno,
			java.lang.String descripcion, java.lang.String fechaIni,
			java.lang.String horaIni, java.lang.String fechaFin,
			java.lang.String horaFin, java.lang.String estId,
			java.lang.String dias, java.lang.String tolerancia,
			java.lang.String horaLimite, java.lang.String operId,
			java.sql.Timestamp fgraba, java.lang.String refrIni,
			java.lang.String refrFin, java.lang.String refrMin,
			java.lang.String cuser, java.lang.String controlID)
			throws CreateException {

		setCodTurno(codTurno);
		setDesTurno(descripcion);
		setFInicio(fechaIni);
		setHInicio(horaIni);
		setFFfin(fechaFin);
		setHFin(horaFin);
		setEstId(estId);
		setDiasInt(dias);
		setTolera(tolerancia);
		setHLimit(horaLimite);
		setOperId(operId);
		setControlId(controlID);
		setFGraba(fgraba);
		setCuser(cuser);
		setRefrIni(refrIni);
		setRefrFin(refrFin);
		setRefrMin(refrMin);

		return null;
	}

	public void ejbPostCreate(java.lang.String codTurno,
			java.lang.String descripcion, java.lang.String fechaIni,
			java.lang.String horaIni, java.lang.String fechaFin,
			java.lang.String horaFin, java.lang.String estId,
			java.lang.String dias, java.lang.String tolerancia,
			java.lang.String horaLimite, java.lang.String operId,
			java.sql.Timestamp fgraba, java.lang.String refrIni,
			java.lang.String refrFin, java.lang.String refrMin,
			java.lang.String cuser, java.lang.String controlID)

	throws CreateException {
		/** @todo Complete this method */
	}

	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cod_turno" jdbc-type="VARCHAR"
	 */
	public abstract void setCodTurno(java.lang.String codTurno);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="des_turno" jdbc-type="VARCHAR"
	 */
	public abstract void setDesTurno(java.lang.String desTurno);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="f_inicio" jdbc-type="VARCHAR"
	 */
	public abstract void setFInicio(java.lang.String fInicio);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="h_inicio" jdbc-type="VARCHAR"
	 */
	public abstract void setHInicio(java.lang.String hInicio);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="f_ffin" jdbc-type="VARCHAR"
	 */
	public abstract void setFFfin(java.lang.String fFfin);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="h_fin" jdbc-type="VARCHAR"
	 */
	public abstract void setHFin(java.lang.String hFin);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="est_id" jdbc-type="VARCHAR"
	 */
	public abstract void setEstId(java.lang.String estId);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="dias_int" jdbc-type="VARCHAR"
	 */
	public abstract void setDiasInt(java.lang.String diasInt);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="tolera_turno" jdbc-type="VARCHAR"
	 */
	public abstract void setTolera(java.lang.String tolera);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="hlimit" jdbc-type="VARCHAR"
	 */
	public abstract void setHLimit(java.lang.String hLimit);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="oper_id" jdbc-type="VARCHAR"
	 */
	public abstract void setOperId(java.lang.String operId);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="scontrol_id" jdbc-type="VARCHAR"
	 */
	public abstract void setControlId(java.lang.String controlId);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="f_graba" jdbc-type="TIMESTAMP"
	 */
	public abstract void setFGraba(java.sql.Timestamp fGraba);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="h_inirefr" jdbc-type="VARCHAR"
	 */
	public abstract void setRefrIni(java.lang.String refrIni);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="h_finref" jdbc-type="VARCHAR"
	 */
	public abstract void setRefrFin(java.lang.String refrFin);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="min_refr" jdbc-type="INTEGER"
	 */
	public abstract void setRefrMin(java.lang.String refrMin);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cuser" jdbc-type="VARCHAR"
	 */
	public abstract void setCuser(java.lang.String cuser);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cod_turno" jdbc-type="VARCHAR"
	 * @ejb.pk-field
	 */
	public abstract java.lang.String getCodTurno();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="des_turno" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getDesTurno();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="f_inicio" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getFInicio();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="h_inicio" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getHInicio();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="f_ffin" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getFFfin();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="h_fin" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getHFin();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="est_id" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getEstId();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="dias_int" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getDiasInt();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="tolera_turno" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getTolera();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="hlimit" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getHLimit();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="oper_id" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getOperId();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="scontrol_id" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getControlId();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="f_graba" jdbc-type="TIMESTAMP"
	 */
	public abstract java.sql.Timestamp getFGraba();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="h_inirefr" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getRefrIni();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="h_finref" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getRefrFin();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="min_refr" jdbc-type="INTEGER"
	 */
	public abstract java.lang.String getRefrMin();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cuser" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getCuser();
}