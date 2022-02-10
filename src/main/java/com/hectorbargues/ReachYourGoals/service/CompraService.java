package com.hectorbargues.ReachYourGoals.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import com.hectorbargues.ReachYourGoals.entity.CompraEntity;
import com.hectorbargues.ReachYourGoals.entity.FacturaEntity;
import com.hectorbargues.ReachYourGoals.entity.ServicioEntity;
import com.hectorbargues.ReachYourGoals.repository.FacturaRepository;
import com.hectorbargues.ReachYourGoals.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompraService {

    @Autowired
    ServicioRepository oServicioRepository;

    @Autowired
    FacturaRepository oFacturaRepository;

    @Autowired
    FacturaService oFacturaService;

    @Autowired
    ServicioService oServicioService;
       
    public CompraEntity generateRandomCompra() {
        CompraEntity oCompraEntity = new CompraEntity();
        oCompraEntity.setCantidad(generateCantidad(1, 200));
        oCompraEntity.setPrecio(generatePrecio(0.99, 99.99));
        oCompraEntity.setFecha(getRadomDate());
        //oCompraEntity.setFactura(generateFactura());
        //oCompraEntity.setProducto(generateProducto());
        oCompraEntity.setFactura(oFacturaService.getRandomFactura());
        oCompraEntity.setServicio(oServicioService.getRandomServicio());

        return oCompraEntity;
    }

    private int generateCantidad(int minValue, int maxValue) {
        return ThreadLocalRandom.current().nextInt(minValue, maxValue);
    }

    private double generatePrecio(double minValue, double maxValue) {
        return Math.round(ThreadLocalRandom.current().nextDouble(minValue, maxValue) * 100d) / 100d;
    }

    private LocalDateTime getRadomDate() {
        int randomSeconds = new Random().nextInt(3600 * 24);
        LocalDateTime anyTime = LocalDateTime.now().minusSeconds(randomSeconds);
        return anyTime;
    }
  

    private FacturaEntity generateFactura() {
        List<FacturaEntity> list = oFacturaRepository.findAll();

        int randomNumber = generateNumber(0, list.size());
        FacturaEntity value = list.get(randomNumber);
        return value;
    }

    private ServicioEntity generateServicio() {
        List<ServicioEntity> list = oServicioRepository.findAll();

        int randomNumber = generateNumber(0, list.size());
        ServicioEntity value = list.get(randomNumber);
        return value;
    }

    private int generateNumber(int minValue, int maxValue) {
        return ThreadLocalRandom.current().nextInt(minValue, maxValue);
    }

}
