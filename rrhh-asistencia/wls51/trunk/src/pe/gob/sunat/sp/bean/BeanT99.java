
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

public class BeanT99 implements Serializable {

  public BeanT99() {
  }

  void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    ois.defaultReadObject();
  }

  void writeObject(ObjectOutputStream oos) throws IOException {
    oos.defaultWriteObject();
  }

  public void setT99cod_tab(String newT99cod_tab) {
    t99cod_tab = newT99cod_tab;
  }

  public String getT99cod_tab() {
    return t99cod_tab;
  }

  public void setT99tip_desc(String newT99tip_desc) {
    t99tip_desc = newT99tip_desc;
  }

  public String getT99tip_desc() {
    return t99tip_desc;
  }

  public void setT99codigo(String newT99codigo) {
    t99codigo = newT99codigo;
  }

  public String getT99codigo() {
    return t99codigo;
  }

  public void setT99descrip(String newT99descrip) {
    t99descrip = newT99descrip;
  }

  public String getT99descrip() {
    return t99descrip;
  }

  public void setT99abrev(String newT99abrev) {
    t99abrev = newT99abrev;
  }

  public String getT99abrev() {
    return t99abrev;
  }

  public void setT99siglas(String newT99siglas) {
    t99siglas = newT99siglas;
  }

  public String getT99siglas() {
    return t99siglas;
  }

  public void setT99tipo(String newT99tipo) {
    t99tipo = newT99tipo;
  }

  public String getT99tipo() {
    return t99tipo;
  }

  public void setT99estado(String newT99estado) {
    t99estado = newT99estado;
  }

  public String getT99estado() {
    return t99estado;
  }

  public void setT99cod_user(String newT99cod_user) {
    t99cod_user = newT99cod_user;
  }

  public String getT99cod_user() {
    return t99cod_user;
  }

  public void setT99_factual(String newT99_factual) {
    t99_factual = newT99_factual;
  }

  public String getT99_factual() {
    return t99_factual;
  }
  private String t99cod_tab;
  private String t99tip_desc;
  private String t99codigo;
  private String t99descrip;
  private String t99abrev;
  private String t99siglas;
  private String t99tipo;
  private String t99estado;
  private String t99cod_user;
  private String t99_factual;
}