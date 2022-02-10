package com.hectorbargues.ReachYourGoals.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hectorbargues.ReachYourGoals.entity.UsuarioEntity;
import com.hectorbargues.ReachYourGoals.helper.RandomHelper;
import com.hectorbargues.ReachYourGoals.helper.TipoUsuarioHelper;
import com.hectorbargues.ReachYourGoals.repository.TipoUsuarioRepository;
import com.hectorbargues.ReachYourGoals.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class UsuarioService {

    @Autowired
    TipoUsuarioRepository oTipoUsuarioRepository;

    @Autowired
    UsuarioRepository oUsuarioRepository;

    private final String DNI_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";
    private final String[] NAMES = {"Jose", "Mark", "Elen", "Toni", "Hector", "Jose", "Laura", "Vika", "Sergio",
        "Javi", "Marcos", "Pere", "Daniel", "Jose", "Javi", "Sergio", "Aaron", "Rafa", "Lionel", "Borja"};

    private final String[] SURNAMES = {"Penya", "Tatay", "Coronado", "Cabanes", "Mikayelyan", "Gil", "Martinez",
        "Bargues", "Raga", "Santos", "Sierra", "Arias", "Santos", "Kuvshinnikova", "Cosin", "Frejo", "Marti",
        "Valcarcel", "Sesa", "Lence", "Villanueva", "Peyro", "Navarro", "Navarro", "Primo", "Gil", "Mocholi",
        "Ortega", "Dung", "Vi", "Sanchis", "Merida", "Aznar", "Aparici", "Tarazón", "Alcocer", "Salom", "Santamaría"};

    public UsuarioEntity generateRandomUser() {
        UsuarioEntity oUserEntity = new UsuarioEntity();
        oUserEntity.setDni(generateDNI());
        oUserEntity.setNombre(generateName());
        oUserEntity.setApellidos(generateSurname());
        oUserEntity.setLogin(oUserEntity.getNombre() + "_" + oUserEntity.getApellidos());
        oUserEntity.setPassword("4298f843f830fb3cc13ecdfe1b2cf10f51f929df056d644d1bca73228c5e8f64"); // wildcart
        oUserEntity.setEmail(generateEmail(oUserEntity.getNombre(), oUserEntity.getApellidos()));
        if (RandomHelper.getRandomInt(0, 10) > 1) {
            oUserEntity.setTipousuario(oTipoUsuarioRepository.getById(TipoUsuarioHelper.USER));
        } else {
            oUserEntity.setTipousuario(oTipoUsuarioRepository.getById(TipoUsuarioHelper.ADMIN));
        }
        oUserEntity.setValidado(false);
        oUserEntity.setActivo(false);

        return oUserEntity;
    }

    private String generateDNI() {
        String dni = "";
        int dniNumber = RandomHelper.getRandomInt(11111111, 99999999 + 1);
        dni += dniNumber + "" + DNI_LETTERS.charAt(dniNumber % 23);
        return dni;
    }

    private String generateName() {
        return NAMES[RandomHelper.getRandomInt(0, NAMES.length - 1)].toLowerCase();
    }

    private String generateSurname() {
        return SURNAMES[RandomHelper.getRandomInt(0, SURNAMES.length - 1)].toLowerCase();
    }

    private String generateEmail(String name, String surname) {
        List<String> list = new ArrayList<>();
        list.add(name);
        list.add(surname);
        return getFromList(list) + "_" + getFromList(list) + "@daw.tk";
    }

    private String getFromList(List<String> list) {
        int randomNumber = RandomHelper.getRandomInt(0, list.size() - 1);
        String value = list.get(randomNumber);
        list.remove(randomNumber);
        return value;
    }

    public UsuarioEntity getRandomUsuario() {
        UsuarioEntity oUsuarioEntity = null;
        int iPosicion = RandomHelper.getRandomInt(0, (int) oUsuarioRepository.count() - 1);
        Pageable oPageable = PageRequest.of(iPosicion, 1);
        Page<UsuarioEntity> usuarioPage = oUsuarioRepository.findAll(oPageable);
        List<UsuarioEntity> usuarioList = usuarioPage.getContent();
        oUsuarioEntity = oUsuarioRepository.getById(usuarioList.get(0).getId());
        return oUsuarioEntity;
    }

}
