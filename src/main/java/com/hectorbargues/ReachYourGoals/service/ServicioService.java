package com.hectorbargues.ReachYourGoals.service;

import java.util.List;
import com.hectorbargues.ReachYourGoals.entity.ServicioEntity;
import com.hectorbargues.ReachYourGoals.helper.RandomHelper;
import com.hectorbargues.ReachYourGoals.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ServicioService {

    @Autowired
    TipoServicioService oTipoServicioService;

    @Autowired
    ServicioRepository oServicioRepository;

    private final String[] SERVICIO = {"Clase"};
    private final String[] CLASE = {"Spinning", "Body Combat", "Body Pump", "Yoga", "Zumba", "Pilates", "Musculación"};
    private final String[] DURACION = {"corta", "mediana", "larga", "personalizada"};
    private final String[] LETTERS_CODE = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public ServicioEntity generateRandomServicio() {
        ServicioEntity oServicioEntity = new ServicioEntity();
        oServicioEntity.setCodigo(Integer.toString(RandomHelper.getRandomInt(1, 100000)) + LETTERS_CODE[RandomHelper.getRandomInt(0, 25)]);
        oServicioEntity.setNombre(generateProduct());
        oServicioEntity.setExistencias(RandomHelper.getRandomInt(0, 100));
        oServicioEntity.setPrecio(RandomHelper.getRadomDouble(0, 100));
        //oServicioEntity.setImagen((long) RandomHelper.getRandomInt(0, 100));
        oServicioEntity.setImagen(1L);
        //oServicioEntity.setTiposervicio(oTipoServicioRepository.getById((long) RandomHelper.getRandomInt(1, (int) oTipoServicioRepository.count())));
        oServicioEntity.setTiposervicio(oTipoServicioService.getRandomTipoServicio());
        return oServicioEntity;
    }

    private String generateProduct() {
        String name = SERVICIO[RandomHelper.getRandomInt(0, SERVICIO.length-1)];
        String clase = CLASE[RandomHelper.getRandomInt(0, CLASE.length-1)].toLowerCase();
        String duracion =DURACION[RandomHelper.getRandomInt(0, DURACION.length-1)].toLowerCase();
        return name + " de " + clase + " de duración " + duracion;
    }

    public ServicioEntity getRandomServicio() {
        ServicioEntity oServicioEntity = null;
        int iPosicion = RandomHelper.getRandomInt(0, (int) oServicioRepository.count() - 1);
        Pageable oPageable = PageRequest.of(iPosicion, 1);
        Page<ServicioEntity> tipoServicioPage = oServicioRepository.findAll(oPageable);
        List<ServicioEntity> tipoServicioList = tipoServicioPage.getContent();
        oServicioEntity = oServicioRepository.getById(tipoServicioList.get(0).getId());
        return oServicioEntity;
    }

}
