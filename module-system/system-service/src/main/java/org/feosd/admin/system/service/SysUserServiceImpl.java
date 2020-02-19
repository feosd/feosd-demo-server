package org.feosd.admin.system.service;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import org.feosd.admin.common.util.ObjectUtils;
import org.feosd.admin.common.util.ValidatorUtil;
import org.feosd.admin.system.dao.SysDepartRepository;
import org.feosd.admin.system.dao.SysUserRepository;
import org.feosd.admin.system.dao.domain.SysDepart;
import org.feosd.admin.system.dao.domain.SysUser;
import org.feosd.admin.system.service.api.SysDepartService;
import org.feosd.admin.system.service.api.SysUserService;
import org.feosd.admin.system.service.api.dto.SysDepartDto;
import org.feosd.admin.system.service.api.dto.SysUserDto;
import org.feosd.admin.system.service.api.dto.UserTreeDto;
import org.feosd.admin.system.service.api.vo.SysUserVo;
import org.feosd.auth.dao.UserRepository;
import org.feosd.auth.dao.domain.Role;
import org.feosd.auth.dao.domain.User;
import org.feosd.auth.service.api.RoleService;
import org.feosd.auth.service.api.UserService;
import org.feosd.auth.service.api.dto.MenuTreeDto;
import org.feosd.auth.service.api.dto.RoleDto;
import org.feosd.auth.service.api.group.Insert;
import org.feosd.base.service.BaseServiceImpl;
import org.feosd.base.service.api.exception.ServiceException;
import org.feosd.base.service.api.query.PageQuery;
import org.feosd.base.service.api.query.Query;
import org.feosd.base.service.api.validation.Valid;
import org.feosd.base.utils.MD5;
import org.feosd.base.utils.PasswordHash;
import org.feosd.base.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.io.InputStream;
import java.util.*;


/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
@Service
@Valid // 如果需要自动数据校验，请添加本注解。
public class SysUserServiceImpl extends BaseServiceImpl<SysUser,SysUserDto,Long> implements SysUserService {
    private final static Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Value(value = "${data.maxImportNum:5000}")
    Integer maxImportNum;
    @Autowired
    SysUserRepository repository;
    @Autowired
    SysDepartRepository sysDepartRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SysDepartService sysDepartService;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    // 内部需要，无需修改。
    @Override
    public SysUser newDomain() {
        return new SysUser();
    }
    // 内部需要，无需修改。
    @Override
    public SysUserDto newDTO() {
        return new SysUserDto();
    }
    // 内部需要，无需修改
    @Override
    public SysUserRepository repository() {
        return repository;
    }

     //如果DTO和domain存在有不同的fieldName，请自定义转换实现。
    @Override
    public void d2DTO(SysUser domain, SysUserDto sysUserDto) {
        //password不能对外暴露出来，需要忽略掉
        BeanUtils.copyProperties(domain, sysUserDto,"password","roles");
        List<RoleDto> dtos = new ArrayList<>();
        if(!CollectionUtils.isEmpty(domain.getRoles())) {
            String roleNames = "";
            for (int i = 0;i < domain.getRoles().size();i++){
                Role role = domain.getRoles().get(i);
                RoleDto roleDto = new RoleDto();
                //menusIds数据量较大，所以忽略掉
                BeanUtils.copyProperties(role,roleDto,"menusIds");
                dtos.add(roleDto);
                roleNames = roleNames + "," + roleDto.getRoleName() ;
            }
            sysUserDto.setRoleNames(roleNames.substring(1));
        }
        sysUserDto.setRoles(dtos);
        SysDepartDto sysDepartDto = new SysDepartDto();
        BeanUtils.copyProperties(domain.getSysDepart(), sysDepartDto);
        sysUserDto.setSysDepartDto(sysDepartDto);
        sysUserDto.setDepartId(sysDepartDto.getId());
        sysUserDto.setDepartName(sysDepartDto.getDepartName());
    }

    //如果DTO和domain存在有不同的fieldName，请自定义转换实现。
    @Override
    public void dto2D(SysUserDto sysUserDto, SysUser domain) {
        String[] nullPropertyNames = getNullPropertyNames(sysUserDto);
        //密码需加密，roles要通过updateUserRoles更新
        String[] properties = ObjectUtils.addArray(nullPropertyNames,new String[]{"password","roles"});
        //修改时，用户名、密码不能直接修改，roles要通过updateUserRoles更新
        if(sysUserDto.getId() != null){
            properties = ObjectUtils.addArray(nullPropertyNames,new String[]{"username","password","roles"});
        }else {//新增时，判断用户名、手机号、uuId是否已存在，密码需加密且不能为空
            userService.queryUserNameExist(sysUserDto.getUsername(),sysUserDto.getRowNum()+"用户名已存在！");
            if(sysUserDto.getMobile() != null) userService.queryMobileExist(sysUserDto.getMobile(),sysUserDto.getRowNum()+"手机号已经被注册！");
            if(StringUtils.isEmpty(sysUserDto.getUuId())) sysUserDto.setUuId(UUID.randomUUID().toString().replace("-",""));
            else userService.queryUuIdExist(sysUserDto.getUuId(),sysUserDto.getRowNum()+"uuId已存在！");

            if(StringUtils.isEmpty(sysUserDto.getPassword())) throw new ServiceException(400,sysUserDto.getRowNum()+"密码不能为空");
            domain.setPassword(PasswordHash.createHash(sysUserDto.getPassword().toLowerCase()));
        }

        BeanUtils.copyProperties(sysUserDto, domain, properties);

        //维护用户部门
        SysDepart sysDepart = new SysDepart();
        // 导入时，没有传部门Id，故根据部门名称查部门
        if (!StringUtils.isEmpty(sysUserDto.getDepartName())){
            sysDepart = sysDepartRepository.findByDepartName(sysUserDto.getDepartName());
            if (sysDepart == null) throw new ServiceException(400,sysUserDto.getRowNum()+"部门名称不存在");
        }else {
            sysDepart = sysDepartRepository.getOne(sysUserDto.getDepartId());
        }
        domain.setSysDepart(sysDepart);

        //维护用户角色
        updateUserRoles(sysUserDto, domain);
    }

    @Override
    public SysUserDto add(@Validated({Insert.class}) SysUserDto sysUserDto) {
        SysUser domain = this.newDomain();
        this.dto2D(sysUserDto,domain);
        return this.toDTO(this.repository().save(domain));
    }

    @Override
    public SysUserDto update(Long id, SysUserDto sysUserDto) {
        Optional<SysUser> model = this.repository().findById(id);
        if (!model.isPresent()) {
            throw new ServiceException(404, "更新对象ID不存在");
        } else {
            SysUser domain = model.get();
            this.dto2D(sysUserDto,domain);
            return this.toDTO(this.repository().save(domain));
        }
    }

    @Override
    public Page<SysUserDto> getSysUserPage(PageQuery query){
        //先检查查询字段和排序字段
        checkQuery(SysUser.class,query.getQueryParams(),query.getSortParams());
        //如果domain中含有byte、short、long、date类型字段需转换字段类型
        toQueryParams(SysUser.class,query.getQueryParams());
        Pageable pageable = PageRequest.of(query.getPageIndex(), query.getPageSize(),new Sort(query.sortOrder()));
        return toDTOPage(repository.searchForPage(SysUser.class,query.getQueryParams(),pageable));
    }

    @Override
    public List<SysUserDto> getSysUserList(Query query){
        //先检查查询字段和排序字段
        checkQuery(SysUser.class,query.getQueryParams(),query.getSortParams());
        //如果domain中含有byte、short、long、date类型字段需转换字段类型
        toQueryParams(SysUser.class,query.getQueryParams());
        return toDTOList(repository.searchList(SysUser.class,query.getQueryParams(),null,new Sort(query.sortOrder())));
    }

    @Override
    public void importExcel(Long usId,InputStream inputStream){
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        List<SysUserDto> sysUserDtos = new ArrayList<>();
        try {
            sysUserDtos = ExcelImportUtil.importExcel(inputStream, SysUserDto.class, params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(403,"Excel解析错误");
        }
        checkExcel(usId,sysUserDtos);
        this.saveAll(sysUserDtos);
    }

    // 校验Excel
    private void checkExcel(Long usId,List<SysUserDto> sysUserDtos){
        Integer size = sysUserDtos.size();
        if(size == 0) throw new ServiceException(403,"导入数据不能为空");
        if(size > maxImportNum) throw new ServiceException(403,"单次导入的数据不能超过"+maxImportNum+"行");

        // 校验不能重复的字段，首先Excel内不能重复
        List<String> usernames = new ArrayList<>();
        List<String> mobiles = new ArrayList<>();
        Integer rowNum = 3;//起始行数

        for (int i = 0;i < size;i++){
            sysUserDtos.get(i).setRowNum("第"+(rowNum++)+"行,");

            if (StringUtils.isEmpty(sysUserDtos.get(i).getUsername())) throw new ServiceException(400,sysUserDtos.get(i).getRowNum()+"用户名不能为空");
            if (sysUserDtos.get(i).getUsername().length() > 20) throw new ServiceException(400,sysUserDtos.get(i).getRowNum()+"用户名不能超过20个字符");
            if (StringUtils.isEmpty(sysUserDtos.get(i).getPassword())) throw new ServiceException(400,sysUserDtos.get(i).getRowNum()+"密码不能为空");
            if (!ValidatorUtil.isPassword(sysUserDtos.get(i).getPassword())) throw new ServiceException(400,sysUserDtos.get(i).getRowNum()+"密码应为8-20位字母加数字");
            if (StringUtils.isEmpty(sysUserDtos.get(i).getName())) throw new ServiceException(400,sysUserDtos.get(i).getRowNum()+"真实姓名不能为空");
            if (!ValidatorUtil.isChinese(sysUserDtos.get(i).getName())) throw new ServiceException(400,sysUserDtos.get(i).getRowNum()+"真实姓名应为汉字");
            if (sysUserDtos.get(i).getName().length() > 10) throw new ServiceException(400,sysUserDtos.get(i).getRowNum()+"真实姓名不能超过10个字");
            if (StringUtils.isEmpty(sysUserDtos.get(i).getMobile())) throw new ServiceException(400,sysUserDtos.get(i).getRowNum()+"手机号码不能为空");
            if (!ValidatorUtil.isMobile(sysUserDtos.get(i).getMobile())) throw new ServiceException(400,sysUserDtos.get(i).getRowNum()+"手机号码格式错误");
            if (!"0,1".contains(sysUserDtos.get(i).getSex())) throw new ServiceException(400,sysUserDtos.get(i).getRowNum()+"性别只能是男女");
            if (!StringUtils.isEmpty(sysUserDtos.get(i).getEmail()) && !ValidatorUtil.isEmail(sysUserDtos.get(i).getEmail())) throw new ServiceException(400,sysUserDtos.get(i).getRowNum()+"邮箱格式错误");
            if (StringUtils.isEmpty(sysUserDtos.get(i).getDepartName())) throw new ServiceException(400,sysUserDtos.get(i).getRowNum()+"所属部门不能为空");

            if (usernames.contains(sysUserDtos.get(i).getUsername())) throw new ServiceException(400,sysUserDtos.get(i).getRowNum()+"用户名重复");
            if (mobiles.contains(sysUserDtos.get(i).getMobile())) throw new ServiceException(400,sysUserDtos.get(i).getRowNum()+"手机号重复");
            usernames.add(sysUserDtos.get(i).getUsername());
            mobiles.add(sysUserDtos.get(i).getMobile());

            // 处理特殊字段
            handleField(usId, sysUserDtos.get(i));
        }
    }

    // 处理特殊字段
    private void handleField(Long usId, SysUserDto sysUserDto) {
        // 导入的用户userRole默认是ADMIN
        sysUserDto.setUserRole("ADMIN");
        sysUserDto.setCreateUserId(usId);
        sysUserDto.setUpdateBy(usId);
        // 密码需先md5
        sysUserDto.setPassword(MD5.MD5(sysUserDto.getPassword()).toLowerCase());
        // 拼接用户角色
        if (!StringUtils.isEmpty(sysUserDto.getRoleNames())){
            List<RoleDto> roleDtos = new ArrayList<>();
            String[] roleNames = sysUserDto.getRoleNames().split(",");
            for(String roleName : roleNames){
                RoleDto roleDto = roleService.findByRoleName(roleName);
                if(roleDto == null) throw new ServiceException(400,sysUserDto.getRowNum()+"用户角色不存在");
                roleDtos.add(roleDto);
            }
            sysUserDto.setRoles(roleDtos);
        }
    }

    @Override
    public SysUserDto addOrUpdateSysUser(SysUserDto sysUserDto){
        if(sysUserDto.getId() == null){//新增
            return this.add(sysUserDto);
        }else {//修改
            if(Objects.equals("admin",sysUserDto.getUsername())) throw new ServiceException(403,"超级管理员禁止修改");
            //修改手机号需校验手机号是否跟别人的手机号重复
            checkMobile(sysUserDto.getId(),sysUserDto.getMobile());
            return this.update(sysUserDto.getId(),sysUserDto);
        }
    }

    @Override
    public List<SysUserDto> findByOrgCodeLike(String orgCode){
        return toDTOList(repository.findBySysDepart_OrgCodeLikeOrderBySysDepartAscSortOrderAscIdAsc(orgCode+"%"));
    }

    //获取所有用户
    @Override
    public List<SysUserDto> findAll(){
        return findByOrgCodeLike("");
    }

    @Override
    public List<SysUserDto> findByManageUserId(Long usId){
        Boolean supperAdmin = roleService.isSupperAdmin(usId);
        //超级管理员查看所有用户
        if(supperAdmin) return findByOrgCodeLike("");
        else return findByOrgCodeLike(repository.getOne(usId).getSysDepart().getOrgCode());
    }

    //获取用户树
    @Override
    public List<UserTreeDto> getUserTree(){
        long start = System.currentTimeMillis();
        List<UserTreeDto> tree = new ArrayList<>();
        List<UserTreeDto> all = getAllDepartAndUser();
        if (!CollectionUtils.isEmpty(all)) {
            all.forEach(child -> {
                // 先找到一级部门作为根
                if (child.getPid() == null || "".equals(child.getPid())){
                    tree.add(child);
                    // 找孩子
                    getChildren(child,all);
                }
            });
        }
        removeNoUserNode(tree);
        long end = System.currentTimeMillis();
        log.info("获取用户树耗时:"+ (end - start)+"ms");
        return tree;
    }

    private void getChildren(UserTreeDto parent, List<UserTreeDto> children) {
        if (!CollectionUtils.isEmpty(children)) {
            children.forEach((child) -> {
                if (Objects.equals(parent.getId(), child.getPid())) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList());
                    }
                    parent.getChildren().add(child);
                    getChildren(child, children);
                }
            });
        }
    }

    private List<UserTreeDto> getAllDepartAndUser(){
        List<UserTreeDto> dtos = new ArrayList<>();
        List<SysDepartDto> departDtos = sysDepartService.findAll();
        addSysDepart(dtos,departDtos);
        List<SysUserDto> sysUserDtos = this.findAll();
        addSysUser(dtos,sysUserDtos);
        return dtos;
    }


    private void addSysDepart(List<UserTreeDto> treeDtos,List<SysDepartDto> departDtos){
        departDtos.forEach(departDto->{
            UserTreeDto userTreeDto = new UserTreeDto();
            userTreeDto.setType("0");
            userTreeDto.setId(departDto.getId());
            userTreeDto.setPid(departDto.getParentId());
            userTreeDto.setName(departDto.getDepartName());
            treeDtos.add(userTreeDto);
        });
    }

    private void addSysUser(List<UserTreeDto> treeDtos,List<SysUserDto> sysUserDtos){
        sysUserDtos.forEach(sysUserDto->{
            UserTreeDto userTreeDto = new UserTreeDto();
            userTreeDto.setType("1");
            userTreeDto.setId(sysUserDto.getId().toString());
            userTreeDto.setPid(sysUserDto.getSysDepartDto().getId());
            userTreeDto.setName(sysUserDto.getName());
            userTreeDto.setUsername(sysUserDto.getUsername());
            userTreeDto.setAvatar(sysUserDto.getAvatar());
            userTreeDto.setMobile(sysUserDto.getMobile());
            treeDtos.add(userTreeDto);
        });
    }

    private void removeNoUserNode(List<UserTreeDto> dtos){
        if (dtos.size() == 0) return;
        for (int i = dtos.size() - 1; i >= 0; i--) {
            if (dtos.get(i).getChildren().isEmpty()) dtos.remove(i);
            else removeNoUserNode(dtos.get(i));
        }
    }

    //移除没有用户的节点
    private boolean removeNoUserNode(UserTreeDto tree) {
        boolean exist = false;
        if("1".equals(tree.getType())) return true;
        if (!tree.getChildren().isEmpty()) {
            for (int i = tree.getChildren().size() - 1; i >=0 ; i--) {
                UserTreeDto child = tree.getChildren().get(i);
                if (!removeNoUserNode(child)) tree.getChildren().remove(i);
                else exist = true;
            }
        }
        return exist;
    }

    @Override
    public SysUserDto getSysUser(Long usId){
        SysUserDto sysUserDto = this.get(usId);
        List<MenuTreeDto> menus = userService.getUserMenus(usId, true, true);
        sysUserDto.setMenus(menus);
        sysUserDto.setUserPermissions(userService.getUserPermissions(usId));
        return sysUserDto;
    }

    @Override
    public SysUserDto findByUsername(String username){
       return toDTO(repository.findByUsername(username));
    }

    @Override
    public SysUserDto updateSysUser(SysUserVo sysUserVo){
        //修改手机号需校验手机号是否跟别人的手机号重复
        checkMobile(sysUserVo.getId(),sysUserVo.getMobile());
        SysUser sysUser = repository.getOne(sysUserVo.getId());
        BeanUtils.copyProperties(sysUserVo,sysUser,getNullPropertyNames(sysUserVo));
        repository.save(sysUser);
        return this.get(sysUserVo.getId());
    }

    //修改手机号需校验手机号是否跟别人的手机号重复
    private void checkMobile(Long usId,String mobile) {
        if (mobile != null){
            User bean = userRepository.findByMobile(mobile);
            //如果别人用该手机号注册过
            if(bean != null && !usId.equals(bean.getId())) throw new ServiceException(403, "手机号码已经被注册");
        }
    }

    private void updateUserRoles(SysUserDto sysUserDto, SysUser sysUser) {
        //剔除重复的角色
        List<RoleDto> roleDtos = ObjectUtils.removeDuplicate(sysUserDto.getRoles());
        //更新时,获取用户原有角色
        List<Role> roles = sysUser.getId() == null ? new ArrayList<>() : sysUser.getRoles();
        //更新人只能维护自己管理的角色
        removeRoles(sysUserDto.getUpdateBy(), roles);
        //维护新增角色
        roleDtos.forEach(roleDto -> {
            Role role = new Role();
            BeanUtils.copyProperties(roleDto,role);
            roles.add(role);
        });
        sysUser.setRoles(roles);
    }

    // 清空可管理的角色
    private void removeRoles(Long userId, List<Role> roles) {
        List<Long> roleIds = getRoleIds(userId);
        if(!CollectionUtils.isEmpty(roles) && !CollectionUtils.isEmpty(roleIds)){
            int num = roles.size() - 1;
            for (int i = num;i >= 0;i--){
                if (roleIds.contains(roles.get(i).getId())) roles.remove(roles.get(i));
            }
        }
    }

    // 获取用户可管理的角色Ids
    private List<Long> getRoleIds(Long usId){
        List<Long> roleIds = new ArrayList<>();
        if(usId == null || Objects.equals(-1L,usId)) return roleIds;
        List<RoleDto> canManageRoles = roleService.getUserCreatedRoles(usId);
        if(!CollectionUtils.isEmpty(canManageRoles)){
            canManageRoles.forEach(roleDto -> {
                roleIds.add(roleDto.getId());
            });
        }
        return roleIds;
    }
}
