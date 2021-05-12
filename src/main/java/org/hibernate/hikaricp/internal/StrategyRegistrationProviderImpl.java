/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.hibernate.hikaricp.internal;

import java.util.Collections;
import java.util.List;

import org.hibernate.boot.registry.selector.SimpleStrategyRegistrationImpl;
import org.hibernate.boot.registry.selector.StrategyRegistration;
import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;


/**
 * Provides the {@link HikariCPConnectionProvider} to the
 * {@link org.hibernate.boot.registry.selector.spi.StrategySelector} service.
 * 
 * @author Brett Meyer
 */
public class StrategyRegistrationProviderImpl implements StrategyRegistrationProvider {
    private static final List<StrategyRegistration> REGISTRATIONS = Collections.singletonList((StrategyRegistration) new SimpleStrategyRegistrationImpl<ConnectionProvider>(ConnectionProvider.class,
                                                                                                                                                                            HikariCPConnectionProvider.class,
                                                                                                                                                                            "hikari",
                                                                                                                                                                            "hikaricp",
                                                                                                                                                                            HikariCPConnectionProvider.class.getSimpleName(),
                                                                                                                                                                            // for consistency's sake
                                                                                                                                                                            "org.hibernate.connection.HikariCPConnectionProvider"));

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<StrategyRegistration> getStrategyRegistrations() {
        return REGISTRATIONS;
    }
}
