package org.feosd.admin.system.service;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import org.feosd.admin.common.util.YouBianCodeUtil;
import org.feosd.admin.system.dao.SysDepartRepository;
import org.feosd.admin.system.dao.SysUserRepository;
import org.feosd.admin.system.dao.domain.SysDepart;
import org.feosd.admin.system.dao.domain.SysUser;
import org.feosd.admin.system.service.api.SysDepartService;
import org.feosd.admin.system.service.api.dto.SysDepartDto;
import org.feosd.auth.service.api.RoleService;
import org.feosd.base.service.BaseServiceImpl;
import org.feosd.base.service.api.exception.ServiceException;
import org.feosd.base.service.api.query.PageQuery;
import org.feosd.base.service.api.query.Query;
import org.feosd.base.service.api.validation.Valid;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
@Service
@Valid // 如果需要自动数据校验，请添加本注解。
public class SysDepartServiceImpl extends BaseServiceImpl<SysDepart,SysDepartDto,String> implements SysDepartService {
    private final static Logger log = LoggerFactory.getLogger(SysDepartServiceImpl.class);

    @Value(value = "${data.maxImportNum:5000}")
    Integer maxImportNum;
    @Autowired
    SysDepartRepository repository;
    @Autowired
    SysUserRepository sysUserRepository;
    @Autowired
    RoleService roleService;

    // 内部需要，无需修改。
    @Override
    public SysDepart newDomain() {
        return new SysDepart();
    }
    // 内部需要，无需修改。
    @Override
    public SysDepartDto newDTO() {
        return new SysDepartDto();
    }
    // 内部需要，无需修改
    @Override
    public SysDepartRepository repository() {
        return repository;
    }

     //如果DTO和domain存在有不同的fieldName，请自定义转换实现。
    @Override
    public void d2DTO(SysDepart domain, SysDepartDto sysDepartDto) {
        super.d2DTO(domain, sysDepartDto);
        if(sysDepartDto.getParentId() != null){
            SysDepartDto parentDepart = this.get(sysDepartDto.getParentId());
            if(parentDepart != null) sysDepartDto.setParentName(parentDepart.getDepartName());
        }else {
            sysDepartDto.setParentName("无");
        }
    }

    //如果DTO和domain存在有不同的fieldName，请自定义转换实现。
    @Override
    public void dto2D(SysDepartDto sysDepartDto, SysDepart domain) {
        //判断部门名称是否已存在
        checkDepartName(sysDepartDto);
        BeanUtils.copyProperties(sysDepartDto, domain, "orgCode","orgType");

        //新增时,自动维护部门编码和部门类型
        if (sysDepartDto.getId() == null){
            // 导入时，没有传父部门Id，故根据父部门名称查出父部门Id
            if (!StringUtils.isEmpty(sysDepartDto.getParentName()) && !"无".equals(sysDepartDto.getParentName())){
                SysDepart parent = repository.findByDepartName(sysDepartDto.getParentName());
                if (parent == null) throw new ServiceException(400,sysDepartDto.getRowNum()+"父部门不存在");
                domain.setParentId(parent.getId());
            }

            if (StringUtils.isEmpty(sysDepartDto.getDepartName())) throw new ServiceException(400,sysDepartDto.getRowNum()+"部门名称不能为空");
            if (sysDepartDto.getDepartName().length() > 20) throw new ServiceException(400,sysDepartDto.getRowNum()+"部门名称不能超过20个字");

            String[] codeArray = generateOrgCode(domain.getParentId());
            domain.setOrgCode(codeArray[0]);
            domain.setOrgType(codeArray[1]);
        }else {
            if (sysDepartDto.getId().equals(sysDepartDto.getParentId())) throw new ServiceException(400,"父部门不能是自己");
            SysDepart sysDepart = repository.getOne(sysDepartDto.getId());
            //修改父部门时，重置所有子部门编码和部门类型
            if (!Objects.equals(sysDepartDto.getParentId(),sysDepart.getParentId())){
                resetChildDepartOrgCode(domain);
            }
        }
    }

    // 重置所有子部门编码
    private void resetChildDepartOrgCode(SysDepart sysDepart){
        List<SysDepart> departs = repository.findByOrgCodeLike(sysDepart.getOrgCode()+"%");
        departs.add(sysDepart);
        departs.forEach(depart -> {
            String[] codeArray = generateOrgCode(sysDepart.getParentId());
            depart.setOrgCode(codeArray[0]);
            depart.setOrgType(codeArray[1]);
            repository.save(depart);
        });
    }

    @Override
    public SysDepartDto add(SysDepartDto sysDepartDto) {
        SysDepart domain = this.newDomain();
        this.dto2D(sysDepartDto,domain);
        return this.toDTO(this.repository().save(domain));
    }

    @Override
    public SysDepartDto update(String id, SysDepartDto sysDepartDto) {
        Optional<SysDepart> model = this.repository().findById(id);
        if (!model.isPresent()) {
            throw new ServiceException(404, "更新对象ID不存在");
        } else {
            SysDepart domain = model.get();
            this.dto2D(sysDepartDto,domain);
            return this.toDTO(this.repository().save(domain));
        }
    }

    public void checkDepartName(SysDepartDto sysDepartDto){
        SysDepart depart = repository.findByDepartName(sysDepartDto.getDepartName());
        if (depart != null && (StringUtils.isEmpty(sysDepartDto.getId()) || !depart.getId().equals(sysDepartDto.getId())))
            throw new ServiceException(400,sysDepartDto.getRowNum()+"部门名称已存在！");
    }

    @Override
    public Page<SysDepartDto> getSysDepartPage(PageQuery query){
        //先检查查询字段和排序字段
        checkQuery(SysDepart.class,query.getQueryParams(),query.getSortParams());
        Pageable pageable = PageRequest.of(query.getPageIndex(), query.getPageSize(),new Sort(query.sortOrder()));
        return toDTOPage(repository.searchForPage(SysDepart.class,query.getQueryParams(),pageable));
    }

    @Override
    public List<SysDepartDto> getSysDepartList(Query query){
        //先检查查询字段和排序字段
        checkQuery(SysDepart.class,query.getQueryParams(),query.getSortParams());
        return toDTOList(repository.searchList(SysDepart.class,query.getQueryParams(),null,new Sort(query.sortOrder())));
    }

    @Override
    @Transactional
    public void importExcel(InputStream inputStream){
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        List<SysDepartDto> sysDepartDtos = new ArrayList<>();
        try {
            sysDepartDtos = ExcelImportUtil.importExcel(inputStream, SysDepartDto.class, params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(403,"Excel解析错误");
        }
        importSysDepart(sysDepartDtos);
    }

    // 导入部门信息
    private void importSysDepart(List<SysDepartDto> sysDepartDtos){
        Integer size = sysDepartDtos.size();
        if(size == 0) throw new ServiceException(403,"导入数据不能为空");
        if(size > maxImportNum) throw new ServiceException(403,"单次导入的数据不能超过"+maxImportNum+"行");

        Integer rowNum = 3;//起始行数

        for (int i = 0;i < size;i++) {
            sysDepartDtos.get(i).setRowNum("第" + (rowNum++) + "行,");
            add(sysDepartDtos.get(i));
        }
    }

    @Override
    public SysDepartDto addOrUpdateSysDepart(SysDepartDto sysDepartDto){
        if(sysDepartDto.getId() == null){
            return this.add(sysDepartDto);
        }else {
            return this.update(sysDepartDto.getId(),sysDepartDto);
        }
    }

    //获取所有部门
    @Override
    public List<SysDepartDto> findAll(){
        return toDTOList(repository.findByDelFlagOrderByOrgTypeAscSortOrderAsc("0"));
    }

    @Override
    public void delete(String id){
        List<SysDepart> sysDeparts = repository.findByParentIdOrderByOrgCodeDesc(id);
        if (!sysDeparts.isEmpty()) throw new ServiceException(403,"部门下有子部门不能删除");

        List<SysUser> sysUsers = sysUserRepository.findBySysDepart_IdOrderBySortOrderAscIdAsc(id);
        if (!sysUsers.isEmpty()) throw new ServiceException(403,"部门下有用户不能删除");
        repository.deleteById(id);
    }

    //获取全部的部门树
    @Override
    public List<SysDepartDto> getSysDepartTree(){
        List<SysDepartDto> tree = new ArrayList<>();
        List<SysDepartDto> all = findAll();
        if (!CollectionUtils.isEmpty(all)) {
            all.forEach(child -> {
                // 先找到一级部门作为根部门
                if (child.getParentId() == null){
                    tree.add(child);
                    // 找到一级部门的子部门
                    getChildren(child,all);
                }
            });
        }
        return tree;
    }

    //根据父部门获取部门树
    @Override
    public List<SysDepartDto> getSysDepartTree(String parentId){
        List<SysDepartDto> tree = new ArrayList<>();
        SysDepartDto sysDepartDto = getSysDepartDto(parentId);
        tree.add(sysDepartDto);
        return tree;
    }

    //根据用户获取部门树
    @Override
    public List<SysDepartDto> getSysDepartTreeByUsId(Long usId){
        Boolean supperAdmin = roleService.isSupperAdmin(usId);
        //超级管理员可以获取全部的部门树
        if(supperAdmin) return getSysDepartTree();
        return getSysDepartTree(sysUserRepository.getOne(usId).getSysDepart().getId());
    }

    //根据部门编码获取部门列表
    @Override
    public List<SysDepartDto> findByOrgCodeLike(String orgCode){
        List<SysDepart> sysDeparts = repository.findByOrgCodeLikeAndDelFlagOrderByOrgTypeAscSortOrderAsc(orgCode+"%","0");
        return toDTOList(sysDeparts);
    }

    //根据用户获取部门列表
    @Override
    public List<SysDepartDto> findByByUsId(Long usId){
        Boolean supperAdmin = roleService.isSupperAdmin(usId);
        //超级管理员可以获取全部的部门树
        if(supperAdmin) return findAll();
        return findByOrgCodeLike(sysUserRepository.getOne(usId).getSysDepart().getOrgCode());
    }

    //获取一个部门并找到它的子部门
    private SysDepartDto getSysDepartDto(String parentId){
        SysDepartDto parent = this.get(parentId);
        List<SysDepartDto> children = findByOrgCodeLike(parent.getOrgCode());
        getChildren(parent,children);
        return parent;
    }

    private void getChildren(SysDepartDto parent, List<SysDepartDto> children) {
        if (!CollectionUtils.isEmpty(children)) {
            children.forEach((child) -> {
                if (Objects.equals(parent.getId(), child.getParentId())) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList());
                    }
                    parent.getChildren().add(child);
                    getChildren(child, children);
                }
            });
        }
    }


    /**
     * 生成部门编码和部门类型
     *
     * @param parentId
     * @return
     */
    private String[] generateOrgCode(String parentId) {
        String[] strArray = new String[2];
        // 创建一个List集合,存储查询返回的所有SysDepart对象
        List<SysDepart> departList = new ArrayList<>();
        // 定义新编码字符串
        String newOrgCode = "";
        // 定义旧编码字符串
        String oldOrgCode = "";
        // 定义部门类型
        String orgType = "";
        // 如果是一级部门,则查询出同级的org_code, 调用工具类生成编码并返回
        if (parentId == null) {
            // 先判断数据库中的表是否为空,空则直接返回初始编码
            departList = repository.findByParentIdIsNullOrderByOrgCodeDesc();
            if(departList == null || departList.size() == 0) {
                strArray[0] = YouBianCodeUtil.getNextYouBianCode(null);
                strArray[1] = "1";
                return strArray;
            }else {
                SysDepart depart = departList.get(0);
                oldOrgCode = depart.getOrgCode();
                orgType = depart.getOrgType();
                newOrgCode = YouBianCodeUtil.getNextYouBianCode(oldOrgCode);
            }
        } else { // 反之则查询出所有同级的部门,获取结果后有两种情况,有同级和没有同级
            // 查询出同级部门的集合
            List<SysDepart> parentList = repository.findByParentIdOrderByOrgCodeDesc(parentId);
            // 查询出父级部门
            SysDepart depart = repository.getOne(parentId);
            // 获取父级部门的Code
            String parentCode = depart.getOrgCode();
            // 根据父级部门类型算出当前部门的类型
            orgType = String.valueOf(Integer.valueOf(depart.getOrgType()) + 1);
            // 处理同级部门为null的情况
            if (parentList == null || parentList.size() == 0) {
                // 直接生成当前的部门编码并返回
                newOrgCode = YouBianCodeUtil.getSubYouBianCode(parentCode, null);
            } else { //处理有同级部门的情况
                // 获取同级部门的编码,利用工具类
                String subCode = parentList.get(0).getOrgCode();
                // 返回生成的当前部门编码
                newOrgCode = YouBianCodeUtil.getSubYouBianCode(parentCode, subCode);
            }
        }
        // 返回最终封装了部门编码和部门类型的数组
        strArray[0] = newOrgCode;
        strArray[1] = orgType;
        return strArray;
    }
}
