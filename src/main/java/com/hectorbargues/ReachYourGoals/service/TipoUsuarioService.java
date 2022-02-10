package com.hectorbargues.ReachYourGoals.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hectorbargues.ReachYourGoals.entity.TipoUsuarioEntity;

@Service
public class TipoUsuarioService {

    public List<TipoUsuarioEntity> generateUsersType() {
        List<TipoUsuarioEntity> usersTypeList = new ArrayList<>();
        usersTypeList.add(new TipoUsuarioEntity(1L, "administrador"));
        usersTypeList.add(new TipoUsuarioEntity(2L, "usuario"));

        return usersTypeList;
    }
}
