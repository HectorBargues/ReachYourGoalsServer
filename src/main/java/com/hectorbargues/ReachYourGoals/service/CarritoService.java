package com.hectorbargues.ReachYourGoals.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import com.hectorbargues.ReachYourGoals.Exception.CarritoVacioEnCompraException;
import com.hectorbargues.ReachYourGoals.Exception.FaltaCantidadDeServiciosEnCompraException;
import com.hectorbargues.ReachYourGoals.Exception.UnauthorizedException;
import com.hectorbargues.ReachYourGoals.entity.CarritoEntity;
import com.hectorbargues.ReachYourGoals.entity.CompraEntity;
import com.hectorbargues.ReachYourGoals.entity.FacturaEntity;
import com.hectorbargues.ReachYourGoals.entity.ServicioEntity;
import com.hectorbargues.ReachYourGoals.entity.UsuarioEntity;
import com.hectorbargues.ReachYourGoals.helper.RandomHelper;
import com.hectorbargues.ReachYourGoals.repository.CarritoRepository;
import com.hectorbargues.ReachYourGoals.repository.CompraRepository;
import com.hectorbargues.ReachYourGoals.repository.FacturaRepository;
import com.hectorbargues.ReachYourGoals.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hectorbargues.ReachYourGoals.repository.UsuarioRepository;

@Service
public class CarritoService {

    @Autowired
    UsuarioRepository oUsuarioRepository;

    @Autowired
    ServicioService oServicioService;

    @Autowired
    UsuarioService oUsuarioService;

    @Autowired
    HttpSession oHttpSession;

    @Autowired
    FacturaRepository oFacturaRepository;

    @Autowired
    CarritoRepository oCarritoRepository;

    @Autowired
    CompraRepository oCompraRepository;

    @Autowired
    ServicioRepository oServicioRepository;

    public ArrayList<CarritoEntity> generate(int rowsPerUser) {
        ArrayList<CarritoEntity> rows = new ArrayList<>();
        List users = oUsuarioRepository.findAll();
        int randomCantidad = 0;
        for (int i = 0; i < users.size(); i++) {
            for (int o = 0; o < rowsPerUser; o++) {
                randomCantidad = RandomHelper.getRandomInt(1, 10);
                CarritoEntity row = new CarritoEntity();
                row.setUsuario(oUsuarioService.getRandomUsuario());
                row.setServicio(oServicioService.getRandomServicio());
                row.setCantidad(randomCantidad);
                rows.add(row);
            }
        }
        return rows;
    }

    @Transactional
    public void compra() throws FaltaCantidadDeServiciosEnCompraException, UnauthorizedException, CarritoVacioEnCompraException {
        UsuarioEntity oSessionUsuarioEntity = (UsuarioEntity) oHttpSession.getAttribute("usuario");
        if (oSessionUsuarioEntity != null) {
            FacturaEntity oFacturaEntity = new FacturaEntity();
            oFacturaEntity.setIva(21);
            oFacturaEntity.setFecha(LocalDateTime.now());
            oFacturaEntity.setPagado(false);
            UsuarioEntity oUsuarioEntity = new UsuarioEntity();
            oUsuarioEntity.setId(oSessionUsuarioEntity.getId());
            oFacturaEntity.setUsuario(oUsuarioEntity);
            List<CarritoEntity> oCarritoList = oCarritoRepository.findByUsuarioId(oSessionUsuarioEntity.getId());
            if (oCarritoList.size() > 0) {
                CarritoEntity oCarritoEntity = null;
                for (int i = 0; i < oCarritoList.size(); i++) {
                    oCarritoEntity = oCarritoList.get(i);
                   ServicioEntity oServicioEntity = oCarritoEntity.getServicio();
                    if (oServicioEntity.getExistencias() >= oCarritoEntity.getCantidad()) {
                        CompraEntity oCompraEntity = new CompraEntity();
                        oCompraEntity.setCantidad(oCarritoEntity.getCantidad());
                        oCompraEntity.setFactura(oFacturaEntity);
                        oCompraEntity.setFecha(oFacturaEntity.getFecha());
                        oCompraEntity.setPrecio(oCarritoEntity.getServicio().getPrecio());
                        oCompraEntity.setServicio(oCarritoEntity.getServicio());
                        oCompraRepository.save(oCompraEntity);
                        oServicioEntity.setExistencias(oServicioEntity.getExistencias() - oCompraEntity.getCantidad());
                        oServicioRepository.save(oServicioEntity);
                    } else {
                        throw new FaltaCantidadDeServiciosEnCompraException(oServicioEntity.getId());
                    }
                }
            } else {
                throw new CarritoVacioEnCompraException();
            }
            oFacturaRepository.save(oFacturaEntity);
        } else {
            throw new UnauthorizedException();
        }
    }

}
