package com.bank.core.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Clase base para todas las entidades.
 * Contiene campos comunes como ID, fechas de creación y actualización.
 *
 * @MappedSuperclass: Indica que esta clase no es una entidad,
 *                    pero sus campos se heredan a las entidades hijas.
 */
@MappedSuperclass
public abstract class BaseEntity {

    /**
     * ID único de la entidad.
     * - @Id: Marca la clave primaria
     * - @GeneratedValue: Genera automáticamente el valor
     * - strategy = GenerationType.UUID: Usa UUID en lugar de autoincrement
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Fecha de creación.
     * - @Column(updatable = false): No se puede modificar después de crear
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha de última actualización.
     * - @Column: Se actualiza automáticamente
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Constructor vacío requerido por JPA
     */
    protected BaseEntity() {
    }

    /**
     * Constructor con parámetros
     */
    protected BaseEntity(String id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Método que se ejecuta ANTES de persistir (guardar) la entidad
     * por primera vez.
     *
     * @PrePersist: Anotación JPA para eventos de ciclo de vida
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Método que se ejecuta ANTES de actualizar la entidad
     *
     * @PreUpdate: Anotación JPA para eventos de ciclo de vida
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===== GETTERS Y SETTERS =====

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ===== EQUALS Y HASHCODE =====
    // Usamos solo ID para comparar entidades

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}