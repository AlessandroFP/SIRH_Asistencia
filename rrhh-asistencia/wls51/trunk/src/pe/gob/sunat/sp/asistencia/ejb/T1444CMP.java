package pe.gob.sunat.sp.asistencia.ejb;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

import pe.gob.sunat.ejb.EJBEstandarLocalCMP;

/**
 * @ejb.bean name="T1444CMP" cmp-version="2.x" description="Entidad de la tabla
 *           t1444devol"
 *           local-jndi-name="ejb/rrhh/cmp/sp/asistencia/t1444cmp" type="CMP"
 *           view-type="local" schema="T1444Schema"
 * 
 * @ejb.home local-class="pe.gob.sunat.sp.asistencia.ejb.T1444CMPHome"
 *           local-extends="javax.ejb.EJBLocalHome"
 * 
 * @ejb.interface local-class="pe.gob.sunat.sp.asistencia.ejb.T1444CMPLocal"
 *                local-extends="javax.ejb.EJBLocalObject"
 * 
 * @ejb.transaction type="Required"
 * @ejb.transaction-type type="Container"
 * 
 * @ejb.persistence table-name="t1444devol"
 * @ejb.pk class="pe.gob.sunat.sp.asistencia.ejb.T1444CMPPK"
 *         extends="java.lang.Object" implements="java.io.Serializable"
 * 
 * @jboss.container-configuration name="Standard CMP 2.x EntityBean"
 * @jboss.persistence create-table="false" remove-table="false"
 *                    table-name="t1444devol"
 * @jboss.version="3.2"
 * 
 * @weblogic.persistence create-table="false" remove-table="false"
 *                       table-name="t1444devol"
 * @weblogic.cache max-beans-in-cache="300"
 * @weblogic.pool max-beans-in-free-pool="300" initial-beans-in-free-pool="0"
 * @weblogic.delay-database-insert-until ejbCreate
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * 
 * @author CGARRATT
 */
abstract public class T1444CMP extends EJBEstandarLocalCMP implements
		EntityBean {
	EntityContext entityContext;

	/**
	 * @ejb.create-method view-type="local"
	 * @param codPers
	 * @param periodo
	 * @param mov
	 * @return
	 * @throws CreateException
	 */
	public T1444CMPPK ejbCreate(java.lang.String codPers,
			java.lang.String periodo, java.lang.String mov)
			throws CreateException {
		setCodPers(codPers);
		setPeriodo(periodo);
		setMov(mov);
		return null;
	}

	public void ejbPostCreate(java.lang.String codPers,
			java.lang.String periodo, java.lang.String mov)
			throws CreateException {
		/** @todo Complete this method */
	}

	/**
	 * @ejb.create-method view-type="local"
	 * @param codPers
	 * @param periodo
	 * @param mov
	 * @throws CreateException
	 */
	public T1444CMPPK ejbCreate(java.lang.String codPers,
			java.lang.String periodo, java.lang.String mov,
			java.lang.Integer total, java.lang.String periodoReg,
			String observacion, java.sql.Timestamp fCreacion, String usuario)
			throws CreateException {
		setCodPers(codPers);
		setPeriodo(periodo);
		setMov(mov);
		setTotal(total);
		setPeriodReg(periodoReg);
		setObserv(observacion);
		setFcreacion(fCreacion);
		setCuserCrea(usuario);
		return null;
	}

	public void ejbPostCreate(java.lang.String codPers,
			java.lang.String periodo, java.lang.String mov,
			java.lang.Integer total, java.lang.String periodoReg,
			String observacion, java.sql.Timestamp fCreacion, String usuario)
			throws CreateException {
		/** @todo Complete this method */
	}

	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cod_pers" jdbc-type="VARCHAR"
	 */
	public abstract void setCodPers(java.lang.String codPers);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="periodo" jdbc-type="VARCHAR"
	 */
	public abstract void setPeriodo(java.lang.String periodo);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="mov" jdbc-type="VARCHAR"
	 */
	public abstract void setMov(java.lang.String mov);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="total" jdbc-type="INTEGER"
	 */
	public abstract void setTotal(java.lang.Integer total);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="period_reg" jdbc-type="VARCHAR"
	 */
	public abstract void setPeriodReg(java.lang.String periodReg);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="observ" jdbc-type="VARCHAR"
	 */
	public abstract void setObserv(java.lang.String observ);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fcreacion" jdbc-type="TIMESTAMP"
	 */
	public abstract void setFcreacion(java.sql.Timestamp fcreacion);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cuser_crea" jdbc-type="VARCHAR"
	 */
	public abstract void setCuserCrea(java.lang.String cuserCrea);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fmod" jdbc-type="TIMESTAMP"
	 */
	public abstract void setFmod(java.sql.Timestamp fmod);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cuser_mod" jdbc-type="VARCHAR"
	 */
	public abstract void setCuserMod(java.lang.String cuserMod);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cod_pers" jdbc-type="VARCHAR"
	 * @ejb.pk-field
	 */
	public abstract java.lang.String getCodPers();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="periodo" jdbc-type="VARCHAR"
	 * @ejb.pk-field
	 */
	public abstract java.lang.String getPeriodo();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="mov" jdbc-type="VARCHAR"
	 * @ejb.pk-field
	 */
	public abstract java.lang.String getMov();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="total" jdbc-type="INTEGER"
	 */
	public abstract java.lang.Integer getTotal();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="period_reg" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getPeriodReg();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="observ" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getObserv();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fcreacion" jdbc-type="TIMESTAMP"
	 */
	public abstract java.sql.Timestamp getFcreacion();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cuser_crea" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getCuserCrea();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fmod" jdbc-type="TIMESTAMP"
	 */
	public abstract java.sql.Timestamp getFmod();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cuser_mod" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getCuserMod();

}