/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojo;

import java.io.Serializable;
import java.util.Objects;
import javafx.geometry.Point3D;

/**
 *
 * @author georg
 */
public class Aviao implements Serializable {

    private String nome;
    private String empresa;
    private Double xLocalizacao;
    private Double yLocalizacao;
    private Double zLocalizacao;
    private Double xDestino;
    private Double yDestino;
    private Double zDestino;
    private Integer passageiros;
    private Integer tamanho;
    private String situacao;
    private Double velocidade;

    public Aviao(String nome, Point3D localizacao, Point3D destino, Integer tamanho, String situacao, Double velocidade) {
        this.nome = nome;
        this.setLocalizacao(localizacao);
        this.setDestino(destino);
        this.tamanho = tamanho;
        this.situacao = situacao;
        this.velocidade = velocidade;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.nome);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Aviao other = (Aviao) obj;
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        return true;
    }

    public Double getxLocalizacao() {
        return xLocalizacao;
    }

    public void setxLocalizacao(Double xLocalizacao) {
        this.xLocalizacao = xLocalizacao;
    }

    public Double getyLocalizacao() {
        return yLocalizacao;
    }

    public void setyLocalizacao(Double yLocalizacao) {
        this.yLocalizacao = yLocalizacao;
    }

    public Double getzLocalizacao() {
        return zLocalizacao;
    }

    public void setzLocalizacao(Double zLocalizacao) {
        this.zLocalizacao = zLocalizacao;
    }

    public Double getxDestino() {
        return xDestino;
    }

    public void setxDestino(Double xDestino) {
        this.xDestino = xDestino;
    }

    public Double getyDestino() {
        return yDestino;
    }

    public void setyDestino(Double yDestino) {
        this.yDestino = yDestino;
    }

    public Double getzDestino() {
        return zDestino;
    }

    public void setzDestino(Double zDestino) {
        this.zDestino = zDestino;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Point3D getLocalizacao() {
        return new Point3D(xLocalizacao, yLocalizacao, zLocalizacao);
    }

    public void setLocalizacao(Point3D localizacao) {
        this.xLocalizacao = localizacao.getX();
        this.yLocalizacao = localizacao.getY();
        this.zLocalizacao = localizacao.getZ();
    }

    public Point3D getDestino() {
        return new Point3D(xDestino, yDestino, zDestino);
    }

    public void setDestino(Point3D destino) {
        this.xDestino = destino.getX();
        this.yDestino = destino.getY();
        this.zDestino = destino.getZ();
    }

    public Integer getPassageiros() {
        return passageiros;
    }

    public void setPassageiros(Integer passageiros) {
        this.passageiros = passageiros;
    }

    public Integer getTamanho() {
        return tamanho;
    }

    public void setTamanho(Integer tamanho) {
        this.tamanho = tamanho;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Double getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(Double velocidade) {
        this.velocidade = velocidade;
    }

    public String stringfy() {
        return this.xLocalizacao + ":"
                + this.yLocalizacao + ":"
                + this.zLocalizacao + ":"
                + this.xDestino + ":"
                + this.yDestino + ":"
                + this.zDestino + ":"
                + this.tamanho + ":"
                + this.situacao + ":"
                + this.velocidade;
    }
}
