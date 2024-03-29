//package com.fyy.mybatis.service.impl;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
//import com.fyy.common.tools.constant.Constant;
//import com.fyy.common.tools.global.page.PageData;
//import com.fyy.common.tools.utils.ConvertUtils;
//import com.fyy.mybatis.service.CrudService;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
///**
// *  CRUD基础服务类
// *
// * @author carl
// */
//public abstract class CrudServiceImpl<M extends BaseMapper<T>, T, D> extends BaseServiceImpl<M, T> implements CrudService<T, D> {
//
//    protected Class<D> currentDtoClass() {
//        return (Class<D>) ReflectionKit.getSuperClassGenericType(getClass(), 2);
//    }
//
//    @Override
//    public PageData<D> page(Map<String, Object> params) {
//        IPage<T> page = baseDao.selectPage(
//            getPage(params, Constant.CREATE_DATE, false),
//            getWrapper(params)
//        );
//
//        return getPageData(page, currentDtoClass());
//    }
//
//    @Override
//    public List<D> list(Map<String, Object> params) {
//        List<T> entityList = baseDao.selectList(getWrapper(params));
//
//        return ConvertUtils.sourceToTarget(entityList, currentDtoClass());
//    }
//
//    public abstract QueryWrapper<T> getWrapper(Map<String, Object> params);
//
//    @Override
//    public D get(Long id) {
//        T entity = baseDao.selectById(id);
//
//        return ConvertUtils.sourceToTarget(entity, currentDtoClass());
//    }
//
//    @Override
//    public void save(D dto) {
//        T entity = ConvertUtils.sourceToTarget(dto, currentModelClass());
//        insert(entity);
//    }
//
//    @Override
//    public void update(D dto) {
//        T entity =  ConvertUtils.sourceToTarget(dto, currentModelClass());
//        updateById(entity);
//    }
//
//    @Override
//    public void delete(Long[] ids) {
//        baseDao.deleteBatchIds(Arrays.asList(ids));
//    }
//
//}