package com.hectorbargues.ReachYourGoals.service;

import java.util.ArrayList;
import java.util.List;
import com.hectorbargues.ReachYourGoals.entity.TipoServicioEntity;
import com.hectorbargues.ReachYourGoals.helper.RandomHelper;
import com.hectorbargues.ReachYourGoals.repository.TipoServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TipoServicioService {

    @Autowired
    TipoServicioRepository oTipoServicioRepository;

    private final String[] TIPO = {"Servicio"};
    private final String[] CARATERISTICA = {"profesional", "económico","estándar"};
    private final String[] LUGAR = {"en un club", "a domicilio", "al aire libre"};

    public List<TipoServicioEntity> generateAllTipoServicioList() {
        List<TipoServicioEntity> TipoServList = new ArrayList<>();
        for (int i = 0; i < TIPO.length; i++) {
            for (int j = 0; j < CARATERISTICA.length; j++) {
                for (int k = 0; k <  LUGAR.length; k++) {
                    TipoServList.add(this.generateTipoServicio(i, j, k));
                }
            }
        }
        return TipoServList;
    }

    public TipoServicioEntity generateTipoServicio() {
        String nombre = TIPO[RandomHelper.getRandomInt(0, TIPO.length - 1)] + " " + CARATERISTICA[RandomHelper.getRandomInt(0, CARATERISTICA.length - 1)] + " " +  LUGAR[RandomHelper.getRandomInt(0,  LUGAR.length - 1)];
        TipoServicioEntity oTipoServicioEntity = new TipoServicioEntity();
        oTipoServicioEntity.setNombre(nombre);
        return oTipoServicioEntity;
    }

    private TipoServicioEntity generateTipoServicio(int i, int j, int k) {
        String nombre = TIPO[i] + " " + CARATERISTICA[j] + " " +  LUGAR[k];
        TipoServicioEntity oTipoServicioEntity = new TipoServicioEntity();
        oTipoServicioEntity.setNombre(nombre);
        return oTipoServicioEntity;
    }

    public TipoServicioEntity getRandomTipoServicio() {
        TipoServicioEntity oTipoServicioEntity = null;
        int iPosicion = RandomHelper.getRandomInt(0, (int) oTipoServicioRepository.count()-1);
        Pageable oPageable = PageRequest.of(iPosicion, 1);
        Page<TipoServicioEntity> tipoServicioPage = oTipoServicioRepository.findAll(oPageable);
        List<TipoServicioEntity> tipoServicioList = tipoServicioPage.getContent();
        oTipoServicioEntity = oTipoServicioRepository.getById(tipoServicioList.get(0).getId());
        return oTipoServicioEntity;
    }

}
