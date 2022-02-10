package com.hectorbargues.ReachYourGoals.api;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.hectorbargues.ReachYourGoals.entity.TipoUsuarioEntity;
import com.hectorbargues.ReachYourGoals.entity.UsuarioEntity;
import com.hectorbargues.ReachYourGoals.helper.TipoUsuarioHelper;
import com.hectorbargues.ReachYourGoals.repository.TipoUsuarioRepository;
import com.hectorbargues.ReachYourGoals.service.TipoUsuarioService;

@RestController
@RequestMapping("/tipousuario")
public class TipoUsuarioController {

    @Autowired
    TipoUsuarioRepository oTipoUsuarioRepository;

    @Autowired
    HttpSession oHttpSession;

    @Autowired
    TipoUsuarioService oUserTypeService;

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(value = "id") Long id) {
        if (id == null || !(oTipoUsuarioRepository.existsById(id))) {
            return new ResponseEntity<Long>(0L, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<TipoUsuarioEntity>(oTipoUsuarioRepository.getById(id), HttpStatus.OK);
    }

//    @GetMapping("/all")
//    public ResponseEntity<?> get() {
//        return new ResponseEntity<List<TipoUsuarioEntity>>(oTipoUsuarioRepository.findAll(), HttpStatus.OK);
//    }
    @GetMapping("/count")
    public ResponseEntity<?> count() {
        return new ResponseEntity<Long>(oTipoUsuarioRepository.count(), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<Page<TipoUsuarioEntity>> getPage(
           @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC) Pageable oPageable,
           @RequestParam(name = "filter", required = false) String strFilter) {
        Page<TipoUsuarioEntity> oPage = null;
        if (strFilter != null) {
            oPage = oTipoUsuarioRepository.findByNombreIgnoreCaseContaining(strFilter, oPageable);
        } else {
            oPage = oTipoUsuarioRepository.findAll(oPageable);
        }
        return new ResponseEntity<>(oPage, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody TipoUsuarioEntity oTipoUsuarioEntity) {
        if (oHttpSession.getAttribute("usuario") == null || ((UsuarioEntity) oHttpSession.getAttribute("usuario"))
               .getTipousuario().getId() != TipoUsuarioHelper.ADMIN) {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
        }

        if (oTipoUsuarioEntity.getId() == null || !oTipoUsuarioRepository.existsById(oTipoUsuarioEntity.getId())) {
            return new ResponseEntity<Long>(0L, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(oTipoUsuarioRepository.save(oTipoUsuarioEntity), HttpStatus.OK);
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate() {
        List<TipoUsuarioEntity> usersTypeList = oUserTypeService.generateUsersType();

        if (oHttpSession.getAttribute("usuario") == null || ((UsuarioEntity) oHttpSession.getAttribute("usuario"))
               .getTipousuario().getId() != TipoUsuarioHelper.ADMIN) {
            return new ResponseEntity<>(0L, HttpStatus.UNAUTHORIZED);
        }

        for (int i = usersTypeList.size() - 1; i >= 0; i--) {
            if (!oTipoUsuarioRepository.existsById(usersTypeList.get(i).getId())) {
                oTipoUsuarioRepository.save(usersTypeList.get(i));
            } else {
                usersTypeList.remove(i);
            }
        }

        return new ResponseEntity<>(oTipoUsuarioRepository.count(), HttpStatus.OK);
    }

}
