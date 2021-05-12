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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.Stoppable;
import org.jboss.logging.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


/**
 * HikariCP Connection provider for Hibernate.
 *
 * @author Brett Wooldridge
 * @author Luca Burgazzoli
 */
public class HikariCPConnectionProvider implements ConnectionProvider, Configurable, Stoppable {

    private static final long serialVersionUID = -9131625057941275711L;

    private static final Logger LOGGER = Logger.getLogger(HikariCPConnectionProvider.class);

    /**
     * HikariCP configuration.
     */
    private HikariConfig hcfg = null;

    /**
     * HikariCP data source.
     */
    private HikariDataSource hds = null;

    // *************************************************************************
    // Configurable
    // *************************************************************************

    @SuppressWarnings("rawtypes")
    @Override
    public void configure(Map props) throws HibernateException {
        try {
            LOGGER.debug("Configuring HikariCP");

            hcfg = HikariConfigurationUtil.loadConfiguration(props);
            hds = new HikariDataSource(hcfg);

        } catch (Exception e) {
            throw new HibernateException(e);
        }

        LOGGER.debug("HikariCP Configured");
    }

    // *************************************************************************
    // ConnectionProvider
    // *************************************************************************

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        if (hds != null) {
            conn = hds.getConnection();
        }

        return conn;
    }

    @Override
    public void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isUnwrappableAs(Class unwrapType) {
        return ConnectionProvider.class.equals(unwrapType) ||
               HikariCPConnectionProvider.class.isAssignableFrom(unwrapType) ||
               DataSource.class.isAssignableFrom(unwrapType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> unwrapType) {
        if (ConnectionProvider.class.equals(unwrapType) ||
            HikariCPConnectionProvider.class.isAssignableFrom(unwrapType)) {
            return (T) this;
        } else if (DataSource.class.isAssignableFrom(unwrapType)) {
            return (T) hds;
        } else {
            throw new UnknownUnwrapTypeException(unwrapType);
        }
    }

    // *************************************************************************
    // Stoppable
    // *************************************************************************

    @Override
    public void stop() {
        hds.close();
    }
}
