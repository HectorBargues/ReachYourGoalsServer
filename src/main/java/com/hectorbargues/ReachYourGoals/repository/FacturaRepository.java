package com.hectorbargues.ReachYourGoals.repository;

import com.hectorbargues.ReachYourGoals.entity.FacturaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FacturaRepository extends JpaRepository<FacturaEntity, Long> {

    @Query(value = "SELECT * FROM factura f WHERE f.id_usuario = ?1", nativeQuery = true)
    Page<FacturaEntity> findByFacturaXUsuario(Long id_usuario, Pageable pageable);

    Page<FacturaEntity> findById(Long idfactura, Pageable pageable);

    @Query(value = "SELECT * FROM factura WHERE id_usuario = ?1 AND (id LIKE  %?2%)",
            nativeQuery = true)
    Page<FacturaEntity> findByUsuarioAndIdFactura(long id_usuario, long id_factura, Pageable oPageable);
    
    

    @Query(value = "SELECT COUNT(*) FROM factura where id_usuario = :id_usuario ", nativeQuery = true)
    Long findByFacturaIdUsuarioCount(Long id_usuario);
}