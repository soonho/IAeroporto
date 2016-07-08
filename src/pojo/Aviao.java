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
    private Point3D localizacao;
    private Point3D destino;
    private Integer passageiros;
    private Integer tamanho;
    private String situacao;
    private Double velocidade;

    public Aviao(String nome, Point3D localizacao, Point3D destino, Integer tamanho, String situacao, Double velocidade) {
        this.nome = nome;
        this.localizacao = localizacao;
        this.destino = destino;
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
        return localizacao;
    }

    public void setLocalizacao(Point3D localizacao) {
        this.localizacao = localizacao;
    }

    public Point3D getDestino() {
        return destino;
    }

    public void setDestino(Point3D destino) {
        this.destino = destino;
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
}
