package com.pardot.rhombus.cli.commands;

import com.pardot.rhombus.cobject.CQLGenerationException;
import org.apache.commons.cli.CommandLine;

/**
 * User: Rob Righter
 * Date: 8/19/13
 */
public class PrepareInserts extends RcliWithCassandraConfig {


    public void executeCommand(CommandLine cl){
        super.executeCommand(cl);

        //now rebuild the keyspace
        try{
            getConnectionManager().setDefaultKeyspace(keyspaceDefinition);
            getConnectionManager().getObjectMapper().prePrepareInsertStatements();
            //looks like a success. Lets go ahead and return as such
            System.exit(0);
        }
        catch (CQLGenerationException e){
            System.out.println(e);
            System.out.println("Error encountered while attempting to prepare the Insert Statements for the keyspace");
        }

    }
}
