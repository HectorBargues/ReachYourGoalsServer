package com.hectorbargues.ReachYourGoals.repository;

import com.hectorbargues.ReachYourGoals.entity.TipoUsuarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoUsuarioRepository extends JpaRepository<TipoUsuarioEntity, Long> {

	public Page<TipoUsuarioEntity> findByNombreIgnoreCaseContaining(String strFilter, Pageable oPageable);

}
