package pe.gob.sunat.personalizer.mail.bean;

import pe.gob.sunat.sol.BeanMensaje;

/**
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author
 * @version 1.0
 */

public class BeanCorreo extends BeanMensaje {

	private static final long serialVersionUID = 1L;

	private String tipo;

	private String buzon_de;

	private String buzon_para;

	private String buzon_cc;

	private String buzon_bcc;

	private String asunto;

	private String mensaje;

	private boolean bd;

	private String parametro;

	private java.util.Vector valores;

	public BeanCorreo() {
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String newTipo) {
		tipo = newTipo;
	}

	public void setBuzon_de(String newBuzon_de) {
		buzon_de = newBuzon_de;
	}

	public String getBuzon_de() {
		return buzon_de;
	}

	public void setBuzon_para(String newBuzon_para) {
		buzon_para = newBuzon_para;
	}

	public String getBuzon_para() {
		return buzon_para;
	}

	public void setBuzon_cc(String newBuzon_cc) {
		buzon_cc = newBuzon_cc;
	}

	public void setBuzon_bcc(String newBuzon_bcc) {
		buzon_bcc = newBuzon_bcc;
	}

	public String getBuzon_cc() {
		return buzon_cc;
	}

	public String getBuzon_bcc() {
		return buzon_bcc;
	}

	public void setAsunto(String newAsunto) {
		asunto = newAsunto;
	}

	public String getAsunto() {
		return asunto;
	}

	public void setMensaje(String newMensaje) {
		mensaje = newMensaje;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setBd(boolean newBd) {
		bd = newBd;
	}

	public boolean isBd() {
		return bd;
	}

	public void setParametro(String newParametro) {
		parametro = newParametro;
	}

	public String getParametro() {
		return parametro;
	}

	public void setValores(java.util.Vector newValores) {
		valores = newValores;
	}

	public java.util.Vector getValores() {
		return valores;
	}
}