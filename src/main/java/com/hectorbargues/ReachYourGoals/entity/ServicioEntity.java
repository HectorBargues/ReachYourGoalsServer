package com.hectorbargues.ReachYourGoals.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "servicio")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ServicioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    private String codigo;
    private String nombre;
    private Integer existencias;
    private Double precio;
    private Long imagen;
   

    @ManyToOne
    @JoinColumn(name = "id_tiposervicio")
    private TipoServicioEntity tiposervicio;

    @OneToMany(mappedBy = "servicio")
    private List<CarritoEntity> carritos = new ArrayList<>();
    
    @OneToMany(mappedBy = "servicio")
    private List<CompraEntity> compras = new ArrayList<>();

    public ServicioEntity() {
    }

    public ServicioEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getExistencias() {
        return existencias;
    }

    public void setExistencias(Integer existencias) {
        this.existencias = existencias;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Long getImagen() {
        return imagen;
    }

    public void setImagen(Long imagen) {
        this.imagen = imagen;
    }

  

    public int getCarritos() {
        return this.carritos.size();
    }

    public int getCompras() {
        return compras.size();
    }

    public TipoServicioEntity getTiposervicio() {
        return tiposervicio;
    }

    public void setTiposervicio(TipoServicioEntity tiposervicio) {
        this.tiposervicio = tiposervicio;
    }

}
