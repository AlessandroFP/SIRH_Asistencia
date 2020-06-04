package pe.gob.sunat.sp.asistencia.ejb;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

import pe.gob.sunat.ejb.EJBEstandarLocalCMP;

/**
 * @ejb.bean name="T1281CMP" cmp-version="2.x" description="Entidad de la tabla
 *           t1281vacaciones_c"
 *           local-jndi-name="ejb/rrhh/cmp/sp/asistencia/t1281cmp" type="CMP"
 *           view-type="local" schema="T1281Schema"
 * 
 * @ejb.home local-class="pe.gob.sunat.sp.asistencia.ejb.T1281CMPHome"
 *           local-extends="javax.ejb.EJBLocalHome"
 * 
 * @ejb.interface local-class="pe.gob.sunat.sp.asistencia.ejb.T1281CMPLocal"
 *                local-extends="javax.ejb.EJBLocalObject"
 * 
 * @ejb.transaction type="Required"
 * @ejb.transaction-type type="Container"
 * 
 * @ejb.persistence table-name="t1281vacaciones_c"
 * @ejb.pk class="pe.gob.sunat.sp.asistencia.ejb.T1281CMPPK"
 *         extends="java.lang.Object" implements="java.io.Serializable"
 * 
 * @jboss.container-configuration name="Standard CMP 2.x EntityBean"
 * @jboss.persistence create-table="false" remove-table="false"
 *                    table-name="t1281vacaciones_c"
 * @jboss.version="3.2"
 * 
 * @weblogic.persistence create-table="false" remove-table="false"
 *                       table-name="t1281vacaciones_c"
 * @weblogic.cache max-beans-in-cache="1000"
 * @weblogic.pool max-beans-in-free-pool="1000" initial-beans-in-free-pool="0"
 * @weblogic.delay-database-insert-until ejbCreate
 * 
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * 
 * @author CGARRATT
 */
abstract public class T1281CMP extends EJBEstandarLocalCMP implements
		EntityBean {

	EntityContext entityContext;

	/**
	 * @ejb.create-method view-type="local"
	 * @param codPers
	 * @param anno
	 * @return
	 * @throws CreateException
	 */
	public T1281CMPPK ejbCreate(java.lang.String codPers, java.lang.String anno)
			throws CreateException {
		setCodPers(codPers);
		setAnno(anno);
		return null;
	}

	public void ejbPostCreate(java.lang.String codPers, java.lang.String anno)
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
	 * @ejb.persistence column-name="anno" jdbc-type="VARCHAR"
	 */
	public abstract void setAnno(java.lang.String anno);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="dias" jdbc-type="INTEGER"
	 */
	public abstract void setDias(java.lang.Integer dias);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="saldo" jdbc-type="INTEGER"
	 */
	public abstract void setSaldo(java.lang.Integer saldo);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="saldo_temp" jdbc-type="INTEGER"
	 */
	public abstract void setSaldoTemp(java.lang.Integer saldoTemp);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fcreacion" jdbc-type="TIMESTAMP"
	 */
	public abstract void setFcreacion(java.sql.Timestamp fcreacion);
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
	 * @ejb.persistence column-name="cuser_crea" jdbc-type="VARCHAR"
	 */
	public abstract void setCuserCrea(java.lang.String cuserCrea);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cod_pers" jdbc-type="VARCHAR"
	 * @ejb.pk-field
	 */
	public abstract java.lang.String getCodPers();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="anno" jdbc-type="VARCHAR"
	 * @ejb.pk-field
	 */
	public abstract java.lang.String getAnno();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="dias" jdbc-type="INTEGER"
	 */
	public abstract java.lang.Integer getDias();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="saldo" jdbc-type="INTEGER"
	 */
	public abstract java.lang.Integer getSaldo();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="saldo_temp" jdbc-type="INTEGER"
	 */
	public abstract java.lang.Integer getSaldoTemp();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fcreacion" jdbc-type="TIMESTAMP"
	 */
	public abstract java.sql.Timestamp getFcreacion();
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
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cuser_crea" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getCuserCrea();
	
}