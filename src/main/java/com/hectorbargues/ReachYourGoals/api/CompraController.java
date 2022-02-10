package com.hectorbargues.ReachYourGoals.api;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import com.hectorbargues.ReachYourGoals.entity.CompraEntity;
import com.hectorbargues.ReachYourGoals.entity.UsuarioEntity;
import com.hectorbargues.ReachYourGoals.repository.CompraRepository;
import com.hectorbargues.ReachYourGoals.repository.FacturaRepository;
import com.hectorbargues.ReachYourGoals.service.CompraService;
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
@RequestMapping("/compra")
public class CompraController {

    @Autowired
    HttpSession oHttpSession;

    @Autowired
    CompraRepository oCompraRepository;

    @Autowired
    FacturaRepository oFacturaRepository;

    @Autowired
    CompraService oCompraService;

    @GetMapping("/{id}")
    public ResponseEntity<CompraEntity> view(@PathVariable(value = "id") Long id) {
        CompraEntity oCompraEntity = oCompraRepository.getById(id);
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else if (oUsuarioEntity.getTipousuario().getId() == 1) {
            if (oCompraRepository.existsById(id)) {
                return new ResponseEntity<CompraEntity>(oCompraRepository.getById(id), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else if (oCompraRepository.existsById(id) && oUsuarioEntity.getId() == oCompraEntity.getFactura().getUsuario().getId()) {

            return new ResponseEntity<CompraEntity>(oCompraRepository.findByCompraIdUsuarioView(oUsuarioEntity.getId(), oCompraRepository.getById(id).getId()), HttpStatus.OK);

        } else if (oCompraRepository.existsById(id) && oUsuarioEntity.getId() != oCompraEntity.getFactura().getUsuario().getId()) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        CompraEntity oCompraEntity = null;
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else if (oUsuarioEntity.getTipousuario().getId() == 1) {

            return new ResponseEntity<Long>(oCompraRepository.count(), HttpStatus.OK);

        } else {

            return new ResponseEntity<Long>(oCompraRepository.findByCompraIdUsuarioCount(oUsuarioEntity.getId()), HttpStatus.OK);
        }

    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CompraEntity oCompraEntity) {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity == null) {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
        } else {
            if (oUsuarioEntity.getTipousuario().getId() == 1) {
                oCompraEntity.setId(null);
                return new ResponseEntity<CompraEntity>(oCompraRepository.save(oCompraEntity), HttpStatus.OK);
            } else {
                return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @PutMapping("")
    public ResponseEntity<?> update(@RequestBody CompraEntity oCompraEntity) {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity == null) {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
        } else {
            if (oUsuarioEntity.getTipousuario().getId() == 1) {
                if (oCompraRepository.existsById(oCompraEntity.getId())) {
                    return new ResponseEntity<CompraEntity>(oCompraRepository.save(oCompraEntity), HttpStatus.OK);
                } else {
                    return new ResponseEntity<Long>(0L, HttpStatus.NOT_MODIFIED);
                }
            } else {
                return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity == null) {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
        } else {
            if (oUsuarioEntity.getTipousuario().getId() == 1) {

                if (oCompraRepository.existsById(id)) {
                    oCompraRepository.deleteById(id);
                    if (oCompraRepository.existsById(id)) {
                        return new ResponseEntity<Long>(0L, HttpStatus.NOT_MODIFIED);
                    } else {
                        return new ResponseEntity<Long>(id, HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<Long>(0L, HttpStatus.NOT_MODIFIED);
                }
            } else {
                return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
            }
        }
    }

//    @GetMapping("/page")
//    public ResponseEntity<Page<CompraEntity>> getPage(@PageableDefault(page = 0, size = 10, direction = Sort.Direction.ASC) Pageable oPageable) {
//
//        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
//        if (oUsuarioEntity == null) {
//            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
//        } else if (oUsuarioEntity.getTipousuario().getId() == 1) {
//            Page<CompraEntity> oPage = null;
//            oPage = oCompraRepository.findAll(oPageable);
//            return new ResponseEntity<>(oPage, HttpStatus.OK);
//        } else {
//            Page<CompraEntity> oPage = null;
//            oPage = oCompraRepository.findByCompraIdUsuarioPage(oUsuarioEntity.getId(), oPageable);
//            return new ResponseEntity<>(oPage, HttpStatus.OK);
//        }
//    }
    @GetMapping("")
    public ResponseEntity<?> getPage(@PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC) Pageable oPageable,
            @RequestParam(name = "filter", required = false) String strFilter, @RequestParam(name = "factura", required = false) Long lFactura, @RequestParam(name = "servicio", required = false) Long lServicio) {
        Page<CompraEntity> oPage = null;
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity == null) {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
        } else {
            if (oUsuarioEntity.getTipousuario().getId() == 1) {
                if (lFactura != null) {
                    if (strFilter != null) {
                        oPage = oCompraRepository.findByFacturaIdAndCantidadOrPrecioOrFecha(lFactura, strFilter, strFilter, strFilter,oPageable);
                    } else {
                        oPage = oCompraRepository.findByFacturaId(lFactura, oPageable);
                    }
                } else if (lServicio != null) {
                    if (strFilter != null) {
                        oPage = oCompraRepository.findByServicioIdAndCantidadOrPrecioOrFecha(lServicio, strFilter, strFilter, strFilter,oPageable);
                    } else {
                        oPage = oCompraRepository.findByServicioId(lServicio, oPageable);
                    }
                } else {
                    if (strFilter != null) {
                        oPage = oCompraRepository.findByIdContain(strFilter, strFilter, strFilter, strFilter,oPageable);
                    } else {
                        oPage = oCompraRepository.findAll(oPageable);
                    }
                }
                return new ResponseEntity<Page<CompraEntity>>(oPage, HttpStatus.OK);
            } else {
                if (lFactura != null) {
                    if (strFilter != null) {
                        oPage = oCompraRepository.findByFacturaIdAndCantidadOrPrecioOrFecha(lFactura,strFilter, strFilter, strFilter,oPageable);
                    } else {
                        oPage = oCompraRepository.findByFacturaIdUsuario(lFactura, oUsuarioEntity.getId(), oPageable);
                    }
                } else if (lServicio!= null) {
                    if (strFilter != null) {
                        oPage = oCompraRepository.findByServicioIdAndCantidadOrPrecioOrFecha(lServicio, strFilter,strFilter, strFilter,oPageable);
                    } else {
                        oPage = oCompraRepository.findByServicioIdUsuario(lServicio,oUsuarioEntity.getId(), oPageable);
                    }
                } else {
                    if (strFilter != null) {
                        oPage = oCompraRepository.findByIdContainUsuario(strFilter, strFilter,strFilter, strFilter, oUsuarioEntity.getId(),oPageable);
                    } else {
                        oPage = oCompraRepository.findAllUsuario(oUsuarioEntity.getId(),oPageable);
                    }
                }
                return new ResponseEntity<Page<CompraEntity>>(oPage, HttpStatus.OK);

            }
        }

    }

//    @PostMapping("/generate")
//    public ResponseEntity<?> generate() {
//        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
//        if (oUsuarioEntity == null) {
//            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
//        } else {
//            if (oUsuarioEntity.getTipousuario().getId() == 1) {
//                return new ResponseEntity<CompraEntity>(oCompraRepository.save(oCompraService.generateRandomCompra()),
//                        HttpStatus.OK);
//            }
//
//            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
//        }
//    }
    @PostMapping("/generate/{amount}")
    public ResponseEntity<?> generateAmount(@PathVariable(value = "amount") Integer amount) {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");

        List<CompraEntity> compraList = new ArrayList<>();

        if (oUsuarioEntity == null) {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
        } else {
            if (oUsuarioEntity.getTipousuario().getId() == 1) {
                for (int i = 0; i < amount; i++) {
                    CompraEntity oCompraEntity = oCompraService.generateRandomCompra();
                    oCompraRepository.save(oCompraEntity);
                    compraList.add(oCompraEntity);
                }
            }
            return new ResponseEntity<>(oCompraRepository.count(), HttpStatus.OK);
        }
    }
}
