package com.hectorbargues.ReachYourGoals.repository;

import com.hectorbargues.ReachYourGoals.entity.UsuarioEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

	UsuarioEntity findByLoginAndPassword(String login, String password);

	@Query(value = "select * from usuario where id_tipousuario = ?1 and (nombre like %?2% or dni like %?3% or apellidos like %?4%)", nativeQuery = true)
	Page<UsuarioEntity> findByTipousuarioIdAndNombreIgnoreCaseContainingOrDniIgnoreCaseContainingOrApellidosIgnoreCaseContaining(
			Long filtertype, String nombre, String dni, String apellidos,Pageable oPageable);

	Page<UsuarioEntity> findByNombreIgnoreCaseContainingOrDniIgnoreCaseContainingOrApellidosIgnoreCaseContaining(
			String nombre, String dni, String apellidos,Pageable oPageable);
}
