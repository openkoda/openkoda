package com.openkoda.repository.specifications;


import com.openkoda.model.component.ServerJs;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ServerJsSpecification {
    public static Specification<ServerJs> getByName(String name) {

        return (root, query, cb) -> cb.equal(root.get("name"), name);

    }
}
