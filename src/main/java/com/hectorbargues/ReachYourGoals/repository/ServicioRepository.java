package com.hectorbargues.ReachYourGoals.repository;

import com.hectorbargues.ReachYourGoals.entity.ServicioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ServicioRepository extends JpaRepository<ServicioEntity, Long> {

    Page<ServicioEntity> findByNombreIgnoreCaseContainingOrCodigoIgnoreCaseContaining(String nombre, String codigo, Pageable oPageable);

    Page<ServicioEntity> findByTiposervicioId(Long tiposervicio, Pageable oPageable);

    @Query(
            value = "SELECT * FROM servicio WHERE id_tiposervicio = ?1 AND (nombre LIKE  %?2% OR codigo LIKE %?3%)",
            nativeQuery = true)
    Page<ServicioEntity> findByTiposervicioIdAndNombreOrCodigo(long IdTiposervicio, String nombre, String codigo, Pageable oPageable);
}
