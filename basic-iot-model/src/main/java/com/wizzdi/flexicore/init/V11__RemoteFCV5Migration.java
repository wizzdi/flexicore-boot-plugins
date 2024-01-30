package com.wizzdi.flexicore.init;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.Savepoint;
import java.sql.Statement;

@Component
public class V11__RemoteFCV5Migration extends BaseJavaMigration {

    private static final Logger logger = LoggerFactory.getLogger(V11__RemoteFCV5Migration.class);

    @Override
    public void migrate(Context context) throws Exception {

        Connection connection = context.getConnection();
        Savepoint v2_0 = connection.setSavepoint("v2_0");
        try (Statement select = context.getConnection().createStatement()) {
                logger.info("Starting Migration of remote to FC V5");
                String sql = """
                        alter table remote 
                        drop constraint IF EXISTS fk_remote_gatewayuser_id ,
                        drop constraint IF EXISTS fk_remote_approvinguser_id,
                        add constraint fk_remote_gatewayuser_id foreign key (gatewayuser_id) references UserTable(id),
                        add constraint fk_remote_approvinguser_id foreign key (approvinguser_id) references UserTable(id)
                        """;
                logger.info("executing SQL: " + sql);
                int updatedEntries = select.executeUpdate(sql);


        }


    }




}