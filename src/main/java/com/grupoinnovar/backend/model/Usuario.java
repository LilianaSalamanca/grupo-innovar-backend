package com.grupoinnovar.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "usuarios",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuario_email", columnNames = "email")
    }
)
public class Usuario {

    public enum TipoUsuario {
        PUBLICO,
        MAYORISTA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 80, nullable = false)
    private String nombre;

    @Column(length = 80)
    private String apellido;

    @Column(length = 150, nullable = false)
    private String email;

    /**
     * Hash de password (BCrypt, Argon2, etc.). Si el usuario se autentica por OAuth
     * y no tiene password local, puede ser null.
     */
    @Column(name = "password_hash", length = 200)
    private String passwordHash;

    @Column(length = 30)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", length = 20, nullable = false)
    private TipoUsuario tipoUsuario = TipoUsuario.PUBLICO;

    @Column(length = 200)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 100)
    private String departamento;

    @Column(length = 20)
    private String codigoPostal;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    /**
     * Si el usuario no se registró (solo compra rápida), se marca como invitado.
     */
    @Column(name = "invitado", nullable = false)
    private boolean invitado = false;

    @Column(nullable = false)
    private boolean activo = true;

    // ---- Hooks ----
    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }

    // ---- Getters y Setters ----
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public boolean isInvitado() {
        return invitado;
    }

    public void setInvitado(boolean invitado) {
        this.invitado = invitado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}