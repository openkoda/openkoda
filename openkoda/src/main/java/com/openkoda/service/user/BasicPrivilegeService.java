/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.service.user;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.DynamicPrivilege;
import com.openkoda.model.PrivilegeGroup;
import com.openkoda.repository.user.DynamicPrivilegeRepository;
import jakarta.inject.Inject;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing basic CRUD like opeartion related to DynamicPrivileges. Should be use instead of plain DynamicPrivilegeRepository to achieve Cacheable mechanism 
 *
 * @author mboronski
 *
 */

@Service
@Profile("!development")
public class BasicPrivilegeService extends ComponentProvider implements HasSecurityRules {

    protected static final String DYNAMIC_PRIVILEGES_ALL_CACHE = "dynamicPrivilegesAll";
    protected static final String DYNAMIC_PRIVILEGES_NAME_CACHE = "dynamicPrivilegesName";
    protected static final String DYNAMIC_PRIVILEGES_CACHE = "dynamicPrivileges";
    protected static final String NO_IMPLEMENTATION_IN_CORE_OPENKODA = "no implementation in core Openkoda";
    
    @Inject protected ApplicationEventPublisher applicationEventPublisher;
    @Inject protected DynamicPrivilegeRepository dynamicPrivilegeRepository;

    @Inject protected BasicPrivilegeService self;
    
    public static class PrivilegeChangeEvent extends ApplicationEvent {

        private static final long serialVersionUID = -8745580408895611463L;

        public PrivilegeChangeEvent(Object source) {
            super(source);
            
        }
    }
    
    public DynamicPrivilege createOrUpdateDynamicPrivilege(Long id, String name, String label, String category, PrivilegeGroup group, boolean removable) {
        throw new NotImplementedException(NO_IMPLEMENTATION_IN_CORE_OPENKODA);
    }
    
    /**
     * Creates new role considering its type (discrimination value)
     */
    public DynamicPrivilege createPrivilege(String name, String label, String category, PrivilegeGroup group) {
        throw new NotImplementedException(NO_IMPLEMENTATION_IN_CORE_OPENKODA);
    }
    
    /**
     * Creates new role considering its type (discrimination value)
     */
    @PreAuthorize(CHECK_CAN_MANAGE_ROLES)
    public DynamicPrivilege createOrUpdateDynamicPrivilege(DynamicPrivilege privilege) {
        debug("[createOrUpdateDynamicPrivilege] Creating or updating privilege {} ", privilege.toString());
        DynamicPrivilege newPrivilege = self.findByName(privilege.getName());
        if (newPrivilege == null) {
            newPrivilege = new DynamicPrivilege();
            newPrivilege.setId(privilege.getId());
        }

        newPrivilege.setName(privilege.getName());
        newPrivilege.setRemovable(privilege.getRemovable());
        newPrivilege.setGroup(privilege.getGroup());
        newPrivilege.setLabel(privilege.getLabel());
        newPrivilege.setCategory(privilege.getCategory());
        newPrivilege = self.save(newPrivilege);
        
        applicationEventPublisher.publishEvent(new PrivilegeChangeEvent(this));
        return newPrivilege;
    }

    /**
     * Validates whether role with given name and type already exists in the database
     */
    public boolean checkIfPrivilegeNameAlreadyExists(String name, BindingResult br) {
        throw new NotImplementedException(NO_IMPLEMENTATION_IN_CORE_OPENKODA);
    }

    public Object notifyOnPrivilagesChange() {
        debug("[notifyOnPrivilagesChange] Privileges have changed, notifying");
        applicationEventPublisher.publishEvent(new PrivilegeChangeEvent(this));
        return null;
    }

    @Caching(put = {
            @CachePut(cacheNames = {DYNAMIC_PRIVILEGES_CACHE}, key = "#result.getId()"),
            @CachePut(cacheNames = {DYNAMIC_PRIVILEGES_NAME_CACHE}, key = "#result.getName()"),
    }, evict = { @CacheEvict(cacheNames = {DYNAMIC_PRIVILEGES_ALL_CACHE}, allEntries = true) })
    public <S extends DynamicPrivilege> S save(S entity) {
        entity = dynamicPrivilegeRepository.save(entity);
        return entity;
    }
    
    public DynamicPrivilege deletePrivilege(long privilegeId) {
        throw new NotImplementedException(NO_IMPLEMENTATION_IN_CORE_OPENKODA);
    }

    public Object updatePrivilege(long privilegeId, String name, String label, String category, PrivilegeGroup privilegeGroup) {
        throw new NotImplementedException(NO_IMPLEMENTATION_IN_CORE_OPENKODA);
    }
    
    public <S extends DynamicPrivilege> void delete(S entity) {
        throw new NotImplementedException(NO_IMPLEMENTATION_IN_CORE_OPENKODA);
    }

    public <S extends DynamicPrivilege> List<S> saveAll(Iterable<S> entities) {
        throw new NotImplementedException(NO_IMPLEMENTATION_IN_CORE_OPENKODA);
    }

    public <S extends DynamicPrivilege> S saveAndFlush(S entity) {
        throw new NotImplementedException(NO_IMPLEMENTATION_IN_CORE_OPENKODA);
    }

    public <S extends DynamicPrivilege> List<S> saveAllAndFlush(Iterable<S> entities) {
        throw new NotImplementedException(NO_IMPLEMENTATION_IN_CORE_OPENKODA);
    }

    public int deletePrivilege(Long aLong) {
        throw new NotImplementedException(NO_IMPLEMENTATION_IN_CORE_OPENKODA);
    }

    @Cacheable(cacheNames = DYNAMIC_PRIVILEGES_ALL_CACHE, unless = "#result == null")
    public List<DynamicPrivilege> findAll(Sort sort) {
        return dynamicPrivilegeRepository.findAll(sort);
    }

    @Cacheable(cacheNames = DYNAMIC_PRIVILEGES_ALL_CACHE, unless = "#result == null")
    public Page<DynamicPrivilege> findAll(Pageable pageable) {
        return dynamicPrivilegeRepository.findAll(pageable);
    }
    
    @Cacheable(cacheNames = DYNAMIC_PRIVILEGES_NAME_CACHE, key = "#p0", unless = "#result == null")
    public DynamicPrivilege findByName(String name) {
        return dynamicPrivilegeRepository.findByName(name);
    }
    
    @Cacheable(cacheNames = DYNAMIC_PRIVILEGES_ALL_CACHE)
    public List<DynamicPrivilege> findAll() {
        return dynamicPrivilegeRepository.findAll();
    }

    public List<DynamicPrivilege> findAllById(Iterable<Long> ids) {
        return dynamicPrivilegeRepository.findAllById(ids);
    }

    @Cacheable(cacheNames = DYNAMIC_PRIVILEGES_CACHE, key = "#p0", unless = "#result == null")
    public Optional<DynamicPrivilege> findById(Long id) {
        return dynamicPrivilegeRepository.findById(id);
    }

    @Cacheable(cacheNames = DYNAMIC_PRIVILEGES_ALL_CACHE)
    public long count() {
        return dynamicPrivilegeRepository.count();
    }
}
