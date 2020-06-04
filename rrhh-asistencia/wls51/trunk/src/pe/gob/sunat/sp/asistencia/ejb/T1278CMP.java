package pe.gob.sunat.sp.asistencia.ejb;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

import pe.gob.sunat.ejb.EJBEstandarLocalCMP;

/**
 * @ejb.bean name="T1278CMP" cmp-version="2.x" description="Entidad de la tabla
 *           t1278periodo_area"
 *           local-jndi-name="ejb/rrhh/cmp/sp/asistencia/t1278cmp" type="CMP"
 *           view-type="local" schema="T1278Schema"
 * 
 * @ejb.home local-class="pe.gob.sunat.sp.asistencia.ejb.T1278CMPHome"
 *           local-extends="javax.ejb.EJBLocalHome"
 * 
 * @ejb.interface local-class="pe.gob.sunat.sp.asistencia.ejb.T1278CMPLocal"
 *                local-extends="javax.ejb.EJBLocalObject"
 * 
 * @ejb.transaction type="Required"
 * @ejb.transaction-type type="Container"
 * 
 * @ejb.persistence table-name="t1278periodo_area"
 * @ejb.pk class="pe.gob.sunat.sp.asistencia.ejb.T1278CMPPK"
 *         extends="java.lang.Object" implements="java.io.Serializable"
 * 
 * @jboss.container-configuration name="Standard CMP 2.x EntityBean"
 * @jboss.persistence create-table="false" remove-table="false"
 *                    table-name="t1278periodo_area"
 * @jboss.version="3.2"
 * 
 * @weblogic.persistence create-table="false" remove-table="false"
 *                       table-name="t1278periodo_area"
 * @weblogic.cache max-beans-in-cache="1000"
 * @weblogic.pool max-beans-in-free-pool="1000" initial-beans-in-free-pool="0"
 * @weblogic.delay-database-insert-until ejbCreate
 * 
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * @author CGARRATT
 */
abstract public class T1278CMP extends EJBEstandarLocalCMP implements
		EntityBean {

	EntityContext entityContext;

	/**
	 * @ejb.create-method view-type="local"
	 * @param periodo
	 * @param uOrgan
	 * @return
	 * @throws CreateException
	 */
	public T1278CMPPK ejbCreate(java.lang.String periodo,
			java.lang.String uOrgan) throws CreateException {
		setPeriodo(periodo);
		setUOrgan(uOrgan);
		return null;
	}

	/**
	 * @ejb.create-method view-type="local"
	 * @param periodo
	 * @param uOrgan
	 * @param estId
	 * @param fgraba
	 * @param usuario
	 * @return
	 * @throws CreateException
	 */
	public T1278CMPPK ejbCreate(java.lang.String periodo,
			java.lang.String uOrgan, java.lang.String estId,
			java.sql.Timestamp fgraba, java.lang.String usuario)
			throws CreateException {

		setPeriodo(periodo);
		setUOrgan(uOrgan);
		setEstId(estId);
		setFgraba(fgraba);
		setCuser(usuario);
		return null;
	}

	public void ejbPostCreate(java.lang.String periodo, java.lang.String uOrgan)
			throws CreateException {
		/** @todo Complete this method */
	}

	public void ejbPostCreate(java.lang.String periodo,
			java.lang.String uOrgan, java.lang.String estId,
			java.sql.Timestamp fgraba, java.lang.String usuario)
			throws CreateException {
	}

	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="periodo" jdbc-type="VARCHAR"
	 */	
	public abstract void setPeriodo(java.lang.String periodo);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="u_organ" jdbc-type="VARCHAR"
	 */	
	public abstract void setUOrgan(java.lang.String uOrgan);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="est_id" jdbc-type="VARCHAR"
	 */	
	public abstract void setEstId(java.lang.String estId);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fgraba" jdbc-type="TIMESTAMP"
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
	 * @ejb.persistence column-name="u_organ" jdbc-type="VARCHAR"
	 * @ejb.pk-field
	 */	
	public abstract java.lang.String getUOrgan();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="est_id" jdbc-type="VARCHAR"
	 */	
	public abstract java.lang.String getEstId();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fgraba" jdbc-type="TIMESTAMP"
	 */	
	public abstract java.sql.Timestamp getFgraba();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cuser" jdbc-type="VARCHAR"
	 */	
	public abstract java.lang.String getCuser();

}