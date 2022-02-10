package com.hectorbargues.ReachYourGoals.repository;

import java.util.List;
import javax.transaction.Transactional;
import com.hectorbargues.ReachYourGoals.entity.CarritoEntity;
import com.hectorbargues.ReachYourGoals.entity.ServicioEntity;
import com.hectorbargues.ReachYourGoals.entity.UsuarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoRepository extends JpaRepository<CarritoEntity, Long> {

	Page<CarritoEntity> findByServicioId(Long id_Servicio, Pageable oPageable);

	Page<CarritoEntity> findByUsuarioId(Long id_Usuario, Pageable oPageable);
        
	List<CarritoEntity> findByUsuarioId(Long id_Usuario);        
        
	long countByUsuarioId(Long id_Usuario);
                
	Page<CarritoEntity> findAllByUsuario(UsuarioEntity usuario, Pageable oPageable);

	Page<CarritoEntity> findAllByIdIgnoreCaseContainingOrCantidadIgnoreCaseContainingOrPrecioIgnoreCaseContainingOrServicioIgnoreCaseContainingOrUsuarioIgnoreCaseContaining(
			String id, String cantidad, String precio, String servicio, String usuario, Pageable oPageable);

	CarritoEntity findByUsuarioAndServicio(UsuarioEntity usuario, ServicioEntity servicio);

	@Transactional
	int deleteAllByUsuario(UsuarioEntity usuario);

	@Transactional
	int deleteByUsuarioAndServicio(UsuarioEntity usuario, ServicioEntity servicio);

}
