package com.hectorbargues.ReachYourGoals.api;

import java.util.ArrayList;
import javax.servlet.http.HttpSession;
import com.hectorbargues.ReachYourGoals.entity.CarritoEntity;
import com.hectorbargues.ReachYourGoals.entity.ServicioEntity;
import com.hectorbargues.ReachYourGoals.entity.UsuarioEntity;
import com.hectorbargues.ReachYourGoals.helper.TipoUsuarioHelper;
import com.hectorbargues.ReachYourGoals.repository.CarritoRepository;
import com.hectorbargues.ReachYourGoals.repository.ServicioRepository;
import com.hectorbargues.ReachYourGoals.repository.UsuarioRepository;
import com.hectorbargues.ReachYourGoals.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    HttpSession oHttpSession;

    @Autowired
    CarritoRepository oCarritoRepository;

    @Autowired
    UsuarioRepository oUsuarioRepository;

    @Autowired
    ServicioRepository oServicioRepository;

    @Autowired
    CarritoService oCarritoService;

    @GetMapping("")
    public ResponseEntity<Page<CarritoEntity>> getPage(
            @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC) Pageable oPageable,
            @RequestParam(name = "idservicio", required = false) Long id_servicio,
            @RequestParam(name = "idusuario", required = false) Long id_usuario) {
        UsuarioEntity oSessionUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        Page<CarritoEntity> oPage = null;

        if (oSessionUsuarioEntity != null
                && oSessionUsuarioEntity.getTipousuario().getId() == TipoUsuarioHelper.ADMIN) {

            if (id_servicio != null) {
                oPage = oCarritoRepository.findByServicioId(id_servicio, oPageable);
            } else if (id_usuario != null) {
                oPage = oCarritoRepository.findByUsuarioId(id_usuario, oPageable);
            } else {
                oPage = oCarritoRepository.findAll(oPageable);
            }
        } else {
            oPage = oCarritoRepository.findByUsuarioId(oSessionUsuarioEntity.getId(), oPageable);
        }

        return new ResponseEntity<>(oPage, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable(value = "id") Long id) {
        UsuarioEntity oSessionUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");

        CarritoEntity carritoEntity = oCarritoRepository.getById(id);

        if (oSessionUsuarioEntity == null || oSessionUsuarioEntity.getTipousuario().getId() != TipoUsuarioHelper.ADMIN
                && oSessionUsuarioEntity.getId() != carritoEntity.getUsuario().getId()) {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<CarritoEntity>(carritoEntity, HttpStatus.OK);
    }

    @GetMapping("/admin/filter/{filter}")
    public ResponseEntity<Page<CarritoEntity>> getAdmin(@PathVariable(value = "filter") String filter,
            @PageableDefault(page = 0, size = 10, direction = Sort.Direction.ASC) Pageable oPageable) {
        UsuarioEntity oSessionUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        Page<CarritoEntity> oCarritoEntity;
        try {
            oSessionUsuarioEntity = oUsuarioRepository.findById(oSessionUsuarioEntity.getId()).get();
        } catch (Exception ex) {
            oSessionUsuarioEntity = null;
        }
        if (oSessionUsuarioEntity != null && oSessionUsuarioEntity.getTipousuario().getId() == 1) {
            oCarritoEntity = oCarritoRepository
                    .findAllByIdIgnoreCaseContainingOrCantidadIgnoreCaseContainingOrPrecioIgnoreCaseContainingOrServicioIgnoreCaseContainingOrUsuarioIgnoreCaseContaining(
                            filter, filter, filter, filter, filter, oPageable);
            return new ResponseEntity<Page<CarritoEntity>>(oCarritoEntity, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping
    public ResponseEntity<?> newOne(@RequestBody CarritoEntity carritoEntity) {
        UsuarioEntity oSessionUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");

        if (carritoEntity.getId() != null) {
            return new ResponseEntity<Long>(0L, HttpStatus.METHOD_NOT_ALLOWED);
        }

        if (oSessionUsuarioEntity == null || oSessionUsuarioEntity.getTipousuario().getId() != TipoUsuarioHelper.ADMIN
                && oSessionUsuarioEntity.getId() != carritoEntity.getUsuario().getId()) {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<CarritoEntity>(oCarritoRepository.save(carritoEntity), HttpStatus.OK);
    }

    @PostMapping("/{id}/{amount}")
    public ResponseEntity<?> add(@PathVariable(value = "id") long id, @PathVariable(value = "amount") int amount) {
        UsuarioEntity oSessionUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        CarritoEntity oCarritoEntity = new CarritoEntity();
        CarritoEntity oCarritoEntityServicio;
        ServicioEntity oServicio = new ServicioEntity();
        try {
            oSessionUsuarioEntity = oUsuarioRepository.findById(oSessionUsuarioEntity.getId()).get();
        } catch (Exception ex) {
            oSessionUsuarioEntity = null;
        }
        if (oSessionUsuarioEntity != null) {
            oServicio = oServicioRepository.getById(id);
            oCarritoEntityServicio = oCarritoRepository.findByUsuarioAndServicio(oSessionUsuarioEntity, oServicio);
            oCarritoEntity.setServicio(oServicio);
            oCarritoEntity.setUsuario(oSessionUsuarioEntity);
            if (oCarritoEntityServicio == null) {
                oCarritoEntity.setId(null);
                oCarritoEntity.setCantidad(amount);
            } else {
                oCarritoEntity.setId(oCarritoEntityServicio.getId());
                oCarritoEntity.setCantidad(oCarritoEntityServicio.getCantidad() + amount);
            }
            oCarritoRepository.save(oCarritoEntity);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/admin/{id}/{amount}/{user}")
    public ResponseEntity<?> addAdmin(@PathVariable(value = "id") long id, @PathVariable(value = "amount") int amount,
            @PathVariable(value = "user") long user) {
        UsuarioEntity oSessionUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        CarritoEntity oCarritoEntity = new CarritoEntity();
        CarritoEntity oCarritoEntityServicio;
        ServicioEntity oServicio;
        UsuarioEntity oUsuario;
        try {
            oSessionUsuarioEntity = oUsuarioRepository.findById(oSessionUsuarioEntity.getId()).get();
        } catch (Exception ex) {
            oSessionUsuarioEntity = null;
        }
        if (oSessionUsuarioEntity != null && oSessionUsuarioEntity.getTipousuario().getId() == 1) {
            oServicio = oServicioRepository.getById(id);
            oUsuario = oUsuarioRepository.getById(user);
            oCarritoEntityServicio = oCarritoRepository.findByUsuarioAndServicio(oUsuario, oServicio);
            oCarritoEntity.setServicio(oServicio);
            oCarritoEntity.setUsuario(oUsuario);
            if (oCarritoEntityServicio == null) {
                oCarritoEntity.setId(null);
                oCarritoEntity.setCantidad(amount);
            } else {
                oCarritoEntity.setId(oCarritoEntityServicio.getId());
                oCarritoEntity.setCantidad(oCarritoEntityServicio.getCantidad() + amount);
            }
            oCarritoRepository.save(oCarritoEntity);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    // @PostMapping("/random/{rows}")
    // public ResponseEntity<?> random(@PathVariable(value = "rows") int rows) {
    // try {
    // ArrayList<CarritoEntity> carritos = oCarritoService.generate(rows);
    // for (int i = 0; i < carritos.size(); i++) {
    // oCarritoRepository.save(carritos.get(i));
    // }
    // return new ResponseEntity<>(carritos, HttpStatus.OK);
    // } catch (Exception e) {
    // return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    // }
    // }
    @PutMapping("/comprar")
    public ResponseEntity<?> comprarCarrito() {
        UsuarioEntity oSessionUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oSessionUsuarioEntity != null) {
            try {
                oCarritoService.compra();
            } catch (Exception e) {
                return new ResponseEntity<>(0L, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<Long>(0L, HttpStatus.OK);
        } else {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
        }

    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody CarritoEntity carritoEntity) {
        UsuarioEntity oSessionUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");

        if (carritoEntity.getId() == null || !oCarritoRepository.existsById(carritoEntity.getId())) {
            return new ResponseEntity<Long>(0L, HttpStatus.METHOD_NOT_ALLOWED);
        }

        if (oSessionUsuarioEntity == null || oSessionUsuarioEntity.getTipousuario().getId() != TipoUsuarioHelper.ADMIN
                && oSessionUsuarioEntity.getId() != carritoEntity.getUsuario().getId()) {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<CarritoEntity>(oCarritoRepository.save(carritoEntity), HttpStatus.OK);
    }

    @DeleteMapping("")
    public ResponseEntity<?> delete() {
        UsuarioEntity oSessionUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        try {
            oSessionUsuarioEntity = oUsuarioRepository.findById(oSessionUsuarioEntity.getId()).get();
        } catch (Exception ex) {
            oSessionUsuarioEntity = null;
        }
        if (oSessionUsuarioEntity != null) {
            oCarritoRepository.deleteAllByUsuario(oSessionUsuarioEntity);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable(value = "id") Long id) {
        UsuarioEntity oSessionUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");

        CarritoEntity carritoEntity = oCarritoRepository.getById(id);

        if (carritoEntity == null) {
            return new ResponseEntity<Long>(0L, HttpStatus.METHOD_NOT_ALLOWED);
        }

        if (oSessionUsuarioEntity == null || oSessionUsuarioEntity.getTipousuario().getId() != TipoUsuarioHelper.ADMIN
                && oSessionUsuarioEntity.getId() != carritoEntity.getUsuario().getId()) {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
        }

        oCarritoRepository.deleteById(id);

        return new ResponseEntity<Long>(1L, HttpStatus.OK);
    }

    @DeleteMapping("/admin/{user}")
    public ResponseEntity<?> deleteAdmin(@PathVariable(value = "user") long user) {
        UsuarioEntity oSessionUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        UsuarioEntity oUsuario;
        try {
            oSessionUsuarioEntity = oUsuarioRepository.findById(oSessionUsuarioEntity.getId()).get();
        } catch (Exception ex) {
            oSessionUsuarioEntity = null;
        }
        if (oSessionUsuarioEntity != null && oSessionUsuarioEntity.getTipousuario().getId() == 1) {
            oUsuario = oUsuarioRepository.getById(user);
            oCarritoRepository.deleteAllByUsuario(oUsuario);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{id_servicio}/{amount}")
    public ResponseEntity<?> deleteOne(@PathVariable(value = "id_servicio") long id_servicio,
            @PathVariable(value = "amount") int amount) {
        UsuarioEntity oSessionUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        CarritoEntity oCarritoEntity;
        ServicioEntity oServicio;
        try {
            oSessionUsuarioEntity = oUsuarioRepository.findById(oSessionUsuarioEntity.getId()).get();
        } catch (Exception ex) {
            oSessionUsuarioEntity = null;
        }
        if (oSessionUsuarioEntity != null) {
            oServicio = oServicioRepository.getById(id_servicio);
            oCarritoEntity = oCarritoRepository.findByUsuarioAndServicio(oSessionUsuarioEntity, oServicio);
            if (amount >= oCarritoEntity.getCantidad()) {
                oCarritoRepository.deleteByUsuarioAndServicio(oSessionUsuarioEntity, oServicio);
            } else {
                oCarritoEntity.setCantidad(oCarritoEntity.getCantidad() - amount);
                oCarritoRepository.save(oCarritoEntity);
            }
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/admin/{id_servicio}/{amount}/{user}")
    public ResponseEntity<?> deleteOneAdmin(@PathVariable(value = "id_servicio") long id_servicio,
            @PathVariable(value = "amount") int amount, @PathVariable(value = "user") long user) {
        UsuarioEntity oSessionUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        CarritoEntity oCarritoEntity = new CarritoEntity();
        ServicioEntity oServicio;
        UsuarioEntity oUsuario;
        try {
            oSessionUsuarioEntity = oUsuarioRepository.findById(oSessionUsuarioEntity.getId()).get();
        } catch (Exception ex) {
            oSessionUsuarioEntity = null;
        }
        if (oSessionUsuarioEntity != null && oSessionUsuarioEntity.getTipousuario().getId() == 1) {
            oUsuario = oUsuarioRepository.getById(user);
            oServicio = oServicioRepository.getById(id_servicio);
            oCarritoEntity = oCarritoRepository.findByUsuarioAndServicio(oUsuario, oServicio);
            if (amount >= oCarritoEntity.getCantidad()) {
                oCarritoRepository.deleteByUsuarioAndServicio(oUsuario, oServicio);
            } else {
                oCarritoEntity.setCantidad(oCarritoEntity.getCantidad() - amount);
                oCarritoRepository.save(oCarritoEntity);
            }
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else if (oUsuarioEntity.getTipousuario().getId() == 1) {
            return new ResponseEntity<Long>(oCarritoRepository.count(), HttpStatus.OK);
        } else {
            return new ResponseEntity<Long>(oCarritoRepository.countByUsuarioId(oUsuarioEntity.getId()), HttpStatus.OK);
        }
    }

    @PostMapping("/generate/{amount}")
    public ResponseEntity<?> generate(@PathVariable(value = "amount") int amount) {
        try {
            ArrayList<CarritoEntity> carritos = oCarritoService.generate(amount);
            for (int i = 0; i < carritos.size(); i++) {
                oCarritoRepository.save(carritos.get(i));
            }
            return new ResponseEntity<>(oCarritoRepository.count(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
