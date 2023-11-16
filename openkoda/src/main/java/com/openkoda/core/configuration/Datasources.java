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

package com.openkoda.core.configuration;

import com.zaxxer.hikari.HikariConfig;

import java.util.List;

/**
 * Datasources for database-based multitenancy scenarios.
 * Created on system start-up.
 * The connections can be configured in application properties as follows:
 * datasources.list[0].name=primary
 * datasources.list[0].config.jdbcUrl=jdbc:postgresql://localhost:5432/database
 * datasources.list[0].config.username=postgres
 * datasources.list[0].config.password=********
 * datasources.list[0].config.maximumPoolSize=10
 * ...
 * datasources.list[1].name=secondary_1
 * ...
 * datasources.list[2].name=secondary_2
 *
 * More config settings can be applied
 * see <a href="https://github.com/brettwooldridge/HikariCP#gear-configuration-knobs-baby">https://github.com/brettwooldridge/HikariCP#gear-configuration-knobs-baby</a>
 * */
public class Datasources {

    public static class TenantDB {
        public String name;
        public HikariConfig config;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public HikariConfig getConfig() {
            return config;
        }

        public void setConfig(HikariConfig config) {
            this.config = config;
        }
    }

    public List<TenantDB> list;

    public List<TenantDB> getList() {
        return list;
    }

    public void setList(List<TenantDB> list) {
        this.list = list;
    }
}
