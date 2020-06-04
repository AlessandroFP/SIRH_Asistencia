/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) Fabian F. Soto Silva<p>
 * Company:      SUNAT<p>
 * @author Fabian F. Soto Silva
 * @version 1.0
 */
package pe.gob.sunat.sp.bean;

import java.io.*;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @stereotype SessionBean
 */
public class BeanT12 implements Serializable {

  private String t12cod_uorgan;
  private String t12descr_uorg;
  private String t12descr_corta;
  private Date t12f_vigenci;
  private Date t12f_baja;
  private String t12cod_nivel;
  private String t12cod_categ;
  private String t12cod_subpr;
  private String t12ind_aplic;
  private String t12cod_anter;
  private String t12ind_estad;

  private String t12cod_jefat;
  private String t12cod_encar;
  private String t12cod_repor;
  private String t12tipo;

  private Date t12f_graba;
  private String t12cod_user;



  private String t12cod_subpro;
  private String t12f_actualiz;
  private String t12h_actualiz;
  private String t12cod_usuario;
  private String t12cod_termin;
  private String t12programa;
  private String t12descr_cargo;
  private String t12cod_local;
  private String t12desc_local;
  private String t12n_piso;
  private String t12n_anexo;
  private String t12anho;

  public BeanT12() {
  }

  public void setT12tipo(String newT12tipo) {
    t12tipo = newT12tipo;
  }
  public String getT12tipo() {
    return t12tipo;
  }

  public void setT12cod_repor(String newT12cod_repor) {
    t12cod_repor = newT12cod_repor;
  }
  public String getT12cod_repor() {
    return t12cod_repor;
  }

  public void setT12cod_encar(String newT12cod_encar) {
    t12cod_encar = newT12cod_encar;
  }
  public String getT12cod_encar() {
    return t12cod_encar;
  }


  public void setT12cod_jefat(String newT12cod_jefat) {
    t12cod_jefat = newT12cod_jefat;
  }
  public String getT12cod_jefat() {
    return t12cod_jefat;
  }

















  public void setT12cod_uorgan(String newT12cod_uorgan) {
    t12cod_uorgan = newT12cod_uorgan;
  }
  public String getT12cod_uorgan() {
    return t12cod_uorgan;
  }

  public void setT12descr_uorg(String newT12descr_uorg) {
    t12descr_uorg = newT12descr_uorg;
  }

  public String getT12descr_uorg() {
    return t12descr_uorg;
  }

  public void setT12descr_corta(String newT12descr_corta) {
    t12descr_corta = newT12descr_corta;
  }

  public String getT12descr_corta() {
    return t12descr_corta;
  }

  public Date getT12f_vigenci(){
    return(t12f_vigenci);
  }

  public void setT12f_vigenci(Date t12f_vigenci){
    this.t12f_vigenci = t12f_vigenci;
  }

  public Date getT12f_baja(){
    return(t12f_baja);
  }

  public void setT12f_baja(Date t12f_baja){
    this.t12f_baja = t12f_baja;
  }

  public String getT12cod_nivel(){
    return(t12cod_nivel);
  }

  public void setT12cod_nivel(String t12cod_nivel){
    this.t12cod_nivel = t12cod_nivel;
  }

  public String getT12cod_categ(){
    return(t12cod_categ);
  }

  public void setT12cod_categ(String t12cod_categ){
    this.t12cod_categ = t12cod_categ;
  }

  public String getT12cod_subpr(){
    return(t12cod_subpr);
  }

  public void setT12cod_subpr(String t12cod_subpr){
    this.t12cod_subpr = t12cod_subpr;
  }

  public void setT12ind_aplic(String newT12ind_aplic) {
    t12ind_aplic = newT12ind_aplic;
  }

  public String getT12ind_aplic() {
    return t12ind_aplic;
  }

  public String getT12cod_anter(){
    return(t12cod_anter);
  }

  public void setT12cod_anter(String t12cod_anter){
    this.t12cod_anter = t12cod_anter;
  }

  public String getT12ind_estad(){
    return(t12ind_estad);
  }

  public void setT12ind_estad(String t12ind_estad){
    this.t12ind_estad = t12ind_estad;
  }

  public Date getT12f_graba(){
    return(t12f_graba);
  }

  public void setT12f_graba(Date t12f_graba){
    this.t12f_graba = t12f_graba;
  }

  public String getT12cod_user(){
    return(t12cod_user);
  }

  public void setT12cod_user(String t12cod_user){
    this.t12cod_user = t12cod_user;
  }

  // Informacion Borrada de las tabla
  public void setT12f_actualiz(String newT12f_actualiz) {
    t12f_actualiz = newT12f_actualiz;
  }
  public String getT12f_actualiz() {
    return t12f_actualiz;
  }
  public void setT12h_actualiz(String newT12h_actualiz) {
    t12h_actualiz = newT12h_actualiz;
  }
  public String getT12h_actualiz() {
    return t12h_actualiz;
  }
  public void setT12cod_usuario(String newT12cod_usuario) {
    t12cod_usuario = newT12cod_usuario;
  }
  public String getT12cod_usuario() {
    return t12cod_usuario;
  }
  public void setT12cod_termin(String newT12cod_termin) {
    t12cod_termin = newT12cod_termin;
  }
  public String getT12cod_termin() {
    return t12cod_termin;
  }
  public void setT12programa(String newT12programa) {
    t12programa = newT12programa;
  }
  public String getT12programa() {
    return t12programa;
  }
  public void setT12descr_cargo(String newT12descr_cargo) {
    t12descr_cargo = newT12descr_cargo;
  }
  public String getT12descr_cargo() {
    return t12descr_cargo;
  }
  public void setT12cod_local(String newT12cod_local) {
    t12cod_local = newT12cod_local;
  }
  public String getT12cod_local() {
    return t12cod_local;
  }
  public void setT12desc_local(String newT12desc_local) {
    t12desc_local = newT12desc_local;
  }
  public String getT12desc_local() {
    return t12desc_local;
  }
  public void setT12n_piso(String newT12n_piso) {
    t12n_piso = newT12n_piso;
  }
  public String getT12n_piso() {
    return t12n_piso;
  }
  public void setT12n_anexo(String newT12n_anexo) {
    t12n_anexo = newT12n_anexo;
  }
  public String getT12n_anexo() {
    return t12n_anexo;
  }
  public void setT12cod_subpro(String newT12cod_subpro) {
    t12cod_subpro = newT12cod_subpro;
  }
  public String getT12cod_subpro() {
    return t12cod_subpro;
  }
  public void setT12anho(String newT12anho) {
    t12anho = newT12anho;
  }
  public String getT12anho() {
    return t12anho;
  }
}