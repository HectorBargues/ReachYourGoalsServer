package com.hectorbargues.ReachYourGoals.service;

import java.util.List;
import com.hectorbargues.ReachYourGoals.entity.FacturaEntity;
import com.hectorbargues.ReachYourGoals.helper.RandomHelper;
import com.hectorbargues.ReachYourGoals.repository.FacturaRepository;
import com.hectorbargues.ReachYourGoals.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FacturaService {

    @Autowired
    UsuarioRepository oUsuarioRepository;

    @Autowired
    FacturaRepository oFacturaRepository;

    @Autowired
    UsuarioService oUsuarioService;

    public FacturaEntity generateRandomFactura() {
        if (oUsuarioRepository.count() > 0) {
            int[] ivas = {4, 10, 21};

            int iva = ivas[(int) (Math.floor(Math.random() * ((ivas.length - 1) - 0 + 1) + 0))];
            FacturaEntity oFacturaEntity = new FacturaEntity();
            oFacturaEntity.setFecha(RandomHelper.getRadomDateTime());
            oFacturaEntity.setIva(iva);

            oFacturaEntity.setUsuario(oUsuarioService.getRandomUsuario());

            if (RandomHelper.getRandomInt(0, 1) == 0) {
                oFacturaEntity.setPagado(true);
            } else {
                oFacturaEntity.setPagado(false);
            }
            return oFacturaEntity;
        } else {
            return null;
        }

    }

    public FacturaEntity getRandomFactura() {
        FacturaEntity oFacturaEntity = null;
        int iPosicion = RandomHelper.getRandomInt(0, (int) oFacturaRepository.count() - 1);
        Pageable oPageable = PageRequest.of(iPosicion, 1);
        Page<FacturaEntity> facturaPage = oFacturaRepository.findAll(oPageable);
        List<FacturaEntity> facturaList = facturaPage.getContent();
        oFacturaEntity = oFacturaRepository.getById(facturaList.get(0).getId());
        return oFacturaEntity;
    }

}
