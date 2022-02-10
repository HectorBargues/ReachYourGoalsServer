/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hectorbargues.ReachYourGoals.api;

import java.util.List;
import javax.servlet.http.HttpSession;
import com.hectorbargues.ReachYourGoals.entity.FacturaEntity;
import com.hectorbargues.ReachYourGoals.entity.UsuarioEntity;
import com.hectorbargues.ReachYourGoals.helper.TipoUsuarioHelper;
import com.hectorbargues.ReachYourGoals.repository.FacturaRepository;
import com.hectorbargues.ReachYourGoals.repository.UsuarioRepository;
import com.hectorbargues.ReachYourGoals.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author a023862896m
 */
@RestController
@RequestMapping("/factura")

public class FacturaController {

    @Autowired
    HttpSession oHttpSession;

    @Autowired
    FacturaRepository oFacturaRepository;

    @Autowired
    UsuarioRepository oUsuarioRepository;

    @Autowired
    FacturaService oFacturaService;

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(value = "id") Long id) {
        //comprueba si es usuario
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        FacturaEntity oFacturaEntity = new FacturaEntity();

        if (oUsuarioEntity == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else {
            if (oUsuarioEntity.getTipousuario().getId() == 1) { // comprueba si es administrador
                if (oFacturaRepository.existsById(id)) {
                    return new ResponseEntity<FacturaEntity>(oFacturaRepository.getById(id), HttpStatus.OK);
                } else {
                    return new ResponseEntity<FacturaEntity>(oFacturaRepository.getById(id), HttpStatus.NOT_FOUND);
                }
            } else {  //usuario sin permiso
                oFacturaEntity = oFacturaRepository.getById(id);
                if (oFacturaEntity != null) {
                    if (oFacturaEntity.getUsuario().getId().equals(oUsuarioEntity.getId())) {
                        return new ResponseEntity<FacturaEntity>(oFacturaRepository.getById(id), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
                    }
                } else {
                    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                }
            }
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> all() {
        if (oFacturaRepository.count() <= 1000) {
            return new ResponseEntity<List<FacturaEntity>>(oFacturaRepository.findAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.PAYLOAD_TOO_LARGE);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody FacturaEntity oFacturaEntity) {

        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");

        if (oUsuarioEntity == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else {

            if (oUsuarioEntity.getTipousuario().getId() == 1) { //administrador

                if (oFacturaEntity.getId() == null) {
                    return new ResponseEntity<FacturaEntity>(oFacturaRepository.save(oFacturaEntity), HttpStatus.OK);
                } else {
                    return new ResponseEntity<Long>(0L, HttpStatus.NOT_MODIFIED);
                }

            } else {  //usuario sin permiso

                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {

        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");

        if (oUsuarioEntity == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else {

            if (oUsuarioEntity.getTipousuario().getId() == 1) { //administrador
                if (oFacturaRepository.existsById(id)) {
                    oFacturaRepository.deleteById(id);

                    if (oFacturaRepository.existsById(id)) {
                        return new ResponseEntity<Long>(id, HttpStatus.NOT_MODIFIED);

                    } else {
                        return new ResponseEntity<Long>(0L, HttpStatus.OK);
                    }

                } else {  //usuario sin permiso

                    return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
                }
            }
        }
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        FacturaEntity oFacturaEntity = null;
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else if (oUsuarioEntity.getTipousuario().getId() == 1) {

            return new ResponseEntity<Long>(oFacturaRepository.count(), HttpStatus.OK);

        } else {

            return new ResponseEntity<Long>(oFacturaRepository.findByFacturaIdUsuarioCount(oUsuarioEntity.getId()), HttpStatus.OK);
        }

    }

    @PutMapping("/")
    public ResponseEntity<?> update(@RequestBody FacturaEntity oFacturaEntity) {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else {
            if (oUsuarioEntity.getTipousuario().getId() == 1) { //administrador              
                if (oFacturaRepository.existsById(oFacturaEntity.getId())) {
                    return new ResponseEntity<FacturaEntity>(oFacturaRepository.save(oFacturaEntity), HttpStatus.OK);
                } else {
                    return new ResponseEntity<Long>(0L, HttpStatus.NOT_MODIFIED);
                }
            } else {  //usuario sin permisos
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @GetMapping("/page")
    public ResponseEntity<?> getPage(@PageableDefault(page = 0, size = 10, direction = Direction.ASC) Pageable oPageable) {

        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");

        Page<FacturaEntity> oPage = oFacturaRepository.findAll(oPageable);
        if (oUsuarioEntity == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else {
            if (oUsuarioEntity.getTipousuario().getId() == 1) { //administrador
                return new ResponseEntity<Page<FacturaEntity>>(oPage, HttpStatus.OK);
            } else {
                return new ResponseEntity<Page<FacturaEntity>>(oFacturaRepository.findByFacturaXUsuario(oUsuarioEntity.getId(), oPageable), HttpStatus.OK);
            }
        }
    }

    @PostMapping("/generate/{amount}")
    public ResponseEntity<?> generate(@PathVariable(value = "amount") int amount) {
        UsuarioEntity oUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oUsuarioEntity.getTipousuario().getId() == 1) {
            if (oUsuarioEntity == null) {
                return new ResponseEntity<>(0L, HttpStatus.UNAUTHORIZED);
            } else {
                if (oUsuarioRepository.count() > 0) {
                    for (int i = 0; i < amount; i++) {
                        FacturaEntity oFacturaEntity = oFacturaService.generateRandomFactura();
                        oFacturaRepository.save(oFacturaEntity);
                    }
                    return new ResponseEntity<>(oFacturaRepository.count(), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(0L, HttpStatus.OK);
                }
            }
        } else {
            return new ResponseEntity<>(0L, HttpStatus.UNAUTHORIZED);
        }
    }
     @GetMapping("/filter/{filtro}")
    public ResponseEntity<Page<FacturaEntity>> getFilteredPage(@PathVariable(value = "filtro") Long sfiltro, @PageableDefault(page = 0, size = 10, direction = Sort.Direction.ASC) Pageable oPageable) {
        Page<FacturaEntity> oPage = null;
        oPage = oFacturaRepository.findByFacturaXUsuario(sfiltro, oPageable);
        return new ResponseEntity<Page<FacturaEntity>>(oPage, HttpStatus.OK);
    }
    

}
