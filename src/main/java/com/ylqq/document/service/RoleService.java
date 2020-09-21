package com.ylqq.document.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ylqq.document.pojo.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ylqq
 */
public interface RoleService {
    /**
     * 添加角色（选择性）
     * @param record
     * @return
     */
    int insertSelective(Role record);

    /**
     * 按主键查询（不级联）
     * @param roleid
     * @return
     */
    Role selectByPrimaryKey(Integer roleid);

    /**
     * 按主键查询（级联）
     * @param roleid
     * @return
     */
    Role selectByPrimaryKeyCascade(Integer roleid);

    /**
     * 非级联模糊查询
     * @param role
     * @return
     */
    List<Role> selectByKeyword(Role role);

    /**
     * 级联模糊查询
     * @param role
     * @return
     */
    List<Role> selectByKeywordCascade(Role role);

    /**
     * 判断是否有相同名称的角色
     * @param roleid
     * @param rolename
     * @return
     */
    List<Role> hasSameRole(@Param("roleid") Integer roleid, @Param("rolename") String rolename);

    /**
     * 按主键选择性更新
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(Role record);

    /**
     * 删除某个角色的全部权限信息（更新权限设置用）
     * @param roleid
     * @return
     */
    int deleteOldRoleRights(Integer roleid);

    /**
     * 为某个角色设置权限
     * @param roleid 角色id
     * @param funid 功能id
     * @return
     */
    int insertNewRoleRightInfo(@Param("roleid") Integer roleid, @Param("funid") Integer funid);

}
