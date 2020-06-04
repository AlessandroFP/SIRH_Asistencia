package pe.gob.sunat.sp.asistencia.ejb;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

import pe.gob.sunat.ejb.EJBEstandarLocalCMP;

/**
 * @ejb.bean name="T1276CMP" cmp-version="2.x" description="Entidad de la tabla
 *           t1276periodo" local-jndi-name="ejb/rrhh/cmp/sp/asistencia/t1276cmp"
 *           type="CMP" view-type="local" schema="T1276Schema"
 * 
 * @ejb.home local-class="pe.gob.sunat.sp.asistencia.ejb.T1276CMPHome"
 *           local-extends="javax.ejb.EJBLocalHome"
 * 
 * @ejb.interface local-class="pe.gob.sunat.sp.asistencia.ejb.T1276CMPLocal"
 *                local-extends="javax.ejb.EJBLocalObject"
 * 
 * @ejb.transaction type="Required"
 * @ejb.transaction-type type="Container"
 * 
 * @ejb.persistence table-name="t1276periodo"
 * @ejb.pk class="pe.gob.sunat.sp.asistencia.ejb.T1276CMPPK"
 *         extends="java.lang.Object" implements="java.io.Serializable"
 * 
 * @jboss.container-configuration name="Standard CMP 2.x EntityBean"
 * @jboss.persistence create-table="false" remove-table="false"
 *                    table-name="t1276periodo"
 * @jboss.version="3.2"
 * 
 * @weblogic.persistence create-table="false" remove-table="false"
 *                       table-name="t1276periodo"
 * @weblogic.cache max-beans-in-cache="300"
 * @weblogic.pool max-beans-in-free-pool="300" initial-beans-in-free-pool="0"
 * @weblogic.delay-database-insert-until ejbCreate
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"

 * 
 * @author CGARRATT
 */
abstract public class T1276CMP extends EJBEstandarLocalCMP implements EntityBean {
	
	EntityContext entityContext;

	/**
	 * @ejb.create-method view-type="local"
	 * @param periodo
	 * @return
	 * @throws CreateException
	 */
	public T1276CMPPK ejbCreate(java.lang.String periodo)
			throws CreateException {
		setPeriodo(periodo);
		return null;
	}

	/**
	 * @ejb.create-method view-type="local"
	 * @param periodo
	 * @param finicio
	 * @param ffin
	 * @param fcierre
	 * @param estId
	 * @param fgraba
	 * @param cuser
	 * @return
	 * @throws CreateException
	 */
	public T1276CMPPK ejbCreate(java.lang.String periodo,
			java.lang.String finicio, java.lang.String ffin,
			java.lang.String fcierre, java.lang.String estId,
			java.sql.Timestamp fgraba, java.lang.String cuser)
			throws CreateException {

		setPeriodo(periodo);
		setFinicio(finicio);
		setFfin(ffin);
		setFcierre(fcierre);
		setEstId(estId);
		setFgraba(fgraba);
		setCuser(cuser);

		return null;
	}

	public void ejbPostCreate(java.lang.String periodo) throws CreateException {
		/** @todo Complete this method */
	}

	public void ejbPostCreate(java.lang.String periodo,
			java.lang.String fcierre, java.lang.String finicio,
			java.lang.String ffin, java.lang.String estId,
			java.sql.Timestamp fgraba, java.lang.String cuser)
			throws CreateException {
		/** @todo Complete this method */
	}

	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="periodo" jdbc-type="VARCHAR"
	 */	
	public abstract void setPeriodo(java.lang.String periodo);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fcierre" jdbc-type="VARCHAR"
	 */
	public abstract void setFcierre(java.lang.String fcierre);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="finicio" jdbc-type="VARCHAR"
	 */
	public abstract void setFinicio(java.lang.String finicio);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="ffin" jdbc-type="VARCHAR"
	 */
	public abstract void setFfin(java.lang.String ffin);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="est_id" jdbc-type="VARCHAR"
	 */
	public abstract void setEstId(java.lang.String estId);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fgraba" jdbc-type="VARCHAR"
	 */
	public abstract void setFgraba(java.sql.Timestamp fgraba);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cuser" jdbc-type="VARCHAR"
	 */
	public abstract void setCuser(java.lang.String cuser);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="periodo" jdbc-type="VARCHAR"
	 * @ejb.pk-field
	 */
	public abstract java.lang.String getPeriodo();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fcierre" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getFcierre();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="finicio" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getFinicio();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="ffin" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getFfin();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="est_id" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getEstId();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fgraba" jdbc-type="VARCHAR"
	 */
	public abstract java.sql.Timestamp getFgraba();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cuser" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getCuser();

}