package pe.gob.sunat.sp.asistencia.ejb;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

import pe.gob.sunat.ejb.EJBEstandarLocalCMP;

/**
 * @ejb.bean name="T132CMP" cmp-version="2.x" description="Entidad de la tabla
 *           t132horacu"
 *           local-jndi-name="ejb/rrhh/cmp/sp/asistencia/t132cmp" type="CMP"
 *           view-type="local" schema="T132Schema"
 * 
 * @ejb.home local-class="pe.gob.sunat.sp.asistencia.ejb.T132CMPHome"
 *           local-extends="javax.ejb.EJBLocalHome"
 * 
 * @ejb.interface local-class="pe.gob.sunat.sp.asistencia.ejb.T132CMPLocal"
 *                local-extends="javax.ejb.EJBLocalObject"
 * 
 * @ejb.transaction type="Required"
 * @ejb.transaction-type type="Container"
 * 
 * @ejb.persistence table-name="t132horacu"
 * @ejb.pk class="pe.gob.sunat.sp.asistencia.ejb.T132CMPPK"
 *         extends="java.lang.Object" implements="java.io.Serializable"
 * 
 * @jboss.container-configuration name="Standard CMP 2.x EntityBean"
 * @jboss.persistence create-table="false" remove-table="false"
 *                    table-name="t132horacu"
 * @jboss.version="3.2"
 * 
 * @weblogic.persistence create-table="false" remove-table="false"
 *                       table-name="t132horacu"
 * @weblogic.cache max-beans-in-cache="300"
 * @weblogic.pool max-beans-in-free-pool="300" initial-beans-in-free-pool="0"
 * @weblogic.delay-database-insert-until ejbCreate
 * 
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * 
 * @author CGARRATT
 */
abstract public class T132CMP extends EJBEstandarLocalCMP implements EntityBean {
	EntityContext entityContext;

	/**
	 * @ejb.create-method view-type="local"
	 * @param codPers
	 * @return
	 * @throws CreateException
	 */
	public T132CMPPK ejbCreate(java.lang.String codPers) throws CreateException {
		setCodPers(codPers);
		return null;
	}

	/**
	 * @ejb.create-method view-type="local"
	 * @param codPers
	 * @param total
	 * @param estado
	 * @param fecha
	 * @param usuario
	 * @return
	 * @throws CreateException
	 */
	public T132CMPPK ejbCreate(String codPers, java.math.BigDecimal total,
			String estado, java.sql.Timestamp fecha, String usuario)
			throws CreateException {

		setCodPers(codPers);
		setTAcum(total);
		setEstId(estado);
		setFcreacion(fecha);
		setCodUser(usuario);
		return null;
	}

	public void ejbPostCreate(java.lang.String codPers) throws CreateException {
		/** @todo Complete this method */
	}

	public void ejbPostCreate(String codPers, java.math.BigDecimal total,
			String estado, java.sql.Timestamp fecha, String usuario)
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
	 * @ejb.persistence column-name="t_acum" jdbc-type="FLOAT"
	 */
	public abstract void setTAcum(java.math.BigDecimal tAcum);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="est_id" jdbc-type="VARCHAR"
	 */
	public abstract void setEstId(java.lang.String estId);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fcreacion" jdbc-type="TIMESTAMP"
	 */
	public abstract void setFcreacion(java.sql.Timestamp fcreacion);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cod_user" jdbc-type="VARCHAR"
	 */
	public abstract void setCodUser(java.lang.String codUser);
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
	 * @ejb.persistence column-name="t_acum" jdbc-type="FLOAT"
	 */
	public abstract java.math.BigDecimal getTAcum();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="est_id" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getEstId();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fcreacion" jdbc-type="TIMESTAMP"
	 */
	public abstract java.sql.Timestamp getFcreacion();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cod_user" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getCodUser();
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