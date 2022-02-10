package com.hectorbargues.ReachYourGoals.repository;

import com.hectorbargues.ReachYourGoals.entity.TipoServicioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoServicioRepository extends JpaRepository<TipoServicioEntity, Long> {    

    public Page<TipoServicioEntity> findByNombreIgnoreCaseContaining(String strFilter, Pageable oPageable);

    public Page<TipoServicioEntity> findById(Long longTipoServicio, Pageable oPageable);

    public Page<TipoServicioEntity> findByIdAndNombreIgnoreCaseContaining(Long longTipoServicio, String strFilter, Pageable oPageable);

}
