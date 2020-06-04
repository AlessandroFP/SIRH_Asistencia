package pe.gob.sunat.sp.asistencia.ejb;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

import pe.gob.sunat.ejb.EJBEstandarLocalCMP;
import pe.gob.sunat.utils.Constantes;

/**
 * 
 * 
 * @ejb.bean cmp-version="2.x" description="Entidad de la tabla
 *           T1456vacacion_gen "
 *           local-jndi-name="ejb/rrhh/cmp/sp/asistencia/t1456cmp" name="T1456CMP"
 *           type="CMP" view-type="local" schema="T1456Schema"
 * 
 * @ejb.home local-class="pe.gob.sunat.sp.asistencia.ejb.T1456CMPHome"
 *           local-extends="javax.ejb.EJBLocalHome"
 * 
 * @ejb.interface local-class="pe.gob.sunat.sp.asistencia.ejb.T1456CMPLocal"
 *                local-extends="javax.ejb.EJBLocalObject"
 * 
 * @ejb.transaction-type type="Container"
 * 
 * @ejb.persistence table-name="T1456vacacion_gen"
 * @ejb.pk class="pe.gob.sunat.sp.asistencia.ejb.T1456CMPPK"
 *         extends="java.lang.Object" implements="java.io.Serializable"
 * 
 * @jboss.container-configuration name="Standard CMP 2.x EntityBean"
 * @jboss.persistence create-table="false" remove-table="false"
 *                    table-name="T1456vacacion_gen"
 * 
 * @jboss.version="3.2" 
 * 
 * @weblogic.persistence create-table="false" remove-table="false" table-name="T1456vacacion_gen"
 * @weblogic.cache max-beans-in-cache="300"
 * @weblogic.pool max-beans-in-free-pool="300" initial-beans-in-free-pool="0"
 * @weblogic.delay-database-insert-until ejbCreate
 * 
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * @author CGARRATT
 *  
 */
public abstract class T1456CMP extends EJBEstandarLocalCMP implements
		EntityBean {

	private EntityContext entityContext;

	/**
	 * @ejb.create-method view-type="local"
	 *	 	
	 * @param data Map
	 * @return java.lang.Object
	 * @throws CreateException
	 */	
	public T1456CMPPK ejbCreate(java.lang.String codPers, java.sql.Date fecha)
			throws CreateException {
		setCodPers(codPers);
		setFecha(fecha);

		return null;
	}

	public void ejbPostCreate(java.lang.String codPers, java.sql.Date fecha)
			throws CreateException {
		/** @todo Complete this method */
	}

	/**
	 * 
	 * @ejb.create-method view-type="local"
	 *  
	 * @param codPers
	 * @param fecha
	 * @param usuario
	 * @return
	 * @throws CreateException
	 */
	public T1456CMPPK ejbCreate(java.lang.String codPers, java.sql.Date fecha,
			java.lang.String usuario) throws CreateException {
		setCodPers(codPers);
		setFecha(fecha);
		setEstId(Constantes.ACTIVO);

		setFcreacion(new java.sql.Timestamp(System.currentTimeMillis()));
		setCuserCrea(usuario);
		return null;
	}

	public void ejbPostCreate(java.lang.String codPers, java.sql.Date fecha,
			java.lang.String Usuario) throws CreateException {
		/** @todo Complete this method */
	}

	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="cod_pers" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */	
	public abstract void setCodPers(java.lang.String codPers);
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="fecha" jdbc-type="DATE"
	 * @ejb.transaction type="Required"
	 */	
	public abstract void setFecha(java.sql.Date fecha);
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="est_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */	
	public abstract void setEstId(java.lang.String estId);
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="fcreacion" jdbc-type="TIMESTAMP"
	 * @ejb.transaction type="Required"
	 */	
	public abstract void setFcreacion(java.sql.Timestamp fcreacion);
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="cuser_crea" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */	
	public abstract void setCuserCrea(java.lang.String cuserCrea);
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="fmod" jdbc-type="TIMESTAMP"
	 * @ejb.transaction type="Required"
	 */	
	public abstract void setFmod(java.sql.Timestamp fmod);
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="cuser_mod" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */	
	public abstract void setCuserMod(java.lang.String cuserMod);

	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="cod_pers" jdbc-type="VARCHAR"
	 * @ejb.pk-field
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getCodPers();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="fecha" jdbc-type="DATE"
	 * @ejb.pk-field
	 * @ejb.transaction type="Required"
	 */
	public abstract java.sql.Date getFecha();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="est_id" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getEstId();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="fcreacion" jdbc-type="TIMESTAMP"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.sql.Timestamp getFcreacion();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="cuser_crea" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getCuserCrea();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="fmod" jdbc-type="TIMESTAMP"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.sql.Timestamp getFmod();
	/**
	 * @ejb.interface-method  view-type="local"
	 * @ejb.persistence column-name="cuser_mod" jdbc-type="VARCHAR"
	 * @ejb.transaction type="Required"
	 */
	public abstract java.lang.String getCuserMod();

}