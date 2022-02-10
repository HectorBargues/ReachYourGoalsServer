package com.hectorbargues.ReachYourGoals.api;

import java.util.List;
import javax.servlet.http.HttpSession;
import com.hectorbargues.ReachYourGoals.entity.ServicioEntity;
import com.hectorbargues.ReachYourGoals.entity.UsuarioEntity;
import com.hectorbargues.ReachYourGoals.repository.ServicioRepository;
import com.hectorbargues.ReachYourGoals.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
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
@RequestMapping("/servicio")
public class ServicioController {

    @Autowired
    ServicioRepository oServicioRepository;

    @Autowired
    HttpSession oHttpSession;

    @Autowired
    ServicioService oServicioService;

    // /producto/3
    @GetMapping("/{id}")
    public ResponseEntity<ServicioEntity> get(@PathVariable(value = "id") Long id) {

        if (oServicioRepository.existsById(id)) {
            return new ResponseEntity<ServicioEntity>(oServicioRepository.getById(id), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

//    // producto/all
//    @GetMapping("/all")
//    public ResponseEntity<List> getall() {
//        return new ResponseEntity<List>(oProductoRepository.findAll(), HttpStatus.OK);
//    }
    // producto/count
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<Long>(oServicioRepository.count(), HttpStatus.OK);
    }

    // /producto?page=0&size=10&sort=precio,desc&filter=verde&tipoproducto=2
    @GetMapping("")
    public ResponseEntity<Page<ServicioEntity>> getPage(@PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC) Pageable oPageable,
           @RequestParam(name = "filter", required = false) String strFilter, @RequestParam(name = "tiposervicio", required = false) Long lTipoServicio) {
        Page<ServicioEntity> oPage = null;
        if (lTipoServicio != null) {
            if (strFilter != null) {
                oPage = oServicioRepository.findByTiposervicioIdAndNombreOrCodigo(lTipoServicio, strFilter, strFilter, oPageable);
            } else {
                oPage = oServicioRepository.findByTiposervicioId(lTipoServicio, oPageable);
            }
        } else {
            if (strFilter != null) {
                oPage = oServicioRepository.findByNombreIgnoreCaseContainingOrCodigoIgnoreCaseContaining(strFilter, strFilter, oPageable);
            } else {
                oPage = oServicioRepository.findAll(oPageable);
            }
        }
        return new ResponseEntity<Page<ServicioEntity>>(oPage, HttpStatus.OK);
    }

    //CREAR
    // producto/
    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody ServicioEntity oServicioEntity) {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity.getTipousuario().getId() == 1) {
            if (oUsuarioEntity == null) {
                return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
            } else {
                oServicioEntity.setId(null);

                return new ResponseEntity<ServicioEntity>(oServicioRepository.save(oServicioEntity), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
        }

    }

    //UPDATE
    //producto/
    @PutMapping("/")
    public ResponseEntity<?> update(@RequestBody ServicioEntity oServicioEntity) {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity.getTipousuario().getId() == 1) {
            if (oUsuarioEntity == null) {
                return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
            } else {
                if (oServicioRepository.existsById(oServicioEntity.getId())) {
                    return new ResponseEntity<ServicioEntity>(oServicioRepository.save(oServicioEntity), HttpStatus.OK);
                } else {
                    return new ResponseEntity<Long>(0L, HttpStatus.NOT_MODIFIED);
                }
            }
        } else {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);

        }
    }

    // producto/id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity.getTipousuario().getId() == 1) {

            if (oUsuarioEntity == null) {
                return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
            } else {
                if (oServicioRepository.existsById(id)) {
                    oServicioRepository.deleteById(id);
                    if (oServicioRepository.existsById(id)) {
                        return new ResponseEntity<Long>(0L, HttpStatus.NOT_MODIFIED);
                    } else {
                        return new ResponseEntity<Long>(id, HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<Long>(0L, HttpStatus.NOT_MODIFIED);
                }
            }
        } else {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);

        }
    }

    @PostMapping("/generate/{amount}")
    public ResponseEntity<?> genera(@PathVariable(value = "amount") int amount) {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");

        if (oUsuarioEntity == null) {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
        } else {
            if (oUsuarioEntity.getTipousuario() == null) {
                return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
            } else {
                if (oUsuarioEntity.getTipousuario().getId() == 1) {
                    if (oUsuarioEntity == null) {
                        return new ResponseEntity<>(0L, HttpStatus.UNAUTHORIZED);
                    } else {
                        for (int i = 0; i < amount; i++) {
                            ServicioEntity oServicioEntity;
                            oServicioEntity = oServicioService.generateRandomServicio();
                            oServicioRepository.save(oServicioEntity);
                        }
                        return new ResponseEntity<>(oServicioRepository.count(), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(0L, HttpStatus.UNAUTHORIZED);
                }
            }
        }
    }

}
