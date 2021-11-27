package ru.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.app.models.Role;
import ru.app.repositories.RoleRepository;

import java.util.List;
import java.util.Objects;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Role> getRolesRights(){
        return roleRepository.findAll().stream().filter(i -> i.getId() > 2).peek(j -> {
            if (Objects.equals(j.getName(), "ROLE_VIEWER"))
                j.setName("Читатель");
            if (Objects.equals(j.getName(), "ROLE_EDITOR"))
                j.setName("Редактор");
        }).toList();
    }
}
