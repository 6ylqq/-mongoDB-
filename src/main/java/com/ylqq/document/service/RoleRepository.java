package com.ylqq.document.service;

import com.ylqq.document.pojo.Function;
import com.ylqq.document.pojo.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author ylqq
 */
public interface RoleRepository extends MongoRepository<Role, Integer> {
    /**
     * 查找包含指定功能的角色
     * @param functions 指定功能
     * @return
     * */
    List<Role> findByFunctionsContains(Optional<Function> functions);

    /**
     * 根据roleid删除角色
     *
     * @param roleId 角色id
     * */
    void deleteRoleByRoleId(Integer roleId);
}
