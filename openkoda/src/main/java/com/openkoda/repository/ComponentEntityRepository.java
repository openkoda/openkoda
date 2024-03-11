package com.openkoda.repository;

import com.openkoda.model.OpenkodaModule;
import com.openkoda.model.common.ComponentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface ComponentEntityRepository <T extends ComponentEntity> extends JpaRepository<T, Long> {

    List<ComponentEntity> findByModule(OpenkodaModule module);

    void deleteByModule(OpenkodaModule module);
}
