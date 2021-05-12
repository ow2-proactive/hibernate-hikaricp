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

import java.util.Map;
import java.util.Properties;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator;

import com.zaxxer.hikari.HikariConfig;


/**
 * Utility class to map Hibernate properties to HikariCP configuration properties.
 * 
 * @author Brett Wooldridge
 * @author Luca Burgazzoli
 * @author Brett Meyer
 */
public class HikariConfigurationUtil {
    public static final String CONFIG_PREFIX = "hibernate.hikari.";

    /**
     * Create/load a HikariConfig from Hibernate properties.
     * 
     * @param props a map of Hibernate properties
     * @return a HikariConfig
     */
    @SuppressWarnings("rawtypes")
    public static HikariConfig loadConfiguration(Map props) {
        Properties hikariProps = new Properties();
        copyProperty(AvailableSettings.AUTOCOMMIT, props, "autoCommit", hikariProps);

        copyProperty(AvailableSettings.DRIVER, props, "driverClassName", hikariProps);
        copyProperty(AvailableSettings.URL, props, "jdbcUrl", hikariProps);
        copyProperty(AvailableSettings.USER, props, "username", hikariProps);
        copyProperty(AvailableSettings.PASS, props, "password", hikariProps);

        copyIsolationSetting(props, hikariProps);

        for (Object keyo : props.keySet()) {
            if (!(keyo instanceof String)) {
                continue;
            }
            String key = (String) keyo;
            if (key.startsWith(CONFIG_PREFIX)) {
                hikariProps.setProperty(key.substring(CONFIG_PREFIX.length()), (String) props.get(key));
            }
        }

        return new HikariConfig(hikariProps);
    }

    @SuppressWarnings("rawtypes")
    private static void copyProperty(String srcKey, Map src, String dstKey, Properties dst) {
        if (src.containsKey(srcKey)) {
            dst.setProperty(dstKey, (String) src.get(srcKey));
        }
    }

    private static void copyIsolationSetting(Map props, Properties hikariProps) {
        final Integer isolation = ConnectionProviderInitiator.extractIsolation(props);
        if (isolation != null) {
            hikariProps.put("transactionIsolation",
                            ConnectionProviderInitiator.toIsolationConnectionConstantName(isolation));
        }
    }

}
