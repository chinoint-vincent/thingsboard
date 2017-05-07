/**
 * Copyright © 2016-2017 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.sql.dashboard;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.thingsboard.server.dao.model.sql.DashboardInfoEntity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 5/6/2017.
 */
@ConditionalOnProperty(prefix = "sql", value = "enabled", havingValue = "true", matchIfMissing = false)
public interface DashboardInfoRepository extends CrudRepository<DashboardInfoEntity, UUID> {

    @Query(nativeQuery = true, value = "SELECT * FROM DASHBOARD WHERE TENANT_ID = ?2 " +
            "AND LOWER(SEARCH_TEXT) LIKE LOWER(CONCAT(?3, '%')) " +
            "ORDER BY ID LIMIT ?1")
    List<DashboardInfoEntity> findByTenantIdFirstPage(int limit, UUID tenantId, String textSearch);

    @Query(nativeQuery = true, value = "SELECT * FROM DASHBOARD WHERE TENANT_ID = ?2 " +
            "AND LOWER(SEARCH_TEXT) LIKE LOWER(CONCAT(?3, '%')) " +
            "AND ID > ?4 ORDER BY ID LIMIT ?1")
    List<DashboardInfoEntity> findByTenantIdNextPage(int limit, UUID tenantId, String textSearch, UUID idOffset);

    @Query(nativeQuery = true, value = "SELECT * FROM DASHBOARD WHERE TENANT_ID = ?2 " +
            "AND CUSTOMER_ID = ?3 AND LOWER(SEARCH_TEXT) LIKE LOWER(CONCAT(?4, '%')) " +
            "ORDER BY ID LIMIT ?1")
    List<DashboardInfoEntity> findByTenantIdAndCustomerIdFirstPage(int limit, UUID tenantId, UUID customerId, String textSearch);

    @Query(nativeQuery = true, value = "SELECT * FROM DASHBOARD WHERE TENANT_ID = ?2 " +
            "AND CUSTOMER_ID = ?3 AND LOWER(SEARCH_TEXT) LIKE LOWER(CONCAT(?4, '%')) " +
            "AND ID > ?5 ORDER BY ID LIMIT ?1")
    List<DashboardInfoEntity> findByTenantIdAndCustomerIdNextPage(int limit, UUID tenantId, UUID customerId, String textSearch, UUID idOffset);
}
