package pe.gob.sunat.sp.asistencia.ejb;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

import pe.gob.sunat.ejb.EJBEstandarLocalCMP;

/**
 * @ejb.bean name="T1280CMP" cmp-version="2.x" description="Entidad de la tabla
 *           t1280tipo_reloj"
 *           local-jndi-name="ejb/rrhh/cmp/sp/asistencia/t1280cmp" type="CMP"
 *           view-type="local" schema="T1280Schema"
 * 
 * @ejb.home local-class="pe.gob.sunat.sp.asistencia.ejb.T1280CMPHome"
 *           local-extends="javax.ejb.EJBLocalHome"
 * 
 * @ejb.interface local-class="pe.gob.sunat.sp.asistencia.ejb.T1280CMPLocal"
 *                local-extends="javax.ejb.EJBLocalObject"
 * 
 * @ejb.transaction type="Required"
 * @ejb.transaction-type type="Container"
 * 
 * @ejb.persistence table-name="t1280tipo_reloj"
 * @ejb.pk class="pe.gob.sunat.sp.asistencia.ejb.T1280CMPPK"
 *         extends="java.lang.Object" implements="java.io.Serializable"
 * 
 * @jboss.container-configuration name="Standard CMP 2.x EntityBean"
 * @jboss.persistence create-table="false" remove-table="false"
 *                    table-name="t1280tipo_reloj"
 * @jboss.version="3.2"
 * 
 * @weblogic.persistence create-table="false" remove-table="false"
 *                       table-name="t1280tipo_reloj"
 * @weblogic.cache max-beans-in-cache="100"
 * @weblogic.pool max-beans-in-free-pool="100" initial-beans-in-free-pool="0"
 * @weblogic.delay-database-insert-until ejbCreate
 * 
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * @author CGARRATT
 */
abstract public class T1280CMP extends EJBEstandarLocalCMP implements
		EntityBean {

	EntityContext entityContext;

	/**
	 * @ejb.create-method view-type="local"
	 * @param reloj
	 * @return
	 * @throws CreateException
	 */
	public T1280CMPPK ejbCreate(java.lang.String reloj)
			throws CreateException {
		setReloj(reloj);
		return null;
	}

	public void ejbPostCreate(java.lang.String reloj) throws CreateException {
		/** @todo Complete this method */
	}

	/**
	 * @ejb.create-method view-type="local"
	 * @param reloj
	 * @param descrip
	 * @param sede
	 * @param estId
	 * @param fgraba
	 * @param cuser
	 * @return
	 * @throws CreateException
	 */
	public T1280CMPPK ejbCreate(java.lang.String reloj,
			java.lang.String descrip, java.lang.String sede,
			java.lang.String estId, java.sql.Timestamp fgraba,
			java.lang.String cuser) throws CreateException {

		setReloj(reloj);
		setDescrip(descrip);
		setSede(sede);
		setEstId(estId);
		setFgraba(fgraba);
		setCuser(cuser);

		return null;
	}

	public void ejbPostCreate(java.lang.String reloj, java.lang.String descrip,
			java.lang.String sede, java.lang.String estId,
			java.sql.Timestamp fgraba, java.lang.String cuser)
			throws CreateException {
		/** @todo Complete this method */
	}

	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="reloj" jdbc-type="VARCHAR"
	 */	
	public abstract void setReloj(java.lang.String reloj);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="descrip" jdbc-type="VARCHAR"
	 */	
	public abstract void setDescrip(java.lang.String descrip);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="sede" jdbc-type="VARCHAR"
	 */	
	public abstract void setSede(java.lang.String sede);
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
	 * @ejb.persistence column-name="reloj" jdbc-type="VARCHAR"
	 * @ejb.pk-field
	 */	
	public abstract java.lang.String getReloj();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="descrip" jdbc-type="VARCHAR"
	 */	
	public abstract java.lang.String getDescrip();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="sede" jdbc-type="VARCHAR"
	 */	
	public abstract java.lang.String getSede();
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